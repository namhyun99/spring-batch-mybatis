package com.template.batch.listener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;



@Slf4j
@Component
public class TemplateStepListener implements StepExecutionListener {

  @Override
  public void beforeStep(StepExecution stepExecution) {
    log.warn(">>>>>> beforeStep. stepName=[{}]",  stepExecution.getStepName());

  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    log.warn(">>>>>> afterStep. stepName=[{}], readCount=[{}], writeCount=[{}], commitCount=[{}]",
            stepExecution.getStepName(), stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getCommitCount());
    return stepExecution.getExitStatus();
  }
}
