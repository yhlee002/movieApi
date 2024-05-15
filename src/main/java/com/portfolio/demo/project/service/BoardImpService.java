package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.vo.BoardImpVO;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
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
public class BoardImpService {

    private final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수

    private final BoardImpRepository boardImpRepository;
    private final MemberRepository memberRepository;

    /**
     * 전체 감상평 게시글 조회
     */
    public ImpressionPagenationVO getAllBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findAll(pageable);

        List<BoardImp> list = pages.getContent();

        log.info("조회된 게시글 수 : {}", list.size());

        List<BoardImpVO> vos = new ArrayList<>();
        list.forEach(boardImp -> {
            Member writer = (Member) Hibernate.unproxy(boardImp.getWriter());
//            List<CommentImp> comments = (List<CommentImp>) Hibernate.unproxy(boardImp.getComments());
//            comments.forEach(comm -> {
//                Member commentWriter = (Member) Hibernate.unproxy(comm.getWriter());
//                comm.updateWriter(commentWriter);
//            });
            boardImp.updateWriter(writer);
//            boardImp.updateComments(comments);
            boardImp.updateComments(new ArrayList<>());
            vos.add(BoardImpVO.create(boardImp));
        });


        return ImpressionPagenationVO.builder()
//                .boardImpList(list.stream().map(BoardImpVO::create).toList())
                .boardImpList(vos)
                .totalPageCnt(pages.getTotalPages())
                .build();
    }

    /**
     * 감상평 게시글 식별번호로 단건 조회
     *
     * @param id
     * @return
     */
    public BoardImp findById(Long id) {
        BoardImp boardImp = boardImpRepository.findBoardImpById(id);
        if (boardImp != null) {
            Member writer = Hibernate.unproxy(boardImp.getWriter(), Member.class);
//            List<CommentImp> comments = (List<CommentImp>) Hibernate.unproxy(boardImp.getComments());
            boardImp.updateWriter(writer);
            boardImp.updateComments(null);
        }
        return boardImp;
    }

    /**
     * 감상평 게시글 식별번호로 이전글 조회
     *
     * @param id
     * @return
     */
    public BoardImp findPrevById(Long id) {
        BoardImp boardImp = boardImpRepository.findPrevBoardImpById(id);
        if (boardImp != null) {
            Member writer = Hibernate.unproxy(boardImp.getWriter(), Member.class);
//            List<CommentImp> comments = (List<CommentImp>) Hibernate.unproxy(boardImp.getComments());
            boardImp.updateWriter(writer);
            boardImp.updateComments(null);
        }
        return boardImp;
    }

    /**
     * 감상평 게시글 식별번호로 다음글 조회
     *
     * @param id
     * @return
     */
    public BoardImp findNextById(Long id) {
        BoardImp boardImp = boardImpRepository.findNextBoardImpById(id);
        if (boardImp != null) {
            Member writer = Hibernate.unproxy(boardImp.getWriter(), Member.class);
//            List<CommentImp> comments = (List<CommentImp>) Hibernate.unproxy(boardImp.getComments());
            boardImp.updateWriter(writer);
            boardImp.updateComments(null);
        }

        return boardImp;
    }

    /**
     * 인기 감상평 게시글 top {size} 조회
     */
    public List<BoardImp> getMostFavImpBoard(int size) {
        return boardImpRepository.findMostFavImpBoards(size);
    }

    /**
     * 감상평 게시글 수정
     *
     * @param imp
     */
    @Transactional
    public void updateBoard(BoardImp imp) { // TODO. 해당 board에 boardId, memNo, regDt 등이 담겨 있다면 다른 내용들도 따로 set하지 않고 바로 save해도 boardId, memNo등이 같으니 변경을 감지하지 않을까?
        // 작성자 정보 검증
        Boolean exist = validateMember(imp.getWriter());
        if (exist) {
            log.info("작성자 정보(memNo : {}) : valid", imp.getWriter().getMemNo());

            boardImpRepository.save(imp);
        } else {
            log.error("작성자 정보가 누락되었습니다.");
            throw new IllegalStateException();
        }
    }

    /**
     * 해당 사용자 정보 확인
     *
     * @param member
     */
    public Boolean validateMember(Member member) {
        if (member == null) return false;
        else return memberRepository.existsById(member.getMemNo());
    }

    /**
     * 감상평 게시글 삭제
     *
     * @param id
     */
    @Transactional
    public void deleteById(Long id) {
        BoardImp board = boardImpRepository.findBoardImpById(id);
        boardImpRepository.delete(board);
    }

    /**
     * 감상평 게시글 추천수 업데이트
     *
     * @param id
     */
    public void upViewCntById(Long id) {
        BoardImp imp = boardImpRepository.findById(id).get();
        imp.updateViewCount();
        boardImpRepository.save(imp);
    }

    /**
     * 복수의 감상평 게시글 삭제
     *
     * @param boards
     */
    public void deleteBoards(java.util.List<BoardImp> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardImpRepository.deleteAll(boards);
    }

    /**
     * 감상평 게시글 조회
     *
     * @param page
     * @return
     */
    @Deprecated
    @Transactional
    public ImpressionPagenationVO getImpPagenation(int page) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by("id").descending());
        Page<BoardImp> pages = boardImpRepository.findAll(pageable);

        log.info("조회된 게시글 수 : {}", pages.getContent().size());

        return ImpressionPagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent().stream().map(BoardImpVO::create).toList())
                .build();
    }

    /**
     * 검색 기능 (작성자명)
     *
     * @param page
     * @param keyword
     */
    @Transactional
    public ImpressionPagenationVO getImpPagenationByWriterName(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findByWriterNameContainingIgnoreCaseOrderByRegDateDesc(keyword, pageable);

        log.info("조회된 게시글 수 : {}", pages.getContent().size());

        return ImpressionPagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent().stream().map(BoardImpVO::create).toList())
                .build();
    }

    /**
     * 검색 기능 (제목 또는 내용)
     *
     * @param page
     * @param keyword
     */
    @Transactional
    public ImpressionPagenationVO getImpPagenationByTitleOrContent(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return ImpressionPagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent().stream().map(BoardImpVO::create).toList())
                .build();

    }

    /**
     * 특정 작성자가 작성한 글(마이페이지에서 조회 가능)
     *
     * @param member
     * @param page
     */
    @Transactional
    public List<BoardImp> getImpsByMember(Member member, int page) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by(Sort.Direction.DESC, "id"));
        Page<BoardImp> pages = boardImpRepository.findAllByWriter(member, pageable);
        return pages.getContent();
    }

}
