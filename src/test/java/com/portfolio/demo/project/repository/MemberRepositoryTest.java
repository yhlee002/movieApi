package com.portfolio.demo.project.repository;

import com.portfolio.demo.project.dto.social.SocialLoginProvider;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
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
    public void 회원_생성() {
        // given
        Member member = MemberTestDataBuilder.user().build();

        // when
        memberRepository.save(member);

        // then
        Assertions.assertNotNull(member.getMemNo());
        Assertions.assertNotNull(member.getName());
        Assertions.assertNotNull(member.getPassword());
        Assertions.assertNotNull(member.getIdentifier());
        Assertions.assertNotNull(member.getProvider());
        Assertions.assertNotNull(member.getRegDate());
        Assertions.assertNotNull(member.getPhone());
        Assertions.assertEquals(MemberCertificated.N, member.getCertification());
    }

    @Test
    public void 회원_정보_수정() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);

        System.out.println("생성된 회원 이름 : " + member.getName());
        System.out.println("생성된 회원 패스워드 : " + member.getPassword());
        System.out.println("생성된 회원 휴대폰 번호 : " + member.getPhone());

        // when
        member.updateName("김영현");
        member.updatePassword("5678");
        member.updatePhone("010-1234-5678");
        memberRepository.save(member);
        entityManager.flush();
    }

    @Test
    public void 회원_탈퇴() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberRepository.save(member);

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
        Member foundMember = memberRepository.findByIdentifier(member.getIdentifier());

        // then
        Assertions.assertEquals(member.getIdentifier(), foundMember.getIdentifier());
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
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<Member> members2 = memberRepository.findByNameIgnoreCaseContaining(member.getName(), pageable);

        // then
        Assertions.assertEquals(3, members2.getContent().size());
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
        Pageable pageable = PageRequest.of(0, 10, Sort.by("regDate").descending());
        Page<Member> members1 = memberRepository.findByNameIgnoreCaseContaining(member.getName(), pageable);
        Pageable pageable2 = PageRequest.of(0, 2, Sort.by("regDate").descending());
        Page<Member> members2 = memberRepository.findByNameIgnoreCaseContaining(member.getName(), pageable2);
        Pageable pageable3 = PageRequest.of(1, 1, Sort.by("regDate").descending());
        Page<Member> members3 = memberRepository.findByNameIgnoreCaseContaining(member.getName(), pageable3);

        // then
        Assertions.assertEquals(3, members1.getContent().size());
        Assertions.assertEquals(2, members2.getContent().size());
        Assertions.assertEquals(1, members3.getContent().size());
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
        Member foundMember = memberRepository.findByPhone(member.getPhone());

        // then
        org.assertj.core.api.Assertions.assertThat(member).isEqualTo(foundMember);
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
                .provider(SocialLoginProvider.none).build();
        memberRepository.save(member);

        // when
        Member foundMember = memberRepository.findByIdentifierAndProvider(member.getIdentifier(), member.getProvider());
        Member foundMember2 = memberRepository.findByIdentifierAndProvider("238095572@socialuser.com", SocialLoginProvider.naver);

        // then
        Assertions.assertNotNull(foundMember);
        Assertions.assertNull(foundMember2);
        org.assertj.core.api.Assertions.assertThat(foundMember).isEqualTo(member);
    }

    @Test
    public void role으로_회원_조회() {
        // given
        Member admin = memberRepository.save(MemberTestDataBuilder.admin().build());
        Member m1 = memberRepository.save(MemberTestDataBuilder.randomIdentifierUser()
                .name("test-member1")
                .build());
        Member m2 = memberRepository.save(MemberTestDataBuilder.randomIdentifierUser()
                .name("test-member2")
                .build());

        memberRepository.save(admin);
        memberRepository.save(m1);
        memberRepository.save(m2);

        // when
        Pageable pageable = PageRequest.of(0, 20, Sort.by("regDate").descending());
        List<Member> admins = memberRepository.findByRole(MemberRole.ROLE_ADMIN, pageable).getContent();
        List<Member> members = memberRepository.findByRole(MemberRole.ROLE_USER, pageable).getContent();;

        // then
        Assertions.assertEquals(1, admins.size());
        Assertions.assertEquals(2, members.size());

    }


}
