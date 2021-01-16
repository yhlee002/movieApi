package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.CommentImpVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public CommentImp saveComment(String content, Long boardId, Long memNo) {

        log.info("서비스로 들어온 commImp 정보// content : " + content + ", boardId : " + boardId + ", memNo : " + memNo);
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
//        log.info("commList : "+commList);
        List<CommentImpVO> commVOList = new ArrayList<>();

        for (CommentImp comment : commList) {
            commVOList.add(new CommentImpVO(comment));
        }

        return commVOList;
    }

}
