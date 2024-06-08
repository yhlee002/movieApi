package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.dto.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoginLogApi {

    private final LoginLogService loginLogService;

    @GetMapping("/loginlogs")
    public ResponseEntity<Result<List<LoginLogParam>>> getAllLogs(@RequestParam int page, @RequestParam int size,
                                                                  @RequestParam(required = false) Long memNo,
                                                                  @RequestParam(required = false) String ip,
                                                                  @RequestParam(required = false) LoginResult result) {
        List<LoginLogParam> params = new ArrayList<>();
        if (memNo == null && ip == null && result == null) {
            params = loginLogService.findAll(page, size);
        } else if (memNo != null) {
            params = loginLogService.findAllByMember(page, size, MemberParam.builder().memNo(memNo).build());
        } else if (ip != null) {
            params = loginLogService.findAllByIp(page, size, ip);
        } else if (result != null) {
            params = loginLogService.findAllByResult(page, size, result);
        }

        return ResponseEntity.ok(new Result<>(params));
    }

    @GetMapping("/loginlog/{id}")
    public ResponseEntity<Result<LoginLogParam>> getLog(@PathVariable Long id) {
        LoginLogParam param = loginLogService.findById(id);

        return ResponseEntity.ok(new Result<>(param));
    }
}
