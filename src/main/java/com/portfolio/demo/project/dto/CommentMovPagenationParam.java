package com.portfolio.demo.project.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CommentMovPagenationParam {
    private int totalPageCnt; // 총 페이지 개수
    private List<CommentMovParam> commentMovsList; // 한 화면에 보여줄 게시글 개수
}