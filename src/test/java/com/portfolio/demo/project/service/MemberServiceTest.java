package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


//@RunWith(SpringRunner.class)
//@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

//    private MemberRepository memberRepository;
//
//    private PasswordEncoder passwordEncoder;
//
//    private TempKey tempKey;

    @BeforeEach
    public void beforeEach() {
//        memberRepository = Mockito.mock(MemberRepository.class);
//        passwordEncoder = Mockito.mock(PasswordEncoder.class);
//        tempKey = Mockito.mock(TempKey.class);
//        memberService = new MemberService(memberRepository, passwordEncoder, tempKey);
    }

    @Test
    void findByMemNo() {
    }

    @Test
    void findByIdentifier() {
    }

    @Test
    void findAllByName() {
    }

    @Test
    void findByPhone() {
    }

    @Test
    void existsByPhone() {
    }

    @Test
    void findByIdentifierAndProvider() {
    }

    @Test
    void 회원가입() {
        // given
        Member member = MemberTestDataBuilder.user().password("1234").build();

        // when
        Member savedMember = memberService.saveMember(member);
        System.out.println("저장된 회원 정보 : " + savedMember);

        Member foundMember = memberService.findByMemNo(savedMember.getMemNo());
        System.out.println("조회된 회원 정보 : " + foundMember);

        // then
        Assertions.assertNotNull(savedMember);
        Assertions.assertEquals(savedMember.getMemNo(), foundMember.getMemNo());
    }

    @Test
    void saveOauthMember() {
    }

    @Test
    void updatePwd() {
        // given
        Member member = MemberTestDataBuilder.user().password("1234").build();
        Member savedMember = memberService.saveMember(member);
        savedMember.updatePassword("5678");
        Member savedMember2 = memberService.saveMember(savedMember);
        Member foundMember = memberService.findByMemNo(savedMember2.getMemNo());

        Assertions.assertEquals(savedMember.getPassword(), savedMember2.getPassword());
        org.assertj.core.api.Assertions.assertThat(savedMember).isEqualTo(foundMember);
    }

    @Test
    void updateCertKey() {
    }

    @Test
    void findByProfile() {
    }

    @Test
    void getAuthentication() {
    }

    @Test
    void updateMember() {
    }

    @Test
    void deleteUserInfo() {
    }

    @Test
    void deleteMember() {
    }
}