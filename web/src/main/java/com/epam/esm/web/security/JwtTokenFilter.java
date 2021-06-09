package com.epam.esm.web.security;

import com.epam.esm.service.exception.InvalidJwtException;
import com.epam.esm.service.exception.NoSuchEntityException;
import com.epam.esm.service.logic.security.JwtTokenProvider;
import com.epam.esm.web.exception.ExceptionResponse;
import com.epam.esm.web.exception.GlobalExceptionControllerAdviser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class JwtTokenFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_TYPE_STR = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final GlobalExceptionControllerAdviser resolver;

    @Value("${jwt.header}")
    private String authHeader;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, GlobalExceptionControllerAdviser resolver) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.resolver = resolver;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) servletRequest);
        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (InvalidJwtException e) {
            Locale locale = servletRequest.getLocale();
            ExceptionResponse response = resolver.handleInvalidJwtException(locale).getBody();
            sendError((HttpServletResponse) servletResponse, response);
        } catch (NoSuchEntityException e) {
            Locale locale = servletRequest.getLocale();
            ExceptionResponse response = resolver.handleNoSuchEntityException(e, locale).getBody();
            sendError((HttpServletResponse) servletResponse, response);
        } catch (Exception e) {
            ExceptionResponse response = resolver.handleOtherExceptions(e).getBody();
            sendError((HttpServletResponse) servletResponse, response);
        }
    }

    private String resolveToken(HttpServletRequest req) {
        String authToken = req.getHeader(authHeader);
        if (authToken != null && authToken.startsWith(AUTHORIZATION_TYPE_STR)) {
            return authToken.substring(AUTHORIZATION_TYPE_STR.length() + 1);
        }
        return null;
    }

    private void sendError(HttpServletResponse httpServletResponse, Object object)
            throws IOException {
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType("application/json");
        String json = new ObjectMapper().writeValueAsString(object);
        httpServletResponse.getWriter().write(json);
        httpServletResponse.flushBuffer();
    }
}
