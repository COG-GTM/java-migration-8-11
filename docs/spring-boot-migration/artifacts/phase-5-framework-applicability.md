# Phase 5: Framework Upgrade — Applicability

Every detection command was run regardless of expected outcome. "NOT APPLICABLE" entries are backed by the same evidence as applicable ones.

| Sub-task | Detection | Applicable? | Action |
|----------|-----------|-------------|--------|
| Security: replace `WebSecurityConfigurerAdapter` | `grep -r WebSecurityConfigurerAdapter src` → `config/SecurityConfig.java` | **YES** | Rewrote as `SecurityFilterChain` `@Bean` |
| Security: update method names | `grep -r "antMatchers\|authorizeRequests" src` → `SecurityConfig.java` | **YES** | `antMatchers`→`requestMatchers`, `authorizeRequests`→`authorizeHttpRequests`, lambda DSL for `csrf`/`headers` |
| Actuator: endpoint path changes | `grep -r "management.endpoint\|/actuator" src application.yml` → none | No | none |
| Spring Batch: `JobBuilder`/`StepBuilder` | `grep -r "spring-batch\|JobBuilderFactory\|StepBuilderFactory"` → 0 | No | none |
| Auto-config: `spring.factories` → `AutoConfiguration.imports` | `find src -name spring.factories` → none | No | none |
| Observability: Micrometer Observation API | `grep -r "micrometer\|Observation\|@Timed"` → 0 | No | none |

## Security migration detail (`config/SecurityConfig.java`)

**Before (Spring Security 5 / Boot 2):**
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests().antMatchers("/").permitAll().and()
                .authorizeRequests().antMatchers("/h2-console/**").permitAll();
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();
    }
}
```

**After (Spring Security 6 / Boot 3):**
```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().permitAll())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));
        return httpSecurity.build();
    }
}
```

### Behavior-preservation note (`.anyRequest().permitAll()`)

The original config declared only two `permitAll` matchers and **no `anyRequest()` rule**, and configured no authentication entry point (no `httpBasic`/`formLogin`). Under Spring Security 5's `FilterSecurityInterceptor`, a request matching **no** authorization rule was **granted** (unmatched ⇒ permit). Spring Security 6's `AuthorizationFilter` **denies** unmatched requests by default. To preserve the original effective behavior (all endpoints reachable — this is an intentionally open demo app, per the class comment about resolving the h2-console 403), an explicit `.anyRequest().permitAll()` is added. This is the faithful SS5→SS6 translation, not a new policy. `csrf` and H2-console frame options remain disabled exactly as before.

Compilation of the remaining `javax.persistence` errors is addressed in Phase 6; after that a full compile validates this file.
