package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.CommentImpService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.vo.CommentImpPagenationVO;
import com.portfolio.demo.project.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
//        log.info(pagenation.toString());
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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/";
    }

//    @RequestMapping("/mypage/delete_info/kakao/oauth2") : 카카오 로그인 연결 끊기 콜백 url


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

}
