package com.portfolio.demo.project.dto.comment;

import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Setter
@Getter
@ToString
@Builder
public class CommentImpParam {

    private Long id;
    private Long boardId;
    private Long writerId;
    private String writerName;
    private String content;
    private String regDate;

    public static CommentImpParam create(CommentImp imp) {
        return CommentImpParam.builder()
                .id(imp.getId())
                .boardId(imp.getBoard().getId())
                .writerId(imp.getWriter().getMemNo())
                .writerName(imp.getWriter().getName())
                .content(imp.getContent())
                .regDate(imp.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static CommentImpParam createWithoutBoarAndWriterAndRegDate(CommentImp imp) {
        return CommentImpParam.builder()
                .id(imp.getId())
                .content(imp.getContent())
                .build();
    }
}
