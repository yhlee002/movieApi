package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import com.portfolio.demo.project.util.TempKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TempKey tempKey;

    public Member findByMemNo(Long memNo) {
        Optional<Member> opt = memberRepository.findById(memNo);

        return opt.orElseGet(() -> null);
    }

    public Member findByIdentifier(String identifier) {
        return memberRepository.findByIdentifier(identifier);
    }

    public List<Member> findAllByNameContaining(String name, int page, int size) {
        return memberRepository.findByNameIgnoreCaseContaining(name, page, size);
    }

    public Boolean validateDuplicationName(String name) {
        return memberRepository.existsByName(name);
    }

    public Member findByPhone(String phone) {
        return memberRepository.findByPhone(phone);
    }

    public Boolean existsByPhone(String phone) {
        return memberRepository.existsByPhone(phone);
    }

    public Member findByIdentifierAndProvider(String identifier, String provider) {
        return memberRepository.findByIdentifierAndProvider(identifier, provider);
    }

    public Member saveMember(Member member) {
        if (member.getMemNo() == null) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
            member.setRole("ROLE_USER");
            member.setCertification("N");
            memberRepository.save(member);
        }

        return memberRepository.save(member);
    }

    public void saveOauthMember(HttpSession session, String id, String name, String phone, String provider) { // Member member
        Map<String, String> profile = (Map<String, String>) session.getAttribute("profile");
        log.info("profile : " + profile);
        String profileImage = profile.get("profile_image");
        if (profileImage != null) {
            profileImage = profileImage.replace("\\", "");
        }

        memberRepository.save(
                Member.builder()
                        .memNo(null)
                        .identifier(id)
                        .name(name)
                        .password("")
                        .phone(phone)
                        .profileImage(profileImage)
                        .provider(provider) // none, naver, kakao
                        .regDt(LocalDateTime.now())
                        .role("ROLE_USER")
                        .certKey(null)
                        .certification("Y")
                        .build()
        );
    }

    public void updatePwd(Long memNo, String pwd) {
        Member member = memberRepository.findById(memNo).get();
        if (member != null) {
            member.setPassword(passwordEncoder.encode(pwd));
            Member memberUpdated = memberRepository.save(member);
            log.info("업데이트된 회원 정보 : " + memberUpdated.toString());
        }
    }

    public void updateCertKey(Long memNo) {
        String certKey = tempKey.getKey(10, false);

        Member member = memberRepository.findById(memNo).get();
        if (member != null) {
            member.setCertKey(passwordEncoder.encode(certKey));
            memberRepository.save(member);
        }
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

    public Member updateMember(Member member) {
        Member originMember = null;
        Optional<Member> originMemberOpt = memberRepository.findById(member.getMemNo());
        if (originMemberOpt.isPresent()) {
            originMember = originMemberOpt.get();

            String name = member.getName();
            String profileImg = member.getProfileImage();
            String phone = member.getPhone();

            /* 닉네임 체크 */
            if (!name.equals(originMember.getName())) { // 이미 있는 원래 닉네임과 다를 경우 변경
                originMember.setName(name);
            }
            /* 프로필 이미지 체크 */
            if (profileImg.length() != 0) { // 프로필 이미지가 존재할 때
                if (!profileImg.equals(originMember.getProfileImage())) { // 프로필 이미지가 현재 DB의 프로필 이미지와 다르면(새로 등록했다면)
                    originMember.setProfileImage(profileImg); // 저장하기
                }
            } else { // 이미지가 없거나 있었다가 제거한 경우
                originMember.setProfileImage(null);
            }
            /* 연락처 체크 */
            if (!phone.equals(originMember.getPhone())) { // 번호가 바뀐 경우
                originMember.setPhone(phone);
            }

            /* 비밀번호 null 체크 */
            if (member.getProvider().equals("none")) {
                if (member.getPassword() != null && member.getPassword().length() != 0) {
                    originMember.setPassword(passwordEncoder.encode(member.getPassword()));
                }
            }

            originMember.setName(member.getName()); // 닉네임 변경시 저장
            originMember.setPhone(member.getPhone()); // 번호 변경시 저장
            memberRepository.save(originMember);

//            log.info("변경된 회원 정보 : " + originMember.toString());
        }
        return originMember;
    }

    public void deleteUserInfo(Long memNo) {
        Optional<Member> opt = memberRepository.findById(memNo);
        if (!opt.isPresent()) {
            log.error("회원 삭제 실패 : 존재하지 않는 회원(MemNo : {})", memNo);
        }
        memberRepository.delete(opt.get());
    }

    public void deleteMember(Member member) {
        memberRepository.delete(member);
    }
}
