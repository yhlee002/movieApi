package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByIdentifier(String identifier);

    @Query(value = "select m.* from Member m where m.name like %:name%", nativeQuery = true)
    List<Member> findByNameIgnoreCaseContaining(@Param("name") String name);

    @Query(value = "select m.* from Member m where m.name like %:name% limit :offset, :size", nativeQuery = true)
    List<Member> findByNameIgnoreCaseContaining(@Param("name") String name, @Param("offset") int offset, @Param("size") int size);

    Boolean existsByName(String name);

    Member findByPhone(String phone);

    Boolean existsByPhone(String phone);

    Member findTop5ByOrderByRegDtDesc();

    Member findByIdentifierAndProvider(String identifier, String provider);
}
