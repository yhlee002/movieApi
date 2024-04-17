package com.portfolio.demo.project.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Builder
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

    public static CommentMovVO create(CommentMov mov) {
        return CommentMovVO.builder()
                .id(mov.getId())
                .writerId(mov.getWriter().getMemNo())
                .writerName(mov.getWriter().getName())
                .movieNo(mov.getMovieNo())
                .content(mov.getContent())
                .regDate(mov.getRegDate())
                .rating(mov.getRating())
                .build();
    }
}
