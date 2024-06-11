package com.portfolio.demo.project.dto.board.request;

import lombok.Data;

@Data
public class UpdateBoardRequest {
    private Long id;
    private String title;
    private String content;
}
