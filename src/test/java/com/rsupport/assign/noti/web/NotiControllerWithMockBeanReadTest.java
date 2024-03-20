package com.rsupport.assign.noti.web;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.rsupport.assign.common.JwtProvider;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.service.NotiService;

import reactor.core.publisher.Flux;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "10000000")
@TestPropertySource(properties = { "management.endpoint.info.enabled=false",
    "management.endpoint.health.enabled=false" })
public class NotiControllerWithMockBeanReadTest {

  @MockBean
  private NotiService notiService;

  @Autowired
  private WebTestClient webTestClient;
  @Autowired
  private JwtProvider jwtProvider;

  private String testJwtToken;

  @BeforeEach
  public void setUp() {
    this.testJwtToken = jwtProvider.generateToken("junit@test.com");
  }

  @Test
  public void should_success_공지글_목록_조회() {
    // given
    Noti noti1 = Noti.builder().id(1L).title("mock").content("content").build();
    Mockito.when(notiService.findList(any())).thenReturn(Flux.just(noti1));

    // when
    webTestClient.get().uri("/noti/list")
        .exchange()

        // then
        .expectStatus().isOk()
        .expectBodyList(Noti.class)
        .contains(noti1) //
    ;
  }

}
