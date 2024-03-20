package com.rsupport.assign.noti.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.codec.multipart.FilePart;

import com.rsupport.assign.noti.entity.Noti;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공지글 입력값 관리 bean.
 * 
 * @author r3n
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotiDto {

  /** 제목 */
  @NotBlank
  @Size(max = 50)
  private String title;
  /** 내용 */
  @NotBlank
  private String content;
  /** 공지 시작일시(yyyy-MM-dd'T'HH:mm:ss */
  @DateTimeFormat
  private String openDateTime;
  /** 공지 종료일시(yyyy-MM-dd'T'HH:mm:ss */
  @DateTimeFormat
  private String closeDateTime;
  /** 작성자 이메일(이 값은 통신 시 전달된 jwt token으로부터 얻음) */
  private String userEmail;
  /** 첨부파일 */
  private List<FilePart> files;
  /** 저장된 첨부파일 ID 목록(이 값은 연동하는 서비스에 따라 파일 id가 될 수도, url이 될 수도 있음) */
  private List<String> fileIds;

  /** 사용자 입력값을 영속 가능한 entity 형태로 변환해 반환. */
  public Noti toEntity() {
    return toEntity(null);
  }

  /**
   * 사용자 입력값을 영속 가능한 entity 형태로 변환해 반환.
   * 
   * @param id 공지글 ID
   * @return 공지글 entity
   */
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
