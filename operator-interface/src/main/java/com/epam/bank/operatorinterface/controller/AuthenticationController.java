package com.epam.bank.operatorinterface.controller;

import com.epam.bank.operatorinterface.domain.dto.AuthenticationRequest;
import com.epam.bank.operatorinterface.domain.dto.AuthenticationResponse;
import com.epam.bank.operatorinterface.service.UserDetailsServiceImpl;
import com.epam.bank.operatorinterface.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private UserDetailsServiceImpl userDetailsServiceImpl;
    private JwtUtil jwtUtil;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
        @RequestBody AuthenticationRequest authenticationRequest) throws BadCredentialsException {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            authenticationRequest.getUserEmail(),
            authenticationRequest.getPassword()));

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(authenticationRequest.getUserEmail());

        String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}