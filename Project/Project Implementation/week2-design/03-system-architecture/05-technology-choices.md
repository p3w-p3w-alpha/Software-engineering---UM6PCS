# SAMS Technology Choices Justification

## Purpose
This document justifies the selection of each technology in the SAMS stack, explaining why it was chosen over alternatives and how it meets project requirements.

---

## Technology Stack Overview

```
┌────────────────────────────────────────────────────┐
│              FRONTEND (Client-Side)                │
│  Vue.js 3.3+ │ Vuex/Pinia │ Vue Router │ Axios    │
│  Element Plus UI │ Vite Build Tool               │
└────────────────────────────────────────────────────┘
                       ↕ REST API
┌────────────────────────────────────────────────────┐
│              BACKEND (Server-Side)                 │
│  Java 17 LTS │ Spring Boot 3.x │ Spring Security  │
│  Spring Data JPA │ Hibernate │ JWT (jjwt)        │
│  Maven │ Lombok                                    │
└────────────────────────────────────────────────────┘
                       ↕ JDBC
┌────────────────────────────────────────────────────┐
│                DATABASE                            │
│  PostgreSQL 13+ │ HikariCP Connection Pool        │
└────────────────────────────────────────────────────┘
```

---

## FRONTEND TECHNOLOGIES

## 1. Vue.js 3.3+ (JavaScript Framework)

### Why Vue.js?

**Primary Reasons:**
1. **Progressive Framework**: Can start simple and scale up
2. **Gentle Learning Curve**: Easier than React or Angular for new developers
3. **Excellent Documentation**: Comprehensive official docs
4. **Strong Community**: Large ecosystem, many plugins
5. **Performance**: Virtual DOM, efficient reactivity system
6. **Composition API**: Modern, flexible component composition
7. **Single File Components**: HTML, CSS, JS in one `.vue` file

**Comparison with Alternatives:**

| Feature | Vue.js 3 | React 18 | Angular 15 |
|---------|----------|----------|------------|
| **Learning Curve** | Easy | Medium | Steep |
| **Bundle Size** | Small (~33KB) | Medium (~42KB) | Large (~167KB) |
| **Documentation** | Excellent | Good | Excellent |
| **TypeScript Support** | Good (optional) | Excellent | Required |
| **State Management** | Vuex/Pinia | Redux/Context | NgRx |
| **CLI/Tooling** | Vue CLI, Vite | Create React App | Angular CLI |
| **Corporate Backing** | Independent | Meta (Facebook) | Google |
| **Job Market** | Growing | Largest | Strong (enterprise) |

**Decision Factors for SAMS:**

✅ **Matches Team Skill Level**
- Project developed by students learning web development
- Vue.js easier to learn than React or Angular
- Lower cognitive load (template syntax vs JSX)

✅ **Meets Performance Requirements**
- NFR-1.1 (Response time < 3 seconds): Vue's reactivity system is fast
- Small bundle size = faster initial load
- Virtual DOM minimizes unnecessary re-renders

✅ **Rapid Development**
- Single File Components speed up development
- Large ecosystem of UI libraries (Element Plus, Vuetify)
- Built-in directives reduce boilerplate (`v-if`, `v-for`, `v-model`)

✅ **Future-Proof**
- Active development (Vue 3 released 2020, stable)
- Composition API aligns with modern JavaScript patterns
- Strong community support

**Alternative Considered:**
- **React**: Rejected due to steeper learning curve (JSX, hooks complexity)
- **Angular**: Rejected due to verbosity and steep learning curve
- **Svelte**: Too new, smaller ecosystem

**SRS Requirements Satisfied:**
- NFR-2.1 (Cross-Platform Compatibility): Runs in all modern browsers
- NFR-1.1 (Performance): Fast rendering, small bundle
- NFR-5.4 (Maintainability): Clear component structure

---

## 2. Vuex / Pinia (State Management)

### Why Vuex/Pinia?

