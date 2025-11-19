# Authentication and Authorization Flow

## Purpose
This document details the complete authentication and authorization implementation for SAMS using JWT (JSON Web Tokens) and Spring Security.

---

## Authentication Overview

**Authentication Method:** JWT (JSON Web Token)
**Security Framework:** Spring Security 6.x
**Password Hashing:** BCrypt
**Token Expiry:** 1 hour (access token), 7 days (refresh token)

---

## 1. User Login Flow (Detailed)

### Step-by-Step Process

```
┌─────────┐                 ┌──────────────┐                ┌──────────┐
│ Client  │                 │  Spring Boot │                │PostgreSQL│
│ (Vue.js)│                 │    Backend   │                │    DB    │
└────┬────┘                 └──────┬───────┘                └─────┬────┘
     │                             │                              │
     │ 1. POST /api/v1/auth/login  │                              │
     │    {username, password}     │                              │
     ├────────────────────────────>│                              │
     │                             │                              │
     │                             │ 2. SELECT * FROM users       │
     │                             │    WHERE username = ?        │
     │                             ├─────────────────────────────>│
     │                             │                              │
     │                             │ 3. User record               │
     │                             │<─────────────────────────────┤
     │                             │                              │
     │                             │ 4. BCrypt.matches(           │
     │                             │      inputPassword,          │
     │                             │      storedHash)             │
     │                             │                              │
     │                             │ 5. Generate JWT token        │
     │                             │    with user claims          │
     │                             │                              │
     │                             │ 6. UPDATE users              │
     │                             │    SET last_login = NOW()    │
     │                             ├─────────────────────────────>│
     │                             │                              │
     │ 7. {token, user info}       │                              │
     │<────────────────────────────┤                              │
     │                             │                              │
     │ 8. Store token in           │                              │
     │    localStorage             │                              │
     │                             │                              │
```

### Implementation Details

#### 1.1 Login Endpoint (Controller)

```java
// AuthController.java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.authenticateUser(request);

        return ResponseEntity.ok(
            ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build()
        );
    }
}
```

#### 1.2 Login Request DTO

```java
// LoginRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

#### 1.3 Login Response DTO

```java
// LoginResponse.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tokenType;
    private Integer expiresIn; // seconds
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private Integer userId;
        private String username;
        private String email;
        private UserRole role;
        private String firstName;
        private String lastName;
    }
}
```

#### 1.4 Authentication Service

```java
// AuthService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse authenticateUser(LoginRequest request) {
        log.info("Authentication attempt for username: {}", request.getUsername());

        // Find user by username
        User user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername())
            .orElseThrow(() -> {
                log.warn("Authentication failed: User not found - {}", request.getUsername());
                return new InvalidCredentialsException("Invalid username or password");
            });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Authentication failed: Invalid password for user - {}", request.getUsername());
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Check if account is active
        if (!user.getIsActive()) {
            log.warn("Authentication failed: Account inactive - {}", request.getUsername());
            throw new AccountInactiveException("Your account has been deactivated");
        }

        log.info("Authentication successful for user: {}", request.getUsername());

        // Generate JWT token
        String token = jwtTokenProvider.createToken(
            user.getUserId(),
            user.getUsername(),
            user.getRole()
        );

        // Update last login timestamp (async to not block response)
        updateLastLogin(user);

        // Build response
        return LoginResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getTokenExpirationSeconds())
            .user(LoginResponse.UserInfo.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build())
            .build();
    }

    @Async
    private void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}
