package com.portfolio.demo.project.entity.board;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "board")
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId; // 글 번호
    @Column(name = "title", nullable = false)
    private String title; // 제목
    @Column(name = "content", nullable = false)
    private String content; // 내용
    @Column(name = "writer_no", nullable = false)
    private Long writerNo; // Member 테이블의 memNo(FK)
    @Column(name = "reg_dt")
    private LocalDateTime regDate; // 작성시간
    @Column(name = "mod_dt")
    private LocalDateTime modDate; // 최종 수정 시간
}
