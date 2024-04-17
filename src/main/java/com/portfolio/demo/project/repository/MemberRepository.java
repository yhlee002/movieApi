package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>{

   Member findByIdentifier(String identifier);

   Member findByName(String name);

   Member findByPhone(String phone);

   Long countByPhone(String phone);

   Member findMemberByIdentifierAndProvider(String identifier, String provider);
}
