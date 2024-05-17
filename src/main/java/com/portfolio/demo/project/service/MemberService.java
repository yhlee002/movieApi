package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.security.UserDetail.UserDetail;
import com.portfolio.demo.project.util.TempKey;
import com.portfolio.demo.project.vo.MemberVO;
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

    public MemberVO findByMemNo(Long memNo) {
        Member member = memberRepository.findById(memNo).orElse(null);

        MemberVO result = null;
        if (member != null) {
            result = MemberVO.create(member);
        } else {
            log.error("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo = {})", memNo);
        }

        return result;
    }

    public MemberVO findByIdentifier(String identifier) {
        Member member = memberRepository.findByIdentifier(identifier);

        MemberVO result = null;
        if (member != null) {
            result = MemberVO.create(member);
        } else {
            log.error("해당 식별자의 회원 정보가 존재하지 않습니다. (memNo = {})", identifier);
        }

        return result;
    }

    public MemberVO findByName(String name) {
        Member member = memberRepository.findByNameIgnoreCase(name);

        MemberVO result = null;
        if (member != null) {
            result = MemberVO.create(member);
        } else {
            log.error("해당 이름의 회원 정보가 존재하지 않습니다. (memNo = {})", name);
        }
        return result;
    }

    public List<MemberVO> findAllByNameContaining(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        List<Member> list = memberRepository.findByNameIgnoreCaseContaining(name, pageable).getContent();

        return list.stream().map(MemberVO::create).toList();
    }

    public MemberVO findByPhone(String phone) {
        Member member = memberRepository.findByPhone(phone);

        MemberVO result = null;
        if (member != null) {
            result = MemberVO.create(member);
        } else {
            log.error("해당 휴대번호의 회원 정보가 존재하지 않습니다. (phone = {})", phone);
        }

        return result;
    }

    public Boolean existsByPhone(String phone) {
        return memberRepository.existsByPhone(phone);
    }

    public MemberVO findByIdentifierAndProvider(String identifier, String provider) {
        Member mem = memberRepository.findByIdentifierAndProvider(identifier, provider);
        return MemberVO.create(mem);
    }

    public MemberVO updateMember(MemberVO member) {
        if (member.getMemNo() == null) {
            if (member.getProvider().equals("none")) {
                member.setPassword(passwordEncoder.encode(member.getPassword()));
                member.setCertification("N");
            } else {
                member.setPassword("");
                member.setCertification("Y");
            }
            if (member.getRole() == null) member.setRole("ROLE_USER");
        }

        Member created = memberRepository.save(
                Member.builder()
                        .memNo(member.getMemNo())
                        .identifier(member.getIdentifier())
                        .name(member.getName())
                        .role(member.getRole())
                        .password(member.getPassword())
                        .certification(member.getCertification())
                        .certKey(member.getCertKey())
                        .provider(member.getProvider())
                        .phone(member.getPhone())
                        .profileImage(member.getProfileImage())
                        .build()
        );

        return MemberVO.create(created);
    }

    public MemberVO saveOauthMember(MemberVO member) {
        Member created = memberRepository.save(
                Member.builder()
                        .memNo(member.getMemNo())
                        .identifier(member.getIdentifier())
                        .name(member.getName())
                        .password(passwordEncoder.encode(member.getPassword()))
                        .phone(member.getPhone())
                        .profileImage(member.getProfileImage())
                        .certification(member.getCertification())
                        .certKey(member.getCertKey())
                        .provider(member.getProvider())
                        .build()
        );

        return MemberVO.create(created);
    }

    public void updatePwd(Long memNo, String pwd) {
        Member member = memberRepository.findById(memNo).orElse(null);
        if (member != null) {

            Member modified = Member.builder()
                    .memNo(member.getMemNo())
                    .identifier(member.getIdentifier())
                    .name(member.getName())
                    .password(passwordEncoder.encode(pwd))
                    .phone(member.getPhone())
                    .profileImage(member.getProfileImage())
                    .certification(member.getCertification())
                    .certKey(member.getCertKey())
                    .provider(member.getProvider())
                    .build();

            memberRepository.save(modified);

            log.info("회원 비밀번호 업데이트(회원 식별번호 : " + member.getMemNo() + ")");
        } else {
//            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
            log.error("해당 아이디를 가진 회원 정보가 존재하지 않습니다. (memNo = {})", memNo);
        }
    }

    public void updateCertKey(Long memNo) {
        String certKey = tempKey.getKey(10, false);

        Member member = memberRepository.findById(memNo).orElse(null);
        if (member != null) {
            member.updateCertKey(passwordEncoder.encode(certKey));
            memberRepository.save(member);
        }
    }

    /* 외부 로그인 api를 통해 로그인하는 경우 - CustomAuthenticationProvider를 거치는 것이 좋을지?(해당 계정의 ROLE 재검사 과정 거침) */
    public Authentication getAuthentication(MemberVO member) {
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
