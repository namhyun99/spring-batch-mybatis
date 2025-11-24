package com.template.batch.custom.reader;

import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.ClassUtils.getShortName;

public class GenericMapperPagingItemReader<T> extends AbstractPagingItemReader<T> {
  //private BiFunction<Integer, Integer, List<T>> queryFunction;
  private MapperItemReadFunction<T> queryFunction;
  private boolean fixedZeroSkipRows = false;

  public GenericMapperPagingItemReader() {
    setName(getShortName(GenericMapperPagingItemReader.class));
  }

  public void setQueryFunction(MapperItemReadFunction<T> queryFunction) {
    this.queryFunction = queryFunction;
  }

  public void setFixedZeroSkipRows(boolean fixedZeroSkipRows) {
    this.fixedZeroSkipRows = fixedZeroSkipRows;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();
    notNull(queryFunction, "A queryFunction is required.");
  }

  @Override
  protected void doReadPage() {
    if(queryFunction == null) {
      throw new IllegalStateException("queryFunction must be set before reading.");
    }

    if (results == null) {
      results = new CopyOnWriteArrayList<>();
    } else {
      results.clear();
    }

    int skipRows = fixedZeroSkipRows ? 0 : getPage() * getPageSize();
    int pageSize = getPageSize();

    List<T> pageResult = queryFunction.read(skipRows, pageSize);

    if (pageResult != null) {
      results.addAll(pageResult);
    }
  }

  @Override
  protected void doJumpToPage(int itemIndex) {
    // Not Implemented
  }
}
