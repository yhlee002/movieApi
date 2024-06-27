package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.dto.certification.CertMessageValidationRequest;
import com.portfolio.demo.project.dto.certification.CertResponse;
import com.portfolio.demo.project.dto.certification.CertificationDataDto;
import com.portfolio.demo.project.dto.member.request.*;
import com.portfolio.demo.project.dto.member.MemberResponse;
import com.portfolio.demo.project.dto.social.*;
import com.portfolio.demo.project.entity.certification.CertificationReason;
import com.portfolio.demo.project.entity.certification.CertificationType;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.oauth2.CustomOAuth2User;
import com.portfolio.demo.project.service.CertificationService;
import com.portfolio.demo.project.dto.certification.SendCertificationNotifyResult;
import com.portfolio.demo.project.util.*;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.dto.member.MemberParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApi {

    private final MemberService memberService;

    private final CertificationService certificationService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final NaverLoginApiUtil naverLoginApiUtil;

    private final NaverProfileApiUtil naverProfileApiUtil;

    private final KakaoLoginApiUtil kakaoLoginApiUtil;

    private final KakaoProfileApiUtil kakaoProfileApiUtil;

    @Value("${web.server.host}")
    private String HOST;

    /**
     * 현재 세션의 회원 정보 조회
     *
     * @param
     * @return
     */
    @GetMapping("/member/current")
    public ResponseEntity<Result<MemberResponse>> getCurrentMember() { // HttpSession session
//        MemberParam member = (MemberParam) session.getAttribute("member");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identifier = "";
        MemberRole role = null;
        SocialLoginProvider provider;
        if (auth instanceof OAuth2AuthenticationToken) {
            CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
            identifier = user.getIdentifier();
            role = user.getRole();
            provider = user.getProvider();
        } else {
            identifier = (String) auth.getPrincipal();
            Iterator<? extends GrantedAuthority> iter = auth.getAuthorities().iterator();
            role = MemberRole.valueOf(iter.next().toString());
            provider = SocialLoginProvider.none;
        }

        if (!identifier.equals("anonymousUser") &&
                !role.equals(MemberRole.ROLE_ANONYMOUS) && !role.equals(MemberRole.ROLE_GUEST)) {
            MemberParam member = memberService.findByIdentifierAndProvider(identifier, provider);
            return new ResponseEntity<>(new Result<>(new MemberResponse(member)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Result<>(null), HttpStatus.OK);
        }
    }

    /**
     * 특정 식별번호의 회원 정보 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/member/{id}")
    public ResponseEntity<Result<MemberResponse>> getMember(@PathVariable Long id) {
        MemberParam member = memberService.findByMemNo(id);
        if (member != null) {
            return ResponseEntity.ok(new Result<>(new MemberResponse(member)));
        } else {
            return ResponseEntity.ok(new Result<>(null));
        }
    }

    /**
     * 특정 조건의 회원 조회
     *
     * @param identifier
     * @param name
     * @param phone
     * @return
     */
    @GetMapping("/member")
    public ResponseEntity<Result<MemberResponse>> findMember(@RequestParam(name = "identifier", required = false) String identifier,
                                                             @RequestParam(name = "provider", required = false) SocialLoginProvider provider,
                                                             @RequestParam(name = "name", required = false) String name,
                                                             @RequestParam(name = "phone", required = false) String phone
    ) {
        MemberParam member = null;
        String condition = null;
        if (identifier != null && !identifier.isEmpty() && provider != null) {
            member = memberService.findByIdentifierAndProvider(identifier, provider);
            condition = "identifier&provider";
        } else if (name != null && !name.isEmpty()) {
            member = memberService.findByName(name); // validateDuplicationName
            condition = "name";
        } else if (phone != null && !phone.isEmpty()) {
            member = memberService.findByPhone(phone);
            condition = "phone";
        }

        log.info("조건: " + condition + " 조회된 사용자(식별번호) :" + (member != null ? member.getMemNo() : "없음"));

        MemberResponse response = null;
        if (member != null) {
            response = new MemberResponse(member);
        }
        return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
    }

    @GetMapping("/members")
    public ResponseEntity<Result<List<MemberResponse>>> findMembers(@RequestParam(name = "identifier", required = false) String identifier,
                                                                    @RequestParam(name = "name", required = false) String name,
                                                                    @RequestParam(name = "phone", required = false) String phone,
                                                                    @RequestParam(name = "role", required = false) MemberRole role,
                                                                    @RequestParam(name = "provider", required = false) SocialLoginProvider provider,
                                                                    @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                                    @RequestParam(name = "size", required = false, defaultValue = "10") int size
    ) {
        List<MemberParam> members = new ArrayList<>();
        if (identifier != null && !identifier.isEmpty()) {
            members = memberService.findAllByIdentifierContaining(identifier, page, size);
        } else if (name != null && !name.isEmpty()) {
            members = memberService.findAllByNameContaining(name, page, size); // validateDuplicationName
        } else if (phone != null && !phone.isEmpty()) {
            members = memberService.findAllByPhoneContaining(phone, page, size);
        } else if (role != null) {
            members = memberService.findAllByRole(role, page, size);
        } else if (provider != null) {
            members = memberService.findAllByProvider(provider, page, size);
        } else {
            members = memberService.findAll(page, size);
        }

        List<MemberResponse> responses = members.stream().map(param -> new MemberResponse(param)).collect(Collectors.toList());
        return ResponseEntity.ok(new Result<>(responses));

    }

    /**
     * 소셜 로그인 API 호출 뒤 로그인 성공시 해당 경로로 리다이렉트
     *
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/sign-up/oauth2")
    public void oauthRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) auth.getPrincipal();
        request.getSession().setAttribute("oauthUser", user);

        response.sendRedirect("http://" + HOST + "/sign-up?type=oauth");
    }

    /**
     * 소셜 로그인 API 호출 이후 세션에 넣어둔 사용자 프로필 정보를 조회
     *
     * @param request
     * @return
     */
    @GetMapping("/oauth2/userinfo")
    public ResponseEntity<Result<MemberResponse>> getOAuthUserInfoFromSession(HttpServletRequest request) {
        CustomOAuth2User user = (CustomOAuth2User) request.getSession().getAttribute("oauthUser");

        MemberResponse result = new MemberResponse();

        if (SocialLoginProvider.naver.equals(user.getProvider())) {
            Map<String, Object> response = user.getAttributes();

            result.setIdentifier(user.getIdentifier());
            result.setProvider(user.getProvider());
            result.setProfileImage(String.valueOf(response.get("profile_image")));
        } else if (SocialLoginProvider.kakao.equals(user.getProvider())){
            Map<String, Object> kakaoAccount = (Map<String, Object>) user.getAttributes().get("kakao_account");
            Map<String, String> profile = (Map<String, String>) kakaoAccount.get("profile");
            String profileImage = profile.get("profile_image_url");

            result.setIdentifier(user.getIdentifier());
            result.setProvider(user.getProvider());
            result.setProfileImage(profileImage);
        }

        return ResponseEntity.ok(new Result<>(result));
    }

    /**
     * 회원가입
     *
     * @param session
     * @param request
     * @return
     */
    @PostMapping("/member")
    public ResponseEntity<Result<MemberResponse>> signUp(HttpSession session, @RequestBody @Valid CreateMemberRequest request) {
        if (SocialLoginProvider.none.equals(request.getProvider())) {
            Long memNo = memberService.saveMember(
                    MemberParam.builder()
                            .identifier(request.getIdentifier())
                            .name(request.getName())
                            .password(request.getPassword())
                            .provider(request.getProvider())
                            .phone(request.getPhone())
                            .role(request.getRole()) // admin만 존재
                            .certification(request.getCertification()) // admin만 존재
                            .build()
            );

            MemberParam created = memberService.findByMemNo(memNo);
            log.info("생성된 유저 식별번호 : {}", created.getMemNo());

            SendCertificationNotifyResult result = certificationService.sendCertificationMail(request.getIdentifier(), CertificationReason.SIGNUP);
            if (result.getResult()) {
                MemberResponse response = new MemberResponse(created);

                return new ResponseEntity<>(new Result<>(response), HttpStatus.CREATED);
            } else {
                throw new IllegalStateException();
            }
        } else {
//            SocialProfileParam profile = (SocialProfileParam) session.getAttribute("profile");
            CustomOAuth2User oAuth2User = (CustomOAuth2User) session.getAttribute("oauthUser");
            SocialLoginProvider provider = oAuth2User.getProvider();

            String profileImage = "";
            if (SocialLoginProvider.naver.equals(provider)) {
                Map<String, String> map = oAuth2User.getAttribute("response");
                profileImage = map.get("profile_image");
            } else {
                Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
                Map<String, String> profile = (Map<String, String>) kakaoAccount.get("profile");
                profileImage = profile.get("profile_image_url");
            }

            if (profileImage != null) {
                profileImage = profileImage.replace("\\", "");
            }

            // id, name, phone, provider, profileImage
            MemberParam member = MemberParam.builder()
                    .identifier(request.getIdentifier())
                    .name(request.getName())
                    .password("")
                    .phone(request.getPhone())
                    .provider(request.getProvider())
                    .role(MemberRole.ROLE_USER)
                    .profileImage(profileImage)
                    .certification(MemberCertificated.Y)
                    .build();

            Long memNo = memberService.saveOauthMember(member);
            MemberParam created = memberService.findByMemNo(memNo);
            MemberResponse response = new MemberResponse(created);

            log.info("생성된 유저 식별번호 : {}", created.getMemNo());

            // 로그인
            Authentication auth = memberService.getAuthentication(created);
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute("member", member);

            // 세션의 profile 제거
            session.removeAttribute("profile");

            return new ResponseEntity<>(new Result<>(response), HttpStatus.CREATED);
        }
    }

    /**
     * 회원 정보 수정
     */
    @PatchMapping("/member")
    public ResponseEntity<Result<MemberResponse>> updateMember(@RequestBody @Valid UpdateMemberRequest request) {
        MemberParam member = memberService.findByMemNo(request.getMemNo());

        member.setName(request.getName());
        member.setPassword(request.getPassword());
        member.setPhone(request.getPhone());
        member.setProfileImage(request.getProfileImage());

        Long memNo = memberService.updateMember(member);

        MemberParam foundMember = memberService.findByMemNo(memNo);
        MemberResponse response = new MemberResponse(foundMember);

        return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
    }

    /**
     * 회원 비밀번호 수정
     *
     * @param request
     */
    @PatchMapping("/member/password")
    public ResponseEntity<Result<MemberResponse>> updateMemberPassword(@RequestBody @Valid UpdatePasswordRequest request) {
        Long memNo = memberService.updatePwd(
                MemberParam.builder().memNo(request.getMemNo()).password(request.getPassword()).build());
        MemberParam foundMember = memberService.findByMemNo(memNo);
        MemberResponse response = new MemberResponse(foundMember);

        return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
    }

    /**
     * 회원 인증 여부 수정
     *
     * @param request
     */
    @PatchMapping("/member/certification")
    public ResponseEntity<Result<MemberResponse>> updateMemberCertification(@RequestBody @Valid UpdateCertificationRequest request) {
        Long memNo = memberService.updateCertification(
                MemberParam.builder()
                        .memNo(request.getMemNo())
                        .certification(request.getCertification())
                        .build());

        MemberParam foundMember = memberService.findByMemNo(memNo);
        MemberResponse response = new MemberResponse(foundMember);

        return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
    }

    /**
     * 회원 권한 수정
     *
     * @param request
     */
    @PatchMapping("/member/role")
    public ResponseEntity<Result<MemberResponse>> updateMemberRole(@RequestBody @Valid UpdateRoleRequest request) {
        Long memNo = memberService.updateRole(
                MemberParam.builder()
                        .memNo(request.getMemNo())
                        .role(request.getRole())
                        .build());

        MemberParam foundMember = memberService.findByMemNo(memNo);
        MemberResponse response = new MemberResponse(foundMember);

        return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
    }

    /**
     * 회원 삭제
     *
     * @param memNo
     * @return
     */
    @DeleteMapping("/member")
    public ResponseEntity<Result<Boolean>> deleteMember(@RequestParam Long memNo) {
        memberService.deleteMember(memNo);

        return ResponseEntity.ok(new Result<>(true));
    }

    /**
     * 로그인 전 입력 정보 유효성 확인
     *
     * @param request
     */
    @PostMapping("/sign-in/check")
    public ResponseEntity<Result<String>> checkInputParams(@RequestBody SigninRequest request) {
        MemberParam foundMember = memberService.findByIdentifierAndProvider(request.getIdentifier(), SocialLoginProvider.none);

        String msg = "";
        if (foundMember != null) { // 해당 이메일의 회원이 존재할 경우
            boolean matched = passwordEncoder.matches(request.getPassword(), foundMember.getPassword());

            log.info("회원이 입력한 값과의 일치 관계 : {}", matched);

            if (matched) { // 해당 회원의 비밀번호와 일치할 경우
                if (foundMember.getCertification().equals(MemberCertificated.Y)) { // 인증된 회원인 경우
                    msg = "matched";
                } else { // 인증되지 않은 회원인 경우
                    msg = "not certified";
                }
            } else { // 해당 회원의 비밀번호와 일치하지 않을 경우
                msg = "didn't matching";
            }
        } else { // 해당 이메일의 회원이 존재하지 않을 경우
            msg = "not user";
        }

        return new ResponseEntity<>(new Result<>(msg), HttpStatus.OK);
    }

    /**
     * 로그아웃
     *
     * @param request
     * @param response
     */
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth); // 세션을 무효화시킴(네이버 로그인 api에서 제공하는 접근 토큰, 리프레시 토큰도 함께 제거될 듯?
        } else throw new IllegalStateException("로그인된 회원 정보가 없습니다.");
    }

    /**
     * 인증 메일 (재)전송
     *
     * @param request
     * @return
     */
    @PostMapping("/cert-mail")
    public ResponseEntity<Result<CertResponse>> sendCertificationEmail(@RequestBody @Valid CertMailRequest request) {// @RequestParam(name = "email")
        SendCertificationNotifyResult result = certificationService.sendCertificationMail(request.getIdentifier(), request.getReason());

        if (result.getResult()) {
            CertResponse reponse = new CertResponse(request.getIdentifier(), CertificationType.EMAIL, result.getCertificationDataDto().getCertKey(), Boolean.TRUE, "");

            return new ResponseEntity<>(new Result<>(reponse), HttpStatus.OK);
        } else throw new IllegalStateException("인증 메일을 전송하는데 실패했습니다. (이메일: " + request.getIdentifier() + ")");
    }

    /**
     * 이메일 인증 검증
     * Get으로 전달된 http://[host]:[post]/sign-up&cert=mail&memNo=[]&certKey=[]으로
     * 빈 페이지에 도달하면 해당 페이지에서 Post 요청 후 결과를 받아 결과에 따라 리다이렉트)
     *
     * @param request
     * @return
     */
    @PostMapping("/cert-mail/validation")
    public ResponseEntity<Result<CertResponse>> validateEmailByCertKey(@RequestBody @Valid CertMailValidationRequest request) {
        // authKey는 해싱된 상태로 링크에 파라미터로 추가되어 이메일 전송됨
        // DB에 저장된 해당 회원의 certKey와 일치하는지 확인하고 정보 수정
        MemberParam member = memberService.findByMemNo(request.getMemNo());
        CertificationDataDto certData = certificationService.findByCertificationIdAndType(member.getIdentifier(), CertificationType.EMAIL);

        if (certData == null) {
            throw new IllegalStateException("인증 정보가 존재하지 않습니다.");
        }

        if (certData.getExpiration().isBefore(LocalDateTime.now())) {
            certificationService.deleteCertification(certData);
            return new ResponseEntity<>(new Result<>(
                    new CertResponse(member.getIdentifier(), CertificationType.EMAIL, request.getCertKey(), Boolean.FALSE, "expired")
            ), HttpStatus.BAD_REQUEST);
        }

        boolean validated = certData.getCertKey().equals(request.getCertKey());

        if (validated) {
            if (certData.getReason().equals(CertificationReason.SIGNUP)) {
                member.setCertification(MemberCertificated.Y);
                memberService.updateCertification(member); // 여기 확인
            } else if (certData.getReason().equals(CertificationReason.FINDPASSWORD)) {
                // 아무 처리하지 않음
            }

            certificationService.deleteCertification(certData);

            return new ResponseEntity<>(new Result<>(
                    new CertResponse(member.getIdentifier(), CertificationType.EMAIL, request.getCertKey(), Boolean.TRUE, certData.getReason().toString())
            ), HttpStatus.OK);

        } else throw new IllegalStateException("인증 정보가 일치하지 않습니다.");
    }

    /**
     * 인증번호를 받은 휴대전화 번호 입력시 해당 번호로 인증번호 전송
     *
     * @param request
     * @return
     */
    @PostMapping("/cert-message")
    public ResponseEntity<Result<CertResponse>> sendCertMessage(@RequestBody @Valid CertMessageValidationRequest request) {

        SendCertificationNotifyResult result = certificationService.sendCertificationMessage(request.getPhone(), request.getReason());

        if (result.getResult()) {
            CertResponse reponse = new CertResponse(request.getPhone(), CertificationType.PHONE, result.getCertificationDataDto().getCertKey(), Boolean.TRUE, "");

            return new ResponseEntity<>(new Result<>(reponse), HttpStatus.OK);
        } else throw new IllegalStateException("인증번호를 전송하는데 실패했습니다. (번호: " + request.getPhone() + ")");
    }

    /**
     * 문자로 전송된 인증번호 검증
     *
     * @param request
     * @return
     */
    @PostMapping("/cert-message/validation") // 인증키 일치 여부 확인 페이지
    public ResponseEntity<Result<CertResponse>> validateCertMessage(@RequestBody @Valid CertMessageValidationRequest request) {

        CertificationDataDto foundData = certificationService.findByCertificationIdAndType(request.getPhone(), CertificationType.PHONE);
        if (foundData.getExpiration().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(
                    new Result<>(
                            new CertResponse(request.getPhone(), CertificationType.PHONE, request.getCertKey(), Boolean.FALSE, "expired")
                    ), HttpStatus.OK);
        } else if (request.getCertKey().equals(foundData.getCertKey())) {
            certificationService.deleteCertification(foundData);
            return new ResponseEntity<>(
                    new Result<>(
                            new CertResponse(request.getPhone(), CertificationType.PHONE, request.getCertKey(), Boolean.TRUE, "")
                    ), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new Result<>(
                            new CertResponse(request.getPhone(), CertificationType.PHONE, request.getCertKey(), Boolean.FALSE, "not matched")
                    ), HttpStatus.OK);
        }
    }
}