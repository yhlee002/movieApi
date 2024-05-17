package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.util.*;
import com.portfolio.demo.project.service.MailService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.service.PhoneMessageService;
import com.portfolio.demo.project.dto.MemberParam;
import com.portfolio.demo.project.dto.SocialLoginParam;
import com.portfolio.demo.project.dto.SocialProfileParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberApi {

    private final MemberService memberService;

    private final PhoneMessageService phoneMessageService;

    private final MailService mailService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final TempKey tempKey = new TempKey();

    private final CertUtil certUtil = new CertUtil(passwordEncoder, tempKey);

    private final NaverLoginApiUtil naverLoginApiUtil;

    private final NaverProfileApiUtil naverProfileApiUtil;

    private final KakaoLoginApiUtil kakaoLoginApiUtil;

    private final KakaoProfileApiUtil kakaoProfileApiUtil;

    /**
     * 현재 세션의 회원 정보 조회
     *
     * @param session
     * @return
     */
    @GetMapping("/member/auth")
    public ResponseEntity<MemberParam> getCurrentMember(HttpSession session) {
        MemberParam member = (MemberParam) session.getAttribute("member");
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    /**
     * 특정 식별번호의 회원 정보 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/member/{id}")
    public ResponseEntity<MemberParam> getMember(@PathVariable Long id) {
        MemberParam user = memberService.findByMemNo(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * 회원가입
     *
     * @param session
     * @param request
     * @return
     */
    @PostMapping("/member")
    public ResponseEntity<MemberParam> signUp(HttpSession session, @RequestBody CreateMemberRequest request) {
        if ("none".equals(request.getProvider())) {
            // 이메일로 찾는 과정 + identifier 컬럼을 유니크 키로 사용
            log.info("전송된 유저 정보 : {}", request);
            Long memNo = memberService.updateMember(
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

            Map<String, String> result = mailService.sendGreetingMail(request.getIdentifier());
            if (result.get("resultCode").equals("success")) {
                return new ResponseEntity<>(created, HttpStatus.CREATED);
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
                    .role("RULE_USER")
                    .profileImage(profileImage)
                    .certification("Y")
                    .build();

            Long memNo = memberService.saveOauthMember(member);

            MemberParam created = memberService.findByMemNo(memNo);

            log.info("생성된 유저 식별번호 : {}", created.getMemNo());

            return new ResponseEntity<>(created, HttpStatus.CREATED);
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
    public ResponseEntity<MemberParam> findMember(@RequestParam(name = "identifier", required = false) String identifier,
                                                  @RequestParam(name = "name", required = false) String name,
                                                  @RequestParam(name = "phone", required = false) String phone
    ) {

        MemberParam member = null;
        if (identifier != null && !identifier.isEmpty()) {
            member = memberService.findByIdentifier(identifier);
            log.info("이메일({})을 통해 찾은 사용자 식별번호 : {}", phone, member.getMemNo());
        } else if (name != null && !name.isEmpty()) {
            member = memberService.findByName(name); // validateDuplicationName
            log.info("이름({})를 통해 찾은 사용자 식별번호 : {}", phone, member.getMemNo());
        } else if (phone != null && !phone.isEmpty()) {
            member = memberService.findByPhone(phone);
            log.info("휴대번호({})를 통해 찾은 사용자 식별번호 : {}", phone, member.getMemNo());
        }

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    /**
     * 소셜 로그인 URL 조회
     *
     * @param session
     */
    @GetMapping("/member/oauth2-url")
    public ResponseEntity<Map<String, String>> getOauthAuthorizationURL(HttpSession session) throws UnsupportedEncodingException {

        SocialLoginParam naverLoginData = naverLoginApiUtil.getAuthorizeData();
        SocialLoginParam kakaoLoginData = kakaoLoginApiUtil.getAuthorizeData();

        session.setAttribute("stateNaver", naverLoginData.getProvider());
        session.setAttribute("stateKakao", kakaoLoginData.getState());

        Map<String, String> map = new HashMap<>();
        map.put("naver", naverLoginData.getApiUrl());
        map.put("kakao", kakaoLoginData.getApiUrl());
        return new ResponseEntity<>(map, HttpStatus.OK);
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
    @ResponseBody
    public ResponseEntity<String> checkInputParams(@RequestBody SigninRequest request) {
        MemberParam foundMember = memberService.findByIdentifier(request.getIdentifier());

        String msg = "";
        if (foundMember != null) { // 해당 이메일의 회원이 존재할 경우
            boolean matched = passwordEncoder.matches(request.getPassword(), foundMember.getPassword());

            log.info("회원이 입력한 값과의 일치 관계 : {}", matched);

            if (matched) { // 해당 회원의 비밀번호와 일치할 경우
                if (foundMember.getCertification().equals("Y")) { // 인증된 회원인 경우
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

        return new ResponseEntity<>(msg, HttpStatus.OK);
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
    public ResponseEntity<SocialProfileParam> getOauthProfile(HttpSession session) {
        SocialProfileParam profile = (SocialProfileParam) session.getAttribute("profile");
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    /**
     * 인증 메일 (재)전송
     *
     * @param email
     * @return
     */
    @ResponseBody
    @GetMapping("/cert-mail")
    public Map<String, String> sendCertMail(@RequestParam(name = "email") String email) {
        return mailService.sendGreetingMail(email);
    }

    /**
     * 이메일 인증 검증
     * Get으로 전달된 http://[host]:[post]/sign-up/cert-mail?memNo=[]&certKey=[]으로
     * 빈 페이지에 도달하면 해당 페이지에서 Post 요청 후 결과를 받아 결과에 따라 리다이렉트)
     *
     * @param request
     * @return
     */
    @PostMapping("/cert-mail")
    public ResponseEntity<MemberParam> validateEmailByCertKey(@RequestBody CertMailValidationRequest request) {
        // authKey는 해싱된 상태로 링크에 파라미터로 추가되어 이메일 전송됨
        // DB에 저장된 해당 회원의 certKey와 일치하는지 확인하고 정보 수정
        MemberParam member = memberService.findByMemNo(request.getMemNo());
        Boolean validated = certUtil.validateCertKey(member, request.getCertKey());

        if (validated) {
            member = certUtil.changeCertStatus(member);
            memberService.updateMember(member);

            return new ResponseEntity<>(member, HttpStatus.OK);

        } else throw new IllegalStateException("인증 정보가 일치하지 않습니다.");
    }

    /**
     * 인증번호를 받은 휴대전화 번호 입력시 해당 번호로 인증번호 전송
     *
     * @param session
     * @param phone
     * @return
     */
    @PostMapping("/cert-message")
    public ResponseEntity<Boolean> sendCertMessage(HttpSession session, @RequestParam String phone) {

        Map<String, String> resultMap = phoneMessageService.sendCertificationMessage(phone);
        String result = resultMap.get("result");
        String phoneAuthKey = resultMap.get("certKey");

        if (result.equals("success")) {
            session.setAttribute("phoneAuthCertKey", passwordEncoder.encode(phoneAuthKey));
            session.setAttribute("phoneNum", phone);

            log.info("phoneAuthKey 인코딩 전 값 : {}", phoneAuthKey);

            return new ResponseEntity<>(true, HttpStatus.OK);
        }

        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    /**
     * 문자로 전송된 인증번호 검증
     *
     * @param session
     * @param certKey
     * @return
     */
    @GetMapping("/cert-message") // 인증키 일치 여부 확인 페이지
    public ResponseEntity<Map<String, String>> validateCertMessage(HttpSession session, @RequestParam String certKey) {
        Map<String, String> result = new HashMap<>();

        String phoneAuthCertKey = (String) session.getAttribute("phoneAuthCertKey");

        if (passwordEncoder.matches(certKey, phoneAuthCertKey)) {
            result.put("resultCode", "success");
            result.put("phoneNum", (String) session.getAttribute("phoneNum"));

            session.removeAttribute("phoneNum");
        } else {
            result.put("resultCode", "fail");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Data
    static class CreateMemberRequest {
        private String identifier;
        private String password;
        private String name;
        private String phone;
        private String provider;
    }

    @Data
    static class SigninRequest {
        private String identifier;
        private String password;
    }

    @Data
    static class CertMailValidationRequest {
        private Long memNo;
        private String certKey;
    }
}

