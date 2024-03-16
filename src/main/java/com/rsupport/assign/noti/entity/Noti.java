package com.rsupport.assign.noti.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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
public class Noti {

  @Id
  private Integer id;

  @Column
  private String title;

  @Column
  private String description;

  @Column
  private LocalDateTime openDateTime;

  @Column
  private LocalDateTime closeDateTime;

  // private List<NotiFile> fileList; // TODO 이것도 flux여야 할까?

}
