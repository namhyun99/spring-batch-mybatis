package com.template.batch.job.partition;

import com.template.batch.dao.master.UserInfoDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TemplatePartitioner implements Partitioner, StepExecutionListener {

  private final UserInfoDao userInfoDao;

  public TemplatePartitioner(UserInfoDao userInfoDao) {
    this.userInfoDao = userInfoDao;
  }

  @Override
  public Map<String, ExecutionContext> partition(int gridSize) {

    int allCount = userInfoDao.getAllCount();
    gridSize = allCount == 10000000 ? 1 : gridSize;

    int partitionSize = allCount / gridSize;

    Map<String, ExecutionContext> result = new HashMap<>();
    for(int i=0; i < gridSize; i++){
      ExecutionContext executionContext = new ExecutionContext();
      result.put("partition-"+i, executionContext);
    }

    return result;
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {

  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    return null;
  }
}
