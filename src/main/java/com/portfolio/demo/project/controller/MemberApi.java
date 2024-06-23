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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/members")
    public ResponseEntity<Result<List<MemberResponse>>> findMembers(@RequestParam(name = "identifier", required = false) String identifier,
                                                                    @RequestParam(name = "name", required = false) String name,
                                                                    @RequestParam(name = "phone", required = false) String phone,
                                                                    @RequestParam(name = "role", required = false) MemberRole role,
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
        } else {
            members = memberService.findAll(page, size);
        }

        List<MemberResponse> responses = members.stream().map(param -> new MemberResponse(param)).collect(Collectors.toList());
        return ResponseEntity.ok(new Result<>(responses));

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
        if (SocialLoginProvider.NONE.equals(request.getProvider())) {
            // 이메일로 찾는 과정 + identifier 컬럼을 유니크 키로 사용
//            log.info("전송된 유저 정보 : {}", request);
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

    @DeleteMapping("/member")
    public ResponseEntity<Result<Boolean>> deleteMember(@RequestParam Long memNo) {
        memberService.deleteMember(memNo);

        return ResponseEntity.ok(new Result<>(true));
    }

    /**
     * 소셜 로그인 URL 조회
     *
     * @param provider 소셜 로그인 공급자(kakao | naver)
     */
    @GetMapping("/member/oauth2-url")
    public ResponseEntity<Result<Map<String, String>>> getOauthAuthorizationURL(HttpSession session, @RequestParam("provider") SocialLoginProvider provider) {

        SocialLoginParam socialLoginData = null;
        if (provider.equals(SocialLoginProvider.NAVER)) {
            socialLoginData = naverLoginApiUtil.getAuthorizeData();
            session.setAttribute("naverState", socialLoginData.getState());
        } else if(provider.equals(SocialLoginProvider.KAKAO)) {
            socialLoginData = kakaoLoginApiUtil.getAuthorizeData();
            session.setAttribute("kakaoState", socialLoginData.getState());
        }

        Map<String, String> map = new HashMap<>();
        map.put("provider", provider.toString());
        map.put("url", socialLoginData != null ? socialLoginData.getApiUrl() : null);

        return new ResponseEntity<>(new Result<>(map), HttpStatus.OK);
    }

    /**
     * 소셜 로그인 API
     * 액세스 토큰 요청
     *
     * @param providerStr
     */
    @GetMapping("/member/oauth2/{provider}")
    public ResponseEntity<Result<Boolean>> getLoginToken(HttpSession session, HttpServletRequest request,
                                                                          @PathVariable("provider") String providerStr) {
        SocialLoginProvider provider = SocialLoginProvider.valueOf(providerStr.toUpperCase());

        String state = request.getParameter("state");
        String storedState = String.valueOf(session.getAttribute(providerStr.toLowerCase() + "State"));

        if (!state.equals(storedState)) {
            return new ResponseEntity<>(new Result<>(Boolean.FALSE), HttpStatus.UNAUTHORIZED); // 401
        }

        if (provider.equals(SocialLoginProvider.NAVER)) {
            Map<String, String> res = naverLoginApiUtil.getTokens(request);
            String access_token = res.get("access_token");
            String refresh_token = res.get("refresh_token");

            session.setAttribute("naverCurrentAT", access_token);
            session.setAttribute("naverCurrentRT", refresh_token);
        } else if (provider.equals(SocialLoginProvider.KAKAO)) {
            Map<String, String> res = kakaoLoginApiUtil.getTokens(request);
            String access_token = res.get("access_token");
            String refresh_token = res.get("refresh_token");

            session.setAttribute("kakaoCurrentAT", access_token);
            session.setAttribute("kakaoCurrentRT", refresh_token);
        }

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
    }

    /**
     * Oauth2.0 - Profile 조회
     * access token을 사용해 사용자 프로필 조회 api 호출
     *
     * @param providerStr
     * @throws ParseException
     */
    @GetMapping("/member/oauth2/profile/{provider}")
    public ResponseEntity<Result<SocialLoginResponse>> getSocialProfile(HttpSession session,
                                                                        @PathVariable("provider") String providerStr) throws ParseException {
        SocialLoginProvider provider = SocialLoginProvider.valueOf(providerStr.toUpperCase());
        String token = String.valueOf(session.getAttribute(providerStr.toLowerCase() + "CurrentAT"));

        SocialLoginResponse response = new SocialLoginResponse();

        if (provider.equals(SocialLoginProvider.NAVER)) {
            SocialProfileParam profile = naverProfileApiUtil.getProfile(token); // Map으로 사용자 데이터 받기
            log.info("Oauth2.0 get profile: provider: {}, profile : {}", profile);

            // 해당 프로필과 일치하는 회원 정보가 있는지 조회 후, 있다면 role 값(ROLE_USER) 반환
            MemberParam member = memberService.findByIdentifier(profile.getId());

            response.setProvider(SocialLoginProvider.NAVER);
            response.setIdentifier(profile.getId());

            if (member != null) { // info.getRole().equals("ROLE_USER")
                if (member.getProvider().equals(SocialLoginProvider.NAVER)) {
                    Authentication auth = memberService.getAuthentication(member);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    session.setAttribute("member", member);

                    response.setResult(Boolean.TRUE);
                    response.setStatus(SocialLoginStatus.LOGIN_SUCCESS);
                    response.setMessage("");
                } else { // none
                    response.setResult(Boolean.FALSE);
                    response.setStatus(SocialLoginStatus.NOT_SOCIAL_MEMBER);
                    response.setMessage("소셜 로그인 정보로 가입된 회원이 아닙니다. 이메일로 로그인해주세요.");
                }
            } else {
                response.setResult(Boolean.FALSE);
                response.setStatus(SocialLoginStatus.SIGNUP_REQUIRED);
                response.setMessage("가입되지 않은 회원입니다.");

                session.setAttribute("profile", profile);
            }
        } else if (provider.equals(SocialLoginProvider.KAKAO)) {
            SocialProfileParam profile = kakaoProfileApiUtil.getProfile(token); // access_token
            log.info("Oauth2.0 get profile: provider: {}, profile : {}", profile);

            MemberParam member = memberService.findByIdentifier(profile.getId());

            response.setProvider(SocialLoginProvider.KAKAO);
            response.setIdentifier(profile.getId());

            if (member != null) {
                if (member.getProvider().equals(SocialLoginProvider.KAKAO)) {
                    Authentication auth = memberService.getAuthentication(member);
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    session.setAttribute("member", member);

                    response.setResult(Boolean.TRUE);
                    response.setStatus(SocialLoginStatus.LOGIN_SUCCESS);
                    response.setMessage("");
                } else { // none
                    response.setResult(Boolean.FALSE);
                    response.setStatus(SocialLoginStatus.NOT_SOCIAL_MEMBER);
                    response.setMessage("소셜 로그인 정보로 가입된 회원이 아닙니다. 이메일로 로그인해주세요.");
                }
            } else { // 필요없는 코드
                response.setResult(Boolean.FALSE);
                response.setStatus(SocialLoginStatus.SIGNUP_REQUIRED);
                response.setMessage("가입되지 않은 회원입니다.");

                session.setAttribute("profile", profile);
            }
        }

        log.info("Oauth2.0 소셜 로그인(유형: {}, 결과: {}, identifier: {}, 메세지: {})",
                response.getProvider(), response.getResult(), response.getIdentifier(), response.getMessage());

        return ResponseEntity.ok(new Result<>(response));
    }

    @GetMapping("/member/oauth2/profile-server")
    public ResponseEntity<Result<SocialProfileParam>> getProfileFromSession(HttpSession session) {
        SocialProfileParam profile = (SocialProfileParam) session.getAttribute("profile");
        return ResponseEntity.ok(new Result<>(profile));
    }

    @DeleteMapping("/member/oauth2/profile-server")
    public ResponseEntity<Result<Boolean>> removeProfileFromSession(HttpSession session) {
        SocialProfileParam profile = (SocialProfileParam) session.getAttribute("profile");

        if (profile != null) {
            session.removeAttribute("profile");
        }

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
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
}