**Vuex:**
- Official state management for Vue 2/3
- Well-established pattern (actions, mutations, state, getters)
- DevTools integration
- Good for complex state management

**Pinia (Newer Alternative):**
- Official recommendation for Vue 3
- Simpler API than Vuex
- Better TypeScript support
- No mutations (just actions)
- Lightweight

**Decision: Start with Vuex, migrate to Pinia later**

**Use Cases in SAMS:**
```javascript
// Auth state (shared across all components)
store.state.auth.user
store.state.auth.token

// Student state
store.state.student.profile
store.state.student.enrollments
store.state.student.grades

// Course state
store.state.courses.list
store.state.courses.filters
```

**Benefits:**
- Centralized state (single source of truth)
- Predictable state changes
- Easy debugging (DevTools timeline)
- Persistent state (localStorage integration)

---

## 3. Vue Router 4.x (Client-Side Routing)

### Why Vue Router?

**Key Features:**
- Official routing library for Vue
- Dynamic route matching
- Navigation guards (auth protection)
- Lazy loading (code splitting)
- History mode (clean URLs)

**SAMS Routing Structure:**
```javascript
routes: [
  { path: '/login', component: Login },
  {
    path: '/student',
    component: StudentLayout,
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      { path: 'dashboard', component: StudentDashboard },
      { path: 'courses', component: CourseRegistration },
      { path: 'grades', component: GradesView }
    ]
  },
  { path: '/faculty', ... },
  { path: '/admin', ... }
]
```

**Navigation Guards:**
```javascript
router.beforeEach((to, from, next) => {
  // Check authentication
  // Verify role-based access
  // Redirect if needed
});
```

**Benefits:**
- SPA experience (no page reloads)
- Role-based route protection
- Lazy loading for performance
- Browser back/forward support

---

## 4. Axios (HTTP Client)

### Why Axios?

**Advantages over Fetch API:**
- Automatic JSON transformation
- Request/response interceptors
- Timeout support
- Better error handling
- Request cancellation
- Progress tracking (file uploads)

**SAMS Axios Configuration:**
```javascript
const api = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
});

// Request interceptor: Add JWT token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor: Handle errors
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Redirect to login
    }
    return Promise.reject(error);
  }
);
```

**Benefits:**
- Centralized error handling
- Automatic token injection
- Consistent API calls across app
- Easy request mocking for testing

---

## 5. Element Plus (UI Component Library)

### Why Element Plus?

**Comparison:**

| Library | Pros | Cons | Decision |
|---------|------|------|----------|
| **Element Plus** | Complete components, good docs, Vue 3 native | Opinionated design | ✅ Chosen |
| Vuetify | Material Design, mature | Vue 2 focused | ❌ Rejected |
| Ant Design Vue | Enterprise-grade, rich components | Complex API | ❌ Rejected |
| Quasar | Full framework, many features | Heavyweight | ❌ Rejected |
| Bootstrap Vue | Familiar, widely used | Not Vue 3 ready | ❌ Rejected |

**Element Plus Components Used:**
- `el-form`, `el-input`, `el-button`: Forms
- `el-table`, `el-pagination`: Data tables
- `el-dialog`, `el-drawer`: Modals
- `el-notification`, `el-message`: Alerts
- `el-menu`, `el-dropdown`: Navigation
- `el-card`, `el-tabs`: Layout

**Benefits:**
- Accelerates development (pre-built components)
- Consistent design system
- Accessibility built-in
- Responsive by default
- Customizable theme

---

## 6. Vite (Build Tool)

### Why Vite?

**Advantages over Webpack:**
- **Lightning-fast HMR**: Hot Module Replacement in milliseconds
- **Instant server start**: No bundling during development
- **Optimized builds**: Rollup for production
- **Native ES modules**: Modern browser support
- **Simple configuration**: Less boilerplate than Webpack

**Performance Comparison:**

| Metric | Vite | Webpack (Vue CLI) |
|--------|------|-------------------|
| Dev server start | <1 second | 5-10 seconds |
| Hot reload | ~50ms | ~500ms |
| Build time | Faster (Rollup) | Slower |
| Config complexity | Low | Medium-High |

