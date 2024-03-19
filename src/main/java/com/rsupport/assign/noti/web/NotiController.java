package com.rsupport.assign.noti.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.service.NotiService;
import com.rsupport.assign.noti.service.NotiValidator;

import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/noti")
public class NotiController {

  @Autowired
  private NotiService service;
  @Autowired
  private NotiValidator validator;

  @GetMapping("/test")
  public Mono<String> test() {
    return Mono.just("hello, world.");
  }

  @GetMapping("/list")
  public Flux<Noti> list1(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sort) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
    return service.findList(pageable);
  }

  @GetMapping("/detail/{id}")
  public Mono<Noti> single(@PathVariable("id") Long id) {
    return service.findOne(id)
        .defaultIfEmpty(Noti.builder().build()); // TODO 이 부분은 다르게 주는 게 나을까?
  }

  @PostMapping("/create")
  public Mono<ApiResponse> create(
      Authentication auth, @Valid @ModelAttribute NotiDto input, Errors errors) {
    // 입력값 검증.
    validator.validate(input, errors);
    if (errors.hasErrors()) {
      ApiResponse invalidInputResponse = ApiResponse.builder()
          .success(false).message(errors.getAllErrors().get(0).getDefaultMessage())
          .build();
      return Mono.just(invalidInputResponse);
    }

    // 공지글 등록 처리.
    return service.insert(auth.getName(), input);
  }

  @PutMapping("/{id}")
  public Mono<ApiResponse> update(
      Authentication auth, @PathVariable("id") Long id, @Valid @ModelAttribute NotiDto input, Errors errors) {
    // 입력값 검증.
    validator.validate(input, errors);
    if (errors.hasErrors()) {
      ApiResponse invalidInputResponse = ApiResponse.builder()
          .success(false).message(errors.getAllErrors().get(0).getDefaultMessage())
          .build();
      return Mono.just(invalidInputResponse);
    }

    // 공지글 수정 처리.
    return service.update(id, auth.getName(), input);
  }

  @DeleteMapping("/{id}")
  public Mono<ApiResponse> delete(Authentication auth, @PathVariable("id") Long id) {
    Mono<ApiResponse> r = service.delete(id, auth.getName()).cache();
    r.subscribe(System.out::println);
    return r;
  }

}
