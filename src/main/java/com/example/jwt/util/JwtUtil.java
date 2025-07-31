package com.example.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtKey;

    @Value("${jwt.access-token-expiration-ms}")
    private long expirationTime;

    private Key getSignInKey()
    {
        byte[] decode = Decoders.BASE64.decode(this.jwtKey);
        Key key = Keys.hmacShaKeyFor(decode);
        return key;

    }

    public String generateAccessToken(UserDetails userDetails)
    {
        logger.debug("Generating access token for user: {}", userDetails.getUsername());

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", roles);
        String token = builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+this.expirationTime))
                .signWith(getSignInKey())
                .compact();
        logger.debug("Access token generated successfully for user: {}", userDetails.getUsername());
        return token;
    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parser().setSigningKey(this.jwtKey).build().parseSignedClaims(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims =  extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractUsername(String token)
    {
        logger.debug("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token)
    {
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails)
    {
        logger.debug("Validating token for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        boolean isValid = (userDetails.getUsername().equals(username) && !isTokenExpired(token));
        logger.debug("Token validation result for user {}: {}", userDetails.getUsername(), isValid);
        return isValid;
    }

    public String refreshToken()
    {
        return UUID.randomUUID().toString();
    }

    public long getExpirationTime() {
        return expirationTime;
    }


    public String extractToken(HttpServletRequest request) {

        final String authHeader = request.getHeader("Authorization");
        return authHeader.substring(7);


    }
}
