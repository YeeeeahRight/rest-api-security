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
import java.security.*;
import java.util.*;

@Component
public class JwtTokenProvider {
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.RS512;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.duration}")
    private long durationTime;
    private KeyPair keys;

    @Autowired
    public JwtTokenProvider(@Qualifier("securityUserService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        try {
            String SIGN_ALGO_NAME = "RSA";
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(SIGN_ALGO_NAME);
            keyGenerator.initialize(2048);
            keys = keyGenerator.genKeyPair();
        } catch (NoSuchAlgorithmException ignore) {
            //NOP
        }
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
                .setHeaderParam("typ", "JWT")
                .signWith(SIGNATURE_ALGORITHM, keys.getPrivate())
                .compact();
    }

    public boolean validateToken(String token) {
        PublicKey publicKey = keys.getPublic();
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(publicKey)
                    .parseClaimsJws(token);
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
        return new UsernamePasswordAuthenticationToken(userDetails, "",
                userDetails.getAuthorities());
    }

    public String getPublicKey() {
        StringBuilder stringBuilder = new StringBuilder();
        String key = Base64.getEncoder().encodeToString(keys.getPublic().getEncoded());
        stringBuilder.append("-----BEGIN PUBLIC KEY-----\n");
        stringBuilder.append(key);
        stringBuilder.append("\n-----END PUBLIC KEY-----");
        return stringBuilder.toString();
    }

    private String extractUsernameFromJwt(String token) {
        return Jwts.parser().setSigningKey(keys.getPublic())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