```

#### 1.5 JWT Token Provider

```java
// JwtTokenProvider.java
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationMs;

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLE = "role";

    /**
     * Creates JWT token with user claims
     */
    public String createToken(Integer userId, String username, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
            .setSubject(username)
            .claim(CLAIM_USER_ID, userId)
            .claim(CLAIM_ROLE, role.name())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    /**
     * Extracts username from token
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    /**
     * Extracts user ID from token
     */
    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        return claims.get(CLAIM_USER_ID, Integer.class);
    }

    /**
     * Extracts user role from token
     */
    public UserRole getUserRoleFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();

        String roleString = claims.get(CLAIM_ROLE, String.class);
        return UserRole.valueOf(roleString);
    }

    /**
     * Validates JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public int getTokenExpirationSeconds() {
        return jwtExpirationMs / 1000;
    }
}
```

#### 1.6 Password Encoder Configuration

```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt with strength 12 (2^12 = 4096 iterations)
        return new BCryptPasswordEncoder(12);
    }

    // Other security configurations...
}
```

---

## 2. Protected Endpoint Request Flow

### Step-by-Step Process

```
┌─────────┐                 ┌──────────────┐                ┌──────────┐
│ Client  │                 │  Spring Boot │                │PostgreSQL│
└────┬────┘                 └──────┬───────┘                └─────┬────┘
     │                             │                              │
     │ 1. GET /api/v1/students/me  │                              │
     │    Authorization: Bearer    │                              │
     │    <JWT token>              │                              │
     ├────────────────────────────>│                              │
     │                             │                              │
     │                    ┌────────┴────────┐                     │
     │                    │ JWT Auth Filter │                     │
     │                    └────────┬────────┘                     │
     │                             │                              │
     │                             │ 2. Extract token from header │
     │                             │                              │
     │                             │ 3. Validate JWT signature    │
     │                             │    and expiration            │
     │                             │                              │
     │                             │ 4. Extract userId, role      │
     │                             │    from token claims         │
     │                             │                              │
     │                             │ 5. Set SecurityContext       │
     │                             │                              │
     │                    ┌────────┴────────┐                     │
     │                    │   Controller    │                     │
     │                    └────────┬────────┘                     │
     │                             │                              │
     │                             │ 6. @PreAuthorize checks role │
     │                             │                              │
     │                             │ 7. SELECT FROM students      │
     │                             │    WHERE student_id = ?      │
     │                             ├─────────────────────────────>│
     │                             │                              │
     │                             │ 8. Student data              │
     │                             │<─────────────────────────────┤
     │                             │                              │
     │ 9. {success, data}          │                              │
     │<────────────────────────────┤                              │
     │                             │                              │
```

### Implementation Details

#### 2.1 JWT Authentication Filter

```java
// JwtAuthenticationFilter.java
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String HEADER_NAME = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Extract JWT token from Authorization header
            String jwt = extractJwtFromRequest(request);

            if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
                // Extract user information from token
                Integer userId = jwtTokenProvider.getUserIdFromToken(jwt);
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                UserRole role = jwtTokenProvider.getUserRoleFromToken(jwt);

                // Create authentication object
                UserPrincipal userPrincipal = new UserPrincipal(userId, username, role);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null, // credentials (not needed, already authenticated)
                        List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                    );

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set authentication for user: {} (ID: {})", username, userId);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_NAME);

        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
```

#### 2.2 User Principal (Security Context)

```java
// UserPrincipal.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal {
    private Integer userId;
    private String username;
    private UserRole role;

    /**
     * Helper method to get authenticated user from SecurityContext
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }

        throw new UnauthorizedException("No authenticated user found");
    }

    /**
     * Helper to get current user ID
     */
    public static Integer getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**
     * Helper to get current user role
     */
    public static UserRole getCurrentUserRole() {
        return getCurrentUser().getRole();
    }
}
```

#### 2.3 Security Configuration

```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (not needed for stateless JWT auth)
            .csrf(csrf -> csrf.disable())

            // Enable CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Session management (stateless)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Exception handling
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/health").permitAll()

                // Admin-only endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // All other API endpoints require authentication
                .requestMatchers("/api/v1/**").authenticated()

                // Deny all other requests
                .anyRequest().denyAll()
            )

            // Add JWT filter before UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Vue.js dev server
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
```

#### 2.4 JWT Authentication Entry Point (Handles Auth Errors)

```java
// JwtAuthenticationEntryPoint.java
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        log.error("Unauthorized request: {}", authException.getMessage());

        // Return 401 Unauthorized with JSON error
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("UNAUTHORIZED")
                .message("Authentication required. Please provide a valid access token.")
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
```

---

## 3. Role-Based Authorization

### Method-Level Security with @PreAuthorize

```java
// StudentController.java
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * Get all students - Admin and Faculty only
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<ApiResponse<Page<StudentDTO>>> getAllStudents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // Implementation...
    }

    /**
     * Get student by ID - Admin, Faculty, or the student themselves
     */
    @GetMapping("/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY') or " +
                  "@studentService.isCurrentUser(#studentId)")
    public ResponseEntity<ApiResponse<StudentDTO>> getStudentById(
            @PathVariable Integer studentId
    ) {
        // Implementation...
    }

    /**
     * Update student profile - Only the student themselves
     */
    @PutMapping("/{studentId}")
    @PreAuthorize("@studentService.isCurrentUser(#studentId) or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentDTO>> updateStudent(
            @PathVariable Integer studentId,
            @Valid @RequestBody UpdateStudentRequest request
    ) {
        // Implementation...
    }
}
```

### Custom Authorization Logic

```java
// StudentService.java
@Service
@RequiredArgsConstructor
public class StudentService {

