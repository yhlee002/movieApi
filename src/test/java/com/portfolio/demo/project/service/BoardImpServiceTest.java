package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.BoardImpTestBuilder;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class BoardImpServiceTest {

    private BoardImpService boardImpService;

    private BoardImpRepository boardImpRepository;

//    private MemberRepository memberRepository;

    @BeforeEach
    public void beforeEach() {
//        memberRepository = Mockito.mock(MemberRepository.class);

        boardImpRepository = Mockito.mock(BoardImpRepository.class);
        boardImpService = new BoardImpService(boardImpRepository);
    }

    @Test
    @AfterEach
    @DisplayName("모든 공지 게시글 조회")
    void getAllBoards() {
        var list = boardImpService.getAllBoards();
        for(BoardImp imp : list) System.out.println(imp.toString());
    }

    @Test
    @DisplayName("게시글 아이디를 이용한 공지 게시글 조회")
    void findById() {
    }

    @Test
    void getMyImpTop5() {
    }

    @Test
    void getFavImpBoard() {
    }

    @Test
    @DisplayName("게시글 작성")
    void updateBoard() {}

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
    void getMyImpListView() {
    }
}
