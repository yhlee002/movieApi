package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.comment.CommentImpPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentMovPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentMovParam;
import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.CommentMovRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CommentMovService {

    private final CommentMovRepository commentMovRepository;

    private final MemberRepository memberRepository;

    /**
     * 추천순으로 댓글 조회(미개발)
     * @param movieId
     * @param pageNum
     * @param size
     */
    public CommentMovPagenationParam getCommentsOrderByRecommended(Long movieId, int pageNum, int size) {
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by(Sort.Direction.DESC, "recommended"));
        Page<CommentMov> page = commentMovRepository.findAllByMovieNo(movieId, pageable);

        return new CommentMovPagenationParam(page);
    }

    /**
     * 해당 영화에 대한 모든 리뷰 가져오기
     *
     * @param movieNo
     * @param pageNum
     * @param size
     */
    public CommentMovPagenationParam getCommentsByMovie(Long movieNo, int pageNum, int size) {
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("regDate").descending());

        Page<CommentMov> result =  null;
        if (movieNo != null) {
            result = commentMovRepository.findAllByMovieNo(movieNo, pageable);
        } else {
            result = commentMovRepository.findAll(pageable);
        }

        return new CommentMovPagenationParam(result);
    }

    /**
     * 전체 리뷰 조회
     *
     * @param pageNum
     * @param size
     */
    public List<CommentMov> getComments(int pageNum, int size) {
        Pageable pageable = PageRequest.of(pageNum, size, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findAll(pageable);

        return page.getContent();
    }

    /**
     * 사용자가 쓴 댓글 조회
     *
     * @param memNo
     * @param page
     * @param size
     */
    public CommentMovPagenationParam getCommentsByMember(Long memNo, int page, int size) {
        Member member = memberRepository.findById(memNo).orElse(null);

        CommentMovPagenationParam param = null;
        if (member != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
            Page<CommentMov> result = commentMovRepository.findByWriter(member, pageable);

            param = new CommentMovPagenationParam(result);
        } else {
            log.error("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo: {})", memNo);
        }

        return param;
    }

    /**
     * 댓글 작성
     *
     * @param commentParam
     */
    public Long saveComment(CommentMovParam commentParam) {
        Member user = memberRepository.findById(commentParam.getWriterId()).orElse(null);

        CommentMov comment = CommentMov.builder()
                .id(commentParam.getId())
                .content(commentParam.getContent())
                .writer(user)
                .movieNo(commentParam.getMovieNo())
                .rating(commentParam.getRating())
                .build();

        commentMovRepository.save(comment);

        return comment.getId();
    }

    /**
     * 댓글 수정
     *
     * @param commentParam
     */
    public Long updateComment(CommentMovParam commentParam) {
        CommentMov comment = commentMovRepository.findById(commentParam.getId()).orElse(null);

        if (comment != null) {
            comment.setContent(commentParam.getContent());
            comment.setRating(commentParam.getRating());
        } else {
            throw new IllegalStateException("해당 아이디의 댓글 정보가 존재하지 않습니다.");
        }

        return comment.getId();
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
    public CommentMovParam findById(Long commentId) {
        CommentMov comm = commentMovRepository.findById(commentId).orElse(null);

        CommentMovParam result = null;
        if (comm != null) {
            result = CommentMovParam.create(comm);
        } else {
            log.error("해당 아이디의 댓글 정보가 존재하지 않습니다. (commentId: {})", commentId);
        }
        return result;
    }
}
