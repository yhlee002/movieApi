package com.portfolio.demo.project.entity.comment;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "movie_no")
    private Long movieNo;

    @Column(name = "content")
    private String content;

    @Column(name = "reg_dt", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    @Column(name = "recommended")
    private Long recommended;

    @Column(name = "rating")
    private int rating;


    @Builder
    public CommentMov(Long commentId, Long writerNo, String content, Long movieNo, LocalDateTime regDate, int rating) {
        this.commentId = commentId;
        this.writerNo = writerNo;
        this.content = content;
        this.movieNo = movieNo;
        this.regDate = regDate;
        this.rating = rating;
    }
}
