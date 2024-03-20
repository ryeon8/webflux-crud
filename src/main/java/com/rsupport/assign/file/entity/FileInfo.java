package com.rsupport.assign.file.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 첨부파일 저장 정보 관리 entity.
 * 
 * @author r3n
 */
@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {

  /** PK */
  @Id
  private Long id;
  /** 첨부파일 ID(uuid) */
  @Column
  private String fileId;
  /** 등록자 정보 */
  @Column
  private String userEmail;
  /** 원본 파일명 */
  @Column
  private String originName;
  /** 등록일시 */
  @Column
  private LocalDateTime createDateTime;

}
