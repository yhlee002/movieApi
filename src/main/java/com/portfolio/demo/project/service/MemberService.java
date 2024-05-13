package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import com.portfolio.demo.project.util.TempKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Member findByName(String name) {
        return memberRepository.findByNameIgnoreCase(name);
    }

    public List<Member> findAllByNameContaining(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        return memberRepository.findByNameIgnoreCaseContaining(name, pageable).getContent();
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
            if (member.getProvider().equals("none")) {
                member.updatePassword(passwordEncoder.encode(member.getPassword()));
                member.updateCertification("N");
            } else {
                member.updateCertification("Y");
            }
            if (member.getRole() == null) member.updateRole("ROLE_USER");
        }

        return memberRepository.save(member);
    }

    public void saveOauthMember(Member member) {
        memberRepository.save(member);
    }

    public void updatePwd(Long memNo, String pwd) {
        Optional<Member> opt = memberRepository.findById(memNo);
        if (opt.isPresent()) {
            Member member = opt.get();
            member.updatePassword(passwordEncoder.encode(pwd));
            memberRepository.save(member);
            log.info("회원 비밀번호 업데이트(회원 식별번호 : " + member.getMemNo() + ")");
        } else {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }
    }

    public void updateCertKey(Long memNo) {
        String certKey = tempKey.getKey(10, false);

        Member member = memberRepository.findById(memNo).get();
        if (member != null) {
            member.updateCertKey(passwordEncoder.encode(certKey));
            memberRepository.save(member);
        }
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
                originMember.updateName(name);
            }
            /* 프로필 이미지 체크 */
            if (profileImg.length() != 0) { // 프로필 이미지가 존재할 때
                if (!profileImg.equals(originMember.getProfileImage())) { // 프로필 이미지가 현재 DB의 프로필 이미지와 다르면(새로 등록했다면)
                    originMember.updateProfileImage(profileImg); // 저장하기
                }
            } else { // 이미지가 없거나 있었다가 제거한 경우
                originMember.updateProfileImage(null);
            }
            /* 연락처 체크 */
            if (!phone.equals(originMember.getPhone())) { // 번호가 바뀐 경우
                originMember.updatePhone(phone);
            }

            /* 비밀번호 null 체크 */
            if (member.getProvider().equals("none")) {
                if (member.getPassword() != null && member.getPassword().length() != 0) {
                    originMember.updatePassword(passwordEncoder.encode(member.getPassword()));
                }
            }

            originMember.updateName(member.getName()); // 닉네임 변경시 저장
            originMember.updatePhone(member.getPhone()); // 번호 변경시 저장
            memberRepository.save(originMember);

            log.info("변경된 회원 정보 : " + originMember.toString());
        }
        return originMember;
    }

    public void deleteMember(Long memNo) {
        Optional<Member> opt = memberRepository.findById(memNo);
        if (opt.isEmpty()) {
            log.error("회원 삭제 실패 : 존재하지 않는 회원(MemNo : {})", memNo);
        } else memberRepository.delete(opt.get());
    }
}
