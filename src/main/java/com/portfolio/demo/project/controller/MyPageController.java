package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/mypage")
public class MyPageController {

    @Autowired
    MemberService memberService;

    @RequestMapping("/{memNo}")
    public String myPage(Model model, @PathVariable Long memNo){
        // 해당 memNo를 가진 회원의 게시글 및 정보 조회
        Member member = memberService.findByMemNo(memNo);
        model.addAttribute("member", member);
        // ... (게시글 가져오기)
        return "mypage/memberInfo";
    }

}
