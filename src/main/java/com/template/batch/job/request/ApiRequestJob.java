package com.template.batch.job.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApiRequestJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  @Bean
  public Job jobApiRequest() {
    return jobBuilderFactory.get("jobApiRequest")
        .start(targetProductStep())
        .next(stepProductApiRequest())
        .build();

  }

  @Bean
  public Step stepProductApiRequest() {
    return stepBuilderFactory.get("stepProductApiRequest")
        .<ApiRequest, ApiResponse>chunk(1)
        .reader(apiRequestUnitReader())
        .processor(apiRequestProcessor())
        .writer(apiRequestWriter())
        .listener(apiChunkListener())
        .build();

  }

  @Bean
  public ChunkListener apiChunkListener() {
    return new ChunkListener() {
      @Override
      public void beforeChunk(ChunkContext context) {
        log.info(" ==== beforeChunk ==== ");
      }

      @Override
      public void afterChunk(ChunkContext context) {
        log.info(" ==== afterChunk ==== ");
      }

      @Override
      public void afterChunkError(ChunkContext context) {
        log.info(" ==== afterChunkError ==== ");
      }
    };
  }

  @Bean
  public ItemWriter<ApiResponse> apiRequestWriter() {
    return items -> {
      for(ApiResponse response : items) {
        log.info("[API RESPONSE] {}", response);
      }
    };
  }

  @Bean
  public ItemProcessor<ApiRequest, ApiResponse> apiRequestProcessor() {
    return request -> {
      log.info("{}", request);

      ApiResponse response = new ApiResponse(true, "success");
      log.info("{}", response);
      return response;
    };
  }

  @Bean
  public ItemReader<ApiRequest> apiRequestUnitReader() {
    return new ProductApiRequestReader();
  }

  @Bean
  public Step targetProductStep() {
    return stepBuilderFactory.get("targetProductStep")
        .tasklet((contribution, chunkContext) -> {

          //1. 전체 프로젝 조회
          List<ProductPolicy> allProductPolicy = findAll();

          //2. 최소수량 이하 product 선별

          //3. 다음 step 에서 사용하도록 저장
          chunkContext.getStepContext()
              .getStepExecution()
              .getJobExecution()
              .getExecutionContext()
              .put("targetProducts", allProductPolicy);

          log.info("targetProducts={}", allProductPolicy.size());
          return RepeatStatus.FINISHED;
        }).build();


  }

  private List<ProductPolicy> findAll() {
    return Arrays.asList(
        new ProductPolicy("ABC", "abc", 10, 10),
        new ProductPolicy("DEF", "def", 20, 20),
        new ProductPolicy("GHI", "ghi", 30, 30)
    );
  }
}
