package com.example.jwt.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for logging security events with consistent structure
 */
public class SecurityLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("com.example.jwt.security");
    
    public static void logAuthenticationAttempt(String username, String clientIP, String userAgent, boolean success) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("userAgent", userAgent);
            MDC.put("action", "authentication");
            
            if (success) {
                logger.info("Authentication successful for user: {}", username);
            } else {
                logger.warn("Authentication failed for user: {}", username);
            }
        } finally {
            clearMDC();
        }
    }
    
    public static void logTokenGeneration(String username, String clientIP, String tokenType) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "token_generation");
            MDC.put("tokenType", tokenType);
            
            logger.info("Token generated for user: {} of type: {}", username, tokenType);
        } finally {
            clearMDC();
        }
    }
    
    public static void logTokenValidation(String username, String clientIP, boolean isValid, String reason) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "token_validation");
            
            if (isValid) {
                logger.info("Token validation successful for user: {}", username);
            } else {
                logger.warn("Token validation failed for user: {} - reason: {}", username, reason);
            }
        } finally {
            clearMDC();
        }
    }
    
    public static void logTokenExpiration(String username, String clientIP) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "token_expiration");
            
            logger.warn("Expired token used by user: {}", username);
        } finally {
            clearMDC();
        }
    }
    
    public static void logLogout(String username, String clientIP) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "logout");
            
            logger.info("User logged out: {}", username);
        } finally {
            clearMDC();
        }
    }
    
    public static void logUnauthorizedAccess(String path, String clientIP, String userAgent) {
        try {
            MDC.put("clientIp", clientIP);
            MDC.put("userAgent", userAgent);
            MDC.put("action", "unauthorized_access");
            MDC.put("resource", path);
            
            logger.warn("Unauthorized access attempt to: {} from IP: {}", path, clientIP);
        } finally {
            clearMDC();
        }
    }
    
    public static void logSuspiciousActivity(String username, String clientIP, String activity, String details) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "suspicious_activity");
            MDC.put("activity", activity);
            
            logger.error("Suspicious activity detected - User: {}, Activity: {}, Details: {}", 
                        username, activity, details);
        } finally {
            clearMDC();
        }
    }
    
    private static void clearMDC() {
        MDC.remove("userId");
        MDC.remove("clientIp");
        MDC.remove("userAgent");
        MDC.remove("action");
        MDC.remove("tokenType");
        MDC.remove("resource");
        MDC.remove("activity");
    }
}
