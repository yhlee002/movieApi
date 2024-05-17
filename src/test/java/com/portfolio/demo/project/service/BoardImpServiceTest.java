package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.BoardImpVO;
import com.portfolio.demo.project.vo.ImpressionPagenationVO;
import com.portfolio.demo.project.vo.MemberVO;
import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardImpServiceTest {

    @Autowired
    private BoardImpService boardImpService;

    @Autowired
    private MemberService memberService;

    MemberVO createUser() {
        Member user = MemberTestDataBuilder.user().build();
        MemberVO member = MemberVO.create(user);
        return memberService.updateMember(member);
    }

    MemberVO createRandomUser() {
        Member user = MemberTestDataBuilder.randomIdentifierUser().build();
        MemberVO member = MemberVO.create(user);
        return memberService.updateMember(member);
    }

    BoardImpVO createBoard(BoardImp board, MemberVO member) {
        BoardImpVO imp = BoardImpVO.create(board);
        imp.setWriterId(member.getMemNo());
        return boardImpService.updateBoard(imp);
    }

    @Test
    void 모든_감상평_게시글_조회() {
        // then
        MemberVO user = createUser();

        for (int n = 0; n < 3; n++) {
            BoardImpVO created = BoardImpVO.create(
                    BoardImpTestDataBuilder.board()
                            .title("test-board-" + n)
                            .build()
            );
            created.setWriterId(user.getMemNo());

            boardImpService.updateBoard(created);
        }

        // when
        List<BoardImpVO> list = boardImpService.getAllBoards(0, 10).getBoardImpList();

        // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 감상평_게시글_식별번호를_이용한_단건_조회() {
        // given
        MemberVO user = createUser();
        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpVO created = createBoard(board, user);

        // when
        BoardImpVO foundBoard = boardImpService.findById(created.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(created.getId(), foundBoard.getId());
    }

    @Test
    void 감상평_게시글_식별번호를_이용해_이전글_조회() {
        // given
        MemberVO user = createUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board().title("Previous Board").build();
        BoardImpVO createdPrev = createBoard(prevBoard, user);
        BoardImp nextBoard = BoardImpTestDataBuilder.board().title("Next board").build();
        BoardImpVO createdNext = createBoard(nextBoard, user);

        // when
        BoardImpVO foundBoard = boardImpService.findPrevById(createdNext.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(createdPrev.getId(), foundBoard.getId());
    }

    @Test
    void 감상평_게시글_식별번호를_이용해_다음글_조회() {
        // given
        MemberVO user = createUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board().title("Previous Board").build();
        BoardImpVO createdPrev = createBoard(prevBoard, user);
        BoardImp nextBoard = BoardImpTestDataBuilder.board().title("Next board").build();
        BoardImpVO createdNext = createBoard(nextBoard, user);

        // when
        BoardImpVO foundBoard = boardImpService.findNextById(createdPrev.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(createdNext.getId(), foundBoard.getId());
    }

    @Test
    void 사용자의_감상평_게시글_조회_최신순() throws InterruptedException {
        // given
        MemberVO user = createUser();

        BoardImpVO board1 = BoardImpVO.create(
                BoardImpTestDataBuilder.board().title("test-board-1").build()
        );
        boardImpService.updateBoard(board1);

        Thread.sleep(500);

        BoardImpVO board2 = BoardImpVO.create(
                BoardImpTestDataBuilder.board().title("test-board-2").build()
        );
        boardImpService.updateBoard(board2);

        Thread.sleep(500);

        BoardImpVO board3 = BoardImpVO.create(
                BoardImpTestDataBuilder.board().title("test-board-3").build()
        );
        boardImpService.updateBoard(board3);

        List<BoardImpVO> list = boardImpService.getImpsByMember(user.getMemNo(), 0, 10);

        // then
        Assertions.assertEquals(3, list.size());

        Assertions.assertEquals("test-board-3", list.get(0).getTitle());
        Assertions.assertAll(
                () -> Assertions.assertEquals("test-board-3", list.get(0).getTitle()),
                () -> Assertions.assertEquals("test-board-2", list.get(1).getTitle()),
                () -> Assertions.assertEquals("test-board-1", list.get(2).getTitle())
        );
    }

    @Test
    void 인기_감상평_게시글_조회() {
        // given
        MemberVO user = createUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().title("board-1").build();
        for (int n = 0; n < 3; n++) b1.updateViewCount();
        BoardImpVO board = createBoard(b1, user);
        boardImpService.updateBoard(board);

        BoardImp b2 = BoardImpTestDataBuilder.board().title("board-2").build();
        for (int n = 0; n < 10; n++) b2.updateViewCount();
        BoardImpVO board2 = createBoard(b2, user);
        boardImpService.updateBoard(board2);

        BoardImp b3 = BoardImpTestDataBuilder.board().title("board-3").build();
        for (int n = 0; n < 7; n++) b3.updateViewCount();
        BoardImpVO board3 = createBoard(b3, user);
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

        MemberVO user = memberService.findByMemNo(100L);
        BoardImp imp = BoardImpTestDataBuilder.board().build();
        BoardImpVO board = createBoard(imp, user);

        // when & then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            boardImpService.updateBoard(board);
        });
    }

    @Test
    void 감상평_게시글_작성_저장된_회원_참조() {
        // given
        MemberVO user = createUser();

        BoardImp imp = BoardImpTestDataBuilder.board().build();

        // when
        BoardImpVO board = createBoard(imp, user);

        // then
        Assertions.assertNotNull(imp.getId());
    }

    @Test
    void 감상평_게시글_조회수_증가() {
        // given
        MemberVO user = createUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        BoardImpVO board = createBoard(b1, user);
        boardImpService.updateBoard(board);

        int views0 = board.getViews();

        boardImpService.upViewCntById(board.getId());
        BoardImpVO foundBoard = boardImpService.findById(board.getId());
        int views1 = foundBoard.getViews();

        boardImpService.upViewCntById(board.getId());
        BoardImpVO foundBoard2 = boardImpService.findById(board.getId());
        int views2 = foundBoard2.getViews();

        Assertions.assertEquals(0, views0);
        Assertions.assertEquals(1, views1);
        Assertions.assertEquals(2, views2);
    }

    @Test
    void 감상평_게시글의_식별번호로_삭제() {
        // given
        MemberVO user = createUser();

        // when
        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        BoardImpVO board = createBoard(b1, user);

        boardImpService.deleteById(board.getId());
        BoardImpVO foundBoard = boardImpService.findById(board.getId());

        // then
        Assertions.assertNull(foundBoard);
    }

    @Test
    void 감상평_게시글_다수_삭제() {
        // given
        MemberVO user = createUser();

        // when
        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        BoardImpVO board = createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        BoardImpVO board2 = createBoard(b2, user);

        List<BoardImpVO> list = Arrays.asList(new BoardImpVO[]{board, board2});

        int exists = boardImpService.getAllBoards(0, 10).getBoardImpList().size();
        boardImpService.deleteBoards(list);
        int exists2 = boardImpService.getAllBoards(0, 10).getBoardImpList().size();

        // then
        Assertions.assertEquals(2, exists);
        Assertions.assertEquals(0, exists2);
    }

    @Test
    void 감상평_게시글_조회_페이지네이션vo() {
        // given
        MemberVO user = createUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        createBoard(b2, user);

        BoardImp b3 = BoardImpTestDataBuilder.board().build();
        createBoard(b3, user);

        // when
        ImpressionPagenationVO pagenationVO = boardImpService.getImpPagenation(0, 10);

        // then
        Assertions.assertEquals(3, pagenationVO.getBoardImpList().size());
        Assertions.assertEquals(1, pagenationVO.getTotalPageCnt());
    }

    @Test
    void 작성자명으로_감상평_게시글_조회_페이지네이션vo() {
        // given
        MemberVO user = memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.user().name("test-user").build()
                )
        );

        MemberVO user2 = memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.randomIdentifierUser().name("test-random-user").build()
                )
        );

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        createBoard(b2, user);

        BoardImp b3 = BoardImpTestDataBuilder.board().build();
        createBoard(b3, user2);

        // when
        ImpressionPagenationVO pagenationVO = boardImpService.getImpPagenationByWriterName(0, 10, user.getName());
        ImpressionPagenationVO pagenationVO2 = boardImpService.getImpPagenationByWriterName(0, 10, "test-user-name");
        ImpressionPagenationVO pagenationVO3 = boardImpService.getImpPagenationByWriterName(0, 10, user2.getName());
        ImpressionPagenationVO pagenationVO4 = boardImpService.getImpPagenationByWriterName(0, 10, "test");

        // then
        Assertions.assertEquals(2, pagenationVO.getBoardImpList().size());
        Assertions.assertEquals(0, pagenationVO2.getBoardImpList().size());
        Assertions.assertEquals(1, pagenationVO3.getBoardImpList().size());
        Assertions.assertEquals(3, pagenationVO4.getBoardImpList().size());
    }

    @Test
    void 제목_또는_내용으로_감상평_게시글_조회_페이지네이션vo() {
        // given
        MemberVO user = memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.user().name("test-user").build()
                )
        );

        MemberVO user2 = memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.randomIdentifierUser().name("test-random-user").build()
                )
        );

        BoardImp b1 = BoardImpTestDataBuilder.board().title("테스트용 게시글입니다.").content("감상 후기를 작성하는 게시판입니다.").build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().title("감상 후기 게시글").content("테스트용 내용").build();
        createBoard(b2, user2);

        BoardImp b3 = BoardImpTestDataBuilder.board().title("test board title").content("테스트 진행중입니다.").build();
        createBoard(b3, user);

        // when
        ImpressionPagenationVO pagenation1 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "테스트");
        ImpressionPagenationVO pagenation2 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "감상 후기");
        ImpressionPagenationVO pagenation3 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "test");
        ImpressionPagenationVO pagenation4 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "Test");

        // then
        Assertions.assertEquals(3, pagenation1.getBoardImpList().size());
        Assertions.assertEquals(2, pagenation2.getBoardImpList().size());
        Assertions.assertEquals(1, pagenation3.getBoardImpList().size());
        Assertions.assertEquals(1, pagenation4.getBoardImpList().size());
    }

    @Test
    void 사용자의_감상평_게시글_조회() {
        // given
        MemberVO user = createUser();

        MemberVO user2 = createRandomUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        createBoard(b2, user);

        BoardImp b3 = BoardImpTestDataBuilder.board().build();
        createBoard(b3, user);

        BoardImp b4 = BoardImpTestDataBuilder.board().build();
        createBoard(b4, user2);

        // when
        List<BoardImpVO> boards = boardImpService.getImpsByMember(user.getMemNo(), 0, 10);
        List<BoardImpVO> boards2 = boardImpService.getImpsByMember(user2.getMemNo(), 0, 10);

        // then
        Assertions.assertEquals(3, boards.size());
        Assertions.assertEquals(1, boards2.size());
    }
}
