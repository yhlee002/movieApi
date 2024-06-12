package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.comment.CommentMov;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.CommentMovTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.dto.comment.CommentMovPagenationParam;
import com.portfolio.demo.project.dto.comment.CommentMovParam;
import com.portfolio.demo.project.dto.member.MemberParam;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentMovServiceTest {

    @Autowired
    private CommentMovService commentMovService;

    @Autowired
    private MemberService memberService;

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

    CommentMovParam createComment(CommentMov comment, MemberParam member) {
        CommentMovParam comm = CommentMovParam.builder()
                .id(comment.getId())
                .movieNo(comment.getMovieNo())
                .content(comment.getContent())
                .rating(comment.getRating())
                .writerId(member.getMemNo())
                .writerName(member.getName())
                .regDate(comment.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
        Long id = commentMovService.saveComment(comm);

        return commentMovService.findById(id);
    }

    @Test
    void 리뷰_작성() {
        // given
        MemberParam user = createUser();

        // when
        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovParam comment = createComment(c, user);

        // then
        Assertions.assertNotNull(comment.getId());
    }

    @Test
    void 리뷰_수정() {
        // given
        MemberParam user = createUser();

        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovParam comment = createComment(c, user);

        // when
        comment.setRating(1);
        comment.setContent("Modified content.");
        commentMovService.updateComment(comment);

        CommentMovParam foundComment = commentMovService.findById(comment.getId());

        // then
        Assertions.assertEquals(1, foundComment.getRating());
        Assertions.assertEquals("Modified content.", foundComment.getContent());
    }

    @Test
    void id를_이용한_리뷰_삭제() {
        // given
        MemberParam user = createUser();

        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovParam comment = createComment(c, user);

        // when
        commentMovService.deleteCommentById(comment.getId());
        CommentMovParam foundComment = commentMovService.findById(comment.getId());

        // then
        Assertions.assertNull(foundComment);
    }

    @Test
    void 모든_리뷰_조회_최신순() {
        // given
        MemberParam user = createUser();
        MemberParam user2 = createRandomUser();

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
        MemberParam user = createUser();

        MemberParam user2 = createRandomUser();

        CommentMov c = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovParam comment = createComment(c, user);

        CommentMov c2 = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovParam comment2 = createComment(c2, user2);

        CommentMov c3 = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovParam comment3 = createComment(c3, user2);

        CommentMov c4 = CommentMovTestDataBuilder.noWayUpComment().build();
        CommentMovParam comment4 = createComment(c4, user2);

        // when
        CommentMovPagenationParam vo = commentMovService.getCommentsByMovie(comment.getMovieNo(), 0, 20);
        List<CommentMovParam> list = vo.getCommentMovsList();
        CommentMovPagenationParam vo2 = commentMovService.getCommentsByMovie(comment4.getMovieNo(), 0, 20);
        List<CommentMovParam> list2 = vo2.getCommentMovsList();
        // then
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(1, list2.size());
        list.forEach(comm -> Assertions.assertEquals(comment.getMovieNo(), comm.getMovieNo()));
        list2.forEach(comm -> Assertions.assertEquals(comment4.getMovieNo(), comm.getMovieNo()));
    }

    @Test
    void 특정_작성자의_리뷰_조회() {
        // given
        MemberParam user = createUser();
        MemberParam user2 = createRandomUser();

        CommentMov c1 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c1, user);

        CommentMov c2 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c2, user2);

        CommentMov c3 = CommentMovTestDataBuilder.noWayUpComment().build();
        createComment(c3, user2);

        // when
        List<CommentMovParam> list = commentMovService.getCommentsByMember(user.getMemNo(), 0, 20);
        List<CommentMovParam> list2 = commentMovService.getCommentsByMember(user2.getMemNo(), 0, 20);

        // then
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(2, list2.size());
    }
}