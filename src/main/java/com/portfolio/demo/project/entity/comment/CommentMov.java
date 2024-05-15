package com.portfolio.demo.project.entity.comment;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "comment_movie")
@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentMov extends BaseEntity {

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

    @Column(name = "rating")
    private int rating;

    @Builder
    public CommentMov(Member writer, String content, Long movieNo, int rating) {
        this.writer = writer;
        this.content = content;
        this.movieNo = movieNo;
        this.rating = rating;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }

    public void updateWriter(Member writer) {
        this.writer = writer;
    }
}
