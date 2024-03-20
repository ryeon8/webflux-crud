package com.rsupport.assign.noti.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.rsupport.assign.noti.entity.Noti;

import reactor.core.publisher.Flux;

/**
 * 공지글 관리 repository.
 * 
 * @author r3n
 */
@Repository
public interface NotiRepository extends ReactiveCrudRepository<Noti, Long> {

  /**
   * 공지글 목록 조회.
   * 
   * @param pageable 페이징 정보
   * @return 공지글 목록
   */
  Flux<Noti> findAllBy(Pageable pageable);

}
