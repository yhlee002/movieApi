package com.portfolio.demo.project.controller;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class CommentApi {

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
        Member user = memberService.findByMemNo(memNo);
        try {
            commentMovService.saveComment(
                    CommentMov.builder()
                            .writer(user)
                            .content(commentContent)
                            .movieNo(movieNo)
                            .rating(rating)
                            .build()
            );
        } catch (Exception e) {
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
        List<CommentMovVO> list = commentMovService.getCommentsByMovie(pageNum, movieCd).stream().map(CommentMovVO::create).toList();
        int totalPage = commentMovService.getTotalPageCountByMovieNo(movieCd);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("totalPageCnt", totalPage);
        result.put("totalCommentCnt", list.size());

        return result;
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
        List<CommentMovVO> commList = commentMovService.getCommentsByMember(member, page).stream().map(CommentMovVO::create).toList();
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
        commentMovService.deleteCommentById(commentId);

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
        BoardImp board = boardImpService.findById(boardId);
        Member user = memberService.findByMemNo(memNo);
        commentImpService.saveComment(
                CommentImp.builder()
                .content(content)
                .writer(user)
                .board(board)
                .build());
    }

    /**
     * 댓글 수정
     * @param comment
     */
    @PatchMapping("/imp/comment")
    public String updateCommentImp(@RequestBody CommentImp comment) {
        CommentImp original = commentImpService.getCommentById(comment.getId());

        try {
            original.updateContent(comment.getContent());
            commentImpService.updateComment(original);
        } catch (Exception e) {
            return "false";
        }
        return "success";
    }

    /**
     * 댓글 삭제
     * @param commentId
     */
    @DeleteMapping("/imp/comment")
    public String deleteCommentImp(Long commentId) {
        commentImpService.deleteCommentById(commentId);

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
        return commentImpService.getCommentsByBoard(board, page)
                .stream().map(CommentImpVO::create).toList();

    }

    @GetMapping("/imp/comment/checkMemNo")
    public List<CommentImpVO> getCommentListByMemNo(Long memNo, int page) {
        Member member = memberService.findByMemNo(memNo);
        return commentImpService.getCommentsByMember(member, page)
                .stream().map(CommentImpVO::create).toList();
    }


}
