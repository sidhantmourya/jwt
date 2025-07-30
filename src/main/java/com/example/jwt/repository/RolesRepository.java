package com.example.jwt.repository;

import com.example.jwt.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer> {

    Optional<Roles> findByName(String roleName);
}
