package com.portfolio.demo.project.util;

import com.portfolio.demo.project.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CertUtil {

    private final PasswordEncoder passwordEncoder;

    private final TempKey tempKey;

    public Boolean validateCertKey(MemberVO member, String certKey) {
        return isMatching(member.getCertKey(), certKey);
    }

    public Boolean isMatching(String certKeyHashValue, String certKeyRowValue) {
        return passwordEncoder.matches(certKeyRowValue, certKeyHashValue);
    }

    public MemberVO changeCertStatus(MemberVO member) {
        /* member 테이블의 certification 값 'Y'로 변경
           member 테이블의 cert_key 값 새로 만들어 넣기 */
        String certKey = tempKey.getKey(10, false);
        member.setCertKey(passwordEncoder.encode(certKey));
        member.setCertification("Y");

        return member;
    }
}
