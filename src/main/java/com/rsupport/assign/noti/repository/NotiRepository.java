package com.rsupport.assign.noti.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.rsupport.assign.noti.entity.Noti;

import reactor.core.publisher.Flux;

@Repository
public interface NotiRepository extends ReactiveCrudRepository<Noti, Long> {

  Flux<Noti> findAllBy(Pageable pageable);

}
