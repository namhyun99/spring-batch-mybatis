package com.template.batch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig extends DefaultBatchConfigurer {

  private final DataSource masterDataSource;
  private final PlatformTransactionManager mainTransactionManager;

  @Override
  protected JobRepository createJobRepository() throws Exception {
    JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
    factory.setDataSource(masterDataSource);
    factory.setTablePrefix("BATCH_");
    factory.setTransactionManager(new DataSourceTransactionManager(masterDataSource));
    factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
    factory.afterPropertiesSet();
    return factory.getObject();
  }

  @Override
  protected JobLauncher createJobLauncher() throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
    jobLauncher.setJobRepository(getJobRepository());
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }

  @Bean(name = "asyncJobLauncher")
  public JobLauncher asyncJobLauncher() throws Exception {
    SimpleJobLauncher jobLauncher = new SimpleJobLauncher();

    jobLauncher.setJobRepository(getJobRepository());

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(1);
    executor.setThreadNamePrefix("execute-async-");
    executor.initialize();

    jobLauncher.setTaskExecutor(executor);
    jobLauncher.afterPropertiesSet();
    return jobLauncher;
  }

  @Bean
  public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
    JobRegistryBeanPostProcessor processor = new JobRegistryBeanPostProcessor();
    processor.setJobRegistry(jobRegistry);
    return processor;
  }

}
