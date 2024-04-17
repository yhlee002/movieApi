package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardNotice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NoticePagenationVO {
    private int totalPageCnt; // 총 페이지 개수
    private List<BoardNotice> boardNoticeList; // 한 화면에 보여줄 게시글 개수
}
