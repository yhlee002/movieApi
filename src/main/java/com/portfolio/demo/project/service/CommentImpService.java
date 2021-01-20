package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.CommentImpPagenationVO;
import com.portfolio.demo.project.vo.CommentImpVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommentImpService {

    @Autowired
    CommentImpRepository commentImpRepository;

    @Autowired
    BoardImpRepository boardImpRepository;

    @Autowired
    MemberRepository memberRepository;

    public List<CommentImpVO> getMyCommTop5(Long memNo) {
        List<CommentImp> commList = commentImpRepository.findTop5ByWriter_MemNoOrderByRegDateDesc(memNo);
        List<CommentImpVO> commVOList = new ArrayList<>();
        for (CommentImp c : commList) {
            commVOList.add(new CommentImpVO(c));
        }
        return commVOList;
    }

    public CommentImp saveComment(String content, Long boardId, Long memNo) {
        BoardImp imp = boardImpRepository.findById(boardId).get();
        Member writer = memberRepository.findById(memNo).get();
        CommentImp commImp = CommentImp.builder()
                .id(null)
                .content(content)
                .writer(writer)
                .board(imp)
                .build();
        log.info("생성된 commImp의 내용 : " + commImp.getContent());
        return commentImpRepository.save(commImp);
    }

    public CommentImp updateComment(Long commentId, String content) {
        CommentImp originImp = commentImpRepository.findById(commentId).get();
        originImp.setContent(content);
        return commentImpRepository.save(originImp);
    }

    public void deleteComment(Long commentId) {
        Optional<CommentImp> comm = commentImpRepository.findById(commentId);
        if (comm.isPresent()) {
            commentImpRepository.delete(comm.get());
        }
    }

    public List<CommentImpVO> getCommentVOList(Long boardId) {
        List<CommentImp> commList = commentImpRepository.findByBoardId(boardId);
        List<CommentImpVO> commVOList = new ArrayList<>();

        for (CommentImp comment : commList) {
            commVOList.add(new CommentImpVO(comment));
        }

        return commVOList;
    }

    public List<CommentImpVO> getCommentVOListByMemNo(Long memNo) {
        List<CommentImp> commList = commentImpRepository.findByWriter_MemNo(memNo);
        List<CommentImpVO> commVOList = new ArrayList<>();

        for (CommentImp comment : commList) {
            commVOList.add(new CommentImpVO(comment));
        }

        return commVOList;
    }

    private final static int COMMENT_COUNT_PER_PAGE = 20;
    private Long memNo;

    public void setMemNo(Long memNo) {
        this.memNo = memNo;
    }

    // 본인이 작성한 댓글(마이페이지에서 조회 가능)
    @Transactional
    public CommentImpPagenationVO getMyCommListView(int pageNum) {
        Long totalCommCnt = commentImpRepository.findCountByWriter_MemNo(memNo);
        int startRow = 0;
        List<CommentImpVO> commVOList = new ArrayList<>();
        CommentImpPagenationVO commPagenationVO = null;
        if (totalCommCnt > 0) {
            startRow = (pageNum - 1) * COMMENT_COUNT_PER_PAGE;

            List<CommentImp> commList = commentImpRepository.findCommImpListViewByWriterNo(memNo, startRow, COMMENT_COUNT_PER_PAGE);
            for (CommentImp comment : commList) {
                commVOList.add(new CommentImpVO(comment));
            }

        } else {
            pageNum = 0;
        }

        int endRow = startRow * COMMENT_COUNT_PER_PAGE;

        commPagenationVO = new CommentImpPagenationVO(totalCommCnt, pageNum, commVOList, COMMENT_COUNT_PER_PAGE, startRow, endRow);

        return commPagenationVO;
    }

}
