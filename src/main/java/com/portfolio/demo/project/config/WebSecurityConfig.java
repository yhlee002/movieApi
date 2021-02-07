package com.portfolio.demo.project.config;

import com.portfolio.demo.project.security.CustomAuthenticationProvider;
import com.portfolio.demo.project.security.SignInSuccessHandler;
import com.portfolio.demo.project.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.sql.DataSource;
import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;
    private final DataSource dataSource;

    private static final String[] CSRF_IGNORE = {"/signin/**", "/signup/**"};

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favicon.ico", "/resources/**", "/js/**", "/css/**", "/webjars/**", "/webjars/bootstrap/4.5.2/**", "/images/**", "/templates/fragments/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/sign-up/**", "/notice", "/imp").permitAll()
                .antMatchers("/user/**", "/logout", "/boardName/**", "/mypage/**", "/imp/**", "/notice/**").authenticated() // ROLE_USER 혹은 ROLE_ADMIN만 접근 가능
                .antMatchers("/admin/**", "/notice/write").hasRole("ADMIN");

        http.httpBasic();

        http.formLogin()
                .loginPage("/sign-in")
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/sign-in/sign-in-processor")
                .successHandler(signInSuccessHandler())
                .permitAll();

        //최대 세션 수를 하나로 제한해 동시 로그인 불가
        http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(true);

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .clearAuthentication(true)
                .invalidateHttpSession(true) // 로그아웃시 세션 삭제
                .deleteCookies("JSESSIONID", "mvif-remember"); // 로그아웃시 쿠키 삭제 ( Remember-me 쿠키도 제거)


//        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());

        http.rememberMe()
                .key("mvif-remember") // 쿠키에 사용되는 값을 암호화하기 위한 키 값
                .userDetailsService(userDetailsService) // 시스템에서 사용자 계정을 조회하기 위한 service
                .tokenRepository(tokenRepository())
                .tokenValiditySeconds(60 * 60 * 24) // 토큰은 24시간 동안 유효
                .rememberMeCookieName("mvif-remember") //브라우저에 보관되는 쿠키의 이름(기본값 : remember-me)
                .rememberMeParameter("remember-me");// 웹 화면에서 로그인할 때 리멤버미 기능의 체크박스 이름
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected CustomAuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    @Bean
    protected SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    protected SignInSuccessHandler signInSuccessHandler() {
        return new SignInSuccessHandler();
    }

    // 로그아웃시 세션정보 제거(세션이 삭제되어도 세션 정보(Set)에 추가된 사용자 정보는 사라지지 않음)
    @Bean
    protected static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        return repository;
    }

}
