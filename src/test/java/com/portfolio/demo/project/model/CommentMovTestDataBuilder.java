package com.portfolio.demo.project.model;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;

public class CommentMovTestDataBuilder {

    public static CommentMov.CommentMovBuilder noWayUpComment() {
        return CommentMov
                .builder()
                .content("test comment")
                .movieNo(1096197L) // No Way Up
                .rating(4);
    }

    public static CommentMov.CommentMovBuilder duneComment() {
        return CommentMov.builder()
                .content("dune comment.")
                .movieNo(693134L)
                .rating(4);
    }
}
