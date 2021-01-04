package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
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
    public String modifyUserInfoProc(HttpSession session, @RequestParam("memNo") Long memNo, @RequestParam("nickname") String name, @RequestParam(name = "pwd", required = false) String pwd, @RequestParam("phone") String phone) {
        log.info("memNo : "+memNo+", name : "+name+", pwd : "+pwd+", phone : "+phone);
        Member member = memberService.updateUserInfo(
                Member.builder()
                        .memNo(memNo)
                        .name(name)
                        .password(pwd) // Service 단에서 'pwd' null check
                        .phone(phone) // 수정가능하도록 하기
                        .build()
        );

//        session.removeAttribute("member");
        session.setAttribute("member", new MemberVO(member));

        return "redirect:/mypage/modify_info";
    }

    @RequestMapping("/mypage/delete_info")
    public String deleteUserInfo(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        System.out.println(memberVO.toString());
        memberService.deletUserInfo(memberVO.getMemNo());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
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
