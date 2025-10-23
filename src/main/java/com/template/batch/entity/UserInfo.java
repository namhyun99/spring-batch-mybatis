package com.template.batch.entity;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
  private String userId;
  private String restFlag;
  private LocalDateTime createDate;
}
