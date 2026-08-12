package com.repairworkshop.technician.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").permitAll()
                // Internal feign call endpoint — accessible to any authenticated user
                .requestMatchers(HttpMethod.GET, "/api/technicians/available").permitAll()
                // Read — USER or ADMIN
                .requestMatchers(HttpMethod.GET, "/api/technicians/**")
                    .hasAnyRole("USER", "ADMIN")
                // Create, Update, Delete — ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/technicians/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/technicians/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/technicians/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/technicians/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
