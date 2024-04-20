package com.portfolio.demo.project;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MovieSiteApplicationTest extends IntegrationTest {

    @Autowired private MemberRepository memberRepository;

    @Autowired private EntityManager entityManager;

    @Test
    void contextLoad() {
        Member admin = MemberTestDataBuilder.admin().build();
        Member savedMember = memberRepository.save(admin);

        entityManager.flush();
        entityManager.clear();

        Member member = memberRepository.findById(savedMember.getMemNo()).orElseThrow();

        Assertions.assertEquals(member.getMemNo(), admin.getMemNo());
        Assertions.assertEquals(member.getName(), admin.getName());
        Assertions.assertEquals(member.getIdentifier(), admin.getIdentifier());
        Assertions.assertEquals(member.getPhone(), admin.getPhone());
        Assertions.assertEquals(member.getRole(), admin.getRole());
//        Assertions.assertEquals(member.getRegDt(), admin.getRegDt());
    }

}
