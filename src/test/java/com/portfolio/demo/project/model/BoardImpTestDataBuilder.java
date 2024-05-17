package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.vo.BoardImpVO;

public class BoardImpTestDataBuilder {

    public static BoardImp.BoardImpBuilder board() {
        return BoardImp.builder()
                .title("example title")
                .content("test board.");
    }
}
