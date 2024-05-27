CREATE TABLE certification_data(
    `id` integer not null auto_increment COMMENT '인증정보 식별번호',
    `certification_type` varchar(10) not null COMMENT '인증 타입(PHONE|EMAIL)',
    `certification_id` varchar(30) not null COMMENT '인증 대상(휴대전화 번호 혹은 이메일)',
    `expiration` timestamp default current_timestamp on update current_timestamp COMMENT '만료일시',
    primary key (`id`),
    unique (`certification_id`, `certification_type`)
);