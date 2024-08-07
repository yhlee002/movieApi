package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.loginlog.LoginLogPagenationParam;
import com.portfolio.demo.project.dto.loginlog.LoginLogParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.entity.loginlog.LoginLog;
import com.portfolio.demo.project.entity.loginlog.LoginResult;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.LoginLogRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class LoginLogService {

    private final LoginLogRepository loginLogRepository;

    private final MemberRepository memberRepository;

    public LoginLogPagenationParam findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<LoginLog> logs = loginLogRepository.findAll(pageable);

        return new LoginLogPagenationParam(logs);
    }

    public LoginLogPagenationParam findAllByMember(int page, int size, MemberParam param) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<LoginLog> logs = loginLogRepository.findByMemNo(param.getMemNo(), pageable);

        return new LoginLogPagenationParam(logs);
    }

    public LoginLogPagenationParam findAllByIp(int page, int size, String ip) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<LoginLog> logs = loginLogRepository.findByIp(ip, pageable);

        return new LoginLogPagenationParam(logs);
    }

    public LoginLogPagenationParam findAllByResult(int page, int size, LoginResult result) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<LoginLog> logs = loginLogRepository.findByResult(result, pageable);

        return new LoginLogPagenationParam(logs);
    }

    public LoginLogParam findById(Long id) {
        LoginLog log = loginLogRepository.findById(id).orElse(null);
        if (log == null) {
            return null;
        } else {
            return LoginLogParam.create(log);
        }
    }

    public Long saveLog(LoginLogParam param) {
        Member member = memberRepository.findById(param.getMemberNo()).orElse(null);

        if (member == null) throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo: " + param.getMemberNo());
        LoginLog log = LoginLog.builder()
                .id(param.getId())
                .ip(param.getIp())
                .member(member)
                .result(param.getResult())
                .build();

        loginLogRepository.save(log);

        return log.getId();
    }

    public void deleteLog(Long id) {
        LoginLog log = loginLogRepository.findById(id).orElse(null);
        loginLogRepository.delete(log);
    }
}
