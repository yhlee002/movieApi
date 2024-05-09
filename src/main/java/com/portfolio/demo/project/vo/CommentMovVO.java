package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CommentMovVO {
    private Long id;
    private Long writerId;
    private String writerName;
    private Long movieNo;
    private String content;
    private String regDate;
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
