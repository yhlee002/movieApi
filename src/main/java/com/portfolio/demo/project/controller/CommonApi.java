package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.security.UserDetailsServiceImpl;
import com.portfolio.demo.project.service.*;
import com.portfolio.demo.project.vo.kobis.movie.MovieVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CommonApi {

    private final MovieService movieService;

    private final BoardNoticeService boardNoticeService;

    private final BoardImpService boardImpService;

    private final MailService mailService;

    @GetMapping("/")
    public String mainPage(Model model, HttpSession session) { // Principal principal
        /**
         * 인증 정보를 꺼내는 법
         * Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         * 인증 정보에서의 username(현재에는 email)을 가져올 수 있음
         * User principal = (User) authentication.getPrincipal();
         * 스프링 MVC 핸들러의 맥개변수로 @AuthenticationPrincipal을 사용하게 되면, getPrincipal()을 통해 얻을 수 있는 객체를 바로 주입받을 수 있다.
         * 현재 로그인(인증)된 사용자가 없는 경우에는 null, 있는 경우에는 username과 authorities 참조 가능
         */

        List<MovieVO> movieVOList = movieService.getDailyBoxOfficeList();

        model.addAttribute("movieList", movieVOList);
        model.addAttribute("recent_notice", boardNoticeService.getRecNoticeBoard());
        model.addAttribute("favorite_imp", boardImpService.getFavImpBoard());

        return "index";
    }

    @GetMapping("/sign-up")
    public String signUpPage() {
        return "sign-up/sign-upForm";
    }

    @PostMapping("/logout")
    public String loout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth); // 세션을 무효화시킴(네이버 로그인 api에서 제공하는 접근 토큰, 리프레시 토큰도 함께 제거될 듯?
        }
        return "redirect:/";
    }

    @GetMapping("/contact")
    public String csMain() {
        return "contact/main";
    }

}
