ALTER TABLE board_imp MODIFY COLUMN `title` VARCHAR(64) NOT NULL COMMENT '게시글 제목';

ALTER TABLE board_notice MODIFY COLUMN `title` VARCHAR(64) NOT NULL COMMENT '게시글 제목';