package com.portfolio.demo.project.entity.comment;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

@Table(name = "movie_comment")
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommentMov {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "writer_no")
    private Long writerNo;

    @Column(name = "name", insertable = false, updatable = false)
    private String writer;

    @Column(name = "content")
    private String content;

    @Column(name = "reg_dt", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String regDate;

    @Column(name = "recommended")
    private Long recommended;

    @Column(name = "movie_no")
    private Long movieNo;

    @Builder
    public CommentMov(Long commentId, Long writerNo, String content, Long movieNo) {
        this.commentId = commentId;
        this.writerNo = writerNo;
        this.content = content;
        this.movieNo = movieNo;

    }
}
