package com.portfolio.demo.project.config;

import com.portfolio.demo.project.security.CustomAuthenticationProvider;
//import com.portfolio.demo.project.security.CustomCsrfFilter;
import com.portfolio.demo.project.security.SignInSuccessHandler;
import com.portfolio.demo.project.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

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
//                .permitAll(); // anonymous만 접근 가능한 경로로 변경 필요
                .successHandler(signInSuccessHandler())
                .permitAll();

        //최대 세션 수를 하나로 제한해 동시 로그인 불가
        http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(true);

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .clearAuthentication(true)
                .invalidateHttpSession(true) // 로그아웃시 세션 삭제
                .deleteCookies("JSESSIONID"); // 로그아웃시 쿠키 삭제

//        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());
    }

//    @Bean
//    protected CsrfTokenRepository csrfTokenRepository() {
//        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//        repository.setHeaderName(CustomCsrfFilter.CSRF_COOKIE_NAME);
//        return repository;
//    }

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


//    @Bean
//    public RememberMeTokenService rememberMeTokenService() throws Exception {
//        return new RememberMeTokenService();
//    }

}
