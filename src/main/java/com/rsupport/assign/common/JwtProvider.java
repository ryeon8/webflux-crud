package com.rsupport.assign.common;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;

/**
 * jwt token 관리 service.
 * 
 * @author r3n
 */
@Component
public class JwtProvider {

  private static final long EXPIRE_DURATION = 24 * 60 * 60 * 1000; // 24 hour

  /** jwt token 암복호화 시크릿키. */
  private String secretKey;
  private Key key;

  @Value("${app.jwt.secret}")
  private void setMeta(String value) {
    this.secretKey = Base64.encodeBase64String(value.getBytes());
    this.key = new SecretKeySpec(this.secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
  }

  /**
   * jwt claims key 타입.
   * 
   * @author r3n
   */
  @Getter
  public static enum ClaimKey {
    EMAIL("email"),
    ROLE("admin"),;

    private String key;

    private ClaimKey(String key) {
      this.key = key;
    }
  }

  /**
   * jwt token 생성.
   * 
   * @param email 사용자 이메일
   * @return jwt token
   */
  public String generateToken(String email) {
    long now = System.currentTimeMillis();

    return Jwts.builder()
        .setSubject(String.format("%s", email))
        .setIssuer("rsupport-assign-noti-server")
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + EXPIRE_DURATION))
        .claim(ClaimKey.EMAIL.getKey(), email)
        // TODO 이 부분은 email이 아닌 User 객체를 받아 꺼내 쓰는 형태로 구현해야 함.
        // 현재는 사용자 정보 관리부가 없으므로 상수값 할당.
        .claim(ClaimKey.ROLE.getKey(), "admin")
        .signWith(key)
        .compact();
  }

  /**
   * jwt token 복호화.
   * 
   * @param token jwt token
   * @return claims
   * @throws JwtException
   */
  public Claims parseClaims(String token) throws JwtException {
    return Jwts.parserBuilder()
        .setSigningKey(key).build()
        .parseClaimsJws(token)
        .getBody();
  }

}