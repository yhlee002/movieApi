package com.portfolio.demo.project.dto;

import com.portfolio.demo.project.entity.board.BoardNotice;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@ToString
public class BoardNoticeParam {
    private Long id;
    private String title;
    private String content;
    private Long writerId;
    private String writerName;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private int views;

    public static BoardNoticeParam create(BoardNotice board) {
        return BoardNoticeParam.builder()
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
