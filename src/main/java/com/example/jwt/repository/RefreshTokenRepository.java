package com.example.jwt.repository;

import com.example.jwt.model.RefreshTokens;
import com.example.jwt.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {

    Optional<RefreshTokens> findByToken(String token);

    @Modifying
    int deleteByUser(Users user);


}
