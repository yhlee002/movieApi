package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardImp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ImpressionPagenationVO {
    private int totalPageCnt; // 총 페이지 개수
    private List<BoardImp> boardImpList; // 한 화면에 보여줄 게시글 개수
}
