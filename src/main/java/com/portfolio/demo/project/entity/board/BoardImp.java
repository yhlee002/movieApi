package com.portfolio.demo.project.entity.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "board_imp")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardImp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content") // , nullable = false
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

    @Column(name = "views")
    private int views; // 조회수

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private List<CommentImp> comments = new ArrayList<>();

    @Builder
    public BoardImp(String title, String content, Member writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.views = 0;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateViewCount() {
        this.views++;
    }
}
