package com.rsupport.assign.noti.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공지글 entity.
 * 
 * @author r3n
 */
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Noti {
  /** PK */
  @Id
  private Long id;
  /** 제목 */
  @Column
  private String title;
  /** 내용 */
  @Column
  private String content;
  /** 공지 시작일시 */
  @Column
  private LocalDateTime openDateTime;
  /** 공지 종료일시 */
  @Column
  private LocalDateTime closeDateTime;
  /** 작성자 email */
  @Column
  @InsertOnlyProperty
  @JsonIgnore
  private String userEmail;
  /** 등록일시 */
  @Column
  @ReadOnlyProperty
  private LocalDateTime createDateTime;

  /** 파일 목록(r2dbc에서는 relation을 지원하지 않는 관계로 oneToMany 설정하지 않음) */
  @Transient
  private List<NotiFile> fileList;

}
