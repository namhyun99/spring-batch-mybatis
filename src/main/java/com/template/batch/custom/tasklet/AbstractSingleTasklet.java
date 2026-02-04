package com.template.batch.custom.tasklet;

import com.template.batch.BatchExecutionKey;
import com.template.batch.dto.ProcessResult;
import com.template.batch.entity.type.BatchJobType;
import com.template.batch.util.IdGeneratorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@StepScope
public abstract class AbstractSingleTasklet implements Tasklet {

  @Value("#{jobParameters[" + BatchExecutionKey.JOB_TYPE + "]}")
  private String jobType;
  
  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    BatchJobType batchJobType = BatchJobType.valueOf(jobType);
    String jobId = IdGeneratorUtil.generateJobId(jobType);

    ProcessResult processResult = new ProcessResult();
    processResult.setName( chunkContext.getStepContext().getJobName());
    
    try {
      processResult = this.doExecute(contribution, chunkContext);
    } catch (Exception e) {
      log.error("[{}] error.", jobId, e);
    }

    return RepeatStatus.FINISHED;
  }

  protected abstract ProcessResult doExecute(StepContribution contribution, ChunkContext chunkContext);

  protected String getJobId(ChunkContext chunkContext){
    return (String) chunkContext.getStepContext().getJobExecutionContext().get(BatchExecutionKey.JOB_ID);
  }
}
