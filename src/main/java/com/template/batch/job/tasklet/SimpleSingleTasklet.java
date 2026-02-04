package com.template.batch.job.tasklet;

import com.template.batch.BatchException;
import com.template.batch.custom.tasklet.AbstractSingleTasklet;
import com.template.batch.dto.ProcessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
@StepScope
public class SimpleSingleTasklet extends AbstractSingleTasklet {

  @Override
  protected ProcessResult doExecute(StepContribution contribution, ChunkContext chunkContext) {

    throw new BatchException("error test", "3893");
    //return null;
  }
}
