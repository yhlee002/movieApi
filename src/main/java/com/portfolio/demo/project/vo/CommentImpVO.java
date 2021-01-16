package com.portfolio.demo.project.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class CommentImpVO {

    @Autowired
    CommentImp commentImp;

    private Long id;
    private Long boardId;
    private Long writerId;
    private String writerName;
    private String content;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    public CommentImpVO(CommentImp entity) {
        this.id = entity.getId();
        this.boardId = entity.getBoard().getId();
        this.writerId = entity.getWriter().getMemNo();
        this.writerName = entity.getWriter().getName();
        this.content = entity.getContent();
        this.regDate = entity.getRegDate();
    }
}
