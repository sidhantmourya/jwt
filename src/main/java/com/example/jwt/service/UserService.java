package com.example.jwt.service;


import com.example.jwt.dto.RegisterRequestDTO;
import com.example.jwt.dto.ResponseDTO;
import com.example.jwt.model.Roles;
import com.example.jwt.model.Users;
import com.example.jwt.repository.RolesRepository;
import com.example.jwt.repository.UserRepository;
import com.example.jwt.logging.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    public UserService(UserRepository userRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder, RedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    public void registerNewUser(RegisterRequestDTO requestDTO)
    {
        String clientIP = MDC.get("clientIp");
        logger.info("Registering new user: {}", requestDTO.getUsername());
        Users cacheUser = getUser(requestDTO.getEmail(), requestDTO.getUsername());
        if(cacheUser!= null || userRepository.existsByEmailOrUsername(requestDTO.getEmail(), requestDTO.getUsername()))
        {
            logger.warn("Registration failed - User already exists: {}", requestDTO.getUsername());
            AuditLogger.logError(requestDTO.getUsername(), "user_registration", "User already exists", clientIP);
            throw new RuntimeException("User already exists");
        }

        try {
            Users user = new Users();
            user.setUsername(requestDTO.getUsername());
            user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
            user.setEmail(requestDTO.getEmail());
            user.setActive(true);
            logger.debug("User object created for: {}", requestDTO.getUsername());
            String rolesStr = requestDTO.getRoles();
            Set<Roles> roles = new HashSet<>();
            if(rolesStr!= null && !rolesStr.isEmpty())
            {
                logger.debug("Processing roles for user {}: {}", requestDTO.getUsername(), rolesStr);
                roles = Arrays.stream(rolesStr.split(","))
                        .map(String::trim)
                        .map(roleName -> {
                            logger.debug("Looking up role: {}", roleName);
                            return rolesRepository.findByName(roleName)
                                    .orElseThrow(()->{
                                        logger.error("Role not found: {} for user: {}", roleName, requestDTO.getUsername());
                                        return new RuntimeException("error Role not found roleName: "+ roleName);
                                    });
                        })
                        .collect(Collectors.toSet());
            }
            user.setRoles(roles);

            updateCache(user, 3600L);

            userRepository.save(user);


            logger.info("User successfully registered: {}", requestDTO.getUsername());
            AuditLogger.logDataModification(requestDTO.getUsername(), "User", user.getId() != null ? user.getId().toString() : "new", "CREATE", clientIP);
        } catch (Exception e) {
            logger.error("Error registering user {}: {}", requestDTO.getUsername(), e.getMessage());
            AuditLogger.logError(requestDTO.getUsername(), "user_registration", e.getMessage(), clientIP);
            throw e;
        }

    }

    public Users getUser(String email, String username) {

        String keyStr = null;
        if(redisTemplate.hasKey("user:email:"+email))
        {
            keyStr  = (String) redisTemplate.opsForValue().get("user:email:"+email);

//            return (Users) redisTemplate.opsForValue().get(id);
        }
        if(redisTemplate.hasKey("user:username:"+username))
        {
            keyStr = (String) redisTemplate.opsForValue().get("user:username:"+username);

//            return (Users) redisTemplate.opsForValue().get(id);
        }
        if(keyStr != null)
        {

            return (Users) redisTemplate.opsForValue().get(keyStr);
        }

        return null;
    }

    public void updateCache(Users user, Long ttlInMili)
    {
        redisTemplate.opsForValue().set("user:email:"+user.getEmail(),String.valueOf(user.getId()), ttlInMili, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set("user:username:"+user.getUsername(), String.valueOf(user.getId()), ttlInMili, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set("user:id:"+user.getId(), user, ttlInMili, TimeUnit.MILLISECONDS);

    }

    public void removeCache(String username)
    {
        Users user = getUser(null, username);
        if(user == null)
        {
            logger.error("User Not present");
            return;
        }
        redisTemplate.delete("user:email:"+user.getEmail());
        redisTemplate.delete("user:username:"+user.getUsername());
        redisTemplate.delete("user:id:"+user.getId());
    }

    public ResponseDTO loadUserDetailsForSecurity()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserStr = authentication.getName();
        String clientIP = MDC.get("clientIp");
        logger.info("Loading user details for: {}", currentUserStr);
        Optional<Users> user = userRepository.findByUsername(currentUserStr);
        if(user.isEmpty())
        {
            logger.error("User details not found for: {}", currentUserStr);
            AuditLogger.logError(currentUserStr, "load_user_details", "User not found", clientIP);
            throw new RuntimeException("User not found");
        }
        Users currentUser = user.get();
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setUsername(currentUser.getUsername());
        responseDTO.setEmail(currentUser.getEmail());
        String roleNames = currentUser.getRoles().stream()
                .map(role -> role.getName().toString()) // Assumes Roles has a getName() method
                .collect(Collectors.joining(", "));
        responseDTO.setRoles(roleNames);
        responseDTO.setCreatedAt(currentUser.getCreatedAt());
        responseDTO.setLastUpdated(currentUser.getUpdatedAt());
        
        logger.debug("User details loaded successfully for: {}", currentUserStr);
        AuditLogger.logUserProfileAccess(currentUserStr, clientIP);
        return responseDTO;
    }

}
