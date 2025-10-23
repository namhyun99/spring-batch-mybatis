package com.template.batch.job;

import com.template.batch.entity.RestUserInfo;
import com.template.batch.entity.UserInfo;
import com.template.batch.listener.TemplateJobListener;
import com.template.batch.listener.TemplateStepListener;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TemplateJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;
  private final SqlSessionFactory sqlSessionFactory;
  private final TemplateJobListener templateJobListener;
  private final TemplateStepListener templateStepListener;

  private static int CHUCK_SIZE = 5000;

  @Bean
  public Job restUserJob() {
    return jobBuilderFactory.get("restUserJob")
            .listener(templateJobListener)
            .start(restUserStep())
            .build();
  }


  @Bean
  public Step restUserStep(){
    return stepBuilderFactory.get("restUserStep")
            .listener(templateStepListener)
            .<UserInfo, RestUserInfo>chunk(CHUCK_SIZE)
            .reader(reader())
            .faultTolerant()
            .processor(processor())
            .writer(writer())
            .build();
  }

  @Bean
  @StepScope
  public MyBatisPagingItemReader<UserInfo> reader() {
    LocalDateTime startDate = LocalDateTime.of(2025,10,23, 0, 0 ,0);
    LocalDateTime endDate = LocalDateTime.of(2025,10,23, 23, 59 ,59);

    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("startDate", startDate);
    parameterValues.put("endDate", endDate);

    return new MyBatisPagingItemReaderBuilder<UserInfo>()
            .sqlSessionFactory(sqlSessionFactory)
            .queryId("com.template.batch.dao.UserInfoDao.findByCreateDateBetween")
            .parameterValues(parameterValues)
            .pageSize(CHUCK_SIZE)
            .build();
  }

  @Bean
  public ItemProcessor<UserInfo, RestUserInfo> processor() {
    return item -> {
      return RestUserInfo.builder()
              .userId(item.getUserId())
              .createDate(item.getCreateDate())
              .build();
    };
  };

  @Bean
  public MyBatisBatchItemWriter<RestUserInfo> writer() {
    return new MyBatisBatchItemWriterBuilder<RestUserInfo>()
            .sqlSessionFactory(sqlSessionFactory)
            .statementId("com.template.batch.dao.RestUserInfoDao.add")
            .assertUpdates(true)
            .build();
  }

}
