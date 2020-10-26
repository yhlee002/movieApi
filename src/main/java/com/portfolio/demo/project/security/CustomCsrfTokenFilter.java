//package com.portfolio.demo.project.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import java.io.IOException;
//
//@Slf4j
//@Component
//public class CustomCsrfTokenFilter implements Filter {
//    private static final String[] EXCLUDE_URL_LIST = {"/logout", "/assets", "/errorpages", "/WEB-INF", "/report/download"};
//    private static final String PREFIX_NAME = "_";
//    private static final String SESSION_TOKEN_NAME = "SESSION_TOKEN_NAME";
//    private static final String SESSION_ACTIVE_TOKEN = "SESSION_ACTIVE_TOKEN";
//    private static final String REDIRECT_URL = "/error?resultMessage=INVALID_TOKEN";
//
//    @Autowired
//    private CsrfTokenRepository csrfTokenRepository;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
