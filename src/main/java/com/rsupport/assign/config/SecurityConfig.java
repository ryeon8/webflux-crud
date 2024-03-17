package com.rsupport.assign.config;

// import static org.springframework.security.config.Customizer.withDefaults;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rsupport.assign.common.SecurityResponseContent;
import com.rsupport.assign.common.JwtProvider;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

  @Autowired
  private JwtProvider jwtProvider;

  private static String[] CAN_ACCESS_ANYBODY = { "/api/token" };

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

  private AuthenticationWebFilter authenticationWebFilter() {
    ReactiveAuthenticationManager authenticationManager = Mono::just;

    AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
    authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
    return authenticationWebFilter;
  }

  private ServerAuthenticationConverter serverAuthenticationConverter() {
    return exchange -> {
      String token = jwtProvider.resolveToken(exchange.getRequest());
      try {
        if (!Objects.isNull(token) && jwtProvider.validateToken(token)) {
          return Mono.justOrEmpty(jwtProvider.getAuthentication(token));
        }
      } catch (AuthenticationException e) {
        log.error(e.getMessage(), e);
      }
      return Mono.empty();
    };
  }

  private ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
    return (exchange, authEx) -> {
      ServerHttpResponse serverHttpResponse = exchange.getResponse();
      serverHttpResponse.getHeaders().setContentType(MediaType.APPLICATION_JSON);

      serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
      SecurityResponseContent errorMessage = SecurityResponseContent.builder()
          .status(HttpStatus.UNAUTHORIZED.value())
          .at(LocalDateTime.now())
          .message(authEx.getMessage())
          .requestPath(exchange.getRequest().getPath().value())
          .build();

      try {
        byte[] errorByte = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .writeValueAsBytes(errorMessage);
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(errorByte);
        return serverHttpResponse.writeWith(Mono.just(dataBuffer));
      } catch (JsonProcessingException e) {
        log.error(e.getMessage(), e);
        return serverHttpResponse.setComplete();
      }
    };
  }
}