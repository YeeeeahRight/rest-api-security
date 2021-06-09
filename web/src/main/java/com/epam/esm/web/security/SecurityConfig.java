package com.epam.esm.web.security;

import com.epam.esm.web.exception.ExceptionResponse;
import com.epam.esm.web.exception.GlobalExceptionControllerHandler;
import com.epam.esm.web.filter.JwtTokenFilter;
import com.epam.esm.web.filter.ServletJsonResponseSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenFilter jwtTokenFilter;
    private final ServletJsonResponseSender jsonResponseSender;
    private final GlobalExceptionControllerHandler resolver;
    private final OauthAuthSuccessHandler oauthAuthSuccessHandler;

    @Autowired
    public SecurityConfig(JwtTokenFilter jwtTokenFilter,
                          ServletJsonResponseSender jsonResponseSender,
                          GlobalExceptionControllerHandler resolver,
                          OauthAuthSuccessHandler oauthAuthSuccessHandler) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jsonResponseSender = jsonResponseSender;
        this.resolver = resolver;
        this.oauthAuthSuccessHandler = oauthAuthSuccessHandler;
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
                )
                .and()
                .oauth2Login()
                .successHandler(oauthAuthSuccessHandler::handle);
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