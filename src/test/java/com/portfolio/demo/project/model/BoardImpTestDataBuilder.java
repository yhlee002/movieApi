package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;

public class BoardImpTestDataBuilder {

    public static BoardImp.BoardImpBuilder board(Member writer) {
        return BoardImp.builder()
                .title("example title")
                .content("test board.")
                .writer(writer);
    }
}
