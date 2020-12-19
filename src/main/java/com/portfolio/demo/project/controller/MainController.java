package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.OauthMember;
import com.portfolio.demo.project.pojo.MemberPojo;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.OauthMemberService;
import com.portfolio.demo.project.security.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Slf4j
@Controller
public class MainController {

    @Autowired
    MemberService memberService;

    @Autowired
    OauthMemberService oauthMemberService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @RequestMapping("/")
    public String mainPage(@AuthenticationPrincipal Principal principal, HttpSession session) { // Principal principal
        /**
         * 인증 정보를 꺼내는 법
         * Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         * 인증 정보에서의 username(현재에는 email)을 가져올 수 있음
         * User principal = (User) authentication.getPrincipal();
         * 스프링 MVC 핸들러의 맥개변수로 @AuthenticationPrincipal을 사용하게 되면, getPrincipal()을 통해 얻을 수 있는 객체를 바로 주입받을 수 있다.
         * 현재 로그인(인증)된 사용자가 없는 경우에는 null, 있는 경우에는 username과 authorities 참조 가능
         */
        log.info("access main page");

        Member member = null;
        OauthMember oMember = null;
        MemberPojo mem = null;

        if (principal != null) {
            log.info(principal.toString());
            member = memberService.findByEmail(principal.getName());

            if (member == null) {
                oMember = oauthMemberService.findOauthMemberByUniqueId(principal.getName());
                if (oMember != null) {
                    log.info("It's oMember ! : " + oMember.toString());
                    mem = new MemberPojo(oMember);
                }
            } else { // member != null
                log.info("It's member ! : " + member.toString());
                mem = new MemberPojo(member);
            }

            log.info("current member : " + mem.toString());

            if (mem.getProfileImage() != null) {
                String profileImage = mem.getProfileImage().replace("\\/", "/");
                System.out.println(profileImage);
                mem.setProfileImage(profileImage);
            }
        }

        session.setAttribute("principal", principal);
        session.setAttribute("member", mem); // 없는 경우 null

        return "index";
    }

    @RequestMapping("/sign-up")
    public String signUpPage() {
        log.info("access sign-up page");
        return "sign-up/sign-upForm";
    }

    @RequestMapping("/elements")
    public String elements() {
        log.info("access elements page");
        return "elements";
    }

    @RequestMapping("/generic")
    public String generic() {
        log.info("access generic page");
        return "generic";
    }

    @RequestMapping("/logout")
    public String logout() {
        return "index";
    }
}
