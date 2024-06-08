package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.dto.board.BoardImpParam;
import com.portfolio.demo.project.dto.comment.CommentImpPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentImpServiceTest {

    @Autowired
    private CommentImpService commentImpService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private BoardImpService boardImpService;

    MemberParam createUser() {
        MemberParam member = MemberParam.create(
                MemberTestDataBuilder.user().build());
        Long memNo = memberService.saveMember(member);

        return memberService.findByMemNo(memNo);

    }

    MemberParam createRandomUser() {
        MemberParam member = MemberParam.create(
                MemberTestDataBuilder.randomIdentifierUser().build());
        Long memNo = memberService.saveMember(member);

        return memberService.findByMemNo(memNo);
    }

    BoardImpParam createBoard(BoardImp board, MemberParam member) {
        BoardImpParam imp = BoardImpParam.create(board);
        imp.setWriterId(member.getMemNo());
        Long id = boardImpService.saveBoard(imp);

        return boardImpService.findById(id);
    }

    CommentImpParam createComment(CommentImp comment, BoardImpParam board, MemberParam member) {
        CommentImpParam comm = CommentImpParam.create(comment);
        comm.setBoardId(board.getId());
        comm.setWriterId(member.getMemNo());
        Long id = commentImpService.saveComment(comm);

        return commentImpService.findById(id);
    }

//    @BeforeEach
//    void setUp() {
//        CommentImpRepository commentImpRepository = Mockito.mock(CommentImpRepository.class);
//        BoardImpRepository boardImpRepository = Mockito.mock(BoardImpRepository.class);
//        MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
//
//        commentImpService = new CommentImpService(commentImpRepository, boardImpRepository, memberRepository);
//
//        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
//        TempKey tempKey = new TempKey();
//        memberService = new MemberService(memberRepository, passwordEncoder, tempKey);
//
//        boardImpService = new BoardImpService(boardImpRepository, memberRepository);
//    }

    @Test
    void 댓글_작성() {
        // given
        MemberParam user = createRandomUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpParam b = createBoard(board, user);


        // when
        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm = createComment(comment, b, user);

        // then
        Assertions.assertNotNull(comm.getId());
    }

    @Test
    void 댓글_수정() {
        // given
        MemberParam user = createUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpParam b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm = createComment(comment, b, user);

        // when
        comm.setContent("Modified content.");
        commentImpService.updateComment(comm);

        CommentImpParam foundComment = commentImpService.findById(comm.getId());

        // then
        Assertions.assertEquals("Modified content.", foundComment.getContent());
    }

    @Test
    void id를_이용한_댓글_삭제() {
        // given
        MemberParam user = createUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpParam b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm = createComment(comment, b, user);

        // when
        commentImpService.deleteCommentById(comm.getId());
        CommentImpParam foundComment = commentImpService.findById(comm.getId());

        // then
        Assertions.assertNull(foundComment);
    }

    @Test
    void 특정_게시글의_댓글_조회() {
        // given
        MemberParam user = createRandomUser();

        MemberParam user2 = createRandomUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpParam b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm = createComment(comment, b, user);

        CommentImp comment2 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm2 = createComment(comment2, b, user);

        CommentImp comment3 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm3 = createComment(comment3, b, user2);

        // when
        CommentImpPagenationParam vo = commentImpService.getCommentsByBoard(b.getId(), 0, 20);
        List<CommentImpParam> list = vo.getCommentImpsList();

                // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 특정_회원의_댓글_조회() {
        // given
        MemberParam user = createUser();

        MemberParam user2 = createRandomUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpParam b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm = createComment(comment, b, user);

        CommentImp comment2 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam comm2 = createComment(comment2, b, user);

        CommentImp comment3 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpParam com3m = createComment(comment3, b, user2);

        // when
        List<CommentImpParam> list = commentImpService.getCommentsByMember(user.getMemNo(), 0, 20);
        List<CommentImpParam> list2 = commentImpService.getCommentsByMember(user2.getMemNo(), 0, 20);

        // then
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(1, list2.size());
    }
}