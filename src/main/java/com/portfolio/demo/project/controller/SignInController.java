package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.OauthMember;
import com.portfolio.demo.project.pojo.MemberPojo;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.OauthMemberService;
import com.portfolio.demo.project.security.OauthUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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

        model.addAttribute("oauth_message", "");

        log.info("access login page");
        return "sign-in/sign-inForm";
    }

    @Autowired
    NaverLoginApiUtil naverLoginApi;

    @Autowired
    NaverProfileApiUtil naverProfileApiUtil;

    @Autowired
    OauthUserService oauthUserService;

    @Autowired
    OauthMemberService oauthMemberService;

    @RequestMapping("/sign-in/naver/oauth2")
    public String naverOauth(HttpSession session, HttpServletRequest request) throws UnsupportedEncodingException, ParseException {

        Map<String, String> res = naverLoginApi.getTokens(request);
        String access_token = res.get("access_token");
        String refresh_token = res.get("refresh_token");

        session.setAttribute("currentAT", access_token);
        session.setAttribute("currentRT", refresh_token);

        /* access token을 사용해 사용자 프로필 조회 api 호출 */
        Map<String, String> profile = naverProfileApiUtil.getProfile(access_token); // Map으로 사용자 데이터 받기
        System.out.println("profile : " + profile);

        String oauthInfo = memberService.getNaverOAuthInfo(profile);
        log.info("oauthInfo : " + oauthInfo);

        if (oauthInfo.equals("ROLE_USER")) { // 네아로 회원이라면 ROLE_USER 반환
            log.info("네아로를 통해 가입되어있는 회원입니다.");
            /* 로그인 절차 */
            Authentication auth = oauthUserService.getAuthByOAuthInfo(profile);
            SecurityContextHolder.getContext().setAuthentication(auth);

            OauthMember oMember = oauthMemberService.findOauthMemberByUniqueId(profile.get("id"));
            log.info("Sign-in User; user uniqueId : " + oMember.getUniqueId() + ", name : " + oMember.getNickname());
            MemberPojo memberPojo = new MemberPojo(oMember);

            session.setAttribute("member", memberPojo);

            return "redirect:/";

//        } else if (oauthInfo.equals("not naver user")) {
//            log.info("비밀번호를 통한 로그인이 필요한 회원입니다.");
//            session.setAttribute("oauth_message", "not naver user");
//
//            return "redirect:/sign-in";

        } else { /* 미가입자 */
            log.info("not user");
            session.setAttribute("profile", profile);
            session.setAttribute("oauth_message", "not user");

            return "redirect:/sign-in";
        }

    }


}

/* 네아로 갱신 핸들러 필요(안만들 경우 한시간만 로그인 유지) */
