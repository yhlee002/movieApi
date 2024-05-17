package com.portfolio.demo.project.dto;

import com.portfolio.demo.project.entity.board.BoardImp;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class BoardImpParam {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String writerName;
    private String regDate;
    private int views;
    private int recommended;
    private int commentSize;

    public static BoardImpParam create(BoardImp board) {
        return BoardImpParam.builder()
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
