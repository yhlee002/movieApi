package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;

@Getter
@ToString
@Builder
public class CommentImpVO {

    private Long id;
//    private Long boardId;
//    private String boardTitle;
    private MemberVO writer;
//    private Long writerId;
//    private String writerName;
    private String content;
    private String regDate;

    public static CommentImpVO create(CommentImp imp) {
        return CommentImpVO.builder()
                .id(imp.getId())
//                .boardId(imp.getBoard().getId())
//                .boardTitle(imp.getBoard().getTitle())
//                .writerId(imp.getWriter().getMemNo())
//                .writerName(imp.getWriter().getName())
                .writer(MemberVO.create(imp.getWriter()))
                .content(imp.getContent())
                .regDate(imp.getRegDate())
                .build();
    }
}
