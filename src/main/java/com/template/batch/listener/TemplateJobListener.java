package com.template.batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Slf4j
@Component
public class TemplateJobListener implements JobExecutionListener {

  @Override
  public void beforeJob(JobExecution jobExecution) {
    Set<Map.Entry<String, Object>> entries = jobExecution.getExecutionContext().entrySet();
    log.warn(">>>>>> beforeJob. jobId=[{}], jobName=[{}]",  jobExecution.getJobId(), jobExecution.getJobInstance().getJobName());
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    log.warn(">>>>>> afterJob. jobId=[{}], jobName=[{}]",  jobExecution.getJobId(), jobExecution.getJobInstance().getJobName());
  }
}
