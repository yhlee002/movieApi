package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    private PasswordEncoder passwordEncoder;

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member findByName(String name) {
        return memberRepository.findByName(name);
    }

    public Member findByPhone(String phone){
        return memberRepository.findByPhone(phone);
    }

    public void saveMember(Member member) {
        Member.builder().memNo(null)
                .email(member.getEmail())
                .name(member.getName())
                .password(passwordEncoder.encode(member.getPassword()))
                .phone(member.getPhone())
                .regDt(member.getRegDt());

        memberRepository.save(member);
    }

    @PostConstruct
    private void created() {
        log.info("User Service 인스턴스 생성");
    }
}
