package com.portfolio.demo.project.dto.comment;

import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;

@Builder
@Setter
@Getter
@ToString
public class CommentMovParam {
    private Long id;
    private Long writerId;
    private String writerName;
    private Long movieNo;
    private String content;
    private String regDate;
    private int rating;

    public static CommentMovParam create(CommentMov mov) {
        return CommentMovParam.builder()
                .id(mov.getId())
                .writerId(mov.getWriter().getMemNo())
                .writerName(mov.getWriter().getName())
                .movieNo(mov.getMovieNo())
                .content(mov.getContent())
                .regDate(mov.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .rating(mov.getRating())
                .build();
    }

    public static CommentMovParam createWithoutWriterAndRegDate(CommentMov mov) {
        return CommentMovParam.builder()
                .id(mov.getId())
                .movieNo(mov.getMovieNo())
                .content(mov.getContent())
                .rating(mov.getRating())
                .build();
    }
}
