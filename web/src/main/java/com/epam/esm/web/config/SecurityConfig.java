package com.epam.esm.web.config;

import com.epam.esm.web.exception.ExceptionResponse;
import com.epam.esm.web.exception.GlobalExceptionControllerAdviser;
import com.epam.esm.web.filter.JwtTokenFilter;
import com.epam.esm.web.filter.ServletJsonResponseSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenFilter jwtTokenFilter;
    private final ServletJsonResponseSender jsonResponseSender;
    private final GlobalExceptionControllerAdviser resolver;

    @Autowired
    public SecurityConfig(JwtTokenFilter jwtTokenFilter, ServletJsonResponseSender jsonResponseSender,
                          GlobalExceptionControllerAdviser resolver) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jsonResponseSender = jsonResponseSender;
        this.resolver = resolver;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**").permitAll()
                .antMatchers("/login", "/signup").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> handleNoJwt(request, response)
                );
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private void handleNoJwt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Locale locale = request.getLocale();
        ExceptionResponse responseObject = resolver.buildNoJwtResponseObject(locale);
        jsonResponseSender.send(response, responseObject);
    }
}