package com.portfolio.demo.project.repository.comment.count;

import com.portfolio.demo.project.dto.comment.count.CommentCount;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.CommentImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentImpCountRepositoryTest {

    @Autowired
    private BoardImpRepository boardImpRepository;

    @Autowired
    private CommentImpRepository commentImpRepository;
    @Autowired
    private CommentImpCountRepository commentImpCountRepository;
    @Autowired
    private MemberRepository memberRepository;

    Member createRandomUser() {
        return MemberTestDataBuilder.randomIdentifierUser().build();
    }

    @Test
    void board_id_목록으로_comment_count_조회하기() {
        // given
        Member boardWriter = createRandomUser();
        memberRepository.save(boardWriter);
        Member commentWriter = createRandomUser();
        memberRepository.save(commentWriter);

        BoardImp b1 = BoardImpTestDataBuilder.board().writer(boardWriter).build();
        boardImpRepository.save(b1);

        BoardImp b2 = BoardImpTestDataBuilder.board().writer(boardWriter).build();
        boardImpRepository.save(b2);

        CommentImp c1 = CommentImpTestDataBuilder.randomComment().board(b1).writer(commentWriter).build();
        commentImpRepository.save(c1);

        CommentImp c2 = CommentImpTestDataBuilder.randomComment().board(b1).writer(commentWriter).build();
        commentImpRepository.save(c2);

        CommentImp c3 = CommentImpTestDataBuilder.randomComment().board(b1).writer(commentWriter).build();
        commentImpRepository.save(c3);

        CommentImp c4 = CommentImpTestDataBuilder.randomComment().board(b2).writer(commentWriter).build();
        commentImpRepository.save(c4);

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<BoardImp> boardImpPage = boardImpRepository.findAll(pageable); // sort 걸지 않고 조회(데이터가 적기 때문에)
        List<BoardImp> boardImps = boardImpPage.getContent();
        List<Long> boardIds = boardImps.stream().map(BoardImp::getId).collect(Collectors.toList());

        List<CommentCount> counts = commentImpCountRepository.findCommentCountsByBoardIds(boardIds);
        Map<Long, Long> commentsMap = counts.stream().collect(Collectors.toMap(CommentCount::getBoardId, CommentCount::getCount));

        // then
        Assertions.assertEquals(commentsMap.get(b1.getId()), 3);
        Assertions.assertEquals(commentsMap.get(b2.getId()), 1);
    }
}