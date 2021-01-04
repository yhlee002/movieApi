package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.util.NaverLoginApiUtil;
import com.portfolio.demo.project.util.NaverProfileApiUtil;
import com.portfolio.demo.project.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Map;

@Slf4j
@Controller
public class SignInController {

    @Autowired
    MemberService memberService;

    @Autowired
    SecureRandom random;

    @Autowired
    PasswordEncoder passwordEncoder;

    /* Naver, Kakao Login API 관련 */
    @RequestMapping("/sign-in")
    public String signInPage(Model model, HttpSession session) throws UnsupportedEncodingException {

        String CLIENT_ID = "RxgOCy0eNX66Nbp0rWRH";
        String callbackURI = URLEncoder.encode("http://localhost:8080/sign-in/naver/oauth2", "utf-8");
        String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
        String state = new BigInteger(130, random).toString();

        apiURL += String.format("&client_id=%s&redirect_uri=%s&state=%s", CLIENT_ID, callbackURI, state);
        session.setAttribute("state", state);
        model.addAttribute("naverLoginUrl", apiURL);

        if (model.containsAttribute("oauth_message")) {
            String oauth_message = (String) model.getAttribute("oauth_message");
            model.addAttribute("oauth_message", oauth_message);
        }

        log.info("access login page");
        return "sign-in/sign-inForm";
    }

    @Autowired
    NaverLoginApiUtil naverLoginApi;

    @Autowired
    NaverProfileApiUtil naverProfileApiUtil;

    @RequestMapping("/sign-in/naver/oauth2")
    public String naverOauth(HttpSession session, HttpServletRequest request, RedirectAttributes rttr) throws UnsupportedEncodingException, ParseException {

        Map<String, String> res = naverLoginApi.getTokens(request);
        String access_token = res.get("access_token");
        String refresh_token = res.get("refresh_token");

        session.setAttribute("currentAT", access_token);
        session.setAttribute("currentRT", refresh_token);

        /* access token을 사용해 사용자 프로필 조회 api 호출 */
        Map<String, String> profile = naverProfileApiUtil.getProfile(access_token); // Map으로 사용자 데이터 받기
        System.out.println("profile : " + profile);

        /* 해당 프로필과 일치하는 회원 정보가 있는지 조회 후, 있다면 role 값(ROLE_USER) 반환 */
        Member member = memberService.findByProfile(profile.get("id"), "naver");

        if (member != null) { // info.getRole().equals("ROLE_USER")
            log.info("회원정보가 존재합니다. \n회원정보 : " + member.toString());

            if (member.getProvider().equals("kakao")) {
                rttr.addFlashAttribute("oauth_message", "kakao user");

                return "redirect:/sign-in";
            } else if (member.getProvider().equals("naver")) {
                Authentication auth = memberService.getAuthentication(member);
                SecurityContextHolder.getContext().setAuthentication(auth);

                MemberVO memberVO = new MemberVO(member);
                session.setAttribute("member", memberVO);

                log.info("Sign-in User; user identifier : " + member.getIdentifier() + ", name : " + member.getName());

                return "redirect:/";
            } else { // none
                log.info("conventional user");
                rttr.addFlashAttribute("oauth_message", "conventional user");
                return "redirect:/sign-in";
            }

        } else {
            log.info("not user");
            session.setAttribute("profile", profile);
            rttr.addFlashAttribute("oauth_message", "not user");

            return "redirect:/sign-in";
        }
    }

    @RequestMapping("/sign-in/checkProc")
    @ResponseBody
    public String checkProc(String email, String pwd) {
        Member member = memberService.findByIdentifier(email);
        if (member != null) { // 해당 이메일의 회원이 존재할 경우
            if (passwordEncoder.matches(pwd, member.getPassword())) { // 해당 회원의 비밀번호와 일치할 경우
                return "matched";
            } else { // 해당 회원의 비밀번호와 일치하지 않을 경우
                return "didn't matching";
            }
        } else { // 해당 이메일의 회원이 존재하지 않을 경우
            return "not user";
        }
    }

}


/* 네아로 갱신 핸들러 필요(안만들 경우 한시간만 로그인 유지) */
