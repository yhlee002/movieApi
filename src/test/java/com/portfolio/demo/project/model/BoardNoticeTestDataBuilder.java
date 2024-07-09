package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.board.BoardNotice;

public class BoardNoticeTestDataBuilder {

    public static BoardNotice.BoardNoticeBuilder board() {
        return BoardNotice.builder()
                .title("example title")
                .content("test board.");
    }
}
