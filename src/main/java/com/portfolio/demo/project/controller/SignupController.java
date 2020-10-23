package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.service.MailService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.PhoneMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Slf4j
@Controller
@RequestMapping("/sign-up")
public class SignupController {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    PhoneMessageService phoneMessageService;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @ResponseBody
    @RequestMapping(value = "/emailCk", method = RequestMethod.POST)
    public Member emailCkProc(@RequestParam String email) {
        log.info("들어온 이메일 : " + email);

        Member member = memberRepository.findByEmail(email);

        if (member != null) {
            log.info("[emailCkProc] member 정보 : " + member.toString());
            return member;
        } else {
            log.info("[emailCkProc] member 정보 : " + null);
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/nameCk", method = RequestMethod.POST)
    public Member nameCkProc(@RequestParam String name) {
        Member member = memberRepository.findByName(name);

        if (member != null) {
            log.info("[nameCkProc] member 정보 : " + member.toString());
            return member;
        } else {
            log.info("[nameCkProc] member 정보 : null");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/phoneCk", method = RequestMethod.POST)
    public Member authPhone(@RequestParam String phone) {
        Member member = memberService.findByPhone(phone);
        if (member != null) {
            log.info("[phoneCkProc] member 정보 : " + member.toString());
            return member;
        } else {
            log.info("[phoneCkProc] member 정보 : null");
            return null;
        }
    }

    @RequestMapping(value = "/phoneCkForm", method = RequestMethod.GET)
    public String phoneCkForm(){
        return "sign-up/phoneCkForm";
    }

    @RequestMapping(value = "/phoneCkProc", method = RequestMethod.GET) // 인증키를 받을 핸드폰 번호 입력 페이지
    public String phoneCkPage(Model m, @RequestParam String phone){
        String phoneAuthKey = phoneMessageService.sendMessageForSignUp(phone);
        log.info("phoneAuthKey 인코딩 전 값 : "+phoneAuthKey);
        m.addAttribute("phoneAuthKey", passwordEncoder.encode(phoneAuthKey));
        m.addAttribute("phoneNum", phone);
        return "sign-up/phoneCkAuth"; 
    }

    @ResponseBody
    @RequestMapping(value = "/phoneCkProc2", method = RequestMethod.POST) // 인증키 일치 여부 확인 페이지
    public String phoneCkProc(@RequestParam String authKey, @RequestParam String phoneAuthKey){
        log.info("authKey : "+authKey);
        log.info("phoneAuthKey : "+phoneAuthKey);


        if(passwordEncoder.matches(authKey, phoneAuthKey)){
            return "인증되었습니다.";
        }else{
            return "인증에 실패했습니다.";
        }
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.GET) // 메일 링크를 통해 인증할 경우의 인증 성공 페이지
    public String welcomePage() {
        return "sign-up/welcome";
    }

    @ResponseBody
    @RequestMapping(value = "/sign-up-processor", method = RequestMethod.POST)
    public Long signUpProc(@RequestParam String email, @RequestParam String name, @RequestParam String password, @RequestParam String phone) {
        Member member = Member.builder()
                .memNo(null)
                .email(email)
                .name(name)
                .password(password)
                .phone(phone)
                .regDt(LocalDateTime.now())
                .build();
        log.info("생성될 member 정보 : "+member.toString());
        memberService.saveMember(member);
        mailService.sendMail(member.getEmail());

        Member member1 = memberRepository.findByEmail(email);
        return member1.getMemNo();
    }

    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String signUpSuccessPage(Model m, Long memNo){
        //이메일 전송
        Member member = memberRepository.findByMemNo(memNo);
        m.addAttribute("member", member);
        return "sign-up/successPage";
    }
}
