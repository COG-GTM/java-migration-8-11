package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 
 * Spring security denied access to h2-console.
 * This configuration will resolve 403 forbidden error when accessing h2-console.
 * 
 * Phase 2: Updated to use SecurityFilterChain (WebSecurityConfigurerAdapter deprecated in Spring Boot 2.7)
 * 
 * @author sbathina
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable()
            .headers().frameOptions().disable();
        
        return httpSecurity.build();
    }
}
