package com.portfolio.demo.project.oauth2.userinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuth2UserDto {
    protected Map<String, Object> attributes;

    public abstract String getId(); // 식별값(ex. google: sub, kakao: id, naver: id)
    public abstract String getNickname();
    public abstract String getProfileUrl();
}
