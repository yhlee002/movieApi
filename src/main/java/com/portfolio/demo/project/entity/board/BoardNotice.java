package com.portfolio.demo.project.entity.board;

import com.portfolio.demo.project.entity.member.Member;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Table(name = "board_notice")
@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 글 번호

    @Column(name = "title", nullable = false)
    private String title; // 제목

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_no")
    private Member writer;

    @Column(name = "content") // , nullable = false
    private String content; // 내용

    @Column(name = "reg_dt", updatable = false)
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate; // 작성시간

    @Column(name = "mod_dt", insertable = false)
    @UpdateTimestamp
    private LocalDateTime modDate; // 최종 수정 시간

    @Column(name = "views")
    private int views; // 조회수

    @Builder
    public BoardNotice(Long id, String title, Member writer, String content, LocalDateTime regDate, LocalDateTime modDate) {
        this.id = id;
        this.title = title;
        this.writer = writer;
        this.content = content;
        this.regDate = regDate;
        this.modDate = modDate;
    }
}
