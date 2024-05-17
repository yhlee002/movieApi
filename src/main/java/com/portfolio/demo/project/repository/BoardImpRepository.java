package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.board.BoardImp;
import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardImpRepository extends JpaRepository<BoardImp, Long> {

    /**
     * 이전글
     *
     * @param id
     * @return
     */
    @Query(value = "select b.* from board_imp b join member m on b.writer_no = m.mem_no where b.id = " +
            "(select b.id from board_imp b where b.id < :id order by b.id desc limit 1)"
            , nativeQuery = true)
    BoardImp findPrevBoardImpById(Long id);

    /**
     * 다음글
     *
     * @param id
     */
    @Query(value = "select b.* from board_imp b join member m on b.writer_no = m.mem_no where b.id = " +
            "(select b.id from board_imp b where b.id > :id order by b.id limit 1)"
            , nativeQuery = true)
    BoardImp findNextBoardImpById(Long id);

    /**
     * 인기 게시글 top {size} 조회
     */
    @Query(value = "select b, m.name from BoardImp b join Member m on b.writer=m order by b.views desc limit :size")
    List<BoardImp> findMostFavImpBoards(@Param("size") int size);

    /**
     * 작성자명으로 검색 결과 조회
     */

//    @Query(value = "select b from BoardImp b join Member m on b.writer = m " +
//            "where m.name like %:name% order by b.id desc limit :size offset :offset")
//    List<BoardImp> findByWriterNameOrderByRegDateDesc(@Param("name") String name, @Param("offset") int offset, @Param("size") int size);
    Page<BoardImp> findByWriterNameContainingIgnoreCaseOrderByRegDateDesc(String name, Pageable pageable);

    @Query(value = "select b from BoardImp b join Member m on b.writer = m " +
            "where m.name like %:name%")
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
