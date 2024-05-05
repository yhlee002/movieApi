package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardImpServiceTest {

    @Autowired
    private BoardImpService boardImpService;

    @Autowired
    private MemberService memberService;

    @Test
    void 모든_감상평_게시글_조회() {
        // then
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        for (int n = 0; n < 3; n++) {
            boardImpService.updateBoard(
                    BoardImpTestDataBuilder.board(user)
                            .title("test-board-" + n)
                            .build()
            );
        }

        // when
        List<BoardImp> list = boardImpService.getAllBoards(0, 10);

        // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 감상평_게시글_식별번호를_이용한_단건_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);
        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        // when
        BoardImp foundBoard = boardImpService.findById(board.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(board.getId(), foundBoard.getId());
    }

    @Test
    void 감상평_게시글_식별번호를_이용해_이전글_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp prevBoard = BoardImpTestDataBuilder.board(user).title("Previous Board").build();
        boardImpService.updateBoard(prevBoard);
        BoardImp nextBoard = BoardImpTestDataBuilder.board(user).title("Next board").build();
        boardImpService.updateBoard(nextBoard);

        // when
        BoardImp foundBoard = boardImpService.findPrevById(nextBoard.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(prevBoard.getId(), foundBoard.getId());
    }

    @Test
    void 감상평_게시글_식별번호를_이용해_다음글_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp prevBoard = BoardImpTestDataBuilder.board(user).title("Previous Board").build();
        boardImpService.updateBoard(prevBoard);
        BoardImp nextBoard = BoardImpTestDataBuilder.board(user).title("Next board").build();
        boardImpService.updateBoard(nextBoard);

        // when
        BoardImp foundBoard = boardImpService.findNextById(prevBoard.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        org.assertj.core.api.Assertions.assertThat(foundBoard).isEqualTo(nextBoard);
        Assertions.assertEquals(nextBoard.getId(), foundBoard.getId());
    }

    @Test
    void 사용자의_감상평_게시글_조회_최신순() throws InterruptedException {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        boardImpService.updateBoard(BoardImpTestDataBuilder.board(user)
                .title("test-board-1")
                .build());
        Thread.sleep(1000);
        boardImpService.updateBoard(BoardImpTestDataBuilder.board(user)
                .title("test-board-2")
                .build());
        Thread.sleep(1000);
        boardImpService.updateBoard(BoardImpTestDataBuilder.board(user)
                .title("test-board-3")
                .build());

        List<BoardImp> list = boardImpService.getRecentBoardImpsByMemberNo(user.getMemNo(), 1);
        List<BoardImp> list2 = boardImpService.getRecentBoardImpsByMemberNo(user.getMemNo(), 2);
        List<BoardImp> list3 = boardImpService.getRecentBoardImpsByMemberNo(user.getMemNo(), 3);

        list.forEach(b -> {
            System.out.println(b.getTitle() + " --- " + b.getRegDate());
        });

        list2.forEach(b -> {
            System.out.println(b.getTitle() + " --- " + b.getRegDate());
        });

        list3.forEach(b -> {
            System.out.println(b.getTitle() + " --- " + b.getRegDate());
        });

        // then
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(2, list2.size());
        Assertions.assertEquals(3, list3.size());

        Assertions.assertEquals("test-board-3", list.get(0).getTitle());
        Assertions.assertAll(
                () -> Assertions.assertEquals("test-board-3", list2.get(0).getTitle()),
                () -> Assertions.assertEquals("test-board-2", list2.get(1).getTitle())
        );
        Assertions.assertAll(
                () -> Assertions.assertEquals("test-board-3", list3.get(0).getTitle()),
                () -> Assertions.assertEquals("test-board-2", list3.get(1).getTitle()),
                () -> Assertions.assertEquals("test-board-1", list3.get(2).getTitle())
        );
    }

    @Test
    void 인기_감상평_게시글_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp board = BoardImpTestDataBuilder.board(user).title("board-1").build();
        for (int n = 0; n < 3; n++) board.updateViewCount();
        boardImpService.updateBoard(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user).title("board-2").build();
        for (int n = 0; n < 10; n++) board2.updateViewCount();
        boardImpService.updateBoard(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board(user).title("board-3").build();
        for (int n = 0; n < 7; n++) board3.updateViewCount();
        boardImpService.updateBoard(board3);

        // when
        List<BoardImp> list = boardImpService.getMostFavImpBoard(3);
        list.forEach(b -> System.out.println(b.getTitle() + " --- " + b.getViews()));

        // then
        Assertions.assertEquals(3, list.size());

        Assertions.assertAll(
                () -> Assertions.assertEquals(10, list.get(0).getViews()),
                () -> Assertions.assertEquals(7, list.get(1).getViews()),
                () -> Assertions.assertEquals(3, list.get(2).getViews())
        );
    }

    @Test
    void 감상평_게시글_작성_저장되지_않은_회원_참조() {
        // given
        MemberService memberServiceMock = Mockito.spy(memberService);
        Mockito.doReturn(
                MemberTestDataBuilder.admin()
                        .memNo(100L)
                        .build()
        ).when(memberServiceMock).findByMemNo(100L);

        Member user = memberService.findByMemNo(100L);
        BoardImp imp = BoardImpTestDataBuilder.board(user).build();

        // when & then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            boardImpService.updateBoard(imp);
        });
    }

    @Test
    void 감상평_게시글_작성_저장된_회원_참조() {
        // given
        Member user = memberService.saveMember(MemberTestDataBuilder.user().build());

        BoardImp imp = BoardImpTestDataBuilder.board(user).build();

        // when
        boardImpService.updateBoard(imp); // BoardImp savedImp =

        // then
        Assertions.assertNotNull(imp.getId());
    }

    @Test
    void 감상평_게시글_조회수_증가() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        int views0 = board.getViews();

        boardImpService.upViewCntById(board.getId());
        BoardImp foundBoard = boardImpService.findById(board.getId());
        int views1 = foundBoard.getViews();

        boardImpService.upViewCntById(board.getId());
        BoardImp foundBoard2 = boardImpService.findById(board.getId());
        int views2 = foundBoard2.getViews();

        Assertions.assertEquals(0, views0);
        Assertions.assertEquals(1, views1);
        Assertions.assertEquals(2, views2);
    }

    @Test
    void 감상평_게시글의_식별번호로_삭제() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        // when
        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        boardImpService.deleteById(board.getId());
        BoardImp foundBoard = boardImpService.findById(board.getId());

        // then
        Assertions.assertNull(foundBoard);
    }

    @Test
    void 감상평_게시글_다수_삭제() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        // when
        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board2);

        int exists = boardImpService.getAllBoards(0, 10).size();
        boardImpService.deleteBoards(Arrays.asList(new BoardImp[]{board, board2}));
        int exists2 = boardImpService.getAllBoards(0, 10).size();

        Assertions.assertEquals(2, exists);
        Assertions.assertEquals(0, exists2);
    }

    @Test
    void 감상평_게시글_조회_페이지네이션vo() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board3);

        // when
        ImpressionPagenationVO pagenationVO = boardImpService.getImpPagenation(0);

        // then
        Assertions.assertEquals(3, pagenationVO.getBoardImpList().size());
        Assertions.assertEquals(1, pagenationVO.getTotalPageCnt());
    }

    @Test
    void 작성자명으로_감상평_게시글_조회_페이지네이션vo() {
        // given
        Member user = MemberTestDataBuilder.user()
                .name("test-user")
                .build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser()
                .name("test-random-user")
                .build();
        memberService.saveMember(user2);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user2).build();
        boardImpService.updateBoard(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board3);

        // when
        ImpressionPagenationVO pagenationVO = boardImpService.getImpPagenationByWriterName(0, user.getName());
        ImpressionPagenationVO pagenationVO2 = boardImpService.getImpPagenationByWriterName(0, "test-user-name");
        ImpressionPagenationVO pagenationVO3 = boardImpService.getImpPagenationByWriterName(0, user2.getName());
        ImpressionPagenationVO pagenationVO4 = boardImpService.getImpPagenationByWriterName(0, "test");

        // then
        Assertions.assertEquals(2, pagenationVO.getBoardImpList().size());
        Assertions.assertEquals(0, pagenationVO2.getBoardImpList().size());
        Assertions.assertEquals(1, pagenationVO3.getBoardImpList().size());
        Assertions.assertEquals(3, pagenationVO4.getBoardImpList().size());
    }

    @Test
    void 제목_또는_내용으로_감상평_게시글_조회_페이지네이션vo() {
        // given
        Member user = MemberTestDataBuilder.user()
                .name("test-user")
                .build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser()
                .name("test-random-user")
                .build();
        memberService.saveMember(user2);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user2).build();
        boardImpService.updateBoard(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board3);

        // when

    }

    @Test
    void 사용자의_감상평_게시글_조회() {
        // given
        Member user = MemberTestDataBuilder.user()
                .build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser()
                .build();
        memberService.saveMember(user2);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board3);

        BoardImp board4 = BoardImpTestDataBuilder.board(user2).build();
        boardImpService.updateBoard(board4);

        // when
        List<BoardImp> boards = boardImpService.getImpsByMember(user, 0);
        List<BoardImp> boards2 = boardImpService.getImpsByMember(user2, 0);

        // then
        Assertions.assertEquals(3, boards.size());
        Assertions.assertEquals(1, boards2.size());
    }
}
