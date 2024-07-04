package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.member.MemberPagenationParam;
import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import com.portfolio.demo.project.dto.member.MemberParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    public MemberParam findByMemNo(Long memNo) {
        Member member = memberRepository.findById(memNo).orElse(null);

        if (member != null) {
            return MemberParam.create(member);
        } else {
            log.error("해당 식별번호의 회원 정보가 존재하지 않습니다. (memNo = {})", memNo);
        }

        return null;
    }

    public List<MemberParam> findByMemNoList(List<Long> memNoList) {
        List<Member> members = memberRepository.findByIds(memNoList);

        if (members.size() > 0) {
            return members.stream().map(MemberParam::create).collect(Collectors.toList());
        } else {
            log.error("해당 식별번호들의 회원 정보가 존재하지 않습니다. (memNo = {})", memNoList.toString());
        }
        return new ArrayList<>();
    }

    public MemberParam findByName(String name) {
        Member member = memberRepository.findByNameIgnoreCase(name);

        if (member != null) {
            return MemberParam.create(member);
        } else {
            log.error("해당 이름의 회원 정보가 존재하지 않습니다. (name = {})", name);
        }
        return null;
    }

    public MemberPagenationParam findAllByIdentifierContaining(String identifier, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<Member> result = memberRepository.findByIdentifierIgnoreCaseContaining(identifier, pageable);

        return new MemberPagenationParam(result);
    }

    public MemberPagenationParam findAllByNameContaining(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<Member> result = memberRepository.findByNameIgnoreCaseContaining(name, pageable);

        return new MemberPagenationParam(result);
    }

    public MemberPagenationParam findAllByPhoneContaining(String phone, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<Member> result = memberRepository.findByPhoneContaining(phone, pageable);

        return new MemberPagenationParam(result);
    }

    public MemberPagenationParam findAllByRole(MemberRole role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<Member> result = memberRepository.findByRole(role, pageable);

        return new MemberPagenationParam(result);
    }

    public MemberPagenationParam findAllByProvider(SocialLoginProvider provider, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<Member> result = memberRepository.findByProvider(provider, pageable);

        return new MemberPagenationParam(result);
    }

    public MemberPagenationParam findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<Member> result = memberRepository.findAll(pageable);

        return new MemberPagenationParam(result);
    }

    public MemberParam findByPhone(String phone) {
        Member member = memberRepository.findByPhone(phone);

        if (member != null) {
            return MemberParam.create(member);
        } else {
            log.error("해당 휴대번호의 회원 정보가 존재하지 않습니다. (phone = {})", phone);
        }

        return null;
    }

    public Boolean existsByPhone(String phone) {
        return memberRepository.existsByPhone(phone);
    }

    public MemberParam findByIdentifierAndProvider(String identifier, SocialLoginProvider provider) {
        if (provider != SocialLoginProvider.none) {
            identifier += "@socialuser.com";
        }

        Member mem = memberRepository.findByIdentifierAndProvider(identifier, provider);
        if (mem != null) {
            return MemberParam.create(mem);
        } else {
            log.error("해당 정보의 회원 정보가 존재하지 않습니다. (identifier = {}, provider = {})", identifier, provider);
        }
        return null;
    }

    public Long saveMember(MemberParam memberParam) {
        if (memberParam.getProvider().equals(SocialLoginProvider.none)) {
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

    public Long saveOauthMember(MemberParam memberParam) {
        Member member = Member.builder()
                .memNo(memberParam.getMemNo())
                .identifier(memberParam.getIdentifier())
                .name(memberParam.getName())
                .password("")
                .phone(memberParam.getPhone())
                .role(MemberRole.ROLE_USER)
                .profileImage(memberParam.getProfileImage())
                .certification(memberParam.getCertification())
                .provider(memberParam.getProvider())
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

            if (SocialLoginProvider.none.equals(member.getProvider())) {
                if (!passwordEncoder.matches(memberParam.getPassword(), member.getPassword())) {
                    member.updatePassword(passwordEncoder.encode(memberParam.getPassword()));
                }
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

    public int updateMultiRole(List<Long> ids, MemberRole role) {
        return memberRepository.updateRoleByIds(ids, role);
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

    /* 외부 로그인 api를 통해 로그인하는 경우 - CustomAuthenticationProvider를 거치는 것이 좋을지?(해당 계정의 ROLE 재검사 과정 거침) */
    public Authentication getAuthentication(MemberParam member) {
        Member user = memberRepository.findByIdentifier(member.getIdentifier());

        if (!SocialLoginProvider.none.equals(user.getProvider())) {
            user.updatePassword("");
        }

        UserDetail userDetail = new UserDetail(user);
        return new UsernamePasswordAuthenticationToken(userDetail.getUsername(), null, userDetail.getAuthorities());
    }

    public void deleteMember(Long memNo) {
        Member member = memberRepository.findById(memNo).orElse(null);
        if (member != null) {
            memberRepository.delete(member);
        } else {
            throw new IllegalStateException("해당 아이디를 가진 회원 정보가 존재하지 않습니다.");
        }
    }

    public void deleteMembers(List<Long> memNoList) {
        memberRepository.deleteByMemNos(memNoList);
    }

    public int updateDelYnByMemNo(Long memNo) {
        return memberRepository.updateDelYnByMemNo(memNo);
    }

    public int updateDelYnByMemNos(List<Long> memNos) {
        return memberRepository.updateDelYnByMemNos(memNos);
    }
}
