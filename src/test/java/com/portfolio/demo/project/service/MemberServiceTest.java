package com.portfolio.demo.project.service;

import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.vo.MemberVO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    MemberVO createAdmin() {
        return memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.admin().build()
                )
        );
    }

    MemberVO createUser() {
        return memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.user().build()
                )
        );
    }

    MemberVO createRandomUser() {
        return memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder.randomIdentifierUser().build()
                )
        );
    }

    @Test
    void 회원_식별번호를_이용한_단건_조회() {
        // given
        MemberVO admin = createAdmin();

        // when
        MemberVO foundUser = memberService.findByMemNo(admin.getMemNo());

        // then
        Assertions.assertEquals(admin.getMemNo(), foundUser.getMemNo());
    }

    @Test
    void unique_key인_identifier를_이용한_단건_조회() {
        // given
        MemberVO user = createUser();

        // when
        MemberVO foundUser = memberService.findByIdentifier(user.getIdentifier());

        // then
        Assertions.assertEquals(user.getMemNo(), foundUser.getMemNo());
    }

    @Test
    void name을_이용한_조회() {
        // given
        for (int i = 0; i < 5; i++) {
            memberService.updateMember(
                    MemberVO.create(
                            MemberTestDataBuilder
                                    .randomIdentifierUser()
                                    .name("abc" + i)
                                    .build()
                    )
            );
        }
        memberService.updateMember(
                MemberVO.create(
                        MemberTestDataBuilder
                                .randomIdentifierUser()
                                .name("efg")
                                .build()
                )
        );

        // when
        List<MemberVO> foundMembers = memberService.findAllByNameContaining("abc", 0, 10);
        List<MemberVO> foundMembers2 = memberService.findAllByNameContaining("e", 0, 10);

        // then
        Assertions.assertEquals(5, foundMembers.size());
        Assertions.assertEquals(1, foundMembers2.size());
    }

    @Test
    void 휴대폰_번호로_회원정보_조회() {
        // given
        String phone = "010-1111-2222";
        MemberVO user = MemberVO.create(MemberTestDataBuilder.user().phone(phone).build());
        memberService.updateMember(user);

        // when
        MemberVO foundMember = memberService.findByPhone(phone);

        // then
        Assertions.assertNotNull(foundMember);
        Assertions.assertEquals(user.getMemNo(), foundMember.getMemNo());
    }

    @Test
    void 휴대폰_번호로_회원_존재여부_확인() {
        // given
        String phone = "010-1111-2222";
        MemberVO user = MemberVO.create(MemberTestDataBuilder.user().phone(phone).build());
        memberService.updateMember(user);

        // when
        Boolean exists = memberService.existsByPhone(phone);

        // then
        Assertions.assertTrue(exists);
    }

    @Test
    void identifier와_provider를_이용한_회원정보_조회() {
        // given
        MemberVO user = createUser();

        // when
        MemberVO foundMember = memberService.findByIdentifierAndProvider(user.getIdentifier(), user.getProvider());
        MemberVO foundMember2 = memberService.findByIdentifierAndProvider("test@example.com", user.getProvider());

        // then
        Assertions.assertNotNull(foundMember);
        Assertions.assertNull(foundMember2);
    }

    @Test
    void 회원가입() {
        // given
        MemberVO admin = createAdmin();
        MemberVO user = createUser();

        // when
        memberService.updateMember(admin);
        memberService.updateMember(user);

        // then
        Assertions.assertNotNull(admin.getMemNo());
        Assertions.assertEquals("ROLE_ADMIN", admin.getRole());
        Assertions.assertNotNull(user.getMemNo());
        Assertions.assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void 회원가입_패스워드_미기입시_오류_발생() {
        // when
        MemberVO user = MemberVO.create(MemberTestDataBuilder.noPasswordUser().build());

        // then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            memberService.updateMember(user);
        });
    }

    @Test
    void 소셜_로그인을_이용한_회원가입() {
        // given
        MemberVO user = createUser();

        // when
        memberService.updateMember(user);

        // then
        Assertions.assertNotNull(user.getMemNo());
    }

    @Test
    void 비밀번호_변경() {
        // given
        MemberVO user = MemberVO.create(
                MemberTestDataBuilder.user().password("1234").build()
        );
        memberService.updateMember(user);

        user.setPassword("5678");
        memberService.updateMember(user);

        // when
        MemberVO foundMember = memberService.findByMemNo(user.getMemNo());

        // then
        Assertions.assertNotEquals("1234", user.getPassword());
        Assertions.assertEquals("5678", foundMember.getPassword());
    }

    @Test
    void 인증키_수정() {
        // given
        MemberVO user = createUser(); // 현재 member.certKey == null

        // when
        memberService.updateCertKey(user.getMemNo());

        // then
        Assertions.assertNotNull(user.getCertKey());
    }

    @Test
    void 회원_정보를_기반으로_Authentication_조회() {
        // given
        MemberVO admin = createAdmin();
        MemberVO user = createUser();
        memberService.updateMember(admin);

        // when
        Authentication auth = memberService.getAuthentication(admin);
        Assertions.assertThrows(NullPointerException.class, () -> memberService.getAuthentication(user));

        Assertions.assertNotNull(auth);
        Assertions.assertEquals("ROLE_ADMIN", auth.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void 회원정보_수정() {
        // given
        MemberVO user = createUser();

        // when
        user.setName("ModifiedName");
        memberService.updateMember(user);

        // then
        Assertions.assertEquals("ModifiedName", user.getName());
    }

    @Test
    void 회원_탈퇴() {
        // given
        MemberVO user = createUser();

        // when
        memberService.deleteMember(user.getMemNo());

        MemberVO foundMember = memberService.findByMemNo(user.getMemNo());

        // then
        Assertions.assertNull(foundMember);
    }
}