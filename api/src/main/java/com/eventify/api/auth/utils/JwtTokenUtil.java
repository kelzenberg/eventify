package com.eventify.api.auth.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.signingKey}") // TODO: env var?
    private String signingKey;

    private final long timeToExpire = 2 * 60 * 60 * 1000; // in milliseconds => 2hrs
    private final long clockSkewBuffer = 2 * 60; // in seconds => 2s

    public String generateToken(UserDetails userDetails) {
        // TODO
        final Long now = new Date().getTime();
        Claims claims = Jwts.claims()
                .setSubject(userDetails.getUsername());
                .setExpiration(new Date(now + timeToExpire));
                .setIssuedAt(now);

        claims.put("userId", "" + userDetails.getId());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, signingKey) // HS512: HMAC using SHA-512
                .compact();
    }

    public String[] parseToken(String token) {
        try {
            // TODO
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setAllowedClockSkewSeconds(clockSkewBuffer)
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            // we can safely trust the JWT

            return jws;
        } catch (JwtException e) {
            // we *cannot* use the JWT as intended
            System.err.println(e.getMessage());
        }
    }
}
