package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardImpService {

    private final int BOARD_COUNT_PER_PAGE = 10; // 한페이지 당 보여줄 게시글의 수

    private final BoardImpRepository boardImpRepository;

    /**
     * 전체 감상평 게시글 조회
     * @deprecated 페이지네이션되는 api 사용으로 사용되지 않음
     */
    public java.util.List<BoardImp> getAllBoards() {
        return boardImpRepository.findAll();
    }

    /**
     * 감상평 게시글 식별번호로 게시글 조회
     * @param id
     * @return
     */
    public BoardImp findById(Long id) {
        return boardImpRepository.findBoardImpById(id);
    }

    // 게시글 단건 조회 + 이전글, 다음글
    public HashMap<String, BoardImp> selectBoardsById(Long id) {
        BoardImp board = boardImpRepository.findBoardImpById(id);
        BoardImp prevBoard = boardImpRepository.findPrevBoardImpByBoardId(id);
        BoardImp nextBoard = boardImpRepository.findNextBoardImpByBoardId(id);
        HashMap<String, BoardImp> boardNoticeMap = new HashMap<>();
        boardNoticeMap.put("board", board);
        boardNoticeMap.put("prevBoard", prevBoard);
        boardNoticeMap.put("nextBoard", nextBoard);

        return boardNoticeMap;
    }

    /**
     * 내가 쓴 감상평 게시글 최신순 5개 조회
     * @param memNo
     * @return
     */
    public List<BoardImp> getMyImpTop5(Long memNo) {
        return boardImpRepository.findTop5ByWriter_MemNoOrderByRegDateDesc(memNo);
    }

    /**
     * 인기 감상평 게시글 top 5 조회
     */
    public List<BoardImp> getFavImpBoard() {
        return boardImpRepository.findTop5ByOrderByViewsDesc();
    }


    /**
     * 감상평 게시글 수정
     * @param imp
     */
    @Transactional
    public Long updateBoard(BoardImp imp) { // 해당 board에 boardId, memNo, regDt 등이 담겨 있다면 다른 내용들도 따로 set하지 않고 바로 save해도 boardId, memNo등이 같으니 변경을 감지하지 않을까?
        Member member = imp.getWriter();

        return boardImpRepository.save(
                BoardImp.builder()
                        .id(imp.getId())
                        .title(imp.getTitle())
                        .writer(member)
                        .content(imp.getContent())
                        .build()
        ).getId();
    }

    /**
     * 감상평 게시글 삭제
     * @param id
     */
    @Transactional
    public void deleteById(Long id) {
        BoardImp board = boardImpRepository.findBoardImpById(boardId);
        boardImpRepository.delete(board);
    }

    /**
     * 감상평 게시글 추천수 업데이트
     * @param id
     */
    public void upViewCnt(Long id) {
        BoardImp imp = boardImpRepository.findById(id).get();
        imp.setViews(imp.getViews() + 1);
        boardImpRepository.save(imp);
    }

    /**
     * 복수의 감상평 게시글 삭제
     * @param boards
     */
    public void deleteBoards(java.util.List<BoardImp> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        boardImpRepository.deleteAll(boards);
    }

    /**
     * 감상평 게시글 조회
     * @param page
     * @return
     */
    @Transactional
    public ImpressionPagenationVO getImps(int page) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by("id").descending());
        Page<BoardImp> pages = boardImpRepository.findAllByOrderByIdDesc(pageable);

        return ImpressionPagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent())
                .build();
    }

    /**
     * 검색 기능 (작성자명)
     * @param page
     * @param keyword
     */
    @Transactional
    public ImpressionPagenationVO getBoardImpsByWriterName(int page, String keyword) {
        Pageable pageable = PageRequest.of(page, BOARD_COUNT_PER_PAGE, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findAllByWriterNameOrderByRegDateDesc(keyword, pageable);
        return ImpressionPagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent())
                .build();
    }

    /**
     * 검색 기능 (제목 또는 내용)
     * @param pageNum
     * @param keyword
     */
    @Transactional
    public ImpressionPagenationVO getBoardImpsByTitleAndContent(int pageNum, String keyword) {
        Pageable pageable = PageRequest.of(pageNum, BOARD_COUNT_PER_PAGE, Sort.by(Sort.Direction.DESC, "id"));
        Page<BoardImp> pages = boardImpRepository.findAllByTitleOrContentContainingOrderByRegDate(keyword, keyword, pageable);
        return ImpressionPagenationVO.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent())
                .build();

    }

    /**
     * 본인이 작성한 글(마이페이지에서 조회 가능)
     * @param member
     * @param pageNum
     */
    @Transactional
    public List<BoardImp> getMyImpListView(Member member, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, BOARD_COUNT_PER_PAGE, Sort.by(Sort.Direction.DESC, "id"));
        Page<BoardImp> page = boardImpRepository.findAllByWriter(member, pageable);
        return page.getContent();
    }

}
