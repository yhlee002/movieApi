package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.OauthMember;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.repository.OauthMemberRepository;
import com.portfolio.demo.project.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/sign-up")
public class SignupController {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    OauthMemberRepository oauthMemberRepository;

    @Autowired
    OauthMemberService oauthMemberService;

    @Autowired
    PhoneMessageService phoneMessageService;

    @Autowired
    MailService mailService;

    @Autowired
    CertKeyService certKeyService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @ResponseBody
    @RequestMapping(value = "/emailCk", method = RequestMethod.POST)
    public Member emailCkProc(@RequestParam String email) {
        log.info("들어온 이메일 : " + email);

        Member member = memberService.findByEmail(email);

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
        Member member = memberService.findByName(name);

        if (member != null) {
            log.info("[nameCkProc] member 정보 : " + member.toString());
            return member;
        } else {
            log.info("[nameCkProc] member 정보 : null");
            return null;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/phoneCk/{memType}", method = RequestMethod.POST)
    public String authPhone(@RequestParam String phone, @PathVariable  String memType) {
        log.info("들어온 phone : "+phone+", memType : "+memType);
        if (memType.equals("naver")) { // 네아로 api 가입 회원의 경우
//            if (memType.equals("naver")) {
            return memberService.IsOauthMemberByPhone(phone);
//            }
//              else if(memType.equals("kakao")){ // 카카오 로그인 api 가입 회원의 경우
//                return "";
//            }
        } else { // 네아로 api 가입 회원이 아닌 경우(memType == "none")
            return memberService.IsBasicMemberByPhone(phone);
        }

    }

    @RequestMapping(value = "/phoneCkForm", method = RequestMethod.GET)
    public String phoneCkForm() {
        return "sign-up/phoneCkForm";
    }

    @RequestMapping(value = "/phoneCkProc", method = RequestMethod.GET) // 인증키를 받을 핸드폰 번호 입력 페이지
    public String phoneCkPage(Model m, @RequestParam String phone, @RequestParam(required = false) String provider) {

        String phoneAuthKey = phoneMessageService.sendMessageForSignUp(phone);
        log.info("phoneAuthKey 인코딩 전 값 : " + phoneAuthKey);
        m.addAttribute("phoneAuthKey", passwordEncoder.encode(phoneAuthKey));
        m.addAttribute("phoneNum", phone);
        return "sign-up/phoneCkAuth";
    }

    @ResponseBody
    @RequestMapping(value = "/phoneCkProc2", method = RequestMethod.POST) // 인증키 일치 여부 확인 페이지
    public String phoneCkProc(@RequestParam String authKey, @RequestParam String phoneAuthKey) {
        if (passwordEncoder.matches(authKey, phoneAuthKey)) {
            return "인증되었습니다.";
        } else {
            return "인증에 실패했습니다.";
        }
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
        memberService.saveMember(member);
        mailService.sendMail(member.getEmail());

        Member memberFindedByEmail = memberRepository.findByEmail(email);
        return memberFindedByEmail.getMemNo();
    }

    @ResponseBody
    @RequestMapping(value = "/sign-up-processor_n", method = RequestMethod.POST)
    public Long signUpProc_o(@RequestParam String id, @RequestParam String name, @RequestParam String phone) {
        OauthMember oMember = OauthMember.builder()
                .oauthmemNo(null)
                .uniqueId(id)
                .nickname(name)
                .phone(phone)
                .provider("naver")
                .regDt(LocalDateTime.now())
                .role("ROLE_USER")
                .build();
        oauthMemberService.saveOauthMember(oMember);
        log.info(oMember.toString());

        OauthMember memberFindedByUniqueId = oauthMemberRepository.findOauthMemberByUniqueId(id);
        log.info("생성된 유저 : "+memberFindedByUniqueId.toString());

        return memberFindedByUniqueId.getOauthmemNo();
    }

    // 회원 가입 성공 및 이메일 전송
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String signUpSuccessPage(Model m, @RequestParam(required = false) Long memNo, @RequestParam(required = false) Long oauthMemNo) {
        if (memNo != null && oauthMemNo == null) {
            Member member = memberRepository.findByMemNo(memNo);
            m.addAttribute("member", member);
            return "sign-up/successPage";
        } else { // (memNo == null && oauthMemNo != null)
//            OauthMember oMember = oauthMemberService.findOauthMemberByOauthMemNo(oauthMemNo);
            return "redirect:/";
        }
    }

    // 가입 이메일 인증
    @RequestMapping(value = "/certificationEmail", method = RequestMethod.GET)
    public String emailAuthKeyCk(@RequestParam Long memNo, @RequestParam String certKey) {
        // authKey는 해싱된 상태로 링크에 파라미터로 추가되어 이메일 전송됨
        // DB에 저장된 해당 회원의 certKey와 일치하는지 확인하고 정보 수정

        Boolean checkVal = certKeyService.CheckCertInfo(memNo, certKey);
        // 인증된 경우 인증 완료 페이지 이동
        if (checkVal) {
            return "sign-up/certEmailSuccess";
        } else {
            return "sign-up/certEmailFail";
        }
    }

    /* 네아로 api, 카카오로그인 api로 접근한 회원가입 */
    @RequestMapping(value = "/oauthMem", method = RequestMethod.GET)
    public String oauthMem(HttpSession session, Model model) {
        session.removeAttribute("oauth_message");

        Map<String, String> profile = (Map<String, String>) session.getAttribute("profile");
        System.out.println("profile : " + profile);
        String id = (String) profile.get("id");
        String nickname = (String) profile.get("nickname");
//        String profile_image = (String) profile.get("profile_image");

        model.addAttribute("id", id);
        model.addAttribute("nickname", nickname);
//        model.addAttribute("profile_image");

        return "sign-up/oauthMemSign-upForm"; // email이 없기 때문에 submit되고 나서 넘어온 값에 email이 null이면 oauthMem으로 가입되게 해야함
    }
}
