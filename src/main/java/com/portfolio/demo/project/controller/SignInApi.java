package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MailService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.RememberMeTokenService;
import com.portfolio.demo.project.util.KakaoLoginApiUtil;
import com.portfolio.demo.project.util.KakaoProfileApiUtil;
import com.portfolio.demo.project.util.NaverLoginApiUtil;
import com.portfolio.demo.project.util.NaverProfileApiUtil;
import com.portfolio.demo.project.vo.MemberVO;
import com.portfolio.demo.project.vo.SocialProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Map;
import java.util.ResourceBundle;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SignInApi {

    private final MemberService memberService;

    private final RememberMeTokenService rememberMeTokenService;

    private final SecureRandom random;

    private final PasswordEncoder passwordEncoder;

    private final NaverLoginApiUtil naverLoginApi;

    private final NaverProfileApiUtil naverProfileApiUtil;

    private final KakaoLoginApiUtil kakaoLoginApiUtil;

    private final KakaoProfileApiUtil kakaoProfileApiUtil;

    private final MailService mailService;

    private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");

    /* Naver, Kakao Login API 관련 */
//    @RequestMapping("/sign-in")
//    public String signInPage(Model model, HttpSession session, Principal principal) throws UnsupportedEncodingException {
//
//        if (principal != null) {
//            log.info("현재 principal 정보 : " + principal.toString());
//
//            Member member = memberService.findByIdentifier(principal.getName());
//
//            Authentication auth = memberService.getAuthentication(member);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            MemberVO memberVO = MemberVO.create(member);
//            session.setAttribute("member", memberVO);
//
//            return "redirect:/";
//        }
//
//        /* 네이버 */
//        String NAVER_CLIENT_ID = resourceBundle.getString("naverClientId");
//        String naverCallBackURI = URLEncoder.encode("http://localhost:8080/sign-in/naver/oauth2", "utf-8");
//        String naverApiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
//        String naverState = new BigInteger(130, random).toString();
//
//        naverApiURL += String.format("&client_id=%s&redirect_uri=%s&state=%s", NAVER_CLIENT_ID, naverCallBackURI, naverState);
//        session.setAttribute("naverState", naverState);
//        model.addAttribute("naverLoginUrl", naverApiURL);
//
//        /* 카카오 */
//        String KAKAO_CLIENT_ID = resourceBundle.getString("kakaoClientId");
//        String kakaoCallBackUrl = URLEncoder.encode("http://localhost:8080/sign-in/kakao/oauth2", "utf-8");
//        String kakaoApiURL = "https://kauth.kakao.com/oauth/authorize?response_type=code";
//        String kakaoState = new BigInteger(130, random).toString();
//
//        kakaoApiURL += String.format("&client_id=%s&redirect_uri=%s&state=%s", KAKAO_CLIENT_ID, kakaoCallBackUrl, kakaoState);
//        session.setAttribute("kakaoState", kakaoState);
//        model.addAttribute("kakaoLoginUrl", kakaoApiURL);
//
//        if (model.containsAttribute("oauthMsg")) {
//            String oauthMsg = (String) model.getAttribute("oauthMsg");
//            model.addAttribute("oauthMsg", oauthMsg);
//        }
//
//        log.info("access login page");
//        return "sign-in/sign-inForm";
//    }




    @PostMapping("/sign-in/check")
    @ResponseBody
    public ResponseEntity<String> checkInputParams(@RequestBody Member member) {
//        String identifier = map.get("identifier");
//        String password = map.get("password");
        Member foundMember = memberService.findByIdentifier(member.getIdentifier());

        String msg = "";
        if (foundMember != null) { // 해당 이메일의 회원이 존재할 경우
            boolean matched = passwordEncoder.matches(member.getPassword(), foundMember.getPassword());

            log.info("회원이 입력한 값과의 일치 관계 : {}", matched);

            if (matched) { // 해당 회원의 비밀번호와 일치할 경우
                if (foundMember.getCertification().equals("Y")) { // 인증된 회원인 경우
                    msg = "matched";
                } else { // 인증되지 않은 회원인 경우
                    msg = "not certified";
                }
            } else { // 해당 회원의 비밀번호와 일치하지 않을 경우
                msg = "didn't matching";
            }
        } else { // 해당 이메일의 회원이 존재하지 않을 경우
            msg = "not user";
        }

        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    // 인증 이메일을 다시 받고자 할 때 작동
    @ResponseBody
    @PostMapping("/certMail")
    public Map<String, String> sendCertMail(String email) {
        return mailService.sendGreetingMail(email);
    }

}
