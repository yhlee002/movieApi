package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommentMovPagenationVO {
    private int currentPageNo; // 현재 페이지
    private int totalPageCnt; // 총 페이지 개수
    private int startPage; // 시작페이지
    private int endPage; // 끝페이지
    private List<CommentMov> commentMovsList; // 한 화면에 보여줄 게시글 개수
    private Long totalCommentCnt; // 총 게시글 개수
    private int commentsPerPage; // 페이지당 출력할 글 개수 (15)
    private int lastPage; // (총 개수에 따른) 마지막 페이지(전체 페이지 개수)
    private int start, end; // 시작인덱스, 끝 인덱스

    public CommentMovPagenationVO() {
        this.currentPageNo = 1;
    }

    public CommentMovPagenationVO(Long totalCommentCnt, int currentPageNo, List<CommentMov> commentMovsList, int commentsPerPage, int start, int end) {
        this.totalCommentCnt = totalCommentCnt;
        this.currentPageNo = currentPageNo;
        this.commentMovsList = commentMovsList;
        this.commentsPerPage = commentsPerPage;
        this.start = start;
        this.end = end;
    }

    public int getStartPage() {
        return (currentPageNo - 1) * commentsPerPage;
    }

    public int getTotalPageCnt() {
        if (totalCommentCnt == 0) {
            totalPageCnt = 0;
        } else {
            totalPageCnt =  (int)(totalCommentCnt / commentsPerPage);
            if (totalCommentCnt % commentsPerPage > 0) {
                totalPageCnt++;
            }
        }
        return totalPageCnt;
    }
}