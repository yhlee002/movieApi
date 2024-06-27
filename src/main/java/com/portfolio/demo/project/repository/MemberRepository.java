package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByIdentifier(String identifier);

    Member findByNameIgnoreCase(String name);

    Page<Member> findByIdentifierIgnoreCaseContaining(String identifier, Pageable pageable);

    Page<Member> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    Page<Member> findByPhoneIgnoreCaseContaining(String phone, Pageable pageable);

    Boolean existsByName(String name);

    Page<Member> findByRole(MemberRole role, Pageable pageable);

    Page<Member> findByProvider(SocialLoginProvider provider, Pageable pageable);

    Member findByPhone(String phone);

    Boolean existsByPhone(String phone);

    Member findByIdentifierAndProvider(String identifier, SocialLoginProvider provider);
}
