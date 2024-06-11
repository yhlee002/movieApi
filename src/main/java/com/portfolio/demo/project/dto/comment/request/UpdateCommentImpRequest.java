package com.portfolio.demo.project.dto.comment.request;

import lombok.Data;

@Data
public class UpdateCommentImpRequest {
    private Long writerId;
    private Long boardId;
    private Long commentId;
    private String content;
}
