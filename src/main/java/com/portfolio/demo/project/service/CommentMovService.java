package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.repository.CommentMovRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.CommentMovPagenationVO;
import com.portfolio.demo.project.vo.CommentMovVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommentMovService {

    @Autowired
    CommentMovRepository commentMovRepository;

    @Autowired
    MemberRepository memberRepository;

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
     * 댓글 출력(Ajax 비동기 통신 사용)
     */

    public Map<String, Object> getCommentListVOOrderByRecommended(int pageNum, Long movieId) {
        long totalCommentCnt = getCommentCntByMovieId(movieId);
        int startRow = 0;
        List<CommentMov> commentMovList = null;

        if (totalCommentCnt > 0) {
            startRow = (pageNum - 1) * COMMENTS_PER_PAGE;
            commentMovList = commentMovRepository.findCommentMovsByOrderByRecommended(movieId, startRow, COMMENTS_PER_PAGE);
        } else {
            pageNum = 0;
        }
        int end = startRow * COMMENTS_PER_PAGE;

        CommentMovPagenationVO pagenationVO = new CommentMovPagenationVO(totalCommentCnt, pageNum, commentMovList, COMMENTS_PER_PAGE, startRow, end);

        // pagenationVO의 commentMovList를 VOList로 변환시켜 보냄(json 직렬화를 위해)
        List<CommentMovVO> commentMovVOList = new ArrayList<>();
        for (CommentMov comm : commentMovList) {
            commentMovVOList.add(new CommentMovVO(comm));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", commentMovVOList);
        result.put("totalPageCnt", pagenationVO.getTotalPageCnt());
        result.put("totalCommentCnt", pagenationVO.getTotalCommentCnt());
        return result;
    }

    public Map<String, Object> getCommentListVOOrderByRegDate(int pageNum, Long movieId) {
        long totalCommentCnt = getCommentCntByMovieId(movieId);
        int startRow = 0;
        List<CommentMov> commentMovList = null;
        List<CommentMovVO> commentMovVOList = new ArrayList<>(); // pagenationVO의 commentMovList를 VOList로 변환시켜 보냄(json 직렬화를 위해)

        if (totalCommentCnt > 0) {
            startRow = (pageNum - 1) * COMMENTS_PER_PAGE;
            commentMovList = commentMovRepository.findCommentMovsByOrderByRegDate(movieId, startRow, COMMENTS_PER_PAGE);
        } else {
            pageNum = 0;
        }
        int end = startRow * COMMENTS_PER_PAGE;

        // 아예 CommentMovPagenationVO 의 commentMovList 필드를 VO필드로 바꾸는 것도 고려해보기
        CommentMovPagenationVO pagenationVO = new CommentMovPagenationVO(totalCommentCnt, pageNum, commentMovList, COMMENTS_PER_PAGE, startRow, end);

        if (commentMovList != null) {
            for (CommentMov comm : commentMovList) {
                commentMovVOList.add(new CommentMovVO(comm));
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", commentMovVOList);
        result.put("totalPageCnt", pagenationVO.getTotalPageCnt());
        result.put("totalCommentCnt", pagenationVO.getTotalCommentCnt());
        return result;
    }
    
    // 해당 영화정보의 코멘트 모두 불러오기
    private Long getCommentCntByMovieId(Long movieId) {
        Long cnt = commentMovRepository.countByMovieNo(movieId);
        if (cnt == null) {
            cnt = 0L;
        }
        return cnt;
    }

    // 해당 영화에 대한 모든 리뷰 가져오기(안쓸듯)
    public List<CommentMovVO> getCommentVOListByMovieCd(Long movieCd) {
        List<CommentMov> commentMov = commentMovRepository.findByMovieNo(movieCd);
        List<CommentMovVO> commentMovVOList = new ArrayList<>();
        for (CommentMov comm : commentMov) {
            commentMovVOList.add(new CommentMovVO(comm));
        }
        return commentMovVOList;
    }

    // 사용자가 쓴 댓글 조회(수정, 삭제 버튼)
    public List<CommentMovVO> getCommentVOListByMemNo(Long memNo) {
        List<CommentMov> commentMov = commentMovRepository.findByWriter_MemNo(memNo);
        List<CommentMovVO> commentMovVOList = new ArrayList<>();
        for (CommentMov comm : commentMov) {
            commentMovVOList.add(new CommentMovVO(comm));
        }
        return commentMovVOList;
    }
}
