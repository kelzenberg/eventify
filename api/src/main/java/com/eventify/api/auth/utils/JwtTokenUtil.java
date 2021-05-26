package com.eventify.api.auth.utils;

import com.eventify.api.auth.exceptions.TokenIsInvalidException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenUtil {

    @Value("${jwt.signingKey}")
    private String signingKey;

    private final long timeToExpire = 2 * 60 * 60 * 1000; // in milliseconds => 2hrs
    private final long clockSkewBuffer = 2 * 60; // in seconds => 2s
    private final String issuer = "api.eventify";

    public String generateToken(UserDetails userDetails) {
        final Date now = new Date();

        Claims claims = Jwts.claims()
                .setSubject(userDetails.getUsername()) // subject is email, getUsername() returns email
                .setIssuer(issuer)
                .setExpiration(new Date(now.getTime() + timeToExpire))
                .setIssuedAt(now);

        // Deprecation is flawed, see: https://github.com/jwtk/jjwt/issues/617
        // noinspection deprecation
        return Jwts.builder()
                .setClaims(claims)
                .signWith(
                        SignatureAlgorithm.HS512, // HS512: HMAC using SHA-512
                        signingKey
                )
                .compact();
    }

    public Claims parseToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setAllowedClockSkewSeconds(clockSkewBuffer)
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public Boolean isTokenInvalid(String token) {
        Claims claims = parseToken(token);

        return List.of(
                claims.getExpiration().after(new Date()), // token is not expired
                claims.getIssuer().equals(issuer) // token issuer is correct
        ).contains(false);
    }

    // Convenience Token Methods:

    public String getSubject(String token) throws TokenIsInvalidException {
        if (isTokenInvalid(token)) {
            throw new TokenIsInvalidException();
        }

        try {
            return parseToken(token).getSubject();
        } catch (JwtException e) {
            System.err.println("[DEBUG] Token Parsing Failed: " + e.getMessage() + "\n" + token);
            throw new TokenIsInvalidException();
        }
    }
}
