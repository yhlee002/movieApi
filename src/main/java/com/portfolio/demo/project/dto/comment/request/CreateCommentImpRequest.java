package com.portfolio.demo.project.dto.comment.request;

import lombok.Data;

@Data
public class CreateCommentImpRequest {
    private Long writerId;
    private Long boardId;
    private String content;
}
