package com.example.jwt.config;

import com.example.jwt.filter.JwtAuthFilter;
import com.example.jwt.handler.AuthEntryPoint;
import com.example.jwt.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AuthEntryPoint authEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
         return http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth.requestMatchers(
                            "/api/auth/login",
                                    "/api/auth/register",
                                    "/actuator/health")
                    .permitAll().anyRequest().authenticated())
                         .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                         .exceptionHandling(ex -> ex
                                 .authenticationEntryPoint(authEntryPoint)
                                 .accessDeniedHandler(accessDeniedHandler))
                    .build();



    }
}
