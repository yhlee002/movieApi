package com.portfolio.demo.project.repository.member;

import com.portfolio.demo.project.dto.member.MemberResponse;
import com.portfolio.demo.project.dto.member.request.MemberSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<MemberResponse> searchPageSimple(MemberSearchCondition condition, Pageable pageable);
}
