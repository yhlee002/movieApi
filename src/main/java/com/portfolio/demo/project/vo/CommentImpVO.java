package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;

@Setter
@Getter
@ToString
@Builder
public class CommentImpVO {

    private Long id;
    private Long boardId;
    private Long writerId;
    private String writerName;
    private String content;
    private String regDate;

    public static CommentImpVO create(CommentImp imp) {
        return CommentImpVO.builder()
                .id(imp.getId())
                .boardId(imp.getBoard().getId())
                .writerId(imp.getWriter().getMemNo())
                .writerName(imp.getWriter().getName())
                .content(imp.getContent())
                .regDate(imp.getRegDate())
                .build();
    }
}
