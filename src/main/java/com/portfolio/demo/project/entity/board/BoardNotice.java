package com.portfolio.demo.project.entity.board;

import com.portfolio.demo.project.entity.BaseEntity;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Table(name = "board_notice")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BoardNotice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 글 번호

    @Column(name = "title", nullable = false)
    private String title; // 제목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

    @Column(name = "content", nullable = false)
    private String content; // 내용

    @Column(name = "mod_dt")
    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modDate; // 최종 수정 시간

    @Column(name = "views")
    private int views = 0; // 조회수

    @Builder
    public BoardNotice(String title, Member writer, String content) {
        this.title = title;
        this.writer = writer;
        this.content = content;
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
