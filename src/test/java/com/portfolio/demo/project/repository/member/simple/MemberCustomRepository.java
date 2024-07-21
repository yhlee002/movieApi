package com.portfolio.demo.project.repository.member.simple;

import com.portfolio.demo.project.dto.member.MemberResponse;
import com.portfolio.demo.project.dto.member.request.MemberSearchCondition;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import com.portfolio.demo.project.model.MemberTestDataBuilder;
import com.portfolio.demo.project.repository.MemberRepository;
import com.portfolio.demo.project.repository.member.MemberRepositoryCustom;
import com.portfolio.demo.project.repository.member.MemberRepositoryCustomImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MemberCustomRepository {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
//    private MemberRepositoryCustomImpl memberRepositoryCustom;
    private MemberRepositoryCustom memberRepositoryCustom;


    @Test
    void 회원_검색() {
        // given
        Member m1 = Member.builder()
                .identifier("moviesitetest-1@google.com")
                .name("abcdefg")
                .phone("01133331111")
                .role(MemberRole.ROLE_USER)
                .provider(SocialLoginProvider.none)
                .certification(MemberCertificated.Y)
                .build();

        Member m2 = Member.builder()
                .identifier("moviesitetest-2@naver.com")
                .name("cdefghijk")
                .phone("01111223344")
                .role(MemberRole.ROLE_ADMIN)
                .provider(SocialLoginProvider.none)
                .certification(MemberCertificated.Y)
                .build();

        Member m3 = Member.builder()
                .identifier("moviesitetest-21@socialuser.com")
                .name("abcjk")
                .phone("01233227788")
                .role(MemberRole.ROLE_USER)
                .provider(SocialLoginProvider.naver)
                .certification(MemberCertificated.Y)
                .build();

        memberRepository.saveAll(Arrays.asList(m1, m2, m3));

        // when
        Pageable pageable = PageRequest.of(0, 10);

        // 전체 검색
        MemberSearchCondition condition0 = MemberSearchCondition.builder().build();
        Page<MemberResponse> result0 = memberRepositoryCustom.searchPageSimple(condition0, pageable);

        // Role 검색 - admin 검색
        MemberSearchCondition condition1 = MemberSearchCondition.builder()
                .role(MemberRole.ROLE_ADMIN).build();
        Page<MemberResponse> result1 = memberRepositoryCustom.searchPageSimple(condition1, pageable);

        // Role 검색 - user 검색
        MemberSearchCondition condition2 = MemberSearchCondition.builder()
                .role(MemberRole.ROLE_USER).build();
        Page<MemberResponse> result2 = memberRepositoryCustom.searchPageSimple(condition2, pageable);

        // Provider 검색 - none 검색
        MemberSearchCondition condition3 = MemberSearchCondition.builder()
                .provider(SocialLoginProvider.none).build();
        Page<MemberResponse> result3 = memberRepositoryCustom.searchPageSimple(condition3, pageable);

        // Provider 검색 - naver 검색
        MemberSearchCondition condition4 = MemberSearchCondition.builder()
                .provider(SocialLoginProvider.naver).build();
        Page<MemberResponse> result4 = memberRepositoryCustom.searchPageSimple(condition4, pageable);

        // Certification 검색 - N 검색
        MemberSearchCondition condition5 = MemberSearchCondition.builder()
                .certification(MemberCertificated.N).build();
        Page<MemberResponse> result5 = memberRepositoryCustom.searchPageSimple(condition5, pageable);

        // identifier 검색
        MemberSearchCondition condition6 = MemberSearchCondition.builder()
                .identifier("2").build();
        Page<MemberResponse> result6 = memberRepositoryCustom.searchPageSimple(condition6, pageable);

        // name 검색
        MemberSearchCondition condition7 = MemberSearchCondition.builder()
                .name("abc").build();
        Page<MemberResponse> result7 = memberRepositoryCustom.searchPageSimple(condition7, pageable);

        // phone 검색
        MemberSearchCondition condition8 = MemberSearchCondition.builder()
                .phone("011").build();
        Page<MemberResponse> result8 = memberRepositoryCustom.searchPageSimple(condition8, pageable);

        // 복합 검색 (name + Role)
        MemberSearchCondition condition9 = MemberSearchCondition.builder()
                .name("jk").role(MemberRole.ROLE_ADMIN).build();
        Page<MemberResponse> result9 = memberRepositoryCustom.searchPageSimple(condition9, pageable);

        // 복합 검색 (identifier + Role + Provider) 예상되는 조회 수 : 0
        MemberSearchCondition condition10 = MemberSearchCondition.builder()
                .identifier("testuser")
                .role(MemberRole.ROLE_ADMIN)
                .provider(SocialLoginProvider.none).build();
        Page<MemberResponse> result10 = memberRepositoryCustom.searchPageSimple(condition10, pageable);

        // then
        Assertions.assertEquals(3, result0.getContent().size());
        Assertions.assertEquals(1, result1.getContent().size());
        Assertions.assertEquals(2, result2.getContent().size());
        Assertions.assertEquals(2, result3.getContent().size());
        Assertions.assertEquals(1, result4.getContent().size());
        Assertions.assertEquals(0, result5.getContent().size());
        Assertions.assertEquals(2, result6.getContent().size());
        Assertions.assertEquals(2, result7.getContent().size());
        Assertions.assertEquals(2, result8.getContent().size());
        Assertions.assertEquals(1, result9.getContent().size());
        Assertions.assertEquals(0, result10.getContent().size());
    }
}
