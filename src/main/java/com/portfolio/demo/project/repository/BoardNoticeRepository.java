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

import java.util.List;

public interface BoardNoticeRepository extends JpaRepository<BoardNotice, Long> {

    /**
     * 공지사항 게시글 전체 조회
     */
    @NotNull
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where (b.delYn IS NULL or b.delYn <> 'Y')")
    Page<BoardNotice> findAll(Pageable pageable);

    /**
     * 특정 회원이 작성한 게시글 조회(최신순)
     * ex. 마이페이지 > 자신이 작성한 글 조회
     *
     * @param member
     */
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where b.delYn IS NULL OR b.delYn <> 'Y'")
    Page<BoardNotice> findAllByWriter(Member member, Pageable pageable);

    /**
     * 공지사항 게시글 단건 조회
     *
     * @param id
     * @return
     */
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where b.id = :id" +
            " and b.delYn IS NULL OR b.delYn <> 'Y'")
    BoardNotice findOneById(@Param("id") Long id);

    // 해당 글의 이전글(해당 글보다 board_id가 낮은 글들을 내림차순으로 나열해 가장 첫번째 것)
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where b.id = " +
            "(select b2.id from BoardNotice b2" +
            " where b2.id < :id and (b2.delYn IS NULL OR b2.delYn <> 'Y') order by b2.id desc limit 1)" +
            " and (b.delYn IS NULL OR b.delYn <> 'Y')")
    BoardNotice findPrevBoardNoticeById(Long id);

    // 해당 글의 다음글(해당 글보다 board_id가 높은 글들을 올림차순으로 나열해 가장 첫번째 것) join Member m on b.writer_no = m.mem_no
    @Query("select b from BoardNotice b" +
            " join fetch b.writer m" +
            " where b.id = " +
            "(select b2.id from BoardNotice b2" +
            " where b2.id > :id and (b2.delYn IS NULL OR b2.delYn <> 'Y') order by b2.id asc limit 1)" +
            " and (b.delYn IS NULL OR b.delYn <> 'Y')")
    BoardNotice findNextBoardNoticeById(Long id);

    /**
     * 최신 게시글 top {size} 조회
     */

    @Query("select b from BoardNotice b" +
            " join b.writer m" +
            " where (b.delYn IS NULL OR b.delYn <> 'Y')" +
            " order by b.regDate desc" +
            " limit :size")
    List<BoardNotice> findRecentBoardNoticesOrderByRegDate(@Param("size") int size);

    @Transactional
    @Modifying
    @Query("update BoardNotice b set b.delYn = 'Y' where b.id = :id")
    int updateDelYnById(Long id);

    @Transactional
    @Modifying
    @Query("update BoardNotice b set b.delYn = 'Y' where b.id in :ids")
    int updateDelYnByIds(List<Long> ids);

    @Transactional
    @Modifying
    @Query("delete from BoardNotice b where b.id in :ids")
    void deleteByIds(List<Long> ids);
}
