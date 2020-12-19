package com.portfolio.demo.project.security;

import com.portfolio.demo.project.entity.member.OauthMember;
import com.portfolio.demo.project.repository.OauthMemberRepository;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class OauthUserService {

    @Autowired
    OauthMemberRepository oMemberRepository;

    public Authentication getAuthByOAuthInfo(Map<String, String> profile) {
        OauthMember oMember = oMemberRepository.findByProviderAndUniqueId("naver", profile.get("id"));
        UserDetail userDetail = new UserDetail(oMember);

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetail.getUsername(), null, userDetail.getAuthorities());

        return auth;
    }
}
