package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.BoardImpVO;
import com.portfolio.demo.project.vo.CommentImpPagenationVO;
import com.portfolio.demo.project.vo.CommentImpVO;
import com.portfolio.demo.project.vo.MemberVO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    MemberVO createUser() {
        return memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.user().build()
                )
        );
    }

    MemberVO createRandomUser() {
        return memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.randomIdentifierUser().build()
                )
        );
    }

    BoardImpVO createBoard(BoardImp board, MemberVO member) {
        BoardImpVO imp = BoardImpVO.create(board);
        imp.setWriterId(member.getMemNo());
        return boardImpService.updateBoard(imp);
    }

    CommentImpVO createComment(CommentImp comment, BoardImpVO board, MemberVO member) {
        CommentImpVO comm = CommentImpVO.create(comment);
        comm.setBoardId(board.getId());
        comm.setWriterId(member.getMemNo());
        return commentImpService.updateComment(comm);
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
        MemberVO user = createRandomUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpVO b = createBoard(board, user);


        // when
        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm = createComment(comment, b, user);

        // then
        Assertions.assertNotNull(comm.getId());
    }

    @Test
    void 댓글_수정() {
        // given
        MemberVO user = createUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpVO b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm = createComment(comment, b, user);

        // when
        comm.setContent("Modified content.");
        commentImpService.updateComment(comm);

        CommentImpVO foundComment = commentImpService.getCommentById(comm.getId());

        // then
        Assertions.assertEquals("Modified content.", foundComment.getContent());
    }

    @Test
    void id를_이용한_댓글_삭제() {
        // given
        MemberVO user = createUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpVO b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm = createComment(comment, b, user);

        // when
        commentImpService.deleteCommentById(comm.getId());
        CommentImpVO foundComment = commentImpService.getCommentById(comm.getId());

        // then
        Assertions.assertNull(foundComment);
    }

    @Test
    void 특정_게시글의_댓글_조회() {
        // given
        MemberVO user = createRandomUser();

        MemberVO user2 = createRandomUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpVO b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm = createComment(comment, b, user);

        CommentImp comment2 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm2 = createComment(comment2, b, user);

        CommentImp comment3 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm3 = createComment(comment3, b, user2);

        // when
        CommentImpPagenationVO vo = commentImpService.getCommentsByBoard(b.getId(), 0, 20);
        List<CommentImpVO> list = vo.getCommentImpsList();

                // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 특정_회원의_댓글_조회() {
        // given
        MemberVO user = createUser();

        MemberVO user2 = createRandomUser();

        BoardImp board = BoardImpTestDataBuilder.board().build();
        BoardImpVO b = createBoard(board, user);

        CommentImp comment = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm = createComment(comment, b, user);

        CommentImp comment2 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO comm2 = createComment(comment2, b, user);

        CommentImp comment3 = CommentImpTestDataBuilder.randomComment().build();
        CommentImpVO com3m = createComment(comment3, b, user2);

        // when
        List<CommentImpVO> list = commentImpService.getCommentsByMember(user.getMemNo(), 0, 20);
        List<CommentImpVO> list2 = commentImpService.getCommentsByMember(user2.getMemNo(), 0, 20);

        // then
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(1, list2.size());
    }
}