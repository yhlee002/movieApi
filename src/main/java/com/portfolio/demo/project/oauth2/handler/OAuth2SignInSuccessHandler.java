package com.portfolio.demo.project.oauth2.handler;

import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.oauth2.CustomOAuth2User;
import com.portfolio.demo.project.service.LoginLogService;
import com.portfolio.demo.project.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        if (!MemberRole.ROLE_GUEST.equals(oAuth2User.getRole())) { // if (member != null) {
            String targetUrl = determineTargetUrl(request, response, authentication);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            request.getSession().setAttribute("oauthUser", oAuth2User);
            getRedirectStrategy().sendRedirect(request, response, "http://" + HOST + ":8080/api/sign-up/oauth2");
        }

//        MemberParam member = memberService.findByIdentifierAndProvider(oAuth2User.getIdentifier(), oAuth2User.getProvider());
//        if (!memberService.findByIdentifierAndProvider(oAuth2User.getIdentifier())) {
//            // 유저 정보가 없으면 회원가입 화면으로 리다이렉트
//            String redirectUrl = "http://your-vue-app.com/signup";
//            getRedirectStrategy().sendRedirect(request, response, "http://" + HOST);
//        } else {
//            // 유저 정보가 있으면 기본 성공 URL로 리다이렉트
//            super.onAuthenticationSuccess(request, response, authentication);
//        }

        /* 멤버 정보 로드 */
//        MemberParam memberParam = null;
//
//        if (MemberRole.ROLE_GUEST == oAuth2User.getRole()) {
////            HttpSession session = request.getSession();
////            session.setAttribute("profile", );
////            response.sendRedirect("/sign-in/"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
//        } else {
//            memberParam = memberService.findByIdentifier(oAuth2User.getIdentifier()); // .getUsername()
//
//            if (memberParam != null) {
//                LoginLogParam logParam = LoginLogParam.builder()
//                        .ip(request.getRemoteAddr())
//                        .memberNo(memberParam.getMemNo())
//                        .memberIdentifier(memberParam.getIdentifier())
//                        .result(LoginResult.SUCCESS)
//                        .build();
//                loginLogService.saveLog(logParam);
//
//                response.setStatus(HttpServletResponse.SC_OK);
//                response.setCharacterEncoding("UTF-8");
//                response.setContentType("text/html; charset=UTF-8");
//
//                JsonObject outputData = new JsonObject();
//                outputData.addProperty("memNo", memberParam.getMemNo());
//                outputData.addProperty("identifier", memberParam.getIdentifier());
//                outputData.addProperty("name", memberParam.getName());
//                outputData.addProperty("profileImage", memberParam.getProfileImage());
//                outputData.addProperty("phone", memberParam.getPhone());
//                outputData.addProperty("regDate", memberParam.getRegDate());
//                outputData.addProperty("role", memberParam.getRole().toString());
//                outputData.addProperty("provider", memberParam.getProvider().toString());
//
//                PrintWriter writer = response.getWriter();
//                writer.write(outputData.toString());
//                writer.flush();
//                writer.close();
//            }
//        }
//
//        HttpSession session = request.getSession();
//        session.setAttribute("member", memberParam);
//
//        SetRedirectStrategyUrl(request, response, authentication);
    }

    public void SetRedirectStrategyUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        SavedRequest savedRequest = requestCache.getRequest(request, response);
//        log.info("saveRequest 정보 : {}", savedRequest.toString());
        log.info("OAuth2SignSuccessHanlder 리다이렉션 발생(경로: {})", "http://" + HOST);
        redirectStrategy.sendRedirect(request, response, "http://" + HOST); // savedRequest.getRedirectUrl()
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 로직을 통해 적절한 리다이렉트 URL 결정
        return "http://" + HOST;
    }
}
