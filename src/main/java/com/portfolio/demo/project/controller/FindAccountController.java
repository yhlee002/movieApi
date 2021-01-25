package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MailService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.PhoneMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class FindAccountController {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    PhoneMessageService messageService;

    @Autowired
    MailService mailService;

    @RequestMapping("/findaccount")
    public String findAccount() {
        return "/sign-in/find-account";
    }


    /* 이메일 찾기 */

    @RequestMapping("/findEmail") // 인증할 연락처(문자) 입력 페이지
    public String findEmail() {
        return "/sign-in/findEmailForm";
    }

    @ResponseBody
    @RequestMapping("/findEmail/phoneCk") // 존재하는 번호인지 Ajax로 검증 - 있는 이메일이라면 문자 보낼지 확인 -> 확인시 문자 전송 후 /find-email2로 이동
    public Map<String, String> findEmailCheckPhone(String phone) {
        Map<String, String> result = new HashMap<>();
        Member member = memberService.findByPhone(phone);
        if (member != null) {
            result.put("resultCode", "exist");
        } else {
            result.put("resultCode", "not exist");
        }
        return result;
    }

    @RequestMapping("/findEmail/checkCertKey") // 인증 번호 입력하는 페이지
    public String sendCertMessage(HttpSession session, @RequestParam(name = "p") String phone) {
        String tempKey = messageService.sendCertificationMessage(phone); // 메세지 전송 후 인증번호를 해시값으로 변형해 세션에 저장
        session.setAttribute("certKey", passwordEncoder.encode(tempKey));
        session.setAttribute("phoneNum", phone);

        return "/sign-in/findEmailForm2";
    }

    @ResponseBody
    @RequestMapping("/findEmail/checkAuthKey")
    public Map<String, String> checkCertKey(HttpSession session, String certKeyInput) {
        Map<String, String> result = new HashMap<>();

        String certKey = (String) session.getAttribute("certKey");
        session.removeAttribute("certKey"); // 세션에서 가져온 뒤 세션에서 제거

        if (passwordEncoder.matches(certKeyInput, certKey)) {
            result.put("resultCode", "true");
        } else {
            result.put("resultCode", "fail");
        }
        return result;
    }

    @RequestMapping("/findEmail/result")
    public String getEmail(HttpSession session, Model model) {
        String phone = (String) session.getAttribute("phoneNum");
        session.removeAttribute("phoneNum");

        Member member = memberService.findByPhone(phone);
        model.addAttribute("email", member.getIdentifier());
        return "/sign-in/findEmailResult";
    }


    /* 비밀번호 찾기 */

    @RequestMapping("/findPwd") // 인증할 이메일 입력 -> Ajax 검증 후, 이메일 전송(certKey 포함 링크) -> 링크 클릭시 비밀번호 변경 페이지로 이동
    public String findPwd() {
        return "/sign-in/findPwdForm";
    }

    @ResponseBody
    @RequestMapping("/findPwd/checkEmail")
    public String findPwd2(@RequestParam String email) {
        Member member = memberService.findByIdentifier(email);
        if (member != null) {
            return "exist";
        }
        return "not exist";
    }

    @ResponseBody
    @RequestMapping("/findPwd/sendMail") // 메일 전송(Ajax 비동기)
    public String findPwd3(@RequestParam String email) {
        Map<String, String> result = mailService.sendCertMail(email);
        return result.get("resultCode");
    }

    @RequestMapping("/findPwd/certificationEmail") // 메일 속 인증 링크가 연결되는 페이지
    public String certificationEmail(HttpSession session, @RequestParam Long memNo, @RequestParam String certKey) {
        Member member = memberService.findByMemNo(memNo);
        if (passwordEncoder.matches(certKey, member.getCertKey())) {
            session.setAttribute("memNo", memNo);
            memberService.updateCertKey(memNo); // 인증에 성공하면 certKey는 다시 갱신시키기
            return "redirect:/findPwd/updatePwd"; //패스워드 변경 페이지로
        } else {
            return "/error"; // 잘못된 접근입니다.
        }
    }

    @RequestMapping("/findPwd/updatePwd")
    public String updatePwdForm() {
        return "/sign-in/updatePwd";
    }

    @ResponseBody
    @RequestMapping("/findPwd/updatePwdProc") // 비밀번호 변경 후 결과 알려주기 (js에서 alert로 알려준 뒤 로그인 페이지로 redirect)
    public String updatePwdProc(HttpSession session, @RequestParam String pwd) {
        Long memNo = (Long) session.getAttribute("memNo");
        session.removeAttribute("memNo");
        memberService.updatePwd(memNo, pwd);
        return "";
    }

}
