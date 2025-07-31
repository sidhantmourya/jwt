package com.example.jwt.filter;

import com.example.jwt.util.JwtUtil;
import com.example.jwt.logging.SecurityLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.extractToken(request);
        if(token == null)
        {
            logger.warn("Token not found");
            filterChain.doFilter(request, response);
            return;
        }
        if(jwtUtil.isTokenExpired(token))
        {
            logger.warn("Token expired");
            filterChain.doFilter(request, response);
            return;
        }




        String username = jwtUtil.extractUsername(token);

        if(username!= null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            logger.debug("Loading user by username: {}", username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean isTokenValid = jwtUtil.validateToken(token, userDetails);
            SecurityLogger.logTokenValidation(username, request.getRemoteAddr(), isTokenValid, isTokenValid ? null : "Invalid token");

            if(isTokenValid)
            {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        }

        filterChain.doFilter(request, response);






    }
}
