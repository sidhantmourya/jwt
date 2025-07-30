package com.example.jwt.service;


import com.example.jwt.dto.RegisterRequestDTO;
import com.example.jwt.dto.ResponseDTO;
import com.example.jwt.model.Roles;
import com.example.jwt.model.Users;
import com.example.jwt.repository.RolesRepository;
import com.example.jwt.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerNewUser(RegisterRequestDTO requestDTO)
    {
        if(userRepository.existsByUsername(requestDTO.getUsername()) || userRepository.existsByEmail(requestDTO.getEmail()))
        {
            throw new RuntimeException("User already exists");
        }

        Users user = new Users();
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEmail(requestDTO.getEmail());
        String rolesStr = requestDTO.getRoles();;
        Set<Roles> roles = new HashSet<>();
        if(rolesStr!= null && !rolesStr.isEmpty())
        {
            roles = Arrays.stream(rolesStr.split(","))
                    .map(String::trim)
                    .map(roleName -> rolesRepository.findByName(roleName)
                            .orElseThrow(()-> new RuntimeException("error Role not found roleName: "+ roleName)))
                    .collect(Collectors.toSet());
        }
        user.setRoles(roles);
        userRepository.save(user);

    }

    public ResponseDTO loadUserDetailsForSecurity()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserStr = authentication.getName();
        Optional<Users> user = userRepository.findByUsername(currentUserStr);
        if(user.isEmpty())
        {
            throw new RuntimeException("User not found");
        }
        Users currentUser = user.get();
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setUsername(currentUser.getUsername());
        responseDTO.setEmail(currentUser.getEmail());
        responseDTO.setRoles(currentUser.getRoles().toString());
        responseDTO.setCreatedAt(currentUser.getCreatedAt());
        responseDTO.setLastUpdated(currentUser.getUpdatedAt());

        return responseDTO;
    }

}
