package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentMovTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.CommentImpVO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CommentImpRepositoryTest {

    @Autowired
    CommentImpRepository commentImpRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BoardImpRepository boardImpRepository;

    @PersistenceContext
    private EntityManager entityManager;

    Member createUser() {
        return MemberTestDataBuilder.user().build();
    }

    Member createRandomMember() {
        return MemberTestDataBuilder.randomIdentifierUser().build();
    }

    BoardImp createBoard(Member writer) {
        return BoardImpTestDataBuilder.board().writer(writer).build();
    }

    CommentImp createComment(Member writer, BoardImp board) {
        return CommentImpTestDataBuilder.randomComment().writer(writer).board(board).build();
    }

    @Test
    void 특정_후기_게시글의_코멘트_조회() {
        // given
        Member user = createRandomMember();
        memberRepository.save(user);

        BoardImp board = createBoard(user);
        boardImpRepository.save(board);

        CommentImp comment1 = createComment(user, board);
        CommentImp comment2 = createComment(user, board);
        CommentImp comment3 = createComment(user, board);
        List<CommentImp> list = Arrays.asList(comment1, comment2, comment3);
        commentImpRepository.saveAll(list);

        BoardImp board2 = createBoard(user);
        boardImpRepository.save(board2);

        commentImpRepository.save(createComment(user, board2));

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<CommentImp> comments = commentImpRepository.findAllByBoard(board, pageable);
        Page<CommentImp> comments2 = commentImpRepository.findAllByBoard(board2, pageable);

        // then
        Assertions.assertEquals(3, comments.getTotalElements());
        comments.getContent().forEach(c -> Assertions.assertEquals(c.getBoard().getId(), board.getId()));

        Assertions.assertEquals(1, comments2.getTotalElements());
        comments2.getContent().forEach(c -> Assertions.assertEquals(c.getBoard().getId(), board2.getId()));
    }

    @Test
    void 작성자를_이용한_코멘트_조회() {
        // given
        Member user = createRandomMember();
        Member user2 = createRandomMember();
        memberRepository.saveAll(List.of(user, user2));

        BoardImp board = createBoard(user);
        boardImpRepository.save(board);

        commentImpRepository.saveAll(
                Arrays.asList(
                        createComment(user, board),
                        createComment(user, board),
                        createComment(user, board),
                        createComment(user2, board)
                )
        );

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<CommentImp> comments = commentImpRepository.findAllByWriter(user, pageable);
        Page<CommentImp> comments2 = commentImpRepository.findAllByWriter(user2, pageable);

        // then
        Assertions.assertEquals(3, comments.getTotalElements());
        Assertions.assertEquals(1, comments2.getTotalElements());
    }

    @Test
    void 작성자를_이용한_코멘트_수_조회() {
        // given
        Member user = createRandomMember();
        Member user2 = createRandomMember();
        memberRepository.saveAll(List.of(user, user2));

        BoardImp board = createBoard(user);
        boardImpRepository.save(board);

        commentImpRepository.saveAll(Arrays.asList(
                createComment(user, board),
                createComment(user, board),
                createComment(user, board),
                createComment(user2, board), // the other member
                createComment(user2, board) // the other member
        ));

        // when
        Integer cnt = commentImpRepository.countCommentImpsByWriter(user);
        Integer cnt2 = commentImpRepository.countCommentImpsByWriter(user2);

        // then
        Assertions.assertEquals(3, cnt);
        Assertions.assertEquals(2, cnt2);
    }

    @Test
    void 모든_코멘트_조회() {
        // given
        Member user = createRandomMember();
        Member user2 = createRandomMember();
        memberRepository.saveAll(Arrays.asList(user, user2));
        BoardImp board = createBoard(user);
        BoardImp board2 = createBoard(user2);
        boardImpRepository.saveAll(Arrays.asList(board, board2));
        commentImpRepository.saveAll(
                Arrays.asList(
                        createComment(user, board),
                        createComment(user, board),
                        createComment(user, board),
                        createComment(user2, board),
                        createComment(user2, board2),
                        createComment(user, board),
                        createComment(user, board2),
                        createComment(user2, board)
                )
        );

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<CommentImp> comments = commentImpRepository.findAll(pageable);

        // then
        Assertions.assertEquals(7, comments.getTotalElements());
    }

    @Test
    void 영화_코멘트_작성() {
        // given
        Member user = createUser();
        memberRepository.save(user);

        BoardImp board = createBoard(user);
        boardImpRepository.save(board);

        // when
        CommentImp comment = createComment(user, board);
        commentImpRepository.save(comment);

        // then
        Assertions.assertNotNull(comment.getRegDate());
    }

    @Test
    void 영화_코멘트_수정() {
        // given
        Member user = createUser();
        memberRepository.save(user);

        BoardImp board = createBoard(user);
        boardImpRepository.save(board);

        // when
        CommentImp comment = createComment(user, board);
        commentImpRepository.save(comment);

        comment.updateContent("Modified Content.");
        commentImpRepository.save(comment);

        // then
        Optional<CommentImp> findComm = commentImpRepository.findById(comment.getId());
        Assertions.assertTrue(findComm.isPresent());
        Assertions.assertEquals("Modified Content.", findComm.get().getContent());
    }

    @Test
    void 영화_코멘트_삭제() {
        // given
        Member user = createUser();
        memberRepository.save(user);

        BoardImp board = createBoard(user);
        boardImpRepository.save(board);

        // when
        CommentImp comment = createComment(user, board);

        commentImpRepository.save(comment);

        // then
        Assertions.assertFalse(commentImpRepository.findById(comment.getId()).isEmpty());

        commentImpRepository.delete(comment);
        Assertions.assertTrue(commentImpRepository.findById(comment.getId()).isEmpty());
    }

}
