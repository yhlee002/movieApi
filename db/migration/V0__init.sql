CREATE TABLE IF NOT EXISTS member
(
    `mem_no`        bigint                        NOT NULL AUTO_INCREMENT,
    `identifier`    varchar(64)                   NOT NULL COMMENT '이메일 혹은 소셜 로그인 사용자의 고유 회원 번호',
    `provider`      enum ('none','naver','kakao') NOT NULL DEFAULT (_utf8mb4'none') COMMENT '기존 회원가입/소셜 로그인 api를 통한 회원가입 구분',
    `name`          varchar(20)                   NOT NULL COMMENT '닉네임',
    `pwd`           varchar(100)                           DEFAULT NULL COMMENT '비밀번호(소셜 api를 통해 회원가입한 경우 null)',
    `phone`         varchar(20)                            DEFAULT NULL COMMENT '연락처',
    `reg_dt`        timestamp                     NOT NULL COMMENT '회원가입일',
    `profile_image` varchar(100)                           DEFAULT NULL COMMENT '프로필 이미지',
    `role`          enum ('ROLE_USER','ROLE_ADMIN')        DEFAULT 'ROLE_USER' COMMENT '권한(일반회원과 관리자 구분)',
    `certification` char(1)                                DEFAULT '0' COMMENT '인증 여부(소셜 api를 이용하지 않고 회원가입할 경우 메일 인증 여부 저장)',
    PRIMARY KEY (`mem_no`),
    UNIQUE KEY (`identifier`),
    UNIQUE KEY (`name`),
    UNIQUE KEY (`phone`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='사용자 정보';

CREATE TABLE IF NOT EXISTS board_imp
(
    `id`          bigint      NOT NULL AUTO_INCREMENT COMMENT '게시글 식별번호',
    `writer_no`   bigint      NOT NULL COMMENT '작성자',
    `title`       varchar(20) NOT NULL COMMENT '게시글 제목',
    `content`     text        NOT NULL COMMENT '게시글 내용',
    `reg_dt`      timestamp COMMENT '후기 작성일자',
    `recommended` integer     NOT NULL DEFAULT '0' COMMENT '추천수',
    `views`       int                  DEFAULT '0' COMMENT '조회수',
    PRIMARY KEY (`id`)
#     FOREIGN KEY (`writer_no`) REFERENCES `member` (`mem_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='후기 감상 게시글';

CREATE TABLE IF NOT EXISTS board_notice
(
    `id`        bigint      NOT NULL AUTO_INCREMENT COMMENT '게시글 식별번호',
    `writer_no` bigint      NOT NULL COMMENT '작성자',
    `title`     varchar(20) NOT NULL COMMENT '게시글 제목',
    `content`   text        NOT NULL COMMENT '게시글 내용',
    `reg_dt`    timestamp   NOT NULL COMMENT '게시글 작성일자',
    `mod_dt`    timestamp DEFAULT NULL COMMENT '게시글 최종 수정일자',
    `views`     int       DEFAULT '0' COMMENT '조회수',
    PRIMARY KEY (`id`)
#     FOREIGN KEY (`writer_no`) REFERENCES `member` (`mem_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='공지 게시글';

CREATE TABLE IF NOT EXISTS comment_imp
(
    `id`        bigint NOT NULL AUTO_INCREMENT COMMENT '후기 댓글 식별번호',
    `board_id`  bigint NOT NULL COMMENT '후기 게시글 식별 번호',
    `writer_no` bigint NOT NULL COMMENT '작성자',
    `content`   varchar(300) DEFAULT NULL COMMENT '리뷰 내용',
    `reg_dt`    timestamp COMMENT '작성일자',
    PRIMARY KEY (`id`),
    KEY `board_id` (`board_id`),
    KEY `writer_no` (`writer_no`)
#     FOREIGN KEY (`board_id`) REFERENCES `board_imp` (`id`)
#     FOREIGN KEY (`writer_no`) REFERENCES `member` (`mem_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='후기 감상 게시글의 댓글';

CREATE TABLE IF NOT EXISTS comment_movie
(
    `id`        bigint NOT NULL AUTO_INCREMENT COMMENT '한줄 리뷰 식별번호',
    `writer_no` bigint NOT NULL COMMENT '작성자',
    `content`   varchar(50) DEFAULT NULL COMMENT '리뷰 내용',
    `reg_dt`    timestamp COMMENT '리뷰 작성일자',
    `movie_no`  bigint      DEFAULT NULL COMMENT '영화 번호(api 기준)',
    `rating`    int         DEFAULT '0' COMMENT '리뷰 평점',
    PRIMARY KEY (`id`)
#     KEY `writer_no` (`writer_no`),
#     FOREIGN KEY (`writer_no`) REFERENCES `member` (`mem_no`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='영화 리뷰 댓글';

CREATE TABLE IF NOT EXISTS persistent_logins
(
    `series`    varchar(64) NOT NULL,
    `username`  varchar(64) NOT NULL,
    `token`     varchar(64) NOT NULL,
    `last_used` timestamp   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='Remember me 기능을 위한 정보';

CREATE TABLE IF NOT EXISTS certification_data
(
    `id`                 bigint       NOT NULL AUTO_INCREMENT COMMENT '인증정보 식별번호',
    `certification_type` varchar(64)  NOT NULL COMMENT '인증 타입',
    `certification_id`   varchar(64)  NOT NULL COMMENT '인증 대상(휴대전화 번호 혹은 이메일)',
    `cert_key`           varchar(100) NOT NULL COMMENT '인증 번호',
    `reason`             varchar(20)           COMMENT '인증 이유',
    `expiration`         timestamp COMMENT '만료일시',
    `reg_dt`             timestamp    NOT NULL COMMENT '생성일시',
    PRIMARY KEY (`id`),
    UNIQUE (`certification_id`, `certification_type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='회원 인증 정보';