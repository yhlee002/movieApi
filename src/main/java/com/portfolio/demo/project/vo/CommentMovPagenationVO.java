package com.portfolio.demo.project.vo;

import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Builder
public class CommentMovPagenationVO {
    private int totalPageCnt; // 총 페이지 개수
    private List<CommentMov> commentMovsList; // 한 화면에 보여줄 게시글 개수
}