package com.portfolio.demo.project.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@ToString
public class CommentImpPagenationParam {
    private int totalPageCnt; // 총 페이지 개수
    private List<CommentImpParam> commentImpsList; // 한 화면에 보여줄 게시글 개수
}
