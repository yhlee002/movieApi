package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.util.TempKey;
import com.portfolio.demo.project.vo.CommentImpPagenationVO;
import com.portfolio.demo.project.vo.CommentImpVO;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@SpringBootTest
//@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentImpServiceTest {

//    @Autowired
    private CommentImpService commentImpService;

//    @Autowired
    private MemberService memberService;

//    @Autowired
    private BoardImpService boardImpService;

    @BeforeEach
    void setUp() {
        CommentImpRepository commentImpRepository = Mockito.mock(CommentImpRepository.class);
        commentImpService = new CommentImpService(commentImpRepository);

        MemberRepository memberRepository = Mockito.mock(MemberRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        TempKey tempKey = new TempKey();
        memberService = new MemberService(memberRepository, passwordEncoder, tempKey);

        BoardImpRepository boardImpRepository = Mockito.mock(BoardImpRepository.class);
        boardImpService = new BoardImpService(boardImpRepository, memberRepository);
    }

    @Test
    void 댓글_작성() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        // when
        CommentImp comment = CommentImpTestDataBuilder.randomComment(user, board).build();
        commentImpService.saveComment(comment);

        // then
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    void 댓글_수정() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        CommentImp comment = CommentImpTestDataBuilder.randomComment(user, board).build();
        commentImpService.saveComment(comment);

        // when
        comment.updateContent("Modified content.");
        commentImpService.updateComment(comment);

        CommentImp foundComment = commentImpService.getCommentById(comment.getId());

        // then
        Assertions.assertEquals("Modified content.", foundComment.getContent());
    }

    @Test
    void id를_이용한_댓글_삭제() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        CommentImp comment = CommentImpTestDataBuilder.randomComment(user, board).build();
        commentImpService.saveComment(comment);

        // when
        commentImpService.deleteCommentById(comment.getId());
        CommentImp foundComment = commentImpService.getCommentById(comment.getId());

        // then
        Assertions.assertNull(foundComment);
    }

    @Test
    void 특정_게시글의_댓글_조회() {
        // given
        Member user = MemberTestDataBuilder.randomIdentifierUser().build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser().build();
        memberService.saveMember(user2);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        CommentImp comment = CommentImpTestDataBuilder.randomComment(user, board).build();
        commentImpService.saveComment(comment);

        CommentImp comment2 = CommentImpTestDataBuilder.randomComment(user, board).build();
        commentImpService.saveComment(comment2);

        CommentImp comment3 = CommentImpTestDataBuilder.randomComment(user2, board).build();
        commentImpService.saveComment(comment3);

        // when
        CommentImpPagenationVO vo = commentImpService.getCommentsByBoard(board, 0);
        List<CommentImpVO> list = vo.getCommentImpsList();

                // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 특정_회원의_댓글_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser().build();
        memberService.saveMember(user2);

        BoardImp board = BoardImpTestDataBuilder.board(user).build();
        boardImpService.updateBoard(board);

        CommentImp comment = CommentImpTestDataBuilder.randomComment(user, board).build();
        commentImpService.saveComment(comment);

        CommentImp comment2 = CommentImpTestDataBuilder.randomComment(user, board).build();
        commentImpService.saveComment(comment2);

        CommentImp comment3 = CommentImpTestDataBuilder.randomComment(user2, board).build();
        commentImpService.saveComment(comment3);

        // when
        List<CommentImp> list = commentImpService.getCommentsByMember(user, 0);
        List<CommentImp> list2 = commentImpService.getCommentsByMember(user2, 0);

        // then
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(1, list2.size());
    }
}