package com.template.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleTaskletTestJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final JobRepository jobRepository;

  @Bean
  public Job jobSimpleTaskletTest(){
    return jobBuilderFactory.get("jobSimpleTaskletTest")
            .start(stepSimpleTaskletTest())
            .build();
  }
  @Bean
  public Step stepSimpleTaskletTest() {
    return stepBuilderFactory.get("stepSimpleTaskletTest")
            .tasklet((contribution, chunkContext) -> {
              log.info(">>>> start simpleStep..");
              StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();

              int idx = 0;
              int endIdx = 100;
              while (true) {
                checkStop(stepExecution, idx);

                if(idx == endIdx) {
                  break;
                }

                Thread.sleep(1000);

                idx ++;
              }

              log.info("<<<< end   simpleStep... ");
              return RepeatStatus.FINISHED;
            }).build();
  }

  protected void checkStop(StepExecution stepExecution, int idx) throws JobInterruptedException {
    // DB에서 최신 상태를 다시 조회
    StepExecution refreshedStepExecution = jobRepository.getLastStepExecution(
            stepExecution.getJobExecution().getJobInstance(),
            stepExecution.getStepName()
    );

    boolean isTerminateOnly = refreshedStepExecution != null
            ? refreshedStepExecution.isTerminateOnly()
            : stepExecution.isTerminateOnly();

    log.info("[{}] stepExecution.isTerminateOnly=[{}]", idx, isTerminateOnly);

    if (isTerminateOnly) {
      throw new JobInterruptedException("STOP 요청 확인");
    }
  }

}
