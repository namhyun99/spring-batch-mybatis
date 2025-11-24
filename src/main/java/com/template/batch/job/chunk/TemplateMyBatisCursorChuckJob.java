package com.template.batch.job.chunk;

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
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TemplateMyBatisCursorChuckJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final TemplateJobListener templateJobListener;
  private final TemplateStepListener templateStepListener;
  private final TemplateChuckListener templateChuckListener;
  private final TemplateDBConnectListener templateJDBCConnectListener;

  public static final String JOB_NAME = "restUserCursorJob";
  public static final String STEP_NAME = "restUserCursorStep";
  public static final int CHUCK_SIZE = 10;

  @Bean
  public Job restUserCursorJob(Step restUserCursorStep) {
    return jobBuilderFactory.get(JOB_NAME)
            .listener(templateJobListener)
            .start(restUserCursorStep)
            .build();
  }

  @Bean
  public Step restUserCursorStep(
          MyBatisCursorItemReader<UserInfo> cursorItemReader,
          ItemProcessor<UserInfo, RestUserInfo> processor,
          MyBatisBatchItemWriter<RestUserInfo> writer,
          @Qualifier("slaveTransactionManager") PlatformTransactionManager slaveTransactionManager
  ){
    return stepBuilderFactory.get(STEP_NAME)
            .listener(templateStepListener)
            .<UserInfo, RestUserInfo>chunk(CHUCK_SIZE)
            .reader(cursorItemReader)
            .processor(processor)
            .writer(writer)
            .listener(templateChuckListener)
//            .listener(templateJDBCConnectListener)
            .transactionManager(slaveTransactionManager)
            .build();
  }

  @Bean
  @StepScope
  public MyBatisCursorItemReader<UserInfo> cursorItemReader(
          @Value("#{jobParameters[startDate]}") String startDate,
          @Value("#{jobParameters[endDate]}") String endDate,
          @Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory
  ) {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    Map<String, Object> parameterValues = new HashMap<>();
    parameterValues.put("startDate", LocalDateTime.parse(startDate, formatter));
    parameterValues.put("endDate", LocalDateTime.parse(endDate, formatter));


    return new MyBatisCursorItemReaderBuilder<UserInfo>()
            .sqlSessionFactory(sqlSessionFactory)
            .queryId("com.template.batch.dao.master.UserInfoDao.findByCreateDateBetween")
            .parameterValues(parameterValues)
//            .maxItemCount(CHUCK_SIZE)
            .saveState(true) // restart 지원 여부
            .build();
  }

}
