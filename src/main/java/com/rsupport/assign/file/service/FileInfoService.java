package com.rsupport.assign.file.service;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;

import com.rsupport.assign.file.entity.FileInfo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 첨부파일 관리 service.
 * 
 * @author r3n
 */
public interface FileInfoService {

  /**
   * 첨부파일 조회.
   * 
   * @param fileId 첨부파일 id
   * @return 첨부파일 원본
   */
  Mono<Resource> readSavedFile(String fileId);

  /**
   * 단일 첨부파일 저장 처리.
   * 
   * @param userEmail    작성자 정보
   * @param filePartMono 첨부파일
   * @return 첨부파일 ID
   */
  Mono<String> uploadSingle(String userEmail, Mono<FilePart> filePartMono);

  /**
   * 여러 개의 첨부파일 저장 처리.
   * 
   * @param userEmail    작성자 정보
   * @param filePartFlux 첨부파일 목록
   * @return 첨부파일 저장 정보
   */
  Flux<FileInfo> uploadMultiple(String userEmail, Flux<FilePart> filePartFlux);

}
