package com.portfolio.demo.project.dto;

import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;

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

    public CommentImpParam(Long id, Long boardId, Long writerId, String writerName, String content, String regDate) {
        this.id = id;
        this.boardId = boardId;
        this.writerId = writerId;
        this.writerName = writerName;
        this.content = content;
        this.regDate = regDate;
    }

    public static CommentImpParam create(CommentImp imp) {
        return CommentImpParam.builder()
                .id(imp.getId())
                .boardId(imp.getBoard().getId())
                .writerId(imp.getWriter().getMemNo())
                .writerName(imp.getWriter().getName())
                .content(imp.getContent())
                .regDate(imp.getRegDate())
                .build();
    }
}
