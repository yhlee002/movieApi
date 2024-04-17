package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
@RequiredArgsConstructor
@Controller
public class BoardApi {

    private final BoardNoticeService boardNoticeService;

    private final BoardImpService boardImpService;

    /**
     * 공지사항 게시판
     */
    // 전체 조회 및 검색
    @RequestMapping("/notices")
    public String noticeBoard(Model model, @RequestParam(name = "p", required = false, defaultValue = "1") int pageNum,
                              @RequestParam(name = "query", required = false) String query) {

        NoticePagenationVO pagenationVO = null;
        if (query != null) {
            pagenationVO = boardNoticeService.getBoardNoticesByTitleOrContent(query, pageNum);
            model.addAttribute("pagenation", pagenationVO);
        } else {
            pagenationVO = boardNoticeService.getBoardNotices(pageNum);
            model.addAttribute("pagenation", pagenationVO);
        }

        return "board_notice/list";
    }

    // 게시글 단건 조회(자세히 보기)
    @RequestMapping("/notice/{boardNo}")
    public String noticeDetail(@PathVariable Long boardNo, Model model) {
        Map<String, BoardNotice> boards = boardNoticeService.selectBoardsByBoardId(boardNo);
        model.addAttribute("board", boards.get("board"));
        model.addAttribute("prevBoard", boards.get("prevBoard"));
        model.addAttribute("nextBoard", boards.get("nextBoard"));

        boardNoticeService.upViewCnt(boardNo);
        return "board_notice/detail";
    }

    // 게시글 작성 페이지 접근
    @GetMapping("/notice/new")
    public String noticeBoardWriteForm(Long boardId, Model model) {
        if (boardId != null) {
            model.addAttribute("board", boardNoticeService.findById(boardId));
        }
        return "board_notice/writeForm";
    }

    // 게시글 작성
    @ResponseBody
    @PostMapping(value = "/notice")
    public Long noticeWriteProc(@RequestBody BoardNotice notice) {
         return boardNoticeService.updateBoard(notice);
    }

    // 게시글 삭제
    @DeleteMapping("/notice")
    public String noticeDeleteProc(Long boardId) {
        boardNoticeService.deleteBoardByBoardId(boardId);

        return "redirect:/notices";
    }


    /**
     * 후기 게시판
     */

    /**
     * 전체 조회 및 검색
     * @param model
     * @param pageNum
     * @param con
     * @param query
     */
    @GetMapping("/imps")
    public String impBoard(Model model, @RequestParam(name = "p", required = false, defaultValue = "1") int pageNum,
                           @RequestParam(name = "con", required = false) String con,
                           @RequestParam(name = "query", required = false) String query) {

        ImpressionPagenationVO pagenationVO = null;
        if (query != null) {
            switch (con) {
                case "writerName":
                    pagenationVO = boardImpService.getBoardImpsByWriterName(pageNum, query);
                    break;

                case "TitleOrContent":
                    pagenationVO = boardImpService.getBoardImpsByTitleAndContent(pageNum, query);
                    break;
            }

        } else {
            pagenationVO = boardImpService.getImps(pageNum);
        }
        model.addAttribute("list", pagenationVO.getBoardImpList());
        model.addAttribute("totalPageCount", pagenationVO.getTotalPageCnt());

        return "board_imp/list";
    }

    /**
     * 후기 게시글 단건 조회
     * @param boardNo
     * @param model
     * @return
     */
    @RequestMapping("/imp/{boardNo}")
    public String impDetail(@PathVariable Long boardNo, Model model) {
        Map<String, BoardImp> boards = boardImpService.selectBoardsByBoardId(boardNo);
        model.addAttribute("board", boards.get("board"));
        model.addAttribute("prevBoard", boards.get("prevBoard"));
        model.addAttribute("nextBoard", boards.get("nextBoard"));

        boardImpService.upViewCnt(boardNo);
        return "board_imp/detail";
    }

    /**
     * 게시글 수정 페이지 접근
     * @param boardId
     * @param model
     */
    @GetMapping("/imp/new")
    public String impBoardUpdateForm(Long boardId, Model model) {
        model.addAttribute("board", boardImpService.findById(boardId));

        return "board_imp/updateForm";
    }

    /**
     * 게시글 수정
     * @param imp
     */
    @ResponseBody
    @PostMapping("/imp")
    public Long impUpdateProc(@RequestBody BoardImp imp) {
        return boardImpService.updateBoard(imp);
    }

    /**
     * 게시글 삭제
     * @param boardId
     */
    @DeleteMapping("/imp")
    public String impDeleteProc(Long boardId) {
        boardImpService.deleteBoardByBoardId(boardId);

        return "redirect:/imps";
    }

    /**
     * 이미지 업로드
     */
    @PutMapping(value = "/summernoteImageFile", produces = "application/json")
    @ResponseBody
    public JsonObject uploadSummernoteImage(@RequestParam("file") MultipartFile multipartFile) {

        log.info("들어온 파일 원래 이름 : " + multipartFile.getOriginalFilename() + ", size : " + multipartFile.getSize());

        JsonObject jsonObject = new JsonObject();

        ResourceBundle bundle = ResourceBundle.getBundle("Res_ko_KR_keys");
        String fileRoot = bundle.getString("profileImageFileRoot");

        String originalFileName = multipartFile.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")); // 마지막 '.'이하의 부분이 확장자
        String savedFileName = UUID.randomUUID() + extension;

        File targetFile = new File(fileRoot + savedFileName);

        try {
            InputStream stream = multipartFile.getInputStream();
            FileUtils.copyInputStreamToFile(stream, targetFile); // 파일 저장

            jsonObject.addProperty("url", "/summernoteImage/" + savedFileName);
            jsonObject.addProperty("responseCode", "success");

        } catch (IOException e) {
            FileUtils.deleteQuietly(targetFile); // 저장된 파일 삭제
            jsonObject.addProperty("responseCode", "error");
            e.printStackTrace();
        }

        return jsonObject;
    }


}
