package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.BoardNoticeTestDataBuilder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardImpRepositoryTest {

    @Autowired
    private BoardImpRepository boardImpRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        boardImpRepository.deleteAll();
        boardImpRepository.flush();
        memberRepository.deleteAll();
        memberRepository.flush();
        entityManager.clear();
    }

    Member createUser() {
        Member user = MemberTestDataBuilder.user().build();
        memberRepository.save(user);
        return user;
    }

    Member createRandomUser() {
        Member user = MemberTestDataBuilder.randomIdentifierUser().build();
        memberRepository.save(user);
        return user;
    }

    @Test
    void 모든_후기_게시글_조회() {
        // given
        Member user = createUser();

        List<BoardImp> boardList = new ArrayList<>();
        BoardImp board = BoardImpTestDataBuilder.board(user)
                .title("test-board-1")
                .content("test-content-1")
                .build();
        boardList.add(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user)
                .title("test-board-2")
                .content("test-content-2")
                .build();
        boardList.add(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board(user)
                .title("test-board-3")
                .content("test-content-3")
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
        BoardImp board = BoardImpTestDataBuilder.board(createUser())
                .build();
        boardImpRepository.save(board);

        // when
        Assertions.assertNotNull(board.getId());
        Assertions.assertNotNull(board.getTitle());
        Assertions.assertNotNull(board.getContent());
        Assertions.assertNotNull(board.getWriter());
        Assertions.assertEquals(0, board.getViews());
        Assertions.assertNotNull(board.getRegDate());
        Assertions.assertEquals(0, board.getComments().size());
    }

    @Test
    void 후기_게시글_수정() {
        // given
        BoardImp imp = BoardImpTestDataBuilder.board(createUser())
                .title("Original title")
                .content("Original content")
                .build();
        boardImpRepository.save(imp);

        // when
        imp.updateTitle("Modified title");
        imp.updateContent("Modified content.");
        boardImpRepository.save(imp);

        // then
        Assertions.assertNotEquals("test-board-1", imp.getTitle());
        Assertions.assertNotEquals("Original content", imp.getContent());
    }

    @Test
    void 후기_게시글_삭제() {
        // given
        BoardImp imp = BoardImpTestDataBuilder.board(createUser())
                .build();
        boardImpRepository.save(imp);

        // when

        boolean exists = boardImpRepository.existsById(imp.getId());
        boardImpRepository.delete(imp);
        boolean exists2 = boardImpRepository.existsById(imp.getId());

        // then
        Assertions.assertTrue(exists);
        Assertions.assertFalse(exists2);
    }

    @Test
    void 후기_게시글_식별번호를_이용한_단건_조회() {
        // given
        BoardImp imp = BoardImpTestDataBuilder.board(createUser())
                .build();
        boardImpRepository.save(imp);

        // when
        BoardImp foundBoard = boardImpRepository.findBoardImpById(imp.getId());

        // then
        org.assertj.core.api.Assertions.assertThat(imp).isEqualTo(foundBoard);
    }

    @Test
    void 후기_게시글_식별번호를_이용한_이전글_조회() {
        // given
        Member user = createUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board(user)
                .build();
        boardImpRepository.save(prevBoard);

        BoardImp nextBoard = BoardImpTestDataBuilder.board(user)
                .build();
        boardImpRepository.save(nextBoard);

        // when
        BoardImp foundBoard = boardImpRepository.findPrevBoardImpById(nextBoard.getId());

        // then
        Assertions.assertEquals(prevBoard.getId(), foundBoard.getId());
        org.assertj.core.api.Assertions.assertThat(prevBoard).isEqualTo(foundBoard);

    }

    @Test
    void 후기_게시글_식별번호를_이용한_다음글_조회() {
        // given
        Member user = createUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board(user)
                .build();
        boardImpRepository.save(prevBoard);

        BoardImp nextBoard = BoardImpTestDataBuilder.board(user)
                .build();
        boardImpRepository.save(nextBoard);

        // when
        BoardImp foundBoard = boardImpRepository.findNextBoardImpById(prevBoard.getId());

        // then
        Assertions.assertEquals(nextBoard.getId(), foundBoard.getId());
        org.assertj.core.api.Assertions.assertThat(nextBoard).isEqualTo(foundBoard);
    }

    @Test
    void 인기_게시글_topN_조회() {
        Random random = new Random();
        // given
        int n = 0;
        while (n < 5) {
            BoardImp imp = BoardImpTestDataBuilder.board(createRandomUser())
                    .title("test-title-" + n)
                    .content("test-content" + n)
                    .build();
            boardImpRepository.saveAndFlush(imp);

            int m = random.nextInt(10);
            for (int i = 0; i < m; i++) {
                imp.updateViewCount();
            }
            n++;
        }
        boardImpRepository.flush();

        // when
        List<BoardImp> list = boardImpRepository.findMostFavImpBoards(3);
        List<BoardImp> list2 = boardImpRepository.findMostFavImpBoards(5);

        // then
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(5, list2.size());

        list.stream().map(BoardImp::getViews).forEach(System.out::println);
        System.out.println("-------------------");
        list2.stream().map(BoardImp::getViews).forEach(System.out::println);
    }

    @Test
    void 작성자로_조회한_최근_후기_게시글_조회_작성일자_내림차순() {
        // given
        Member user = createUser();
        Member user2 = createRandomUser();

        int n = 0;
        while (n < 5) {
            BoardImp imp = BoardImpTestDataBuilder.board(user)
                    .title("test-title-" + n)
                    .content("test content " + n)
                    .build();
            boardImpRepository.saveAndFlush(imp);
            n++;
        }

        boardImpRepository.save(BoardImpTestDataBuilder.board(user2)
                .title("test-title-5")
                .content("test content 5")
                .build());
        boardImpRepository.flush();

        // when
        Pageable pageable = PageRequest.of(0, 3, Sort.by("regDate").descending());
        Page<BoardImp> page = boardImpRepository.findAllByWriter(user, pageable);

        Pageable pageable2 = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<BoardImp> page2 = boardImpRepository.findAllByWriter(user, pageable2);

        //then
        Assertions.assertEquals(2, page.getTotalPages());
        Assertions.assertEquals(3, page.getContent().size());
        Assertions.assertEquals(5, page.getTotalElements());

        Assertions.assertEquals(1, page2.getTotalPages());
        Assertions.assertEquals(5, page2.getContent().size());

    }

    @Test
    void 작성자_이름으로_조회한_최근_후기_게시글_조회_작성일자_내림차순() {
        // given
        Member user = createUser();
        Member user2 = createRandomUser();

        int n = 0;
        while (n < 10) {
            boardImpRepository.saveAndFlush(BoardImpTestDataBuilder.board(user)
                    .title("test-board-" + n)
                    .build()
            );
            n++;
        }

        while (n < 14) {
            boardImpRepository.saveAndFlush(BoardImpTestDataBuilder.board(user2)
                    .title("test-board-" + n)
                    .build()
            );
            n++;
        }

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<BoardImp> page = boardImpRepository.findByWriterName(user.getName(), pageable);
        List<BoardImp> list = page.getContent();
        Page<BoardImp> page2 = boardImpRepository.findByWriterName(user2.getName(), pageable);
        List<BoardImp> list2 = page2.getContent();

        // then


        Assertions.assertAll(
                () -> Assertions.assertEquals(10, list.size()),
                () -> Assertions.assertEquals(4, list2.size()),
                () -> list.stream().map(b -> b.getWriter().getName()).forEach((username) -> {
                    Assertions.assertEquals(user.getName(), username);
                }),
                () -> list2.stream().map(b -> b.getWriter().getName()).forEach((username) -> {
                    Assertions.assertEquals(user2.getName(), username);
                })
        );

    }

    @Test
    void 후기_게시글의_제목_또는_내용으로_검색() {
        // given
        Member user = createUser();
        List<BoardImp> boardList = new ArrayList<>();
        BoardImp board = BoardImpTestDataBuilder.board(user)
                .title("efg")
                .content("example content")
                .build();
        boardList.add(board);

        BoardImp board2 = BoardImpTestDataBuilder.board(user)
                .title("bcde")
                .content("test content ef")
                .build();
        boardList.add(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board(user)
                .title("abcdefg")
                .content("234566")
                .build();
        boardList.add(board3);

        BoardImp board4 = BoardImpTestDataBuilder.board(user)
                .title("example")
                .content("bcd")
                .build();
        boardList.add(board4);

        boardImpRepository.saveAll(boardList);

        // when
        final String keyword = "exam";
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<BoardImp> page = boardImpRepository.findAllByTitleContainingOrContentContaining(keyword, keyword, pageable);
        List<BoardImp> list = page.getContent();

        final String keyword2 = "fg";
        Page<BoardImp> page2 = boardImpRepository.findAllByTitleContainingOrContentContaining(keyword2, keyword2, pageable);
        List<BoardImp> list2 = page2.getContent();

        // then
        Assertions.assertAll(
                () -> list.forEach(b -> {
                    Assertions.assertTrue(b.getTitle().contains(keyword) || b.getContent().contains(keyword));
                }),
                () -> list2.forEach(b -> {
                    Assertions.assertTrue(b.getTitle().contains(keyword2) || b.getContent().contains(keyword2));
                })
        );
    }
}