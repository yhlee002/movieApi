package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.CommentMovTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentMovRepositoryTest {

    @Autowired
    private CommentMovRepository commentMovRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    Member createUser() {
        return MemberTestDataBuilder.user().build();
    }

    Member createRandomMember() {
        return MemberTestDataBuilder.randomIdentifierUser().build();
    }

    @Test
    void 특정_영화_코멘트_조회() {
        // given & when
        Long movieId = 1096197L; // sample id(영화제목 : No Way Up)

        Member member = createRandomMember();
        memberRepository.save(member);

        CommentMov commentMov = CommentMovTestDataBuilder.noWayUpComment(member)
                .content("Nice movie!")
                .build();
        CommentMov commentMov2 = CommentMovTestDataBuilder.noWayUpComment(member)
                .content("Good!")
                .build();
        commentMovRepository.save(commentMov);
        commentMovRepository.save(commentMov2);

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findAllByMovieNo(movieId, pageable);

        // then
        Assertions.assertNotEquals(0, page.getContent().size());
        Assertions.assertEquals(2, page.getContent().size());
    }

    @Test
    void 작성자를_이용한_영화_코멘트_조회() {
        Long movieId = 1096197L; // sample id(영화제목 : No Way Up)

        Member user = createRandomMember();
        memberRepository.save(user);

        Member user2 = createRandomMember();
        memberRepository.save(user2);

        CommentMov commentMov = CommentMovTestDataBuilder.noWayUpComment(user)
                .content("Nice movie!")
                .rating(5)
                .build();
        commentMovRepository.save(commentMov);

        CommentMov commentMov2 = CommentMovTestDataBuilder.noWayUpComment(user2)
                .content("Good!")
                .rating(5)
                .build();
        commentMovRepository.save(commentMov2);

        CommentMov commentMov3 = CommentMovTestDataBuilder.noWayUpComment(user)
                .content("So so..")
                .rating(3)
                .build();
        commentMovRepository.save(commentMov3);

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<CommentMov> page = commentMovRepository.findByWriter(user, pageable);
        Page<CommentMov> page2 = commentMovRepository.findByWriter(user2, pageable);

        // then
        Assertions.assertEquals(2, page.getContent().size());
        page.getContent().forEach(comm -> Assertions.assertAll(
                () -> Assertions.assertEquals(comm.getWriter().getMemNo(), user.getMemNo()),
                () -> Assertions.assertEquals(comm.getWriter().getIdentifier(), user.getIdentifier()),
                () -> Assertions.assertEquals(comm.getWriter().getProvider(), user.getProvider())
        ));

        Assertions.assertEquals(1, page2.getContent().size());
        page2.getContent().forEach(comm -> Assertions.assertAll(
                () -> Assertions.assertEquals(comm.getWriter().getMemNo(), user2.getMemNo()),
                () -> Assertions.assertEquals(comm.getWriter().getIdentifier(), user2.getIdentifier()),
                () -> Assertions.assertEquals(comm.getWriter().getProvider(), user2.getProvider())
        ));
    }

    @Test
    void 영화_코멘트_작성() {
        // given
        Member member = createUser();
        memberRepository.save(member);

        // when
        CommentMov commentMov = CommentMovTestDataBuilder
                .noWayUpComment(member)
                .build();
        commentMovRepository.save(commentMov);

        // then
        Assertions.assertNotNull(commentMov.getRegDate());
    }

    @Test
    void 영화_코멘트_수정() {
        // given
        Member member = createUser();
        memberRepository.save(member);

        // when
        CommentMov commentMov = CommentMovTestDataBuilder
                .noWayUpComment(member)
                .build();
        commentMovRepository.save(commentMov);

        commentMov.updateContent("Modified Content.");
        commentMov.updateRating(2);
        commentMovRepository.save(commentMov);

        // then
        Optional<CommentMov> findComm = commentMovRepository.findById(commentMov.getId());
        Assertions.assertTrue(findComm.isPresent());
        Assertions.assertEquals("Modified Content.", findComm.get().getContent());
        Assertions.assertEquals(2, findComm.get().getRating());

    }

    @Test
    void 영화_코멘트_삭제() {
        // given
        Member member = createUser();
        memberRepository.save(member);

        // when
        CommentMov commentMov = CommentMovTestDataBuilder
                .noWayUpComment(member)
                .build();
        commentMovRepository.save(commentMov);

        // then
        Assertions.assertFalse(commentMovRepository.findById(commentMov.getId()).isEmpty());

        commentMovRepository.delete(commentMov);
        Assertions.assertTrue(commentMovRepository.findById(commentMov.getId()).isEmpty());
    }
}