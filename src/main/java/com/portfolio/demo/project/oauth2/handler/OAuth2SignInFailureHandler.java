package com.portfolio.demo.project.oauth2.handler;

import com.portfolio.demo.project.dto.loginlog.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.service.LoginLogService;
import com.portfolio.demo.project.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Slf4j
@RequiredArgsConstructor
public class OAuth2SignInFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        String identifier = request.getParameter("username");
        log.info("로그인 실패(시도한 identifier: {})", identifier);

        if (identifier != null) return;

        MemberParam member = memberService.findByIdentifier(identifier);
        if (member != null) {
            LoginLogParam logParam = LoginLogParam.builder()
                    .ip(request.getRemoteAddr())
                    .memberNo(member.getMemNo())
                    .memberIdentifier(member.getIdentifier())
                    .result(LoginResult.FAILURE)
                    .build();
            loginLogService.saveLog(logParam);
        }
    }
}
