package com.rsupport.assign.noti.service.impl;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.service.NotiValidator;

@Service
public class NotiValidatorImpl implements NotiValidator {

  private long fileUploadableCntLimit;
  private long singleFileSizeLimitMB;
  private long singleFileSizeLimitByte;

  public NotiValidatorImpl(
      @Value("${app.file.uploadable.cnt}") int fileUploadableCntLimit,
      @Value("${app.single.file.size.mb}") int singleFileZieLimitMB) {
    this.fileUploadableCntLimit = fileUploadableCntLimit;
    this.singleFileSizeLimitMB = singleFileZieLimitMB;
    this.singleFileSizeLimitByte = singleFileZieLimitMB * 1024 * 1024;
  }

  @Override
  public boolean isAuthor(Noti saved, String authorized) {
    return saved != null && StringUtils.equals(saved.getUserEmail(), authorized);
  }

  @Override
  public List<Pair<String, String>> validate(NotiDto input, Object... params) {
    List<Pair<String, String>> invalid = new ArrayList<>();

    // 공지 기간 검증.
    if (StringUtils.isNotBlank(input.getOpenDateTime()) && StringUtils.isNotBlank(input.getCloseDateTime())) {
      LocalDateTime openDateTime = LocalDateTime.parse(input.getOpenDateTime()); // 입력값 패턴 검증은 Validation에서 담당.
      LocalDateTime closeDateTime = LocalDateTime.parse(input.getCloseDateTime());
      if (openDateTime.isAfter(closeDateTime)) {
        invalid.add(Pair.of("openDateTime", "공지 시작 일시는 공지 종료 일시보다 클 수 없습니다. 공지 기간을 확인해 주세요."));
      }
    }

    // 첨부파일 검증.
    int savedFileCnt = input.getFileIds() == null ? 0 : input.getFileIds().size();
    int newFileCnt = input.getFiles() == null ? 0 : input.getFiles().size();
    if (savedFileCnt + newFileCnt > fileUploadableCntLimit) {
      invalid.add(Pair.of("files", MessageFormat.format("첨부파일은 최대 {0}개까지 첨부할 수 있습니다.", fileUploadableCntLimit)));
    }

    boolean oversizeFileUploaded = input.getFiles() != null &&
        input.getFiles().stream()
            .filter(file -> file.headers().getContentLength() > singleFileSizeLimitByte)
            .findAny().isPresent();
    if (oversizeFileUploaded) {
      invalid.add(Pair.of("files", MessageFormat.format("첨부파일은 건당 최대 {0}MB까지 첨부할 수 있습니다.", singleFileSizeLimitMB)));
    }

    return invalid;
  }

}
