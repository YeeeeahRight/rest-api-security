package com.epam.esm.web.filter;

import com.epam.esm.service.exception.InvalidJwtException;
import com.epam.esm.service.exception.NoSuchEntityException;
import com.epam.esm.service.logic.jwt.JwtTokenProvider;
import com.epam.esm.web.exception.ExceptionResponse;
import com.epam.esm.web.exception.GlobalExceptionControllerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
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
    private final GlobalExceptionControllerHandler resolver;
    private final ServletJsonResponseSender jsonResponseSender;

    @Value("${jwt.header}")
    private String authHeader;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, GlobalExceptionControllerHandler resolver,
                          ServletJsonResponseSender jsonResponseSender) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.resolver = resolver;
        this.jsonResponseSender = jsonResponseSender;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException {
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
            ExceptionResponse responseObject = resolver.handleInvalidJwtException(locale).getBody();
            jsonResponseSender.send((HttpServletResponse) servletResponse, responseObject);
        } catch (NoSuchEntityException e) {
            Locale locale = servletRequest.getLocale();
            ExceptionResponse responseObject = resolver.handleNoSuchEntityException(e, locale).getBody();
            jsonResponseSender.send((HttpServletResponse) servletResponse, responseObject);
        } catch (Exception e) {
            ExceptionResponse responseObject = resolver.handleOtherExceptions(e).getBody();
            jsonResponseSender.send((HttpServletResponse) servletResponse, responseObject);
        }
    }

    private String resolveToken(HttpServletRequest req) {
        String authToken = req.getHeader(authHeader);
        if (authToken != null && authToken.startsWith(AUTHORIZATION_TYPE_STR)) {
            return authToken.substring(AUTHORIZATION_TYPE_STR.length() + 1);
        }
        return null;
    }
}
