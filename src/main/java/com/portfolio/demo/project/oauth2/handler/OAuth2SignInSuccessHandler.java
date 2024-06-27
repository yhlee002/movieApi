package com.portfolio.demo.project.oauth2.handler;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.dto.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.oauth2.CustomOAuth2User;
import com.portfolio.demo.project.service.LoginLogService;
import com.portfolio.demo.project.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class OAuth2SignInSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginLogService loginLogService;

    @Value("${web.server.host}")
    private String HOST;

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        log.info("로그인 성공(시도한 identifier: {}, authenticated: {})", oAuth2User.getIdentifier(), authentication.getAuthorities());

        /* 멤버 정보 로드 */
        MemberParam memberParam = null;

        if (MemberRole.ROLE_GUEST == oAuth2User.getRole()) {
//            HttpSession session = request.getSession();
//            session.setAttribute("profile", );
//            response.sendRedirect("/sign-in/"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
        } else {
            memberParam = memberService.findByIdentifier(oAuth2User.getIdentifier()); // .getUsername()

            if (memberParam != null) {
                LoginLogParam logParam = LoginLogParam.builder()
                        .ip(request.getRemoteAddr())
                        .memberNo(memberParam.getMemNo())
                        .memberIdentifier(memberParam.getIdentifier())
                        .result(LoginResult.SUCCESS)
                        .build();
                loginLogService.saveLog(logParam);

                response.setStatus(HttpServletResponse.SC_OK);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");

                JsonObject outputData = new JsonObject();
                outputData.addProperty("memNo", memberParam.getMemNo());
                outputData.addProperty("identifier", memberParam.getIdentifier());
                outputData.addProperty("name", memberParam.getName());
                outputData.addProperty("profileImage", memberParam.getProfileImage());
                outputData.addProperty("phone", memberParam.getPhone());
                outputData.addProperty("regDate", memberParam.getRegDate());
                outputData.addProperty("role", memberParam.getRole().toString());
                outputData.addProperty("provider", memberParam.getProvider().toString());

                PrintWriter writer = response.getWriter();
                writer.write(outputData.toString());
                writer.flush();
                writer.close();
            }
        }

        HttpSession session = request.getSession();
        session.setAttribute("member", memberParam);

        SetRedirectStrategyUrl(request, response, authentication);
    }

    public void SetRedirectStrategyUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        SavedRequest savedRequest = requestCache.getRequest(request, response);
//        log.info("saveRequest 정보 : {}", savedRequest.toString());
        log.info("redirect uri : {}", "http://" + HOST);
        redirectStrategy.sendRedirect(request, response, "http://" + HOST); // savedRequest.getRedirectUrl()
    }
}
