package com.example.jwt.repository;

import com.example.jwt.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("Select count(u)>0 from Users u where u.email = :email or u.username = :username")
    boolean existsByEmailOrUsername(@Param("email") String email, @Param("username") String username);


}
