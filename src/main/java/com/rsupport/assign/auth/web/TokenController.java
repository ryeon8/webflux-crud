package com.rsupport.assign.auth.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rsupport.assign.common.JwtProvider;

import reactor.core.publisher.Mono;

@RestController
public class TokenController {

  @Autowired
  private JwtProvider jwtProvider;

  @GetMapping("/api/token")
  public Mono<String> token(@RequestParam("email") String email) {
    return Mono.just(jwtProvider.generateToken(email));
  }
}
