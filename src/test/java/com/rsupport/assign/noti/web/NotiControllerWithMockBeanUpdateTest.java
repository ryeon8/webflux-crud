package com.rsupport.assign.noti.web;

// import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.common.JwtProvider;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.service.NotiService;

import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "10000000")
@TestPropertySource(properties = { "management.endpoint.info.enabled=false",
    "management.endpoint.health.enabled=false" })
public class NotiControllerWithMockBeanUpdateTest {

  @MockBean
  private NotiService notiService;

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private JwtProvider jwtProvider;

  private final String testEmail = "junit@test.com";
  private String testJwtToken;

  @BeforeEach
  public void setUp() {
    this.testJwtToken = jwtProvider.generateToken(testEmail);
  }

  @Test
  public void should_fail_공지글_수정_인증_헤더_없음() {
    // given
    Noti saved = createMockNotiSaved();
    Mockito.when(notiService.findOne(any())).thenReturn(Mono.just(saved));

    // when
    webTestClient.put()
        .uri("/noti/" + saved.getId())
        .exchange()

        // then
        .expectStatus()
        .is4xxClientError() // 401 unauthorized
    ;
  }

  @Test
  public void should_failed_공지글_수정_필수값인_제목_입력_없음() {
    // given
    Noti saved = createMockNotiSaved();
    Mockito.when(notiService.findOne(any())).thenReturn(Mono.just(saved));

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("content", "description test");

    // when
    webTestClient.put()
        .uri("/noti/" + saved.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(formData)
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
  public void should_failed_공지글_수정_필수값인_내용_입력_없음() {
    // given
    Noti saved = createMockNotiSaved();
    Mockito.when(notiService.findOne(any())).thenReturn(Mono.just(saved));

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("title", "junit test");

    // when
    webTestClient.put()
        .uri("/noti/" + saved.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(formData)
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
  public void should_success_공지글_수정_필수값_입력() {
    // given
    Noti saved = createMockNotiSaved();
    Mockito.when(notiService.findOne(any())).thenReturn(Mono.just(saved));
    Mockito.when(notiService.update(any(), any(), any()))
        .thenReturn(Mono.just(ApiResponse.builder().success(true).build()));

    MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
    formData.add("title", "junit test");
    formData.add("content", "description test");

    // when
    webTestClient.put()
        .uri("/noti/" + saved.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(formData)
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
  public void should_success_공지글_등록_필수값과_첨부파일1개_입력() {
    // given
    Noti saved = createMockNotiSaved();
    Mockito.when(notiService.findOne(any())).thenReturn(Mono.just(saved));
    Mockito.when(notiService.update(any(), any(), any()))
        .thenReturn(Mono.just(ApiResponse.builder().success(true).build()));

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("title", "junit test");
    builder.part("content", "description test");
    builder.part("files", "junit file upload test".getBytes()).filename("test.txt");

    // when
    webTestClient.put()
        .uri("/noti/" + saved.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(builder.build())
        .exchange()

        // then
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(ApiResponse.class) //
        .value(ApiResponse::isSuccess, is(true)) //
    ;
  }

  @Test
  public void should_success_공지글_등록_필수값과_첨부파일2개_입력() {
    // given
    Noti saved = createMockNotiSaved();
    Mockito.when(notiService.findOne(any())).thenReturn(Mono.just(saved));
    Mockito.when(notiService.update(any(), any(), any()))
        .thenReturn(Mono.just(ApiResponse.builder().success(true).build()));

    MultipartBodyBuilder builder = new MultipartBodyBuilder();
    builder.part("title", "junit test");
    builder.part("content", "description test");
    builder.part("files", "junit file upload test1".getBytes()).filename("test1.txt");
    builder.part("files", "junit file upload test2".getBytes()).filename("test2.txt");

    // when
    webTestClient.put()
        .uri("/noti/" + saved.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testJwtToken)
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .bodyValue(builder.build())
        .exchange()

        // then
        .expectStatus()
        .is2xxSuccessful()
        .expectBody(ApiResponse.class) //
        .value(ApiResponse::isSuccess, is(true)) //
    ;
  }

  private Noti createMockNotiSaved() {
    return createMockNotiSaved(testEmail);
  }

  private Noti createMockNotiSaved(String userEmail) {
    return Noti.builder()
        .id(1L)
        .title("mock noti")
        .content("mock noti for junit test")
        .userEmail(userEmail)
        .build();
  }
}
