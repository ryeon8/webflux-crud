package com.rsupport.assign.file.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.file.entity.FileInfo;
import com.rsupport.assign.file.repository.FileInfoRepository;
import com.rsupport.assign.file.service.FileInfoService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {

  private String fileUploadDir;
  private FileInfoRepository repo;

  @Value("${app.file.upload.dir}")
  public void setFileUploadDir(String value) {
    this.fileUploadDir = value;
  }

  private FileInfoServiceImpl(@Autowired FileInfoRepository repo) {
    this.repo = repo;
  }

  @Override
  public Mono<Resource> readSavedFile(String fileId) {
    return repo.findByFileId(fileId)
        .map(saved -> {
          Path filePath = Paths.get(fileUploadDir, saved.getFileId());
          try {
            return new InputStreamResource(Files.newInputStream(filePath));
          } catch (IOException e) {
            log.error("파일 조회 실패", e);
            return null;
          }
        });
  }

  @Override
  public Mono<ApiResponse> upload(String email, Flux<FilePart> filePartFlux) {
    Map<String, String> saveResult = new HashMap<>();

    return filePartFlux
        .flatMap(filePart -> {
          String newFileId = UUID.randomUUID().toString();
          saveResult.put(filePart.filename(), newFileId);

          Mono<Void> saveFile = filePart.transferTo(Paths.get(fileUploadDir, newFileId));

          FileInfo yetSaved = FileInfo.builder()
              .fileId(newFileId)
              .userEmail(email)
              .originName(filePart.filename())
              .build();
          Mono<FileInfo> persist = repo.save(yetSaved);

          return Mono.when(saveFile, persist);
        }).then(Mono.just(ApiResponse.builder().optional(saveResult).build()));
  }

}
