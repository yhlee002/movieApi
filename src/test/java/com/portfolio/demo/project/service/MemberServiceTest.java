package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


//@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Test
    void 회원_식별번호를_이용한_단건_조회() {
        // given
        Member member = MemberTestDataBuilder.admin().build();
        memberService.saveMember(member);

        // when
        Member foundUser = memberService.findByMemNo(member.getMemNo());

        // then
        Assertions.assertEquals(member.getMemNo(), foundUser.getMemNo());
    }

    @Test
    void unique_key인_identifier를_이용한_단건_조회() {
        // given
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(user);

        // when
        Member foundUser = memberService.findByIdentifier(user.getIdentifier());

        // then
        Assertions.assertEquals(user.getMemNo(), foundUser.getMemNo());
    }

    @Test
    void name을_이용한_조회() {
        // given
        for (int i = 0; i < 5; i++) {
            memberService.saveMember(
                    MemberTestDataBuilder
                    .randomIdentifierUser()
                    .name("abc" + i)
                    .build()
            );
        }
        memberService.saveMember(
                MemberTestDataBuilder
                        .randomIdentifierUser()
                        .name("efg")
                        .build()
        );

        // when
        List<Member> foundMembers = memberService.findAllByNameContaining("abc", 0, 10);
        List<Member> foundMembers2 = memberService.findAllByNameContaining("e", 0, 10);

        // then
        Assertions.assertEquals(5, foundMembers.size());
        Assertions.assertEquals(1, foundMembers2.size());
    }

    @Test
    void 휴대폰_번호로_회원정보_조회() {
        // given
        String phone = "010-1111-2222";
        Member member = MemberTestDataBuilder.user().phone(phone).build();
        memberService.saveMember(member);

        // when
        Member foundMember = memberService.findByPhone(phone);

        // then
        Assertions.assertNotNull(foundMember);
        Assertions.assertEquals(member.getMemNo(), foundMember.getMemNo());
    }

    @Test
    void 휴대폰_번호로_회원_존재여부_확인() {
        // given
        String phone = "010-1111-2222";
        Member member = MemberTestDataBuilder.user().phone(phone).build();
        memberService.saveMember(member);

        // when
        Boolean exists = memberService.existsByPhone(phone);

        // then
        Assertions.assertTrue(exists);
    }

    @Test
    void identifier와_provider를_이용한_회원정보_조회() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberService.saveMember(member);

        // when
        Member foundMember = memberService.findByIdentifierAndProvider(member.getIdentifier(), member.getProvider());
        Member foundMember2 = memberService.findByIdentifierAndProvider("test@example.com", member.getProvider());

        // then
        Assertions.assertNotNull(foundMember);
        Assertions.assertNull(foundMember2);
    }

    @Test
    void 회원가입() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        Member user = MemberTestDataBuilder.user().build();

        // when
        memberService.saveMember(admin);
        memberService.saveMember(user);

        // then
        Assertions.assertNotNull(admin.getMemNo());
        Assertions.assertEquals("ROLE_ADMIN", admin.getRole());
        Assertions.assertNotNull(user.getMemNo());
        Assertions.assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void 회원가입_패스워드_미기입시_오류_발생() {
        // when
        Member member = MemberTestDataBuilder.user().password(null).build();

        // then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            memberService.saveMember(member);
        });
    }

    @Test
    void 소셜_로그인을_이용한_회원가입() {
        // given
        Member user = MemberTestDataBuilder.naverUser().build();

        // when
        memberService.saveMember(user);

        // then
        Assertions.assertNotNull(user.getMemNo());
    }

    @Test
    void 비밀번호_변경() {
        // given
        Member member = MemberTestDataBuilder.user().password("1234").build();
        memberService.saveMember(member);
        member.updatePassword("5678");
        memberService.saveMember(member);
        Member foundMember = memberService.findByMemNo(member.getMemNo());

        Assertions.assertNotEquals("1234", member.getPassword());
        Assertions.assertEquals("5678", foundMember.getPassword());
    }

    @Test
    void 인증키_수정() {
        // given
        Member member = MemberTestDataBuilder.user().build();
        memberService.saveMember(member);
        // 현재 member.certKey == null

        // when
        memberService.updateCertKey(member.getMemNo());

        // then
        Assertions.assertNotNull(member.getCertKey());
    }

    @Test
    void 회원_정보를_기반으로_Authentication_조회() {
        // given
        Member admin = MemberTestDataBuilder.admin().build();
        Member user = MemberTestDataBuilder.user().build();
        memberService.saveMember(admin);

        // when
        Authentication auth = memberService.getAuthentication(admin);
        Assertions.assertThrows(NullPointerException.class, () -> memberService.getAuthentication(user));

        Assertions.assertNotNull(auth);
        Assertions.assertEquals("ROLE_ADMIN", auth.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void 회원정보_수정() {
        // given
        Member member  = MemberTestDataBuilder.user().build();
        memberService.saveMember(member);

        // when
        member.updateName("ModifiedName");
        memberService.saveMember(member);

        // then
        Assertions.assertEquals("ModifiedName", member.getName());
    }

    @Test
    void 회원_탈퇴() {
        // given
        Member member  = MemberTestDataBuilder.user().build();
        memberService.saveMember(member);

        // when
        memberService.deleteMember(member.getMemNo());
        System.out.println(member.getMemNo());

        Member foundMember = memberService.findByMemNo(member.getMemNo());

        // then
        Assertions.assertNull(foundMember);
    }
}