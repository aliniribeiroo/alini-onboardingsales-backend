package com.aliniribeiro.helpdesk.api.security.controller;

import com.aliniribeiro.helpdesk.api.entitiy.User;
import com.aliniribeiro.helpdesk.api.security.jwt.JwtAuthenticationRequest;
import com.aliniribeiro.helpdesk.api.security.jwt.JwtTokenUtil;
import com.aliniribeiro.helpdesk.api.security.model.CurrentUser;
import com.aliniribeiro.helpdesk.api.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    private static final String TOKEN_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    /**
     * Gera e retorna um novo token JWT.
     *
     * @return ResponseEntity<Response   <   TokenDto>>
     * @throws AuthenticationException
     */
    @PostMapping(value = "/api/auth")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
        final String token = jwtTokenUtil.getToken(userDetails);
        final User user = userService.findByEmail(authenticationRequest.getEmail());
        user.setPassword(null);

        return ResponseEntity.ok(new CurrentUser(token, user));
    }
}
