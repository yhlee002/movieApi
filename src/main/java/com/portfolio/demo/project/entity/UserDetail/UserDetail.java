package com.portfolio.demo.project.entity.UserDetail;

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

        log.info("들어온 memeber("+member.getEmail()+")의 권한 : "+member.getRole());
        if (member.getRole().equals("ROLE_ADMIN")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return authorities;
    }
}
