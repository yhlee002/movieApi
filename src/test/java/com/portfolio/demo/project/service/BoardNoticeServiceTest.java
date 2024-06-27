package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.model.BoardNoticeTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.dto.board.BoardNoticeParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import com.portfolio.demo.project.dto.board.NoticePagenationParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardNoticeServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    BoardNoticeService boardNoticeService;

    MemberParam createAdmin() {
        MemberParam admin = MemberParam.create(
                MemberTestDataBuilder.admin().build());
        Long memNo = memberService.saveMember(admin);
        return memberService.findByMemNo(memNo);
    }

    BoardNoticeParam createBoard(BoardNotice notice, MemberParam member) {
        BoardNoticeParam board = BoardNoticeParam.createWithoutWriterAndRegDate(notice);
        board.setWriterId(member.getMemNo());
        Long id = boardNoticeService.saveBoard(board);
        return boardNoticeService.findById(id);
    }

    @Test
    void 전체_공지_게시글_조회() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board().title("test-board-1").build();
        createBoard(board, admin);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board().title("test-board-2").build();
        createBoard(board2, admin);

        // when
        NoticePagenationParam vo = boardNoticeService.getAllBoards(0, 5);
        List<BoardNoticeParam> list = vo.getBoardNoticeList();

        // then
        assertEquals(2, list.size());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_단건_조회() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b1 = createBoard(board, admin);

        // when

        // then
        Assertions.assertNotNull(b1.getId());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_단건_조회_이전글_및_다음글() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice prevBoard = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b1 = createBoard(prevBoard, admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b2 = createBoard(board, admin);

        BoardNotice nextBoard = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b3 = createBoard(nextBoard, admin);

        // when
        BoardNoticeParam prevBoardNotice = boardNoticeService.findPrevById(b2.getId());
        BoardNoticeParam boardNotice = boardNoticeService.findById(b2.getId());
        BoardNoticeParam nextBoardNotice = boardNoticeService.findNextById(b2.getId());

        // then
        Assertions.assertEquals(b1.getId(), prevBoardNotice.getId());
        Assertions.assertEquals(b2.getId(), boardNotice.getId());
        Assertions.assertEquals(b3.getId(), nextBoardNotice.getId());
    }

    @Test
    void 최근_공지사항_게시글_n개_조회() throws InterruptedException {
        // given
        MemberParam admin = createAdmin();

        for (int i = 0; i < 10; i++) {
            BoardNotice board = BoardNoticeTestDataBuilder.board().build();
            createBoard(board, admin);

            Thread.sleep(1000);
        }

        // when
        List<BoardNoticeParam> list = boardNoticeService.getRecentNoticeBoards(10);
        List<BoardNoticeParam> list2 = boardNoticeService.getRecentNoticeBoards(7);

        // then
        Assertions.assertEquals(10, list.size());
        Assertions.assertEquals(7, list2.size());
    }

    @Test
    void 게시글_작성_및_수정() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b1 = createBoard(board, admin);

        // when
        b1.setTitle("Modified Title");
        b1.setContent("Modified content.");

        boardNoticeService.updateBoard(b1);

        BoardNoticeParam foundBoard = boardNoticeService.findById(b1.getId());

        // then
        Assertions.assertEquals("Modified Title", foundBoard.getTitle());
        Assertions.assertEquals("Modified content.", foundBoard.getContent());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_삭제() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b1 = createBoard(board, admin);

        // when
        boardNoticeService.deleteBoardByBoardId(b1.getId());
        BoardNoticeParam foundBoard = boardNoticeService.findById(b1.getId());

        // then
        Assertions.assertNull(foundBoard);
    }

    @Test
    void 공지사항_게시글_다수_삭제() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b1 = createBoard(board, admin);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b2 = createBoard(board2, admin);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b3 = createBoard(board3, admin);

        // when
        boardNoticeService.deleteBoards(
                Arrays.asList(b1, b2, b3)
        );

        NoticePagenationParam vo = boardNoticeService.getAllBoards(0, 10);
        List<BoardNoticeParam> list = vo.getBoardNoticeList();

        // then
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_조회수_증가() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board().build();
        BoardNoticeParam b1 = createBoard(board, admin);

        // when
        boardNoticeService.upViewCntById(b1.getId());
        boardNoticeService.upViewCntById(b1.getId());
        boardNoticeService.upViewCntById(b1.getId());

        BoardNoticeParam foundBoard = boardNoticeService.findById(b1.getId());

        // then
        Assertions.assertEquals(3, foundBoard.getViews());
    }

    @Test
    void 공지사항_게시글_페이지네이션vo() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board().build();
        createBoard(board, admin);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board().build();
        createBoard(board2, admin);

        // when
        NoticePagenationParam pagenation = boardNoticeService.getAllBoards(0, 10);

        // then
        Assertions.assertEquals(2, pagenation.getBoardNoticeList().size());
        Assertions.assertEquals(1, pagenation.getTotalPageCnt());
    }

    @Test
    void 제목_또는_내용으로_공지사항_게시글_조회_페이지네이션vo() {
        // given
        MemberParam admin = createAdmin();

        BoardNotice board = BoardNoticeTestDataBuilder.board()
                .title("test")
                .content("zxcv")
                .build();
        createBoard(board, admin);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board()
                .title("abcdef")
                .content("test")
                .build();
        createBoard(board2, admin);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board()
                .title("test-notice")
                .content("abc")
                .build();
        createBoard(board3, admin);

        // when
        NoticePagenationParam pagenation = boardNoticeService.getBoardNoticePagenationByTitleOrContent(0, 10, "test");
        NoticePagenationParam pagenation2 = boardNoticeService.getBoardNoticePagenationByTitleOrContent(0, 10, "abc");

        // then
        Assertions.assertEquals(3, pagenation.getBoardNoticeList().size());
        Assertions.assertEquals(1, pagenation.getTotalPageCnt());

        Assertions.assertEquals(2, pagenation2.getBoardNoticeList().size());
        Assertions.assertEquals(1, pagenation2.getTotalPageCnt());
    }
}