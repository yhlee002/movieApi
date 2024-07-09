package com.portfolio.demo.project.oauth2.handler;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.dto.loginlog.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class OAuth2SignInSuccessHandler extends SimpleUrlAuthenticationSuccessHandler { // implements AuthenticationSuccessHandler {

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

        if (!MemberRole.ROLE_GUEST.equals(oAuth2User.getRole())) {
            MemberParam member = memberService.findByIdentifierAndProvider(oAuth2User.getIdentifier(), oAuth2User.getProvider());

            if (member != null) {
                LoginLogParam logParam = LoginLogParam.builder()
                        .ip(request.getRemoteAddr())
                        .memberNo(member.getMemNo())
                        .memberIdentifier(member.getIdentifier())
                        .result(LoginResult.SUCCESS)
                        .build();
                loginLogService.saveLog(logParam);

                response.setStatus(HttpServletResponse.SC_OK);
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html; charset=UTF-8");

                JsonObject outputData = new JsonObject();
                outputData.addProperty("memNo", member.getMemNo());
                outputData.addProperty("identifier", member.getIdentifier());
                outputData.addProperty("name", member.getName());
                outputData.addProperty("profileImage", member.getProfileImage());
                outputData.addProperty("phone", member.getPhone());
                outputData.addProperty("regDate", member.getRegDate());
                outputData.addProperty("role", member.getRole().toString());
                outputData.addProperty("provider", member.getProvider().toString());

                PrintWriter writer = response.getWriter();
                writer.write(outputData.toString());
                writer.flush();
                writer.close();

                HttpSession session = request.getSession();
                session.setAttribute("member", member);
            }

            String targetUrl = determineTargetUrl(request, response, authentication);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            request.getSession().setAttribute("oauthUser", oAuth2User);
            getRedirectStrategy().sendRedirect(request, response, "http://" + HOST + ":8080/api/members/sign-up/oauth2");
        }
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 로직을 통해 적절한 리다이렉트 URL 결정
        return "http://" + HOST;
    }
}
