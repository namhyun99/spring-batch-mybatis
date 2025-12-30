package com.template.batch.job;

import com.template.batch.BatchExecutionKey;
import com.template.batch.entity.type.BatchJobType;
import com.template.batch.job.chunk.TemplateMyBatisPagingChuckJob;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateMybatisPagingChuckJobTest extends AbstractSetUserTestData{

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired
  private TemplateMyBatisPagingChuckJob templateChuckDefaultJob;
  @Autowired
  @Qualifier(TemplateMyBatisPagingChuckJob.STEP_NAME)
  private Step restUserStep;

  @Test
  public void restUserJob() throws Exception {
     JobParameters jobParameters =
            new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis())
                    .addString(BatchExecutionKey.JOB_TYPE.name(), BatchJobType.TEMPLATE.getCode())
                    .addString("startDate", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 00:00:00")
                    .addString("endDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 23:59:59")
                    .toJobParameters();

    JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher().run(templateChuckDefaultJob.restUserJob(restUserStep), jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(actualJobInstance.getJobName()).isEqualTo(TemplateMyBatisPagingChuckJob.JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    assertThat(userInfoDao.getAllCount()).isEqualTo(restUserInfoDao.getAllCount());
  }
}