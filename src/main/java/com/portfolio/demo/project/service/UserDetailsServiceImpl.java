package com.portfolio.demo.project.service;

import com.portfolio.demo.project.entity.UserDetail.UserDetail;
import com.portfolio.demo.project.entity.member.Member;
import com.portfolio.demo.project.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

//    @PostConstruct
//    private void created() {
//        log.info("UserDetails Service 인스턴스 생성");
//    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("loadUserByUserName user id {}", email);

        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new UsernameNotFoundException(email);
        }

        log.info("loadUserByUsername(" + email + ") : ", member.toString());
        log.info("부여될 권한 : " + member.getRole());

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(member.getRole()));

        return new UserDetail(member);
    }
}
