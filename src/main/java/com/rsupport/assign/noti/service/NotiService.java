package com.rsupport.assign.noti.service;

import org.springframework.data.domain.Pageable;

import com.rsupport.assign.common.ApiResponse;
import com.rsupport.assign.noti.dto.NotiDto;
import com.rsupport.assign.noti.entity.Noti;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 공지글 관리 service.
 * 
 * @author r3n
 */
public interface NotiService {

  /**
   * 공지글 목록 조회. 반환하는 공지글 목록 각 요소는 첨부파일 정보를 가지고 있지 않음에 주의한다.
   * 
   * @param pageable 페이징 정보
   * @return 공지글 목록
   */
  Flux<Noti> findList(Pageable pageable);

  /**
   * 공지글 상세 조회. 첨부파일 ID 목록 정보가 포함된 상태로 반환된다.
   * 
   * @param id 공지글 PK
   * @return 공지글
   */
  Mono<Noti> findOne(Long id);

  /**
   * 공지글 신규 등록.
   * 
   * @param auth  작성자 정보
   * @param input 입력값
   * @return 처리 결과
   */
  Mono<ApiResponse> insert(String auth, NotiDto input);

  /**
   * 공지글 수정 처리.
   * 
   * @param id    공지글 PK
   * @param auth  작성자 정보(이 값이 id에 해당하는 공지글의 작성자 정보와 일치하지 않으면 수정 처리 실패)
   * @param input 입력값
   * @return 처리 결과
   */
  Mono<ApiResponse> update(Long id, String auth, NotiDto input);

  /**
   * <pre>
   * 공지글 삭제 처리.
   * 삭제 시 연관 공지글 첨부파일(NotiFile) 정보도 일괄 삭제되나,
   * 첨부파일 정보 자체는 삭제되지 않는다.
   * </pre>
   * 
   * @param id   공지글 PK
   * @param auth 작성자 정보
   * @return 처리 결과
   */
  Mono<ApiResponse> delete(Long id, String auth);

}
