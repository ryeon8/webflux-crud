package com.rsupport.assign.noti.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.service.NotiValidator;

@Service
public class NotiValidatorImpl implements NotiValidator {

  @Override
  public boolean isAuthor(Noti saved, String authorized) {
    return saved != null && StringUtils.equals(saved.getUserEmail(), authorized);
  }

  @Override
  public List<Pair<String, String>> validate(NotiDto input, Object... params) {
    List<Pair<String, String>> invalid = new ArrayList<>();

    if (StringUtils.isNotBlank(input.getOpenDateTime()) && StringUtils.isNotBlank(input.getCloseDateTime())) {
      LocalDateTime openDateTime = LocalDateTime.parse(input.getOpenDateTime());
      LocalDateTime closeDateTime = LocalDateTime.parse(input.getCloseDateTime());
      if (openDateTime.isAfter(closeDateTime)) {
        invalid.add(Pair.of("openDateTime", "공지 시작 일시는 공지 종료 일시보다 클 수 없습니다. 공지 기간을 확인해 주세요."));
      }
    }

    return invalid;
  }

}
