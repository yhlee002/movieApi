package com.portfolio.demo.project.dto;

import com.portfolio.demo.project.entity.loginlog.LoginLog;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginLogParam {
    private Long id;
    private Long memberNo;
    private String memberIdentifier;
    private String regDate;
    private String ip;
    private LoginResult result;

    public static LoginLogParam create(LoginLog log) {
        return LoginLogParam.builder()
                .id(log.getId())
                .memberNo(log.getMember().getMemNo())
                .memberIdentifier(log.getMember().getIdentifier())
                .regDate(log.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .ip(log.getIp())
                .result(log.getResult())
                .build();
    }
}
