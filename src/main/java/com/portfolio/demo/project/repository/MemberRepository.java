package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

// Mybatis의 DAO 역할(DB Layer 접근자)
@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{
   List<Member> findAll();
   Member findByMemNo(Long memNo);
   Member findByIdentifier(String identifier);
   Member findByName(String name);
   Member findByPhone(String phone);

   @Query("SELECT m FROM Member m WHERE m.identifier=?1 and m.provider=?2")
   Member findByIdentifierAndProvider(String identifier, String provider);
}
