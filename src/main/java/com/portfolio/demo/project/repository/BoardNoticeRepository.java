package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardNoticeRepository extends JpaRepository<BoardNotice, Long> {

    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.mod_dt, b.views " +
            "from board_notice b join Member m on b.writer_no = m.mem_no"
            , nativeQuery = true)
    List<BoardNotice> findAll();

    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.mod_dt, b.views " +
            "from board_notice b join Member m on b.writer_no = m.mem_no where b.board_id=?1"
            , nativeQuery = true)
    BoardNotice findBoardByBoarId(Long boardId);

    // 해당 글의 이전글(해당 글보다 board_id가 낮은 글들을 내림차순으로 나열해 가장 첫번째 것)
    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.mod_dt, b.views " +
            "from board_notice b join Member m on b.writer_no = m.mem_no where b.board_id = " +
            "(select b.board_id from board_notice b where b.board_id < ?1 order by board_id desc limit 1)"
            , nativeQuery = true)
    BoardNotice findPrevBoardByBoardId(Long boardId); // 인자로 받는 boardId는 기준이 되는 글의 번호

    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.mod_dt, b.views " +
            "from board_notice b join Member m on b.writer_no = m.mem_no where b.board_id = " +
            "(select b.board_id from board_notice b where b.board_id > ?1 order by board_id asc limit 1)"
            , nativeQuery = true)
    BoardNotice findNextBoardByBoardId(Long boardId);

    List<BoardNotice> findAllByWriterNo(Long writerNo); // 해당 작성자의 글 조회, 자신이 작성한 글 조회

//    Board findByBoardId(Long BoardId); // 게시글 번호로 게시글 내용 가져오기(url에 PathVariable 사용) --> findById(Long id)존재

//    @Query("select b from Board b where b.title like %?1%")
//    List<Board> findAllByTitle(String title); // '제목'으로 검색
//
//    @Query("select b from Board b where b.content like %?1%")
//    List<Board> findAllByContent(String content); // '내용'으로 검색

    @Query("select b from BoardNotice b where b.title like %?1% or b.content like %?1%")
    List<BoardNotice> findAllByTitleAndContent(String titleOrContent); // '제목 또는 내용'으로 검색

    @Query("select b from BoardNotice b join Member m on b.writerNo = m.memNo where m.name = ?1")
    List<BoardNotice> findAllByWriterName(String name); // '작성자명'으로 검색

    /* 페이지네이션 */
    @Query("select count(b) from BoardNotice b")
    int findTotalCount();

    @Query(value = "select b.board_id, b.title, b.content, b.writer_no, m.name as writer, b.reg_dt, b.mod_dt, b.views " +
                        "from board_notice b join Member m on b.writer_no = m.mem_no order by b.board_id desc limit ?1, ?2"
            , nativeQuery = true)
    List<BoardNotice> findBoardNoticeListView(int startRow, int boardCntPerPage);
}
