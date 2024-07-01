package com.portfolio.demo.project.dto.comment;

import com.portfolio.demo.project.entity.comment.CommentImp;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentImpPagenationParam {
    private int totalPageCnt;
    private int currentPage;
    private int size;
    private long totalElementCnt;
    private List<CommentImpParam> commentImpsList;

    public CommentImpPagenationParam(Page<CommentImp> page) {
        this.totalPageCnt = page.getTotalPages();
        this.currentPage = page.getPageable().getPageNumber();
        this.size = page.getPageable().getPageSize();
        this.totalElementCnt = page.getTotalElements();
        this.commentImpsList = page.getContent().stream().map(CommentImpParam::create).collect(Collectors.toList());
    }
}
