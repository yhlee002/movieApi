package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Order(1)
    public void 회원_생성() {
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);
        entityManager.flush();

        System.out.println("저장된 회원 식별번호 : " + member.getMemNo());
    }

    @Test
    @Order(2)
    public void 회원_정보_수정() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);
        entityManager.flush();

        System.out.println("생성된 회원 이름 : " + member.getName());
        System.out.println("생성된 회원 패스워드 : " + member.getPassword());
        System.out.println("생성된 회원 휴대폰 번호 : " + member.getPhone());

        // when
        member.setName("김영현");
        member.setPassword("1234");
        member.setPhone("010-1234-5678");
        memberRepository.save(member);
        entityManager.flush();

        System.out.println("변경된 회원 이름 : " + member.getName());
        System.out.println("변경된 회원 패스워드 : " + member.getPassword());
        System.out.println("변경된 회원 휴대폰 번호 : " + member.getPhone());
    }

    @Test
    @Order(3)
    public void 회원_탈퇴() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);
        entityManager.flush();

        // when
        memberRepository.delete(member);
        entityManager.flush();

        // then
        Optional<Member> optionalMember = memberRepository.findById(member.getMemNo());
        Assertions.assertFalse(optionalMember.isPresent());
    }

    @Test
    public void identifier를_이용한_회원_조회() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByIdentifier(member.getIdentifier());

        // then
        Assertions.assertEquals(member.getIdentifier(), findMember.getIdentifier());
    }

    @Test
    public void name을_이용한_회원_조회() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);

        Member member2 = MemberTestDataBuilder.randomIdentifierUser()
                .name(member.getName() + "0")
                .phone("010-1234-5678")
                .build();
        memberRepository.save(member2);

        Member member3 = MemberTestDataBuilder.randomIdentifierUser()
                .name(member.getName() + "a")
                .phone("010-333-4444")
                .build();
        memberRepository.save(member3);

        Member member4 = MemberTestDataBuilder.randomIdentifierUser()
                .name(member.getName().substring(2))
                .phone("010-3395-5304")
                .build();
        memberRepository.save(member4);

        // when
        List<Member> members = memberRepository.findByNameIgnoreCaseContaining(member.getName());


        // then
        Assertions.assertEquals(3, members.size());
    }

    @Test
    public void name을_이용한_회원_조회_페이지네이션() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);

        Member member2 = MemberTestDataBuilder.randomIdentifierUser()
                .name(member.getName() + "0")
                .phone("010-1234-5678")
                .build();
        memberRepository.save(member2);

        Member member3 = MemberTestDataBuilder.randomIdentifierUser()
                .name(member.getName() + "a")
                .phone("010-333-4444")
                .build();
        memberRepository.save(member3);

        Member member4 = MemberTestDataBuilder.randomIdentifierUser()
                .name(member.getName().substring(2))
                .phone("010-3395-5304")
                .build();
        memberRepository.save(member4);

        // when
        List<Member> members1 = memberRepository.findByNameIgnoreCaseContaining(member.getName(), 0, 10);
        List<Member> members2 = memberRepository.findByNameIgnoreCaseContaining(member.getName(), 0, 2);
        List<Member> members3 = memberRepository.findByNameIgnoreCaseContaining(member.getName(), 2, 2);

        // then
        Assertions.assertEquals(3, members1.size());
        Assertions.assertEquals(2, members2.size());
        Assertions.assertEquals(1, members3.size());
    }

    @Test
    public void 회원_이름_중복_검사() {
        // given
        Member member = MemberTestDataBuilder.user()
                .name("test-member").build();

        // when
        memberRepository.save(member);

        // then
        Assertions.assertTrue(memberRepository.existsByName(member.getName()));
        Assertions.assertFalse(memberRepository.existsByName("test-member2"));
    }

    @Test
    public void 전화번호로_회원_조회() {
        // given
        Member member = MemberTestDataBuilder.user()
                .name("test-member")
                .phone("010-1234-5678").build();
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByPhone(member.getPhone());

        // then
        org.assertj.core.api.Assertions.assertThat(member).isEqualTo(findMember);
    }

    @Test
    public void 전화번호_중복_검사() {
        // given
        Member member = MemberTestDataBuilder.user()
                .name("test-member")
                .phone("010-1234-5678").build();
        memberRepository.save(member);

        // when
        Boolean exists = memberRepository.existsByPhone(member.getPhone());
        Boolean exists2 = memberRepository.existsByPhone("010-1212-3434");

        // then
        Assertions.assertTrue(exists);
        Assertions.assertFalse(exists2);
    }

    @Test
    public void identifier_와_provider로_회원_조회() {
        // given
        Member member = MemberTestDataBuilder.user()
                .name("test-member")
                .phone("010-1234-5678")
                .identifier("test-mail@gmail.com")
                .provider("none").build();
        memberRepository.save(member);

        // when
        Member findMember = memberRepository.findByIdentifierAndProvider(member.getIdentifier(), member.getProvider());
        Member findMember2 = memberRepository.findByIdentifierAndProvider("238095572", "naver");

        // then
        Assertions.assertNotNull(findMember);
        Assertions.assertNull(findMember2);
        org.assertj.core.api.Assertions.assertThat(findMember).isEqualTo(member);
    }


}
