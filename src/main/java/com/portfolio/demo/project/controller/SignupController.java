package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.service.CertKeyService;
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

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/sign-up")
public class SignupController {

    @Autowired
    MemberService memberService;

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

        Member member = memberService.findByIdentifier(email);

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
    @RequestMapping(value = "/phoneCk", method = RequestMethod.POST) // /{memType}
    public Member authPhone(@RequestParam String phone) { // , @PathVariable String memType
        log.info("들어온 phone number : " + phone);

        return memberService.findByPhone(phone);


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
    public Long signUpProc(@RequestParam String email, @RequestParam String name, @RequestParam String password, @RequestParam String phone, @RequestParam String provider) {
        Member member = Member.builder()
                .memNo(null)
                .identifier(email)
                .name(name)
                .password(password)
                .phone(phone)
                .provider(provider)
                .build();
        memberService.saveMember(member);
        mailService.sendGreetingMail(member.getIdentifier());

        /* DB에 저장된 데이터 로드 */
        Member createdMember = memberService.findByIdentifier(email);
        log.info("생성된 유저 : " + createdMember.toString());
        return createdMember.getMemNo();
    }

    @ResponseBody
    @RequestMapping(value = "/sign-up-processor_oauth", method = RequestMethod.POST)
    public Long signUpProc_o(HttpSession session, @RequestParam String id, @RequestParam String name, @RequestParam String phone, @RequestParam String provider) {
        memberService.saveOauthMember(session, id, name, phone, provider);

        /* DB에 저장된 데이터 로드 */
        Member createdMember = memberService.findByIdentifierAndProvider(id, provider);
        log.info("생성된 유저 : " + createdMember.toString());

        return createdMember.getMemNo();
    }

    // 회원 가입 성공 및 이메일 전송
    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String signUpSuccessPage(Model m, @RequestParam(required = false) Long memNo, @RequestParam(required = false) Long oauthMemNo) {
        if (memNo != null && oauthMemNo == null) {
            Member member = memberService.findByMemNo(memNo);
            if (member != null) {
                m.addAttribute("member", member);
            }

            return "sign-up/successPage";
        } else {

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

        model.addAttribute("id", id);
        model.addAttribute("nickname", nickname);

        return "sign-up/oauthMemSign-upForm"; // email이 없기 때문에 submit되고 나서 넘어온 값에 email이 null이면 oauthMem으로 가입되게 해야함
    }
}
