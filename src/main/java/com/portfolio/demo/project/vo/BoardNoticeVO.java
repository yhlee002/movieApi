package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardNotice;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Setter
@Getter
@ToString
public class BoardNoticeVO {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String writerName;
    private String regDate;
    private String modDate;
    private int views;

    public static BoardNoticeVO create(BoardNotice board) {
        return BoardNoticeVO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writerId(board.getWriter().getMemNo())
                .writerName(board.getWriter().getName())
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .views(board.getViews())
                .build();
    }
}
