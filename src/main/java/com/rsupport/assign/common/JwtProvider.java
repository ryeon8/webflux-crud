package com.rsupport.assign.common;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

@Component
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
  public static enum ClaimKey {
    EMAIL("email"),
    ROLE("admin"),;

    private String key;

    private ClaimKey(String key) {
      this.key = key;
    }
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

  public Claims parseClaims(String token) throws JwtException {
    return Jwts.parserBuilder()
        .setSigningKey(key).build()
        .parseClaimsJws(token)
        .getBody();
  }

}