package com.template.batch.listener;

import com.template.batch.BatchConstants;
import com.template.batch.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TemplateJobListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    String batchJobId = IdGenerator.generateJobId();
    jobExecution.getExecutionContext().putString(BatchConstants.BATCH_JOB_ID.name(), batchJobId);
    log.warn("[{}] beforeJob. jobId=[{}], jobName=[{}]",  batchJobId, jobExecution.getJobId(), jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    String batchJobId = jobExecution.getExecutionContext().getString(BatchConstants.BATCH_JOB_ID.name());
    log.warn("[{}] afterJob. jobId=[{}], jobName=[{}]", batchJobId, jobExecution.getJobId(), jobExecution.getJobInstance().getJobName());
  }
}
