package com.rsupport.assign.noti.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/noti")
public class NotiController {

  @GetMapping("/test")
  public Mono<String> test() {
    return Mono.just("hello, world");
  }

}
