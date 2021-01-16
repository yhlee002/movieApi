package com.portfolio.demo.project.entity.comment;

import com.portfolio.demo.project.entity.member.Member;
import lombok.*;

import javax.persistence.*;

@Table(name = "recommend_comment_movie")
@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentMovRecommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentMov comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

    @Builder
    public CommentMovRecommend(Long id, CommentMov comment, Member writer) {
        this.id = id;
        this.comment = comment;
        this.writer = writer;
    }
}


