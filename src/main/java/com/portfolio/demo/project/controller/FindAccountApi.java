package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.controller.member.certkey.CertificationDataDto;
import com.portfolio.demo.project.entity.certification.CertificationReason;
import com.portfolio.demo.project.entity.certification.CertificationType;
import com.portfolio.demo.project.dto.Result;
import com.portfolio.demo.project.service.CertificationService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.dto.MemberParam;
import com.portfolio.demo.project.service.certification.SendCertificationNotifyResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FindAccountApi {

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final CertificationService certificationService;

    @ResponseBody
    @RequestMapping("/findEmail/phoneCk")
    // 존재하는 번호인지, aouth인증 회원인지 Ajax로 검증 - 있는 이메일이라면 문자 보낼지 확인 -> 확인시 문자 전송 후 /find-email2로 이동
    public ResponseEntity<Result<Map<String, String>>> findEmailCheckPhone(String phone) {
        Map<String, String> map = new HashMap<>();
        MemberParam member = memberService.findByPhone(phone);
        if (member != null && member.getProvider().equals("none")) {
            map.put("resultCode", "exist");
        } else if (member != null && (member.getProvider().equals("naver") || member.getProvider().equals("kakao"))) {
            map.put("resultCode", "oauth-member");
        } else { // member == null
            map.put("resultCode", "not exist");
        }

        return new ResponseEntity<>(new Result<>(map), HttpStatus.OK);
    }

//    @RequestMapping("/findEmail/checkCertKey") // 인증 번호 입력하는 페이지
//    public String sendCertMessage(HttpSession session, @RequestParam(name = "p") String phone) {
//        Map<String, String> resultMap = messageService.sendCertificationMessage(phone);
//        String result = resultMap.get("result");
//        String certKey = resultMap.get("certKey");
//
//        if (result.equals("success")) {
//            // 메세지 전송 후 인증번호를 해시값으로 변형해 세션에 저장
//            session.setAttribute("certKey", passwordEncoder.encode(certKey));
//            session.setAttribute("phoneNum", phone);
//        }
//
//        return "sign-in/findEmailForm2";
//    }

    @ResponseBody
    @RequestMapping("/findEmail/checkAuthKey")
    public ResponseEntity<Result<Map<String, String>>> checkCertKey(HttpSession session, String certKeyInput) {
        Map<String, String> map = new HashMap<>();

        String certKey = (String) session.getAttribute("certKey");
        session.removeAttribute("certKey"); // 세션에서 가져온 뒤 세션에서 제거

        if (passwordEncoder.matches(certKeyInput, certKey)) {
            map.put("resultCode", "true");
        } else {
            map.put("resultCode", "fail");
        }

        return new ResponseEntity<>(new Result<>(map), HttpStatus.OK);
    }

//    @RequestMapping("/findEmail/result")
//    public String getEmail(HttpSession session, Model model) {
//        String phone = (String) session.getAttribute("phoneNum");
//        session.removeAttribute("phoneNum");
//
//        MemberParam member = memberService.findByPhone(phone);
//        model.addAttribute("email", member.getIdentifier());
//        return "sign-in/findEmailResult";
//    }


    /* 비밀번호 찾기 */

//    @RequestMapping("/findPwd") // 인증할 이메일 입력 -> Ajax 검증 후, 이메일 전송(certKey 포함 링크) -> 링크 클릭시 비밀번호 변경 페이지로 이동
//    public String findPwd() {
//        return "sign-in/findPwdForm";
//    }

//    @ResponseBody
//    @RequestMapping("/findPwd/checkEmail")
//    public String findPwd2(@RequestParam String email) {
//        MemberParam member = memberService.findByIdentifier(email);
//        String result = "";
//        if (member != null) {
//            result = "exist";
//        } else {
//            result = "not exist";
//        }
//        log.info("비밀번호찾기 이메일 조회 - result : {}", result);
//        return result;
//    }

    @ResponseBody
    @RequestMapping("/findPwd/sendMail") // 메일 전송(Ajax 비동기)
    public ResponseEntity<Result<SendCertificationNotifyResult>> findPwd3(@RequestParam String email) {
        SendCertificationNotifyResult sendResult = certificationService.sendCertificationMail(email, CertificationReason.FINDPASSWORD);

        return new ResponseEntity<>(new Result<>(sendResult), HttpStatus.OK);
    }

    // TODO. 수정 예정
    @RequestMapping("/findPwd/cert-mail") // 메일 속 인증 링크가 연결되는 페이지
    public ResponseEntity<Result<Boolean>> certificationEmail(@RequestParam Long memNo, @RequestParam String certKey) {
        MemberParam member = memberService.findByMemNo(memNo);
        CertificationDataDto certData = certificationService.findByCertificationIdAndType(member.getIdentifier(), CertificationType.EMAIL);
        if (certData.getCertKey().equals(certKey)) {
            return new ResponseEntity<>(new Result<>(Boolean.TRUE), HttpStatus.OK); // 추후 패스워드 변경 페이지로
        } else {
            return new ResponseEntity<>(new Result<>(Boolean.TRUE), HttpStatus.BAD_REQUEST);
        }
    }

//    @RequestMapping("/findPwd/updatePwd")
//    public String updatePwdForm() {
//        return "sign-in/updatePwd";
//    }

    @ResponseBody
    @RequestMapping("/findPwd/updatePwdProc") // 비밀번호 변경 후 결과 알려주기 (js에서 alert로 알려준 뒤 로그인 페이지로 redirect)
    public void updatePwdProc(HttpSession session, @RequestBody @Valid MemberParam memberParam) { // as is: @RequestParam String pwd, to be @RequestBody @Valid MemberParam
        memberService.updatePwd(memberParam);
    }

}
