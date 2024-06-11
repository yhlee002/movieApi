package com.portfolio.demo.project.dto.comment.request;

import lombok.Data;

@Data
public class UpdateCommentMovRequest {
    private Long writerId;
    private Long movieNo;
    private Long commentId;
    private String content;
    private Integer rating;
}
