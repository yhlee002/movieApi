package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.service.BoardImpService;
import com.portfolio.demo.project.service.CommentImpService;
import com.portfolio.demo.project.service.CommentMovService;
import com.portfolio.demo.project.service.MemberService;
import com.portfolio.demo.project.vo.CommentImpVO;
import com.portfolio.demo.project.vo.CommentMovVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentMovService commentMovService;

    private final CommentImpService commentImpService;

    private final MemberService memberService;

    private final BoardImpService boardImpService;

    /**
     * 댓글 작성
     * @param commentContent
     * @param memNo
     * @param movieNo
     * @param rating
     */
    @PostMapping("/movieInfo/comment")
    public ResponseEntity<String> writeCommentMovieInfo(String commentContent, Long memNo, Long movieNo, int rating) {
        CommentMov comment = commentMovService.saveComment(memNo, commentContent, movieNo, rating);
        if (comment != null) {
            return new ResponseEntity<>("success", HttpStatus.OK);
        }

        return new ResponseEntity<>("fail", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 댓글을 작성일자 내림차순으로 조회(TODO. 페이지네이션 필요)
     * @param pageNum
     * @param movieCd
     * @return
     */
    @GetMapping("/movieInfo/comment")
    public Map<String, Object> getCommentList(@RequestParam(name = "p") int pageNum, Long movieCd) {
        Map<String, Object> map = commentMovService.getCommentsOrderByRegDate(pageNum, movieCd);

        return map;
    }

    /**
     * @deprecated 추후 Vue.js 등으로 변경시 프론트에서 모두 처리하도록 변경시 삭제 예정
     * 사용자 식별번호를 이용해 사용자가 입력한 댓글을 식별하기 위한 메서드
     * @param memNo
     */
    @GetMapping("/movieInfo/comment/checkMemNo")
    public ResponseEntity<List<CommentMovVO>> getMovieCommentListByMemNo(Long memNo, int page) {
        log.info("들어온 사용자 식별번호 : " + memNo);
        Member member = memberService.findByMemNo(memNo);
        List<CommentMovVO> commList = commentMovService.getCommentsByMember(member, page);
        log.info("반환될 댓글 리스트 : " + commList);

        return new ResponseEntity<>(commList, HttpStatus.OK);
    }

    /**
     * 댓글 수정
     * @param content
     * @param commentId
     */
    @PatchMapping("/movieInfo/comment")
    public ResponseEntity<String> updateCommentMov(String content, Long commentId) {
        if (commentMovService.updateMovComment(commentId, content) != null) {
            return new ResponseEntity<>("success", HttpStatus.OK);
        }
        return new ResponseEntity<>("false", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * 댓글 삭제
     * @param commentId
     * @return
     */
    @DeleteMapping("/movieInfo/comment")
    public String deleteCommentMov(Long commentId) {
        commentMovService.deleteMovComment(commentId);

        return "success";
    }

    // ------------ 영화 감상 후기 게시판 댓글 ------------ //

    /**
     * 댓글 작성
     * @param content
     * @param boardId
     * @param memNo
     */
    @PostMapping("/imp/comment")
    public void writeCommentImp(String content, Long boardId, Long memNo) {
        commentImpService.saveComment(content, boardId, memNo);
    }

    /**
     * 댓글 수정
     * @param content
     * @param commentId
     */
    @PatchMapping("/imp/comment")
    public String updateCommentImp(String content, Long commentId) {
        if (commentImpService.updateComment(commentId, content) != null) {
            return "success";
        }
        return "false";
    }

    /**
     * 댓글 삭제
     * @param commentId
     */
    @DeleteMapping("/imp/comment")
    public String deleteCommentImp(Long commentId) {
        commentImpService.deleteComment(commentId);

        return "success";
    }


    /**
     * 해당 글에 대한 전체 댓글 조회
     * @param boardId
     * @param page
     */
    @GetMapping("/imp/comments")
    public List<CommentImpVO> getCommentList(Long boardId, int page) {
        BoardImp board = boardImpService.findById(boardId);
        List<CommentImpVO> impList = commentImpService.getCommentsByBoard(board, page);

        return impList;
    }

    @GetMapping("/imp/comment/checkMemNo")
    public List<CommentImpVO> getCommentListByMemNo(Long memNo, int page) {
        Member member = memberService.findByMemNo(memNo);
        List<CommentImpVO> impList = commentImpService.getCommentsByMember(member, page);

        return impList;
    }


}
