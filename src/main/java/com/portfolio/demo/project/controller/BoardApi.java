package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class BoardApi {

    private final BoardNoticeService boardNoticeService;

    private final BoardImpService boardImpService;

    /**
     * 전체 공지사항 게시글 조회 및 검색
     *
     * @param size
     * @param pageNum
     * @param query
     */
    @GetMapping("/notices")
    public ResponseEntity<NoticePagenationVO> notices(@RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                      @RequestParam(name = "page", required = false, defaultValue = "0") int pageNum,
                                                      @RequestParam(name = "query", required = false) String query) {

        NoticePagenationVO pagenationVO = null;
        if (query != null) {
            pagenationVO = boardNoticeService.getBoardNoticePagenationByTitleOrContent(pageNum, size, query);
        } else {
            pagenationVO = boardNoticeService.getAllBoards(pageNum, size);
        }

        return new ResponseEntity<>(pagenationVO, HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 단건 조회
     *
     * @param id
     */
    @GetMapping("/notice/{id}")
    public ResponseEntity<Map<String, BoardNoticeVO>> notice(@PathVariable Long id) {
        Map<String, BoardNoticeVO> map = new HashMap<>();
        BoardNotice board = boardNoticeService.findById(id);
        BoardNotice prev = boardNoticeService.findPrevById(id);
        BoardNotice next = boardNoticeService.findNextById(id);
        map.put("board", board != null ? BoardNoticeVO.create(board) : null);
        map.put("prevBoard", prev != null ? BoardNoticeVO.create(prev) : null);
        map.put("nextBoard", next != null ? BoardNoticeVO.create(next) : null);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PatchMapping("/notice/view")
    public ResponseEntity<BoardNoticeVO> updateViews(@RequestBody BoardNotice boardNotice) {
        boardNoticeService.upViewCntById(boardNotice.getId());
        BoardNotice notice = boardNoticeService.findById(boardNotice.getId());

        return new ResponseEntity<>(BoardNoticeVO.create(notice), HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 작성
     *
     * @param notice
     */
    @ResponseBody
    @PostMapping("/notice")
    public ResponseEntity<BoardNotice> updateNotice(@RequestBody BoardNotice notice) {
        return new ResponseEntity<>(boardNoticeService.updateBoard(notice), HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 삭제
     *
     * @param boardId
     */
    @DeleteMapping("/notice")
    public ResponseEntity<Boolean> deleteNotice(Long boardId) {
        boardNoticeService.deleteBoardByBoardId(boardId);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 전체 후기 게시글 조회 및 검색
     *
     * @param size
     * @param pageNum
     * @param condition
     * @param query
     */
    @GetMapping("/imps")
    public ResponseEntity<ImpressionPagenationVO> imps(@RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                       @RequestParam(name = "page", required = false, defaultValue = "0") int pageNum,
                                                       @RequestParam(name = "con", required = false) String condition,
                                                       @RequestParam(name = "query", required = false) String query) {

        ImpressionPagenationVO pagenationVO = null;
        if (query != null) {
            pagenationVO = switch (condition) {
                case "writerName" -> boardImpService.getImpPagenationByWriterName(pageNum, size, query);
                case "TitleOrContent" -> boardImpService.getImpPagenationByTitleOrContent(pageNum, size, query);
                default -> pagenationVO;
            };

        } else {
            pagenationVO = boardImpService.getAllBoards(pageNum, size);
        }

        return new ResponseEntity<>(pagenationVO, HttpStatus.OK);
    }

    /**
     * 후기 게시글 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/imp/{id}")
    public ResponseEntity<Map<String, BoardImpVO>> impDetail(@PathVariable Long id) {
        Map<String, BoardImpVO> map = new HashMap<>();
        BoardImp board = boardImpService.findById(id);
        BoardImp prev = boardImpService.findPrevById(id);
        BoardImp next = boardImpService.findNextById(id);
        map.put("board", board != null ? BoardImpVO.create(board) : null);
        map.put("prevBoard", prev != null ? BoardImpVO.create(prev) : null);
        map.put("nextBoard", next != null ? BoardImpVO.create(next) : null);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    /**
     * 후기 게시글 수정
     *
     * @param imp
     */
    @ResponseBody
    @PatchMapping("/imp")
    public ResponseEntity<BoardImpVO> updateImp(@RequestBody BoardImp imp) {
        boardImpService.updateBoard(imp);
        BoardImp result = boardImpService.findById(imp.getId());

        return new ResponseEntity<>(BoardImpVO.create(result), HttpStatus.OK);
    }

    /**
     * 후기 게시글 삭제
     *
     * @param boardId
     */
    @DeleteMapping("/imp")
    public String deleteImp(Long boardId) {
        boardImpService.deleteById(boardId);

        return "redirect:/imps";
    }

    /**
     * summernote 이미지 업로드
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
