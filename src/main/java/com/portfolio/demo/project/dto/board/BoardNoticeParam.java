package com.portfolio.demo.project.dto.board;

import com.portfolio.demo.project.entity.DeleteFlag;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.MemberRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

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
    private String writerProfileImage;
    private MemberRole writerRole;
    private String regDate;
    private String modDate;
    private int views;
    private DeleteFlag delYn;

    public static BoardNoticeParam create(BoardNotice board) {
        return BoardNoticeParam.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writerId(board.getWriter().getMemNo())
                .writerName(board.getWriter().getName())
                .writerProfileImage(board.getWriter().getProfileImage())
                .writerRole(board.getWriter().getRole())
                .regDate(board.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .modDate(board.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .views(board.getViews())
                .delYn(board.getDelYn())
                .build();
    }

    public static BoardNoticeParam createWithoutWriterAndRegDate(BoardNotice board) {
        return BoardNoticeParam.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .views(board.getViews())
                .delYn(board.getDelYn())
                .build();
    }
}
