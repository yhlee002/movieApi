package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class CommentImpVO {

    private Long id;
    private Long boardId;
    private String boardTitle;
    private Long writerId;
    private String writerName;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    public static CommentImpVO create(CommentImp imp) {
        return CommentImpVO.builder()
                .id(imp.getId())
                .boardId(imp.getBoard().getId())
                .boardTitle(imp.getBoard().getTitle())
                .writerId(imp.getWriter().getMemNo())
                .writerName(imp.getWriter().getName())
                .content(imp.getContent())
                .regDate(imp.getRegDate())
                .build();
    }
}
