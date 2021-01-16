package com.portfolio.demo.project.entity.comment;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "comment_movie")
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommentMov {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

    @Column(name = "movie_no")
    private Long movieNo;

    @Column(name = "content")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "reg_dt", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    @Column(name = "recommended", insertable = false, columnDefinition = "LONG DEFAULT 0")
    private Long recommended;

    @Column(name = "rating")
    private int rating;

    @Builder
    public CommentMov(Long id, Member writer, String content, Long movieNo, Long recommended, int rating) {
        this.id = id;
        this.writer = writer;
        this.content = content;
        this.movieNo = movieNo;
        this.recommended = recommended;
        this.rating = rating;
    }
}