**Vite Configuration (vite.config.js):**
```javascript
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
```

**Benefits:**
- Faster development iteration
- Better developer experience
- Modern tooling (ESM, esbuild)
- Future-proof

---

## BACKEND TECHNOLOGIES

## 7. Java 17 LTS (Programming Language)

### Why Java 17?

**Java Version Evolution:**
- Java 8 (2014): LTS, widely used
- Java 11 (2018): LTS, modern features
- **Java 17 (2021): LTS, latest stable** ← Chosen
- Java 21 (2023): LTS, very new

**Java 17 Features Used in SAMS:**
- **Records**: Immutable DTOs `record LoginRequest(String username, String password)`
- **Text Blocks**: Multi-line SQL/JSON strings
- **Pattern Matching**: Enhanced `instanceof` checks
- **Sealed Classes**: Restricted inheritance for security
- **NullPointerException improvements**: Better error messages

**Why Java over Alternatives?**

| Language | Pros | Cons | Decision |
|----------|------|------|----------|
| **Java** | Mature, extensive libraries, Spring ecosystem | Verbose | ✅ Chosen |
| Python | Rapid development, simple syntax | Slower, dynamic typing | ❌ |
| C# (.NET) | Similar to Java, good tooling | Windows-centric | ❌ |
| Kotlin | Modern, concise, JVM-compatible | Smaller community | ❌ |
| Go | Fast, simple, good concurrency | Less mature ecosystem | ❌ |
| Node.js | JavaScript everywhere, fast I/O | Callback hell, less structured | ❌ |

**Java Advantages for SAMS:**
1. **Strong Typing**: Catches errors at compile-time
2. **Spring Ecosystem**: Comprehensive, battle-tested frameworks
3. **Enterprise-Ready**: Used by 90% of Fortune 500
4. **Performance**: JVM optimization, efficient memory management
5. **Security**: Built-in security features, mature libraries
6. **Tooling**: IntelliJ IDEA, Eclipse, excellent debugging
7. **Community**: Massive community, extensive documentation
8. **Job Market**: High demand for Java/Spring developers

**SRS Requirements Satisfied:**
- NFR-1.1 (Performance): JVM performance optimizations
- NFR-4.1 (Security): Java security features
- NFR-5.1 (Code Quality): Strong typing enforces quality
- NFR-5.4 (Maintainability): Clear structure, OOP principles

---

## 8. Spring Boot 3.x (Application Framework)

### Why Spring Boot?

**Spring Boot Benefits:**
1. **Auto-Configuration**: Convention over configuration
2. **Embedded Server**: No need for external Tomcat
3. **Production-Ready**: Actuator for monitoring
4. **Dependency Injection**: Loose coupling, testability
5. **Comprehensive Ecosystem**: Data, Security, Cloud, etc.
6. **Rapid Development**: Starter dependencies, CLI

**Spring Boot vs Alternatives:**

| Framework | Pros | Cons | Decision |
|-----------|------|------|----------|
| **Spring Boot** | Comprehensive, mature, extensive docs | Heavyweight | ✅ Chosen |
| Micronaut | Faster startup, lower memory | Less mature | ❌ |
| Quarkus | Native compilation, fast | Newer ecosystem | ❌ |
| Jakarta EE | Standard, enterprise | Complex setup | ❌ |
| Vert.x | Reactive, fast | Steeper learning curve | ❌ |

**Spring Boot Starters Used:**
- `spring-boot-starter-web`: REST API, embedded Tomcat
- `spring-boot-starter-data-jpa`: JPA/Hibernate
- `spring-boot-starter-security`: Authentication/authorization
- `spring-boot-starter-validation`: Bean Validation
- `spring-boot-starter-test`: Testing framework

**Key Spring Boot Features in SAMS:**

