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

/**
 * Security configuration for the whole app
 * this was a pain to setup - spring security is confusing
 *
 * handles JWT auth, CORS, and endpoint permissions
 * DONT TOUCH THIS unless you really know what your doing
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // lets us use @PreAuthorize on methods
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // inject our custom JWT filter
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // password encoder - using bcrypt because its secure
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * main security filter chain - this is where all teh magic happens
     * took like 3 days to get this working properly lol
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable csrf for JWT - stateless so we dont need it
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // enable CORS for frontend
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // no sessions - using JWT tokens instead
            )
            .authorizeHttpRequests(auth -> auth
                // public stuff - anyone can access these
                .requestMatchers("/api/auth/login", "/api/auth/validate").permitAll()

                // static files - html css js etc
                .requestMatchers("/", "/index.html", "/*.html", "/css/**", "/js/**", "/images/**").permitAll()

                // websocket endpoints - the authentication is handeled seperately
                .requestMatchers("/ws/**").permitAll() // initial ws connection
                .requestMatchers("/app/**").authenticated()
                .requestMatchers("/topic/**", "/queue/**").authenticated()

                // admin only stuff - dont let regular users access this
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // attendance - students can see their own, faculty and admin see all
                .requestMatchers("/api/attendance/user/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/attendance/statistics/user/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/attendance/percentage/**").hasAnyRole("STUDENT", "FACULTY", "ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/attendance/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "FACULTY")

                // dashboard and reports - admin only
                .requestMatchers("/api/dashboard/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers("/api/reports/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // system health stuff
                .requestMatchers("/api/system/**").hasAnyRole("ADMIN", "SUPER_ADMIN")

                // regular protected endpoints - all authenticated users
                // this is getting long but idk how else to do it
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

                // catch all - need to be logged in
                .anyRequest().authenticated()
            )
            // add our JWT filter - has to be before the default one
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS config so frontend can talk to backend
     * had issues with this in chrome - make sure to update origins if needed
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // allowed origins - add your frontend URL here if not in list
        // kept adding ports because vite keeps switching lol
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:8080",  // spring boot
            "http://localhost:3000",  // react
            "http://localhost:4200",  // angular (we dont use this but just in case)
            "http://localhost:8081",  // vue
            "http://localhost:5173",  // vite default
            "http://localhost:5174",  // vite when 5173 is busy
            "http://localhost:5175",  // happens sometimes
            "http://localhost:5176"   // just to be safe
        ));

        // all teh http methods we need
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // headers - Authorization is the important one for JWT
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "Cache-Control"
        ));

        configuration.setAllowCredentials(true); // need this for cookies n stuff

        // let frontend see the auth header
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        // apply to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
