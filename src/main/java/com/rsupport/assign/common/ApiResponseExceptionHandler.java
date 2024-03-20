package com.rsupport.assign.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * 오류 메세지 공통 처리 handler.
 * 
 * @author r3n
 */
@RestControllerAdvice
@Slf4j
public class ApiResponseExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> handleException(Exception e) {
    log.error("[api-response]", e);

    ApiResponse result = ApiResponse.builder()
        .success(false)
        .message(e.getMessage())
        .build();

    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
