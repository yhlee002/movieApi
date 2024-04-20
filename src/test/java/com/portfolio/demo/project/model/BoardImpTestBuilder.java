package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;

public class BoardImpTestBuilder {

    public static BoardImp.BoardImpBuilder randomBoard() {
        return BoardImp.builder()
            .id(1L)
            .title("dldudgus214@naver.com")
            .content("000-000-0000");
    }
}
