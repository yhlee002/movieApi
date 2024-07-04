package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
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

public interface BoardImpRepository extends JpaRepository<BoardImp, Long> {

    /**
     * 후기 게시글 전체 조회
     */
    @NotNull
    @Query("select b from BoardImp b" +
            " join fetch b.writer m" +
            " where b.delYn != 'Y'")
    Page<BoardImp> findAll(Pageable pageable);

    @Query(value = "select b from BoardImp b" +
            " join fetch b.writer m" +
            " where b.id = :id")
    BoardImp findOneById(@Param("id") Long id);

    /**
     * 이전글
     *
     * @param id
     * @return
     */
    @Query("select b from BoardImp b" +
            " join fetch b.writer m" +
            " where b.id = " +
            "(select b2.id from BoardImp b2 where b2.id < :id  and b2.delYn != 'Y' order by b2.id desc limit 1)" +
            " and b.delYn != 'Y'")
    BoardImp findPrevBoardImpById(@Param("id") Long id);

    /**
     * 다음글
     *
     * @param id
     */
    @Query("select b from BoardImp b" +
            " join fetch b.writer m" +
            " where b.id = " +
            "(select b2.id from BoardImp b2 where b2.id > :id and b2.delYn != 'Y' order by b2.id limit 1)" +
            " and b.delYn != 'Y'")
    BoardImp findNextBoardImpById(Long id);

    /**
     * 인기 게시글 top {size} 조회
     */
    @Query("select b, m.name from BoardImp b" +
            " join fetch b.writer m" +
            " where b.delYn != 'Y'" +
            " order by b.views desc limit :size")
    List<BoardImp> findMostFavImpBoards(@Param("size") int size);

    @Query("select b from BoardImp b" +
            " join b.writer m" +
            " left join b.comments c" +
            " group by b.id" +
            " having b.delYn != 'Y'" +
            " order by count(c.id) desc")
    Page<BoardImp> findAllOrderByCommentsCountDesc(Pageable pageable);

    /**
     * 작성자명으로 검색 결과 조회
     */
    Page<BoardImp> findByWriterNameContainingIgnoreCaseOrderByRegDateDesc(String name, Pageable pageable);

    @Query("select b from BoardImp b" +
            " join fetch b.writer m" +
            " where m.name like %:name% and b.delYn != 'Y'")
    Page<BoardImp> findByWriterName(@Param("name") String name, Pageable pageable);

    /**
     * 제목 또는 내용으로 검색 결과 조회
     */

    @Query("select b from BoardImp b" +
            " join fetch b.writer m" +
            " where b.title like %:title% or b.content like %:content" +
            " and b.delYn != 'Y'")
    Page<BoardImp> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    /**
     * 특정 회원이 작성한 게시글 조회(최신순)
     * ex. 마이페이지 > 자신이 작성한 글 조회
     *
     * @param member
     */
    @Query("select b from BoardImp b" +
            " join fetch b.writer m" +
            " where m.memNo = :memNo" +
            " and b.delYn != 'Y'")
    Page<BoardImp> findAllByWriter(Member member, Pageable pageable);

    @Transactional
    @Modifying
    @Query("update BoardImp b set b.delYn = 'Y' where b.id = :id")
    int updateDelYnById(Long id);

    @Transactional
    @Modifying
    @Query("update BoardImp b set b.delYn = 'Y' where b.id in :ids")
    int updateDelYnByIds(List<Long> ids);

    @Transactional
    @Modifying
    @Query("delete from BoardImp b where b.id in :ids")
    void deleteByIds(List<Long> ids);
}
