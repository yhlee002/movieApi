package com.portfolio.demo.project.entity.board;

import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table(name = "board_imp")
@Entity
@Setter
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardImp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content") // , nullable = false
    private String content;

    @Column(name = "writer_no") // , nullable = false
    private Long writerNo; // Member 테이블의 memNo(FK)

    @Column(name = "name", insertable = false, updatable = false)
    private String writer;

    @Column(name = "reg_dt", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;

    @Column(name = "views")
    private int views; // 조회수

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "board_no")
    private List<CommentImp> comments;

    @Builder
    public BoardImp(Long boardId, String title, String content, Long writerNo, LocalDateTime regDate) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.writerNo = writerNo;
        this.regDate = regDate;
    }
}
