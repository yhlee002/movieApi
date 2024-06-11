package com.portfolio.demo.project.dto.board.response;

import com.portfolio.demo.project.dto.board.BoardImpParam;
import lombok.Data;

@Data
public class MultiBoardImpResponse {
    private BoardImpParam board;
    private BoardImpParam prevBoard;
    private BoardImpParam nextBoard;
}
