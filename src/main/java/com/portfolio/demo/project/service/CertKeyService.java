package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.util.TempKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CertKeyService {

    private final MemberRepository memberRepository;

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final TempKey tempKey;

    public Boolean CheckCertInfo(Long memNo, String certKey) {

        Boolean match = isMatching(memNo, certKey);
        if (match) {
            changeCertInfo(memberService.findByMemNo(memNo));
            return true;
        } else {
            return false;

        }
    }

    public Boolean isMatching(Long memNo, String certKeyRowValue) {
        Member member = memberService.findByMemNo(memNo);
        String certKeyHashValue = member.getCertKey();

        return passwordEncoder.matches(certKeyRowValue, certKeyHashValue);
    }

    public void changeCertInfo(Member member) {
        /* member 테이블의 certification 값 'Y'로 변경
           member 테이블의 cert_key 값 새로 만들어 넣기 */
        String certKey = tempKey.getKey(10, false);
        member.updateCertKey(passwordEncoder.encode(certKey));
        member.updateCertification("Y");

        memberRepository.save(member);
    }
}
