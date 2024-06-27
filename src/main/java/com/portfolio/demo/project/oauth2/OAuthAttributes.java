package com.portfolio.demo.project.oauth2;

import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.oauth2.userinfo.KakaoOAuth2UserInfo;
import com.portfolio.demo.project.oauth2.userinfo.NaverOAuth2UserInfo;
import com.portfolio.demo.project.oauth2.userinfo.OAuth2UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthAttributes {

    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값
    private OAuth2UserDto oAuth2UserInfo; // 소셜 타입별 로그인 유저 정보(이를 상속받아 provider별 dto 존재)

    public static OAuthAttributes of(SocialLoginProvider provider, String nameAttributeKey, Map<String, Object> attributes) {

        if (SocialLoginProvider.naver == provider) {
            return ofNaver(nameAttributeKey, attributes);
        } else if (SocialLoginProvider.kakao == provider) {
            return ofKakao(nameAttributeKey, attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    public static Member toEntity(SocialLoginProvider provider, OAuth2UserDto oAuth2UserDto) {
        return Member.builder()
                .provider(provider)
                .identifier(oAuth2UserDto.getId()) // UUID.randomUUID() + "@socialuser.com"
                .name(oAuth2UserDto.getNickname())
                .profileImage(oAuth2UserDto.getProfileUrl())
                .role(MemberRole.ROLE_GUEST) // 첫 소셜 로그인시 role_guest -> 회원가입 의사 물은 후 role_user
                .certification(MemberCertificated.Y)
//                .role(MemberRole.ROLE_USER)
                .build();
    }
}
