package com.sams.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

/**
 * this filter runs on EVERY request to check for JWT tokens
 * extends OncePerRequestFilter so it only runs once per request (duh)
 *
 * basically: checks Authorization header -> validates token -> sets up security context
 * took a while to understand how spring security filters work tbh
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // constructor injection - spring will give us the JwtUtil bean
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        // look for the Authorization header
        String authHeader = request.getHeader("Authorization");

        // check if we have a bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // strip off the "Bearer " part - we just want the token
            String token = authHeader.substring(7);

            // make sure token is valid and not expired
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                // get user info from the token
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                // spring security needs the ROLE_ prefix for hasRole checks
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // create the auth token - password is null because we already validated
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        username,
                        null, // no password needed here
                        Collections.singletonList(authority)
                    );

                // add request details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // put it in the security context so other parts of app can access it
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // keep going with the filter chain
        filterChain.doFilter(request, response);
    }
}
