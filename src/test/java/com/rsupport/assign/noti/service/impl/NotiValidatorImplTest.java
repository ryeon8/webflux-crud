package com.rsupport.assign.noti.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;

import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;

public class NotiValidatorImplTest {

  int fileUploadableCntLimit = 1;
  int singleFileSizeLimitMB = 1;
  private NotiValidatorImpl validator = new NotiValidatorImpl(fileUploadableCntLimit, singleFileSizeLimitMB);

  @Test
  public void should_success_작성자_본인() {
    // given
    String tryToEditAuth = "junit@test.com";
    Noti saved = Noti.builder()
        .userEmail(tryToEditAuth)
        .build();

    // when
    boolean isAuthor = validator.isAuthor(saved, tryToEditAuth);

    // then
    assertThat(isAuthor).isTrue();
  }

  @Test
  public void should_failed_작성자가_아님() {
    // given
    String tryToEditAuth = "junit@test.com";
    Noti saved = Noti.builder()
        .userEmail("not" + tryToEditAuth)
        .build();

    // when
    boolean isAuthor = validator.isAuthor(saved, tryToEditAuth);

    // then
    assertThat(isAuthor).isFalse();
  }

  @Test
  public void should_success_공지기간없음() {
    // given
    String[] targetFieldNames = { "openDateTime", "closeDateTime" };
    NotiDto input = NotiDto.builder()
        .openDateTime(null).closeDateTime(null).build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isFalse();
  }

  @Test
  public void should_success_공지시작일시없음() {
    // given
    String[] targetFieldNames = { "openDateTime", "closeDateTime" };
    NotiDto input = NotiDto.builder()
        .openDateTime(null).closeDateTime("2024-01-01T00:00:00").build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isFalse();
  }

  @Test
  public void should_success_공지종료일시없음() {
    // given
    String[] targetFieldNames = { "openDateTime", "closeDateTime" };
    NotiDto input = NotiDto.builder()
        .openDateTime("2024-01-01T00:00:00").closeDateTime(null).build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isFalse();
  }

  @Test
  public void should_success_공지기간있음_시작일시_equals_종료일시() {
    // given
    String[] targetFieldNames = { "openDateTime", "closeDateTime" };
    String dateTime = "2024-01-01T10:00:00";
    NotiDto input = NotiDto.builder()
        .openDateTime(dateTime).closeDateTime(dateTime).build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isFalse();
  }

  @Test
  public void should_failed_공지기간있음_시작일시가_종료일시보다_미래() {
    // given
    String[] targetFieldNames = { "openDateTime", "closeDateTime" };
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime openDateTime = LocalDateTime.now();
    NotiDto input = NotiDto.builder()
        .openDateTime(openDateTime.format(formatter))
        .closeDateTime(openDateTime.minusDays(1).format(formatter))
        .build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isTrue();
  }

  @Test
  public void should_success_공지기간있음_시작일시가_종료일시보다_과거() {
    // given
    String[] targetFieldNames = { "openDateTime", "closeDateTime" };
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime openDateTime = LocalDateTime.now();
    NotiDto input = NotiDto.builder()
        .openDateTime(openDateTime.format(formatter))
        .closeDateTime(openDateTime.plusDays(1).format(formatter))
        .build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isFalse();
  }

  @Test
  public void should_success_첨부파일_없음() {
    // given
    String targetFieldNames = "files";
    NotiDto input = NotiDto.builder().files(null).build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isFalse();
  }

  @Test
  public void should_failed_첨부파일_있음() {
    // given
    String targetFieldNames = "files";
    NotiDto input = NotiDto.builder()
        .files(mockFilePartList(fileUploadableCntLimit, singleFileSizeLimitMB))
        .build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isFalse();
  }

  @Test
  public void should_failed_첨부파일이_첨부_가능_개수_초과() {
    // given
    String targetFieldNames = "files";
    NotiDto input = NotiDto.builder()
        .files(mockFilePartList(fileUploadableCntLimit + 1, singleFileSizeLimitMB))
        .build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isTrue();
  }

  @Test
  public void should_failed_첨부파일_중_첨부_가능_사이즈_초과() {
    // given
    String targetFieldNames = "files";
    NotiDto input = NotiDto.builder()
        .files(mockFilePartList(fileUploadableCntLimit + 1, singleFileSizeLimitMB + 1))
        .build();

    // when
    boolean rejected = validate(input, targetFieldNames);

    // then
    assertThat(rejected).isTrue();
  }

  private List<FilePart> mockFilePartList(int size, int singleFileSizeMB) {
    HttpHeaders headersMock = mock(HttpHeaders.class);
    when(headersMock.getContentLength()).thenReturn(singleFileSizeMB * 1024 * 1024L);

    List<FilePart> filePartList = IntStream.range(0, size)
        .mapToObj(i -> {
          FilePart filePart = mock(FilePart.class);
          when(filePart.headers()).thenReturn(headersMock);
          return filePart;
        })
        .toList();

    return filePartList;
  }

  private boolean validate(NotiDto input, String... targetFieldNames) {
    return validator.validate(input).stream()
        .map(e -> e.getKey())
        .filter(key -> Arrays.asList(targetFieldNames).contains(key))
        .findAny().isPresent();
  }
}
