package com.rsupport.assign.noti.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.codec.multipart.FilePart;

import com.rsupport.assign.noti.entity.Noti;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotiDto {

  private String title;
  private String content;
  private String openDateTime;
  private String closeDateTime;
  private String userEmail;
  private List<FilePart> files;

  public Noti toEntity(Long id) {
    return Noti.builder()
        .id(id)
        .title(title)
        .userEmail(userEmail)
        .openDateTime(StringUtils.isBlank(openDateTime) ? null : LocalDateTime.parse(openDateTime))
        .closeDateTime(StringUtils.isBlank(closeDateTime) ? null : LocalDateTime.parse(closeDateTime))
        .content(content)
        .build();
  }

}
