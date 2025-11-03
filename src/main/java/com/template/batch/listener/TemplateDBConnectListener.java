package com.template.batch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TemplateDBConnectListener implements
        ItemReadListener<Object>,
        ItemProcessListener<Object, Object>,
        ItemWriteListener<Object>,
        ChunkListener {

  @Qualifier("masterSqlSessionFactory") private final SqlSessionFactory masterSqlSessionFactory;
  @Qualifier("slaveSqlSessionFactory") private final SqlSessionFactory slaveSqlSessionFactory;


  // --- ChunkListener ---
  @Override
  public void beforeChunk(ChunkContext context) {
    logConnection("BeforeChunk", masterSqlSessionFactory, slaveSqlSessionFactory);
  }

  @Override
  public void afterChunk(ChunkContext context) {
    logConnection("AfterChunk", masterSqlSessionFactory, slaveSqlSessionFactory);
  }

  @Override
  public void afterChunkError(ChunkContext context) {
    log.error("[Chunk] Error in step {}", context.getStepContext().getStepName());
  }

  // --- ItemReadListener ---
  @Override
  public void beforeRead() {
  }

  @Override
  public void afterRead(Object item) {
    logConnection("AfterRead", masterSqlSessionFactory, slaveSqlSessionFactory);
  }

  @Override
  public void onReadError(Exception ex) {
    log.error("[Reader] Error", ex);
  }

  // --- ItemProcessListener ---
  @Override
  public void beforeProcess(Object item) {
  }

  @Override
  public void afterProcess(Object item, Object result) {
  }

  @Override
  public void onProcessError(Object item, Exception e) {
    log.error("[Processor] Error on item {}", item, e);
  }

  // --- ItemWriteListener ---
  @Override
  public void beforeWrite(List<?> items) {
  }

  @Override
  public void afterWrite(List<?> items) {
  }

  @Override
  public void onWriteError(Exception e, List<?> items) {
    log.error("[Writer] Error", e);
  }

  // --- 공통 Connection 로깅 메서드 ---
  private void logConnection(String phase, SqlSessionFactory master, SqlSessionFactory slave) {
    try (Connection masterConn = master.openSession().getConnection();
         Connection slaveConn = slave.openSession().getConnection()) {

      log.info("[{}] MasterConnection={} valid={}", phase, masterConn.hashCode(), masterConn.isValid(1));
      log.info("[{}] SlaveConnection={} valid={}", phase, slaveConn.hashCode(), slaveConn.isValid(1));

    } catch (SQLException e) {
      log.error("[{}] Connection check failed", phase, e);
    }
  }
}
