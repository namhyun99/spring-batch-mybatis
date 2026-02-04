package com.template.batch.job.request;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiRequestJobTest {

  @Autowired
  private ApiRequestJob apiRequestJob;
  @Autowired
  private JobLauncherTestUtils jobLauncherTestUtils;

  @Test
  public void testApiRequestJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    jobLauncherTestUtils.getJobLauncher().run(
        apiRequestJob.jobApiRequest(),
        getJobParameter()
    );
  }

  private JobParameters getJobParameter() {
    return new JobParametersBuilder()
        .addDate("timestamp", new Date())
        .toJobParameters();
  }
}