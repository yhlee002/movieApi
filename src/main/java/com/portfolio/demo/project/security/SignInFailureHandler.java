package com.portfolio.demo.project.security;

import com.portfolio.demo.project.dto.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.service.LoginLogService;
import com.portfolio.demo.project.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class SignInFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private MemberService memberService;

    private final LoginLogService loginLogService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String memNo = request.getParameter("username");

        MemberParam member = memberService.findByMemNo(Long.getLong(memNo));
        if (member != null) {
            LoginLogParam logParam = LoginLogParam.builder()
                    .ip(request.getRemoteAddr())
                    .memberNo(member.getMemNo())
                    .memberIdentifier(member.getIdentifier())
                    .result(LoginResult.FAILURE)
//                        .regDate(LocalDateTime::now)
                    .build();
            loginLogService.saveLog(logParam);
        }
    }
}
