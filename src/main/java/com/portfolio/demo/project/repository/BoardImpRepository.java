package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardImpRepository extends JpaRepository<BoardImp, Long> {

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

    /**
     * @deprecated
     * @param writerName
     * @param startRow
     * @param boardCntPerPage
     * @return
     */
    @Query(value = "select b.* from BoardImp b join Member m on b.writer_no = m.mem_no where m.name = ?1 order by b.id desc limit ?2, ?3"
            , nativeQuery = true)
    List<BoardImp> findBoardImpListViewByWriterName(String writerName, int startRow, int boardCntPerPage);

    Page<BoardImp> findBoardImpsByWriter_Name(String writerName, Pageable pageable);

    // 제목 또는 내용으로 검색 결과 조회
    @Query("select count(b) from BoardImp b where b.title like %?1% or b.content like %?1%")
    int findBoardImpSearchResultTotalCountTC(String titleOrContent);

//    @Query(value = "select b.* from board_imp b join Member m on b.writer_no = m.mem_no where b.title like %?1% or b.content like %?1% order by b.id desc limit ?2, ?3"
//            , nativeQuery = true)
//    List<BoardImp> findBoardImpsByTitleOrContent(String titleOrContent, int startRow, int boardCntPerPage);
    Page<BoardImp> findBoardImpsByTitleOrContent(String keyword, Pageable pageable);

    // 자신이 작성한 글 조회(마이페이지)
    int countBoardImpsByWriter(Member member);

    Page<BoardImp> findBoardImpsByWriter(Member member, Pageable pageable);

    // 자신이 작성한 글 최신순 5개(마이페이지)
    List<BoardImp> findTop5ByWriter_MemNoOrderByRegDateDesc(Long memNo);

    // '제목 또는 내용'으로 검색
    @Query("select b from BoardImp b where b.title like %?1% or b.content like %?1%")
    List<BoardImp> findAllBoardImpByTitleAndContent(String titleOrContent);

    /**
     * @deprecated
     * 감상평 게시글 조회
     * @param startRow
     * @param boardCntPerPage
     */
    @Query(value = "select b.* from board_imp b join Member m on b.writer_no = m.mem_no order by b.id desc limit ?1, ?2"
            , nativeQuery = true)
    List<BoardImp> findBoardImpListView(int startRow, int boardCntPerPage);

    Page<BoardImp> findBoardImpsOrderById(Pageable pageable);
}
