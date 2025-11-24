package com.template.batch.custom.writer.builder;

import com.template.batch.custom.writer.GenericMapperBatchItemWriter;
import com.template.batch.custom.writer.GenericMapperItemWriter;
import com.template.batch.custom.writer.MapperItemWriteFunction;

public class GenericMapperItemWriterBuilder<T> {
  private MapperItemWriteFunction<T> writerFunction;

  public GenericMapperItemWriterBuilder<T> writerFunction(MapperItemWriteFunction<T> writerFunction) {
    this.writerFunction = writerFunction;
    return this;
  }

  public GenericMapperBatchItemWriter<T> build() {
    GenericMapperItemWriter<T> writer = new GenericMapperItemWriter<T>();
    writer.setWriterFunction(writerFunction);
    return writer;
  }
}
