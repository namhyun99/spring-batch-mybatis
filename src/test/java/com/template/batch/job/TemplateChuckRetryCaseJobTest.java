package com.template.batch.job;

import com.template.batch.BatchConstants;
import com.template.batch.BatchJobType;
import com.template.batch.job.chunk.TemplateChuckRetryCaseJob;
import lombok.extern.slf4j.Slf4j;
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
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateChuckRetryCaseJobTest extends AbstractSetUserTestData{

  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;
  @Autowired
  private TemplateChuckRetryCaseJob templateChuckRetryCaseJob;
  @Autowired
  @Qualifier(TemplateChuckRetryCaseJob.STEP_NAME)
  private Step writerRetryStep;


  @Test
  public void writerRetryJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    JobParameters jobParameters =
            new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis())
                    .addString(BatchConstants.BATCH_JOB_TYPE.name(), BatchJobType.TEMPLATE.getCode())
                    .addString("startDate", LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 00:00:00")
                    .addString("endDate", LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " 23:59:59")
                    .toJobParameters();

    JobExecution jobExecution = jobLauncherTestUtils.getJobLauncher().run(templateChuckRetryCaseJob.writerRetryJob(writerRetryStep), jobParameters);
    JobInstance actualJobInstance = jobExecution.getJobInstance();
    ExitStatus actualJobExitStatus = jobExecution.getExitStatus();

    // then
    assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    assertThat(actualJobInstance.getJobName()).isEqualTo(TemplateChuckRetryCaseJob.JOB_NAME);
    assertThat(actualJobExitStatus.getExitCode()).isEqualTo(ExitStatus.COMPLETED.getExitCode());

    assertThat(userInfoDao.getAllCount()).isEqualTo(100);
    assertThat(restUserInfoDao.getAllCount()).isEqualTo(100);
  }

}