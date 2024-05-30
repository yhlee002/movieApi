package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import com.portfolio.demo.project.util.TempKey;
import com.portfolio.demo.project.dto.MemberParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TempKey tempKey;

    public MemberParam findByMemNo(Long memNo) {
        Member member = memberRepository.findById(memNo).orElse(null);

        MemberParam result = null;
        if (member != null) {
            result = MemberParam.create(member);
        } else {
            log.error("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo = {})", memNo);
        }

        return result;
    }

    public MemberParam findByIdentifier(String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);

        MemberParam result = null;
        if (member != null) {
            result = MemberParam.create(member);
        } else {
            log.error("해당 식별자의 회원 정보가 존재하지 않습니다. (memNo = {})", identifier);
        }

        return result;
    }

    public MemberParam findByName(String name) {
        Member member = memberRepository.findByNameIgnoreCase(name);

        MemberParam result = null;
        if (member != null) {
            result = MemberParam.create(member);
        } else {
            log.error("해당 이름의 회원 정보가 존재하지 않습니다. (memNo = {})", name);
        }
        return result;
    }

    public List<MemberParam> findAllByNameContaining(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        List<Member> list = memberRepository.findByNameIgnoreCaseContaining(name, pageable).getContent();

        return list.stream().map(MemberParam::create).toList();
    }

    public MemberParam findByPhone(String phone) {
        Member member = memberRepository.findByPhone(phone);

        MemberParam result = null;
        if (member != null) {
            result = MemberParam.create(member);
        } else {
            log.error("해당 휴대번호의 회원 정보가 존재하지 않습니다. (phone = {})", phone);
        }

        return result;
    }

    public Boolean existsByPhone(String phone) {
        return memberRepository.existsByPhone(phone);
    }

    public MemberParam findByIdentifierAndProvider(String identifier, String provider) {
        Member mem = memberRepository.findByIdentifierAndProvider(identifier, provider);
        return MemberParam.create(mem);
    }

    public Long saveMember(MemberParam memberParam) {
        if (memberParam.getProvider().equals("none")) {
            memberParam.setPassword(passwordEncoder.encode(memberParam.getPassword()));
            memberParam.setCertification(MemberCertificated.N);
        } else {
            memberParam.setPassword("");
            memberParam.setCertification(MemberCertificated.Y);
        }
        if (memberParam.getRole() == null) memberParam.setRole(MemberRole.ROLE_USER);

        Member member = Member.builder()
                .memNo(null)
                .identifier(memberParam.getIdentifier())
                .name(memberParam.getName())
                .role(memberParam.getRole())
                .password(memberParam.getPassword())
                .certification(memberParam.getCertification())
                .provider(memberParam.getProvider())
                .phone(memberParam.getPhone())
                .profileImage(memberParam.getProfileImage())
                .build();

        memberRepository.save(member);

        return member.getMemNo();
    }

    public Long updateMember(MemberParam memberParam) {
        Member member = memberRepository.findById(memberParam.getMemNo()).orElse(null);

        if (member != null) {
            member.updateName(memberParam.getName());
            member.updatePhone(memberParam.getPhone());
            member.updateProfileImage(memberParam.getProfileImage());
            member.updateCertification(memberParam.getCertification());

            if (!passwordEncoder.matches(memberParam.getPassword(), member.getPassword())) {
                member.updatePassword(passwordEncoder.encode(memberParam.getPassword()));
            }
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return member.getMemNo();
    }

    public Long updateCertification(MemberParam memberParam) {
        Member member = memberRepository.findById(memberParam.getMemNo()).orElse(null);

        if (member != null) {
            member.updateCertification(memberParam.getCertification());
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return member.getMemNo();
    }

    public Long updateRole(MemberParam memberParam) {
        Member member = memberRepository.findById(memberParam.getMemNo()).orElse(null);

        if (member != null) {
            member.updateRole(memberParam.getRole());
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return member.getMemNo();
    }

    public Long saveOauthMember(MemberParam memberParam) {
        Member member = Member.builder()
                .memNo(memberParam.getMemNo())
                .identifier(memberParam.getIdentifier())
                .name(memberParam.getName())
                .password(passwordEncoder.encode(memberParam.getPassword()))
                .phone(memberParam.getPhone())
                .profileImage(memberParam.getProfileImage())
                .certification(memberParam.getCertification())
                .provider(memberParam.getProvider())
                .build();
        memberRepository.save(member);

        return member.getMemNo();
    }

    public Long updatePwd(MemberParam memberParam) {
        Member member = memberRepository.findById(memberParam.getMemNo()).orElse(null);

        if (member != null) {
            member.updatePassword(passwordEncoder.encode(memberParam.getPassword()));
        } else {
            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
        }

        return member.getMemNo();
    }

//    public Long updateCertKey(Long memNo) {
//        String certKey = tempKey.getKey(10, false);
//
//        Member member = memberRepository.findById(memNo).orElse(null);
//
//        if (member != null) {
//            member.updateCertKey(passwordEncoder.encode(certKey));
//        } else {
//            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
//        }
//
//        return member.getMemNo();
//    }

    /* 외부 로그인 api를 통해 로그인하는 경우 - CustomAuthenticationProvider를 거치는 것이 좋을지?(해당 계정의 ROLE 재검사 과정 거침) */
    public Authentication getAuthentication(MemberParam member) {
        Member user = memberRepository.findByIdentifier(member.getIdentifier());
        UserDetail userDetail = new UserDetail(user);
        return new UsernamePasswordAuthenticationToken(userDetail.getUsername(), null, userDetail.getAuthorities());
    }

    public void deleteMember(Long memNo) {
        Member member = memberRepository.findById(memNo).orElse(null);
        if (member != null) {
            memberRepository.delete(member);
        } else {
            log.error("해당 아이디를 가진 회원 정보가 존재하지 않습니다. (memNo = {})", memNo);
        }
    }
}
