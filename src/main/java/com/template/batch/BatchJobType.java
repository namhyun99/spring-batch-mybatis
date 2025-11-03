package com.template.batch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BatchJobType {
  TEMPLATE ("TP"),
  ;

  private final String code;


  public static BatchJobType find(String code){
    return Arrays.stream(BatchJobType.values())
            .filter(a -> a.getCode().equals(code))
            .findFirst()
            .orElse(null);
  }
}
