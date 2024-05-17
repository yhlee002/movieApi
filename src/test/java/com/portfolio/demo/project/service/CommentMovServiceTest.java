package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.CommentMovTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.CommentMovPagenationVO;
import com.portfolio.demo.project.vo.CommentMovVO;
import com.portfolio.demo.project.vo.MemberVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentMovServiceTest {

    @Autowired
    private CommentMovService commentMovService;

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

    CommentMovVO createComment(CommentMov comment, MemberVO member) {
        CommentMovVO comm = CommentMovVO.create(comment);
        comm.setWriterId(member.getMemNo());
        return commentMovService.updateComment(comm);
    }

    @Test
    void 리뷰_작성() {
        // given
        MemberVO user = createUser();
        memberService.updateMember(user);

        // when
        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovVO comment = createComment(c, user);

        // then
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    void 리뷰_수정() {
        // given
        MemberVO user = createUser();

        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovVO comment = createComment(c, user);

        // when
        comment.setRating(1);
        comment.setContent("Modified content.");
        commentMovService.updateComment(comment);

        CommentMovVO foundComment = commentMovService.getCommentById(comment.getId());

        // then
        Assertions.assertEquals(1, foundComment.getRating());
        Assertions.assertEquals("Modified content.", foundComment.getContent());
    }

    @Test
    void id를_이용한_리뷰_삭제() {
        // given
        MemberVO user = createUser();

        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovVO comment = createComment(c, user);

        // when
        commentMovService.deleteCommentById(comment.getId());
        CommentMovVO foundComment = commentMovService.getCommentById(comment.getId());

        // then
        Assertions.assertNull(foundComment);
    }

    @Test
    void 모든_리뷰_조회_최신순() {
        // given
        MemberVO user = createUser();
        MemberVO user2 = createRandomUser();

        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c, user);

        CommentMov c2 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c2, user2);

        CommentMov c3 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c3, user2);

        // when
        List<CommentMov> list = commentMovService.getComments(0, 20);

        // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 특정_영화에_대한_리뷰_조회() {
        // given
        MemberVO user = createUser();

        MemberVO user2 = createRandomUser();

        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovVO comment = createComment(c, user);

        CommentMov c2 = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovVO comment2 = createComment(c2, user2);

        CommentMov c3 = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovVO comment3 = createComment(c3, user2);

        CommentMov c4 = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovVO comment4 = createComment(c4, user2);

        // when
        CommentMovPagenationVO vo = commentMovService.getCommentsByMovie(comment.getMovieNo(), 0, 20);
        List<CommentMovVO> list = vo.getCommentMovsList();
        CommentMovPagenationVO vo2 = commentMovService.getCommentsByMovie(comment4.getMovieNo(), 0, 20);
        List<CommentMovVO> list2 = vo2.getCommentMovsList();
        // then
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(1, list2.size());
        list.forEach(comm -> Assertions.assertEquals(comment.getMovieNo(), comm.getMovieNo()));
        list2.forEach(comm -> Assertions.assertEquals(comment4.getMovieNo(), comm.getMovieNo()));
    }

    @Test
    void 특정_작성자의_리뷰_조회() {
        // given
        MemberVO user = createUser();
        MemberVO user2 = createRandomUser();

        CommentMov c1 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c1, user);

        CommentMov c2 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c2, user2);

        CommentMov c3 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c3, user2);

        // when
        List<CommentMovVO> list = commentMovService.getCommentsByMember(user.getMemNo(), 0, 20);
        List<CommentMovVO> list2 = commentMovService.getCommentsByMember(user2.getMemNo(), 0, 20);

        // then
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(2, list2.size());
    }
}