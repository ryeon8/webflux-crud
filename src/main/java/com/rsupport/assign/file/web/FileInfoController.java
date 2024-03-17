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

import com.rsupport.assign.common.ApiResponse;
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

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<ApiResponse> upload(Authentication auth, @RequestPart("files") Flux<FilePart> filePartFlux) {
    return service.upload(auth.getName(), filePartFlux);
  }

}
