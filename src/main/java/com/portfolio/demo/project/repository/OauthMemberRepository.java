package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.member.OauthMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OauthMemberRepository extends JpaRepository<OauthMember, Long> {
    List<OauthMember> findAll();
    OauthMember findByNickname(String name);
    OauthMember findByPhone(String phone);
    OauthMember findByUniqueId(String uniqueId);
//    @Query("SELECT o.role FROM OauthMember o WHERE o.uniqueId=?1")
//    String findRoleByUniqueId(String uniqueId);
    OauthMember findByOauthmemNo(Long oauthMemNo);

    @Query("SELECT o FROM OauthMember o WHERE o.uniqueId=?1")
    OauthMember findOauthMemberByUniqueId(String uniqueId);


    @Query("SELECT o FROM OauthMember o WHERE o.provider=?1 and o.uniqueId=?2")
        // oauth_member
    OauthMember findByProviderAndUniqueId(String provider, String uniqueId);
}
