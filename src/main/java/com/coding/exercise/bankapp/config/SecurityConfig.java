package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring security configuration to allow access to h2-console and API endpoints.
 *
 * NOTE: WebSecurityConfigurerAdapter is deprecated in Spring Security 5.7+ (Spring Boot 2.7+).
 * When upgrading to Spring Boot 2.7+, refactor this class to use a SecurityFilterChain @Bean instead:
 *
 * <pre>
 * {@code
 * @Configuration
 * @EnableWebSecurity
 * public class SecurityConfig {
 *     @Bean
 *     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *         http.authorizeHttpRequests(auth -> auth
 *                 .requestMatchers("/", "/h2-console/**").permitAll()
 *                 .anyRequest().authenticated()
 *             )
 *             .csrf(csrf -> csrf.disable())
 *             .headers(headers -> headers.frameOptions(frame -> frame.disable()))
 *             .httpBasic(Customizer.withDefaults());
 *         return http.build();
 *     }
 * }
 * }
 * </pre>
 *
 * @author sbathina
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll().and()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll();

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
}
