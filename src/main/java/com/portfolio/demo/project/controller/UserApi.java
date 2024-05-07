package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.vo.MemberVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserApi {

    private final MemberService memberService;

    @GetMapping("/user")
    public MemberVO getCurrentUser(HttpSession session) {
        return (MemberVO) session.getAttribute("member");
    }

    @GetMapping("/user/{id}")
    public MemberVO getUser(@PathVariable Long id) {
        Member user = memberService.findByMemNo(id);
        return MemberVO.create(user);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth); // 세션을 무효화시킴(네이버 로그인 api에서 제공하는 접근 토큰, 리프레시 토큰도 함께 제거될 듯?
        } else throw new IllegalStateException("로그인된 회원 정보가 없습니다.");
    }
}

