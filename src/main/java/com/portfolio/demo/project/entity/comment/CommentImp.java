package com.portfolio.demo.project.entity.comment;

import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

@Table(name = "comment_imp")
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class CommentImp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardImp board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

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
