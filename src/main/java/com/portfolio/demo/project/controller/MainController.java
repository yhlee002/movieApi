package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.security.UserDetailsServiceImpl;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.service.BoxOfficeService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.util.BoxOfficeListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Slf4j
@Controller
public class MainController {

    @Autowired
    MemberService memberService;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    BoxOfficeService boxOfficeService;

    @Autowired
    BoardNoticeService boardNoticeService;

    @Autowired
    BoardImpService boardImpService;

    @RequestMapping("/")
    public String mainPage(@AuthenticationPrincipal Principal principal, HttpSession session, Model model) { // Principal principal
        /**
         * 인증 정보를 꺼내는 법
         * Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         * 인증 정보에서의 username(현재에는 email)을 가져올 수 있음
         * User principal = (User) authentication.getPrincipal();
         * 스프링 MVC 핸들러의 맥개변수로 @AuthenticationPrincipal을 사용하게 되면, getPrincipal()을 통해 얻을 수 있는 객체를 바로 주입받을 수 있다.
         * 현재 로그인(인증)된 사용자가 없는 경우에는 null, 있는 경우에는 username과 authorities 참조 가능
         */

        model.addAttribute("movieList", boxOfficeService.getDailyBoxOfficeList());
        model.addAttribute("recent_notice", boardNoticeService.getRecNoticeBoard());
        model.addAttribute("favorite_imp", boardImpService.getFavImpBoard());

        return "index";
    }

    @RequestMapping("/sign-up")
    public String signUpPage() {
        return "sign-up/sign-upForm";
    }

//    @RequestMapping("/elements")
//    public String elements() {
//        return "elements";
//    }
//
//    @RequestMapping("/generic")
//    public String generic() {
//        return "generic";
//    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String loout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }

    @RequestMapping("/cs")
    public String csMain(){
        return "/cs/main";
    }
}
