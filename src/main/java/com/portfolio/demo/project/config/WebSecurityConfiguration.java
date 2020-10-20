package com.portfolio.demo.project.config;

import com.portfolio.demo.project.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/js/**", "/css/**", "/webjars/**", "/webjars/bootstrap/4.5.2/**", "/images/**", "/templates/fragments/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/sign-up/**").permitAll()
                .and()
                .formLogin()
                .loginPage("/sign-in")
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/sign-in/sign-in-processor")
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()); // .passwordEncoder(passwordEncoder())
    }

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
