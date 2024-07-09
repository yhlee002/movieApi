package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from Member m where m.memNo in :ids and (m.delYn IS NULL OR m.delYn <> 'Y')")
    List<Member> findByIds(List<Long> ids);

    @Query("select m from Member m where m.identifier = :identifier and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Member findByIdentifier(String identifier);

    @Query("select m from Member m where m.name = :name and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Member findByNameIgnoreCase(String name);

    @Query("select m from Member m" +
            " where lower(m.identifier) like lower('%:identifier%') and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Page<Member> findByIdentifierIgnoreCaseContaining(String identifier, Pageable pageable);

    @Query("select m from Member m" +
            " where lower(m.name) like lower('%:name%') and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Page<Member> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    @Query("select m from Member m where m.phone like %:phone% and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Page<Member> findByPhoneContaining(String phone, Pageable pageable);

    @Query("select m from Member m where m.role = :role and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Page<Member> findByRole(MemberRole role, Pageable pageable);

    @Query("select m from Member m where m.provider = :provider and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Page<Member> findByProvider(SocialLoginProvider provider, Pageable pageable);

    @Query("select m from Member m where m.phone = :phone and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Member findByPhone(String phone);

    @Query("select exists(select m.memNo from Member m" +
            " where m.phone = :phone and (m.delYn IS NULL OR m.delYn <> 'Y'))")
    Boolean existsByPhone(String phone);

    @Query("select m from Member m where m.identifier = :identifier and m.provider = :provider and (m.delYn IS NULL OR m.delYn <> 'Y')")
    Member findByIdentifierAndProvider(String identifier, SocialLoginProvider provider);

    @Transactional
    @Modifying(clearAutomatically=true)
    @Query("update Member m set m.role = :role where m.memNo in :memNos")
    int updateRoleByIds(@Param("memNos") List<Long> memNos, @Param("role") MemberRole role);

    @Transactional
    @Modifying(clearAutomatically=true)
    @Query("delete from  Member m where m.memNo in :memNos")
    void deleteByMemNos(@Param("memNos") List<Long> memNos);
}
