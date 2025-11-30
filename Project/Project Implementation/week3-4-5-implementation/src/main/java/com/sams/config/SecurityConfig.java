package com.sams.config;

import com.sams.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize annotations
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable CSRF for stateless JWT
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // enable CORS
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no sessions, use JWT
            )
            .authorizeHttpRequests(auth -> auth
                // public endpoints - no authentication required
                .requestMatchers("/api/auth/login", "/api/auth/validate").permitAll() // login and token validation only

                // allow static resources (HTML, CSS, JS) without authentication
                .requestMatchers("/", "/index.html", "/*.html", "/css/**", "/js/**", "/images/**").permitAll()

                // WebSocket endpoints - require authentication (handled at method level with @PreAuthorize)
                .requestMatchers("/ws/**").permitAll() // WebSocket endpoint for initial connection
                .requestMatchers("/app/**").authenticated() // Application destination prefix for messages
                .requestMatchers("/topic/**", "/queue/**").authenticated() // Message broker destinations

                // admin endpoints - only ADMIN and SUPER_ADMIN roles
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // attendance endpoints - students can view their own attendance
                .requestMatchers("/api/attendance/user/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/attendance/statistics/user/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/attendance/percentage/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/attendance/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "FACULTY")

                // dashboard analytics endpoints - ADMIN and SUPER_ADMIN
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // reports endpoints - ADMIN and SUPER_ADMIN
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // system metrics and health endpoints - ADMIN and SUPER_ADMIN only
                .requestMatchers("/api/system/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // protected endpoints - authentication required
                .requestMatchers("/api/users/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/courses/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/enrollments/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/grades/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/payments/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/files/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/degree-progress/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/notifications/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/messages/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/connections/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/study-groups/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/assignments/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/submissions/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")

                // all other API requests require authentication
                .anyRequest().authenticated()
            )
            // add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS configuration - allow frontend to access backend APIs
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // allow requests from these origins (update with your frontend URLs)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080",  // Spring Boot webapp (our frontend!)
            "http://localhost:3000",  // React default
            "http://localhost:4200",  // Angular default
            "http://localhost:8081",  // Vue default
            "http://localhost:5173",  // Vite default
            "http://localhost:5174",  // Vite alternate port
            "http://localhost:5175",  // Vite alternate port 2
            "http://localhost:5176"   // Vite alternate port 3
        ));

        // allow these HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // allow these headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Cache-Control"
        ));

        // allow credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        // expose these headers to frontend
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
