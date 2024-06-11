package com.portfolio.demo.project.dto.board.request;

import lombok.Data;

@Data
public class CreateBoardRequest {
    private String title;
    private String content;
    private Long writerId;
}
