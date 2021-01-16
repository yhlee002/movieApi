package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.board.BoardNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardImpRepository extends JpaRepository<BoardImp, Long> {

    // 모든 소감글 조회
    @Query(value = "select b from BoardImp b")
    List<BoardImp> findAllBoardImp();

    // board_id로 조회
    BoardImp findBoardImpById(Long boardId);

    // 이전글
    @Query(value = "select b.* from board_imp b join Member m on b.writer_no = m.mem_no where b.id = " +
            "(select b.id from board_imp b where b.id < ?1 order by b.id desc limit 1)"
            , nativeQuery = true)
    BoardImp findPrevBoardImpByBoardId(Long boardId);

    // 다음글
    @Query(value = "select b.* from board_imp b join Member m on b.writer_no = m.mem_no where b.id = " +
            "(select b.id from board_imp b where b.id > ?1 order by b.id asc limit 1)"
            , nativeQuery = true)
    BoardImp findNextBoardImpByBoardId(Long boardId);
    
    // 인기 게시글 top 5 조회
    @Query(value = "select b.*, m.name from board_imp b join member m on b.writer_no=m.mem_no order by b.views desc limit 5", nativeQuery = true)
    List<BoardImp> findTop5ByOrderByViewsDesc();

    // 작성자명 검색 결과 조회
    @Query(value = "select count(b) from BoardImp b where b.writer.name like %?1%")
    int findBoardNoticeSearchResultTotalCountWN(String writerName);

    @Query(value = "select b.* from BoardImp b join Member m on b.writer_no = m.mem_no where m.name = ?1 order by b.id desc limit ?2, ?3"
            , nativeQuery = true)
    List<BoardImp> findBoardImpListViewByWriterName(String writerName, int startRow, int boardCntPerPage);

    // 제목 또는 내용으로 검색 결과 조회
    @Query("select count(b) from BoardNotice b where b.title like %?1% or b.content like %?1%")
    int findBoardImpSearchResultTotalCountTC(String titleOrContent);

    @Query(value = "select b.* from board_notice b join Member m on b.writer_no = m.mem_no where b.title like %?1% or b.content like %?1% order by b.id desc limit ?2, ?3"
            , nativeQuery = true)
    List<BoardImp> findBoardImpListViewByTitleOrContent(String titleOrContent, int startRow, int boardCntPerPage);

    // 해당 작성자의 글 조회, 자신이 작성한 글 조회
    @Query(value = "select count(b) from BoardImp b where b.writer.memNo = ?1")
    int findBoardImpTotalCountByMemNo(Long memNo);

    @Query(value = "select b.* from board_imp b join Member m on b.writer_no = m.mem_no where m.mem_no = ?1 order by b.id desc limit ?2, ?3"
            , nativeQuery = true)
    List<BoardImp> findBoardImpListViewByWriterNo(Long memNo, int startRow, int boardCntPerPage);


    // '제목 또는 내용'으로 검색
    @Query("select b from BoardImp b where b.title like %?1% or b.content like %?1%")
    List<BoardImp> findAllBoardImpByTitleAndContent(String titleOrContent);

    /* 페이지네이션 */
    // 모든 게시글 조회
    @Query(value = "select count(b) from BoardImp b")
    Long findCount();

    @Query(value = "select b.* from board_imp b join Member m on b.writer_no = m.mem_no order by b.id desc limit ?1, ?2"
            , nativeQuery = true)
    List<BoardImp> findBoardImpListView(int startRow, int boardCntPerPage);


}
