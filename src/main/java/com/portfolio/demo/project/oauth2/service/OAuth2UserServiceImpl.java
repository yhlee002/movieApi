package com.portfolio.demo.project.oauth2.service;

import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.oauth2.CustomOAuth2User;
import com.portfolio.demo.project.oauth2.OAuthAttributes;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("OAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        /*
         * DefaultOAuth2UserService 객체 생성
         * DeaultOAuth2UserService: 소셜 로그인 API에 사용자 프로필 제공 URI로 요청을 전송한 후
         * 사용자 정보를 얻어 DefaultOAuth2User 객체를 생성 및 반환
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

//        OAuth2User oAuth2User = super.loadUser(userRequest);
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        // registrationId(provider) 추출
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        SocialLoginProvider provider = getProvider(registrationId);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        // provider에 따른 dto 생성
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(provider, userNameAttributeName, oAuth2User.getAttributes());

        Member createdUser = getUser(oAuthAttributes, provider);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(String.valueOf(createdUser.getRole()))),
                oAuth2User.getAttributes(),
                oAuthAttributes.getNameAttributeKey(),
                createdUser.getIdentifier(),
                createdUser.getRole()
        );
    }

    public SocialLoginProvider getProvider(String registrationId) {
        if (String.valueOf(SocialLoginProvider.kakao).equals(registrationId.toLowerCase()))       return SocialLoginProvider.kakao;
        else if (String.valueOf(SocialLoginProvider.naver).equals(registrationId.toLowerCase())) return SocialLoginProvider.naver;

        return null;
    }

    @Transactional
    public Member getUser(OAuthAttributes oAuthAttributes, SocialLoginProvider provider) {
        Member user = memberRepository.findByIdentifierAndProvider(oAuthAttributes.getOAuth2UserInfo().getId(), provider);

        if (user == null) {
            return createUser(provider, oAuthAttributes);
        }

        if (!user.getProfileImage().equals(oAuthAttributes.getOAuth2UserInfo().getProfileUrl())) {
            user.updateProfileImage(oAuthAttributes.getOAuth2UserInfo().getProfileUrl());
        }

        if (!user.getName().equals(oAuthAttributes.getOAuth2UserInfo().getNickname())) {
            user.updateName(oAuthAttributes.getOAuth2UserInfo().getNickname());
        }

        return user;
    }

    private Member createUser(SocialLoginProvider provider, OAuthAttributes oAuthAttributes) {
        return memberRepository.save(OAuthAttributes.toEntity(provider, oAuthAttributes.getOAuth2UserInfo()));
    }
}

