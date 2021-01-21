package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.service.CommentImpService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.vo.CommentImpPagenationVO;
import com.portfolio.demo.project.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    BoardImpService boardImpService;

    @Autowired
    CommentImpService commentImpService;

    @RequestMapping("/mypage")
    public String mypage(Model model, HttpSession session) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        model.addAttribute("boardList", boardImpService.getMyImpTop5(memberVO.getMemNo()));
        model.addAttribute("commList", commentImpService.getMyCommTop5(memberVO.getMemNo()));

        return "mypage/memberInfo";
    }

    @RequestMapping("/mypage/imp-board")
    public String myImpBoard(Model model, HttpSession session, @RequestParam(name = "p", defaultValue = "1") int pageNum) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        boardImpService.setMemNo(memberVO.getMemNo());
        model.addAttribute("pagenation", boardImpService.getMyImpListView(pageNum));

        return "/mypage/impBoards";
    }

    @RequestMapping("/mypage/imp-comment")
    public String myImpComment(Model model, HttpSession session, @RequestParam(name = "p", defaultValue = "1") int pageNum) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        commentImpService.setMemNo(memberVO.getMemNo());
        CommentImpPagenationVO pagenation = commentImpService.getMyCommListView(pageNum);
//        log.info(pagenation.toString());
        model.addAttribute("pagenation", pagenation);

        return "/mypage/impComments";
    }

    @RequestMapping("/mypage/modify_info")
    public String modifyUserInfo() {

        return "mypage/modifyInfo";
    }

    @RequestMapping("/mypage/modify_info_proc")
    public String modifyUserInfoProc(HttpSession session, @RequestParam("memNo") Long memNo, @RequestParam("nickname") String name, @RequestParam(name = "pwd", required = false) String pwd, @RequestParam("phone") String phone) {
        log.info("memNo : " + memNo + ", name : " + name + ", pwd : " + pwd + ", phone : " + phone);
        Member member = memberService.updateUserInfo(
                Member.builder()
                        .memNo(memNo)
                        .name(name)
                        .password(pwd)
                        .phone(phone) // 수정가능하도록 하기
                        .build()
        );

        session.setAttribute("member", new MemberVO(member));

        return "redirect:/mypage";
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

//    @RequestMapping("/mypage/delete_info/kakao/oauth2") : 카카오 로그인 연결 끊기 콜백 url


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

}
