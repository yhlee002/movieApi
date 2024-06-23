package com.portfolio.demo.project.dto.social;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SocialProfileParam {
    private String id;
    private String nickname;
    private String profileImageUrl; // profile_image_url
}
