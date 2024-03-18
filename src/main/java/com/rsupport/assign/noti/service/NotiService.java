package com.rsupport.assign.noti.service;

import org.springframework.data.domain.Pageable;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotiService {

  Flux<Noti> findList(Pageable pageable);

  Mono<Noti> findOne(Long id);

  Mono<ApiResponse> insert(String auth, NotiDto input);

  Mono<ApiResponse> update(Long id, String auth, NotiDto input);

  Mono<ApiResponse> delete(Long id, String auth);

}
