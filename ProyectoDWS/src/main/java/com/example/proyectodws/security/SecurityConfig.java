package com.example.proyectodws.security;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    RepositoryUserDetailsService userDetailsService;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        .requestMatchers(HttpMethod.GET, "/api/users/me").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/courses/").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/courses/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/courses/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/subjects/").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/subjects/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/subjects/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/comments/").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/comments/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/comments/**").hasRole("ADMIN")
                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll()
                );

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        /*http
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC PAGES
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/styles/**").permitAll()
                        .requestMatchers("/scripts/**").permitAll()
                        .requestMatchers("/courses/**").permitAll()
                        .requestMatchers("/course/**").permitAll()
                        .requestMatchers("/subjects/**").permitAll()
                        .requestMatchers("/subject/**").permitAll()
                        .requestMatchers("/aboutUS/**").permitAll()
                        .requestMatchers("/contact/**").permitAll()
                        .requestMatchers("/ds/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // PRIVATE PAGES
                        .requestMatchers("/profile").hasAnyRole("USER")
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
        */
        return http.build();
    }
}
