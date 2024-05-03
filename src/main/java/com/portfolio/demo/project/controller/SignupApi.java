package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.CertKeyService;
import com.portfolio.demo.project.service.MailService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.PhoneMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sign-up")
public class SignupApi {

    private final MemberService memberService;

    private final PhoneMessageService phoneMessageService;

    private final MailService mailService;

    private final CertKeyService certKeyService;

    private final PasswordEncoder passwordEncoder;

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
    @GetMapping("/nameCk")
    public Integer nameCkProc(@RequestParam String name) {
        Boolean exists = memberService.validateDuplicationName(name);
        if (!exists) log.info("[name 사용 가능 여부 확인] member 정보 존재하지 않음(확인한 name : {})", name);
        else log.info("[name 사용 가능 여부 확인] member 정보 존재(확인한 name : {})", name);
        return exists ? 1 : 0;
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
    public String phoneCkPage(HttpSession session, @RequestParam String phone, @RequestParam(required = false) String provider) {

        Map<String, String> resultMap = phoneMessageService.sendCertificationMessage(phone);
        String result = resultMap.get("result");
        String phoneAuthKey = resultMap.get("certKey");

        if (result.equals("success")) {
            log.info("phoneAuthKey 인코딩 전 값 : " + phoneAuthKey);
            session.setAttribute("phoneAuthCertKey", passwordEncoder.encode(phoneAuthKey));
            session.setAttribute("phoneNum", phone);
        }
        return "sign-up/phoneCkAuth";
    }

    @ResponseBody
    @RequestMapping(value = "/phoneCkProc2", method = RequestMethod.POST) // 인증키 일치 여부 확인 페이지
    public Map<String, String> phoneCkProc(HttpSession session, @RequestParam String certKey) {
        Map<String, String> result = new HashMap<>();
        String phoneAuthCertKey = (String) session.getAttribute("phoneAuthCertKey");
        if (passwordEncoder.matches(certKey, phoneAuthCertKey)) {
            result.put("resultCode", "success");
            result.put("phoneNum", (String) session.getAttribute("phoneNum"));
            session.removeAttribute("phoneNum");
        } else {
            result.put("resultCode", "fail");
        }
        return result;
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
        /* 여러번 클릭시 멤버는 여러번 생성되었는데 이메일이 가지 않았을 경우를 대비해
           이메일로 찾는 과정 + identifier 컬럼을 유니크 키로 사용 */
        Member tempMember = memberService.findByIdentifier(email);
        if (tempMember != null) {
            memberService.deleteMember(tempMember.getMemNo());
        }
        memberService.saveMember(member);
        Map<String, String> result = mailService.sendGreetingMail(member.getIdentifier());

        /* DB에 저장된 데이터 로드 */
        Member createdMember = memberService.findByIdentifier(email);
        log.info("생성된 유저 : " + createdMember.toString());

        if (result.get("resultCode") == "success") {
            return createdMember.getMemNo();
        } else {
            memberService.deleteMember(createdMember.getMemNo());
            return -1L;
        }

    }

    @ResponseBody
    @RequestMapping(value = "/sign-up-processor_oauth", method = RequestMethod.POST)
    public Long signUpProc_o(HttpSession session, @RequestParam String id, @RequestParam String name, @RequestParam String phone, @RequestParam String provider) {
        Map<String, String> profile = (Map<String, String>) session.getAttribute("profile");
        log.info("profile : " + profile);

        String profileImage = profile.get("profile_image");
        if (profileImage != null) {
            profileImage = profileImage.replace("\\", "");
        }

        // id, name, phone, provider, profileImage
        memberService.saveOauthMember(
                Member.builder()
                        .identifier(id)
                        .password("")
                        .name(name)
                        .phone(phone)
                        .provider(provider)
                        .profileImage(profileImage)
                        .role("ROLE_USER")
                        .certKey(null)
                        .certification("Y")
                        .build()
        );

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
