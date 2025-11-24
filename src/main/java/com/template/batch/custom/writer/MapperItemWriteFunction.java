package com.template.batch.custom.writer;

@FunctionalInterface
public interface MapperItemWriteFunction<T> {
  int write(T item);
}
