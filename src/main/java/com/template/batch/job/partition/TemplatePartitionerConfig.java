package com.template.batch.job.partition;

import com.template.batch.dao.master.UserInfoDao;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class TemplatePartitionerConfig {

  @Bean
  public Partitioner templatePartition(UserInfoDao userInfoDao) {
    return new TemplatePartitioner(userInfoDao);
  }

  @Bean("templatePartitionTaskExecutor")
  public TaskExecutor templatePartitionTaskExecutor(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(10);
    executor.setThreadNamePrefix("partition-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(300);
    executor.initialize();
    return executor;
  }
}
