package com.portfolio.demo.project.entity.board;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "board_notice")
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class BoardNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId; // 글 번호

    @Column(name = "title", nullable = false)
    private String title; // 제목

    @Column(name = "writer_no", nullable = false) // , nullable = false
    private Long writerNo; // Member 테이블의 memNo(FK)

    @Column(name = "name")
    private String writer;

    @Column(name = "content", nullable = false) // , nullable = false
    private String content; // 내용

    @Column(name = "reg_dt", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate; // 작성시간

    @Column(name = "mod_dt", columnDefinition = "TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime modDate; // 최종 수정 시간

    @Column(name = "views")
    private int views; // 조회수

    @Builder
    public BoardNotice(Long boardId, String title, Long writerNo, String content, LocalDateTime regDate) {
        this.boardId = boardId;
        this.title = title;
        this.writerNo = writerNo;
        this.content = content;
        this.regDate = regDate;
    }
}
