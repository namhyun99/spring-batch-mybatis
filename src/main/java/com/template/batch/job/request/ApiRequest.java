package com.template.batch.job.request;

import lombok.*;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest {
  private String code;
  private int requestCount;
}
