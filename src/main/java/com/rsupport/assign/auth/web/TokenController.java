package com.rsupport.assign.auth.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rsupport.assign.common.JwtProvider;

import reactor.core.publisher.Mono;

/**
 * <pre>
 * 인증 관련 controller.
 * 별도 서버로 구축할 것을 염두에 두고 작성되었음.
 * </pre>
 * 
 * @author r3n
 */
@RestController
public class TokenController {

  @Autowired
  private JwtProvider jwtProvider;

  /**
   * <pre>
   * jwt token 발행. 
   * 실제 인증 서버가 있다고 가정하고 개발했으므로 인증 관련해서는 단순히 email로 jwt 토큰을 반환하도록 구현함.
   * </pre>
   * 
   * @param email 사용자 정보
   * @return jwt token
   */
  @GetMapping("/api/token")
  public Mono<String> token(@RequestParam("email") String email) {
    return Mono.just(jwtProvider.generateToken(email));
  }
}
