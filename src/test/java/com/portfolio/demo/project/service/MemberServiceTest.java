package com.portfolio.demo.project.service;

import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.dto.MemberParam;
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

    MemberParam createAdmin() {
        MemberParam admin = MemberParam.create(
                MemberTestDataBuilder.admin().build());
        Long memNo = memberService.saveMember(admin);

        return memberService.findByMemNo(memNo);
    }

    MemberParam createUser() {
        MemberParam user = MemberParam.create(
                MemberTestDataBuilder.user().build());
        Long memNo = memberService.saveMember(user);

        return memberService.findByMemNo(memNo);
    }

    MemberParam createRandomUser() {
        MemberParam user = MemberParam.create(
                MemberTestDataBuilder.randomIdentifierUser().build());
        Long memNo = memberService.saveMember(user);

        return memberService.findByMemNo(memNo);
    }

    @Test
    void 회원_식별번호를_이용한_단건_조회() {
        // given
        MemberParam admin = createAdmin();

        // when
        MemberParam foundUser = memberService.findByMemNo(admin.getMemNo());

        // then
        Assertions.assertEquals(admin.getMemNo(), foundUser.getMemNo());
    }

    @Test
    void unique_key인_identifier를_이용한_단건_조회() {
        // given
        MemberParam user = createUser();

        // when
        MemberParam foundUser = memberService.findByIdentifier(user.getIdentifier());

        // then
        Assertions.assertEquals(user.getMemNo(), foundUser.getMemNo());
    }

    @Test
    void name을_이용한_조회() {
        // given
        for (int i = 0; i < 5; i++) {
            memberService.saveMember(
                    MemberParam.create(
                            MemberTestDataBuilder
                                    .randomIdentifierUser()
                                    .name("abc" + i)
                                    .build()
                    )
            );
        }
        memberService.saveMember(
                MemberParam.create(
                        MemberTestDataBuilder
                                .randomIdentifierUser()
                                .name("efg")
                                .build()
                )
        );

        // when
        List<MemberParam> foundMembers = memberService.findAllByNameContaining("abc", 0, 10);
        List<MemberParam> foundMembers2 = memberService.findAllByNameContaining("e", 0, 10);

        // then
        Assertions.assertEquals(5, foundMembers.size());
        Assertions.assertEquals(1, foundMembers2.size());
    }

    @Test
    void 휴대폰_번호로_회원정보_조회() {
        // given
        String phone = "010-1111-2222";
        MemberParam user = MemberParam.create(MemberTestDataBuilder.user().phone(phone).build());
        memberService.saveMember(user);

        // when
        MemberParam foundMember = memberService.findByPhone(phone);

        // then
        Assertions.assertNotNull(foundMember);
        Assertions.assertEquals(user.getMemNo(), foundMember.getMemNo());
    }

    @Test
    void 휴대폰_번호로_회원_존재여부_확인() {
        // given
        String phone = "010-1111-2222";
        MemberParam user = MemberParam.create(MemberTestDataBuilder.user().phone(phone).build());
        memberService.saveMember(user);

        // when
        Boolean exists = memberService.existsByPhone(phone);

        // then
        Assertions.assertTrue(exists);
    }

    @Test
    void identifier와_provider를_이용한_회원정보_조회() {
        // given
        MemberParam user = createUser();

        // when
        MemberParam foundMember = memberService.findByIdentifierAndProvider(user.getIdentifier(), user.getProvider());
        MemberParam foundMember2 = memberService.findByIdentifierAndProvider("test@example.com", user.getProvider());

        // then
        Assertions.assertNotNull(foundMember);
        Assertions.assertNull(foundMember2);
    }

    @Test
    void 회원가입() {
        // given
        MemberParam admin = createAdmin();
        MemberParam user = createUser();

        // when

        // then
        Assertions.assertNotNull(admin.getMemNo());
        Assertions.assertEquals("ROLE_ADMIN", admin.getRole());
        Assertions.assertNotNull(user.getMemNo());
        Assertions.assertEquals("ROLE_USER", user.getRole());
    }

    @Test
    void 회원가입_패스워드_미기입시_오류_발생() {
        // when
        MemberParam user = MemberParam.create(MemberTestDataBuilder.noPasswordUser().build());

        // then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            memberService.saveMember(user);
        });
    }

    @Test
    void 소셜_로그인을_이용한_회원가입() {
        // given
        MemberParam user = createUser();

        // when

        // then
        Assertions.assertNotNull(user.getMemNo());
    }

    @Test
    void 비밀번호_변경() {
        // given
        MemberParam user = MemberParam.create(
                MemberTestDataBuilder.user().password("1234").build()
        );
        memberService.saveMember(user);

        user.setPassword("5678");
        memberService.updateMember(user);

        // when
        MemberParam foundMember = memberService.findByMemNo(user.getMemNo());

        // then
        Assertions.assertNotEquals("1234", user.getPassword());
        Assertions.assertEquals("5678", foundMember.getPassword());
    }

    @Test
    void 회원_정보를_기반으로_Authentication_조회() {
        // given
        MemberParam admin = createAdmin();
        MemberParam user = createUser();

        // when
        Authentication auth = memberService.getAuthentication(admin);
        Assertions.assertThrows(NullPointerException.class, () -> memberService.getAuthentication(user));

        Assertions.assertNotNull(auth);
        Assertions.assertEquals("ROLE_ADMIN", auth.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void 회원정보_수정() {
        // given
        MemberParam user = createUser();

        // when
        user.setName("ModifiedName");
        memberService.updateMember(user);

        // then
        Assertions.assertEquals("ModifiedName", user.getName());
    }

    @Test
    void 회원_탈퇴() {
        // given
        MemberParam user = createUser();

        // when
        memberService.deleteMember(user.getMemNo());

        MemberParam foundMember = memberService.findByMemNo(user.getMemNo());

        // then
        Assertions.assertNull(foundMember);
    }
}