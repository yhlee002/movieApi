package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardNoticeTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardNoticeRepositoryTest {

    @Autowired
    private BoardNoticeRepository boardNoticeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    Member createAdmin() {
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);
        return admin;
    }

    @Test
    void 모든_공지사항_게시글_조회() {
        // given
        Member admin = createAdmin();

        // 게시글 작성 전 존재하는 게시글 수
        Pageable pageable = PageRequest.of(0, 100, Sort.by("regDate").descending());
        Page<BoardNotice> page0 = boardNoticeRepository.findAll(pageable);

        List<BoardNotice> boardList = new ArrayList<>();
        BoardNotice board = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .title("abcdefg")
                .content("1234566")
                .build();
        boardList.add(board);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .title("poiuytre")
                .content("566789")
                .build();
        boardList.add(board2);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .title("mnbkjbcd")
                .content("1234")
                .build();
        boardList.add(board3);

        boardNoticeRepository.saveAll(boardList);

        // when
        Page<BoardNotice> page = boardNoticeRepository.findAll(pageable);

        // then
        Assertions.assertEquals(page0.getContent().size() + 3, page.getTotalElements());
        Assertions.assertEquals(page0.getContent().size() + 3, page.getContent().size());
    }

    @Test
    void 공지사항_게시글_작성() {
        // given
        BoardNotice board = BoardNoticeTestDataBuilder
                .board()
                .writer(createAdmin())
                .build();
        boardNoticeRepository.saveAndFlush(board);

        // when
        Assertions.assertNotNull(board.getId());
        Assertions.assertNotNull(board.getTitle());
        Assertions.assertNotNull(board.getContent());
        Assertions.assertNotNull(board.getWriter());
        Assertions.assertEquals(0, board.getViews());
        Assertions.assertNotNull(board.getRegDate());
        Assertions.assertNotNull(board.getModDate());
    }

    @Test
    void 공지사항_게시글_수정() {
        // given
        BoardNotice notice = BoardNoticeTestDataBuilder.board()
                .writer(createAdmin())
                .title("Original title")
                .content("Original content")
                .build();
        boardNoticeRepository.save(notice);

        // when
        BoardNotice modified = BoardNotice.builder()
                .id(notice.getId())
                .title("Modified title")
                .content("Modified content")
                .writer(notice.getWriter())
                .views(notice.getViews())
                .build();

        boardNoticeRepository.save(modified);

        // then
        Assertions.assertNotEquals("Original title", notice.getTitle());
        Assertions.assertEquals("Modified content", modified.getContent());
    }

    @Test
    void 공지사항_게시글_삭제() {
        // given
        BoardNotice board = BoardNoticeTestDataBuilder
                .board()
                .writer(createAdmin())
                .build();
        boardNoticeRepository.save(board);

        // when
        boolean exists = boardNoticeRepository.existsById(board.getId());
        boardNoticeRepository.delete(board);
        boolean exists2 = boardNoticeRepository.existsById(board.getId());

        // then
        Assertions.assertTrue(exists);
        Assertions.assertFalse(exists2);
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_단건_조회() {
        // given
        BoardNotice boardNotice = BoardNoticeTestDataBuilder
                .board()
                .writer(createAdmin())
                .build();
        boardNoticeRepository.save(boardNotice);

        // when
        BoardNotice foundBoard = boardNoticeRepository.findOneById(boardNotice.getId());

        // then
        Assertions.assertEquals(boardNotice.getId(), foundBoard.getId());
        org.assertj.core.api.Assertions.assertThat(boardNotice).isEqualTo(foundBoard);
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_이전글_조회() {
        // given
        Member admin = createAdmin();

        BoardNotice prevBoard = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(prevBoard);

        BoardNotice nextBoard = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(nextBoard);

        // when
        BoardNotice foundBoard = boardNoticeRepository.findPrevBoardNoticeById(nextBoard.getId());

        // then
        Assertions.assertEquals(prevBoard.getId(), foundBoard.getId());
        org.assertj.core.api.Assertions.assertThat(prevBoard).isEqualTo(foundBoard);
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_다음글_조회() {
        // given
        Member admin = createAdmin();

        BoardNotice prevBoard = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(prevBoard);

        BoardNotice nextBoard = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(nextBoard);

        // when
        BoardNotice foundBoard = boardNoticeRepository.findNextBoardNoticeById(prevBoard.getId());

        // then
        Assertions.assertEquals(nextBoard.getId(), foundBoard.getId());
        org.assertj.core.api.Assertions.assertThat(nextBoard).isEqualTo(foundBoard);
    }

    @Test
    void 최근_공지사항_게시글_조회_작성일자_내림차순() throws InterruptedException {
        // given
        Member admin = createAdmin();

        int seq = 0;
        while (seq < 10) {
            BoardNotice board = BoardNoticeTestDataBuilder.board()
                    .writer(admin)
                    .title("test-notice-" + seq)
                    .content("test-notice-" + seq + " content.")
                    .build();
            boardNoticeRepository.save(board);

            Thread.sleep(1000);
            seq++;
        }

        // when
        List<BoardNotice> boardNoticeList = boardNoticeRepository.findRecentBoardNoticesOrderByRegDate(5);
        List<BoardNotice> boardNoticeList2 = boardNoticeRepository.findRecentBoardNoticesOrderByRegDate(8);

        // then
        Assertions.assertAll(
                () -> {
                    for (BoardNotice boardNotice : boardNoticeList) {
                        int sequence = Integer.parseInt(boardNotice.getTitle().replace("test-notice-", ""));
                        Assertions.assertTrue(sequence > 4);
                    }
                },
                () -> {
                    Assertions.assertEquals(boardNoticeList.size(), 5);
                },
                () -> {
                    for (BoardNotice boardNotice : boardNoticeList2) {
                        int sequence = Integer.parseInt(boardNotice.getTitle().replace("test-notice-", ""));
                        Assertions.assertTrue(sequence > 1);
                    }
                },
                () -> {
                    Assertions.assertEquals(boardNoticeList2.size(), 8);
                }
        );
    }

    @Test
    void 공지사항_게시글의_제목_또는_내용으로_검색() {
        // given
        Member admin = createAdmin();

        List<BoardNotice> boardList = new ArrayList<>();
        BoardNotice board = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .title("abcdefg")
                .content("1234566")
                .build();
        boardList.add(board);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .title("poiuytre")
                .content("566789")
                .build();
        boardList.add(board2);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .title("mnbkjbcd")
                .content("234566")
                .build();
        boardList.add(board3);

        BoardNotice board4 = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .title("dfg45341234")
                .content("bcd")
                .build();
        boardList.add(board4);

        boardNoticeRepository.saveAll(boardList);

        // when
        String keyword = "bcd";
        // case 1
        Pageable pageable = PageRequest.of(0, 10);
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnoreCase("title", "content")
                .withIgnorePaths("views")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        BoardNotice boardParam = BoardNotice.builder().title(keyword).content(keyword).build();
        Example<BoardNotice> example = Example.of(boardParam, matcher);

        Page<BoardNotice> page = boardNoticeRepository.findAll(example, pageable);
        List<BoardNotice> list = page.getContent();

        // case 2
        keyword = "1234";

        boardParam = BoardNotice.builder().title(keyword).content(keyword).build();
        example = Example.of(boardParam, matcher);

        Page<BoardNotice> page2 = boardNoticeRepository.findAll(example, pageable);
        List<BoardNotice> list2 = page2.getContent();

        // case 3
        keyword = "566";

        boardParam = BoardNotice.builder().title(keyword).content(keyword).build();
        example = Example.of(boardParam, matcher);

        Page<BoardNotice> page3 = boardNoticeRepository.findAll(example, pageable);
        List<BoardNotice> list3 = page3.getContent();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, list.size()),
                () -> Assertions.assertEquals(2, list2.size()),
                () -> Assertions.assertEquals(3, list3.size())
        );
    }
}