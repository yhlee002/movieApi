package com.portfolio.demo.project.security.handler;

import com.portfolio.demo.project.dto.loginlog.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.service.LoginLogService;
import com.portfolio.demo.project.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class SignInFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private MemberService memberService;

    @Autowired
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String memNo = request.getParameter("username");
        log.info("로그인 실패(시도한 identifier: {})", memNo);

        if (memNo != null) return;

        MemberParam member = memberService.findByMemNo(Long.getLong(memNo));
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
