package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.CommentMovRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.*;
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

    /**
     * 댓글 작성
     *
     * @param comment
     */
    public CommentMovVO updateComment(CommentMovVO comment) {
        Member user = memberRepository.findById(comment.getWriterId()).orElse(null);

        CommentMovVO result = null;

        if (user != null) {
            CommentMov comm = commentMovRepository.save(
                    CommentMov.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .writer(user)
                            .movieNo(comment.getMovieNo())
                            .rating(comment.getRating())
                            .build()
            );
            result = CommentMovVO.create(comm);
        } else {
            log.error("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo: {})", comment.getWriterId());
        }

        return result;
    }

    /**
     * 추천순으로 댓글 조회(미개발)
     * @param pageNum
     * @param movieId
     * @return
     */
    public Map<String, Object> getCommentsOrderByRecommended(Long movieId, int pageNum, int size) {
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(Sort.Direction.DESC, "recommended"));
        Page<CommentMov> page = commentMovRepository.findAllByMovieNo(movieId, pageable);
        List<CommentMovVO> commentMovVOList = page.getContent().stream().map(CommentMovVO::create).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("list", commentMovVOList);
        result.put("totalPageCnt", page.getTotalPages());
        result.put("totalCommentCnt", page.getTotalElements());
        return result;
    }

    /**
     * 해당 영화에 대한 모든 리뷰 가져오기
     */
    public CommentMovPagenationVO getCommentsByMovie(Long movieNo, int pageNum, int size) {
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("regDate").descending());
        Page<CommentMov> pages = commentMovRepository.findAllByMovieNo(movieNo, pageable);

        List<CommentMov> list = pages.getContent();

        log.info("조회된 댓글 수 : {}", list.size());

        List<CommentMovVO> vos = list.stream().map(CommentMovVO::create).toList();

        return CommentMovPagenationVO.builder()
                .commentMovsList(vos)
                .totalPageCnt(pages.getTotalPages())
                .build();
    }

    /**
     * 해당 영화에 대한 모든 리뷰의 전체 페이지 수
     *
     * @deprecated 제거 예정
     */
    public Integer getTotalPageCountByMovieNo(Long movieNo, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findAllByMovieNo(movieNo, pageable);
        return page.getTotalPages();
    }

    /**
     * 전체 리뷰 조회
     */
    public List<CommentMov> getComments(int pageNum, int size) {
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findAll(pageable);

        return page.getContent();
    }

    /**
     * 사용자가 쓴 댓글 조회(수정, 삭제 버튼)
     *
     * @param memNo
     * @param page
     */
    public List<CommentMovVO> getCommentsByMember(Long memNo, int page, int size) {
        Member member = memberRepository.findById(memNo).orElse(null);

        List<CommentMovVO> result = new ArrayList<>();

        if (member != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<CommentMov> pages = commentMovRepository.findByWriter(member, pageable);
            List<CommentMov> list = pages.getContent();

            result = list.stream().map(CommentMovVO::create).toList();
        } else {
            log.error("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo: {})", memNo);
        }

        return result;
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
    public CommentMovVO getCommentById(Long commentId) {
        CommentMov comm = commentMovRepository.findById(commentId).orElse(null);

        CommentMovVO result = null;
        if (comm != null) {
            result = CommentMovVO.create(comm);
        } else {
            log.error("해당 아이디의 댓글 정보가 존재하지 않습니다. (commentId: {})", commentId);
        }
        return result;
    }
}
