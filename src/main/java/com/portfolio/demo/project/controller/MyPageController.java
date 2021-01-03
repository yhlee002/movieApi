package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class MyPageController {

    @Autowired
    MemberService memberService;

    @RequestMapping("/mypage")
    public String mypage() {
        return "mypage/memberInfo";
    }

    @RequestMapping("/mypage/modify_info")
    public String modifyUserInfo() {

        return "mypage/modifyInfo";
    }

    @RequestMapping("/mypage/modify_info_proc")
    public String modifyUserInfoProc(Long memNo, String name, String password, String phone) {

        memberService.updateUserInfo(
                Member.builder()
                        .memNo(memNo)
                        .name(name)
                        .password(password)
                        .phone(phone)
                        .build()
        );

        return "mypage/memberInfo";
    }

    @RequestMapping("/mypage/delete_info")
    public String deleteUserInfo(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        Member member = (Member) session.getAttribute("member");
        memberService.deletUserInfo(member);

        return "redirect:/";
    }

    @RequestMapping("/mypage/authentication")
    public String authenticationUser(String pwd) {
        SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }

    @RequestMapping("/mypage/uploadProfileImage")
    public String uploadProfileImageForm() {
        /* 프로필 이미지 변경시 업로드 페이지로 감 */
        return "mypage/uploadProfileImageForm";
    }

    @RequestMapping(value = "/mypage/uploadProfileImage_proc", method = RequestMethod.POST)
    public String uploadProfileImageProc() {
        /* 실제로 넘어온 이미지를 서버에 업로드하고 DB의 프로필 이미지 경로를 수정 */
        return "";
    }

//    @RequestMapping("/mypage/{memNo}")
//    public String myPage(Model model, @PathVariable Long memNo){
//        // 해당 memNo를 가진 회원의 게시글 및 정보 조회
//        Member member = memberService.findByMemNo(memNo);
//        model.addAttribute("member", member);
//        // ... (게시글 가져오기)
//        return "mypage/memberInfo";
//    }

}
