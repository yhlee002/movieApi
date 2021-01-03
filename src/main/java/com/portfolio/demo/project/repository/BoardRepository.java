package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Override
    List<Board> findAll();

    List<Board> findAllByWriterNo(Long writerNo); // 해당 작성자의 글 조회, 자신이 작성한 글 조회

//    Board findByBoardId(Long BoardId); // 게시글 번호로 게시글 내용 가져오기(url에 PathVariable 사용) --> findById(Long id)존재

//    @Query("select b from Board b where b.title like %?1%")
//    List<Board> findAllByTitle(String title); // '제목'으로 검색
//
//    @Query("select b from Board b where b.content like %?1%")
//    List<Board> findAllByContent(String content); // '내용'으로 검색

    @Query("select b from Board b where b.title like %?1% or b.content like %?1%")
    List<Board> findAllByTitleAndContent(String titleOrContent); // '제목 또는 내용'으로 검색

    @Query("select b from Board b join Member m on b.writerNo = m.memNo where m.name = ?1")
    List<Board> findAllByWriterName(String name); // '작성자명'으로 검색
}
