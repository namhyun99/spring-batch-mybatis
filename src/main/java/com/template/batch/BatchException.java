package com.template.batch;

import lombok.Getter;

public class BatchException extends RuntimeException {

  @Getter
  private String code;

  public BatchException(String message) {
    super(message);
  }

  public BatchException(String message, String code) {
    super(message);
    this.code = code;
  }
}
