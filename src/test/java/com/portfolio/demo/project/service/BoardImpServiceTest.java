package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.repository.BoardImpRepository;
import com.portfolio.demo.project.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class BoardImpServiceTest {

    private BoardImpService boardImpService;

    private BoardImpRepository boardImpRepository;

    private MemberRepository memberRepository;

    @BeforeAll
    public void beforeAll() {}

    @BeforeEach
    public void beforeEach() {
        memberRepository = Mockito.mock(MemberRepository.class);

        boardImpRepository = Mockito.mock(BoardImpRepository.class);
        boardImpService = new BoardImpService(boardImpRepository);
    }

    @Test
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
    void updateBoard() {


//        BoardImp imp = BoardImp.builder()
//                .id(1L)
//                .title("First impression board.")
//                .content("Hello.")
//                .writer()
    }

    @Test
    void deleteBoardByBoardId() {
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
    void getBoardImpsByWriterName() {
    }

    @Test
    void getBoardImpsByTitleAndContent() {
    }

    @Test
    void getMyImpListView() {
    }
}