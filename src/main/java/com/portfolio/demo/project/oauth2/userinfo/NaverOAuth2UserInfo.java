package com.portfolio.demo.project.oauth2.userinfo;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserDto {

    private Map<String, Object> response;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        response = (Map<String, Object>) attributes.get("response");
    }

    public String getResultCode() {
        return String.valueOf(attributes.get("resultCode"));
    }

    public String getMessage() {
        return String.valueOf(attributes.get("message"));
    }

    @Override
    public String getId() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return response == null ? null : (String) response.get("id");
    }

    @Override
    public String getNickname() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return response.get("nickname") == null ? null : (String) response.get("nickname");
    }

    @Override
    public String getProfileUrl() {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return response.get("profile_image") == null ? null : (String) response.get("profile_image");
    }
}
