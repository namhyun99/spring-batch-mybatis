package com.template.batch.custom.writer;

import java.util.List;

@FunctionalInterface
public interface MapperItemListWriteFunction<T> {
  int writeList(List<T> item);
}
