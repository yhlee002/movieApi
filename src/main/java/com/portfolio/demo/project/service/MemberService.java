package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member findByMemNo(Long memNo) {
        Member mem = null;
        Optional<Member> member = memberRepository.findById(memNo);
        if (member.isPresent()) {
            mem = member.get();
        }

        return mem;
    }

    public Member findByIdentifier(String identifier) {
        return memberRepository.findByIdentifier(identifier);
    }

    public Member findByName(String name) {
        return memberRepository.findByName(name);
    }

    public Member findByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }

    public Member findByIdentifierAndProvider(String identifier, String provider) {
        return memberRepository.findByIdentifierAndProvider(identifier, provider);
    }

    public void saveMember(Member member) {
        memberRepository.save(
                Member.builder().memNo(null)
                        .identifier(member.getIdentifier())
                        .name(member.getName())
                        .password(passwordEncoder.encode(member.getPassword())) // null일 경우 어떻게 처리되는지?
                        .phone(member.getPhone())
                        .role("ROLE_USER")
                        .profileImage(member.getProfileImage())
                        .provider(member.getProvider()) // none, naver, kakao
//                        .regDt(member.getRegDt())
                        .certKey(null)
                        .certification("N")
                        .build()
        );
    }

    public void saveOauthMember(HttpSession session, String id, String name, String phone, String provider) { // Member member
        Map<String, String> profile = (Map<String, String>) session.getAttribute("profile");
        log.info("profile : " + profile);
        String profileImage = profile.get("profile_image");
        log.info("profileImage : " + profileImage);

        memberRepository.save(
                Member.builder()
                        .memNo(null)
                        .identifier(id)
                        .name(name)
                        .password("")
                        .phone(phone)
                        .profileImage(profileImage.replace("\\", ""))
                        .provider(provider) // none, naver, kakao
                        .regDt(LocalDateTime.now())
                        .role("ROLE_USER")
                        .certKey(null)
                        .certification("Y")
                        .build()
        );
    }

    /* provider 전달 필요(naver, kakao) */
    public Member findByProfile(String identifier, String provider) {
        Member member = memberRepository.findByIdentifierAndProvider(identifier, provider);

        return member;
    }

    /* 외부 로그인 api를 통해 로그인하는 경우 - CustomAuthenticationProvider를 거치는 것이 좋을지?(해당 계정의 ROLE 재검사 과정 거침) */
    public Authentication getAuthentication(Member member) {
        UserDetail userDetail = new UserDetail(member);
        return new UsernamePasswordAuthenticationToken(userDetail.getUsername(), null, userDetail.getAuthorities());
    }

    public void updateUserInfo(Member member) {
        Member originMember = null;
        Optional<Member> originMemberOpt = memberRepository.findById(member.getMemNo());
        if (originMemberOpt.isPresent()) {
            originMember = originMemberOpt.get();
            member.setName(member.getName());
            member.setPassword(passwordEncoder.encode(member.getPassword()));
            member.setPhone(member.getPhone());
        }
    }

    public void deletUserInfo(Member member) {
        memberRepository.delete(member);
    }
}
