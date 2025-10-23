package com.template.batch.entity;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RestUserInfo {
  private String userId;
  private LocalDateTime createDate;
}
