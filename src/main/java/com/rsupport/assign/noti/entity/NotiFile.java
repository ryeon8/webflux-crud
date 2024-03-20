package com.rsupport.assign.noti.entity;

import org.springframework.data.relational.core.mapping.Table;

import com.rsupport.assign.file.entity.FileInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 공지글 첨부파일 entity.
 * 
 * @author r3n
 * @see FileInfo
 */
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotiFile {

  /** 공지글 PK */
  private Long notiId;
  /** 첨부파일 PK {@link FileInfo#getrFileId()} */
  private String fileId;

}
