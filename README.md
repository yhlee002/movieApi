# Movie Site


## 개요
영화 정보, 영화에 대한 평점과 리뷰, 일상 등을 남기고 공유할 수 있는 사이트 개발(2020.10.21 ~ 2021.04.16)

### 리팩토링
#### 요약
* 기간: 2024.04 ~ 2024.06
* 스프링부트 버전 2.3.4 -> 3.2.3 마이그레이션
* API 데이터 반환시 엔티티를 직접 반환하던 코드를 ResponseEntity와 별도의 DTO를 이용해 정돈
* JPA 활용해 데이터 조회 기능 최적화 시도
* Git Actions, AWS S3, AWS CodeDeploy를 이용해 CI/CD 파이프라인 구축
#### 주요 변경사항
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


## 개발환경
* 언어/프레임워크 : Java 17/Spring Boot 3.2.3
* 데이터베이스 : MySQL 8.4 (Migration: Flyway)
* 컨테이너 : Docker compose를 이용한 MySQL, Flyway 구성
* 사용된 프레임워크 : Spring MVC, Spring Web, Spring Security, JPA(Hibernate)
* 사용된 외부 API
   - 네아로(네이버 아이디로 로그인) API, 카카오 로그인 API
   - 영화진흥위원회 박스오피스 API, 영화 상세 정보 API
   - TMDB 영화 API, KMDB 영화 API
   - CoolSMS message API


## 배포 환경
Amazon EC2 Instance(OS: Amazon Linux 2)


## 아키텍처
![image](https://github.com/yhlee002/movieApi/assets/60289743/e989b96f-6f13-45ff-916a-aafb58f6dfcd)


## DB Schema
![image](https://github.com/yhlee002/web_moviePublic/assets/60289743/d7dce3c6-f809-45f0-8d0d-65dc0dd9f2e1)


## 주요 기능
### 회원가입
      - 입력된 정보는 Ajax 비동기 통신을 통해 정규표현식(regular expression)을 사용한 유효성검사와 중복 여부 검사를 거쳐 저장.
      - 문자 API를 사용해 연락처 인증
      - 소셜 로그인 API를 사용한 회원가입
      - 아이디 및 비밀번호 찾기
      - 기존 방식의 회원가입시 등록한 이메일에 인증 링크를 포함한 메일을 Google SMTP를 통해 발송

### 로그인
      - 스프링시큐리티를 이용해 사용자 인증 및 권한 부여
      - OAuth2 인증을 통한 로그인 기능
         + 네아로(네이버 아이디로 로그인), 카카오 로그인과 같은 소셜 로그인 API를 사용한 로그인 가능
      - 세션에 저장된 로그인 정보를 이용해 추가적인 로그인 방지, 로그아웃시 세션 제거
      - 스프링 시큐리티의 Remember Me 기능과 Intercepter를 이용한 자동 로그인 기능

### 게시글과 댓글
      - 로그인 여부에 따라 게시판 접근에 차별을 둠 + 회원의 권한에 따라 글쓰기 및 수정 삭제 가능
      - 게시글 작성할 때 이미지파일 업로드시 서버에 해당 파일을 저장한 후 이 파일을 보여주는 방식
      - 게시글에 댓글 입력/수정/삭제 가능
      - 사용자가 탈퇴하여 회원정보 삭제 시 사용자가 입력한 게시글과 게시글의 댓글 삭제

### 주제별 검색
      - 사용자는 게시판에서 원하는 정보를 검색하여 정보를 받음
      - 사용자는 제목 또는 내용, 작성자 등을 직접 선택하여 검색

