package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardNoticeRepository extends JpaRepository<BoardNotice, Long> {

    /**
     * 공지사항 게시글 단건 조회
     * @param boardId
     * @return
     */
    BoardNotice findBoardNoticeByBoardId(Long boardId);

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


    /**
     * @deprecated
     * 페이지네이션
     */
    @Query(value = "select b.* from board_notice b join Member m on b.writer_no = m.mem_no order by b.id desc limit ?1, ?2"
            , nativeQuery = true)
    List<BoardNotice> findBoardNoticeListView(int startRow, int boardCntPerPage);

    Page<BoardNotice> findBoardNotices(Pageable pageable);

    /**
     * 제목 또는 내용으로 검색 결과 조회
     * @param keyword
     * @return
     */
    Page<BoardNotice> findByTitleOrContentContaining(String keyword, Pageable pageable);
}
