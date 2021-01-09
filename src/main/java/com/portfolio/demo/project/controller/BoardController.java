package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

@Slf4j
@Controller
public class BoardController {

    @Autowired
    BoardNoticeService boardNoticeService;

    @Autowired
    BoardImpService boardImpService;

    @RequestMapping("/notice")
    public String noticeBoard(Model model, @RequestParam(name = "p", required = false, defaultValue = "1") int pageNum) {
        NoticePagenationVO pagenationVO = boardNoticeService.getNoticeListView(pageNum);
        model.addAttribute("pagenation", pagenationVO);

        return "board_notice/list";
    }

    @RequestMapping("/notice/write")
    public String noticeBoardWriteForm() {

        return "board_notice/writeForm";
    }

    @RequestMapping("/notice/{boardNo}")
    public String noticeDetail(@PathVariable Long boardNo, Model model) {
        Map<String, BoardNotice> boards = boardNoticeService.selectBoardByBoardId(boardNo);
        model.addAttribute("board", boards.get("board"));
        model.addAttribute("prevBoard", boards.get("prevBoard"));
        model.addAttribute("nextBoard", boards.get("nextBoard"));

        return "board_notice/detail";
    }

    @RequestMapping("/imp")
    public String impBoard(Model model, @RequestParam(name = "p", required = false, defaultValue = "1") int pageNum) {
        ImpressionPagenationVO pagenationVO = boardImpService.getImpListView(pageNum);
        model.addAttribute("pagenation", pagenationVO);

        return "board_impression/list";
    }

    @RequestMapping("/imp/write")
    public String impBoardWriteForm() {

        return "board_impression/writeForm";
    }

    @RequestMapping("/imp/{boardNo}")
    public String impDetail(@PathVariable Long boardNo, Model model) {
        Map<String, BoardImp> boards = boardImpService.selectBoardByBoardId(boardNo);
        model.addAttribute("board", boards.get("board"));
        model.addAttribute("prevBoard", boards.get("prevBoard"));
        model.addAttribute("nextBoard", boards.get("nextBoard"));

        return "board_impression/detail";
    }


    /* 이미지 업로드 */
    @RequestMapping(value = "/uploadSummernoteImageFile", produces = "application/json")
    @ResponseBody
    public JsonObject uploadSummernoteImage(@RequestParam("file") MultipartFile multipartFile) {

        log.info("들어온 파일 원래 이름 : " + multipartFile.getOriginalFilename() + ", size : " + multipartFile.getSize());

        JsonObject jsonObject = new JsonObject();

        ResourceBundle bundle = ResourceBundle.getBundle("Res_ko_KR_keys");
        String fileRoot = bundle.getString("summernoteImageFilesRoot");

        log.info("저장될 파일 경로 : " + fileRoot);

        String originalFileName = multipartFile.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 마지막 '.'이하의 부분이 확장자
        String savedFileName = UUID.randomUUID() + extension;

        File targetFile = new File(fileRoot + savedFileName);

        try {
            InputStream stream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(stream, targetFile); // 파일 저장

            jsonObject.addProperty("url", "/summernoteImage/" + savedFileName);
            jsonObject.addProperty("responseCode", "success");

            log.info("jsonObject : " + jsonObject.toString());

        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile); // 저장된 파일 삭제
            jsonObject.addProperty("responseCode", "error");
            e.printStackTrace();
        }

        return jsonObject;
    }

    /* 글 작성 */
    @ResponseBody
    @RequestMapping(value = "/notice/writeProc", method = RequestMethod.POST)
    public Long noticeWriteProc(String title, Long writerNo, String content) {
        log.info("title : "+title+", writerNo : "+writerNo+", content : "+content);
        return boardNoticeService.saveBoard(title, writerNo, content).getBoardId();
    }

}
