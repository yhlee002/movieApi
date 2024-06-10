package com.portfolio.demo.project.security;

import com.portfolio.demo.project.security.UserDetail.UserDetail;
import lombok.RequiredArgsConstructor;
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

    @Autowired // 제거 임시 보류
    private UserDetailsServiceImpl userDetailsService;

    @Autowired // 제거 임시 보류
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identifier = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        log.info("커스텀 프로바이더 실행.");

        UserDetail user = (UserDetail) userDetailsService.loadUserByUsername(identifier);

        log.info("userDetailsService에서 받아온 user 정보 : "+user.toString());

        if(!user.isEnabled()){
            throw new BadCredentialsException(identifier);
        }
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new BadCredentialsException(identifier);
        }
        return new UsernamePasswordAuthenticationToken(identifier, password, user.getAuthorities());
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
