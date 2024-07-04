package com.portfolio.demo.project.dto.comment;

import com.portfolio.demo.project.entity.DeleteFlag;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.MemberRole;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Setter
@Getter
@ToString
@Builder
public class CommentImpParam {

    private Long id;
    private Long boardId;
    private String boardTitle;
    private Long writerId;
    private String writerName;
    private String writerProfileImage;
    private MemberRole writerRole;
    private String content;
    private String regDate;
    private DeleteFlag delYn;

    public static CommentImpParam create(CommentImp imp) {
        return CommentImpParam.builder()
                .id(imp.getId())
                .boardId(imp.getBoard().getId())
                .boardTitle(imp.getBoard().getTitle())
                .writerId(imp.getWriter().getMemNo())
                .writerName(imp.getWriter().getName())
                .writerProfileImage(imp.getWriter().getProfileImage())
                .writerRole(imp.getWriter().getRole())
                .content(imp.getContent())
                .regDate(imp.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static CommentImpParam createWithoutBoardAndWriterAndRegDate(CommentImp imp) {
        return CommentImpParam.builder()
                .id(imp.getId())
                .content(imp.getContent())
                .build();
    }
}
