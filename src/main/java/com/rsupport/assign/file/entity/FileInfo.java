package com.rsupport.assign.file.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileInfo {

  @Id
  private Long id;
  @Column
  private String fileId;
  @Column
  private String userEmail;
  @Column
  private String originName;
  @Column
  private LocalDateTime createDateTime;

}
