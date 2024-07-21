package com.portfolio.demo.project.service.member.search;

import com.portfolio.demo.project.dto.member.MemberPagenationParam;
import com.portfolio.demo.project.dto.member.MemberResponse;
import com.portfolio.demo.project.dto.member.request.MemberSearchCondition;
import com.portfolio.demo.project.repository.member.MemberRepositoryCustomImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberSearchService {

    private final MemberRepositoryCustomImpl memberRepositoryCustom;

    public MemberPagenationParam search(int page, int size, MemberSearchCondition condition) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MemberResponse> pageObj = memberRepositoryCustom.searchPageSimple(condition, pageable);

        return MemberPagenationParam.builder()
                .totalPageCnt(pageObj.getTotalPages())
                .totalElementCnt(pageObj.getTotalElements())
                .memberList(pageObj.getContent())
                .currentPage(page)
                .size(size)
                .build();
    }
}
