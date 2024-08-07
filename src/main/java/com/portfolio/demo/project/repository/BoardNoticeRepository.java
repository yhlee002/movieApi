package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardNotice;
import com.portfolio.demo.project.entity.member.Member;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardNoticeRepository extends JpaRepository<BoardNotice, Long> {

    /**
     * 공지사항 게시글 전체 조회
     */
    @NotNull
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m")
    Page<BoardNotice> findAll(Pageable pageable);

    /**
     * 특정 회원이 작성한 게시글 조회(최신순)
     * ex. 마이페이지 > 자신이 작성한 글 조회
     *
     * @param member
     */
    Page<BoardNotice> findAllByWriter(Member member, Pageable pageable);

    /**
     * 공지사항 게시글 단건 조회
     *
     * @param id
     * @return
     */
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where b.id = :id")
    BoardNotice findOneById(@Param("id") Long id);

    // 해당 글의 이전글(해당 글보다 board_id가 낮은 글들을 내림차순으로 나열해 가장 첫번째 것)
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where b.id = " +
            "(select b2.id from BoardNotice b2 where b2.id < :id order by b2.id desc limit 1)"
    )
    BoardNotice findPrevBoardNoticeById(Long id);

    // 해당 글의 다음글(해당 글보다 board_id가 높은 글들을 올림차순으로 나열해 가장 첫번째 것) join Member m on b.writer_no = m.mem_no
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where b.id = " +
            "(select b2.id from BoardNotice b2 where b2.id > :id order by b2.id asc limit 1)"

    )
    BoardNotice findNextBoardNoticeById(Long id);

    /**
     * 최신 게시글 top {size} 조회
     */

    @Query("select b from BoardNotice b join Member m on b.writer = m order by b.regDate desc limit :size")
    List<BoardNotice> findRecentBoardNoticesOrderByRegDate(@Param("size") int size);

    /**
     * 제목 또는 내용으로 검색 결과 조회
     *
     * @param title
     * @param content
     */
    Page<BoardNotice> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from BoardNotice b where b.id in :ids")
    void deleteByIds(List<Long> ids);

    @Transactional
    @Modifying
    @Query("delete from BoardNotice b where b.writer.memNo = :writerNo")
    void deleteByWriterNo(Long writerNo);

    @Transactional
    @Modifying
    @Query("delete from BoardNotice b where b.writer.memNo in :writerNos")
    void deleteByWriterNos(List<Long> writerNos);


}
