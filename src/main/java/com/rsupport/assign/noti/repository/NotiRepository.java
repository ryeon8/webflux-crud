package com.rsupport.assign.noti.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.rsupport.assign.noti.entity.Noti;

@Repository
public interface NotiRepository extends ReactiveCrudRepository<Noti, Long> {

}
