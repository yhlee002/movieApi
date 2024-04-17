package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.MailService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.util.KakaoLoginApiUtil;
import com.portfolio.demo.project.util.KakaoProfileApiUtil;
import com.portfolio.demo.project.util.NaverLoginApiUtil;
import com.portfolio.demo.project.util.NaverProfileApiUtil;
import com.portfolio.demo.project.vo.MemberVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
public class SignInController {

    private final MemberService memberService;

    private final SecureRandom random;

    private final PasswordEncoder passwordEncoder;

    private final NaverLoginApiUtil naverLoginApi;

    private final NaverProfileApiUtil naverProfileApiUtil;

    private final KakaoLoginApiUtil kakaoLoginApiUtil;

    private final KakaoProfileApiUtil kakaoProfileApiUtil;

    private final MailService mailService;

    private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");

    /* Naver, Kakao Login API 관련 */
    @RequestMapping("/sign-in")
    public String signInPage(Model model, HttpSession session, Principal principal) throws UnsupportedEncodingException {

        if (principal != null) {
            log.info("현재 principal 정보 : " + principal.toString());

            Member member = memberService.findByIdentifier(principal.getName());

            Authentication auth = memberService.getAuthentication(member);
            SecurityContextHolder.getContext().setAuthentication(auth);

            MemberVO memberVO = new MemberVO(member);
            session.setAttribute("member", memberVO);

            return "redirect:/";
        }

        /* 네이버 */
        String NAVER_CLIENT_ID = resourceBundle.getString("naverClientId");
        String naverCallBackURI = URLEncoder.encode("http://3.36.203.4:8080/sign-in/naver/oauth2", "utf-8");
        String naverApiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
        String naverState = new BigInteger(130, random).toString();

        naverApiURL += String.format("&client_id=%s&redirect_uri=%s&state=%s", NAVER_CLIENT_ID, naverCallBackURI, naverState);
        session.setAttribute("naverState", naverState);
        model.addAttribute("naverLoginUrl", naverApiURL);

        /* 카카오 */
        String KAKAO_CLIENT_ID = resourceBundle.getString("kakaoClientId");
        String kakaoCallBackUrl = URLEncoder.encode("http://3.36.203.4:8080/sign-in/kakao/oauth2", "utf-8");
        String kakaoApiURL = "https://kauth.kakao.com/oauth/authorize?response_type=code";
        String kakaoState = new BigInteger(130, random).toString();

        kakaoApiURL += String.format("&client_id=%s&redirect_uri=%s&state=%s", KAKAO_CLIENT_ID, kakaoCallBackUrl, kakaoState);
        session.setAttribute("kakaoState", kakaoState);
        model.addAttribute("kakaoLoginUrl", kakaoApiURL);

        if (model.containsAttribute("oauth_message")) {
            String oauth_message = (String) model.getAttribute("oauth_message");
            model.addAttribute("oauth_message", oauth_message);
        }

        log.info("access login page");
        return "sign-in/sign-inForm";
    }

    @RequestMapping("/sign-in/naver/oauth2")
    public String naverOauth(HttpSession session, HttpServletRequest request, RedirectAttributes rttr) throws UnsupportedEncodingException, ParseException {

        Map<String, String> res = naverLoginApi.getTokens(request);
        String access_token = res.get("access_token");
        String refresh_token = res.get("refresh_token");

        session.setAttribute("naverCurrentAT", access_token);
        session.setAttribute("naverCurrentRT", refresh_token);

        /* access token을 사용해 사용자 프로필 조회 api 호출 */
        Map<String, String> profile = naverProfileApiUtil.getProfile(access_token); // Map으로 사용자 데이터 받기
        log.info("profile : " + profile);

        /* 해당 프로필과 일치하는 회원 정보가 있는지 조회 후, 있다면 role 값(ROLE_USER) 반환 */
        Member member = memberService.findByProfile(profile.get("id"), "naver");

        if (member != null) { // info.getRole().equals("ROLE_USER")
            log.info("회원정보가 존재합니다. \n회원정보 : " + member.toString());

            if (member.getProvider().equals("naver")) {
                Authentication auth = memberService.getAuthentication(member);
                SecurityContextHolder.getContext().setAuthentication(auth);

                MemberVO memberVO = new MemberVO(member);
                session.setAttribute("member", memberVO);

                return "redirect:/";
            } else { // none
                log.info("conventional user");
                rttr.addFlashAttribute("oauth_message", "conventional user");
                return "redirect:/sign-in";
            }

        } else {
            log.info("not user");
            session.setAttribute("profile", profile);
            session.setAttribute("provider", "naver");
            rttr.addFlashAttribute("oauth_message", "not user");

            return "redirect:/sign-in";
        }
    }

    @RequestMapping("/sign-in/kakao/oauth2")
    public String kakaoOauth(HttpSession session, HttpServletRequest request, RedirectAttributes rttr) throws ParseException, UnsupportedEncodingException {
        log.info(request.toString());
        Map<String, String> res = kakaoLoginApiUtil.getTokens(request);
        String access_token = res.get("access_token");
        String refresh_token = res.get("refresh_token");

        session.setAttribute("kakaoCurrentAT", access_token);
        session.setAttribute("kakaoCurrentRT", refresh_token);

        Map<String, String> profile = kakaoProfileApiUtil.getProfile(access_token);
        log.info("profile : " + profile);

        Member member = memberService.findByProfile(profile.get("id"), "kakao");
        if (member != null) {
            log.info("회원정보가 존재합니다. \n회원정보 : " + member.toString());

            if (member.getProvider().equals("kakao")) {
                Authentication auth = memberService.getAuthentication(member);
                SecurityContextHolder.getContext().setAuthentication(auth);

                MemberVO memberVO = new MemberVO(member);
                session.setAttribute("member", memberVO);

                return "redirect:/";
            } else { // none
                log.info("conventional user");
                rttr.addFlashAttribute("oauth_message", "conventional user");
                return "redirect:/sign-in";
            }
        } else { // 필요없는 코드
            log.info("not user");
            session.setAttribute("profile", profile);
            session.setAttribute("provider", "kakao");
            rttr.addFlashAttribute("oauth_message", "not user");
            return "redirect:/sign-in";
        }
    }


    @PostMapping("/sign-in/check")
    @ResponseBody
    public String checkProc(String email, String pwd) {
        Member member = memberService.findByIdentifier(email);
        log.info("회원이 입력한 값과의 일치 관계 : {}", passwordEncoder.matches(pwd, member.getPassword()));
        if (member != null) { // 해당 이메일의 회원이 존재할 경우
            if (passwordEncoder.matches(pwd, member.getPassword())) { // 해당 회원의 비밀번호와 일치할 경우
                if (member.getCertification().equals("Y")) { // 인증된 회원인 경우
                    return "matched";
                } else { // 인증되지 않은 회원인 경우
                    return "not certified";
                }
            } else { // 해당 회원의 비밀번호와 일치하지 않을 경우
                return "didn't matching";
            }
        } else { // 해당 이메일의 회원이 존재하지 않을 경우
            return "not user";
        }
    }

    // 인증 이메일을 다시 받고자 할 때 작동
    @ResponseBody
    @PostMapping("/certMail")
    public Map<String, String> sendCertMail(String email) {
        return mailService.sendGreetingMail(email);
    }

}
