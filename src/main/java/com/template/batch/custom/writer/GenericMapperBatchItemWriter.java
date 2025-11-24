package com.template.batch.custom.writer;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;


public abstract class GenericMapperBatchItemWriter<T> implements ItemWriter<T>, InitializingBean {

  public abstract void write(List<? extends T> items) throws Exception;

  public abstract void afterPropertiesSet() throws Exception;

}