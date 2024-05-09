package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.util.CertUtil;
import com.portfolio.demo.project.service.MailService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.PhoneMessageService;
import com.portfolio.demo.project.vo.MemberVO;
import com.portfolio.demo.project.vo.SocialProfile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApi {

    private final MemberService memberService;

    private final PhoneMessageService phoneMessageService;

    private final MailService mailService;

    private final CertUtil certKeyService;

    @GetMapping("/api/member")
    public ResponseEntity<MemberVO> getCurrentMember(HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @GetMapping("/api/member/{id}")
    public ResponseEntity<MemberVO> getMember(@PathVariable Long id) {
        Member user = memberService.findByMemNo(id);
    return new ResponseEntity<>(MemberVO.create(user), HttpStatus.OK);
    }

    @PostMapping("/api/member")
    public ResponseEntity<MemberVO> signUp(HttpSession session, @RequestBody Member member) { // , @RequestParam(name = "type", required = false, defaultValue = "d") String type
        if ("none".equals(member.getProvider())) {
            // 이메일로 찾는 과정 + identifier 컬럼을 유니크 키로 사용
            log.info("전송된 유저 정보 : {}", member);
            memberService.saveMember(member);
            Map<String, String> result = mailService.sendGreetingMail(member.getIdentifier());

            Member createdMember = memberService.findByIdentifier(member.getIdentifier());
            log.info("생성된 유저 식별번호 : {}", createdMember.getMemNo());

            if (result.get("resultCode").equals("success")) {
                return new ResponseEntity<>(MemberVO.create(createdMember), HttpStatus.CREATED);
            } else {
                throw new IllegalStateException();
            }
        } else {
            SocialProfile profile = (SocialProfile) session.getAttribute("profile");
            log.info("조횐 프로필 : {}", profile);

            String profileImage = profile.getProfileImageUrl();
            if (profileImage != null) {
                profileImage = profileImage.replace("\\", "");
            }

            // id, name, phone, provider, profileImage
            memberService.saveOauthMember(
                    Member.builder()
                            .identifier(member.getIdentifier())
                            .password("")
                            .name(member.getName())
                            .phone(member.getPhone())
                            .provider(member.getProvider())
                            .profileImage(profileImage)
                            .role("ROLE_USER")
                            .certKey(null)
                            .certification("Y")
                            .build()
            );

            /* DB에 저장된 데이터 로드 */
            Member createdMember = memberService.findByIdentifierAndProvider(member.getIdentifier(), member.getProvider());
            log.info("생성된 유저 식별번호 : {}", createdMember.getMemNo());

            return new ResponseEntity<>(MemberVO.create(createdMember), HttpStatus.CREATED);
        }
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth); // 세션을 무효화시킴(네이버 로그인 api에서 제공하는 접근 토큰, 리프레시 토큰도 함께 제거될 듯?
        } else throw new IllegalStateException("로그인된 회원 정보가 없습니다.");
    }
}

