package com.rsupport.assign.noti.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.repository.NotiRepository;
import com.rsupport.assign.noti.service.NotiService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NotiServiceImpl implements NotiService {

  private NotiRepository repo;

  public NotiServiceImpl(@Autowired NotiRepository repo) {
    this.repo = repo;
  }

  @Override
  public Flux<Noti> findList() {
    return repo.findAll();
  }

  @Override
  public Mono<Noti> findOne(String id) {
    return repo.findById(Long.parseLong(id));
  }

  @Override
  public Mono<ApiResponse> insert(NotiDto input) {
    Noti yetSaved = input.toEntity(null);

    return repo.save(yetSaved)
        .map(saved -> ApiResponse.builder().success(true).id(saved.getId()).build());
  }

  @Override
  public Mono<ApiResponse> update(String id, NotiDto input) {
    Noti yetSaved = input.toEntity(Long.parseLong(id));

    return repo.save(yetSaved)
        .map(saved -> ApiResponse.builder().success(true).build());
  }

  @Override
  public Mono<ApiResponse> delete(String id) {
    return findOne(id)
        .flatMap(exist -> {
          return repo.delete(exist);
        })
        .then(Mono.just(ApiResponse.builder().success(true).build()));
  }

  @Override
  public Mono<Boolean> checkWriter(String userEmail, String id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'checkWriter'");
  }

}
