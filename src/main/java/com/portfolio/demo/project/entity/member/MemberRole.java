package com.portfolio.demo.project.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    ROLE_ADMIN("관리자"), ROLE_USER("일반회원"),
    ROLE_GUEST("소셜 로그인 시도 사용자"), ROLE_ANONYMOUS("비회원");

    private final String desc;
}
