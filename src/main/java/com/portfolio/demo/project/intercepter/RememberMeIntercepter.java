package com.portfolio.demo.project.intercepter;

import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.oauth2.CustomOAuth2User;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.dto.member.MemberParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Iterator;

@Slf4j
public class RememberMeIntercepter implements HandlerInterceptor {

    @Autowired // 제거 임시 보류
    private MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String identifier = "";
        MemberRole role = null;
        if (auth instanceof OAuth2AuthenticationToken) {
            CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
            identifier = user.getIdentifier();
            role = user.getRole();
        } else {
            identifier = (String) auth.getPrincipal();
            role = auth.getAuthorities().contains(MemberRole.ROLE_ADMIN) ? MemberRole.ROLE_ADMIN :
            auth.getAuthorities().contains(MemberRole.ROLE_USER) ? MemberRole.ROLE_USER : MemberRole.ROLE_GUEST;
            Iterator<? extends GrantedAuthority> iter = auth.getAuthorities().iterator();
            role = MemberRole.valueOf(iter.next().toString());
        }

        log.info("RememberMeIntercepter 로그인 된 유저 확인(정보: {}, 권한: {})", auth.getName(), auth.getAuthorities());

        MemberParam member = memberService.findByIdentifier(identifier);

        if (member != null) {
            log.info("RememberMeIntercepter member 조회(id: {}, identifier: {})", member.getMemNo(), member.getIdentifier());
            session.setAttribute("member", member);
        } else {
            log.info("RememberMeIntercepter member 조회(없음)");
        }

        return true;
}
}
