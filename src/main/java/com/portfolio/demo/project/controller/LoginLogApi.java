package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.dto.loginlog.LoginLogPagenationParam;
import com.portfolio.demo.project.dto.loginlog.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.service.LoginLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LoginLog", description = "로그인 로그 관련 API 입니다.")
@RestController
@RequiredArgsConstructor
public class LoginLogApi {

    private final LoginLogService loginLogService;

    @GetMapping("/loginlogs")
    public ResponseEntity<Result<LoginLogPagenationParam>> getAllLogs(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                            @RequestParam(name = "memNo", required = false) Long memNo,
                                                                            @RequestParam(name = "ip", required = false) String ip,
                                                                            @RequestParam(name = "result", required = false) LoginResult result) {
        LoginLogPagenationParam param = new LoginLogPagenationParam();
        if (memNo == null && ip == null && result == null) {
            param = loginLogService.findAll(page, size);
        } else if (memNo != null) {
            param = loginLogService.findAllByMember(page, size, MemberParam.builder().memNo(memNo).build());
        } else if (ip != null) {
            param = loginLogService.findAllByIp(page, size, ip);
        } else if (result != null) {
            param = loginLogService.findAllByResult(page, size, result);
        }

        return ResponseEntity.ok(new Result<>(param));
    }

    @GetMapping("/loginlogs/{id}")
    public ResponseEntity<Result<LoginLogParam>> getLog(@PathVariable Long id) {
        LoginLogParam param = loginLogService.findById(id);

        return ResponseEntity.ok(new Result<>(param));
    }
}
