package com.example.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter to capture request details and add them to MDC for logging context
 */
@Component
@Order(1) // Execute before JWT filter
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Generate trace ID for request correlation
        String traceId = UUID.randomUUID().toString();
        
        try {
            // Add request details to MDC
            MDC.put("traceId", traceId);
            MDC.put("clientIp", getClientIpAddress(request));
            MDC.put("userAgent", request.getHeader("User-Agent"));
            MDC.put("requestMethod", request.getMethod());
            MDC.put("requestUri", request.getRequestURI());
            
            logger.info("Incoming request: {} {} from IP: {}", 
                       request.getMethod(), request.getRequestURI(), getClientIpAddress(request));
            
            long startTime = System.currentTimeMillis();
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info("Request completed: {} {} - Status: {} - Duration: {}ms", 
                       request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
                       
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }

    /**
     * Extract the real client IP address, considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For can contain multiple IPs, take the first one
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        String xForwardedProto = request.getHeader("X-Forwarded-Proto");
        if (xForwardedProto != null) {
            String cfConnectingIp = request.getHeader("CF-Connecting-IP");
            if (cfConnectingIp != null && !cfConnectingIp.isEmpty()) {
                return cfConnectingIp; // Cloudflare
            }
        }
        
        // Fall back to remote address
        return request.getRemoteAddr();
    }
}
