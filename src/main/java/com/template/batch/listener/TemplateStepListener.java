package com.template.batch.listener;


import com.template.batch.BatchConstants;
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
    String batchJobId = stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.BATCH_JOB_ID.name());
    log.warn("[{}] beforeStep. stepName=[{}]", batchJobId, stepExecution.getStepName());

  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    String batchJobId = stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.BATCH_JOB_ID.name());

    log.warn("[{}] afterStep. stepName=[{}], readCount=[{}], writeCount=[{}], commitCount=[{}]",
            batchJobId, stepExecution.getStepName(), stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getCommitCount());
    return stepExecution.getExitStatus();
  }
}
