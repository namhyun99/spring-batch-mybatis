package com.template.batch.job;

import com.template.batch.BatchException;
import com.template.batch.dao.slave.RestUserInfoDao;
import com.template.batch.entity.slave.RestUserInfo;
import com.template.batch.entity.master.UserInfo;
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
public class TemplateChuckErrorCaseJob {
  private final JobBuilderFactory jobBuilderFactory;
  private final StepBuilderFactory stepBuilderFactory;

  private final TemplateJobListener templateJobListener;
  private final TemplateStepListener templateStepListener;

  private final RestUserInfoDao restUserInfoDao;

  public static final String JOB_NAME = "writerErrorJob";
  public static final String STEP_NAME = "writerErrorStep";
  private final int CHUCK_SIZE = 10;



  @Bean
  public Job writerErrorJob(Step writerErrorStep) {
    return jobBuilderFactory.get(JOB_NAME)
            .listener(templateJobListener)
            .start(writerErrorStep)
            .build();
  }

  @Bean
  public Step writerErrorStep(
          MyBatisPagingItemReader<UserInfo> errorReader,
          ItemProcessor<UserInfo, RestUserInfo> errorProcessor,
          ItemWriter<RestUserInfo> errorWriter,
          @Qualifier("slaveTransactionManager") PlatformTransactionManager slaveTransactionManager
  ){
    return stepBuilderFactory.get(STEP_NAME)
            .listener(templateStepListener)
            .<UserInfo, RestUserInfo>chunk(CHUCK_SIZE)
            .reader(errorReader)
            .processor(errorProcessor)
            .writer(errorWriter)
            .transactionManager(slaveTransactionManager)
            .build();
  }

  @Bean
  @StepScope
  public MyBatisPagingItemReader<UserInfo> errorReader(
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
  public ItemProcessor<UserInfo, RestUserInfo> errorProcessor() {
    return item -> {
      return RestUserInfo.builder()
              .userId(item.getUserId())
              .createDate(item.getCreateDate())
              .build();
    };
  };

  @Bean
  public ItemWriter<RestUserInfo> errorWriter(@Qualifier("slaveSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
    return items -> {

      items.stream().forEach( r -> {
        String seqNum = r.getUserId().substring(r.getUserId().length() - 4, r.getUserId().length());
//        log.info("seqNum=[{}], {}", seqNum, r);
        if(seqNum.equals("0024")) {
          throw new BatchException("Writer Exception at UserId=" + r.getUserId());
        }

        restUserInfoDao.add(r);
      });
    };
  }
}
