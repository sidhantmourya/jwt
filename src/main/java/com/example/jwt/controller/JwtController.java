package com.example.jwt.controller;

import com.example.jwt.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class JwtController {

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ResponseDTO>> register(@RequestBody RegisterRequestDTO request)
    {

        ResponseDTO data = new ResponseDTO();
        ApiResponse<ResponseDTO> response = new ApiResponse<>(true, "registered", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenDTO>> login(@RequestBody RequestLoginDTO dto)
    {
        TokenDTO jwtToken = new TokenDTO();
        ApiResponse<TokenDTO> response = new ApiResponse<>(true, "login", jwtToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ResponseDTO>> me()
    {
        ResponseDTO data = new ResponseDTO();
        ApiResponse<ResponseDTO> response = new ApiResponse<>(true, "me", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<ResponseDTO>> validate()
    {
        ResponseDTO data = new ResponseDTO();
        ApiResponse<ResponseDTO> response = new ApiResponse<>(true, "me", data);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenDTO>> refresh()
    {
        TokenDTO jwtToken = new TokenDTO();
        ApiResponse<TokenDTO> response = new ApiResponse<>(true, "reffresh", jwtToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout()
    {
        ApiResponse<Void> response = new ApiResponse<>(true, "logout", null);
        return ResponseEntity.ok(response);
    }


}
