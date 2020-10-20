//package com.portfolio.demo.project.config;
//
////import com.portfolio.demo.project.service.RememberMeTokenService;
//
//import com.portfolio.demo.project.service.UserService;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.csrf.CsrfFilter;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.web.filter.CharacterEncodingFilter;
//
//import javax.servlet.DispatcherType;
//import javax.servlet.Filter;
//import java.util.EnumSet;
//
//@Configuration
//@EnableWebSecurity(debug = true) // Spring Boot가 제공해주는 기본적인 Spring Security 설정은 쓰이지 않게 됨
////@EnableGlobalMethodSecurity
////@RequiredArgsConstructor
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/assets/**", "/image/**"); // "/templates/**"
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        http.addFilterBefore(filter, CsrfFilter.class); // SpringSecurityFilterChain
//
//        http.authorizeRequests()// 요청에 대한 권한을 지정
//                .antMatchers("/").permitAll() // 전체 접근 허용
//                .antMatchers("/login/**").permitAll();
////                .antMatchers("/board/{boardNo}/**", "/mypage/**").hasAnyRole("USER", "ADMIN")
////                .antMatchers("/userboard/**").hasRole("ADMIN");
////                .anyRequest().authenticated(); // 인증되어야 접근 허용
//
//        http.formLogin() // .csrf().disable()
//                .loginPage("/login")
//                .loginProcessingUrl("/login/login-processor")
//                .usernameParameter("email")
//                .passwordParameter("password")
//                .defaultSuccessUrl("/")
//                .failureUrl("/login?error")
//                .permitAll()
//                .and()
//                .exceptionHandling().accessDeniedPage("/denied");
//        // .and().exceptionHandling().accessDeniedHandler(MemberAccessDeniedHandler());
//
////        http.rememberMe() // 로그인한 사용자만 접근 가능(리멤버 기능)
////                .key("xxxoxxo")
////                .rememberMeParameter("auto-login")
////                .rememberMeCookieName("remember-me")
////                .tokenValiditySeconds(60 * 60 * 24 * 30) // 30일 유지
////                .tokenRepository(rememberMeTokenService())
////                .userDetailsService(userService());
//
//        http.logout()
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
//                .logoutSuccessUrl("/login/loginForm")
//                .invalidateHttpSession(true) // 로그아웃시 세션 삭제
//                .deleteCookies("JSESSIONID") // 로그아웃시 쿠키 삭제
//                .permitAll();
//
//        //최대 세션 수를 하나로 제한해 동시 로그인 불가
////        http.sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(true);
//    }
//
//    // userDetailsService로서 UserService를 지정
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
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
////    @Bean
////    public RememberMeTokenService rememberMeTokenService() throws Exception {
////        return new RememberMeTokenService();
////    }
//
//}
