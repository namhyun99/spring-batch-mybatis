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
public class TemplateChuckDefaultJobTest {
  private static int TEST_DATA_COUNT = 100000;

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired
  private TemplateChuckDefaultJob templateChuckDefaultJob;
  @Autowired
  @Qualifier(TemplateChuckDefaultJob.STEP_NAME)
  private Step restUserStep;
  @Autowired
  private UserInfoDao userInfoDao;
  @Autowired
  private RestUserInfoDao restUserInfoDao;


  @Before
  public void setup(){
//    jobLauncherTestUtils.setJob(templateJob.restUserJob());

    int beforeCount = userInfoDao.getAllCount();
    log.info("------ before set data. count=[{}]", beforeCount);

    List<UserInfo> testUserInfos = new ArrayList<>();

    for(int i=0; i < TEST_DATA_COUNT; i++) {
      LocalDateTime createDate = LocalDateTime.now();
      String userId = IdGenerator.generateUserId();

      testUserInfos.add(UserInfo.builder().userId(userId).restFlag("N").createDate(createDate).build());
    }

    userInfoDao.addAll(testUserInfos);
//    restUserInfoDao.add(RestUserInfo.builder().userId(testUserInfos.get(0).getUserId()).createDate(testUserInfos.get(0).getCreateDate()).build());
    int userCount = userInfoDao.getAllCount();
    int restUserCount = restUserInfoDao.getAllCount();
    log.info("------ before set data. userCount=[{}], restUserCount=[{}]", userCount, restUserCount);
  }

  @Test
  public void restUserJob() throws Exception {
     JobParameters jobParameters =
            new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis())
                    .addString("startDate", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 00:00:00")
                    .addString("endDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 23:59:59")
                    .toJobParameters();

    JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher().run(templateChuckDefaultJob.restUserJob(restUserStep), jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(actualJobInstance.getJobName()).isEqualTo(TemplateChuckDefaultJob.JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    assertThat(userInfoDao.getAllCount()).isEqualTo(restUserInfoDao.getAllCount());
  }

  @After
  public void clear(){
    userInfoDao.deleteAll();
    restUserInfoDao.deleteAll();
    log.info("---- clear test data.");
  }
}