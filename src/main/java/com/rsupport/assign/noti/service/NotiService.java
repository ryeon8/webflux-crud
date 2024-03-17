package com.rsupport.assign.noti.service;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotiService {

  Flux<Noti> findList();

  Mono<Noti> findOne(String id);

  Mono<ApiResponse> insert(NotiDto input);

  Mono<ApiResponse> update(String id, NotiDto input);

  Mono<ApiResponse> delete(String id);

  Mono<Boolean> checkWriter(String name, String id);

}
