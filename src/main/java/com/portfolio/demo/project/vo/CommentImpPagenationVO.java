package com.portfolio.demo.project.vo;

import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
public class CommentImpPagenationVO {
    private int totalPageCnt; // 총 페이지 개수
    private List<CommentImpVO> commentImpsList; // 한 화면에 보여줄 게시글 개수
}
