package com.template.batch.custom.writer;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static org.springframework.util.Assert.notNull;

@Slf4j
public class GenericMapperListWriter<T> extends GenericMapperBatchItemWriter<T> {
  private MapperItemListWriteFunction<T> writeListFunction;

  public void setWriteListFunction(MapperItemListWriteFunction<T> writeListFunction) {
    this.writeListFunction = writeListFunction;
  }

  @Override
  public void write(List<? extends T> items) {
    if(!items.isEmpty()) {
      writeListFunction.writeList((List<T>) items);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    notNull(writeListFunction, "writeListFunction or a writeListFunction is required.");
  }
}
