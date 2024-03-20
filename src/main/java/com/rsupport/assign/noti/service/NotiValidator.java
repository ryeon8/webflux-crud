package com.rsupport.assign.noti.service;

import com.rsupport.assign.common.InputValidator;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;

/**
 * 공지글 입력값 검증 service.
 * 
 * @author r3n
 */
public interface NotiValidator extends InputValidator<NotiDto> {

  /**
   * 공지글 작성자 여부 검증.
   * 
   * @param saved      영속화된 공지글 정보
   * @param authorized 공지글 작성자 비교 정보
   * @return true: saved 작성자, false: saved 작성자 아님
   */
  boolean isAuthor(Noti saved, String authorized);

}
