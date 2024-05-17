package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.dto.BoardImpParam;
import com.portfolio.demo.project.dto.ImpressionPagenationParam;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardImpService {

    private final BoardImpRepository boardImpRepository;

    private final MemberRepository memberRepository;

    /**
     * 전체 감상평 게시글 조회
     */
    public ImpressionPagenationParam getAllBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findAll(pageable);

        List<BoardImp> list = pages.getContent();

        log.info("조회된 게시글 수 : {}", list.size());

        List<BoardImpParam> vos = new ArrayList<>();
        list.forEach(boardImp -> {
            BoardImpParam vo = BoardImpParam.create(boardImp);

            vos.add(vo);
        });

        return ImpressionPagenationParam.builder()
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
    public BoardImpParam findById(Long id) {
        BoardImp boardImp = boardImpRepository.findById(id).orElse(null);

        BoardImpParam vo = null;

        if (boardImp != null) {
            vo = BoardImpParam.create(boardImp);
        }

        return vo;
    }

    /**
     * 감상평 게시글 식별번호로 이전글 조회
     *
     * @param id
     * @return
     */
    public BoardImpParam findPrevById(Long id) {
        BoardImp boardImp = boardImpRepository.findPrevBoardImpById(id);

        BoardImpParam vo = null;

        if (boardImp != null) {
            vo = BoardImpParam.create(boardImp);
        }
        return vo;
    }

    /**
     * 감상평 게시글 식별번호로 다음글 조회
     *
     * @param id
     * @return
     */
    public BoardImpParam findNextById(Long id) {
        BoardImp boardImp = boardImpRepository.findNextBoardImpById(id);

        BoardImpParam vo = null;

        if (boardImp != null) {
            vo = BoardImpParam.create(boardImp);
        }

        return vo;
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
    public BoardImpParam updateBoard(BoardImpParam imp) {
        // 작성자 정보 검증
        Member writer = memberRepository.findById(imp.getWriterId()).orElse(null);

        BoardImpParam result = null;

        if (writer != null) {
            log.info("작성자 정보(memNo : {}) : valid", writer.getMemNo());
            BoardImp created = boardImpRepository.save(
                    BoardImp.builder()
                            .id(imp.getId())
                            .title(imp.getTitle())
                            .content(imp.getContent())
                            .writer(writer)
                            .views(imp.getViews())
                            .recommended(imp.getRecommended())
                            .build()
            );

            result = BoardImpParam.create(created);
        } else {
//            throw new IllegalStateException("해당 아이디의 회원 정보가 존재하지 않습니다.");
            log.error("해당 아이디의 회원 정보가 존재하지 않습니다. (memNo: {})", imp.getWriterId());
        }

        return result;
    }

    /**
     * 감상평 게시글 삭제
     *
     * @param id
     */
    @Transactional
    public void deleteById(Long id) {
        BoardImp board = boardImpRepository.findById(id).orElse(null);
        if (board != null) {
            boardImpRepository.delete(board);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 감상평 게시글 추천수 업데이트
     *
     * @param id
     */
    public void upViewCntById(Long id) {
        BoardImp imp = boardImpRepository.findById(id).orElse(null);
        if (imp != null) {
            BoardImp modified = BoardImp.builder()
                    .id(imp.getId())
                    .title(imp.getTitle())
                    .content(imp.getContent())
                    .writer(imp.getWriter())
                    .views(imp.getViews() + 1)
                    .recommended(imp.getRecommended())
                    .build();

            boardImpRepository.save(modified);
        } else {
            throw new IllegalStateException("해당 아이디의 게시글 정보가 존재하지 않습니다.");
        }
    }

    /**
     * 복수의 감상평 게시글 삭제
     *
     * @param boards
     */
    public void deleteBoards(List<BoardImpParam> boards) { // 자신이 작성한 글 목록에서 선택해서 삭제 가능
        List<BoardImp> list = boards.stream().map(b ->  {
            Member member = memberRepository.findById(b.getWriterId()).orElse(null);

            return BoardImp.builder()
                    .id(b.getId())
                    .title(b.getTitle())
                    .content(b.getContent())
                    .writer(member)
                    .views(b.getViews())
                    .recommended(b.getRecommended())
                    .build();
        }).toList();

        boardImpRepository.deleteAll(list);
    }

    /**
     * 감상평 게시글 조회
     *
     * @param page
     * @return
     */
    @Deprecated
    @Transactional
    public ImpressionPagenationParam getImpPagenation(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<BoardImp> pages = boardImpRepository.findAll(pageable);

        log.info("조회된 게시글 수 : {}", pages.getContent().size());

        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent().stream().map(BoardImpParam::create).toList())
                .build();
    }

    /**
     * 검색 기능 (작성자명)
     *
     * @param page
     * @param keyword
     */
    @Transactional
    public ImpressionPagenationParam getImpPagenationByWriterName(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findByWriterNameContainingIgnoreCaseOrderByRegDateDesc(keyword, pageable);

        log.info("조회된 게시글 수 : {}", pages.getContent().size());

        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent().stream().map(BoardImpParam::create).toList())
                .build();
    }

    /**
     * 검색 기능 (제목 또는 내용)
     *
     * @param page
     * @param keyword
     */
    @Transactional
    public ImpressionPagenationParam getImpPagenationByTitleOrContent(int page, Integer size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("regDate").descending());
        Page<BoardImp> pages = boardImpRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, pageable);
        return ImpressionPagenationParam.builder()
                .totalPageCnt(pages.getTotalPages())
                .boardImpList(pages.getContent().stream().map(BoardImpParam::create).toList())
                .build();

    }

    /**
     * 특정 작성자가 작성한 글(마이페이지에서 조회 가능)
     *
     * @param memNo
     * @param page
     * @param size
     */
    @Transactional
    public List<BoardImpParam> getImpsByMember(Long memNo, int page, int size) {
        Member member = memberRepository.findById(memNo).orElse(null);

        List<BoardImpParam> result = new ArrayList<>();

        if (member != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            Page<BoardImp> pages = boardImpRepository.findAllByWriter(member, pageable);
            List<BoardImp> list = pages.getContent();

            result = list.stream().map(BoardImpParam::create).toList();
        }

        return result;
    }

}
