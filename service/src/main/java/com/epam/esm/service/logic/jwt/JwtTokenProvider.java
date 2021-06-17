package com.epam.esm.service.logic.jwt;

import com.epam.esm.service.exception.InvalidJwtException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class JwtTokenProvider {
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;

    //RS256

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.duration}")
    private long durationTime;

    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(@Qualifier("securityUserService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String username, Map<String, Object> payloadObjects) {
        Claims claims = Jwts.claims();
        claims.setSubject(username);
        claims.putAll(payloadObjects);
        Date currentTime = new Date();
        Date expirationTime = new Date(currentTime.getTime() + durationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentTime)
                .setExpiration(expirationTime)
                .signWith(SIGNATURE_ALGORITHM, secretKey)
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            Date expirationDate = claimsJws.getBody().getExpiration();
            Date currentDate = new Date();
            return !expirationDate.before(currentDate);
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException("JWT token is expired or invalid");
        }
    }

    public Authentication getAuthentication(String token) {
        String username = extractUsernameFromJwt(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String extractUsernameFromJwt(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
}
