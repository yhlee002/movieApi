package com.portfolio.demo.project.dto.comment.request;

import lombok.Data;

@Data
public class CreateCommentMovRequest {
    private Long writerId;
    private Long movieNo;
    private String content;
    private Integer rating;
}
