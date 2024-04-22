package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.board.BoardImp;

public class BoardImpTestDataBuilder {

    public static BoardImp.BoardImpBuilder randomBoard() {
        return BoardImp.builder()
                .title("example title")
                .content("test board.");
    }
}
