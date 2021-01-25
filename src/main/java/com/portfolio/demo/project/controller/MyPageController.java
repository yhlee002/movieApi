package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.*;
import com.portfolio.demo.project.vo.CommentImpPagenationVO;
import com.portfolio.demo.project.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

@Slf4j
@Controller
public class MyPageController {

    @Autowired
    MemberService memberService;

    @Autowired
    BoardImpService boardImpService;

    @Autowired
    CommentImpService commentImpService;

    @Autowired
    PhoneMessageService messageService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RememberMeTokenService rememberMeTokenService;

    @RequestMapping("/mypage")
    public String mypage(Model model, HttpSession session) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        model.addAttribute("boardList", boardImpService.getMyImpTop5(memberVO.getMemNo()));
        model.addAttribute("commList", commentImpService.getMyCommTop5(memberVO.getMemNo()));

        return "mypage/memberInfo";
    }

    @RequestMapping("/mypage/imp-board")
    public String myImpBoard(Model model, HttpSession session, @RequestParam(name = "p", defaultValue = "1") int pageNum) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        boardImpService.setMemNo(memberVO.getMemNo());
        model.addAttribute("pagenation", boardImpService.getMyImpListView(pageNum));

        return "/mypage/impBoards";
    }

    @RequestMapping("/mypage/imp-comment")
    public String myImpComment(Model model, HttpSession session, @RequestParam(name = "p", defaultValue = "1") int pageNum) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        commentImpService.setMemNo(memberVO.getMemNo());
        CommentImpPagenationVO pagenation = commentImpService.getMyCommListView(pageNum);
        model.addAttribute("pagenation", pagenation);

        return "/mypage/impComments";
    }

    @RequestMapping("/mypage/modify_info")
    public String modifyUserInfo() {
        return "mypage/modifyInfo";
    }

    @RequestMapping("/mypage/modify_info_proc")
    public String modifyUserInfoProc(HttpSession session, @RequestParam("memNo") Long memNo, @RequestParam("nickname") String name,
                                     @RequestParam(name = "pwd", required = false) String pwd, @RequestParam("phone") String phone,
                                     @RequestParam(name = "profileImage", required = false) String profileImage) {
        log.info("memNo : " + memNo + ", name : " + name + ", pwd : " + pwd + ", phone : " + phone + ", profileImage : " + profileImage);
        Member originMember = memberService.findByMemNo(memNo);
        if (originMember != null) {
            if (name != "") { // 이미 있음
                originMember.setName(name);
            }
            if (pwd != "") {
                originMember.setPassword(pwd);
            }
            if (profileImage != "") {
                originMember.setProfileImage(profileImage);
            } else { // 이미지가 없거나 있었다가 제거한 경우
                originMember.setProfileImage(null);
            }
            if (phone != "") { // 이미 있음
                originMember.setPhone(phone);
            }
            Member member = memberService.updateUserInfo(originMember);

            session.setAttribute("member", new MemberVO(member));
        }

        return "redirect:/mypage";
    }

    @RequestMapping("/mypage/delete_info")
    public String deleteUserInfo(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        MemberVO memberVO = (MemberVO) session.getAttribute("member");
        System.out.println(memberVO.toString());
        memberService.deletUserInfo(memberVO.getMemNo());
        rememberMeTokenService.removeUserTokens(memberVO.getIdentifier()); // DB의 persistent_logins 토큰 제거 (쿠키는 로그아웃 로직에서 자동 제거)

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
    }

    @RequestMapping("/mypage/uploadProfileImage")
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

    // 새로운 핸드폰 번호 입력 페이지
    @RequestMapping("/mypage/modify_info/phoneCk")
    public String phoneCkForm() {
        return "/mypage/modifyInfo_phoneUpdate1";
    }

    @ResponseBody
    @RequestMapping("/mypage/modify_info/phoneCkProc")
    public String phoneCkProc(String phone) {
        Member member = memberService.findByPhone(phone);
        if (member != null) {
            return "exist";
        } else {
            return "not exist"; // 없어야 해당 번호 사용 가능
        }
    }

    // 인증번호 입력 페이지
    @RequestMapping("/mypage/modify_info/phoneCkCert")
    public String phoneCkCertForm(HttpSession session, @RequestParam String phone) {
        String certKey = messageService.sendCertificationMessage(phone);
        session.setAttribute("phoneNum", phone);
        session.setAttribute("certKey", certKey);
        return "/mypage/modifyInfo_phoneUpdate2";
    }

    // 인증번호 검증(일치하는지 여부 보내주기)
    @ResponseBody
    @RequestMapping("/mypage/modify_info/phoneCkCertProc")
    public Map<String, String> phoneCkCertProc(HttpSession session, String certKey) {
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

        return result;
    }

}
