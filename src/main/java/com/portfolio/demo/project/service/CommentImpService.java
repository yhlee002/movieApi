package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.CommentImpVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentImpService {

    private final int COMMENT_COUNT_PER_PAGE = 20;

    private final CommentImpRepository commentImpRepository;

    private final BoardImpRepository boardImpRepository;

    private final MemberRepository memberRepository;

    public List<CommentImpVO> getMyCommTop5(Long memNo) {
        List<CommentImp> commList = commentImpRepository.findTop5ByWriter_MemNoOrderByRegDateDesc(memNo);
        List<CommentImpVO> commVOList = new ArrayList<>();
        for (CommentImp c : commList) {
            commVOList.add(CommentImpVO.create(c));
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

    public List<CommentImpVO> getCommentsByBoard(BoardImp board, int page) {

        List<CommentImp> commList = commentImpRepository.findByBoard(board);
        List<CommentImpVO> commVOList = new ArrayList<>();

        for (CommentImp comment : commList) {
            commVOList.add(CommentImpVO.create(comment));
        }

        return commVOList;
    }

    public List<CommentImpVO> getCommentsByMember(Member member, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, COMMENT_COUNT_PER_PAGE, Sort.by(Sort.Direction.DESC, "reg_dt"));
        Page<CommentImpVO> page = commentImpRepository.findAllByWriter(member, pageable).map(CommentImpVO::create);

        return page.getContent();
    }

//    /**
//     * @deprecated getMyComments로 대체 예정
//     * 본인이 작성한 댓글(마이페이지에서 조회 가능)
//     * @param pageNum 페이지 번호
//     */
//    @Transactional
//    public CommentImpPagenationVO getMyCommListView(int pageNum) {
//        Long totalCommCnt = commentImpRepository.countCommentImpsByWriter_MemNo(memNo);
//        int startRow = 0;
//        List<CommentImpVO> commVOList = new ArrayList<>();
//        CommentImpPagenationVO commPagenationVO = null;
//        if (totalCommCnt > 0) {
//            startRow = (pageNum - 1) * COMMENT_COUNT_PER_PAGE;
//
//            List<CommentImp> commList = commentImpRepository.findCommentImpsByWriterNo(memNo, startRow, COMMENT_COUNT_PER_PAGE);
//            for (CommentImp comment : commList) {
//                commVOList.add(CommentImpVO.create(comment));
//            }
//
//        } else {
//            pageNum = 0;
//        }
//
//        commPagenationVO = CommentImpPagenationVO.builder()
//                .totalCommentCnt(totalCommCnt)
//                .currentPageNo(pageNum)
//                .commentImpsList(commVOList)
//                .commentsPerPage(COMMENT_COUNT_PER_PAGE)
//                .build();
//
//        return commPagenationVO;
//    }

    public Long getTotalCommentCount(Member member) {
        return commentImpRepository.countCommentImpsByWriter(member);
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public List<CommentImpVO> getMyComments(Member member, int pageNum, String criteria) { // criteria : 정렬 기준

        Pageable pageable = PageRequest.of(pageNum, COMMENT_COUNT_PER_PAGE, Sort.by(Sort.Direction.DESC, criteria));
        Page<CommentImpVO> page = commentImpRepository.findAllByWriter(member, pageable).map(CommentImpVO::create);

        return page.getContent();
    }

}
