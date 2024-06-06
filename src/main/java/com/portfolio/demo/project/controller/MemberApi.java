package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.controller.member.certkey.*;
import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.entity.certification.CertificationReason;
import com.portfolio.demo.project.entity.certification.CertificationType;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.service.CertificationService;
import com.portfolio.demo.project.service.certification.SendCertificationNotifyResult;
import com.portfolio.demo.project.util.*;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.dto.MemberParam;
import com.portfolio.demo.project.dto.SocialLoginParam;
import com.portfolio.demo.project.dto.SocialProfileParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
        String principal = (String) auth.getPrincipal();

        if (!principal.equals("anonymousUser")) {
            MemberParam member = memberService.findByIdentifier(principal);
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
        return new ResponseEntity<>(new Result<>(new MemberResponse(member)), HttpStatus.OK);
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
                                                             @RequestParam(name = "name", required = false) String name,
                                                             @RequestParam(name = "phone", required = false) String phone
    ) {

        MemberParam member = null;
        String condition = null;
        if (identifier != null && !identifier.isEmpty()) {
            member = memberService.findByIdentifier(identifier);
            condition = "identifier";
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

    /**
     * 회원가입
     *
     * @param session
     * @param request
     * @return
     */
    @PostMapping("/member")
    public ResponseEntity<Result<MemberResponse>> signUp(HttpSession session, @RequestBody @Valid CreateMemberRequest request) {
        if ("none".equals(request.getProvider())) {
            // 이메일로 찾는 과정 + identifier 컬럼을 유니크 키로 사용
//            log.info("전송된 유저 정보 : {}", request);
            Long memNo = memberService.saveMember(
                    MemberParam.builder()
                            .identifier(request.getIdentifier())
                            .name(request.getName())
                            .password(request.getPassword())
                            .provider(request.getProvider())
                            .phone(request.getPhone())
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
            SocialProfileParam profile = (SocialProfileParam) session.getAttribute("profile");
            log.info("조횐 프로필 : {}", profile);

            String profileImage = profile.getProfileImageUrl();
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
     * 소셜 로그인 URL 조회
     *
     * @param session
     */
    @GetMapping("/member/oauth2-url")
    public ResponseEntity<Result<Map<String, String>>> getOauthAuthorizationURL(HttpSession session) throws UnsupportedEncodingException {

        SocialLoginParam naverLoginData = naverLoginApiUtil.getAuthorizeData();
        SocialLoginParam kakaoLoginData = kakaoLoginApiUtil.getAuthorizeData();

        session.setAttribute("stateNaver", naverLoginData.getProvider());
        session.setAttribute("stateKakao", kakaoLoginData.getState());

        Map<String, String> map = new HashMap<>();
        map.put("naver", naverLoginData.getApiUrl());
        map.put("kakao", kakaoLoginData.getApiUrl());
        return new ResponseEntity<>(new Result<>(map), HttpStatus.OK);
    }

    /**
     * 네이버 로그인 API TODO. 수정 예정
     *
     * @param session
     * @param request
     * @param rttr
     * @return
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @GetMapping("/member/oauth2/naver")
    public String oauthNaver(HttpSession session, HttpServletRequest request, RedirectAttributes rttr) throws UnsupportedEncodingException, ParseException {

        Map<String, String> res = naverLoginApiUtil.getTokens(request);
        String access_token = res.get("access_token");
        String refresh_token = res.get("refresh_token");

        session.setAttribute("naverCurrentAT", access_token);
        session.setAttribute("naverCurrentRT", refresh_token);

        /* access token을 사용해 사용자 프로필 조회 api 호출 */
        SocialProfileParam profile = naverProfileApiUtil.getProfile(access_token); // Map으로 사용자 데이터 받기
        log.info("profile : {}", profile);

        /* 해당 프로필과 일치하는 회원 정보가 있는지 조회 후, 있다면 role 값(ROLE_USER) 반환 */
        MemberParam member = memberService.findByIdentifierAndProvider(profile.getId(), "naver");

        if (member != null) { // info.getRole().equals("ROLE_USER")
            log.info("회원정보가 존재합니다. \n회원정보 : " + member.toString());

            if (member.getProvider().equals("naver")) {
                Authentication auth = memberService.getAuthentication(member);
                SecurityContextHolder.getContext().setAuthentication(auth);

                session.setAttribute("member", member);

                return "redirect:/";
            } else { // none
                log.info("소셜 로그인 정보로 가입된 회원이 아닙니다. 이메일로 로그인해주세요.");

                rttr.addFlashAttribute("oauthMsg", "소셜 로그인 정보로 가입된 회원이 아닙니다. 이메일로 로그인해주세요.");
                return "redirect:/sign-in";
            }

        } else {
            log.info("소셜 로그인 진행 결과: 가입되지 않은 회원입니다.");

            session.setAttribute("profile", profile);
            session.setAttribute("provider", "naver");
            rttr.addFlashAttribute("oauthMsg", "가입된 회원이 아닙니다. 회원가입을 진행해주세요.");

            return "redirect:/sign-in";
        }
    }

    /**
     * 카카오 로그인 API TODO. 수정 예정
     *
     * @param session
     * @param request
     * @param rttr
     * @return
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    @GetMapping("/member/oauth2/kakao")
    public String oauthKakao(HttpSession session, HttpServletRequest request, RedirectAttributes rttr) throws UnsupportedEncodingException, ParseException {

        Map<String, String> res = kakaoLoginApiUtil.getTokens(request);
        String access_token = res.get("access_token");
        String refresh_token = res.get("refresh_token");

        session.setAttribute("kakaoCurrentAT", access_token);
        session.setAttribute("kakaoCurrentRT", refresh_token);

        SocialProfileParam profile = kakaoProfileApiUtil.getProfile(access_token);
        log.info("profile : {}", profile);

        MemberParam member = memberService.findByIdentifierAndProvider(profile.getId(), "kakao");
        if (member != null) {
            log.info("회원정보가 존재합니다. \n회원정보 : " + member.toString());

            if (member.getProvider().equals("kakao")) {
                Authentication auth = memberService.getAuthentication(member);
                SecurityContextHolder.getContext().setAuthentication(auth);

                MemberParam memberParam = member;
                session.setAttribute("member", memberParam);

                return "redirect:/";
            } else { // none
                log.info("소셜 로그인 정보로 가입된 회원이 아닙니다. 이메일로 로그인해주세요.");

                rttr.addFlashAttribute("oauthMsg", "소셜 로그인 정보로 가입된 회원이 아닙니다. 이메일로 로그인해주세요.");
                return "redirect:/sign-in";
            }
        } else { // 필요없는 코드
            log.info("소셜 로그인 진행 결과: 가입되지 않은 회원입니다.");

            session.setAttribute("profile", profile);
            session.setAttribute("provider", "kakao");
            rttr.addFlashAttribute("oauthMsg", "가입된 회원이 아닙니다. 회원가입을 진행해주세요.");
            return "redirect:/sign-in";
        }
    }

    /**
     * 로그인 전 입력 정보 유효성 확인
     *
     * @param request
     * @return
     */
    @PostMapping("/sign-in/check")
    public ResponseEntity<Result<String>> checkInputParams(@RequestBody SigninRequest request) {
        MemberParam foundMember = memberService.findByIdentifier(request.getIdentifier());

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
     * 회원가입시 소셜 API로 접근한 경우 소셜 프로필 정보 조회
     *
     * @param session
     * @return
     */
    @GetMapping("/member/oauth-profile")
    public ResponseEntity<Result<SocialProfileParam>> getOauthProfile(HttpSession session) {
        SocialProfileParam profile = (SocialProfileParam) session.getAttribute("profile");
        return new ResponseEntity<>(new Result<>(profile), HttpStatus.OK);
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

    @Data
    static class MemberResponse {
        private Long memNo;
        private String identifier;
        private String name;
        private String phone;
        private String provider;
        private String profileImage;
        private MemberRole role;
        private MemberCertificated certification;
        private String regDate;

        public MemberResponse(MemberParam member) {
            this.memNo = member.getMemNo();
            this.identifier = member.getIdentifier();
            this.name = member.getName();
            this.phone = member.getPhone();
            this.provider = member.getProvider();
            this.profileImage = member.getProfileImage();
            this.role = member.getRole();
            this.certification = member.getCertification();
            this.regDate = member.getRegDate();
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String identifier;
        private String password;
        @NotEmpty
        private String name;
        @NotEmpty
        private String phone;
        @NotEmpty
        private String provider;
    }

    @Data
    static class UpdateMemberRequest {
        @NotNull
        private Long memNo;
        private String password;
        private String name;
        private String phone;
        private String provider;
        private String profileImage;
    }

    @Data
    static class UpdatePasswordRequest {
        @NotNull
        private Long memNo;
        @NotEmpty
        private String password;
    }

    @Data
    static class UpdateCertificationRequest {
        @NotNull
        private Long memNo;
        @NotNull
        private MemberCertificated certification;
    }

    @Data
    static class UpdateRoleRequest {
        @NotNull
        private Long memNo;
        @NotNull
        private MemberRole role;
    }

    @Data
    static class SigninRequest {
        @NotEmpty
        private String identifier;
        private String password;
    }

    @Data
    static class CertMailRequest {
        @NotEmpty
        private String identifier;
        @NotNull
        private CertificationReason reason;
    }

    @Data
    static class CertMailValidationRequest {
        @NotNull
        private Long memNo;
        @NotEmpty
        private String certKey;
    }

}

