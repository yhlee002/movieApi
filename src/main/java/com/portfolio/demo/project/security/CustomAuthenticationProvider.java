package com.portfolio.demo.project.security;

import com.portfolio.demo.project.security.UserDetail.UserDetail;
import com.portfolio.demo.project.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        log.info("커스텀 프로바이더 실행.");

        UserDetail user = (UserDetail) userDetailsService.loadUserByUsername(email);

        log.info("userDetailsService에서 받아온 user 정보 : "+user.toString());

        if(!user.isEnabled()){
            throw new BadCredentialsException(email);
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException(email);
        }

        return new UsernamePasswordAuthenticationToken(email, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