**1. Auto-Configuration:**
```java
@SpringBootApplication  // Combines @Configuration, @EnableAutoConfiguration, @ComponentScan
public class SamsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SamsApplication.class, args);
    }
}
```

**2. Dependency Injection:**
```java
@Service
@RequiredArgsConstructor  // Lombok generates constructor injection
public class StudentService {
    private final StudentRepository studentRepository;  // Automatically injected
    private final EnrollmentRepository enrollmentRepository;
}
```

**3. Configuration Properties:**
```properties
# application.properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
jwt.secret=${JWT_SECRET}
jwt.expiration-ms=3600000
```

**Benefits:**
- Rapid development (less boilerplate)
- Production-ready features (health checks, metrics)
- Large community support
- Extensive documentation
- Easy testing

---

## 9. Spring Security 6.x (Authentication & Authorization)

### Why Spring Security?

**Features:**
- Authentication (login, JWT)
- Authorization (role-based access control)
- CSRF protection
- Session management
- Password encoding (BCrypt)
- Method-level security (`@PreAuthorize`)
- CORS configuration

**Spring Security Configuration:**
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // Stateless JWT
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/**").authenticated()
        )
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

**Alternatives Considered:**
- **Custom Auth**: Rejected (reinventing the wheel, security risks)
- **OAuth 2.0 (Keycloak)**: Too complex for Phase 1
- **Apache Shiro**: Less integrated with Spring

**Benefits:**
- Industry-standard security
- Well-tested (mature library)
- Comprehensive features
- Easy integration with Spring Boot
- Active security patches

---

## 10. Spring Data JPA / Hibernate (ORM)

### Why JPA/Hibernate?

**ORM Benefits:**
- Object-Relational Mapping (Java objects ↔ Database tables)
- Automatic SQL generation
- Database vendor independence
- Caching (first-level, second-level)
- Lazy loading
- Relationship management

**Spring Data JPA Features:**
```java
public interface StudentRepository extends JpaRepository<Student, Integer> {
    // Method name generates SQL automatically
    Optional<Student> findByStudentNumber(String studentNumber);

    // Custom query
    @Query("SELECT s FROM Student s WHERE s.major = :major AND s.academicStanding = :standing")
    List<Student> findByMajorAndStanding(String major, AcademicStanding standing);
}
```

**Generated SQL (automatic):**
```sql
SELECT * FROM students WHERE student_number = ?;
SELECT * FROM students WHERE major = ? AND academic_standing = ?;
```

**Alternatives:**

| Technology | Pros | Cons | Decision |
|------------|------|------|----------|
| **JPA/Hibernate** | Mature, comprehensive, Spring integrated | Complex, performance overhead | ✅ Chosen |
| MyBatis | More SQL control, lighter | More boilerplate | ❌ |
| JDBC Template | Full SQL control, fast | No ORM, manual mapping | ❌ |
| jOOQ | Type-safe SQL, good performance | Steep learning curve | ❌ |

**Benefits for SAMS:**
- Rapid development (no SQL writing for 80% of queries)
- Type-safe queries
- Automatic relationship handling
- Transaction management
- Database portability (PostgreSQL → MySQL easy migration)

---

## 11. PostgreSQL 13+ (Relational Database)

### Why PostgreSQL?

**PostgreSQL Advantages:**
1. **Open Source**: Free, no licensing costs
2. **ACID Compliance**: Reliable transactions
3. **Advanced Features**: JSON support, full-text search, triggers, views
4. **Performance**: Efficient indexing, query optimization
5. **Scalability**: Handles large datasets well
6. **Extensibility**: Custom data types, functions
7. **Strong Community**: Excellent documentation, active development
8. **Standards Compliance**: SQL standard adherence

**Comparison with Alternatives:**

| Database | Pros | Cons | Decision |
|----------|------|------|----------|
| **PostgreSQL** | Feature-rich, reliable, open source | Slightly more complex than MySQL | ✅ Chosen |
| MySQL | Simple, widely used, fast reads | Less feature-rich, Oracle ownership | ❌ |
| Oracle | Enterprise features, support | Expensive, proprietary | ❌ |
| MS SQL Server | Good Windows integration | Expensive, Windows-centric | ❌ |
| MongoDB (NoSQL) | Flexible schema, fast writes | No ACID, not relational | ❌ |
| SQLite | Embedded, simple | Not for multi-user | ❌ |

**PostgreSQL Features Used in SAMS:**

**1. ENUM Types:**
```sql
CREATE TYPE user_role AS ENUM ('STUDENT', 'FACULTY', 'ADMIN');
CREATE TYPE enrollment_status AS ENUM ('ENROLLED', 'WAITLISTED', 'DROPPED', 'COMPLETED');
```

**2. Triggers:**
```sql
CREATE TRIGGER update_course_enrollment_count
    AFTER INSERT OR UPDATE OR DELETE ON enrollments
    FOR EACH ROW
    EXECUTE FUNCTION update_course_enrollment_count();
```

**3. Views:**
```sql
CREATE VIEW student_schedule AS
SELECT e.student_id, c.course_code, c.schedule_days, c.schedule_time
FROM enrollments e
INNER JOIN courses c ON e.course_id = c.course_id
WHERE e.status = 'ENROLLED';
```

**4. Advanced Indexing:**
```sql
CREATE INDEX idx_courses_search ON courses USING gin(to_tsvector('english', course_name));
CREATE INDEX idx_enrollments_student_semester ON enrollments(student_id, semester, academic_year);
```

**5. JSON Support (for future features):**
```sql
-- Store payment course coverage as JSON array
courses_covered JSONB
```

**Why Not NoSQL (MongoDB)?**
- SAMS has well-defined relationships (students, courses, enrollments)
- ACID transactions critical (enrollment, payment)
- Complex joins needed (transcript generation)
- Data structure stable (not rapidly evolving)

**SRS Requirements Satisfied:**
- NFR-3.2 (Fault Tolerance): ACID guarantees data consistency
- NFR-1.4 (Scalability): Can handle growth beyond 100 students
- NFR-4.1 (Security): Row-level security, encrypted connections

---

## 12. JWT (JSON Web Tokens) - jjwt Library

### Why JWT?

**JWT for Authentication:**
- **Stateless**: No server-side session storage
- **Scalable**: Any server can validate token
- **Self-Contained**: Contains user info (userId, role)
- **Standard**: RFC 7519, widely adopted

**JWT Structure:**
```
Header.Payload.Signature

eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.  ← Header (algorithm, type)
eyJzdWIiOiJzYXJhaC5qb2huc29uIiwidXNlcklkIjoxMjMsInJvbGUiOiJTVFVERU5UIn0.  ← Payload (claims)
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c  ← Signature (verify integrity)
```

**Decoded Payload:**
```json
{
  "sub": "sarah.johnson",
  "userId": 123,
  "role": "STUDENT",
  "iat": 1631872400,
  "exp": 1631876000
}
```

**Alternatives:**

| Method | Pros | Cons | Decision |
|--------|------|------|----------|
| **JWT** | Stateless, scalable, standard | Token size, cannot revoke easily | ✅ Chosen |
| Session Cookies | Revocable, simple | Server storage, not scalable | ❌ |
| OAuth 2.0 | Standard, flexible | Complex for simple use case | ❌ (Phase 2) |
| API Keys | Simple | Less secure, no expiration | ❌ |

**JWT Implementation (jjwt library):**
```java
String token = Jwts.builder()
    .setSubject(username)
    .claim("userId", userId)
    .claim("role", role.name())
    .setIssuedAt(new Date())
    .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
    .signWith(SignatureAlgorithm.HS512, jwtSecret)
    .compact();
```

**Benefits:**
- Horizontal scaling (any instance can validate)
- Mobile-friendly (no cookies)
- Microservices-ready
- Cross-domain compatible

---

## ADDITIONAL TECHNOLOGIES

## 13. Lombok (Code Generation)

**Purpose:** Reduce Java boilerplate

**Features:**
- `@Data`: Generates getters, setters, toString, equals, hashCode
- `@NoArgsConstructor`, `@AllArgsConstructor`: Constructor generation
- `@RequiredArgsConstructor`: Constructor for final fields (dependency injection)
- `@Builder`: Builder pattern
- `@Slf4j`: Logger field

**Before Lombok:**
```java
public class Student {
    private Integer studentId;
    private String firstName;

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @Override
    public boolean equals(Object o) { /* 15 lines */ }
    @Override
    public int hashCode() { /* 5 lines */ }
    @Override
    public String toString() { /* 5 lines */ }
}
```

**With Lombok:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    private Integer studentId;
    private String firstName;
}
```