    /**
     * Checks if current authenticated user is the specified student
     * Used in @PreAuthorize expressions
     */
    public boolean isCurrentUser(Integer studentId) {
        try {
            Integer currentUserId = UserPrincipal.getCurrentUserId();
            return currentUserId.equals(studentId);
        } catch (UnauthorizedException ex) {
            return false;
        }
    }

    // Other service methods...
}
```

### Authorization Examples by Role

| Endpoint | STUDENT | FACULTY | ADMIN |
|----------|---------|---------|-------|
| GET /api/v1/students/me | ✅ (own) | ✅ (own) | ✅ (own) |
| GET /api/v1/students/{id} | ✅ (own only) | ✅ (all) | ✅ (all) |
| GET /api/v1/students | ❌ | ✅ | ✅ |
| POST /api/v1/enrollments | ✅ (self only) | ❌ | ✅ (any student) |
| POST /api/v1/grades | ❌ | ✅ (own courses) | ✅ (all) |
| POST /api/v1/courses | ❌ | ✅ | ✅ |
| POST /api/v1/payments/{id}/validate | ❌ | ❌ | ✅ |
| GET /api/v1/admin/statistics | ❌ | ❌ | ✅ |

---

## 4. Frontend Integration (Vue.js)

### 4.1 Axios Interceptor for JWT

```javascript
// src/services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 10000
});

// Request interceptor - Add JWT token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - Handle 401 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expired or invalid - redirect to login
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### 4.2 Login Component

```javascript
// src/views/Login.vue
<script>
import api from '@/services/api';

export default {
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      loading: false,
      error: null
    };
  },
  methods: {
    async handleLogin() {
      this.loading = true;
      this.error = null;

      try {
        const response = await api.post('/auth/login', this.loginForm);

        if (response.data.success) {
          // Store token and user info
          localStorage.setItem('accessToken', response.data.data.token);
          localStorage.setItem('user', JSON.stringify(response.data.data.user));

          // Redirect based on role
          const role = response.data.data.user.role;
          if (role === 'STUDENT') {
            this.$router.push('/student/dashboard');
          } else if (role === 'FACULTY') {
            this.$router.push('/faculty/dashboard');
          } else if (role === 'ADMIN') {
            this.$router.push('/admin/dashboard');
          }
        }
      } catch (error) {
        this.error = error.response?.data?.error?.message || 'Login failed';
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
```

### 4.3 Auth Store (Vuex)

```javascript
// src/store/modules/auth.js
export default {
  namespaced: true,
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('accessToken') || null
  },
  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: (state) => state.user,
    userRole: (state) => state.user?.role
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    },
    SET_TOKEN(state, token) {
      state.token = token;
      localStorage.setItem('accessToken', token);
    },
    LOGOUT(state) {
      state.user = null;
      state.token = null;
      localStorage.removeItem('user');
      localStorage.removeItem('accessToken');
    }
  },
  actions: {
    logout({ commit }) {
      commit('LOGOUT');
    }
  }
};
```

### 4.4 Route Guards

```javascript
// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/student/dashboard',
    name: 'StudentDashboard',
    component: () => import('@/views/StudentDashboard.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' }
  },
  {
    path: '/faculty/dashboard',
    name: 'FacultyDashboard',
    component: () => import('@/views/FacultyDashboard.vue'),
    meta: { requiresAuth: true, role: 'FACULTY' }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/AdminDashboard.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken');
  const user = JSON.parse(localStorage.getItem('user'));

  if (to.meta.requiresAuth) {
    if (!token) {
      // Not authenticated - redirect to login
      next({ name: 'Login', query: { redirect: to.fullPath } });
    } else if (to.meta.role && user.role !== to.meta.role) {
      // Wrong role - redirect to appropriate dashboard
      next({ name: `${user.role.toLowerCase()}Dashboard` });
    } else {
      // Authenticated and authorized
      next();
    }
  } else {
    // Public route
    next();
  }
});

export default router;
```

---

## 5. Security Best Practices

### 5.1 Password Security

**Requirements Enforced:**
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character

