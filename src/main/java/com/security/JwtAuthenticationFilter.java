package com.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_STRING = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer ";

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HEADER_STRING);
        String username = null;
        String authenticationToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authenticationToken = header.replace(TOKEN_PREFIX, StringUtils.EMPTY);
            try {
                username = jwtUtil.getUsernameFromToken(authenticationToken);
            } catch (IllegalArgumentException illegalArgumentException) {
                logger.error("An error occurred while getting the username from token.", illegalArgumentException);
            } catch (ExpiredJwtException expiredJwtException) {
                logger.warn("The token has expired and it is not valid anymore.", expiredJwtException);
            } catch (SignatureException signatureException) {
                logger.error("Authentication failed. Username or password not valid.");
            }
        }
        authenticateUser(request, username, authenticationToken);
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(HttpServletRequest request, String username, String authenticationToken) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateTokenWithUserDetails(authenticationToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        jwtUtil.getAuthentication(userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }
}
