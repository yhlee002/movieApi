package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardImpRepository extends JpaRepository<BoardImp, Long> {

    /**
     * 후기 게시글 전체 조회
     */
    @NotNull
    @Query("select b from BoardImp b" +
            " join fetch b.writer m")
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
            "(select b2.id from BoardImp b2 where b2.id < :id order by b2.id desc limit 1)")
    BoardImp findPrevBoardImpById(@Param("id") Long id);

    /**
     * 다음글
     *
     * @param id
     */
    @Query("select b from BoardImp b" +
            " join fetch b.writer m" +
            " where b.id = " +
            "(select b2.id from BoardImp b2 where b2.id > :id order by b2.id limit 1)")
    BoardImp findNextBoardImpById(Long id);

    /**
     * 인기 게시글 top {size} 조회
     */
    @Query("select b, m.name from BoardImp b" +
            " join fetch b.writer m" +
            " order by b.views desc limit :size")
    List<BoardImp> findMostFavImpBoards(@Param("size") int size);

    @Query(value = "select b from BoardImp b" +
            " join b.writer m" +
            " left join b.comments c" +
            " group by b.id" +
            " order by count(c.id) desc")
    Page<BoardImp> findAllOrderByCommentsCountDesc(Pageable pageable);

    /**
     * 작성자명으로 검색 결과 조회
     */
//    @Query(value = "select b from BoardImp b join Member m on b.writer = m " +
//            "where m.name like %:name% order by b.id desc limit :size offset :offset")
//    List<BoardImp> findByWriterNameOrderByRegDateDesc(@Param("name") String name, @Param("offset") int offset, @Param("size") int size);
    Page<BoardImp> findByWriterNameContainingIgnoreCaseOrderByRegDateDesc(String name, Pageable pageable);

    @Query(value = "select b from BoardImp b" +
            " join fetch b.writer m" +
            " where m.name like %:name%")
    Page<BoardImp> findByWriterName(@Param("name") String name, Pageable pageable);

//    @Query(value = "select count(b) from BoardImp b join Member m on b.writer = m " +
//            "where m.name like %:name% order by b.id desc limit :size offset :offset")
//    Integer findTotalPagesByWriterNameOrderByRegDateDesc(@Param("name") String name, @Param("offset") int offset, @Param("size") int size);

    /**
     * 제목 또는 내용으로 검색 결과 조회
     */
    Page<BoardImp> findAllByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    /**
     * 특정 회원이 작성한 게시글 조회(최신순)
     * ex. 마이페이지 > 자신이 작성한 글 조회
     *
     * @param member
     * @return
     */
    Page<BoardImp> findAllByWriter(Member member, Pageable pageable);
}
