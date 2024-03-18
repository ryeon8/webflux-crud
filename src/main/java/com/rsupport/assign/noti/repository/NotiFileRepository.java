package com.rsupport.assign.noti.repository;

import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.rsupport.assign.noti.entity.NotiFile;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NotiFileRepository extends ReactiveCrudRepository<NotiFile, Long> {

  Flux<NotiFile> findAllByNotiId(Long notiId);

  Mono<Void> deleteAllByNotiId(Long notiId);

  Mono<Void> deleteAllByNotiIdAndFileIdNotIn(Long notiId, List<String> fileIdList);

}
