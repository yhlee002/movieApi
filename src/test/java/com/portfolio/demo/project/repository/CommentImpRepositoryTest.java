package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentMovTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
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
        return BoardImpTestDataBuilder.board(writer).build();
    }

    @Test
    void 특정_후기_게시글의_코멘트_조회() {
        // given
        Member member = createRandomMember();
        memberRepository.save(member);

        BoardImp board = createBoard(member);
        boardImpRepository.save(board);

        List<CommentImp> list = Arrays.asList(CommentImpTestDataBuilder.randomComment(member, board).build(), CommentImpTestDataBuilder.randomComment(member, board).build(), CommentImpTestDataBuilder.randomComment(member, board).build());
        commentImpRepository.saveAll(list);

        BoardImp board2 = createBoard(member);
        boardImpRepository.save(board2);

        commentImpRepository.save(CommentImpTestDataBuilder.randomComment(member, board2).build());

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<CommentImp> comments = commentImpRepository.findByBoard(board, pageable);
        Page<CommentImp> comments2 = commentImpRepository.findByBoard(board2, pageable);

        // then
        Assertions.assertEquals(3, comments.getTotalElements());
        comments.getContent().forEach(c -> Assertions.assertEquals(c.getBoard().getId(), board.getId()));

        Assertions.assertEquals(1, comments2.getTotalElements());
        comments2.getContent().forEach(c -> Assertions.assertEquals(c.getBoard().getId(), board2.getId()));
    }

    @Test
    void 작성자를_이용한_코멘트_조회() {
        // given
        Member member = createRandomMember();
        Member member2 = createRandomMember();
        memberRepository.saveAll(List.of(member, member2));

        BoardImp board = createBoard(member);
        boardImpRepository.save(board);

        commentImpRepository.saveAll(
                Arrays.asList(
                        CommentImpTestDataBuilder.randomComment(member, board).build(),
                        CommentImpTestDataBuilder.randomComment(member, board).build(),
                        CommentImpTestDataBuilder.randomComment(member, board).build(),
                        CommentImpTestDataBuilder.randomComment(member2, board).build() // the other member

                )
        );

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<CommentImp> comments = commentImpRepository.findAllByWriter(member, pageable);
        Page<CommentImp> comments2 = commentImpRepository.findAllByWriter(member2, pageable);

        // then
        Assertions.assertEquals(3, comments.getTotalElements());
        Assertions.assertEquals(1, comments2.getTotalElements());
    }

    @Test
    void 작성자를_이용한_코멘트_수_조회() {
        // given
        Member member = createRandomMember();
        Member member2 = createRandomMember();
        memberRepository.saveAll(List.of(member, member2));

        BoardImp board = createBoard(member);
        boardImpRepository.save(board);

        commentImpRepository.saveAll(Arrays.asList(
                CommentImpTestDataBuilder.randomComment(member, board).build(),
                CommentImpTestDataBuilder.randomComment(member, board).build(),
                CommentImpTestDataBuilder.randomComment(member, board).build(),
                CommentImpTestDataBuilder.randomComment(member2, board).build(), // the other member
                CommentImpTestDataBuilder.randomComment(member2, board).build() // the other member

        ));

        // when
        Integer cnt = commentImpRepository.countCommentImpsByWriter(member);
        Integer cnt2 = commentImpRepository.countCommentImpsByWriter(member2);

        // then
        Assertions.assertEquals(3, cnt);
        Assertions.assertEquals(2, cnt2);
    }

    @Test
    void 모든_코멘트_조회() {
        // given
        Member member = createRandomMember();
        Member member2 = createRandomMember();
        memberRepository.saveAll(Arrays.asList(member, member2));
        BoardImp board = createBoard(member);
        BoardImp board2 = createBoard(member2);
        boardImpRepository.saveAll(Arrays.asList(board, board2));
        commentImpRepository.saveAll(
                Arrays.asList(
                        CommentImpTestDataBuilder.randomComment(member, board).build(),
                        CommentImpTestDataBuilder.randomComment(member, board).build(),
                        CommentImpTestDataBuilder.randomComment(member, board).build(),
                        CommentImpTestDataBuilder.randomComment(member2, board).build(),
                        CommentImpTestDataBuilder.randomComment(member2, board2).build(),
                        CommentImpTestDataBuilder.randomComment(member, board2).build(),
                        CommentImpTestDataBuilder.randomComment(member2, board).build()
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
        Member member = createUser();
        memberRepository.save(member);

        BoardImp board = createBoard(member);
        boardImpRepository.save(board);

        // when
        CommentImp comment = CommentImpTestDataBuilder
                .randomComment(member, board)
                .build();
        commentImpRepository.save(comment);

        // then
        Assertions.assertNotNull(comment.getRegDate());
    }

    @Test
    void 영화_코멘트_수정() {
        // given
        Member member = createUser();
        memberRepository.save(member);

        BoardImp board = createBoard(member);
        boardImpRepository.save(board);

        // when
        CommentImp comment = CommentImpTestDataBuilder
                .randomComment(member, board)
                .build();
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
        Member member = createUser();
        memberRepository.save(member);

        BoardImp board = createBoard(member);
        boardImpRepository.save(board);

        // when
        CommentImp comment = CommentImpTestDataBuilder
                .randomComment(member, board)
                .build();

        commentImpRepository.save(comment);

        // then
        Assertions.assertFalse(commentImpRepository.findById(comment.getId()).isEmpty());

        commentImpRepository.delete(comment);
        Assertions.assertTrue(commentImpRepository.findById(comment.getId()).isEmpty());
    }

}
