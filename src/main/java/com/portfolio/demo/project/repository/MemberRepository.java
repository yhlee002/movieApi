package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByIdentifier(String identifier);

    Page<Member> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    Boolean existsByName(String name);

    List<Member> findByRoleIgnoreCase(String role);

    Member findByPhone(String phone);

    Boolean existsByPhone(String phone);

    Member findByIdentifierAndProvider(String identifier, String provider);
}
