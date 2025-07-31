package com.example.jwt.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Utility class for logging audit/business events with consistent structure
 */
public class AuditLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("com.example.jwt.audit");
    
    public static void logUserRegistration(String username, String email, String clientIP) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "user_registration");
            MDC.put("resource", "user");
            
            logger.info("New user registered - Username: {}, Email: {}", username, email);
        } finally {
            clearMDC();
        }
    }
    
    public static void logUserProfileAccess(String username, String clientIP) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "profile_access");
            MDC.put("resource", "user_profile");
            
            logger.info("User profile accessed by: {}", username);
        } finally {
            clearMDC();
        }
    }
    
    public static void logTokenRefresh(String username, String clientIP) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "token_refresh");
            MDC.put("resource", "refresh_token");
            
            logger.info("Token refreshed for user: {}", username);
        } finally {
            clearMDC();
        }
    }
    
    public static void logApiEndpointAccess(String username, String endpoint, String method, String clientIP) {
        try {
            MDC.put("userId", username != null ? username : "anonymous");
            MDC.put("clientIp", clientIP);
            MDC.put("action", "api_access");
            MDC.put("resource", endpoint);
            MDC.put("method", method);
            
            logger.info("API endpoint accessed - Method: {}, Endpoint: {}, User: {}", 
                       method, endpoint, username != null ? username : "anonymous");
        } finally {
            clearMDC();
        }
    }
    
    public static void logDataModification(String username, String entity, String entityId, String operation, String clientIP) {
        try {
            MDC.put("userId", username);
            MDC.put("clientIp", clientIP);
            MDC.put("action", "data_modification");
            MDC.put("resource", entity);
            MDC.put("operation", operation);
            MDC.put("entityId", entityId);
            
            logger.info("Data modification - Entity: {}, ID: {}, Operation: {}, User: {}", 
                       entity, entityId, operation, username);
        } finally {
            clearMDC();
        }
    }
    
    public static void logError(String username, String operation, String errorMessage, String clientIP) {
        try {
            MDC.put("userId", username != null ? username : "unknown");
            MDC.put("clientIp", clientIP);
            MDC.put("action", "error_occurred");
            MDC.put("operation", operation);
            
            logger.error("Error during operation: {} for user: {} - {}", operation, username, errorMessage);
        } finally {
            clearMDC();
        }
    }
    
    private static void clearMDC() {
        MDC.remove("userId");
        MDC.remove("clientIp");
        MDC.remove("action");
        MDC.remove("resource");
        MDC.remove("method");
        MDC.remove("operation");
        MDC.remove("entityId");
    }
}
