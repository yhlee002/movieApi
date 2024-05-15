package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.CommentMovTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.CommentMovPagenationVO;
import com.portfolio.demo.project.vo.CommentMovVO;
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

    @Test
    void 리뷰_작성() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        // when
        CommentMov comment = CommentMovTestDataBuilder.noWayUpComment(user).build();
        commentMovService.saveComment(comment);

        // then
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    void 리뷰_수정() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        CommentMov comment = CommentMovTestDataBuilder.noWayUpComment(user).build();
        commentMovService.saveComment(comment);

        // when
        comment.updateRating(1);
        comment.updateContent("Modified content.");

        CommentMov foundComment = commentMovService.getCommentById(comment.getId());

        // then
        Assertions.assertEquals(1, foundComment.getRating());
        Assertions.assertEquals("Modified content.", foundComment.getContent());
    }

    @Test
    void id를_이용한_리뷰_삭제() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        CommentMov comment = CommentMovTestDataBuilder.noWayUpComment(user).build();
        commentMovService.saveComment(comment);

        // when
        commentMovService.deleteCommentById(comment.getId());
        CommentMov foundComment = commentMovService.getCommentById(comment.getId());

        // then
        Assertions.assertNull(foundComment);
    }

    @Test
    void 모든_리뷰_조회_최신순() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser().build();
        memberService.saveMember(user2);

        CommentMov comment = CommentMovTestDataBuilder.noWayUpComment(user).build();
        commentMovService.saveComment(comment);

        CommentMov comment2 = CommentMovTestDataBuilder.noWayUpComment(user2).build();
        commentMovService.saveComment(comment2);

        CommentMov comment3 = CommentMovTestDataBuilder.noWayUpComment(user2).build();
        commentMovService.saveComment(comment3);

        // when
        List<CommentMov> list = commentMovService.getComments(0);

        // then
        Assertions.assertEquals(3, list.size());
    }

    @Test
    void 특정_영화에_대한_리뷰_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser().build();
        memberService.saveMember(user2);

        CommentMov comment = CommentMovTestDataBuilder.noWayUpComment(user).build();
        commentMovService.saveComment(comment);

        CommentMov comment2 = CommentMovTestDataBuilder.noWayUpComment(user2).build();
        commentMovService.saveComment(comment2);

        CommentMov comment3 = CommentMovTestDataBuilder.noWayUpComment(user2).build();
        commentMovService.saveComment(comment3);

        CommentMov comment4 = CommentMovTestDataBuilder.duneComment(user2).build();
        commentMovService.saveComment(comment4);

        // when
        CommentMovPagenationVO vo = commentMovService.getCommentsByMovie(0, comment.getMovieNo());
        List<CommentMovVO> list = vo.getCommentMovsList();
        CommentMovPagenationVO vo2 = commentMovService.getCommentsByMovie(0, comment4.getMovieNo());
        List<CommentMovVO> list2 = vo2.getCommentMovsList();
                // then
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(1, list2.size());
        list.forEach(c -> Assertions.assertEquals(comment.getMovieNo(), c.getMovieNo()));
        list2.forEach(c -> Assertions.assertEquals(comment4.getMovieNo(), c.getMovieNo()));
    }

    @Test
    void 특정_작성자의_리뷰_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        Member user2 = MemberTestDataBuilder.randomIdentifierUser().build();
        memberService.saveMember(user2);

        CommentMov comment = CommentMovTestDataBuilder.noWayUpComment(user).build();
        commentMovService.saveComment(comment);

        CommentMov comment2 = CommentMovTestDataBuilder.noWayUpComment(user2).build();
        commentMovService.saveComment(comment2);

        CommentMov comment3 = CommentMovTestDataBuilder.noWayUpComment(user2).build();
        commentMovService.saveComment(comment3);

        // when
        List<CommentMov> list = commentMovService.getCommentsByMember(user, 0);
        List<CommentMov> list2 = commentMovService.getCommentsByMember(user2, 0);

        // then
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(2, list2.size());
    }
}