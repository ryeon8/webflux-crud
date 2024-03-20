package com.rsupport.assign.config;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.common.JwtProvider;
import com.rsupport.assign.common.JwtProvider.ClaimKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * spring-security 설정.
 * 
 * @author r3n
 * @see https://docs.spring.io/spring-security/reference/reactive/configuration/webflux.html
 * @see https://velog.io/@ddclub12/WebfluxSpring-SecurityJWT-Simple하게-구현하기
 */
@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

  @Autowired
  private JwtProvider jwtProvider;

  private static String[] CAN_ACCESS_ANYBODY = {
      "/api/token", "/noti/list", "/noti/detail/**", "/file/download/**", "/noti/test",
      "/actuator", "/actuator/prometheus", "/actuator/**"
  };

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.csrf(csrf -> csrf.disable())
        .cors(cors -> cors.disable())
        .formLogin(formLogin -> formLogin.disable())
        .anonymous(ano -> ano.disable())
        .authorizeExchange(exchanges -> exchanges.pathMatchers(CAN_ACCESS_ANYBODY).permitAll())
        .authorizeExchange(exchanges -> exchanges.anyExchange().authenticated())
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(serverAuthenticationEntryPoint()))
        .logout(logout -> logout.disable())
        .addFilterBefore(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
    //
    ;
    return http.build();
  }

  /** 인증 filter. */
  private AuthenticationWebFilter authenticationWebFilter() {
    ReactiveAuthenticationManager authenticationManager = Mono::just;

    AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
    authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
    return authenticationWebFilter;
  }

  /**
   * header의 bearer token으로부터 jwt token 추출.
   * 
   * @param bearerToken
   * @return jwt token
   */
  private String resolveToken(String bearerToken) {
    if (!StringUtils.isBlank(bearerToken) && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }

    return null;
  }

  /** 인증 정보(jwt token) resolver. */
  private ServerAuthenticationConverter serverAuthenticationConverter() {
    return exchange -> {
      String jwtToken = resolveToken(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
      try {
        if (StringUtils.isNotBlank(jwtToken)) {
          Claims claims = jwtProvider.parseClaims(jwtToken);
          return Mono.just(getAuthentication(claims));
        }
      } catch (AuthenticationException | JwtException e) {
        log.error(e.getMessage(), e);
      }

      return Mono.empty();
    };
  }

  /**
   * <pre>
   * 인증 객체 생성. 설계 상 인증 서버는 별도로 구축하고, 
   * 해당 서버로부터 받아온 jwt 토큰을 파싱해 쓰는 형태로 하거나,
   * 해당 서버로 jwt 토큰 유효성 검사를 수행하도록 해야 하므로 이 부분은 webClient가 적용되어야 하나
   * 인증 서버가 있다고 가정만 하고 개발을 진행했으므로 email 외에는 모두 dummy를 이용하도록 구현했다.
   * </pre>
   * 
   * @param claims jwt claims
   */
  private Authentication getAuthentication(Claims claims) throws AuthenticationException {
    // 별도 인증 서버가 있고, 그 서버에서 관리자 계정을 관리한다고 가정하므로 여기서는 jwtToken에서 email만 복호화해 .
    // 실제로는 webClient를 이용해 통신 후 jwtToken 유효성 검증 단계가 필요함.
    // 그러나 현재 로컬 환경에서
    // io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider 문제가 해결되지 않아
    // 시간 관계상 임의 설정을 유지하기로 함.
    Collection<? extends GrantedAuthority> authorities = Arrays
        .stream(claims.get(ClaimKey.ROLE.getKey()).toString().split(","))
        .map(SimpleGrantedAuthority::new)
        .toList();

    String dummyPassword = "dummy-password-123";
    User user = new User(claims.get(ClaimKey.EMAIL.getKey()).toString(), dummyPassword, authorities);

    return new UsernamePasswordAuthenticationToken(user, dummyPassword, authorities);
  }

  /** 인증 entry point. jwt를 이용하므로 인증을 시도하는 것 자체가 인가되지 않은 접근이므로 언제나 401을 반환한다. */
  private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
    return (exchange, authEx) -> {
      ServerHttpResponse serverHttpResponse = exchange.getResponse();
      serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
      ApiResponse unauthorized = ApiResponse.builder()
          .success(false)
          .optional(exchange.getRequest().getPath().value())
          .message(authEx.getMessage())
          .build();

      try {
        byte[] errorByte = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .writeValueAsBytes(unauthorized);
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte);
        return serverHttpResponse.writeWith(Mono.just(dataBuffer));
      } catch (JsonProcessingException e) {
        log.error(e.getMessage(), e);
        return serverHttpResponse.setComplete();
      }
    };
  }
}