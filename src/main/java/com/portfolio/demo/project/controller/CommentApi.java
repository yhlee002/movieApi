package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.dto.board.request.MultiDeleteRequest;
import com.portfolio.demo.project.dto.comment.CommentImpPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.dto.comment.CommentMovPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentMovParam;
import com.portfolio.demo.project.dto.comment.request.CreateCommentImpRequest;
import com.portfolio.demo.project.dto.comment.request.CreateCommentMovRequest;
import com.portfolio.demo.project.dto.comment.request.UpdateCommentImpRequest;
import com.portfolio.demo.project.dto.comment.request.UpdateCommentMovRequest;
import com.portfolio.demo.project.service.CommentImpService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.dto.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment", description = "댓글 관련 API 입니다.")
@RequestMapping("/comments")
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
    @GetMapping("/movies")
    public ResponseEntity<Result<CommentMovPagenationParam>> getCommentMovs(
            @RequestParam(name = "movieNo", required = false) Long movieNo,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        CommentMovPagenationParam vos = commentMovService.getCommentsByMovie(movieNo, page, size);

        return new ResponseEntity<>(new Result<>(vos), HttpStatus.OK);
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Result<CommentMovParam>> getCommentMov(@PathVariable Long id) {
        CommentMovParam mov = commentMovService.findById(id);
        return new ResponseEntity<>(new Result<>(mov), HttpStatus.OK);
    }

    /**
     * 댓글 작성
     *
     * @param request
     */
    @PostMapping("/movies")
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
    @PatchMapping("/movies")
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
    @DeleteMapping("/movies")
    public ResponseEntity<Result<Boolean>> deleteCommentMov(@RequestParam Long commentId) {
        commentMovService.deleteCommentById(commentId);

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
    }

    /**
     * @param memNo
     *
     * 사용자 식별번호를 이용해 사용자가 입력한 댓글 조회
     */
    @GetMapping("/movies/members")
    public ResponseEntity<Result<CommentMovPagenationParam>> getCommentMovsByMemNo(
            @RequestParam(name = "memNo") Long memNo,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size) {
        CommentMovPagenationParam param = commentMovService.getCommentsByMember(memNo, page, size);

        return new ResponseEntity<>(new Result<>(param), HttpStatus.OK);
    }

    // ------------ 영화 감상 후기 게시판 댓글 ------------ //

    /**
     * (해당 글에 대한) 전체 댓글 조회
     *
     * @param boardId
     * @param page
     */
    @GetMapping("/imps")
    public ResponseEntity<Result<CommentImpPagenationParam>> getCommentImpsByBoard(
            @RequestParam(name = "boardId", required = false) Long boardId,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        CommentImpPagenationParam vo = null;
        if (boardId != null) {
            vo = commentImpService.getCommentsByBoard(boardId, page, size);
        } else {
            vo = commentImpService.getComments(page, size);
        }

        return new ResponseEntity<>(new Result<>(vo), HttpStatus.OK);
    }

    @GetMapping("/imps/members")
    public ResponseEntity<Result<CommentImpPagenationParam>> getCommentListByMemNo(
            @RequestParam(name = "memNo") Long memNo,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "20") int size
    ) {
        CommentImpPagenationParam param = commentImpService.getCommentsByMember(memNo, page, size);

        return new ResponseEntity<>(new Result<>(param), HttpStatus.OK);
    }

    /**
     * 댓글 작성
     *
     * @param request
     */
    @PostMapping("/imps")
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
    @PatchMapping("/imps")
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
    @DeleteMapping("/imps")
    public ResponseEntity<Result<Boolean>> deleteCommentImp(@RequestParam Long commentId) {
        commentImpService.deleteCommentById(commentId);

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
    }

    /**
     * 복수의 댓글 삭제
     *
     * @param request 삭제하고자 하는 댓글 식별번호 목록
     */
    @PostMapping("/imps/batch-delete")
    public ResponseEntity<Result<Boolean>> deleteCommentImps(@RequestBody MultiDeleteRequest request) {
        commentImpService.deleteByIds(request.getIds());

        return ResponseEntity.ok(new Result<>(Boolean.TRUE));
    }
}
