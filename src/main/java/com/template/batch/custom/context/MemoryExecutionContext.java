package com.template.batch.custom.context;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryExecutionContext implements Serializable {
  private volatile boolean dirty;
  private final Map<String, Object> map;

  public MemoryExecutionContext() {
    this.dirty = false;
    this.map = new ConcurrentHashMap();
  }

  public MemoryExecutionContext(Map<String, Object> map) {
    this.dirty = false;
    this.map = new ConcurrentHashMap(map);
  }

  public MemoryExecutionContext(ExecutionContext executionContext) {
    this();
    if (executionContext != null) {
      for(Map.Entry<String, Object> entry : executionContext.entrySet()) {
        this.map.put(entry.getKey(), entry.getValue());
      }

    }
  }

  public void putString(String key, @Nullable String value) {
    this.put(key, value);
  }

  public void putLong(String key, long value) {
    this.put(key, value);
  }

  public void putInt(String key, int value) {
    this.put(key, value);
  }

  public void putDouble(String key, double value) {
    this.put(key, value);
  }

  public void put(String key, @Nullable Object value) {
    if (value != null) {
      Object result = this.map.put(key, value);
      this.dirty = result == null || result != null && !result.equals(value);
    } else {
      Object result = this.map.remove(key);
      this.dirty = result != null;
    }

  }

  public boolean isDirty() {
    return this.dirty;
  }

  public String getString(String key) {
    return (String)this.readAndValidate(key, String.class);
  }

  public String getString(String key, String defaultString) {
    return !this.containsKey(key) ? defaultString : this.getString(key);
  }

  public long getLong(String key) {
    return (Long)this.readAndValidate(key, Long.class);
  }

  public long getLong(String key, long defaultLong) {
    return !this.containsKey(key) ? defaultLong : this.getLong(key);
  }

  public int getInt(String key) {
    return (Integer)this.readAndValidate(key, Integer.class);
  }

  public int getInt(String key, int defaultInt) {
    return !this.containsKey(key) ? defaultInt : this.getInt(key);
  }

  public double getDouble(String key) {
    return (Double)this.readAndValidate(key, Double.class);
  }

  public double getDouble(String key, double defaultDouble) {
    return !this.containsKey(key) ? defaultDouble : this.getDouble(key);
  }

  @Nullable
  public Object get(String key) {
    return this.map.get(key);
  }

  private Object readAndValidate(String key, Class<?> type) {
    Object value = this.get(key);
    if (!type.isInstance(value)) {
      throw new ClassCastException("Value for key=[" + key + "] is not of type: [" + type + "], it is [" + (value == null ? null : "(" + value.getClass() + ")" + value) + "]");
    } else {
      return value;
    }
  }

  public boolean isEmpty() {
    return this.map.isEmpty();
  }

  public void clearDirtyFlag() {
    this.dirty = false;
  }

  public Set<Map.Entry<String, Object>> entrySet() {
    return this.map.entrySet();
  }

  public boolean containsKey(String key) {
    return this.map.containsKey(key);
  }

  @Nullable
  public Object remove(String key) {
    return this.map.remove(key);
  }

  public boolean containsValue(Object value) {
    return this.map.containsValue(value);
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof ExecutionContext)) {
      return false;
    } else if (this == obj) {
      return true;
    } else {
      ExecutionContext rhs = (ExecutionContext)obj;
      return this.entrySet().equals(rhs.entrySet());
    }
  }

  public int hashCode() {
    return this.map.hashCode();
  }

  public String toString() {
    return this.map.toString();
  }

  public int size() {
    return this.map.size();
  }

}
