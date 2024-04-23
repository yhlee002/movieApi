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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    /**
     * @Transactional의 동작을 인위적으로 제한한 메서드에 한해 트랜잭션 롤백이 일어나지 않기 때문
     */
    @BeforeEach
    public void setUp() {
        boardNoticeRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void 공지사항_게시글_작성() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

        BoardNotice boardNotice = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(boardNotice);

        // when
        Assertions.assertNotNull(boardNotice.getId());
        Assertions.assertNotNull(boardNotice.getTitle());
        Assertions.assertNotNull(boardNotice.getContent());
        Assertions.assertNotNull(boardNotice.getWriter());
        Assertions.assertEquals(0, boardNotice.getViews());
        Assertions.assertNotNull(boardNotice.getRegDate());
        Assertions.assertNotNull(boardNotice.getModDate());
    }

    @Test
    void 공지사항_게시글_수정1() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

        BoardNotice boardNotice = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(boardNotice);

        // when
        boardNotice.setTitle("Modified title");
        boardNotice.setContent("Modified content");

        boardNoticeRepository.save(boardNotice);
        BoardNotice foundNotice = boardNoticeRepository.findById(boardNotice.getId()).get();

        // then
        org.assertj.core.api.Assertions.assertThat(boardNotice).isEqualTo(foundNotice);
    }

    @Test
    void 공지사항_게시글_수정2_작성일자는_수정되지_않는다() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

        BoardNotice boardNotice = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(boardNotice);

        // when
        BoardNotice foundNotice = boardNoticeRepository.findById(boardNotice.getId()).get();
        foundNotice.setRegDate(LocalDateTime.now());
        boardNoticeRepository.save(foundNotice);

        // then
        Assertions.assertEquals(boardNotice.getRegDate(), foundNotice.getRegDate());
    }

    @Test
    void 공지사항_게시글_삭제() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

        BoardNotice boardNotice = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(boardNotice);

        // when
        BoardNotice foundNotice = boardNoticeRepository.findBoardNoticeById(boardNotice.getId());
        boardNoticeRepository.delete(foundNotice);
        BoardNotice foundNotice2 = boardNoticeRepository.findBoardNoticeById(boardNotice.getId());

        // then
        org.assertj.core.api.Assertions.assertThat(boardNotice).isEqualTo(foundNotice);
        Assertions.assertNull(foundNotice2);
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_단건_조회() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

        BoardNotice boardNotice = BoardNoticeTestDataBuilder.board()
                .writer(admin)
                .build();
        boardNoticeRepository.save(boardNotice);

        // when
        BoardNotice foundBoard = boardNoticeRepository.findBoardNoticeById(boardNotice.getId());

        // then
        Assertions.assertEquals(boardNotice.getId(), foundBoard.getId());
        org.assertj.core.api.Assertions.assertThat(boardNotice).isEqualTo(foundBoard);
    }

    @Test
    void 공지사항_게시글_식별번호를_이용한_이전글_조회() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

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
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 최근_공지사항_게시글_조회_작성일자_내림차순() throws InterruptedException {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

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
    void 모든_공지사항_게시글_조회() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

        List<BoardNotice> boardList = new ArrayList<>();
        BoardNotice board = BoardNoticeTestDataBuilder.board()
                .title("abcdefg")
                .content("1234566")
                .build();
        boardList.add(board);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board()
                .title("poiuytre")
                .content("566789")
                .build();
        boardList.add(board2);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board()
                .title("mnbkjbcd")
                .content("1234")
                .build();
        boardList.add(board3);

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("reg_date").descending());
        Page<BoardNotice> page = boardNoticeRepository.findAll(pageable);

        // then
    }

    @Test
    void 공지사항_게시글의_제목_또는_내용으로_검색() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        memberRepository.save(admin);

        List<BoardNotice> boardList = new ArrayList<>();
        BoardNotice board = BoardNoticeTestDataBuilder.board()
                .title("abcdefg")
                .content("1234566")
                .writer(admin)
                .build();
        boardList.add(board);

        BoardNotice board2 = BoardNoticeTestDataBuilder.board()
                .title("poiuytre")
                .content("566789")
                .writer(admin)
                .build();
        boardList.add(board2);

        BoardNotice board3 = BoardNoticeTestDataBuilder.board()
                .title("mnbkjbcd")
                .content("234566")
                .writer(admin)
                .build();
        boardList.add(board3);

        BoardNotice board4 = BoardNoticeTestDataBuilder.board()
                .title("dfg45341234")
                .content("bcd")
                .writer(admin)
                .build();
        boardList.add(board4);


        boardNoticeRepository.saveAll(boardList);

        // when
        String keyword = "bcd";
        // case 1
        Pageable pageable = PageRequest.of(0, 10);
        Page<BoardNotice> page = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        List<BoardNotice> list = page.getContent();

        // case 2
        keyword = "1234";
        Page<BoardNotice> page2 = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        List<BoardNotice> list2 = page2.getContent();

        // case 3
        keyword = "566";
        Page<BoardNotice> page3 = boardNoticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
        List<BoardNotice> list3 = page3.getContent();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, list.size()),
                () -> Assertions.assertEquals(2, list2.size()),
                () -> Assertions.assertEquals(3, list3.size())
        );
    }
}