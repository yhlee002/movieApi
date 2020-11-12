package com.portfolio.demo.project.security.UserDetail;

import com.portfolio.demo.project.entity.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Slf4j
public class UserDetail extends User {
    public UserDetail(Member member) {
        super(member.getEmail(), member.getPassword(), authorities(member));
    }

    private static Collection<? extends GrantedAuthority> authorities(Member member) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (member.getRole().equals("ROLE_ADMIN")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            log.info("들어온 memeber("+member.getEmail()+")의 권한 : ROLE_ADMIN");
        } else { // 여러개의 권한을 가질 수 있게 하려면 if문으로 대체(연관해서 userDetailsServiceImpl과 CustomAuthenticationProvider 수정 필요 - 권한 집합을 처리할 수 있게)
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            log.info("들어온 memeber("+member.getEmail()+")의 권한 : ROLE_USER");
        }
        return authorities;
    }
}
