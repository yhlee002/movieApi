package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class MainController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @RequestMapping("/")
    public String mainPage(@AuthenticationPrincipal User user, Model model) {
        log.info("access main page");

        model.addAttribute("currentUser", user);
        return "index";
    }

    @RequestMapping("/sign-in")
    public String signInPage() {
        log.info("access login page");
        return "sign-in/sign-inForm";
    }

    @RequestMapping("/sign-in/sign-in-processor")
    public void signInProc(@RequestParam String email, @RequestParam String password) {
        log.debug("access sign-inProc");

        UserDetails user = userDetailsService.loadUserByUsername(email);
    }

    @RequestMapping("/sign-up")
    public String signUpPage() {
        log.info("access sign-up page");
        return "/sign-up/sign-upForm";
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
