package com.example.jwt.service;

import com.example.jwt.model.RefreshTokens;
import com.example.jwt.model.Users;
import com.example.jwt.repository.RefreshTokenRepository;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.util.JwtUtil;
import com.example.jwt.logging.SecurityLogger;
import com.example.jwt.logging.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public RefreshTokens createRefreshToken(String username)
    {
        String clientIP = MDC.get("clientIp");
        logger.info("Creating refresh token for user: {}", username);
        Optional<Users> currentUser = userRepository.findByUsername(username);
        if(currentUser.isEmpty())
        {
            logger.error("Refresh token creation failed - User not found: {}", username);
            AuditLogger.logError(username, "refresh_token_creation", "User not found", clientIP);
            throw new RuntimeException("User not found");
        }
        Users user = currentUser.get();

        String refreshToken = jwtUtil.refreshToken();
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationTime()/1000);

        RefreshTokens refreshTokens = new RefreshTokens();
        refreshTokens.setToken(refreshToken);
        refreshTokens.setExpiryDate(expirationDate);
        refreshTokens.setUser(user);
        refreshTokens.setRevoked(false);

        logger.debug("Saving refresh token for user: {} with token: {}", username, refreshToken);
        SecurityLogger.logTokenGeneration(username, clientIP, "refresh_token");
        return refreshTokenRepository.save(refreshTokens);


    }

    public Users verifyAndGetUserFromToken(){
        return null;
    }

    public String deleteToken(String token)
    {
        String clientIP = MDC.get("clientIp");
        logger.info("Deleting token: {}", token);
        Optional<RefreshTokens> refreshToken = refreshTokenRepository.findByToken(token);
        if(refreshToken.isEmpty())
        {
            logger.error("Deletion failed - Token not found: {}", token);
            AuditLogger.logError(null, "delete_token", "Token not found", clientIP);
            throw new RuntimeException("Token not found");
        }
        RefreshTokens refreshTokens = refreshToken.get();
        refreshTokens.setRevoked(true);
        refreshTokenRepository.save(refreshTokens);
        logger.info("Token successfully deleted: {}", token);
        SecurityLogger.logLogout(null, clientIP);
        return "Logged Out";
    }


}
