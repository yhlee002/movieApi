package com.portfolio.demo.project.intercepter;

import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.dto.member.MemberParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Slf4j
public class RememberMeIntercepter implements HandlerInterceptor {

    @Autowired // 제거 임시 보류
    private MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        if (request.getSession().getAttribute("member") == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getName() != "anonymousUser") {
                log.info("현재 auth : " + auth);

                MemberParam member = memberService.findByIdentifier((String) auth.getPrincipal());
//                UserDetail userDetail = new UserDetail(member);

                log.info("찾아온 member : " + member.toString());
                session.setAttribute("member", member);
            }
        }

        return true;
    }
}
