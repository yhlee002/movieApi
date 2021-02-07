package com.portfolio.demo.project.intercepter;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class RememberMeIntercepter extends HandlerInterceptorAdapter {

    @Autowired
    MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        if (request.getSession().getAttribute("member") == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.getName() != "anonymousUser") {
                log.info("현재 auth : " + auth);
                UserDetail userDetail = (UserDetail) auth.getPrincipal();
                Member member = memberService.findByIdentifier(userDetail.getUsername());
                log.info("찾아온 member : " + member.toString());
                session.setAttribute("member", new MemberVO(member));
            }
        }

        return true;
    }
}