**Benefits:** Less code, better readability, less bugs

---

## 14. Maven (Build Tool)

**Why Maven over Gradle?**

| Feature | Maven | Gradle |
|---------|-------|--------|
| Configuration | XML (pom.xml) | Groovy/Kotlin DSL |
| Learning Curve | Lower | Higher |
| Performance | Slower | Faster |
| Ecosystem | Mature | Growing |
| Convention | Strong | Flexible |

**Decision:** Maven chosen for:
- Simpler for beginners
- Spring Boot excellent Maven support
- Widely used in enterprise
- Stable, predictable builds

---

## Technology Decision Summary

### Core Stack

| Layer | Technology | Version | Justification |
|-------|------------|---------|---------------|
| **Frontend** | Vue.js | 3.3+ | Easy to learn, fast, modern |
| | Vue Router | 4.x | Official routing, guards |
| | Vuex | 4.x | Centralized state |
| | Axios | 1.x | HTTP client, interceptors |
| | Element Plus | 2.x | UI components |
| | Vite | 4.x | Fast build, HMR |
| **Backend** | Java | 17 LTS | Modern, stable, enterprise |
| | Spring Boot | 3.x | Rapid development, comprehensive |
| | Spring Security | 6.x | Authentication/authorization |
| | Spring Data JPA | 3.x | ORM, repository pattern |
| | Hibernate | 6.x | ORM implementation |
| | JWT (jjwt) | 0.11+ | Stateless auth |
| | Lombok | 1.18+ | Reduce boilerplate |
| | Maven | 3.8+ | Build tool |
| **Database** | PostgreSQL | 13+ | Feature-rich, reliable |
| | HikariCP | (included) | Connection pooling |

