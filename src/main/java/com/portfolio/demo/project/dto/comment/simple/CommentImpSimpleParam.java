package com.portfolio.demo.project.dto.comment.simple;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentImpSimpleParam {

    private Long id;
    private Long boardId;
    private Long writerId;
    private String writerName;
    private String content;
    private LocalDateTime regDate;

    public CommentImpSimpleParam(Long id, Long boardId, Long writerId, String writerName, String content, LocalDateTime regDate) {
        this.id = id;
        this.boardId = boardId;
        this.writerId = writerId;
        this.writerName = writerName;
        this.content = content;
        this.regDate = regDate;
    }

}
