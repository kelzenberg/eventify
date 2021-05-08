package com.eventify.api.auth.filters;

import com.eventify.api.auth.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
        final String tokenHeader = req.getHeader("Authorization");

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing token in request header");
        }

        String token = tokenHeader.substring(7);
        try {
            String[] parsedToken = jwtTokenUtil.parseToken(token);

            ... // TODO

            SecurityContextHolder.getContext().setAuthentication(emailPasswordAuthenticationToken); // TODO
        }
    }
}
