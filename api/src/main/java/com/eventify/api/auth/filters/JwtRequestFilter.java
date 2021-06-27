package com.eventify.api.auth.filters;

import com.eventify.api.auth.utils.JwtTokenUtil;
import com.eventify.api.entities.user.services.UserDetailsWrapperService;
import com.eventify.api.handlers.exceptions.TokenIsInvalidException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

    @Autowired
    private UserDetailsWrapperService userDetailsWrapperService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException, ResponseStatusException {
        final String tokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (SecurityContextHolder.getContext().getAuthentication() != null // authenticated user exists already
                || tokenHeader == null
                || !tokenHeader.trim().startsWith("Bearer ")
        ) {
            chain.doFilter(request, response);
            return;
        }

        String token = tokenHeader.split(" ")[1].trim();

        try {
            if (jwtTokenUtil.isTokenInvalid(token)) {
                chain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = userDetailsWrapperService.loadUserByUsername(
                    jwtTokenUtil.parseToken(token).getSubject()
            );
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired", e);
        } catch (TokenIsInvalidException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is invalid", e);
        } catch (JwtException e) {
            System.err.println("[DEBUG] Token Parsing Failed: " + e.getMessage() + "\n" + token);
            // do nothing but continue with chain
        }

        chain.doFilter(request, response);
    }
}
