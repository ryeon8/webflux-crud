package com.rsupport.assign.file.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.rsupport.assign.file.entity.FileInfo;
import com.rsupport.assign.file.service.FileInfoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <pre>
 * 파일 관리 controller.
 * 이 controller에서 제공하는 기능은 이후 별도 서버로 구축하거나
 * amazon s3 등으로 대체될 가능성을 염두에 두고 개발했다.
 * 또한 내부적으로 일차적인 검증을 거친 후에야 호출되는 서비스 레이어인 관계로
 * 첨부파일에 대한 별도 검증을 처리하지 않았다.
 * </pre>
 * 
 * @author r3n
 */
@RestController
@RequestMapping("/file")
public class FileInfoController {

  @Autowired
  private FileInfoService service;

  /**
   * 첨부파일 다운로드
   * 
   * @param fileId 첨부파일 PK
   */
  @GetMapping("/download/{fileId}")
  public Mono<ResponseEntity<Resource>> download(@PathVariable("fileId") String fileId) {
    return service.readSavedFile(fileId)
        .map(resource -> ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  /**
   * 단일 첨부파일 업로드 처리.
   * 
   * @param auth         인증 정보
   * @param filePartMono 단일 첨부파일
   * @return 발행된 첨부파일 ID
   */
  @PostMapping(value = "/upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<String> uploadSingle(Authentication auth, @RequestPart("file") Mono<FilePart> filePartMono) {
    return service.uploadSingle(auth.getName(), filePartMono);
  }

  /**
   * 여러 개의 첨부파일 일괄 업로드 처리.
   * 
   * @param auth         인증 정보
   * @param filePartFlux 여러 개의 첨부파일
   * @return 첨부파일 저장 정보
   */
  @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Flux<FileInfo> uploadMultiple(Authentication auth,
      @RequestPart("files") Flux<FilePart> filePartFlux) {
    return service.uploadMultiple(auth.getName(), filePartFlux);
  }

}
