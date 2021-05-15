package com.eventify.api.auth.controllers;

import com.eventify.api.auth.provider.UserDetailsWrapperService;
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
    private UserDetailsWrapperService userDetailsWrapperService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private String authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        UserDetails userDetails = userDetailsWrapperService.loadUserByUsername(email);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAuthToken(@RequestBody JwtRegisterRequest body) {
        String email = body.getEmail();
        String password = body.getPassword();
        String displayName = body.getDisplayName();

        User newUser;
        try {
            newUser = userService.createUser(email, password, displayName);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        try {
            String token = authenticate(email, password);
            return ResponseEntity
                    .created(new URI("/user/" + newUser.getId()))
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(new JwtResponse(token)); // TODO: token in body is debug for now
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            userService.deleteUser(newUser.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> getAuthToken(@RequestBody JwtAuthenticationRequest body) {
        String email = body.getEmail();
        String password = body.getPassword();

        try {
            String token = authenticate(email, password);
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(new JwtResponse(token)); // TODO: token in body is debug for now
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
