package com.portfolio.demo.project.dto.board;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.portfolio.demo.project.dto.comment.CommentMovParam;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImpressionPagenationParam {
    private int totalPageCnt; // 총 페이지 개수
    private int currentPage;
    private int size;
    private long totalElementCnt;
    private List<BoardImpParam> boardImpList; // 한 화면에 보여줄 게시글 개수

    public ImpressionPagenationParam(Page<BoardImp> page) {
        this.totalPageCnt = page.getTotalPages();
        this.currentPage = page.getPageable().getPageNumber();
        this.size = page.getPageable().getPageSize();
        this.totalElementCnt = page.getTotalElements();
        this.boardImpList = page.getContent().stream().map(BoardImpParam::create).collect(Collectors.toList());
    }
}
