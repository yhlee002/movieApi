CREATE TABLE recommended_board
(
    `id`       bigint AUTO_INCREMENT NOT NULL COMMENT '식별번호',
    `mem_no`   bigint                NOT NULL COMMENT '회원 식별번호',
    `board_id` bigint                NOT NULL COMMENT '게시글 식별번호',
    `reg_dt`   timestamp COMMENT '로그인 시도 시간',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='사용자의 게시글 추천 정보';