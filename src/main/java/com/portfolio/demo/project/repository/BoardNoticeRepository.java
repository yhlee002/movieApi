package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardNoticeRepository extends JpaRepository<BoardNotice, Long> {

    // 모든 notice 게시글 조회
    @Query(value = "select b from BoardNotice b")
    List<BoardNotice> findAllBoardNotice();

    // board_id로 조회
//    BoardNotice findByBoarId(Long boardId);

    // 해당 글의 이전글(해당 글보다 board_id가 낮은 글들을 내림차순으로 나열해 가장 첫번째 것)  join Member m on b.writer_no = m.mem_no
    @Query(value = "select b.* from board_notice b where b.id = " +
            "(select b.id from board_notice b where b.id < ?1 order by id desc limit 1)"
            , nativeQuery = true)
    BoardNotice findPrevBoardNoticeByBoardId(Long boardId); // 인자로 받는 boardId는 기준이 되는 글의 번호

    // 해당 글의 다음글(해당 글보다 board_id가 높은 글들을 올림차순으로 나열해 가장 첫번째 것) join Member m on b.writer_no = m.mem_no
    @Query(value = "select b.* from board_notice b where b.id = " +
            "(select b.id from board_notice b where b.id > ?1 order by id asc limit 1)"
            , nativeQuery = true)
    BoardNotice findNextBoardNoticeByBoardId(Long boardId);

    // 최신 게시글 top 5 조회
    List<BoardNotice> findTop5ByOrderByRegDateDesc();


    /* 페이지네이션 */
    // 전체 게시글 조회
    @Query("select count(b) from BoardNotice b")
    int findCount();

    @Query(value = "select b.* from board_notice b join Member m on b.writer_no = m.mem_no order by b.id desc limit ?1, ?2"
            , nativeQuery = true)
    List<BoardNotice> findBoardNoticeListView(int startRow, int boardCntPerPage);

    // 제목 또는 내용으로 검색 결과 조회
    @Query("select count(b) from BoardNotice b where b.title like %?1% or b.content like %?1%")
    int findBoardNoticeSearchResultTotalCountTC(String titleOrContent);

    @Query(value = "select b.* from board_notice b where b.title like %?1% or b.content like %?1% order by b.id desc limit ?2, ?3"
            , nativeQuery = true)
    List<BoardNotice> findBoardNoticeListViewByTitleOrContent(String titleOrContent, int startRow, int boardCntPerPage);

    // '제목 또는 내용'으로 검색(검색창만 두고 조건 선택 X)
    @Query("select b from BoardNotice b where b.title like %?1% or b.content like %?1%")
    List<BoardNotice> findByTitleOrContentContaining(String titleOrContent);
}
