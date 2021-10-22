package com.controller;

import com.controller.dto.AuthenticationDto;
import com.controller.dto.LoggedResponseDto;
import com.facade.UserFacade;
import com.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/authentication")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    public static final long TOKEN_VALIDITY = 86400 * 1000L;
    private final UserFacade userFacade;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "/login", consumes = "application/json")
    public ResponseEntity<LoggedResponseDto> login(@RequestBody @Valid AuthenticationDto authDto) {
        userFacade.checkCredentials(authDto);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDto.getUsername(), authDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtUtil.createLoginToken(authentication, TOKEN_VALIDITY);
        LoggedResponseDto loggedResponseDto = userFacade.createLoggedResponse(token, authDto);
        return ResponseEntity.ok().body(loggedResponseDto);
    }
}
