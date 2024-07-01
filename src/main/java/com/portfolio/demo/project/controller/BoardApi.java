package com.portfolio.demo.project.controller;

import com.google.gson.JsonObject;
import com.portfolio.demo.project.dto.board.BoardImpParam;
import com.portfolio.demo.project.dto.board.BoardNoticeParam;
import com.portfolio.demo.project.dto.board.ImpressionPagenationParam;
import com.portfolio.demo.project.dto.board.NoticePagenationParam;
import com.portfolio.demo.project.dto.board.request.CreateBoardRequest;
import com.portfolio.demo.project.dto.board.request.UpdateBoardRequest;
import com.portfolio.demo.project.dto.board.response.MultiBoardImpResponse;
import com.portfolio.demo.project.dto.board.response.MultiBoardNoticeResponse;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.BoardNoticeService;
import com.portfolio.demo.project.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

@Tag(name = "Board", description = "일반 게시글 관련 API 입니다.")
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
    public ResponseEntity<Result<NoticePagenationParam>> notices(@RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                                 @RequestParam(name = "page", required = false, defaultValue = "0") int pageNum,
                                                                 @RequestParam(name = "query", required = false) String query,
                                                                 @RequestParam(name = "condition", required = false) String condition,
                                                                 @RequestParam(name = "orderby", required = false) String orderBy) {

        NoticePagenationParam pagenationVO = null;
        if (query != null) {
            if ("titleOrContent".equals(condition)) {
                pagenationVO = boardNoticeService.getBoardNoticePagenationByTitleOrContent(pageNum, size, query);
            }
        } else if (orderBy != null) {
            if ("views".equals(orderBy)) {
                pagenationVO = boardNoticeService.getAllBoardsOrderByCondition(pageNum, size, condition);
            } else if ("recent".equals(orderBy)) {
                pagenationVO = boardNoticeService.getAllBoards(pageNum, size);
            }
        } else {
            pagenationVO = boardNoticeService.getAllBoards(pageNum, size);
        }

        return new ResponseEntity<>(new Result<>(pagenationVO), HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 단건 조회
     *
     * @param id
     */
    @GetMapping("/notices/{id}")
    public ResponseEntity<Result<MultiBoardNoticeResponse>> notice(@PathVariable Long id) {
        MultiBoardNoticeResponse response = new MultiBoardNoticeResponse();
        BoardNoticeParam board = boardNoticeService.findById(id);
        BoardNoticeParam prev = boardNoticeService.findPrevById(id);
        BoardNoticeParam next = boardNoticeService.findNextById(id);

        response.setBoard(board);
        response.setPrevBoard(prev);
        response.setNextBoard(next);

        return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 작성
     *
     * @param request
     */
    @PostMapping("/notices")
    public ResponseEntity<Result<BoardNoticeParam>> createNotice(@RequestBody CreateBoardRequest request) {
        BoardNoticeParam notice = BoardNoticeParam.builder()
                .title(request.getTitle().trim())
                .content(request.getContent())
                .writerId(request.getWriterId())
                .build();
        Long id = boardNoticeService.saveBoard(notice);

        BoardNoticeParam created = boardNoticeService.findById(id);

        Result<BoardNoticeParam> result = new Result<>(created);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 수정
     *
     * @param request
     */
    @PatchMapping("/notices")
    public ResponseEntity<Result<BoardNoticeParam>> updateNotice(@RequestBody UpdateBoardRequest request) {
        BoardNoticeParam board = BoardNoticeParam.builder()
                .id(request.getId())
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        Long id = boardNoticeService.updateBoard(board);

        BoardNoticeParam foundBoard = boardNoticeService.findById(id);

        return new ResponseEntity<>(new Result<>(foundBoard), HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 조회수 상승
     *
     * @param request
     * @return
     */
    @PatchMapping("/notices/view")
    public ResponseEntity<Result<BoardNoticeParam>> updateNoticeViews(@RequestBody UpdateBoardRequest request) {
        boardNoticeService.upViewCntById(request.getId());
        BoardNoticeParam notice = boardNoticeService.findById(request.getId());

        return new ResponseEntity<>(new Result<>(notice), HttpStatus.OK);
    }

    /**
     * 공지사항 게시글 삭제
     *
     * @param boardId
     */
    @DeleteMapping("/notices")
    public ResponseEntity<Result<Boolean>> deleteNotice(@RequestParam Long boardId) {
        boardNoticeService.deleteBoardByBoardId(boardId);

        return new ResponseEntity<>(new Result<>(Boolean.TRUE), HttpStatus.OK);
    }

    /**
     * 전체 후기 게시글 조회 및 검색
     *
     * @param size
     * @param pageNum
     * @param condition 검색 조건(제목 또는 내용 | 글쓴이)
     * @param query     검색 키워드
     * @param orderBy   정렬 기준
     */
    @GetMapping("/imps")
    public ResponseEntity<Result<ImpressionPagenationParam>> imps(
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            @RequestParam(name = "page", required = false, defaultValue = "0") int pageNum,
            @RequestParam(name = "condition", required = false) String condition,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "orderby", required = false) String orderBy
    ) {
        ImpressionPagenationParam pagenationVO = null;
        if (query != null) {
            pagenationVO = switch (condition) {
                case "writerName" -> boardImpService.getImpPagenationByWriterName(pageNum, size, query);
                case "titleOrContent" -> boardImpService.getImpPagenationByTitleOrContent(pageNum, size, query);
                default -> pagenationVO;
            };

        } else if (orderBy != null) {
            if ("views".equals(orderBy) || "recommended".equals(orderBy)) {
                pagenationVO = boardImpService.getAllBoardsOrderByCondition(pageNum, size, orderBy);
            } else if ("comments".equals(orderBy)) {
                pagenationVO = boardImpService.getAllBoardsOrderByCommentSizeDesc(pageNum, size);
            } else if ("recent".equals(orderBy)) {
                pagenationVO = boardImpService.getAllBoards(pageNum, size);
            }
        } else {
            pagenationVO = boardImpService.getAllBoards(pageNum, size);
        }

        return new ResponseEntity<>(new Result<>(pagenationVO), HttpStatus.OK);
    }

    /**
     * 후기 게시글 단건 조회
     *
     * @param id
     * @return
     */
    @GetMapping("/imps/{id}")
    public ResponseEntity<Result<MultiBoardImpResponse>> impDetail(@PathVariable Long id) {
        BoardImpParam board = boardImpService.findById(id);
        BoardImpParam prev = boardImpService.findPrevById(id);
        BoardImpParam next = boardImpService.findNextById(id);

        MultiBoardImpResponse response = new MultiBoardImpResponse();
        response.setBoard(board);
        response.setPrevBoard(prev);
        response.setNextBoard(next);

        return new ResponseEntity<>(new Result<>(response), HttpStatus.OK);
    }

    /**
     * 후기 게시글 작성
     *
     * @param request
     */
    @PostMapping("/imps")
    public ResponseEntity<Result<BoardImpParam>> createImp(@RequestBody CreateBoardRequest request) {
        BoardImpParam imp = BoardImpParam.builder()
                .title(request.getTitle().trim())
                .content(request.getContent())
                .writerId(request.getWriterId())
                .build();
        Long id = boardImpService.saveBoard(imp);

        BoardImpParam created = boardImpService.findById(id);

        Result<BoardImpParam> result = new Result<>(created);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 후기 게시글 수정
     *
     * @param request
     */
    @ResponseBody
    @PatchMapping("/imps")
    public ResponseEntity<Result<BoardImpParam>> updateImp(@RequestBody @Valid UpdateBoardRequest request) {
        BoardImpParam boardImp = boardImpService.findById(request.getId());
        boardImp.setTitle(request.getTitle());
        boardImp.setContent(request.getContent());
        boardImpService.updateBoard(boardImp);

        BoardImpParam board = boardImpService.findById(boardImp.getId());

        return new ResponseEntity<>(new Result<>(board), HttpStatus.OK);
    }

    /**
     * 후기 게시글 조회수 상승
     *
     * @param request
     * @return
     */
    @PatchMapping("/imps/views")
    public ResponseEntity<Result<BoardImpParam>> updateImpViews(@RequestBody UpdateBoardRequest request) {
        boardImpService.upViewCntById(request.getId());
        BoardImpParam imp = boardImpService.findById(request.getId());

        return new ResponseEntity<>(new Result<>(imp), HttpStatus.OK);
    }

    /**
     * 후기 게시글 삭제
     *
     * @param boardId
     */
    @DeleteMapping("/imps")
    public ResponseEntity<Result<Boolean>> deleteImp(@RequestParam Long boardId) {
        boardImpService.deleteById(boardId);

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
    }

    /**
     * summernote 이미지 업로드
     */
    @Deprecated
    @PutMapping(value = "/summernoteImageFile", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Result<JsonObject>> uploadSummernoteImage(@RequestParam("file") MultipartFile multipartFile) {

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

        return new ResponseEntity<>(new Result<>(jsonObject), HttpStatus.OK);
    }
}
