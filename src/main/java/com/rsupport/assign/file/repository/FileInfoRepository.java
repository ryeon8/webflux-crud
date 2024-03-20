package com.rsupport.assign.file.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.rsupport.assign.file.entity.FileInfo;

import reactor.core.publisher.Mono;

/**
 * 첨부파일 저장 정보 관리 repository
 * 
 * @author r3n
 */
@Repository
public interface FileInfoRepository extends ReactiveCrudRepository<FileInfo, Long> {

  /**
   * 첨부파일 저장 정보 조회.
   * 
   * @param fileId 첨부파일 ID
   * @return 첨부파일 저장 정보
   */
  Mono<FileInfo> findByFileId(String fileId);

}
