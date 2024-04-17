package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@ToString
public class BoardImpVO {
    private Long id;
    private String title;
    private String content;
    private Member writer;
    private LocalDateTime regDate;
    private int views; // 조회수
    private List<CommentImp> comments;

    public static BoardImpVO create(BoardImp board) {
        return BoardImpVO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regDate(board.getRegDate())
                .views(board.getViews())
                .comments(board.getComments())
                .build();
    }
}
