package com.template.batch.job;

import com.template.batch.dao.slave.RestUserInfoDao;
import com.template.batch.dao.master.UserInfoDao;
import com.template.batch.entity.master.UserInfo;
import com.template.batch.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateChuckErrorCaseJobTest {
  private static int TEST_DATA_COUNT = 100;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired
  private TemplateChuckErrorCaseJob templateChuckErrorCaseJob;
  @Autowired
  @Qualifier(TemplateChuckErrorCaseJob.STEP_NAME)
  private Step writerErrorStep;
  @Autowired
  private UserInfoDao userInfoDao;
  @Autowired
  private RestUserInfoDao restUserInfoDao;

  @Before
  public void setup(){
    int beforeCount = userInfoDao.getAllCount();
    log.info("------ before set data. count=[{}]", beforeCount);

    List<UserInfo> testUserInfos = new ArrayList<>();

    for(int i=0; i < TEST_DATA_COUNT; i++) {
      LocalDateTime createDate = LocalDateTime.now();
      String userId = IdGenerator.generateUserId();

      testUserInfos.add(UserInfo.builder().userId(userId).restFlag("N").createDate(createDate).build());
    }

    userInfoDao.addAll(testUserInfos);
    int afterCount = userInfoDao.getAllCount();
    log.info("------ before set data. count=[{}]", afterCount);
  }

  @Test
  public void writerErrorJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    JobParameters jobParameters =
            new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis())
                    .addString("startDate", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 00:00:00")
                    .addString("endDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 23:59:59")
                    .toJobParameters();

    JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher().run(templateChuckErrorCaseJob.writerErrorJob(writerErrorStep), jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.FAILED);
    assertThat(actualJobInstance.getJobName()).isEqualTo(TemplateChuckErrorCaseJob.JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.FAILED.getExitCode());

    assertThat(userInfoDao.getAllCount()).isEqualTo(100);
    assertThat(restUserInfoDao.getAllCount()).isEqualTo(20);
  }

  @After
  public void clear(){
    userInfoDao.deleteAll();
    restUserInfoDao.deleteAll();
    log.info("---- clear test data.");
  }

}