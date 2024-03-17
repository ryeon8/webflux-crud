package com.rsupport.assign.common;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtProvider {

  private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

  private String secretKey;
  private Key key;

  @Value("${app.jwt.secret}")
  private void setMeta(String value) {
    this.secretKey = Base64.encodeBase64String(value.getBytes());
    this.key = new SecretKeySpec(this.secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
  }

  @Getter
  @AllArgsConstructor
  private enum ClaimKey {
    EMAIL("email"),
    ROLE("admin"),;

    private String key;
  }

  public String generateToken(String email) {
    long now = System.currentTimeMillis();

    return Jwts.builder()
        .setSubject(String.format("%s", email))
        .setIssuer("rsupport-assign-noti-server")
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + EXPIRE_DURATION))
        .claim(ClaimKey.EMAIL.getKey(), email)
        .claim(ClaimKey.ROLE.getKey(), "admin") // TODO 이 부분은 email이 아닌 User 객체를 받아 꺼내 쓰는 형태로 구현해야 함.
        .signWith(key)
        .compact();
  }

  public String getToken(ServerHttpRequest request) {
    return request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
  }

  public String resolveToken(ServerHttpRequest request) {
    String bearerToken = getToken(request);

    if (!StringUtils.isBlank(bearerToken) && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }

    return null;
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(key).build()
          .parseClaimsJws(token);

      return true;
    } catch (ExpiredJwtException ex) {
      log.error("토큰 만료", ex.getMessage());
    } catch (IllegalArgumentException | MalformedJwtException ex) {
      log.error("잘못된 양식", ex);
    } catch (UnsupportedJwtException ex) {
      log.error("not supported", ex);
    } catch (SecurityException ex) {
      log.error("Signature validation failed", ex);
    } catch (Exception ex) {
      log.error("원인을 알 수 없음", ex);
    }

    return false;
  }

  public Authentication getAuthentication(String token) throws AuthenticationException {
    Claims claims = parseClaims(token);

    Collection<? extends GrantedAuthority> authorities = Arrays
        .stream(claims.get(ClaimKey.ROLE.getKey()).toString().split(","))
        .map(SimpleGrantedAuthority::new)
        .toList();

    User user = new User(ClaimKey.EMAIL.getKey(), "dummy-password-123", authorities);

    return new UsernamePasswordAuthenticationToken(user, "", authorities);
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder()
          .setSigningKey(key).build()
          .parseClaimsJws(token)
          .getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }

}