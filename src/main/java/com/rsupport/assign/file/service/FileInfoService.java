package com.rsupport.assign.file.service;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;

import com.rsupport.assign.file.entity.FileInfo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileInfoService {

  Mono<Resource> readSavedFile(String fileId);

  Mono<String> uploadSingle(String name, Mono<FilePart> filePartMono);

  Flux<FileInfo> uploadMultiple(String username, Flux<FilePart> filePartFlux);

}
