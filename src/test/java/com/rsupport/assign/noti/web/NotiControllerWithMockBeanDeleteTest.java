package com.rsupport.assign.noti.web;

// import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.common.JwtProvider;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.repository.NotiRepository;

import reactor.core.publisher.Mono;

@Nested
@SpringBootTest
@AutoConfigureWebTestClient(timeout = "10000000")
public class NotiControllerWithMockBeanDeleteTest {

  @MockBean
  private NotiRepository repo;

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private JwtProvider jwtProvider;

  private String testEmail = "junit@test.com";
  private String testJwtToken;

  @BeforeEach
  public void setUp() {
    this.testJwtToken = jwtProvider.generateToken(testEmail);
  }

  @Test
  public void should_fail_공지글_수정_인증_헤더_없음() {
    // given
    Noti saved = createMockNotiSaved(testEmail);
    Mockito.when(repo.findById(saved.getId())).thenReturn(Mono.just(saved));

    // when
    webTestClient.delete()
        .uri("/noti/" + saved.getId())
        .exchange()

        // then
        .expectStatus()
        .is4xxClientError() // 401 unauthorized
    ;
  }

  @Test
  public void should_success_공지글_삭제() {
    // given
    Noti saved = createMockNotiSaved(testEmail);
    Mockito.when(repo.findById(saved.getId())).thenReturn(Mono.just(saved));
    Mockito.when(repo.delete(saved)).thenReturn(Mono.empty());

    // when
    webTestClient.delete()
        .uri("/noti/" + saved.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .exchange()

        // then
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(ApiResponse.class)
        .value(ApiResponse::isSuccess, is(true))
    //
    ;
  }

  @Test
  public void should_failed_타인의_공지글_삭제() {
    // given
    Noti saved = createMockNotiSaved("notme@test.com");
    Mockito.when(repo.findById(saved.getId())).thenReturn(Mono.just(saved));

    // when
    webTestClient.delete()
        .uri("/noti/" + saved.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .exchange()

        // then
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(ApiResponse.class)
        .value(ApiResponse::isSuccess, is(false))
    //
    ;
  }

  @Test
  public void should_failed_존재하지_않는_공지글_삭제() {
    // given
    long dummyId = 1L;
    Mockito.when(repo.findById(dummyId)).thenReturn(Mono.empty());

    // when
    webTestClient.delete()
        .uri("/noti/" + dummyId)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .exchange()

        // then
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(ApiResponse.class)
        .value(ApiResponse::isSuccess, is(false))
    //
    ;
  }

  private Noti createMockNotiSaved(String userEmail) {
    return Noti.builder()
        .id(10L)
        .title("mock noti")
        .content("mock noti for junit test")
        .userEmail(userEmail)
        .build();
  }
}
