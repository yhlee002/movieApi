package com.portfolio.demo.project.dto.board;

import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticePagenationParam {
    private int totalPageCnt; // 총 페이지 개수
    private int currentPage;
    private int size;
    private long totalElementCnt;
    private List<BoardNoticeParam> boardNoticeList; // 한 화면에 보여줄 게시글 개수

    public NoticePagenationParam(Page<BoardNotice> page) {
        this.totalPageCnt = page.getTotalPages();
        this.currentPage = page.getPageable().getPageNumber();
        this.size = page.getPageable().getPageSize();
        this.totalElementCnt = page.getTotalElements();
        this.boardNoticeList = page.getContent().stream().map(BoardNoticeParam::create).collect(Collectors.toList());
    }
}