---

## SRS Compliance Matrix

| Technology | SRS Requirements Satisfied |
|------------|---------------------------|
| Vue.js | NFR-1.1 (Performance), NFR-2.1 (Cross-platform), NFR-5.4 (Maintainability) |
| Spring Boot | NFR-5.1 (Code Quality), NFR-5.3 (Extensibility), NFR-1.4 (Scalability) |
| Spring Security | NFR-4.1 (Security), FR-1.2 (Authorization) |
| JWT | NFR-1.4 (Scalability - stateless), NFR-4.1 (Security) |
| PostgreSQL | NFR-3.2 (Fault Tolerance - ACID), NFR-1.4 (Scalability), NFR-4.1 (Security) |
| JPA/Hibernate | NFR-5.4 (Maintainability), NFR-1.1 (Performance - caching) |

---

## Future Technology Considerations

### Phase 2 Enhancements:
- **Redis**: Distributed caching, session storage
- **RabbitMQ/Kafka**: Asynchronous messaging, notifications
- **Elasticsearch**: Advanced search capabilities
- **Docker**: Containerization for deployment
- **Nginx**: Reverse proxy, load balancing
- **AWS S3**: File storage (documents, transcripts)
- **SendGrid/SMTP**: Email notifications
- **Jasper Reports**: PDF transcript generation

---

**Document Status:** Complete
**Technologies Documented:** 14 core technologies
**Decision Rationale:** Justified for each technology
**Completion:** System architecture section 100% complete
**Next Step:** UI wireframes deliverables
