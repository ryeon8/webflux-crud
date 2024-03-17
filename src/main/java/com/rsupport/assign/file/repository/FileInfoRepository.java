package com.rsupport.assign.file.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.rsupport.assign.file.entity.FileInfo;

import reactor.core.publisher.Mono;

@Repository
public interface FileInfoRepository extends ReactiveCrudRepository<FileInfo, Long> {

  Mono<FileInfo> findByFileId(String originName);

}
