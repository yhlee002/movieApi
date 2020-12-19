package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.OauthMember;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.repository.OauthMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MemberService {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OauthMemberRepository oMemberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member findByName(String name) {
        return memberRepository.findByName(name);
    }

    public Member findByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }

    public Member findByMemNo(Long memNo) {
        return memberRepository.findByMemNo(memNo);
    }

    public void saveMember(Member member) {

        memberRepository.save(
                Member.builder().memNo(null)
                        .email(member.getEmail())
                        .name(member.getName())
                        .password(passwordEncoder.encode(member.getPassword()))
                        .phone(member.getPhone())
                        .role("ROLE_USER")
                        .regDt(member.getRegDt())
                        .certKey(null)
                        .certification("N")
                        .build()
        );
    }

    /* OauthMember 타입일 경우 oauth_member 테이블에서 조회 (기존 회원일 경우(Member 타입) member 테이블에서 조회(findByPhone()) */
    public String IsOauthMemberByPhone(String phone) {
        OauthMember oMember = oMemberRepository.findByPhone(phone);

        if (oMember != null) {
            log.info("[IsOauthMemberByPhone] oMember 정보 : " + oMember.toString());
            return oMember.toString();
        } else {
            log.info("[IsOauthMemberByPhone] oMember 정보 : null");
            return null;
        }
    }

    public String IsBasicMemberByPhone(String phone) {
        Member member = memberRepository.findByPhone(phone);

        if (member != null) {
            log.info("[IsBasicMemberByPhone] member 정보 : " + member.toString());
            return member.toString();
        } else {
            log.info("[IsBasicMemberByPhone] member 정보 : null");
            return null;
        }
    }

    /* 회원가입 여부 확인 - 네아로 회원이라면 ROLE 반환 */
    public String getNaverOAuthInfo(Map<String, String> profile) {

        /* 사용자 프로필 조회 api를 통해 얻은 사용자 정보를 기존 회원가입 방식으로 가입이력이 있는 회원인지 조회(DB 조회) - 취소 */
        String id = profile.get("id");
        OauthMember oauthMember = oMemberRepository.findByUniqueId(id);

        if (oauthMember != null) {
            OauthMember oMember = oMemberRepository.findByProviderAndUniqueId("naver", profile.get("id"));

            if (oMember != null) { /* 네아로를 통해 가입한 회원 - 그대로 로그인 진행. */
                String nickname = profile.get("nickname");
                String profileImage = profile.get("profile_image").replace("https://", "");
//                profileImage.replace("\\/", "/");
                log.info("profileImage url : "+profileImage);

//                if (!oMember.getNickname().equals(nickname)) {
                    oMemberRepository.save(OauthMember.builder()
                            .oauthmemNo(oMember.getOauthmemNo())
                            .uniqueId(oMember.getUniqueId())
                            .nickname(nickname)
                            .profileImage(profileImage)
                            .provider(oMember.getProvider())
                            .phone(oMember.getPhone())
                            .regDt(oMember.getRegDt())
                            .role(oMember.getRole())
                            .build());
//                }

//                if(oMember.getProfileImage() != null && !oMember.getProfileImage().equals(profileImage)) {
//
//                }
                return oMember.getRole();
            } else { /* 카카오 로그인 api를 통해 가입한 회원 */
                OauthMember oMember2 = oMemberRepository.findByProviderAndUniqueId("kakao", profile.get("id"));
                return oMember2.getRole();
            }
        } else {
            /* 미가입자인 경우 */
            return "not user";
        }
    }

}
