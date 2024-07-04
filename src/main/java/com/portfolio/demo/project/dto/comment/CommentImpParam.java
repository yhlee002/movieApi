package com.portfolio.demo.project.dto.comment;

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

    public static CommentImpParam create(CommentImp imp) {
        return CommentImpParam.builder()
                .id(imp.getId())
//                .boardId(imp.getBoard() != null ? imp.getBoard().getId() : null)
//                .boardTitle(imp.getBoard() != null ? imp.getBoard().getTitle() : null)
                .writerId(imp.getWriter() != null ? imp.getWriter().getMemNo() : null)
                .writerName(imp.getWriter() != null ? imp.getWriter().getName() : null)
                .writerProfileImage(imp.getWriter() != null ? imp.getWriter().getProfileImage() : null)
                .writerRole(imp.getWriter() != null ? imp.getWriter().getRole() : null)
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
