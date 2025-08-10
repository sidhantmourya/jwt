package com.example.jwt.service;

import com.example.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBlackListService {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    public TokenBlackListService(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String jwtToken)
    {
        redisTemplate.delete("valid:"+jwtToken);

        Date expiration = jwtUtil.extractExpiration(jwtToken);
        long remValidity = expiration.getTime() - new Date().getTime();

        if(!jwtUtil.isTokenExpired(jwtToken))
        {
            redisTemplate.opsForValue().set("blacklist:"+ jwtToken, true,remValidity, TimeUnit.MILLISECONDS );
        }

    }

    public boolean isTokenBlackListed(String jwtToken)
    {
        return redisTemplate.hasKey("blacklist:" + jwtToken);
    }
}
