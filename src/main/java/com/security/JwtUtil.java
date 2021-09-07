package com.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Date;
import java.util.function.Function;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

public class JwtUtil {

    private static final String SIGNING_KEY = "mU13nG4t";

    public String createLoginToken(Authentication authentication, Long validity) {
        return Jwts.builder()
                .setSubject(((User) authentication.getPrincipal()).getUsername())
                .signWith(HS256, SIGNING_KEY)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validity))
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public Boolean validateTokenWithUserDetails(String token, UserDetails userDetails) {
        isSignedWithKey(token);
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public UsernamePasswordAuthenticationToken getAuthentication(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(userDetails, StringUtils.EMPTY, Collections.emptyList());
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public void isSignedWithKey(String token) {
        JwtParser jwtParser = Jwts.parser().setSigningKey(SIGNING_KEY);
        if (!jwtParser.isSigned(token)) {
            throw new JwtException("Could not verify integrity of token.");
        }
    }
}
