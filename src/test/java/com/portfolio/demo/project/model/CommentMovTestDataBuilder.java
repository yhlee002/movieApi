package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;

public class CommentMovTestDataBuilder {

    public static CommentMov.CommentMovBuilder noWayUpComment(Member member) {
        return CommentMov
                .builder()
                .writer(member)
                .content("test comment")
                .movieNo(1096197L) // No Way Up
                .rating(4);
    }

    public static CommentMov.CommentMovBuilder duneComment(Member member) {
        return CommentMov.builder()
                .writer(member)
                .content("dune comment.")
                .movieNo(693134L)
                .rating(4);
    }
}
