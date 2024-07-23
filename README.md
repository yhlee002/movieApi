# Movie Site


## 개요
### 개요
영화 정보, 영화에 대한 평점과 리뷰, 일상 등을 남기고 공유할 수 있는 사이트 개발(2020.10.21 ~ 2021.04.16)

### 접속 경로
[MovieSite](http://3.38.19.101)


### 개발환경
```text
- 언어/프레임워크 : Java 17/Spring Boot 3.2.3
- 데이터베이스 : MySQL 8.4 (Migration: Flyway)
- 컨테이너 : Docker compose를 이용한 MySQL, Flyway 구성
- 주요 프레임워크 : Spring MVC, Spring Web, Spring Security, JPA(Hibernate), Spring OAuth2
- 사용된 외부 API
   + 네아로(네이버 아이디로 로그인) API, 카카오 로그인 API
   + 영화진흥위원회 박스오피스 API, 영화 상세 정보 API
   + TMDB 영화 API, KMDB 영화 API
   + CoolSMS message API
```

### 배포 환경
```text
- Amazon EC2 Instance(OS: Amazon Linux 2)
- RDS MySQL 8.4
```

## 아키텍처
<img width="1000" alt="DB Schema" src="https://github.com/user-attachments/assets/811a76ba-6b4b-4f9e-aaf7-32353af6be2a">


## DB Schema
<img width="1211" alt="DB Schema" src="https://github.com/user-attachments/assets/23ae693e-7750-4a34-8f96-8e1888270c4f">


## API 명세(Swagger)
[API 명세서](http://3.38.19.101:8080/api/swagger-ui.html)


## 리팩토링
### 요약
```
- 기간: 2024.04 ~ 2024.07
- 스프링부트 버전 2.3.4 -> 3.2.3 마이그레이션
- API 데이터 반환시 엔티티를 직접 반환하던 코드를 ResponseEntity와 별도의 DTO를 이용해 정돈
- JPA 활용해 데이터 조회 기능 최적화 시도
- EC2 + RDS 환경 배포
- Git Actions, AWS S3, AWS CodeDeploy를 이용해 CI/CD 파이프라인 구축
- Spring Security + OAuth2.0 기능 -> Spring OAuth2 사용 방식으로 변경
```

### 주요 변경사항
```text
- 2024.04
  + 스프링부트 버전 업그레이드(2.3.4 -> 3.2.3)
  + 스프링 시큐리티 버전 업그레이드로 WebSecurityConfig.java 리팩토링
  + Docker compose를 이용해 DB 연결 및 마이그레이션(flyway 적용)
  + 주요 Api, Service, Repository의 테스트 코드 작성
  + 영화 API 추가 적용
  + LocalDateTime타입의 시간 컬럼 필드들을 JPA Audit을 이용해 관리되도록 변경

- 2024.05
  + JPA를 이용해 데이터 조회해오는 기능 리팩토링(fetch join 적용, 일부 컬렉션 조회시 DTO로 조회)
  + API 반환 데이터 컨벤션 수정
  + DB 및 flyway 버전 업그레이드
  + 계층간 DTO 데이터 구조 리팩토링
    - 컨트롤러 <-> 서비스 간에 사용되는 DTO와 반환 데이터로 사용되는 DTO 구분

- 2024.06
  + 주요 API 작성 및 수정
  + Git Actions + AWS S3, AWS CodeDeploy를 이용해 CI/CD 파이프라인 구축
  + 구현된 OAuth2.0 소셜 로그인 기능을 Spring OAuth2을 사용하는 방식으로 마이그레이션

- 2024.07
  + Swagger를 이용한 API 명세서 생성 자동화
  + QueryDSL을 이용한 동적 쿼리 생성
```


## 주요 기능
- 공통 기능
   - 주요 데이터에 대한 CRUD
   - 다수의 검색 옵션을 선택한 게시글 검색(QueryDSL을 이용한 동적 쿼리 개발)
   - 리스트 페이지네이션(JPA의 Page, Pageable 클래스를 이용)
   - 옵션 선택에 따른 sorting
- 회원가입 & 로그인
   - Spring Security를 이용한 인증 & 인가
   - 기본 회원 가입 및 로그인
   - 소셜 로그인 API(Oauth2.0)를 이용한 회원가입 및 로그인
      - Spring OAuth2 프레임워크를 이용해 인증이 끝나면 API 서버로 리다이렉트해 회원 정보를 포함해 이동할 웹 페이지를 결정
   - 문자 API를 통해 연락처 인증, 이메일 API를 이용한 이메일 인증
   - 입력 값에 대한 axios를 이용한 async 통신을 통해 정규표현식에 기반한 유효성 검사 실시
- 영화 조회
   - TMDB, KMDB 등의 영화 API를 활용해 영화 검색, 박스오피스 영화 목록 조회
