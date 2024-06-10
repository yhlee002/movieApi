package com.portfolio.demo.project.security;

import com.portfolio.demo.project.dto.LoginLogParam;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.service.LoginLogService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.dto.member.MemberParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class SignInSuccessHandler implements AuthenticationSuccessHandler {
    /**
     * 인증 권한이 필요한 페이지에 접근하게 되면, 로그인 화면을 띄우기 전에 필요한 정보들을 세션에 저장
     * RequestCache(I) : spring security에서 제공하는 사용자의 요청을 저장하고 꺼낼 수 있음
     * RequestCache 객체를 이용해 사용자 요청 정보들이 들어 있는 SavedRequest 클래스 객체를 세션에 저장 가능
     * 이 SavedRequest 객체를 가져와서 로그인 화면을 보기 전에 방문했던 URL 정보를 가져와 다시 해당 주소로 리다이렉트 해줄 수 있음
     */
    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private static final String defaultUrl = "/";

    @Autowired // 제거 임시 보류
    private MemberService memberService;

    private final LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login Success - principal : " + authentication.getPrincipal() + ", authenticated : " + authentication.getAuthorities());

        System.out.println("authentication.getPrincipal() : "+authentication.getPrincipal().toString());

        String principal = (String) authentication.getPrincipal();

        /* 멤버 정보 로드 */
        MemberParam memberParam = null;

        if (principal != null) {
            MemberParam member = memberService.findByIdentifier(principal); // .getUsername()

            if (member != null) {
                LoginLogParam logParam = LoginLogParam.builder()
                        .ip(request.getRemoteAddr())
                        .memberNo(member.getMemNo())
                        .memberIdentifier(member.getIdentifier())
                        .result(LoginResult.SUCCESS)
//                        .regDate(LocalDateTime::now)
                        .build();
                loginLogService.saveLog(logParam);

                memberParam = member;

                response.setStatus(HttpServletResponse.SC_OK);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");

                Map<String, Object> outputData = new HashMap<>();
                outputData.put("memNo", memberParam.getMemNo());
                outputData.put("identifier", memberParam.getIdentifier());
                outputData.put("name", memberParam.getName());
                outputData.put("profileImage", memberParam.getProfileImage());
                outputData.put("phone", memberParam.getPhone());
                outputData.put("regDate", memberParam.getRegDate());
                outputData.put("role", memberParam.getRole().toString());
                outputData.put("provider", memberParam.getProvider());


                PrintWriter writer = response.getWriter();
                writer.write(outputData.toString());
                writer.flush();
                writer.close();
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("principal", principal);
        session.setAttribute("member", memberParam); // 없는 경우 null -> SignInController에서 담음

//        SetRedirectStrategyUrl(request, response, authentication);
    }

    public void SetRedirectStrategyUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest == null) { // 인증 권한이 필요한 url을 통해 로그인 페이지에 접근한 것이 아니라면(세션에 저장된 정보가 없다면)
            log.info("saveRequest is null");
            redirectStrategy.sendRedirect(request, response, defaultUrl);
        } else { // 인증 권한이 필요한 url을 통해 로그인 페이지에 접근한 것이라면
            log.info("saveRequest : "+savedRequest.toString());
            redirectStrategy.sendRedirect(request, response, savedRequest.getRedirectUrl());
        }
    }


}
