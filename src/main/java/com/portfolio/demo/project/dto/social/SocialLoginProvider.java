package com.portfolio.demo.project.dto.social;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialLoginProvider {
    naver("네이버"), kakao("카카오"), none("없음");

    private final String desc;
}
