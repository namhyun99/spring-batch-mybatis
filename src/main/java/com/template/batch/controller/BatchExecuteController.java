package com.template.batch.controller;

import com.template.batch.job.SimpleTaskletTestJob;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping(value = "/api/v1/execute")
@RequiredArgsConstructor
public class BatchExecuteController {

  private final JobOperator jobOperator;

  @Qualifier("asyncJobLauncher")
  private final JobLauncher asyncJobLauncher;
  private final SimpleTaskletTestJob simpleJob;


  @GetMapping("/run")
  public long findAllJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    return asyncJobLauncher.run(simpleJob.jobSimpleTaskletTest(), getJobParameters()).getId();
  }

  private JobParameters getJobParameters() {
    return new JobParametersBuilder()
            .addDate("timestamp", new Date())
            .toJobParameters();
  }

  @GetMapping("/stop/{executionId}")
  public boolean stop(@PathVariable long executionId) throws NoSuchJobExecutionException, JobExecutionNotRunningException {
    return jobOperator.stop(executionId);
  }

}
