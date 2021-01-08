package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardImp;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ImpressionPagenationVO {
    private int currentPageNo; // 현재 페이지
    private int totalPageCnt; // 총 페이지 개수
    private int startPage; // 시작페이지
    private int endPage; // 끝페이지
    private List<BoardImp> boardImpList; // 한 화면에 보여줄 게시글 개수
    private int totalBoardCnt; // 총 게시글 개수
    private int boardsPerPage; // 페이지당 출력할 글 개수 (15)
    private int lastPage; // (총 개수에 따른) 마지막 페이지(전체 페이지 개수)
    private int start, end; // 시작인덱스, 끝 인덱스

    public ImpressionPagenationVO() {
        this.currentPageNo = 1;
    }

    public ImpressionPagenationVO(int totalBoardCnt, int currentPageNo, List<BoardImp> boardImpList, int boardsPerPage, int start, int end) {
        this.totalBoardCnt = totalBoardCnt;
        this.currentPageNo = currentPageNo;
        this.boardImpList = boardImpList;
        this.boardsPerPage = boardsPerPage;
        this.start = start;
        this.end = end;


    }

    public int getStartPage() {
        return (currentPageNo - 1) * boardsPerPage;
    }

    public int getTotalPageCnt() {
        if (totalBoardCnt == 0) {
            totalPageCnt = 0;
        } else {
            totalPageCnt = totalBoardCnt / boardsPerPage;
            if (totalBoardCnt % boardsPerPage > 0) {
                totalPageCnt++;
            }
        }
        return totalPageCnt;
    }
}
