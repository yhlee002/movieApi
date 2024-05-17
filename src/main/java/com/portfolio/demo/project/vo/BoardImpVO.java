package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Setter
@Getter
@ToString
public class BoardImpVO {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String writerName;
    private String regDate;
    private int views;
    private int recommended;
    private int commentSize;

    public static BoardImpVO create(BoardImp board) {
        return BoardImpVO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writerId(board.getWriter().getMemNo())
                .writerName(board.getWriter().getName())
                .regDate(board.getRegDate())
                .views(board.getViews())
                .recommended(board.getRecommended())
                .commentSize(board.getComments().size())
                .build();
    }
}
