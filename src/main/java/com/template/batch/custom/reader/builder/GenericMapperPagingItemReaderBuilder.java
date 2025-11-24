package com.template.batch.custom.reader.builder;

import com.template.batch.custom.reader.GenericMapperPagingItemReader;
import com.template.batch.custom.reader.MapperItemReadFunction;

import java.util.Optional;

public class GenericMapperPagingItemReaderBuilder<T> {

  private MapperItemReadFunction<T> queryFunction;
  private boolean fixedZeroSkipRows;

  private Integer pageSize;
  private Boolean saveState;
  private Integer maxItemCount;

  public GenericMapperPagingItemReaderBuilder<T> queryFunction(MapperItemReadFunction<T> queryFunction) {
    this.queryFunction = queryFunction;
    return this;
  }

  public GenericMapperPagingItemReaderBuilder<T> fixedZeroSkipRows(boolean fixedZeroSkipRows) {
    this.fixedZeroSkipRows = fixedZeroSkipRows;
    return this;
  }

  public GenericMapperPagingItemReaderBuilder<T> pageSize(int pageSize) {
    this.pageSize = pageSize;
    return this;
  }

  public GenericMapperPagingItemReaderBuilder<T> saveState(boolean saveState) {
    this.saveState = saveState;
    return this;
  }

  public GenericMapperPagingItemReaderBuilder<T> maxItemCount(int maxItemCount) {
    this.maxItemCount = maxItemCount;
    return this;
  }

  public GenericMapperPagingItemReader<T> build() {
    GenericMapperPagingItemReader<T> reader = new GenericMapperPagingItemReader<>();
    reader.setQueryFunction(this.queryFunction);
    Optional.ofNullable(this.fixedZeroSkipRows).ifPresent(reader::setFixedZeroSkipRows);
    Optional.ofNullable(this.pageSize).ifPresent(reader::setPageSize);
    Optional.ofNullable(this.saveState).ifPresent(reader::setSaveState);
    Optional.ofNullable(this.maxItemCount).ifPresent(reader::setMaxItemCount);
    return reader;
  }
}
