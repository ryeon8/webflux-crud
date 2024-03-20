package com.rsupport.assign.file.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

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
        .flatMap(saved -> {
          Path filePath = Paths.get(fileUploadDir, saved.getFileId());
          try {
            return Mono.just(new InputStreamResource(Files.newInputStream(filePath)));
          } catch (IOException e) {
            log.error("파일 조회 실패", e);
            return Mono.empty();
          }
        });
  }

  @Override
  public Mono<String> uploadSingle(String email, Mono<FilePart> filePartMono) {
    return filePartMono
        .flatMap(filePart -> {
          String newFileId = UUID.randomUUID().toString();

          filePart.transferTo(Paths.get(fileUploadDir, newFileId));

          FileInfo yetSaved = FileInfo.builder()
              .fileId(newFileId)
              .userEmail(email)
              .originName(filePart.filename())
              .build();
          repo.save(yetSaved);

          return Mono.just(newFileId);
        });
  }

  @Override
  public Flux<FileInfo> uploadMultiple(String email, Flux<FilePart> filePartFlux) {
    return filePartFlux
        .flatMap(filePart -> {
          String fileId = UUID.randomUUID().toString();
          FileInfo fileInfo = FileInfo.builder()
              .fileId(fileId).userEmail(email).originName(filePart.filename())
              .build();
          return serializeFile(filePart, fileId)
              .and(persistFile(fileInfo))
              .thenReturn(fileInfo);
        });
  }

  /**
   * 첨부파일을 저장 경로에 저장 처리.
   * 
   * @param filePart 첨부파일
   * @param filename 파일명
   */
  private Mono<Void> serializeFile(FilePart filePart, String filename) {
    return filePart.transferTo(Paths.get(fileUploadDir, filename));
  }

  /**
   * 첨부파일 메타 정보 영속화 처리.
   * 
   * @param yetSaved 첨부파일 정보
   */
  private Mono<FileInfo> persistFile(FileInfo yetSaved) {
    return repo.save(yetSaved);
  }

}
