package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@ToString
public class BoardImpVO {
    private Long id;
    private String title;
    private String content;
    private MemberVO writer;
    private String regDate;
    private int views;
    private int recommended;
    private List<CommentImp> comments;
    private int commentSize;

    public static BoardImpVO create(BoardImp board) {
        return BoardImpVO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(MemberVO.create(board.getWriter()))
                .regDate(board.getRegDate())
                .views(board.getViews())
                .recommended(board.getRecommended())
                .comments(board.getComments())
                .commentSize(board.getComments().size())
                .build();
    }

    public void updateCommentSize(int commentSize) {
        this.commentSize = commentSize;
    }
}
