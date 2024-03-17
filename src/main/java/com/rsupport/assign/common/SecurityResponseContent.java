package com.rsupport.assign.common;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SecurityResponseContent {

  private int status;
  private LocalDateTime at;
  private String message;
  private String requestPath;

}
