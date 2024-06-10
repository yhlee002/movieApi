package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.dto.comment.CommentImpPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.dto.comment.CommentMovPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentMovParam;
import com.portfolio.demo.project.service.CommentImpService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.dto.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentApi {

    private final CommentMovService commentMovService;

    private final CommentImpService commentImpService;

    /**
     * 댓글을 작성일자 내림차순으로 조회
     *
     * @param page
     * @param movieNo
     * @return
     */
    @GetMapping("/comment/movie")
    public ResponseEntity<Result<CommentMovPagenationParam>> getCommentMovs(
            @RequestParam("movieNo") Long movieNo,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        CommentMovPagenationParam vos = commentMovService.getCommentsByMovie(movieNo, page, size);

        return new ResponseEntity<>(new Result<>(vos), HttpStatus.OK);
    }

    /**
     * 댓글 작성
     *
     * @param request
     */
    @PostMapping("/comment/movie")
    public ResponseEntity<Result<CommentMovParam>> createCommentMov(@RequestBody CreateCommentMovRequest request) {
        Long id = commentMovService.saveComment(
                CommentMovParam.builder()
                        .movieNo(request.getMovieNo())
                        .content(request.getContent())
                        .writerId(request.getWriterId())
                        .rating(request.getRating())
                        .build()
        );

        CommentMovParam comment = commentMovService.findById(id);

        return new ResponseEntity<>(new Result<>(comment), HttpStatus.OK);    }

    /**
     * 댓글 수정
     *
     * @param request
     */
    @PatchMapping("/comment/movie")
    public ResponseEntity<Result<CommentMovParam>> updateCommentMov(@RequestBody UpdateCommentMovRequest request) {
        CommentMovParam commentParam = CommentMovParam.builder()
                .id(request.getCommentId())
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        Long id = commentMovService.updateComment(commentParam);
        CommentMovParam comment = commentMovService.findById(id);

        return new ResponseEntity<>(new Result<>(comment), HttpStatus.OK);
    }


    /**
     * 댓글 삭제
     *
     * @param commentId
     * @return
     */
    @DeleteMapping("/comment/movie")
    public ResponseEntity<Result<Boolean>> deleteCommentMov(@RequestParam Long commentId) {
        commentMovService.deleteCommentById(commentId);

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
    }

    /**
     * @param memNo
     * @deprecated 추후 Vue.js 등으로 변경시 프론트에서 모두 처리하도록 변경시 삭제 예정
     * 사용자 식별번호를 이용해 사용자가 입력한 댓글을 식별하기 위한 메서드
     */
    @GetMapping("/comment/movie/checkMemNo")
    public ResponseEntity<Result<List<CommentMovParam>>> getCommentMovsByMemNo(
            @RequestParam(name = "memNo") Long memNo,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        List<CommentMovParam> commList = commentMovService.getCommentsByMember(memNo, page, size);

        log.info("조회된 댓글 수 : {}", commList);

        return new ResponseEntity<>(new Result<>(commList), HttpStatus.OK);
    }

    // ------------ 영화 감상 후기 게시판 댓글 ------------ //

    /**
     * 해당 글에 대한 전체 댓글 조회
     *
     * @param boardId
     * @param page
     */
    @GetMapping("/comments/imp")
    public ResponseEntity<Result<CommentImpPagenationParam>> getCommentImpsByBoard(
            @RequestParam(name = "boardId") Long boardId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        CommentImpPagenationParam vo = commentImpService.getCommentsByBoard(boardId, page, size);

        return new ResponseEntity<>(new Result<>(vo), HttpStatus.OK);
    }

    /**
     * 댓글 작성
     *
     * @param request
     */
    @PostMapping("/comment/imp")
    public ResponseEntity<Result<CommentImpParam>> writeCommentImp(@RequestBody CreateCommentImpRequest request) {
        Long id = commentImpService.saveComment(
                CommentImpParam.builder()
                        .content(request.getContent())
                        .boardId(request.getBoardId())
                        .writerId(request.getWriterId())
                        .build());

        CommentImpParam imp = commentImpService.findById(id);

        return ResponseEntity.ok(new Result<>(imp));
    }

    /**
     * 댓글 수정
     *
     * @param request
     */
    @PatchMapping("/comment/imp")
    public ResponseEntity<Result<CommentImpParam>> updateCommentImp(@RequestBody UpdateCommentImpRequest request) {
        CommentImpParam comm = CommentImpParam.builder()
                .id(request.getCommentId())
                .writerId(request.getWriterId())
                .content(request.getContent())
                .boardId(request.getBoardId())
                .build();

        Long id = commentImpService.updateComment(comm);
        CommentImpParam imp = commentImpService.findById(id);

        return ResponseEntity.ok(new Result<>(imp));
    }

    /**
     * 댓글 삭제
     *
     * @param commentId
     */
    @DeleteMapping("/comment/imp")
    public ResponseEntity<Result<Boolean>> deleteCommentImp(@RequestParam Long commentId) {
        commentImpService.deleteCommentById(commentId);

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
    }

    @GetMapping("/comment/imp/checkMemNo")
    public ResponseEntity<Result<List<CommentImpParam>>> getCommentListByMemNo(
            @RequestParam(name = "memNo") Long memNo,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        List<CommentImpParam> comments = commentImpService.getCommentsByMember(memNo, page, size);

        return new ResponseEntity<>(new Result<>(comments), HttpStatus.OK);
    }

    @Data
    static class CreateCommentMovRequest {
        private Long writerId;
        private Long movieNo;
        private String content;
        private Integer rating;
    }

    @Data
    static class UpdateCommentMovRequest {
        private Long writerId;
        private Long movieNo;
        private Long commentId;
        private String content;
        private Integer rating;
    }

    @Data
    static class CreateCommentImpRequest {
        private Long writerId;
        private Long boardId;
        private String content;
    }

    @Data
    static class UpdateCommentImpRequest {
        private Long writerId;
        private Long boardId;
        private Long commentId;
        private String content;

    }
}
