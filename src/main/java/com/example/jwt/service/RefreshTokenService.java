package com.example.jwt.service;

import com.example.jwt.model.RefreshTokens;
import com.example.jwt.model.Users;
import com.example.jwt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    public RefreshTokens createRefreshToken(String username)
    {
        Optional<Users> currentUser = userRepository.findByUsername(username);
        if(currentUser.isEmpty())
        {
            throw new RuntimeException("User not found");
        }
        Users user = currentUser.get();
    }


}
