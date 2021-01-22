package com.portfolio.demo.project.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
public class CommentMovVO {
    private Long id;
    private Long writerId;
    private String writerName;
    private Long movieNo;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
    private int rating;

    public CommentMovVO(CommentMov entity) {
        this.id = entity.getId();
        this.writerId = entity.getWriter().getMemNo();
        this.writerName = entity.getWriter().getName();
        this.movieNo = entity.getMovieNo();
        this.content = entity.getContent();
        this.regDate = entity.getRegDate();
        this.rating = entity.getRating();
    }
}
