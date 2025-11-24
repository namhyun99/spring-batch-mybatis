package com.template.batch.custom.reader;

import java.util.List;

@FunctionalInterface
public interface MapperItemReadFunction<T> {
  List<T> read(int skipRows, int pageSize);
}
