package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.repository.CommentMovRepository;
import com.portfolio.demo.project.vo.CommentMovPagenationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommentMovService {

    @Autowired
    CommentMovRepository commentMovRepository;

    private final static int COMMENTS_PER_PAGE = 20;

    // 댓글 작성
    public Long saveComment(Long writerNo, String content, Long movieNo, int rating) {
        return commentMovRepository.save(new CommentMov(null, writerNo, content, movieNo, null, rating)).getCommentId();
    }


    /**
     * 댓글 출력(Ajax 비동기 통신 사용)
     */

    public Map<String, Object> getCommentListOrderByRecommended(int pageNum, Long movieId) {
        int totalCommentCnt = getAllCommentCnt();
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

        Map<String, Object> result = new HashMap<>();
        result.put("list", print(pagenationVO));
        result.put("totalPageCnt", pagenationVO.getTotalPageCnt());
        return result;

    }

    public Map<String, Object> getCommentListOrderByRegDate(int pageNum, Long movieId) {
        int totalCommentCnt = getAllCommentCnt();
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

//        return print(pagenationVO);
        Map<String, Object> result = new HashMap<>();
        result.put("list", print(pagenationVO));
        result.put("totalPageCnt", pagenationVO.getTotalPageCnt());
        return result;
    }

    private int getAllCommentCnt() {
        return commentMovRepository.findAllCommentMovsCnt();
    }

    private String print(CommentMovPagenationVO pagenationVO) {
        if (pagenationVO.getCommentMovsList() != null){

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append("<table style=''>");
            for (CommentMov comm : pagenationVO.getCommentMovsList()) {
                strBuilder.append(
                        "<tr>" +
                                "<td>" + comm.getWriter() + "</td>" +
                                "<td>" + comm.getContent() + "</td>" +
                                "<td>" + comm.getRegDate() + "</td>" +
                                "<td>" + comm.getRecommended() + "</td>" +
                                "</tr>");
            }
            return strBuilder.toString();
        } else {
            return "";
        }
    }
}