**Validation:**
```java
// PasswordValidator.java
public class PasswordValidator {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    public static boolean isValid(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static void validate(String password) {
        if (!isValid(password)) {
            throw new ValidationException(
                "Password must be at least 8 characters and contain uppercase, " +
                "lowercase, number, and special character"
            );
        }
    }
}
```

### 5.2 Token Security

**JWT Secret Configuration:**
```properties
# application.properties (production - use environment variables!)
jwt.secret=${JWT_SECRET:defaultSecretKeyForDevelopmentOnlyChangeInProduction}
jwt.expiration-ms=3600000  # 1 hour in milliseconds
jwt.refresh-expiration-ms=604800000  # 7 days
```

**Important:**
- Never commit JWT secret to version control
- Use strong, randomly generated secret (minimum 256 bits)
- Rotate secrets periodically in production
- Store in environment variables or secret management system (AWS Secrets Manager, Azure Key Vault)

### 5.3 HTTPS Enforcement

**Production Configuration:**
```java
// SecurityConfig.java (production profile)
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // Require HTTPS
        .requiresChannel(channel ->
            channel.anyRequest().requiresSecure()
        )

        // Other security configs...

    return http.build();
}
```

### 5.4 Rate Limiting (Prevent Brute Force)

```java
// RateLimitFilter.java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, List<Long>> requestCounts = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 5;  // max requests
    private static final long TIME_WINDOW = 60000;  // per minute

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Only rate limit login endpoint
        if (request.getRequestURI().contains("/auth/login")) {
            String clientIp = getClientIP(request);

            if (isRateLimited(clientIp)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many login attempts. Please try again later.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String clientIp) {
        long currentTime = System.currentTimeMillis();
        requestCounts.putIfAbsent(clientIp, new ArrayList<>());

        List<Long> timestamps = requestCounts.get(clientIp);

        // Remove old timestamps outside time window
        timestamps.removeIf(timestamp -> currentTime - timestamp > TIME_WINDOW);

        // Check if limit exceeded
        if (timestamps.size() >= MAX_REQUESTS) {
            return true;
        }

        // Add current request timestamp
        timestamps.add(currentTime);
        return false;
    }

    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
```

### 5.5 Audit Logging

```java
// AuditService.java
@Service
@Slf4j
public class AuditService {

    @Async
    public void logAuthenticationSuccess(String username, String ipAddress) {
        log.info("AUDIT: Successful login - Username: {}, IP: {}", username, ipAddress);
        // Store in audit_log table (future enhancement)
    }

    @Async
    public void logAuthenticationFailure(String username, String ipAddress, String reason) {
        log.warn("AUDIT: Failed login - Username: {}, IP: {}, Reason: {}",
            username, ipAddress, reason);
        // Store in audit_log table and alert on repeated failures
    }

    @Async
    public void logSensitiveAction(String action, Integer userId, String details) {
        log.info("AUDIT: Sensitive action - Action: {}, User: {}, Details: {}",
            action, userId, details);
        // Examples: password change, payment validation, grade modification
    }
}
```

---

## 6. Error Handling

### Authentication Exceptions

```java
// Custom exceptions
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException(String message) {
        super(message);
    }
}

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
```

### Global Exception Handler

```java
// GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("INVALID_CREDENTIALS")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("UNAUTHORIZED")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("INSUFFICIENT_PERMISSIONS")
                .message("You do not have permission to perform this action")
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}
```

---

## 7. Refresh Token Flow (Optional - Future Enhancement)

```java
// RefreshTokenService.java
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshTokenExpirationMs;

    /**
     * Creates refresh token for user
     */
    @Transactional
    public String createRefreshToken(Integer userId) {
        // Generate random token
        String token = UUID.randomUUID().toString();

        // Create refresh token entity
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpirationMs));

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    /**
     * Validates refresh token and generates new access token
     */
    @Transactional
    public String refreshAccessToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenString)
            .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        // Check expiration
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("Refresh token expired");
        }

        // Get user
        User user = userRepository.findById(refreshToken.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User", refreshToken.getUserId()));

        // Generate new access token
        String newAccessToken = jwtTokenProvider.createToken(
            user.getUserId(),
            user.getUsername(),
            user.getRole()
        );

        return newAccessToken;
    }
}
```

---

**Document Status:** Complete
**Authentication Method:** JWT with Spring Security
**Security Level:** Production-ready with best practices
**Next Step:** Error handling standards documentation
