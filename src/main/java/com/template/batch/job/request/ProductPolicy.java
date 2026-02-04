package com.template.batch.job.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPolicy {
  private String code;
  private String name;
  private int  minQty;
  private int  maxQty;
}
