package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.*;
import com.portfolio.demo.project.vo.BoardImpVO;
import com.portfolio.demo.project.vo.CommentImpVO;
import com.portfolio.demo.project.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MyPageApi {

    private final MemberService memberService;

    private final BoardImpService boardImpService;

    private final CommentImpService commentImpService;

    private final PhoneMessageService messageService;

    private final RememberMeTokenService rememberMeTokenService;

    @GetMapping("/mypage")
    public String mypage(Model model, HttpSession session) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        Member member = memberService.findByMemNo(memberVO.getMemNo());
        model.addAttribute("boardList",
                boardImpService.getImpsByMember(member, 5)
                        .stream().map(BoardImpVO::create).toList()
        );
        model.addAttribute("commList",
                commentImpService.getCommentsByMember(member, 5)
                        .stream().map(CommentImpVO::create).toList()
        );

        return "mypage/memberInfo";
    }

    @GetMapping("/mypage/imp-board")
    public String myImpBoard(Model model, HttpSession session, @RequestParam(name = "p", defaultValue = "1") int pageNum) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        Member member = memberService.findByMemNo(memberVO.getMemNo());
        model.addAttribute("pagenation", boardImpService.getImpsByMember(member, pageNum));

        return "mypage/impBoards";
    }

    @GetMapping("/mypage/imp-comment")
    public String myImpComment(Model model, HttpSession session, @RequestParam(name = "p", defaultValue = "1") int pageNum) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        Member member = memberService.findByMemNo(memberVO.getMemNo());

        List<CommentImpVO> list = commentImpService.getCommentsByMember(member, pageNum).stream().map(CommentImpVO::create).toList();
        model.addAttribute("list", list);
        return "mypage/impComments";
    }

    @GetMapping("/mypage/modify_info")
    public String modifyUserInfo() {
        return "mypage/modifyInfo";
    }

    @PatchMapping("/mypage/modify_info")
    public String modifyUserInfoProc(HttpSession session, @RequestParam("memNo") Long memNo, @RequestParam("nickname") String name,
                                     @RequestParam(name = "pwd", required = false) String pwd, @RequestParam("phone") String phone,
                                     @RequestParam(name = "profileImage", required = false) String profileImage) {

        Member inputMember = Member.builder()
                .memNo(memNo)
                .name(name)
                .password(pwd)
                .phone(phone)
                .profileImage(profileImage)
                .build();

        Member createdMember = memberService.updateMember(inputMember); // 비밀번호 체크는 서비스단에서 실시

        session.setAttribute("member", MemberVO.create(createdMember));

        return "redirect:/mypage";
    }

    @DeleteMapping("/mypage/info")
    public String deleteMember(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws BadRequestException {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        log.info("delete user info: {}", memberVO.toString());

        memberService.deleteMember(memberVO.getMemNo());
        rememberMeTokenService.removeUserTokens(memberVO.getIdentifier()); // DB의 persistent_logins 토큰 제거 (쿠키는 로그아웃 로직에서 자동 제거)

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
    }

    @GetMapping("/mypage/uploadProfileImage")
    public String uploadProfileImageForm() {
        /* 프로필 이미지 변경시 업로드 페이지로 감 */
        return "mypage/uploadProfileImageForm";
    }

    @ResponseBody
    @RequestMapping(value = "/mypage/uploadProfileImage_proc")
    public JsonObject uploadProfileImageProc(@RequestParam("file") MultipartFile file) {
        /* 실제로 넘어온 이미지를 서버에 업로드하고 DB의 프로필 이미지 경로를 수정 */
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Res_ko_KR_keys");
        String fileRoot = resourceBundle.getString("profileImageFileRoot");
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 마지막 '.'이하의 부분이 확장자
        String savedFileName = UUID.randomUUID() + extension;

        File newFile = new File(fileRoot + savedFileName);
        log.info("saved file path: {}" + newFile.getAbsolutePath());

        JsonObject jsonObject = new JsonObject();
        try {
            InputStream inputStream = file.getInputStream();
            FileUtils.copyInputStreamToFile(inputStream, newFile);
            jsonObject.addProperty("url", "/profileImage/" + savedFileName);
            jsonObject.addProperty("responseCode", "success");
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.deleteQuietly(newFile);
            jsonObject.addProperty("responseCode", "error");
        }

        return jsonObject;
    }

    /**
     * 새로운 핸드폰 번호 입력 페이지
     */
    @GetMapping("/mypage/modify_info/phone")
    public String phoneCkForm() {
        return "mypage/modifyInfo_phoneUpdate1";
    }

    /**
     * 새로운 핸드폰 번호 유효성 검사(존재 여부 확인)
     *
     * @param phone
     * @return 해당 핸드폰 번호로 저장되어있는 사용자 수
     */
    @GetMapping("/mypage/modify_info/phone/check/exist")
    public ResponseEntity<Boolean> phoneCkProc(@RequestParam String phone) {
        return new ResponseEntity<>(memberService.existsByPhone(phone)
                , HttpStatus.OK);
    }

    /**
     * 인증번호 전송
     *
     * @param phone
     * @return 인증번호 전송 결과
     */
    @PostMapping("/mypage/modify_info/phone/check")
    public ResponseEntity<String> phoneCertForm(HttpSession session, @RequestParam String phone) {
        Map<String, String> resultMap = messageService.sendCertificationMessage(phone);
        String result = resultMap.get("result");
        String certKey = resultMap.get("certKey");

        if (result.equals("success")) {
            session.setAttribute("phoneNum", phone);
            session.setAttribute("certKey", certKey);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        // Todo. Error Status 수정
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/mypage/modify_info/phone2")
    public String updatePhoneForm() {
        return "mypage/modifyInfo_phoneUpdate2";
    }

    /**
     * 인증번호 일치 여부 검증
     *
     * @param certKey
     * @return 검증 결과
     */
    @ResponseBody
    @RequestMapping("/mypage/modify_info/phone/check/certmessage")
    public ResponseEntity<Map<String, String>> checkPhoneCertVal(HttpSession session, String certKey) {
        Map<String, String> result = new HashMap<>();
        String phone = (String) session.getAttribute("phoneNum");
        session.removeAttribute("phoneNum");
        String userCertKey = (String) session.getAttribute("certKey");
        session.removeAttribute("certKey");

        if (certKey.equals(userCertKey)) {
            result.put("resultCode", "true");
            result.put("phoneNum", phone);
        } else {
            result.put("resultCode", "false");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
