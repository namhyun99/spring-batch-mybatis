package com.template.batch.custom.writer;


import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.springframework.util.Assert.notNull;

@Slf4j
public class GenericMapperItemWriter<T> extends GenericMapperBatchItemWriter<T> {
  private MapperItemWriteFunction<T> writerFunction;

  public void setWriterFunction(MapperItemWriteFunction<T> writerFunction) {
    this.writerFunction = writerFunction;
  }

  @Override
  public void write(List<? extends T> items) {
    if (!items.isEmpty()) {
      log.debug("Executing batch with {} items.", items.size());

      for (T item : items) {
        writerFunction.write(item);
      }
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    notNull(writerFunction, "A writerFunction or a writerFunction is required.");
  }
}
