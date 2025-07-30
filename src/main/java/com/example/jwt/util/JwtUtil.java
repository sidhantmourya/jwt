package com.example.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import static io.jsonwebtoken.Jwts.*;

@Component
public class JwtUtil {

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

        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        claims.put("roles", roles);
        return builder().setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+this.expirationTime))
                .signWith(getSignInKey())
                .compact();
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
        final String username = extractUsername(token);
        return (userDetails.getUsername().equals(username) && !isTokenExpired(token));
    }

    public String refreshToken()
    {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }


}
