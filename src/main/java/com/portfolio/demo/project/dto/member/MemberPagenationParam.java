package com.portfolio.demo.project.dto.member;

import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.entity.member.Member;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberPagenationParam {
    private int totalPageCnt;
    private int currentPage;
    private int size;
    private long totalElementCnt;
    private List<MemberParam> memberList;

    public MemberPagenationParam(Page<Member> page) {
        this.totalPageCnt = page.getTotalPages();
        this.currentPage = page.getPageable().getPageNumber();
        this.size = page.getPageable().getPageSize();
        this.totalElementCnt = page.getTotalElements();
        this.memberList = page.getContent().stream().map(MemberParam::create).collect(Collectors.toList());
    }
}
