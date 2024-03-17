package com.rsupport.assign.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {

  private boolean success;
  private String message;
  private Object id;
  private Object optional;

}
