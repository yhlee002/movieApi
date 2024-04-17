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

    private final MemberRepository memberRepository;

    private final static int COMMENTS_PER_PAGE = 20;

    // 댓글 작성
    public CommentMov saveComment(Long writerNo, String content, Long movieNo, int rating) {
        CommentMov comment = commentMovRepository.save(
                CommentMov.builder()
                        .id(null)
                        .writer(memberRepository.findById(writerNo).get())
                        .content(content)
                        .movieNo(movieNo)
                        .rating(rating)
                        .build());

        return comment;
    }

    /**
     * 추천순으로 댓글 조회(미개발)
     * @param pageNum
     * @param movieId
     * @return
     */
    public Map<String, Object> getCommentListVOOrderByRecommended(int pageNum, Long movieId) {
        Pageable pageable = PageRequest.of(pageNum, COMMENTS_PER_PAGE, Sort.by(Sort.Direction.DESC, "recommended"));
        Page<CommentMov> page = commentMovRepository.findCommentMovsByMovieNoOrderByRecommended(movieId, pageable);
        List<CommentMovVO> commentMovVOList = page.getContent().stream().map(CommentMovVO::create).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", commentMovVOList);
        result.put("totalPageCnt", page.getTotalPages());
        result.put("totalCommentCnt", page.getTotalElements());
        return result;
    }

    /**
     * 댓글 출력(Ajax 비동기 통신 사용)
     */
    public Map<String, Object> getCommentsOrderByRegDate(int pageNum, Long movieNo) {
        Pageable pageable = PageRequest.of(pageNum, COMMENTS_PER_PAGE, Sort.by(Sort.Direction.DESC, "reg_dt"));
        Page<CommentMovVO> page = commentMovRepository.findAllByMovieNo(movieNo, pageable)
                .map(CommentMovVO::create);
        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getContent());
        result.put("totalPageCnt", page.getTotalPages());
        result.put("totalCommentCnt", page.getTotalElements());
        return result;
    }

    /**
     * 해당 영화에 대한 모든 리뷰 가져오기
     * @param movieCd
     */
    public List<CommentMovVO> getCommentsByMovie(int pageNum, Long movieCd) {
        Pageable pageable = PageRequest.of(pageNum, COMMENTS_PER_PAGE, Sort.by("regDate").descending());
        List<CommentMov> commentMov = commentMovRepository.findAllByMovieNo(movieCd, pageable).getContent();
        List<CommentMovVO> commentMovVOList = new ArrayList<>();
        for (CommentMov comm : commentMov) {
            commentMovVOList.add(CommentMovVO.create(comm));
        }
        return commentMovVOList;
    }

    /**
     * 사용자가 쓴 댓글 조회(수정, 삭제 버튼)
     * @param member
     * @param page
     */
    public List<CommentMovVO> getCommentsByMember(Member member, int page) {
        Pageable pageable = PageRequest.of(page, COMMENTS_PER_PAGE, Sort.by("regDate").descending());
        Page<CommentMov> pages = commentMovRepository.findByWriter(member, pageable);
        List<CommentMovVO> commentMovVOList = pages.getContent()
                .stream()
                .map(CommentMovVO::create)
                .collect(Collectors.toList());
        return commentMovVOList;
    }

    /**
     * 댓글 수정
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
     * @param commentId
     */
    public void deleteMovComment(Long commentId) {
        Optional<CommentMov> comm = commentMovRepository.findById(commentId);
        if (comm.isPresent()) {
            commentMovRepository.delete(comm.get());
        }
    }
}
