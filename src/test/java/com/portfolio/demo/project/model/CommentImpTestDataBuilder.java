package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;

import java.util.Random;

public class CommentImpTestDataBuilder {

    private static final Random random = new Random();
    public static CommentImp.CommentImpBuilder randomComment(Member writer, BoardImp board) {
        return CommentImp.builder()
                .writer(writer)
                .board(board)
                .content("Random comment for test." + random.nextInt(1000));
    }
}
