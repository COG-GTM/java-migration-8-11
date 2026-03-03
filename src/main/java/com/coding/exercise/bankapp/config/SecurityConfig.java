package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration using the modern SecurityFilterChain bean approach.
 *
 * <p>Migrated from the deprecated {@code WebSecurityConfigurerAdapter} pattern
 * (deprecated in Spring Security 5.7+) to an explicit {@code SecurityFilterChain}
 * bean. This approach is supported from Spring Security 5.4+ / Spring Boot 2.4+.</p>
 *
 * <p>The H2 console requires CSRF to be disabled and frame options to be turned off
 * so that it can render its internal frames correctly.</p>
 *
 * @author sbathina
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorize -> authorize
                .antMatchers("/").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic()
            .and()
            .csrf().disable()
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}
