package com.portfolio.demo.project.service;

import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.dto.board.BoardImpParam;
import com.portfolio.demo.project.dto.board.ImpressionPagenationParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardImpServiceTest {

    @Autowired
    private BoardImpService boardImpService;

    @Autowired
    private MemberService memberService;
    @Autowired
    private CommentImpService commentImpService;

    MemberParam createUser() {
        Member user = MemberTestDataBuilder.user().build();
        MemberParam member = MemberParam.create(user);
        Long memNo = memberService.saveMember(member);

        return memberService.findByMemNo(memNo);
    }

    MemberParam createRandomUser() {
        Member user = MemberTestDataBuilder.randomIdentifierUser().build();
        MemberParam member = MemberParam.create(user);
        Long memNo = memberService.saveMember(member);

        return memberService.findByMemNo(memNo);
    }

    BoardImpParam createBoard(BoardImp board, MemberParam member) {
        BoardImpParam imp = BoardImpParam.createWithoutWriterAndRegDate(board);
        if (member != null) {
            imp.setWriterId(member.getMemNo());
        }
        Long id = boardImpService.saveBoard(imp);

        return boardImpService.findById(id);
    }

    CommentImpParam createRandomComment(BoardImpParam board, MemberParam member) {
        CommentImp c = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam param = CommentImpParam.createWithoutBoardAndWriterAndRegDate(c);
        param.setBoardId(board.getId());
        param.setWriterId(member.getMemNo());
        Long id = commentImpService.saveComment(param);

        return commentImpService.findById(id);
    }

    @Test
    void 모든_감상평_게시글_조회() {
        // then
        MemberParam user = createUser();

        for (int n = 0; n < 3; n++) {
            BoardImpParam created = BoardImpParam.createWithoutWriterAndRegDate(
                    BoardImpTestDataBuilder.board()
                            .title("test-board-" + n)
                            .build()
            );
            created.setWriterId(user.getMemNo());

            boardImpService.saveBoard(created);
        }

        // when
        List<BoardImpParam> list = boardImpService.getAllBoards(0, 10).getBoardImpList();

        // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 감상평_게시글_식별번호를_이용한_단건_조회() {
        // given
        MemberParam user = createUser();
        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpParam created = createBoard(board, user);

        // when
        BoardImpParam foundBoard = boardImpService.findById(created.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(created.getId(), foundBoard.getId());
    }

    @Test
    void 감상평_게시글_식별번호를_이용해_이전글_조회() {
        // given
        MemberParam user = createUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board().title("Previous Board").build();
        BoardImpParam createdPrev = createBoard(prevBoard, user);
        BoardImp nextBoard = BoardImpTestDataBuilder.board().title("Next board").build();
        BoardImpParam createdNext = createBoard(nextBoard, user);

        // when
        BoardImpParam foundBoard = boardImpService.findPrevById(createdNext.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(createdPrev.getId(), foundBoard.getId());
    }

    @Test
    void 감상평_게시글_식별번호를_이용해_다음글_조회() {
        // given
        MemberParam user = createUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board().title("Previous Board").build();
        BoardImpParam createdPrev = createBoard(prevBoard, user);
        BoardImp nextBoard = BoardImpTestDataBuilder.board().title("Next board").build();
        BoardImpParam createdNext = createBoard(nextBoard, user);

        // when
        BoardImpParam foundBoard = boardImpService.findNextById(createdPrev.getId());

        // then
        Assertions.assertNotNull(foundBoard);
        Assertions.assertEquals(createdNext.getId(), foundBoard.getId());
    }

    @Test
    void 사용자의_감상평_게시글_조회_최신순() throws InterruptedException {
        // given
        MemberParam user = createUser();

        BoardImpParam board1 = BoardImpParam.createWithoutWriterAndRegDate(
                BoardImpTestDataBuilder.board().title("test-board-1").build()
        );
        board1.setWriterId(user.getMemNo());
        boardImpService.saveBoard(board1);

        Thread.sleep(500);

        BoardImpParam board2 = BoardImpParam.createWithoutWriterAndRegDate(
                BoardImpTestDataBuilder.board().title("test-board-2").build()
        );
        board2.setWriterId(user.getMemNo());
        boardImpService.saveBoard(board2);

        Thread.sleep(500);

        BoardImpParam board3 = BoardImpParam.createWithoutWriterAndRegDate(
                BoardImpTestDataBuilder.board().title("test-board-3").build()
        );
        board3.setWriterId(user.getMemNo());
        boardImpService.saveBoard(board3);

        List<BoardImpParam> list = boardImpService.getImpsByMember(user.getMemNo(), 0, 10);

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
        MemberParam user = createUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().title("board-1").build();
        BoardImpParam board = createBoard(b1, user);
        Long bId1 = boardImpService.saveBoard(board);
        boardImpService.upViewCntById(bId1);

        BoardImp b2 = BoardImpTestDataBuilder.board().title("board-2").build();
        BoardImpParam board2 = createBoard(b2, user);
        Long bId2 = boardImpService.saveBoard(board2);
        boardImpService.upViewCntById(bId2);
        boardImpService.upViewCntById(bId2);

        BoardImp b3 = BoardImpTestDataBuilder.board().title("board-3").build();
        BoardImpParam board3 = createBoard(b3, user);
        Long bId3 = boardImpService.saveBoard(board3);
        boardImpService.upViewCntById(bId3);
        boardImpService.upViewCntById(bId3);
        boardImpService.upViewCntById(bId3);

        // when
        List<BoardImpParam> list = boardImpService.getMostFavImpBoard(3);
        list.forEach(b -> System.out.println(b.getTitle() + " --- " + b.getViews()));

        // then
        Assertions.assertEquals(3, list.size());

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, list.get(0).getViews()),
                () -> Assertions.assertEquals(2, list.get(1).getViews()),
                () -> Assertions.assertEquals(1, list.get(2).getViews())
        );
    }

    @Test
    void 감상평_게시글_작성_저장된_회원_참조() {
        // given
        MemberParam user = createUser();

        BoardImp imp = BoardImpTestDataBuilder.board().build();

        // when
        BoardImpParam board = createBoard(imp, user);

        // then
        Assertions.assertNotNull(board.getId());
    }

    @Test
    void 감상평_게시글_조회수_증가() {
        // given
        MemberParam user = createUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        BoardImpParam board = createBoard(b1, user);
        int views0 = board.getViews();

        boardImpService.upViewCntById(board.getId());
        BoardImpParam foundBoard = boardImpService.findById(board.getId());
        int views1 = foundBoard.getViews();

        boardImpService.upViewCntById(board.getId());
        BoardImpParam foundBoard2 = boardImpService.findById(board.getId());
        int views2 = foundBoard2.getViews();

        Assertions.assertEquals(0, views0);
        Assertions.assertEquals(1, views1);
        Assertions.assertEquals(2, views2);
    }

    @Test
    void 감상평_게시글의_식별번호로_삭제() {
        // given
        MemberParam user = createUser();

        // when
        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        BoardImpParam board = createBoard(b1, user);

        boardImpService.deleteById(board.getId());
        BoardImpParam foundBoard = boardImpService.findById(board.getId());

        // then
        Assertions.assertNull(foundBoard);
    }

    @Test
    void 감상평_게시글_다수_삭제() {
        // given
        MemberParam user = createUser();

        // when
        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        BoardImpParam board = createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        BoardImpParam board2 = createBoard(b2, user);

        List<BoardImpParam> list = Arrays.asList(board, board2);

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
        MemberParam user = createUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        createBoard(b2, user);

        BoardImp b3 = BoardImpTestDataBuilder.board().build();
        createBoard(b3, user);

        // when
        ImpressionPagenationParam pagenationVO = boardImpService.getImpPagenation(0, 10);

        // then
        Assertions.assertEquals(3, pagenationVO.getBoardImpList().size());
        Assertions.assertEquals(1, pagenationVO.getTotalPageCnt());
    }

    @Test
    void 작성자명으로_감상평_게시글_조회_페이지네이션vo() {
        // given
        MemberParam userParam = MemberParam.create(
                MemberTestDataBuilder.user().name("test-user").build()
        );
        Long memNo = memberService.saveMember(userParam);
        MemberParam user = memberService.findByMemNo(memNo);

        MemberParam userParam2 = MemberParam.create(
                MemberTestDataBuilder.randomIdentifierUser().name("test-random-user").build()
        );
        Long memNo2 = memberService.saveMember(userParam2);
        MemberParam user2 = memberService.findByMemNo(memNo2);

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        createBoard(b2, user);

        BoardImp b3 = BoardImpTestDataBuilder.board().build();
        createBoard(b3, user2);

        // when
        ImpressionPagenationParam pagenationVO = boardImpService.getImpPagenationByWriterName(0, 10, user.getName());
        ImpressionPagenationParam pagenationVO2 = boardImpService.getImpPagenationByWriterName(0, 10, "test-user-name");
        ImpressionPagenationParam pagenationVO3 = boardImpService.getImpPagenationByWriterName(0, 10, user2.getName());
        ImpressionPagenationParam pagenationVO4 = boardImpService.getImpPagenationByWriterName(0, 10, "test");

        // then
        Assertions.assertEquals(2, pagenationVO.getBoardImpList().size());
        Assertions.assertEquals(0, pagenationVO2.getBoardImpList().size());
        Assertions.assertEquals(1, pagenationVO3.getBoardImpList().size());
        Assertions.assertEquals(3, pagenationVO4.getBoardImpList().size());
    }

    @Test
    void 제목_또는_내용으로_감상평_게시글_조회_페이지네이션vo() {
        // given
        MemberParam userParam = MemberParam.create(
                MemberTestDataBuilder.user().name("test-user").build()
        );
        Long memNo = memberService.saveMember(userParam);
        MemberParam user = memberService.findByMemNo(memNo);

        MemberParam userParam2 = MemberParam.create(
                MemberTestDataBuilder.randomIdentifierUser().name("test-random-user").build()
        );
        Long memNo2 = memberService.saveMember(userParam2);
        MemberParam user2 = memberService.findByMemNo(memNo2);

        BoardImp b1 = BoardImpTestDataBuilder.board().title("테스트용 게시글입니다.").content("감상 후기를 작성하는 게시판입니다.").build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().title("감상 후기 게시글").content("테스트용 내용").build();
        createBoard(b2, user2);

        BoardImp b3 = BoardImpTestDataBuilder.board().title("test board title").content("테스트 진행중입니다.").build();
        createBoard(b3, user);

        // when
        ImpressionPagenationParam pagenation1 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "테스트");
        ImpressionPagenationParam pagenation2 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "감상 후기");
        ImpressionPagenationParam pagenation3 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "test");
        ImpressionPagenationParam pagenation4 = boardImpService.getImpPagenationByTitleOrContent(0, 10, "Test");

        // then
        Assertions.assertEquals(3, pagenation1.getBoardImpList().size());
        Assertions.assertEquals(2, pagenation2.getBoardImpList().size());
        Assertions.assertEquals(1, pagenation3.getBoardImpList().size());
        Assertions.assertEquals(1, pagenation4.getBoardImpList().size());
    }

    @Test
    void 사용자의_감상평_게시글_조회() {
        // given
        MemberParam user = createUser();

        MemberParam user2 = createRandomUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().build();
        createBoard(b1, user);

        BoardImp b2 = BoardImpTestDataBuilder.board().build();
        createBoard(b2, user);

        BoardImp b3 = BoardImpTestDataBuilder.board().build();
        createBoard(b3, user);

        BoardImp b4 = BoardImpTestDataBuilder.board().build();
        createBoard(b4, user2);

        // when
        List<BoardImpParam> boards = boardImpService.getImpsByMember(user.getMemNo(), 0, 10);
        List<BoardImpParam> boards2 = boardImpService.getImpsByMember(user2.getMemNo(), 0, 10);

        // then
        Assertions.assertEquals(3, boards.size());
        Assertions.assertEquals(1, boards2.size());
    }

    @Test
    void collection_size로_sort하기() {
        // given
        MemberParam boardWriter = createUser();
        MemberParam commentWriter = createRandomUser();

        BoardImpParam b1 = createBoard(BoardImpTestDataBuilder.board().build(), boardWriter);
        BoardImpParam b2 = createBoard(BoardImpTestDataBuilder.board().build(), boardWriter);

        for (int i = 0; i < 20; i++) {
            createRandomComment(b1, commentWriter);
        }
        for (int i = 0; i < 15; i++) {
            createRandomComment(b2, commentWriter);
        }

        // when
        ImpressionPagenationParam pagenationParam = boardImpService.getAllBoardsOrderByCommentSizeDesc(0, 10);

        // then
        Assertions.assertEquals(b1.getId(), pagenationParam.getBoardImpList().get(0).getId());
        Assertions.assertEquals(20, pagenationParam.getBoardImpList().get(0).getCommentSize());

        Assertions.assertEquals(b2.getId(), pagenationParam.getBoardImpList().get(1).getId());
        Assertions.assertEquals(15, pagenationParam.getBoardImpList().get(1).getCommentSize());
    }
}
