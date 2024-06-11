package com.portfolio.demo.project.dto.board.response;

import com.portfolio.demo.project.dto.board.BoardNoticeParam;
import lombok.Data;

@Data
public class MultiBoardNoticeResponse {
    private BoardNoticeParam board;
    private BoardNoticeParam prevBoard;
    private BoardNoticeParam nextBoard;
}
