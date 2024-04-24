package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.BoardNoticeTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardImpRepositoryTest {

    @Autowired
    private BoardImpRepository boardImpRepository;

    @Autowired
    private MemberRepository memberRepository;

    Member createUser() {
        Member user = MemberTestDataBuilder.user().build();
        memberRepository.save(user);
        return user;
    }

    @Test
    void 후기_게시글_식별번호를_이용한_단건_조회() {
        // given
        Member user = createUser();

        BoardImp board = BoardImpTestDataBuilder.board()
                .writer(user)
                .build();
        boardImpRepository.save(board);

        // when
        BoardImp foundBoard = boardImpRepository.findBoardImpById(board.getId());

        // then
        org.assertj.core.api.Assertions.assertThat(board).isEqualTo(foundBoard);
    }

    @Test
    void 모든_후기_게시글_조회() {
        // given
        Member user = createUser();

        List<BoardImp> boardList = new ArrayList<>();
        BoardImp board = BoardImpTestDataBuilder.board()
                .title("test-board-1")
                .content("test-content-1")
                .writer(user)
                .build();
        boardList.add(board);

        BoardImp board2 = BoardImpTestDataBuilder.board()
                .title("test-board-2")
                .content("test-content-2")
                .writer(user)
                .build();
        boardList.add(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board()
                .title("test-board-3")
                .content("test-content-3")
                .writer(user)
                .build();
        boardList.add(board3);

        boardImpRepository.saveAll(boardList);

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<BoardImp> page = boardImpRepository.findAll(pageable);

        // then
        Assertions.assertEquals(3, page.getContent().size());
        Assertions.assertIterableEquals(boardList, page.getContent());
    }

    @Test
    void 후기_게시글_작성() {
        // given
        BoardImp board = BoardImpTestDataBuilder.board()
                .writer(createUser())
                .build();
        boardImpRepository.save(board);

        // when
        Assertions.assertNotNull(board.getId());
        Assertions.assertNotNull(board.getTitle());
        Assertions.assertNotNull(board.getContent());
        Assertions.assertNotNull(board.getWriter());
        Assertions.assertEquals(0, board.getViews());
        Assertions.assertNotNull(board.getRegDate());
        Assertions.assertNotNull(board.getComments());
    }

    @Test
    void 후기_게시글_수정() {

    }

    @Test
    void findPrevBoardImpByBoardId() {

    }

    @Test
    void findNextBoardImpByBoardId() {
    }

    @Test
    void findTop5ByOrderByViewsDesc() {
    }

    @Test
    void findByWriterNameOrderByRegDateDesc() {
    }

    @Test
    void findTotalPagesByWriterNameOrderByRegDateDesc() {

    }

    @Test
    void findAllByTitleOrContentContainingOrderByRegDate() {
    }

    @Test
    void findAllByWriter() {
    }

    @Test
    void findRecentBoardsByMember() {
    }
}