package com.rsupport.assign.noti.repository;

import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.rsupport.assign.noti.entity.NotiFile;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 공지글 첨부파일 관리 repository.
 * 
 * @author r3n
 */
@Repository
public interface NotiFileRepository extends ReactiveCrudRepository<NotiFile, Long> {

  /**
   * 특정 공지글에 등록된 전체 파일 목록 조회.
   * 
   * @param notiId 공지글 PK
   * @return 공지글 첨부파일 목록
   */
  Flux<NotiFile> findAllByNotiId(Long notiId);

  /**
   * 특정 공지글에 등록된 파일 중 일부 삭제 처리.
   * 
   * @param notiId          공지글 PK
   * @param notInFileIdList 첨부파일 ID 목록
   */
  Mono<Void> deleteAllByNotiIdAndFileIdNotIn(Long notiId, List<String> notInFileIdList);

}
