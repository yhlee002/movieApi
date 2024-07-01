package com.portfolio.demo.project.dto.comment;

import com.portfolio.demo.project.entity.comment.CommentMov;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentMovPagenationParam {
    private int totalPageCnt;
    private int currentPage;
    private int size;
    private long totalElementCnt;
    private List<CommentMovParam> commentMovsList;

    public CommentMovPagenationParam(Page<CommentMov> page) {
        this.totalPageCnt = page.getTotalPages();
        this.currentPage = page.getPageable().getPageNumber();
        this.size = page.getPageable().getPageSize();
        this.totalElementCnt = page.getTotalElements();
        this.commentMovsList = page.getContent().stream().map(CommentMovParam::create).collect(Collectors.toList());
    }
}