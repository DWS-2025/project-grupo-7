package com.example.proyectodws.security;

import com.example.proyectodws.security.jwt.AccessDeniedHandlerJwt;
import com.example.proyectodws.security.jwt.JwtRequestFilter;
import com.example.proyectodws.security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import java.util.Base64;
import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

    @Autowired
    private AccessDeniedHandlerJwt accessDeniedHandlerJwt;

    private final SecureRandom secureRandom = new SecureRandom();

    private String generateNonce() {
        byte[] nonceBytes = new byte[32];
        secureRandom.nextBytes(nonceBytes);
        return Base64.getEncoder().encodeToString(nonceBytes);
    }

    private HeaderWriter cspHeaderWriter() {
        return (request, response) -> {
            String nonce = generateNonce();
            request.setAttribute("cspNonce", nonce);
            response.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                            "script-src 'self' 'nonce-" + nonce + "'; " +
                            "style-src 'self'; " +
                            "img-src 'self' data:; " +
                            "font-src 'self'; " +
                            "frame-ancestors 'none'; " +
                            "form-action 'self'; " +
                            "base-uri 'self'; " +
                            "object-src 'none'");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        // Add HSTS and CSP headers for API endpoints
        http.headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                        .includeSubDomains(true)
                        .preload(true)
                        .maxAgeInSeconds(31536000)) // 1 year
                .addHeaderWriter(cspHeaderWriter())
        );

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(unauthorizedHandlerJwt)
                        .accessDeniedHandler(accessDeniedHandlerJwt))
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC ENDPOINTS
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/*/video").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/subjects").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/subjects/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/subjects/*/image").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/courses/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/subjects/*").permitAll()

                        // USER ENDPOINTS
                        .requestMatchers(HttpMethod.GET, "/api/users/me").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/courses/*/enroll").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/*/unenroll").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/comments/course/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/comments").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/comments/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/comments/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/profile").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/profile").hasAnyRole("USER")/// admin cant delete him/her profile
                        .requestMatchers(HttpMethod.GET, "/api/users/*/image").hasAnyRole("USER", "ADMIN")

                        // ADMIN ENDPOINTS
                        .requestMatchers(HttpMethod.POST, "/api/courses").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/courses/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/courses/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/subjects").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/subjects/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/subjects/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/*").hasRole("ADMIN")

                        // Deny all other API requests
                        .anyRequest().denyAll()
                );

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // Add JWT Token filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        // Add HSTS and CSP headers for web endpoints
        http.headers(headers -> headers
                .httpStrictTransportSecurity(hsts -> hsts
                        .includeSubDomains(true)
                        .preload(true)
                        .maxAgeInSeconds(31536000)) // 1 year
                .addHeaderWriter(cspHeaderWriter())
        );

        http
                .securityMatcher("/**")
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC PAGES
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/videos/**").permitAll()
                        .requestMatchers("/styles/**").permitAll()
                        .requestMatchers("/scripts/**").permitAll()
                        .requestMatchers("/courses").permitAll()
                        .requestMatchers("/subjects").permitAll()
                        .requestMatchers("/subject/*").permitAll()
                        .requestMatchers("/subject/*/courses").permitAll()
                        .requestMatchers("/course/*/image").permitAll()
                        .requestMatchers("/course/*").permitAll()
                        .requestMatchers("/subject/*/image").permitAll()
                        .requestMatchers("/about-us").permitAll()
                        .requestMatchers("/contact").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/loginerror").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/error").permitAll()

                        // USER PAGES
                        .requestMatchers("/enrolled_courses").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/course/*/enroll").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/course/*/unenroll").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/course/*/comments/new").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/course/*/comments/*/delete").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/course/*/comments/*/edit").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/course/*/comments/*/update").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/users/*/image").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/profile").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/profile/**").hasAnyRole("USER", "ADMIN")

                        // ADMIN PAGES
                        .requestMatchers("/courses/new").hasRole("ADMIN")
                        .requestMatchers("/courses/saved").hasRole("ADMIN")
                        .requestMatchers("/course/*/edit").hasRole("ADMIN")
                        .requestMatchers("/course/*/edited_course").hasRole("ADMIN")
                        .requestMatchers("/course/*/enrolledStudents").hasRole("ADMIN")
                        .requestMatchers("/course/*/delete").hasRole("ADMIN")
                        .requestMatchers("/subjects/new").hasRole("ADMIN")
                        .requestMatchers("/subjects/saved").hasRole("ADMIN")
                        .requestMatchers("/subject/*/edit").hasRole("ADMIN")
                        .requestMatchers("/subject/*/delete").hasRole("ADMIN")
                        .requestMatchers("/users").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        http.sessionManagement(management -> management
                .sessionFixation().migrateSession()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        );

        // Configure cookie settings
        http.rememberMe(remember -> remember
                .alwaysRemember(false)
                .useSecureCookie(true)
        );

        return http.build();
    }
}
