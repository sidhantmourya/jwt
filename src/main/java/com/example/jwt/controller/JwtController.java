package com.example.jwt.controller;

import com.example.jwt.dto.*;
import com.example.jwt.service.AuthenticationService;
import com.example.jwt.service.RefreshTokenService;
import com.example.jwt.service.TokenBlackListService;
import com.example.jwt.service.UserService;
import com.example.jwt.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import com.example.jwt.logging.AuditLogger;
import com.example.jwt.logging.SecurityLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class JwtController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlackListService tokenBlackListService;

    public JwtController(UserService userService, AuthenticationService authenticationService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, TokenBlackListService tokenBlackListService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlackListService = tokenBlackListService;
    }


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ResponseDTO>> register(@RequestBody RegisterRequestDTO request, HttpServletRequest httpRequest)
    {

        String clientIP = MDC.get("clientIp");
        userService.registerNewUser(request);
        AuditLogger.logUserRegistration(request.getUsername(), request.getEmail(), clientIP);
        ApiResponse<ResponseDTO> response = new ApiResponse<>(true, "User Created Successfully", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDTO>> login(@RequestBody RequestLoginDTO dto, HttpServletRequest request)
    {
        String clientIP = MDC.get("clientIp");

        TokenDTO jwtToken = authenticationService.login(dto);

        SecurityLogger.logAuthenticationAttempt(dto.getUsername(), clientIP, request.getHeader("User-Agent"), true);
        AuditLogger.logApiEndpointAccess(dto.getUsername(), "/auth/login", "POST", clientIP);

        ApiResponse<TokenDTO> response = new ApiResponse<>(true, "login", jwtToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ResponseDTO>> me()
    {
        ResponseDTO data = userService.loadUserDetailsForSecurity();
        ApiResponse<ResponseDTO> response = new ApiResponse<>(true, "me", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ResponseDTO>> validate(HttpServletRequest request)
    {
        String jwtToken = jwtUtil.extractToken(request);
        boolean isValid = tokenBlackListService.isTokenBlackListed(jwtToken);
        ResponseDTO data = new ResponseDTO();
        ApiResponse<ResponseDTO> response = new ApiResponse<>(true, "Token Validity: " + isValid,null);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDTO>> refresh()
    {
        TokenDTO jwtToken = new TokenDTO();
        ApiResponse<TokenDTO> response = new ApiResponse<>(true, "refresh", jwtToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, @RequestBody RequestLogoutDTO logoutDTO)
    {
        String clientIP = MDC.get("clientIp");

        String token = jwtUtil.extractToken(request);
        String username = jwtUtil.extractUsername(token);

        SecurityLogger.logLogout(username, clientIP);
        AuditLogger.logApiEndpointAccess(username, "/auth/logout", "POST", clientIP);


        String responseStr = refreshTokenService.deleteToken(logoutDTO.getToken());
        tokenBlackListService.blacklistToken(token);
        userService.removeCache(username);

        ApiResponse<Void> response = new ApiResponse<>(true, responseStr, null);
        return ResponseEntity.ok(response);
    }


}
