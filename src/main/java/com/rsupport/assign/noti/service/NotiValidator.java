package com.rsupport.assign.noti.service;

import com.rsupport.assign.common.InputValidator;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;

public interface NotiValidator extends InputValidator<NotiDto> {

  boolean isAuthor(Noti saved, String authorized);

}
