package com.template.batch.job;

import com.template.batch.entity.slave.RestUserInfo;
import com.template.batch.entity.master.UserInfo;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class TemplateChuckDefaultJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final TemplateJobListener templateJobListener;
  private final TemplateStepListener templateStepListener;

  public static final String JOB_NAME = "restUserJob";
  public static final String STEP_NAME = "restUserStep";
  private final int CHUCK_SIZE = 5000;

  @Bean
  public Job restUserJob(Step restUserStep) {
    return jobBuilderFactory.get(JOB_NAME)
            .listener(templateJobListener)
            .start(restUserStep)
            .build();
  }

  @Bean
  public Step restUserStep(
          MyBatisPagingItemReader<UserInfo> reader,
          ItemProcessor<UserInfo, RestUserInfo> processor,
          MyBatisBatchItemWriter<RestUserInfo> writer,
          @Qualifier("slaveTransactionManager") PlatformTransactionManager slaveTransactionManager
  ){
    return stepBuilderFactory.get(STEP_NAME)
            .listener(templateStepListener)
            .<UserInfo, RestUserInfo>chunk(CHUCK_SIZE)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .faultTolerant()
            .retryLimit(2)
            .retry(Exception.class)
            .transactionManager(slaveTransactionManager)
            .build();
  }

  @Bean
  @StepScope
  public MyBatisPagingItemReader<UserInfo> reader(
          @Value("#{jobParameters[startDate]}") String startDate,
          @Value("#{jobParameters[endDate]}") String endDate,
          @Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory
  ) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("startDate", LocalDateTime.parse(startDate, formatter));
    parameterValues.put("endDate", LocalDateTime.parse(endDate, formatter));

    return new MyBatisPagingItemReaderBuilder<UserInfo>()
            .sqlSessionFactory(sqlSessionFactory)
            .queryId("com.template.batch.dao.master.UserInfoDao.findByCreateDateBetween")
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
  public MyBatisBatchItemWriter<RestUserInfo> writer(@Qualifier("slaveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
    return new MyBatisBatchItemWriterBuilder<RestUserInfo>()
            .sqlSessionFactory(sqlSessionFactory)
            .statementId("com.template.batch.dao.slave.RestUserInfoDao.add")
            .assertUpdates(true)
            .build();
  }

}
