package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestDataBuilder;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.util.TempKey;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

class BoardImpServiceTest {

    private BoardImpService boardImpService;

    private BoardImpRepository boardImpRepository;


    private MemberService memberService;

    private MemberRepository memberRepository;

    private PasswordEncoder passwordEncoder;

    private TempKey tempKey;


    @BeforeAll
    public static void beforeAll() {

    }

    @BeforeEach
    public void beforeEach() {
        boardImpRepository = Mockito.mock(BoardImpRepository.class);
        memberRepository = Mockito.mock(MemberRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        tempKey = Mockito.mock(TempKey.class);
        boardImpService = new BoardImpService(boardImpRepository, memberRepository);
        memberService = new MemberService(memberRepository, passwordEncoder, tempKey);

    }

    @Test
    @Order(2)
    @AfterEach
    @DisplayName("모든 공지 게시글 조회")
    void getAllBoards() {
        var list = boardImpService.getAllBoards();
        for (BoardImp imp : list) System.out.println(imp.toString());
    }

    @Test
    @DisplayName("게시글 아이디를 이용한 공지 게시글 조회")
    void findById() {
    }

    @Test
    void getMyImpTop5() {
    }

    @Test
    void getMostFavImpBoard() {
    }

    @Test
    @DisplayName("게시글 작성(저장되지 않은 작성자를 참조하는 경우)")
    void updateBoard() {
        // given
        MemberService memberServiceMock = Mockito.spy(memberService);
        Mockito.doReturn(
                MemberTestDataBuilder.admin()
                        .memNo(100L)
                        .build()
        ).when(memberServiceMock).findByMemNo(100L);

        Member member = memberService.findByMemNo(100L);
        BoardImp imp = BoardImpTestDataBuilder.board().writer(member).build();

        // when & then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            boardImpService.updateBoard(imp);
        });
    }

    @Test
    @DisplayName("게시글 작성(저장된 작성자를 참조하는 경우)")
    @Transactional
    void updateBoardWithExistMember() {
        // given
        Member member = memberService.saveMember(MemberTestDataBuilder.user().build());

        BoardImp imp = BoardImpTestDataBuilder.board().writer(member).build();

        // when & then
        boardImpService.updateBoard(imp); // BoardImp savedImp =

//
//        Assertions.assertEquals(savedImp.getTitle(), imp.getTitle());
//        Assertions.assertEquals(savedImp.getWriter(), imp.getWriter());
//        Assertions.assertEquals(savedImp.getContent(), imp.getContent());
    }

    @Test
    void deleteById() {
    }

    @Test
    void upViewCnt() {
    }

    @Test
    void deleteBoards() {
    }

    @Test
    void getImps() {
    }

    @Test
    void getBoardImpsByWriter() {
    }

    @Test
    void getBoardImpsByTitleAndContent() {
    }

    @Test
    void getImpsByMember() {
    }
}
