package com.portfolio.demo.project.dto.comment.request;

import lombok.Data;

@Data
public class UpdateCommentImpRequest {
    private Long id;
    private String content;
}
