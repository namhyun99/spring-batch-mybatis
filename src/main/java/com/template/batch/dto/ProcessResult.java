package com.template.batch.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProcessResult  {
  private String name;
  private int fileSize;
  private int totalCount;
  private int processCount;
  private int unProcessCount;
  private String resultCode;
  private String resultMessage;
}
