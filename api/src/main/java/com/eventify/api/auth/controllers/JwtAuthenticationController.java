package com.eventify.api.auth.controllers;

import com.eventify.api.auth.utils.JwtTokenUtil;
import com.eventify.api.user.data.User;
import com.eventify.api.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private String getToken(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAuthToken(@RequestBody JwtRegisterRequest body) throws Exception {
        String email = body.getEmail();
        String password = body.getPassword();
        String displayName = body.getDisplayName();

        User newUser = userService.createUser(email, password, displayName);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            return ResponseEntity.created(new URI("/user/" + newUser.getId()))
                    .header(HttpHeaders.AUTHORIZATION, getToken(email))
                    .body(new JwtResponse(getToken(email)));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            userService.deleteUser(newUser.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> getAuthToken(@RequestBody JwtLoginRequest body) throws Exception {
        String email = body.getEmail();
        String password = body.getPassword();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, getToken(email))
                    .body(new JwtResponse(getToken(email)));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
