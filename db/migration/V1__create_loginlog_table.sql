CREATE TABLE IF NOT EXISTS login_log
(
    `id` bigint NOT NULL COMMENT '식별번호',
    `mem_no` bigint NOT NULL COMMENT '로그인 회원 식별번호',
    `reg_dt` timestamp COMMENT '로그인 시도 시간',
    `ip` varchar(64) NOT NULL COMMENT 'IP 주소',
    `result` varchar(10) NOT NULL COMMENT '성공 여부',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`mem_no`) REFERENCES `member` (`mem_no`)
) ENGINE = InnoDB
DEFAULT CHARSET = utf8mb4 COMMENT ='회원 로그인 이력';