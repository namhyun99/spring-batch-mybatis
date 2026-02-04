package com.template.batch.job.request;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApiResponse {
  private boolean success;
  private String message;
}
