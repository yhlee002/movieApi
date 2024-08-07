package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.dto.board.BoardImpParam;
import com.portfolio.demo.project.dto.comment.CommentImpParam;
import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.comment.CommentImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.CommentImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.dto.comment.simple.CommentImpSimpleParam;
import com.portfolio.demo.project.repository.comment.simple.CommentImpSimpleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BoardImpRepositoryTest {

    @Autowired
    private BoardImpRepository boardImpRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentImpRepository commentImpRepository;

    @Autowired
    private CommentImpSimpleRepository commentImpSimpleRepository;

    @PersistenceContext
    private EntityManager entityManager;

    Member createRandomUser() {
        Member user = MemberTestDataBuilder.randomIdentifierUser().build();
        memberRepository.save(user);
        return user;
    }

    CommentImp createRandomCommentWithRandomUser(BoardImp board) {
        CommentImp comment = CommentImpTestDataBuilder.randomComment()
                .writer(createRandomUser()).board(board).build();
        commentImpRepository.save(comment);
        return comment;
    }

    @Test
    void 모든_후기_게시글_조회() {
        // given
        Member user = createRandomUser();

        // 게시글 작성 전 존재하는 게시글 수
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<BoardImp> page0 = boardImpRepository.findAll(pageable);

        List<BoardImp> boardList = new ArrayList<>();
        BoardImp board = BoardImpTestDataBuilder.board()
                .writer(user)
                .title("test-board-1")
                .content("test-content-1")
                .build();
        boardList.add(board);

        BoardImp board2 = BoardImpTestDataBuilder.board()
                .writer(user)
                .title("test-board-2")
                .content("test-content-2")
                .build();
        boardList.add(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board()
                .writer(user)
                .title("test-board-3")
                .content("test-content-3")
                .build();
        boardList.add(board3);

        boardImpRepository.saveAll(boardList);

        // when
        Page<BoardImp> page = boardImpRepository.findAll(pageable);

        // then
        Assertions.assertEquals(page0.getContent().size() + 3, page.getContent().size());
    }

    @Test
    void 모든_후기_게시글_조회_패치조인_컬렉션최적화() {
        // given
        Member user = createRandomUser();

        // 게시글 작성 전 존재하는 게시글 수
        Pageable pageable = PageRequest.of(0, 100, Sort.by("regDate").descending());
        Pageable pageable2 = PageRequest.of(0, 100, Sort.by("regDate").descending());

        Page<BoardImp> page0 = boardImpRepository.findAll(pageable);
        List<BoardImp> alreadyExistBoards = page0.getContent();

        BoardImp board = BoardImpTestDataBuilder.board().writer(user)
                .title("test-board-1").content("test-content-1").build();
        boardImpRepository.save(board);

        BoardImp board2 = BoardImpTestDataBuilder.board().writer(user)
                .title("test-board-2").content("test-content-2").build();
        boardImpRepository.save(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board().writer(user)
                .title("test-board-3").content("test-content-3").build();
        boardImpRepository.save(board3);

        createRandomCommentWithRandomUser(board);
        createRandomCommentWithRandomUser(board2);
        createRandomCommentWithRandomUser(board2);
        createRandomCommentWithRandomUser(board3);

        entityManager.flush();

        // when
        Page<BoardImp> page = boardImpRepository.findAll(pageable);
        List<BoardImp> boardList = page.getContent();
        List<Long> ids = boardList.stream().map(BoardImp::getId).collect(Collectors.toList());

        Page<CommentImpSimpleParam> commentPage = commentImpSimpleRepository.findAllParamsByBoardIds(ids, pageable2);
        List<CommentImpSimpleParam> simpleParams = commentPage.getContent();
        List<CommentImpParam> params = simpleParams.stream().map(p -> CommentImpParam.builder()
                .id(p.getId())
                .boardId(p.getBoardId())
                .writerName(p.getWriterName())
                .writerId(p.getWriterId())
                .content(p.getContent())
                .regDate(p.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build()
        ).collect(Collectors.toList());
        Map<Long, List<CommentImpParam>> commentMap = params.stream().collect(Collectors.groupingBy(CommentImpParam::getBoardId));

        List<BoardImpParam> boardParams = boardList.stream().map(BoardImpParam::create).collect(Collectors.toList());
        boardParams.forEach(b -> {
            b.setComments(commentMap.get(b.getId()));
        });

        BoardImpParam b1 = boardParams.stream().filter(b -> b.getId() == board.getId()).findFirst().orElse(null);
        BoardImpParam b2 = boardParams.stream().filter(b -> b.getId() == board2.getId()).findFirst().orElse(null);
        BoardImpParam b3 = boardParams.stream().filter(b -> b.getId() == board3.getId()).findFirst().orElse(null);

        // then
        Assertions.assertEquals(alreadyExistBoards.size() + 3, boardParams.size());
        Assertions.assertEquals(1, b1.getComments().size());
        Assertions.assertEquals(2, b2.getComments().size());
        Assertions.assertEquals(1, b3.getComments().size());
    }

    @Test
    void 후기_게시글_작성() {
        // given
        BoardImp board = BoardImpTestDataBuilder.board()
                .writer(createRandomUser())
                .build();
        boardImpRepository.save(board);

        // when
        Assertions.assertNotNull(board.getId());
        Assertions.assertNotNull(board.getTitle());
        Assertions.assertNotNull(board.getContent());
        Assertions.assertNotNull(board.getWriter());
        Assertions.assertEquals(0, board.getViews());
        Assertions.assertNotNull(board.getRegDate());
    }

    @Test
    void 후기_게시글_수정() {
        // given
        BoardImp imp = BoardImpTestDataBuilder.board()
                .writer(createRandomUser())
                .title("Original title")
                .content("Original content")
                .build();
        boardImpRepository.save(imp);

        // when
        BoardImp modified = BoardImp.builder()
                .id(imp.getId())
                .title("Modified title")
                .content("Modified content.")
                .writer(imp.getWriter())
                .views(imp.getViews())
                .recommended(imp.getRecommended())
                .build();

        boardImpRepository.save(modified);

        // then
        Assertions.assertNotEquals("test-board-1", imp.getTitle());
        Assertions.assertNotEquals("Original content", imp.getContent());
    }

    @Test
    void 후기_게시글_삭제() {
        // given
        BoardImp imp = BoardImpTestDataBuilder.board()
                .writer(createRandomUser())
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
        BoardImp imp = BoardImpTestDataBuilder.board()
                .writer(createRandomUser())
                .build();
        boardImpRepository.save(imp);

        // when
        BoardImp foundBoard = boardImpRepository.findOneById(imp.getId());

        // then
        org.assertj.core.api.Assertions.assertThat(imp).isEqualTo(foundBoard);
    }

    @Test
    void 후기_게시글_식별번호를_이용한_단건_조회_패치조인_컬렉션최적화() {
        // given
        BoardImp imp = BoardImpTestDataBuilder.board()
                .writer(createRandomUser())
                .build();
        boardImpRepository.save(imp);

        Member commentWriter1 = createRandomUser();
        Member commentWriter2 = createRandomUser();

        CommentImp comment1 = CommentImpTestDataBuilder.randomComment()
                .board(imp).writer(commentWriter1).build();
        commentImpRepository.save(comment1);

        CommentImp comment2 = CommentImpTestDataBuilder.randomComment()
                .board(imp).writer(commentWriter2).build();
        commentImpRepository.save(comment2);

        entityManager.flush();

        BoardImpParam boardParam = BoardImpParam.create(imp);

        Pageable pageable = PageRequest.of(0, 20, Sort.by("regDate").descending());
        Page<CommentImpSimpleParam> commentPage = commentImpSimpleRepository.findAllParamsByBoardId(imp.getId(), pageable);
        List<CommentImpSimpleParam> simpleParams = commentPage.getContent();
        List<CommentImpParam> comments = simpleParams.stream().map(p -> CommentImpParam.builder()
                .id(p.getId())
                .boardId(p.getBoardId())
                .writerName(p.getWriterName())
                .writerId(p.getWriterId())
                .content(p.getContent())
                .regDate(p.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build()
        ).collect(Collectors.toList());

        boardParam.setComments(comments);

        Assertions.assertEquals(2, boardParam.getComments().size());
    }

    @Test
    void 후기_게시글_식별번호를_이용한_이전글_조회() {
        // given
        Member user = createRandomUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board()
                .writer(user)
                .build();
        boardImpRepository.save(prevBoard);

        BoardImp nextBoard = BoardImpTestDataBuilder.board()
                .writer(user)
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
        Member user = createRandomUser();

        BoardImp prevBoard = BoardImpTestDataBuilder.board()
                .writer(user)
                .build();
        boardImpRepository.save(prevBoard);

        BoardImp nextBoard = BoardImpTestDataBuilder.board()
                .writer(user)
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
            BoardImp imp = BoardImpTestDataBuilder.board()
                    .writer(createRandomUser())
                    .title("test-title-" + n)
                    .content("test-content" + n)
                    .build();
            boardImpRepository.saveAndFlush(imp);

            int m = random.nextInt(10);
            imp.updateViewCount(m);

            n++;
        }
        boardImpRepository.flush();

        // when
        List<BoardImp> list = boardImpRepository.findMostFavImpBoards(3);
        List<BoardImp> list2 = boardImpRepository.findMostFavImpBoards(5);

        // then
        Assertions.assertEquals(3, list.size());
        Assertions.assertEquals(5, list2.size());
    }

    @Test
    void 작성자로_조회한_최근_후기_게시글_조회_작성일자_내림차순() {
        // given
        Member user = createRandomUser();
        Member user2 = createRandomUser();

        int n = 0;
        while (n < 5) {
            BoardImp imp = BoardImpTestDataBuilder.board()
                    .writer(user)
                    .title("test-title-" + n)
                    .content("test content " + n)
                    .build();
            boardImpRepository.saveAndFlush(imp);
            n++;
        }

        boardImpRepository.save(BoardImpTestDataBuilder.board()
                .writer(user2)
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
        Member user = createRandomUser();
        Member user2 = createRandomUser();

        int n = 0;
        while (n < 10) {
            boardImpRepository.saveAndFlush(BoardImpTestDataBuilder.board()
                    .writer(user)
                    .title("test-board-" + n)
                    .build()
            );
            n++;
        }

        while (n < 14) {
            boardImpRepository.saveAndFlush(BoardImpTestDataBuilder.board()
                    .writer(user2)
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
        Member user = createRandomUser();
        List<BoardImp> boardList = new ArrayList<>();
        BoardImp board = BoardImpTestDataBuilder.board()
                .writer(user)
                .title("efg")
                .content("sadfsdfhgjdsqwrqwe")
                .build();
        boardList.add(board);

        BoardImp board2 = BoardImpTestDataBuilder.board()
                .writer(user)
                .title("bcde")
                .content("oihk jhbmn pcv")
                .build();
        boardList.add(board2);

        BoardImp board3 = BoardImpTestDataBuilder.board()
                .writer(user)
                .title("abcdefg")
                .content("sdwjn lkmer lkopcv")
                .build();
        boardList.add(board3);

        BoardImp board4 = BoardImpTestDataBuilder.board()
                .writer(user)
                .title("werwepcv")
                .content("bcd")
                .build();
        boardList.add(board4);

        boardImpRepository.saveAll(boardList);

        // when
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnoreCase("title", "content")
                .withIgnorePaths("views", "recommended")
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        final String keyword = "efg";

        BoardImp impParam = BoardImp.builder().title(keyword).content(keyword).build();
        Example<BoardImp> example = Example.of(impParam, matcher);

        Page<BoardImp> page = boardImpRepository.findAll(example, pageable);
        List<BoardImp> list = page.getContent();

        final String keyword2 = "pcv";

        impParam = BoardImp.builder().title(keyword2).content(keyword2).build();
        example = Example.of(impParam, matcher);

        Page<BoardImp> page2 = boardImpRepository.findAll(example, pageable);
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

    @Test
    void comment_size로_sort하기() {
        // given
        Member boardWriter = createRandomUser();

        BoardImp b1 = BoardImpTestDataBuilder.board().writer(boardWriter).build();
        boardImpRepository.save(b1);

        BoardImp b2 = BoardImpTestDataBuilder.board().writer(boardWriter).build();
        boardImpRepository.save(b2);

        for (int i = 0; i < 10; i++) {
            createRandomCommentWithRandomUser(b1);
        }

        for (int i = 0; i < 8; i++) {
            createRandomCommentWithRandomUser(b2);
        }

        // when
        Pageable pageable = PageRequest.of(0, 10);
        Page<BoardImp> boardImpPage = boardImpRepository.findAllOrderByCommentsCountDesc(pageable);
        List<BoardImp> boardImps = boardImpPage.getContent();

        // then
        Assertions.assertEquals(boardImps.get(0).getId(), b1.getId());
        Assertions.assertEquals(boardImps.get(1).getId(), b2.getId());
    }
}