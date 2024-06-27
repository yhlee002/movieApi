package com.portfolio.demo.project.oauth2.userinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserDto {

    Map<String, Object> account;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        account = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getNickname() {
//        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account == null) {
            return null;
        }

        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return profile == null ? null : (String) profile.get("nickname");
    }

    @Override
    public String getProfileUrl() {
//        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if (account == null) {
            return null;
        }

        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return profile == null ? null : String.valueOf(profile.get("thumbnail_image_url"));
    }
}