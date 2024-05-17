package com.portfolio.demo.project.entity.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;

import jakarta.persistence.*;

@Table(name = "comment_imp")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentImp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardImp board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_mem_no")
    private Member writer;

    @Column(name = "content")
    private String content;

    public void updateBoard(BoardImp board) {
        this.board = board;
    }

    public void updateWriter(Member writer) {
        this.writer = writer;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
