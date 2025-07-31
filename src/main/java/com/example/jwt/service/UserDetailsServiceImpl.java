package com.example.jwt.service;

import com.example.jwt.model.Users;
import com.example.jwt.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Loading user by username: {}", username);
        Optional<Users> user = userRepository.findByUsername(username);
        if(user.isEmpty())
        {
            logger.warn("User not found for username: {}", username);
            throw new UsernameNotFoundException("User not found");
        }

        logger.debug("User found and loaded: {}", username);
        return user.get();

    }
}
