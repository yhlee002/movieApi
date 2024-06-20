package com.portfolio.demo.project.dto.comment.count;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCount {
    private Long boardId;
    private Long count;

    public CommentCount(Long boardId, Long count) {
        this.boardId = boardId;
        this.count = count;
    }
}
