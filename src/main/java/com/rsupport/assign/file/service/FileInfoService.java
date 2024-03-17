package com.rsupport.assign.file.service;

import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;

import com.rsupport.assign.common.ApiResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FileInfoService {

  Mono<Resource> readSavedFile(String fileId);

  Mono<ApiResponse> upload(String username, Flux<FilePart> filePartFlux);

}
