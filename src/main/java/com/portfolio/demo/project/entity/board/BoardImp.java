package com.portfolio.demo.project.entity.board;

import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.DeleteFlag;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Table(name = "board_imp")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class BoardImp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

    private int views;

    private int recommended;

    @Enumerated(EnumType.STRING)
    private DeleteFlag delYn;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "board")
    private List<CommentImp> comments = new ArrayList<>();

    public static BoardImp createWithDelYnIsN() {
        return new BoardImp();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateWriter(Member writer) {
        this.writer = writer;
    }

    public void updateViewCount(int views) {
        this.views = views;
    }

    public void updateRecommended(int recommended) {
        this.recommended = recommended;
    }
}
