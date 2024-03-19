package com.rsupport.assign.noti.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.file.service.FileInfoService;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;
import com.rsupport.assign.noti.entity.NotiFile;
import com.rsupport.assign.noti.repository.NotiFileRepository;
import com.rsupport.assign.noti.repository.NotiRepository;
import com.rsupport.assign.noti.service.NotiService;
import com.rsupport.assign.noti.service.NotiValidator;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Service
@Slf4j
public class NotiServiceImpl implements NotiService {

  private FileInfoService fileInfoService;
  private WebClient webClient;
  private NotiValidator validator;
  private NotiRepository notiRepo;
  private NotiFileRepository notiFileRepo;

  public NotiServiceImpl(
      FileInfoService fileInfoService,
      WebClient webClient,
      NotiValidator validator,
      NotiRepository notiRepo,
      NotiFileRepository notiFileRepo) {
    this.fileInfoService = fileInfoService;
    this.webClient = webClient;
    this.validator = validator;
    this.notiRepo = notiRepo;
    this.notiFileRepo = notiFileRepo;
  }

  @Override
  public Flux<Noti> findList(Pageable pageable) {
    return notiRepo.findAllBy(pageable);
  }

  @Override
  public Mono<Noti> findOne(Long id) {
    return notiRepo.findById(id)
        .flatMap(saved -> notiFileRepo.findAllByNotiId(id).collectList().map(notiFileList -> {
          saved.setFileList(notiFileList);
          return saved;
        }));
  }

  public Flux<String> saveFiles(NotiDto input) {
    List<FilePart> files = input.getFiles() == null ? Collections.emptyList() : input.getFiles();
    return fileInfoService.uploadMultiple(input.getUserEmail(), Flux.fromStream(files.stream()))
        .map(fileInfo -> fileInfo.getFileId());

    // TODO Unable to load
    // io.netty.resolver.dns.macos.MacOSDnsServerAddressStreamProvider
    // 계속 발생해서 우선 service를 주입 받아서 업로드하는 것으로 구현.
    // 이 부분은 별도 구축된 파일 서버와의 통신 로직(amazon s3 등)이 들어가야 함.
    // return Flux.fromIterable(input.getFiles())
    // .flatMap(file -> webClient.post()
    // .uri("http://127.0.0.1:8080/file/upload/single")
    // .header(HttpHeaders.AUTHORIZATION, "bearer " + jwtToken)
    // .contentType(MediaType.MULTIPART_FORM_DATA)
    // .body(fromMultipartData("file", file)) // TODO jwt token
    // .retrieve()
    // .bodyToMono(String.class));
  }

  @Override
  @Transactional
  public Mono<ApiResponse> insert(String auth, NotiDto input) {
    input.setUserEmail(auth);

    return notiRepo.save(input.toEntity())
        .flatMapMany(noti -> {
          return saveFiles(input)
              .flatMap(fileId -> {
                NotiFile notiFile = NotiFile.builder().notiId(noti.getId()).fileId(fileId).build();
                return notiFileRepo.save(notiFile);
              })
              .flatMap(success -> Mono.just(Tuples.of(noti.getId(), true)))
              .onErrorResume(error -> {
                log.error("파일 저장 실패", error);
                return Mono.just(Tuples.of(noti.getId(), false));
              }); // TODO 이 시점의 id는 뭘까?
        })
        .reduce(Tuples.of(0L, true), (r1, r2) -> Tuples.of(r2.getT1(), r1.getT2() && r2.getT2()))
        .flatMap(r -> Mono.just(ApiResponse.builder().success(r.getT2()).id(r.getT1()).build()));
  }

  @Override
  public Mono<ApiResponse> update(Long id, String auth, NotiDto input) {
    input.setUserEmail(auth);

    return notiRepo.findById(id)
        .filter(saved -> validator.isAuthor(saved, auth))
        .flatMap(saved -> notiRepo.save(input.toEntity(id)))
        .flatMap(noti -> {
          // 기존의 파일 중 삭제된 파일 제거.
          return notiFileRepo.deleteAllByNotiIdAndFileIdNotIn(id, input.getFileIds()).thenReturn(noti);
        })
        .flatMapMany(noti -> {
          // 신규 파일 등록.
          return saveFiles(input)
              .flatMap(fileId -> {
                NotiFile notiFile = NotiFile.builder().notiId(noti.getId()).fileId(fileId).build();
                return notiFileRepo.save(notiFile);
              })
              .map(success -> true)
              .onErrorResume(error -> {
                log.error("파일 저장 실패", error);
                return Mono.just(false);
              });
        })
        .collectList()
        .map(results -> results.stream().allMatch(Boolean::booleanValue) && results.size() > 0)
        .map(result -> ApiResponse.builder().success(result).message(result ? null : "수정 권한 없거나 공지글이 존재하지 않음").build()) //
    ;
  }

  @Override
  public Mono<ApiResponse> delete(Long id, String auth) {
    return notiRepo.findById(id)
        .filter(saved -> validator.isAuthor(saved, auth))
        .flatMap(saved -> notiRepo.delete(saved) // cascade delete이므로 noti_file도 함께 삭제됨.
            .then(Mono.just(ApiResponse.builder().success(true).build())) //
        )
        .switchIfEmpty(Mono.just(ApiResponse.builder().success(false).message("이미 삭제된 글이거나 권한 없음").build()));
  }

}
