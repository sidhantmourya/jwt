package com.example.jwt.service;

import com.example.jwt.dto.RegisterRequestDTO;
import com.example.jwt.dto.RequestLoginDTO;
import com.example.jwt.dto.TokenDTO;
import com.example.jwt.model.RefreshTokens;
import com.example.jwt.model.Users;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.logging.SecurityLogger;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, UserService userService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    public TokenDTO login(RequestLoginDTO requestDTO)
    {
        String clientIP = MDC.get("clientIp");
        logger.info("Login attempt for user: {}", requestDTO.getUsername());
        try {
            Authentication token = new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword());
            authenticationManager.authenticate(token);
            logger.debug("Authentication successful for user: {}", requestDTO.getUsername());
        } catch (Exception e) {
            logger.error("Authentication failed for user: {} - {}", requestDTO.getUsername(), e.getMessage());
            SecurityLogger.logAuthenticationAttempt(requestDTO.getUsername(), clientIP, null, false);
            throw e;
        }
        Optional<Users> users = userRepository.findByUsername(requestDTO.getUsername());
        if(users.isEmpty())
        {
            logger.error("User not found during login: {}", requestDTO.getUsername());
            throw new UsernameNotFoundException("User not found");
        }

        Users user = users.get();
        logger.debug("Generating tokens for user: {}", user.getUsername());
        String accessToken = jwtUtil.generateAccessToken(user);
        SecurityLogger.logTokenGeneration(user.getUsername(), clientIP, "access_token");
        
        RefreshTokens refreshTokens = refreshTokenService.createRefreshToken(user.getUsername());
        SecurityLogger.logTokenGeneration(user.getUsername(), clientIP, "refresh_token");

        Date issuedAt = jwtUtil.extractClaim(accessToken, Claims::getIssuedAt);

        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setAccessToken(accessToken);
        tokenDTO.setExpires(jwtUtil.getExpirationTime());
        tokenDTO.setRefreshToken(refreshTokens.getToken());
        tokenDTO.setIssuedAt(issuedAt);
        
        logger.info("Login successful for user: {}", user.getUsername());
        return tokenDTO;

    }


}
