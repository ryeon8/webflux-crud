package com.rsupport.assign.noti.web;

// import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.service.NotiService;

import reactor.core.publisher.Flux;

@SpringBootTest
@AutoConfigureWebTestClient
public class NotiControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockBean
  private NotiService notiService;

  @Test
  public void should_success_공지글_목록_조회() {
    // given
    Noti noti1 = Noti.builder().id(1L).title("mock").content("content").build();
    Mockito.when(notiService.findList()).thenReturn(Flux.just(noti1));

    // when
    webTestClient.get().uri("/noti/list")
        .exchange()

        // then
        .expectStatus().isOk()
        .expectBodyList(Noti.class)
        .contains(noti1);
  }

  @Test
  public void should_fail_공지글_등록_without_jwt_token() {
    // given

    // when
    webTestClient.get().uri("/noti/create")
        .exchange()

        // then
        .expectStatus().is4xxClientError();
  }

  // @Test
  // public void should_success_공지글_등록() {
  // webTestClient.mutateWith(mockJwt())
  // .get()
  // .uri("/noti/create")
  // .exchange()
  // .expectStatus()
  // .isForbidden();
  // }

}
