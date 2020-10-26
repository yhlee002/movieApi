package com.portfolio.demo.project.config;

import com.portfolio.demo.project.security.CustomAuthenticationProvider;
import com.portfolio.demo.project.security.CustomCsrfFilter;
import com.portfolio.demo.project.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
                .antMatchers("/", "/sign-up/**", "/boardName").permitAll()
                .antMatchers("/user/**", "/logout", "/boardName/**").authenticated() // ROLE_USER 혹은 ROLE_ADMIN만 접근 가능
                .antMatchers("/admin/**").hasRole("ADMIN");

        http.httpBasic();
        http.csrf()
                .ignoringAntMatchers("/sign-in/**", "sign-up/**")
                .csrfTokenRepository(csrfTokenRepository())
                .and()
                .addFilterAfter(new CustomCsrfFilter(), CsrfFilter.class);

        http.formLogin()
                .loginPage("/sign-in")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
//                .loginProcessingUrl("/sign-in/sign-in-processor")
//                .failureUrl("sign-in/sign-in-fail")
                .permitAll(); // anonymous만 접근 가능한 경로로 변경 필요

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

    @Bean
    protected CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName(CustomCsrfFilter.CSRF_COOKIE_NAME);
        return repository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService()); // .passwordEncoder(passwordEncoder())
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
    protected CustomCsrfFilter customCsrfFilter(){
        return new CustomCsrfFilter();
    }

//    @Bean
//    protected CustomAccessDeniedHandler customAccessDeniedHandler(){
//        return new CustomAccessDeniedHandler();
//    }
//
//    static class CustomAccessDeniedHandler implements AccessDeniedHandler{
//
//        @Override
//        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//
//        }
//    }
//
//    @Order(1) // 이벤트 핸들러의 처리 순서를 결정
//    @Bean
//    public FilterRegistrationBean getSpringSecurityFilterChainBindedToError(@Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain) {
//
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(springSecurityFilterChain);
//        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
//
//        return registration;
//    }
//
//    @Bean
//    public RememberMeTokenService rememberMeTokenService() throws Exception {
//        return new RememberMeTokenService();
//    }

}
