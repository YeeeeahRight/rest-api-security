package com.epam.esm.web.security;

import com.epam.esm.service.logic.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_TYPE_STR = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.header}")
    private String authHeader;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) servletRequest);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public String resolveToken(HttpServletRequest req) {
        String authToken = req.getHeader(authHeader);
        if (authToken != null && authToken.startsWith(AUTHORIZATION_TYPE_STR)) {
            return authToken.substring(AUTHORIZATION_TYPE_STR.length() + 1);
        }
        return null;
    }
}
