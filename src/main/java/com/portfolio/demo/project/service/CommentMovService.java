package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.CommentMovRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.CommentMovPagenationVO;
import com.portfolio.demo.project.vo.CommentMovVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentMovService {

    private final CommentMovRepository commentMovRepository;

    private final static int COMMENTS_PER_PAGE = 20;

    // 댓글 작성
    public void saveComment(CommentMov comment) {
        commentMovRepository.save(comment);
    }

//    /**
//     * 추천순으로 댓글 조회(미개발)
//     * @param pageNum
//     * @param movieId
//     * @return
//     */
//    public Map<String, Object> getCommentListVOOrderByRecommended(int pageNum, String direction, Long movieId) {
//        Sort.Direction sortDirection = direction != null && direction.toUpperCase().equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
//        Pageable pageable = PageRequest.of(pageNum, COMMENTS_PER_PAGE, Sort.by(sortDirection, "recommended"));
//        Page<CommentMov> page = commentMovRepository.findAllByMovieNoOrderByRecommended(movieId, pageable);
//        List<CommentMovVO> commentMovVOList = page.getContent().stream().map(CommentMovVO::create).collect(Collectors.toList());
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("list", commentMovVOList);
//        result.put("totalPageCnt", page.getTotalPages());
//        result.put("totalCommentCnt", page.getTotalElements());
//        return result;
//    }

    /**
     * 해당 영화에 대한 모든 리뷰 가져오기
     */
    public List<CommentMov> getCommentsByMovie(int pageNum, Long movieNo) {
        Pageable pageable = PageRequest.of(pageNum, COMMENTS_PER_PAGE, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findAllByMovieNo(movieNo, pageable);
        return page.getContent();
    }

    /**
     * 해당 영화에 대한 모든 리뷰의 전체 페이지 수
     */
    public Integer getTotalPageCountByMovieNo(Long movieNo) {
        Pageable pageable = PageRequest.of(0, COMMENTS_PER_PAGE, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findAllByMovieNo(movieNo, pageable);
        return page.getTotalPages();
    }

    /**
     * 전체 리뷰 조회
     */
    public List<CommentMov> getComments(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, COMMENTS_PER_PAGE, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findAll(pageable);

        return page.getContent();
    }

    /**
     * 사용자가 쓴 댓글 조회(수정, 삭제 버튼)
     *
     * @param member
     * @param page
     */
    public List<CommentMov> getCommentsByMember(Member member, int page) {
        Pageable pageable = PageRequest.of(page, COMMENTS_PER_PAGE, Sort.by("regDate").descending());
        Page<CommentMov> pages = commentMovRepository.findByWriter(member, pageable);
        return pages.getContent();
    }

    /**
     * 댓글 수정
     *
     * @param commentId
     * @param content
     */
    public CommentMov updateMovComment(Long commentId, String content) {
        CommentMov originImp = commentMovRepository.findById(commentId).get();
        originImp.setContent(content);
        return commentMovRepository.save(originImp);
    }

    /**
     * 댓글 삭제
     *
     * @param commentId
     */
    public void deleteCommentById(Long commentId) {
        Optional<CommentMov> comm = commentMovRepository.findById(commentId);
        if (comm.isPresent()) {
            commentMovRepository.delete(comm.get());
        }
    }

    /**
     * id를 이용한 댓글 단건 조회
     *
     * @param commentId
     */
    public CommentMov getCommentById(Long commentId) {
        return commentMovRepository.findById(commentId).orElse(null);
    }
}
