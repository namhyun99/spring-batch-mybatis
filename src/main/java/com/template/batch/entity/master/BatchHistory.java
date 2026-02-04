package com.template.batch.entity.master;

import lombok.*;
import org.springframework.batch.core.BatchStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BatchHistory {
  private String jobId;   //pk
  private String jobType;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private String name;
  private int fileSize;
  private int totalCount;
  private int processCount;
  private int unprocessCount;
  private BatchStatus batchStatus;
  private String resultCode;
  private String resultMessage;
}
