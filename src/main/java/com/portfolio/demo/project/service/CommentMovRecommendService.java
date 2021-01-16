package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.comment.CommentMovRecommend;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.CommentMovRecommendRepository;
import com.portfolio.demo.project.repository.CommentMovRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentMovRecommendService {
    /**
     * 영화 상세 정보 페이지 코멘트(리뷰) 좋아요 기능 구현 서비스
     */

    @Autowired
    CommentMovRecommendRepository commentMovRecommendRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentMovRepository commentMovRepository;

    /**
     * 좋아요 했었던 상태라면 좋아요 취소(-1), 좋아요 하지 않은 상태라면 좋아요(+1)
     */

    @Transactional
    public CommentMovRecommend recommendModify(Long commentId, Long memNo) {
        if (commentId != null && memNo != null) {

            CommentMovRecommend info = getRecommendInfo(commentId, memNo); // 해당 유저의 해당 댓글에 대한 좋아요 레코드(기록)이 있는지 가져옴

            Member member = null;
            CommentMov comm = null;

            if (info != null) { // 좋아요되어있다면 좋아요 지우기
                commentMovRecommendRepository.delete(info);
                recommedCntDown(commentId); // // 코멘트 레코드의 추천 수 1 감소
                return null;

            } else { // 좋아요가 되어있지 않으면 좋아요 레코드 생성
                Optional<Member> memberOpt = memberRepository.findById(memNo);
                if (memberOpt.isPresent()) {
                    member = memberOpt.get();
                }

                Optional<CommentMov> commOpt = commentMovRepository.findById(commentId);
                if (commOpt.isPresent()) {
                    comm = commOpt.get();
                }

                commentMovRecommendRepository.save(CommentMovRecommend.builder()
                        .id(null)
                        .comment(comm)
                        .writer(member)
                        .build());

                // 코멘트 레코드의 추천 수 1 증가
                recommedCntUp(commentId);

                return commentMovRecommendRepository.findByCommentIdAndMemNo(commentId, memNo);
            }
        } else {
            return null;
        }

    }

    // 로그인된 회원의 해당 댓글에 대한 좋아요 정보 찾기
    public CommentMovRecommend getRecommendInfo(Long commentId, Long memNo) {
        CommentMovRecommend commRec = commentMovRecommendRepository.findByCommentIdAndMemNo(commentId, memNo);
        return commRec;
    }

    // 회원이 좋아요 한 댓글들의 아이디 목록 조회
    public List<Long> getRecommendInfoByMemNo(Long memNo) {

        List<Long> commIdList = commentMovRecommendRepository.findCommentIdByMemNo(memNo);
        log.info("해당 회원 아이디로 조회된 좋아요된 댓글 아이디 목록 : " + commIdList);
        return commIdList;
    }

    private void recommedCntUp(Long commentId) {
        CommentMov comm = null;
        Optional<CommentMov> commOpt = commentMovRepository.findById(commentId);
        if (commOpt.isPresent()) {
            comm = commOpt.get();
        }

        List<CommentMovRecommend> recommendList = commentMovRecommendRepository.findByCommentId(commentId);
        comm.setRecommended(comm.getRecommended() - 1);

        commentMovRepository.save(comm);
    }

    private void recommedCntDown(Long commentId) {
        CommentMov comm = null;
        Optional<CommentMov> commOpt = commentMovRepository.findById(commentId);
        if (commOpt.isPresent()) {
            comm = commOpt.get();
        }

        List<CommentMovRecommend> recommendList = commentMovRecommendRepository.findByCommentId(commentId);
        comm.setRecommended(comm.getRecommended() - 1);

        commentMovRepository.save(comm);
    }
}
