package com.template.batch.job.chunk;

import com.template.batch.BatchException;
import com.template.batch.dao.slave.RestUserInfoDao;
import com.template.batch.entity.master.UserInfo;
import com.template.batch.entity.slave.RestUserInfo;
import com.template.batch.listener.TemplateJobListener;
import com.template.batch.listener.TemplateStepListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TemplateChuckRetryCaseJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final TemplateJobListener templateJobListener;
  private final TemplateStepListener templateStepListener;

  private final RestUserInfoDao restUserInfoDao;

  public static final String JOB_NAME = "writerRetryJob";
  public static final String STEP_NAME = "writerRetryStep";
  private final int CHUCK_SIZE = 5000;


  @Bean
  public Job writerRetryJob(Step writerRetryStep) {
    return jobBuilderFactory.get(JOB_NAME)
            .listener(templateJobListener)
            .start(writerRetryStep)
            .build();
  }

  @Bean
  public Step writerRetryStep(
          MyBatisPagingItemReader<UserInfo> retryReader,
          ItemProcessor<UserInfo, RestUserInfo> retryProcessor,
          ItemWriter<RestUserInfo> retryWriter,
          @Qualifier("slaveTransactionManager") PlatformTransactionManager slaveTransactionManager
  ) {
    return stepBuilderFactory.get(STEP_NAME)
            .listener(templateStepListener)
            .<UserInfo, RestUserInfo>chunk(CHUCK_SIZE)
            .reader(retryReader)
            .processor(retryProcessor)
            .writer(retryWriter)
            .faultTolerant()
            .retryLimit(3)
            .retry(Exception.class)
            .transactionManager(slaveTransactionManager)
            .build();
  }

  @Bean
  @StepScope
  public MyBatisPagingItemReader<UserInfo> retryReader(
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
  public ItemProcessor<UserInfo, RestUserInfo> retryProcessor() {
    return item ->
            RestUserInfo.builder()
                    .userId(item.getUserId())
                    .createDate(item.getCreateDate())
                    .build();

  }


  private int attempt = 0;

  @Bean
  public ItemWriter<RestUserInfo> retryWriter(@Qualifier("slaveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
    return items ->
            items.stream().forEach(r -> {
              String seqNum = r.getUserId().substring(r.getUserId().length() - 4, r.getUserId().length());
//        log.info("attempt=[{}], seqNum=[{}], {}", attempt, seqNum, r);
              if (seqNum.equals("0024") && attempt++ < 2) {
                log.error("Writer Exception at UserId=[{}]", r.getUserId());
                throw new BatchException("Writer Exception at UserId=" + r.getUserId());
              }

              restUserInfoDao.add(r);
            });
  }
}
