package com.portfolio.demo.project.config;

import com.portfolio.demo.project.oauth2.handler.OAuth2SignInFailureHandler;
import com.portfolio.demo.project.oauth2.handler.OAuth2SignInSuccessHandler;
import com.portfolio.demo.project.oauth2.service.OAuth2UserServiceImpl;
import com.portfolio.demo.project.security.*;
import com.portfolio.demo.project.security.handler.SignInFailureHandler;
import com.portfolio.demo.project.security.handler.SignInSuccessHandler;
import com.portfolio.demo.project.security.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final DataSource dataSource;

    private static final String[] CSRF_IGNORE = {"/signin/**", "/signup/**"};

    @Value("${web.server.host}")
    private String HOST;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() throws Exception {
        return (web) -> web.ignoring().requestMatchers("/favicon.ico", "/resources/**", "/js/**", "/css/**", "/webjars/**", "/webjars/bootstrap/4.5.2/**", "/images/**", "/templates/fragments/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, InMemoryClientRegistrationRepository clientRegistrationRepository) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/api/oauth2/authorization/**").permitAll()
                        .anyRequest().permitAll()
//                .requestMatchers("/", "/sign-in/**", "/sign-up/**", "/api/**", "/error/**").permitAll()
//                .requestMatchers("/user/**", "/logout", "/boardName/**", "/mypage/**", "/imp/**", "/notice/**").authenticated() // ROLE_USER 혹은 ROLE_ADMIN만 접근 가능
//                .requestMatchers("/admin/**", "/notice/new").hasRole("ADMIN")
        );

        http.cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(HttpBasicConfigurer::disable);

        http.formLogin(login -> login
                .usernameParameter("identifier")
                .passwordParameter("password")
                .loginProcessingUrl("/members/sign-in")
                .defaultSuccessUrl("/")
                .successHandler(signInSuccessHandler())
                .failureHandler(signinFailureHandler())
                .permitAll());

        // redirect-uri로 들어올 때 실행
        http.oauth2Login(login -> login
                .failureUrl("http://" + HOST + "/sign-in")
                .userInfoEndpoint(config -> config.userService(oAuth2UserService))
                .successHandler(oAuth2SignInSuccessHandler())
                .failureHandler(oAuth2SignInFailureHandler())
                .permitAll());

        //최대 세션 수를 하나로 제한해 동시 로그인 불가
        http.sessionManagement(session -> session.maximumSessions(1).maxSessionsPreventsLogin(true));

        http.logout(logout -> logout
                .logoutUrl("/members/logout")
                .logoutSuccessUrl("http://" + HOST)
                .clearAuthentication(true)
                .invalidateHttpSession(true) // 로그아웃시 세션 삭제
                .deleteCookies("JSESSIONID", "mvif-remember")); // 로그아웃시 쿠키 삭제 ( Remember-me 쿠키도 제거)


//        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler());

        http.rememberMe(rememberMe -> rememberMe.key("mvif-remember") // 쿠키에 사용되는 값을 암호화하기 위한 키 값
                .userDetailsService(userDetailsService) // 시스템에서 사용자 계정을 조회하기 위한 service
                .tokenRepository(tokenRepository())
                .tokenValiditySeconds(60 * 60 * 24) // 토큰은 24시간 동안 유효
                .rememberMeCookieName("mvif-remember") //브라우저에 보관되는 쿠키의 이름(기본값 : remember-me)
                .rememberMeParameter("remember-me"));// 웹 화면에서 로그인할 때 리멤버미 기능의 체크박스 이름

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost", "http://localhost/", "http://3.38.19.101"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // "Authorization", "Content-Type", "X-Auth-Token"
        configuration.setExposedHeaders(Arrays.asList("set-cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(7200L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean // 제거 임시 보류
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider();
    }

//    @Bean
//    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(MemberRepository memberRepository) {
//        return new OAuth2UserServiceImpl(memberRepository);
//    }

    @Bean
    public SecureRandom secureRandom() {
        return new SecureRandom();
    }

    @Bean
    public OAuth2SignInSuccessHandler oAuth2SignInSuccessHandler() {
        return new OAuth2SignInSuccessHandler();
    }

    @Bean
    public OAuth2SignInFailureHandler oAuth2SignInFailureHandler() {
        return new OAuth2SignInFailureHandler();
    }

    @Bean
    public SignInSuccessHandler signInSuccessHandler() {
        return new SignInSuccessHandler();
    }

    @Bean
    public SignInFailureHandler signinFailureHandler() {
        return new SignInFailureHandler();
    }

    // 로그아웃시 세션정보 제거(세션이 삭제되어도 세션 정보(Set)에 추가된 사용자 정보는 사라지지 않음)
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl repository = new JdbcTokenRepositoryImpl();
        repository.setDataSource(dataSource);
        return repository;
    }

}
