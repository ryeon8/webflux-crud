package com.rsupport.assign.file.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
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

@RestController
@RequestMapping("/file")
public class FileInfoController {

  @Autowired
  private FileInfoService service;

  @GetMapping("/download/{fileId}")
  public ResponseEntity<Mono<Resource>> download(@PathVariable("fileId") String fileId) {
    Mono<Resource> saved = service.readSavedFile(fileId);
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(saved);
  }

  @PostMapping(value = "/upload/single", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<String> uploadSingle(Authentication auth, @RequestPart("file") Mono<FilePart> filePartMono) {
    return service.uploadSingle(auth.getName(), filePartMono);
  }

  // @PostMapping(value = "/upload/multiple", consumes =
  // MediaType.MULTIPART_FORM_DATA_VALUE)
  // public Mono<ApiResponse> uploadMultiple(Authentication auth,
  // @RequestPart("files") Flux<FilePart> filePartFlux) {
  // return service.uploadMultiple(auth.getName(), filePartFlux);
  // }

  @PostMapping(value = "/upload/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Flux<FileInfo> uploadMultiple2(Authentication auth,
      @RequestPart("files") Flux<FilePart> filePartFlux) {
    return service.uploadMultiple(auth.getName(), filePartFlux);
  }

}
