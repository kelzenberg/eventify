package com.eventify.api.auth.controllers;

import com.eventify.api.auth.utils.JwtTokenUtil;
import com.eventify.api.constants.AdminPaths;
import com.eventify.api.constants.PublicPaths;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.services.UserDetailsWrapperService;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.mail.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

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
    private MailService mailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    private String authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        UserDetails userDetails = userDetailsWrapperService.loadUserByUsername(email);
        return jwtTokenUtil.generateToken(userDetails);
    }

    @PostMapping(PublicPaths.REGISTER)
    public ResponseEntity<?> registerAuthToken(@Valid @RequestBody JwtRegisterRequest body) throws URISyntaxException, MessagingException {
        String email = body.getEmail().trim();
        String password = body.getPassword().trim();
        String displayName = body.getDisplayName().trim();

        User newUser = userService.create(email, password, displayName);
        String token = authenticate(email, password);
        mailService.sendRegisterMail(newUser.getEmail(), newUser.getCreatedAt(), newUser.getVerificationHash());

        return ResponseEntity
                .created(new URI(AdminPaths.USERS + newUser.getId()))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(new JwtResponse(token));
    }

    @PostMapping(PublicPaths.LOGIN)
    public ResponseEntity<?> getAuthToken(@Valid @RequestBody JwtAuthenticationRequest body) {
        String email = body.getEmail().trim();
        String password = body.getPassword().trim();
        String token = authenticate(email, password);

        // TODO: send verification mail again if not yet verified

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(new JwtResponse(token));
    }
}
