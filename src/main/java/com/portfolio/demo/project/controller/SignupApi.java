package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.util.CertUtil;
import com.portfolio.demo.project.vo.MemberVO;
import com.portfolio.demo.project.vo.SocialProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sign-up")
public class SignupApi {

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final CertUtil certUtil;

    @GetMapping("/emailCk")
    public ResponseEntity<MemberVO> findMemberByEmail(@RequestParam String email) {
        Member member = memberService.findByIdentifier(email);

        if (member != null) {
            log.info("[이메일 조회] 회원 식별번호 : {}, 이메일 : {}", member.getMemNo(), email);
            return new ResponseEntity<>(MemberVO.create(member), HttpStatus.OK);
        } else {
            log.info("[이메일 조회] 조회된 회원 정보가 없습니다. (조회 이메일 : {})", email);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping("/nameCk")
    public Integer validateName(@RequestParam String name) {
        Boolean exists = memberService.validateDuplicationName(name);
        if (!exists) log.info("[name 사용 가능 여부 확인] member 정보 존재하지 않음(확인한 name : {})", name);
        else log.info("[name 사용 가능 여부 확인] member 정보 존재(확인한 name : {})", name);
        return exists ? 1 : 0;
    }

    @GetMapping("/phoneCk") // /{memType}
    public Member findMemberByPhone(@RequestParam String phone) { // , @PathVariable String memType
        log.info("들어온 phone number : {}", phone);

        return memberService.findByPhone(phone);
    }

//    @RequestMapping(value = "/phoneCkProc", method = RequestMethod.GET) // 인증키를 받을 핸드폰 번호 입력 페이지
//    public String phoneCkPage(HttpSession session, @RequestParam String phone, @RequestParam(required = false) String provider) {
//
//        Map<String, String> resultMap = phoneMessageService.sendCertificationMessage(phone);
//        String result = resultMap.get("result");
//        String phoneAuthKey = resultMap.get("certKey");
//
//        if (result.equals("success")) {
//            log.info("phoneAuthKey 인코딩 전 값 : " + phoneAuthKey);
//            session.setAttribute("phoneAuthCertKey", passwordEncoder.encode(phoneAuthKey));
//            session.setAttribute("phoneNum", phone);
//        }
//        return "sign-up/phoneCkAuth";
//    }

    @PostMapping("/phoneCkProc2") // 인증키 일치 여부 확인 페이지
    public Map<String, String> validatePhoneCertKey(HttpSession session, @RequestParam String certKey) {
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

    // 회원 가입 성공 및 이메일 전송
//    @RequestMapping(value = "/success", method = RequestMethod.GET)
//    public String signUpSuccessPage(Model m, @RequestParam(required = false) Long memNo, @RequestParam(required = false) Long oauthMemNo) {
//        if (memNo != null && oauthMemNo == null) {
//            Member member = memberService.findByMemNo(memNo);
//            if (member != null) {
//                m.addAttribute("member", member);
//            }
//
//            return "sign-up/successPage";
//        } else {
//
//            return "redirect:/";
//        }
//    }

    // 가입 이메일 인증
    @RequestMapping(value = "/certificationEmail", method = RequestMethod.GET)
    public ResponseEntity<MemberVO> validateEmailByCertKey(@RequestParam Long memNo, @RequestParam String certKey) {
        // authKey는 해싱된 상태로 링크에 파라미터로 추가되어 이메일 전송됨
        // DB에 저장된 해당 회원의 certKey와 일치하는지 확인하고 정보 수정
        Member member = memberService.findByMemNo(memNo);
        Boolean validated = certUtil.validateCertKey(member, certKey);

        if (validated) {
            member = certUtil.changeCertStatus(member);
            memberService.saveMember(member);
            return new ResponseEntity<>(MemberVO.create(member), HttpStatus.OK);
        } else throw new IllegalStateException("인증 정보가 일치하지 않습니다.");
//        // 인증된 경우 인증 완료 페이지 이동
//        if (checkVal) {
//            return "sign-up/certEmailSuccess";
//        } else {
//            return "sign-up/certEmailFail";
//        }
    }

    /* 네아로 api, 카카오로그인 api로 접근한 회원가입 */
//    @RequestMapping(value = "/oauthMem", method = RequestMethod.GET)
//    public String oauthMem(HttpSession session, Model model) {
//        session.removeAttribute("oauth_message");
//
//        Map<String, String> profile = (Map<String, String>) session.getAttribute("profile");
//        String id = (String) profile.get("id");
//        String nickname = (String) profile.get("nickname");
//
//        model.addAttribute("id", id);
//        model.addAttribute("nickname", nickname);
//
//        return "sign-up/oauthMemSign-upForm"; // email이 없기 때문에 submit되고 나서 넘어온 값에 email이 null이면 oauthMem으로 가입되게 해야함
//    }

    @GetMapping("/oauthMem")
    public ResponseEntity<SocialProfile> getOauthProfile(HttpSession session) {
        session.removeAttribute("oauth_message");

        SocialProfile profile = (SocialProfile) session.getAttribute("profile");
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }
}
