package com.rsupport.assign.noti.entity;

import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotiFile {

  private Integer notiId;
  private String fileUrl;

}
