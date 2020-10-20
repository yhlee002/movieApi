package com.portfolio.demo.project.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class MainController {

    @RequestMapping("/")
    public String mainPage() {
        log.info("access main page");
        return "index";
    }

    @RequestMapping("/sign-in")
    public String signInPage() {
        log.info("access login page");
        return "sign-in/sign-inForm";
    }

    @RequestMapping("/sign-in/sign-in-processor")
    public String signInProc(Model m, @RequestParam String email, @RequestParam String password){
        log.debug("access sign-inProc");

        m.addAttribute("email", email);
        m.addAttribute("password", password);

        return "sign-in/sign-inProc";
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
}
