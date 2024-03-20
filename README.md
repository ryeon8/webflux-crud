# <rsupport 개발 과제> 공지사항 관리 REST API 구현 
## ENV
1. spring-webflux
2. postgreSQL
3. openjdk 17.0.10
4. Lombok
5. gradle
6. docker

## 실행 방법
```
gh repo clone ryeon8/webflux-crud
cd webflux-crud/docker/postgre
docker-compose up -d
cd ../..
gradle wrpper build bootRun
```

## 테스트 방법
- 단위/통합 테스트: gradle test 커맨드 수행
- API 실제 요청 테스트: vscode extension rest client 설치 후, simple-run-test.http 이용.

## 기능 요구 사항
- [x] 공지사항 등록, 수정, 삭제, 조회 API를 구현한다. 
- [x] 공지사항 등록 시 입력 항목: 제목, 내용, 공지 시작일시, 공지 종료일시, 첨부파일(여러 개) 
- [x] 공지사항 조회 시 응답: 제목, 내용, 등록일시, 조회수, 작성자 

## 개발 전 분석 및 전략
- 대용량 트래픽 > 서비스별로 서버를 분리, 메인 서비스 제공 서버인 공지글 API 서버는 webflux로 개발
- 공지글이라는 특성에서
  - 데이터가 많지 않을 것(pk, fk만 잡고 별도 index 도입하지 않음)
  - 읽기 요청이 압도적으로 많을 것
  - 한번 작성된 글은 삭제가 자주 발생하지 않을 것
  - 위 항목으로 가정, PostgreSQL 도입

## 개발 환경 제약
- 시스템 구성도 계획과 달리 서비스 분리 없이 하나의 application 및 서버에서 모든 것을 수행하도록 구현.

## 시스템 구성도
![시스템구성도.png](https://github.com/ryeon8/webflux-crud/assets/51218198/1ccb69b8-44fd-403b-b362-d6090e02e166)

## 서버별 서비스 URL
### 공지글 API 서버
- [x] 공지글 목록: GET /noti/list?page=&size=&order=
- [x] 공지글 상세 조회: GET /noti/{id}
- [x] 공지글 등록: POST /noti/create
- [x] 공지글 수정: PUT /noti/{id}
- [x] 공지글 삭제: DELETE /noti/{id}

### 파일 API 서버
- [x] 파일 업로드: POST /file/upload/multiple
- [x] 파일 다운로드: GET /file/download/{fileId}

### 인증 서버
- [x] jwt token 발행: /api/token?email=
- [ ] jwt token 유효성 검증
- [ ] 신규 계정 생성
- [ ] 계정 수정
- [ ] 계정 삭제

## 목록 URL Locust 부하 테스트 결과
[locust_report_10000_1000_3m_not_redis.pdf](https://github.com/ryeon8/webflux-crud/files/14665672/locust_report_10000_1000_3m_not_redis.pdf)

### 문제점 및 해결 전략
- 상위 5%의 응답이 유달리 늦어지는 현상이 있어 레디스 도입을 고려.
- 실제 레디스 도입 전 병목점 분석 시도
  - scouter로 병목점 분석을 시도했으나 webflux 지원이 되지 않는 것인지 모든 응답에 행이 걸리는 것으로 나타남.
  - 분석툴을 prometheus로 변경, spring actuator 설정 시도.
  - spring actuator는 설정되었으나 prometheus에서 이용할 locust metrics 취득에서 문제점 발생 -> 이 지점에서 시간 관계로 중지.

## 이후 과제
- 서버 이중화
- mvc와 성능 비교
- 병목점 분석
- 레디스 적용(병목점이 DB가 맞다면)
