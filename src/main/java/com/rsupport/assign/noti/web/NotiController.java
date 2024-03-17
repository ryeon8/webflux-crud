package com.rsupport.assign.noti.web;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.service.NotiService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/noti")
public class NotiController {

  @Autowired
  private NotiService service;

  @GetMapping("/test")
  public Mono<String> test() {
    return Mono.just("hello, world.");
  }

  @GetMapping("/list")
  public Flux<Noti> list() {
    return service.findList();
  }

  @GetMapping("/detail/{id}")
  public Mono<Noti> single(@PathVariable("id") String id) {
    return service.findOne(id);
  }

  @PostMapping("/create")
  public Mono<ApiResponse> create(Authentication auth, @ModelAttribute NotiDto input) {
    input.setUserEmail(auth.getName());

    return service.insert(input);
  }

  @PutMapping("/{id}")
  public Mono<ApiResponse> update(Authentication auth, @PathVariable("id") String id, @ModelAttribute NotiDto input) {
    return service.findOne(id)
        .flatMap(saved -> {
          boolean validUser = StringUtils.equals(saved.getUserEmail(), auth.getName());
          return validUser
              ? service.update(id, input)
              : Mono.just(ApiResponse.builder().success(false).message("수정 권한 없음").build());
        });
  }

  @DeleteMapping("/{id}")
  public Mono<ApiResponse> delete(Authentication auth, @PathVariable("id") String id) {
    return service.findOne(id)
        .flatMap(saved -> {
          boolean validUser = StringUtils.equals(saved.getUserEmail(), auth.getName());
          return validUser
              ? service.delete(id)
              : Mono.just(ApiResponse.builder().success(false).message("삭제 권한 없음").build());
        });
  }

}
