package com.template.batch.job.chunk;

import com.template.batch.custom.reader.GenericMapperPagingItemReader;
import com.template.batch.custom.reader.builder.GenericMapperPagingItemReaderBuilder;
import com.template.batch.custom.writer.GenericMapperBatchItemWriter;
import com.template.batch.custom.writer.builder.GenericMapperItemWriterBuilder;
import com.template.batch.dao.master.UserInfoDao;
import com.template.batch.dao.slave.RestUserInfoDao;
import com.template.batch.entity.master.UserInfo;
import com.template.batch.entity.slave.RestUserInfo;
import com.template.batch.listener.TemplateChuckListener;
import com.template.batch.listener.TemplateDBConnectListener;
import com.template.batch.listener.TemplateJobListener;
import com.template.batch.listener.TemplateStepListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TemplateMapperPagingChuckJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final TemplateJobListener templateJobListener;
  private final TemplateStepListener templateStepListener;
  private final TemplateChuckListener templateChuckListener;
  private final TemplateDBConnectListener templateJDBCConnectListener;

  public static final String JOB_NAME = "restUserJob2";
  public static final String STEP_NAME = "restUserStep2";

  public static final int CHUCK_SIZE = 10;

  @Bean
  public Job restUserJob2(Step restUserStep) {
    return jobBuilderFactory.get(JOB_NAME)
            .listener(templateJobListener)
            .start(restUserStep)
            .build();
  }

  @Bean
  public Step restUserStep2(
          GenericMapperPagingItemReader<UserInfo> customMapperReader,
          ItemProcessor<UserInfo, RestUserInfo> customProcessor2,
          MyBatisBatchItemWriter<RestUserInfo> customMapperItemWriter,
          @Qualifier("slaveTransactionManager") PlatformTransactionManager slaveTransactionManager
  ) {
    return stepBuilderFactory.get(STEP_NAME)
            .listener(templateStepListener)
            .<UserInfo, RestUserInfo>chunk(CHUCK_SIZE)
            .reader(customMapperReader)
            .processor(customProcessor2)
            .writer(customMapperItemWriter)
            .listener(templateChuckListener)
//            .listener(templateJDBCConnectListener)
            .transactionManager(slaveTransactionManager)
            .build();
  }

  @Bean
  @StepScope
  public GenericMapperPagingItemReader<UserInfo> customMapperReader(
          UserInfoDao userInfoDao,
          @Value("#{jobParameters[startDate]}") String startDate,
          @Value("#{jobParameters[endDate]}") String endDate,
          @Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory
  ) {

    return new GenericMapperPagingItemReaderBuilder<UserInfo>()
            .queryFunction((skipRows, pageSize) -> userInfoDao.findByCreateDateBetween(startDate, endDate, skipRows, pageSize))
            .pageSize(CHUCK_SIZE)
            .build();
  }

  @Bean
  public ItemProcessor<UserInfo, RestUserInfo> customProcessor2() {
    return item ->
            RestUserInfo.builder()
                    .userId(item.getUserId())
                    .createDate(item.getCreateDate())
                    .build();

  }

  @Bean
  public GenericMapperBatchItemWriter<RestUserInfo> customMapperItemWriter(
          RestUserInfoDao restUserInfoDao) {

    return new GenericMapperItemWriterBuilder<RestUserInfo>()
            .writerFunction(item -> restUserInfoDao.add(item))
            .build();
  }

}
