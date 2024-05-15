package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
public class BoardNoticeVO {
    private Long id;
    private String title;
    private String content;
    private MemberVO writer;
    private String regDate;
    private String modDate;
    private int views;

    public static BoardNoticeVO create(BoardNotice board) {
        return BoardNoticeVO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(MemberVO.create(board.getWriter()))
                .regDate(board.getRegDate())
                .modDate(board.getModDate())
                .views(board.getViews())
                .build();
    }
}
