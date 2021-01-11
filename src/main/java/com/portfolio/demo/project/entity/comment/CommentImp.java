package com.portfolio.demo.project.entity.comment;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "imp_comment")
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommentImp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(name = "board_no")
    private Long boardId;

    @Column(name = "writer_no") // , nullable = false
    private Long writerNo; // Member 테이블의 memNo(FK)

    @Column(name = "content") // , nullable = false
    private String content;

    @Column(name = "reg_dt", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    @Column(name = "recommend")
    private int recomment;
}
