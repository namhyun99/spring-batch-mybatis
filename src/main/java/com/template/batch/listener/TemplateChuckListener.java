package com.template.batch.listener;

import com.template.batch.BatchExecutionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TemplateChuckListener implements ChunkListener {
  @Override
  public void beforeChunk(ChunkContext context) {
    String jobId = context.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getString(BatchExecutionKey.JOB_ID);
    log.info("🟢 [{}] BeforeChunk - {}",
            jobId,
            context.getStepContext().getStepName());
  }

  @Override
  public void afterChunk(ChunkContext context) {
    String jobId = context.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getString(BatchExecutionKey.JOB_ID);
    log.info("🔵 [{}] AfterChunk - Read={}, Write={}",
            jobId,
            context.getStepContext().getStepExecution().getReadCount(),
            context.getStepContext().getStepExecution().getWriteCount());
  }

  @Override
  public void afterChunkError(ChunkContext context) {
    String jobId = context.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getString(BatchExecutionKey.JOB_ID);

    log.error("🔴 [{}] Chunk Error - {}", jobId, context.getStepContext().getStepName());
  }
}
