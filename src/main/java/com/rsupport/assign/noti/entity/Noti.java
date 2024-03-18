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

@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Noti {

  @Id
  private Long id;
  @Column
  private String title;
  @Column
  private String content;
  @Column
  private LocalDateTime openDateTime;
  @Column
  private LocalDateTime closeDateTime;
  @Column
  @InsertOnlyProperty
  @JsonIgnore
  private String userEmail;
  @Column
  @ReadOnlyProperty
  private LocalDateTime createDateTime;

  @Transient
  private List<NotiFile> fileList;

}
