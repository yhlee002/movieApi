package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardNoticeTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.NoticePagenationVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardNoticeServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    BoardNoticeService boardNoticeService;

    @Test
    void 전체_공지_게시글_조회() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        boardNoticeService.updateBoard(
                BoardNoticeTestDataBuilder.board(admin).title("test-board-1").build()
        );

        boardNoticeService.updateBoard(
                BoardNoticeTestDataBuilder.board(admin).title("test-board-2").build()
        );

        // when
        List<BoardNotice> list = boardNoticeService.getAllBoards(0, 5);

        // then
        assertEquals(2, list.size());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_단건_조회() {
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board);

        // when
        BoardNotice foundBoard = boardNoticeService.getById(board.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(board.getId(), foundBoard.getId());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_단건_조회_이전글_및_다음글() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice prevBoard = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(prevBoard);
        BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board);
        BoardNotice nextBoard = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(nextBoard);

        // when
        Map<String, BoardNotice> result = boardNoticeService.getBoardsByBoardId(board.getId());

        // then
        Assertions.assertEquals(prevBoard.getId(), result.get("prevBoard").getId());
        Assertions.assertEquals(board.getId(), result.get("board").getId());
        Assertions.assertEquals(nextBoard.getId(), result.get("nextBoard").getId());
    }

    @Test
    void 최근_공지사항_게시글_n개_조회() throws InterruptedException {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        for (int i = 0; i < 10; i++) {
            BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
            boardNoticeService.updateBoard(board);
            Thread.sleep(1000);
        }

        // when
        List<BoardNotice> list = boardNoticeService.getRecentNoticeBoard(10);
        List<BoardNotice> list2 = boardNoticeService.getRecentNoticeBoard(7);

        // then
        Assertions.assertEquals(10, list.size());
        Assertions.assertEquals(7, list2.size());
    }

    @Test
    void 게시글_작성_및_수정() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board);

        // when
        board.updateContent("Modified content.");
        boardNoticeService.updateBoard(board);

        BoardNotice foundBoard = boardNoticeService.getById(board.getId());

        // then
        Assertions.assertEquals("Modified content.", foundBoard.getContent());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_삭제() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board);

        // when
        boardNoticeService.deleteBoardByBoardId(board.getId());
        BoardNotice foundBoard = boardNoticeService.getById(board.getId());

        // then
        Assertions.assertNull(foundBoard);
    }

    @Test
    void 공지사항_게시글_다수_삭제() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board2);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board3);

        // when
        boardNoticeService.deleteBoards(
                Arrays.asList(board, board2, board3)
        );

        List<BoardNotice> list = boardNoticeService.getAllBoards(0, 10);

        // then
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_조회수_증가() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board);

        // when
        boardNoticeService.upViewCntById(board.getId());
        boardNoticeService.upViewCntById(board.getId());
        boardNoticeService.upViewCntById(board.getId());

        // then
        Assertions.assertEquals(3, board.getViews());
    }

    @Test
    void 공지사항_게시글_페이지네이션vo() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board(admin).build();
        boardNoticeService.updateBoard(board2);

        // when
        NoticePagenationVO pagenation = boardNoticeService.getBoardNoticePagenation(0);

        // then
        Assertions.assertEquals(2, pagenation.getBoardNoticeList().size());
        Assertions.assertEquals(1, pagenation.getTotalPageCnt());
    }

    @Test
    void 제목_또는_내용으로_공지사항_게시글_조회_페이지네이션vo() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberService.saveMember(admin);

        BoardNotice board = BoardNoticeTestDataBuilder.board(admin)
                .title("test")
                .content("zxcv")
                .build();
        boardNoticeService.updateBoard(board);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board(admin)
                .title("abcdef")
                .content("test")
                .build();
        boardNoticeService.updateBoard(board2);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board(admin)
                .title("test-notice")
                .content("abc")
                .build();
        boardNoticeService.updateBoard(board3);

        // when
        NoticePagenationVO pagenation = boardNoticeService.getBoardNoticePagenationByTitleOrContent("test", 0);
        NoticePagenationVO pagenation2 = boardNoticeService.getBoardNoticePagenationByTitleOrContent("abc", 0);

        // then
        Assertions.assertEquals(3, pagenation.getBoardNoticeList().size());
        Assertions.assertEquals(1, pagenation.getTotalPageCnt());

        Assertions.assertEquals(2, pagenation2.getBoardNoticeList().size());
        Assertions.assertEquals(1, pagenation2.getTotalPageCnt());
    }
}