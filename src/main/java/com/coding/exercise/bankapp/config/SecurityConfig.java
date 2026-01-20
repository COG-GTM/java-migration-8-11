package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring security configuration.
 * This configuration allows access to H2 console and Swagger UI without authentication.
 * 
 * @author sbathina
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/").permitAll()
                        .antMatchers("/h2-console/**").permitAll()
                        .antMatchers("/swagger-ui/**").permitAll()
                        .antMatchers("/swagger-ui.html").permitAll()
                        .antMatchers("/v3/api-docs/**").permitAll()
                        .antMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic()
                .and()
                .csrf().disable()
                .headers().frameOptions().disable();
        
        return httpSecurity.build();
    }
}
