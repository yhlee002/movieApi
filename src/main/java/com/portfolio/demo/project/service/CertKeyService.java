package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.util.TempKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CertKeyService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TempKey tempKey;

    public Boolean CheckCertInfo(Long memNo, String certKey) {

        Boolean match = isMatching(memNo, certKey);
        if (match) {
            changeCertInfo(memberRepository.findByMemNo(memNo));
            return true;
        } else {
            return false;

        }
    }

    public Boolean isMatching(Long memNo, String certKeyRowValue) {
        Member member = memberRepository.findByMemNo(memNo);
        String certKeyHashValue = member.getCertKey();

        return passwordEncoder.matches(certKeyRowValue, certKeyHashValue);
    }

    public void changeCertInfo(Member member) {
        /* member 테이블의 certification 값 'Y'로 변경
           member 테이블의 cert_key 값 새로 만들어 넣기 */
        String certKey = tempKey.getKey(10, false);
        memberRepository.save(Member.builder()
                .memNo(member.getMemNo())
                .identifier(member.getIdentifier())
                .name(member.getName())
                .password(member.getPassword())
                .phone(member.getPhone())
                .role(member.getRole())
                .certKey(passwordEncoder.encode(certKey))
                .certification("Y")
                .regDt(member.getRegDt())
                .build());

    }
}
