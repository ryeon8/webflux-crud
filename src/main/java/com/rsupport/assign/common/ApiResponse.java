package com.rsupport.assign.common;

import lombok.Builder;
import lombok.Data;

/**
 * api 응답 결과 공통 bean.
 * 
 * @author r3n
 */
@Data
@Builder
public class ApiResponse {

  /** 요청 결과 */
  private boolean success;
  /** 추가 메세지(오류 내용 등) */
  private String message;
  /** 요청에 의해 신규로 발급된 PK 정보 등 */
  private Object id;
  /** 이 외 스펙을 지정할 수 없는 객체 전달. (TODO 이후 파싱해서 쓸 때를 생각하면 generic을 지원하는 게 나을지도) */
  private Object optional;

}
