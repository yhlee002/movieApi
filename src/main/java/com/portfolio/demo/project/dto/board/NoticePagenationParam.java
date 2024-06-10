package com.portfolio.demo.project.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NoticePagenationParam {
    private int totalPageCnt; // 총 페이지 개수
    private List<BoardNoticeParam> boardNoticeList; // 한 화면에 보여줄 게시글 개수
}
