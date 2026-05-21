package com.coding.exercise.bankapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 
 * Spring security denied access to h2-console.
 * This configuration will resolve 403 forbidden error when accessing h2-console.
 * 
 * @author sbathina
 *
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/dashboard/**").permitAll()
                .antMatchers("/css/**", "/js/**").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated().and().httpBasic();

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
}
