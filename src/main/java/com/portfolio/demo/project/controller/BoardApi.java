package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.vo.*;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MultiBoardNoticeResponse> notice(@PathVariable Long id) {
        MultiBoardNoticeResponse response = new MultiBoardNoticeResponse();
        BoardNoticeVO board = boardNoticeService.findById(id);
        BoardNoticeVO prev = boardNoticeService.findPrevById(id);
        BoardNoticeVO next = boardNoticeService.findNextById(id);

        response.setBoard(board);
        response.setPrevBoard(prev);
        response.setNextBoard(next);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 조회수 상승
     *
     * @param request
     * @return
     */
    @PatchMapping("/notice/view")
    public ResponseEntity<BoardNoticeVO> updateViews(@RequestBody UpdateBoardRequest request) {
        boardNoticeService.upViewCntById(request.getId());
        BoardNoticeVO notice = boardNoticeService.findById(request.getId());

        return new ResponseEntity<>(notice, HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 작성
     *
     * @param request
     */
    @PostMapping("/notice")
    public ResponseEntity<BoardNoticeVO> createNotice(@RequestBody CreateBoardRequest request) {
        BoardNoticeVO notice = BoardNoticeVO.builder()
                .id(request.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(request.getWriterId())
                .build();
        BoardNoticeVO created = boardNoticeService.updateBoard(notice);

        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 수정
     *
     * @param request
     */
    @PatchMapping("/notice")
    public ResponseEntity<BoardNoticeVO> updateNotice(@RequestBody UpdateBoardRequest request) {
        BoardNoticeVO notice = boardNoticeService.findById(request.getId());

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());
        BoardNoticeVO board = boardNoticeService.updateBoard(notice);

        return new ResponseEntity<>(board, HttpStatus.OK);
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
    public ResponseEntity<MultiBoardImpResponse> impDetail(@PathVariable Long id) {
        BoardImpVO board = boardImpService.findById(id);
        BoardImpVO prev = boardImpService.findPrevById(id);
        BoardImpVO next = boardImpService.findNextById(id);

        MultiBoardImpResponse response = new MultiBoardImpResponse();
        response.setBoard(board);
        response.setPrevBoard(prev);
        response.setNextBoard(next);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 후기 게시글 수정
     *
     * @param request
     */
    @ResponseBody
    @PatchMapping("/imp")
    public ResponseEntity<BoardImpVO> updateImp(@RequestBody @Valid UpdateBoardRequest request) {
        BoardImpVO boardImp = boardImpService.findById(request.getId());

        boardImpService.updateBoard(boardImp);

        BoardImpVO result = boardImpService.findById(boardImp.getId());

        return new ResponseEntity<>(result, HttpStatus.OK);
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

    @Data
    private static class CreateBoardRequest {
        private Long id;
        private String title;
        private String content;
        private Long writerId;
    }

    @Data
    private static class UpdateBoardRequest {
        private Long id;
        private String title;
        private String content;
    }

    @Data
    private static class MultiBoardNoticeResponse {
        private BoardNoticeVO board;
        private BoardNoticeVO prevBoard;
        private BoardNoticeVO nextBoard;
    }

    @Data
    private static class MultiBoardImpResponse {
        private BoardImpVO board;
        private BoardImpVO prevBoard;
        private BoardImpVO nextBoard;
    }
}
