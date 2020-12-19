package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.member.OauthMember;
import com.portfolio.demo.project.repository.OauthMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OauthMemberService {
    @Autowired
    OauthMemberRepository oMemberRepository;

    public OauthMember findOauthMemberByOauthMemNo(Long oauthMemNo){
        return oMemberRepository.findByOauthmemNo(oauthMemNo);
    }

    public OauthMember findOauthMemberByUniqueId(String uniqueId) {
        return oMemberRepository.findOauthMemberByUniqueId(uniqueId);
    }

    public void saveOauthMember(OauthMember oauthMember) {
        oMemberRepository.save(oauthMember);
    }
}
