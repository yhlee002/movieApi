package com.portfolio.demo.project.repository.member;

import com.portfolio.demo.project.dto.member.MemberResponse;
import com.portfolio.demo.project.dto.member.QMemberResponse;
import com.portfolio.demo.project.dto.member.request.MemberSearchCondition;
import com.portfolio.demo.project.entity.member.MemberCertificated;
import com.portfolio.demo.project.entity.member.MemberRole;
import com.portfolio.demo.project.entity.member.SocialLoginProvider;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.portfolio.demo.project.entity.member.QMember.member;

@Repository // TODO. 필요 여부 확인하기
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MemberResponse> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
        /* fetchResults(deprecated) 사용 */
//        QueryResults<MemberResponse> result =  queryFactory
//                .select(new QMemberResponse(member.memNo, member.identifier,
//                        member.name, member.phone, member.provider, member.profileImage,
//                        member.role, member.certification, member.regDate.stringValue()))
//                .from(member)
//                .where(member.role.eq(condition.getRole()),
//                        member.certification.eq(condition.getCertification()),
//                        member.provider.eq(condition.getProvider()),
//                        member.identifier.containsIgnoreCase(condition.getIdentifier()),
//                        member.name.containsIgnoreCase(condition.getName()))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchResults();
//
//        long totalElemCnt = result.getTotal();
//        List<MemberResponse> list = result.getResults();

        /* 카운트 쿼리와 컨텐츠 쿼리 구분하기 */
        List<MemberResponse> list = queryFactory
                .select(new QMemberResponse(member.memNo, member.identifier,
                        member.name, member.phone, member.provider, member.profileImage,
                        member.role, member.certification, member.regDate.stringValue()))
                .from(member)
                .where(roleEq(condition.getRole()),
                        certificationEq(condition.getCertification()),
                        providerEq(condition.getProvider()),
                        identifierContainsIgnoreCase(condition.getIdentifier()),
                        nameContainsIgnoreCase(condition.getName()),
                        phoneContains(condition.getPhone()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalElemCnt = queryFactory
                .select(member.count()) // == count(member.id)
                .from(member)
                .where(roleEq(condition.getRole()),
                        certificationEq(condition.getCertification()),
                        providerEq(condition.getProvider()),
                        identifierContainsIgnoreCase(condition.getIdentifier()),
                        nameContainsIgnoreCase(condition.getName()),
                        phoneContains(condition.getPhone()))
                .fetchOne();

        return new PageImpl<MemberResponse>(list, pageable, totalElemCnt);
    }

    private BooleanExpression identifierContainsIgnoreCase(String identifier) {
        return StringUtils.hasText(identifier) ? member.identifier.containsIgnoreCase(identifier) : null;
    }

    private BooleanExpression nameContainsIgnoreCase(String name) {
        return StringUtils.hasText(name) ? member.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression phoneContains(String phone) {
        return StringUtils.hasText(phone) ? member.phone.contains(phone) : null;
    }

    private BooleanExpression roleEq(MemberRole role) {
        return role != null ? member.role.eq(role) : null;
    }

    private BooleanExpression certificationEq(MemberCertificated certification) {
        return certification != null ? member.certification.eq(certification) : null;
    }

    private BooleanExpression providerEq(SocialLoginProvider provider) {
        return provider != null ? member.provider.eq(provider) : null;
    }
}
