package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardImpRepository extends JpaRepository<BoardImp, Long> {

    // 모든 소감글 조회
    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.views " +
            "from board_imp b join Member m on b.writer_no = m.mem_no"
            , nativeQuery = true)
    List<BoardImp> findAllBoardImp();

    // board_id로 조회
    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.views " +
            "from board_imp b join Member m on b.writer_no = m.mem_no where b.board_id=?1"
            , nativeQuery = true)
    BoardImp findBoardImpByBoarId(Long boardId);

    // 이전글
    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.views " +
            "from board_notice b join Member m on b.writer_no = m.mem_no where b.board_id = " +
            "(select b.board_id from board_imp b where b.board_id < ?1 order by board_id desc limit 1)"
            , nativeQuery = true)
    BoardImp findPrevBoardImpByBoardId(Long boardId);

    // 다음글
    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.views " +
            "from board_imp b join Member m on b.writer_no = m.mem_no where b.board_id = " +
            "(select b.board_id from board_imp b where b.board_id > ?1 order by board_id asc limit 1)"
            , nativeQuery = true)
    BoardImp findNextBoardImpByBoardId(Long boardId);

    // 해당 작성자의 글 조회, 자신이 작성한 글 조회
    List<BoardImp> findAllBoardImpByWriterNo(Long writerNo);

    // '제목 또는 내용'으로 검색
    @Query("select b from BoardImp b where b.title like %?1% or b.content like %?1%")
    List<BoardImp> findAllBoardImpByTitleAndContent(String titleOrContent);

    // '작성자명'으로 검색
    @Query("select b from BoardImp b join Member m on b.writerNo = m.memNo where m.name like %?1%")
    List<BoardImp> findAllBoardImpByWriterName(String name);


    /* 페이지네이션 */
    @Query("select count(b) from BoardImp b")
    int findBoardImpTotalCount();

    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.views " +
            "from board_imp b join Member m on b.writer_no = m.mem_no order by b.board_id desc limit ?1, ?2"
            , nativeQuery = true)
    List<BoardImp> findBoardImpListView(int startRow, int boardCntPerPage);
}
