# SAMS System Architecture Diagram

## Purpose
This document visualizes the complete system architecture for SAMS using a layered, three-tier architecture pattern.

---

## High-Level Architecture Overview

```mermaid
graph TB
    subgraph "Client Layer - Presentation Tier"
        Browser[Web Browser]
        VueApp[Vue.js 3 Application]
        Router[Vue Router]
        Store[Vuex State Management]
        Components[Vue Components]
    end

    subgraph "Application Layer - Business Logic Tier"
        Gateway[API Gateway / Load Balancer]
        SpringBoot[Spring Boot Application]
        Controllers[REST Controllers]
        Services[Business Services]
        Security[Spring Security + JWT]
        Validation[Input Validation]
    end

    subgraph "Data Layer - Persistence Tier"
        JPA[Spring Data JPA]
        Repositories[JPA Repositories]
        PostgreSQL[(PostgreSQL Database)]
        Triggers[Database Triggers]
        Views[Database Views]
    end

    subgraph "External Services - Future Integration"
        EmailService[Email Service SMTP]
        PaymentGateway[Payment Gateway API]
        FileStorage[File Storage S3]
    end

    Browser -->|HTTP/HTTPS| VueApp
    VueApp --> Router
    VueApp --> Store
    VueApp --> Components

    Components -->|REST API Calls| Gateway
    Gateway -->|Forward Requests| SpringBoot

    SpringBoot --> Controllers
    Controllers -->|Authenticate| Security
    Controllers -->|Validate| Validation
    Controllers -->|Business Logic| Services

    Services --> JPA
    JPA --> Repositories
    Repositories -->|SQL Queries| PostgreSQL

    PostgreSQL --> Triggers
    PostgreSQL --> Views

    Services -.->|Future| EmailService
    Services -.->|Future| PaymentGateway
    Services -.->|Future| FileStorage

    style VueApp fill:#42b983
    style SpringBoot fill:#6db33f
    style PostgreSQL fill:#336791
    style EmailService fill:#ddd
    style PaymentGateway fill:#ddd
    style FileStorage fill:#ddd
```

---

## Detailed Layer Architecture

### 1. Presentation Layer (Frontend - Vue.js)

```mermaid
graph TD
    subgraph "Vue.js Frontend Architecture"
        A[main.js - App Entry Point] --> B[App.vue - Root Component]
        B --> C[Vue Router]
        B --> D[Vuex Store]

        C --> E1[Student Views]
        C --> E2[Faculty Views]
        C --> E3[Admin Views]
        C --> E4[Public Views]

        E1 --> F1[Dashboard]
        E1 --> F2[Course Registration]
        E1 --> F3[Grades View]
        E1 --> F4[Payment Submit]

        E2 --> G1[Faculty Dashboard]
        E2 --> G2[Course Management]
        E2 --> G3[Grade Entry]

        E3 --> H1[Admin Dashboard]
        E3 --> H2[User Management]
        E3 --> H3[Payment Validation]
        E3 --> H4[Reports]

        D --> I1[Auth Module]
        D --> I2[Student Module]
        D --> I3[Course Module]

        J[Axios HTTP Client] --> K[API Service Layer]
        K --> L[Backend REST API]

        F1 --> K
        F2 --> K
        G1 --> K
        H1 --> K
    end

    style A fill:#42b983
    style B fill:#42b983
    style C fill:#35495e
    style D fill:#35495e
```

**Key Components:**
- **Router**: Client-side routing (SPA - Single Page Application)
- **Vuex Store**: Centralized state management
- **API Service**: Axios-based HTTP client with interceptors
- **Views**: Page-level components for each user role
- **Components**: Reusable UI components (forms, tables, cards)

---

### 2. Application Layer (Backend - Spring Boot)

```mermaid
graph TD
    subgraph "Spring Boot Application Architecture"
        A[Embedded Tomcat Server :8080] --> B[Spring Boot Application]

        B --> C[Web Layer]
        C --> C1[REST Controllers]
        C --> C2[Exception Handlers]
        C --> C3[Request Validators]

        B --> D[Security Layer]
        D --> D1[JWT Authentication Filter]
        D --> D2[Spring Security Config]
        D --> D3[JWT Token Provider]
        D --> D4[Password Encoder BCrypt]

        B --> E[Service Layer]
        E --> E1[AuthService]
        E --> E2[StudentService]
        E --> E3[CourseService]
        E --> E4[EnrollmentService]
        E --> E5[GradeService]
        E --> E6[PaymentService]

        E --> F[Repository Layer]
        F --> F1[UserRepository]
        F --> F2[StudentRepository]
        F --> F3[CourseRepository]
        F --> F4[EnrollmentRepository]
        F --> F5[GradeRepository]

        F --> G[Spring Data JPA]
        G --> H[Hibernate ORM]
        H --> I[JDBC Driver]
        I --> J[(PostgreSQL Database)]

        B --> K[Cross-Cutting Concerns]
        K --> K1[Logging Aspect]
        K --> K2[Transaction Management]
        K --> K3[Caching]
        K --> K4[Async Processing]
    end

    style B fill:#6db33f
    style C fill:#f0ad4e
    style E fill:#5bc0de
    style F fill:#5cb85c
```

**Layering Principle:**
- **Controllers**: Handle HTTP requests/responses, delegate to services
- **Services**: Business logic, transaction management
- **Repositories**: Data access abstraction
- **Cross-cutting**: Logging, caching, async, security

---

### 3. Data Layer (Persistence - PostgreSQL)

```mermaid
graph TD
    subgraph "PostgreSQL Database Architecture"
        A[PostgreSQL 13+] --> B[SAMS Database]

        B --> C[Tables 11]
        C --> C1[users]
        C --> C2[students]
        C --> C3[faculty]
        C --> C4[courses]
        C --> C5[enrollments]
        C --> C6[grades]
        C --> C7[payments]
        C --> C8[study_groups]
        C --> C9[study_group_members]
        C --> C10[notifications]
        C --> C11[degree_requirements]

        B --> D[Indexes 45+]
        D --> D1[Primary Keys]
        D --> D2[Foreign Keys]
        D --> D3[Unique Constraints]
        D --> D4[Performance Indexes]

        B --> E[Triggers]
        E --> E1[update_updated_at_column]
        E --> E2[update_course_enrollment_count]

        B --> F[Views]
        F --> F1[student_schedule]
        F --> F2[course_enrollment_summary]

        B --> G[Constraints]
        G --> G1[Foreign Key Constraints]
        G --> G2[Check Constraints]
        G --> G3[Unique Constraints]

        B --> H[ENUM Types]
        H --> H1[user_role]
        H --> H2[enrollment_status]
        H --> H3[payment_status]
        H --> H4[payment_method]
        H --> H5[grade_type]
    end

    style A fill:#336791
    style C fill:#5cb85c
```

---

## Deployment Architecture (Production)

```mermaid
graph TB
    subgraph "Client Side"
        User[End Users] --> CDN[CDN - Vue.js Static Assets]
    end

    subgraph "Load Balancer Layer"
        CDN --> LB[Nginx Load Balancer]
    end

    subgraph "Application Server Cluster"
        LB --> App1[Spring Boot Instance 1 :8080]
        LB --> App2[Spring Boot Instance 2 :8081]
        LB --> App3[Spring Boot Instance 3 :8082]
    end

    subgraph "Database Layer"
        App1 --> DB[(PostgreSQL Primary)]
        App2 --> DB
        App3 --> DB
        DB --> Replica[(PostgreSQL Replica Read-Only)]
    end

    subgraph "Caching Layer"
        App1 --> Redis[Redis Cache]
        App2 --> Redis
        App3 --> Redis
    end

    subgraph "Monitoring & Logging"
        App1 --> Logs[Centralized Logging]
        App2 --> Logs
        App3 --> Logs
        DB --> Monitoring[Database Monitoring]
    end

    style User fill:#fff
    style LB fill:#f0ad4e
    style App1 fill:#6db33f
    style App2 fill:#6db33f
    style App3 fill:#6db33f
    style DB fill:#336791
    style Redis fill:#d82c20
```

**Scalability Features:**
- **Horizontal Scaling**: Multiple Spring Boot instances behind load balancer
- **Stateless API**: JWT enables any instance to handle any request
- **Database Replication**: Read replicas for query load distribution
- **Caching**: Redis for session data and frequently accessed resources

---

## Request Flow Diagram

### Typical API Request Flow

```mermaid
sequenceDiagram
    participant Browser
    participant VueApp
    participant Nginx
    participant SpringBoot
    participant Security
    participant Controller
    participant Service
    participant Repository
    participant PostgreSQL

    Browser->>VueApp: User clicks "View Grades"
    VueApp->>VueApp: Get JWT token from localStorage
    VueApp->>Nginx: GET /api/v1/students/123/grades<br/>Authorization: Bearer <token>
    Nginx->>SpringBoot: Forward request
    SpringBoot->>Security: JWT Auth Filter
    Security->>Security: Validate token signature
    Security->>Security: Extract userId, role from claims
    Security->>Security: Set SecurityContext
    Security->>Controller: Proceed to GradeController
    Controller->>Controller: @PreAuthorize check
    Controller->>Service: gradeService.getStudentGrades(123)
    Service->>Service: Business logic validation
    Service->>Repository: gradeRepository.findByStudentId(123)
    Repository->>PostgreSQL: SELECT * FROM grades WHERE student_id = 123
    PostgreSQL-->>Repository: Result set (List<Grade>)
    Repository-->>Service: List<Grade>
    Service->>Service: Map to DTOs
    Service-->>Controller: List<GradeDTO>
    Controller->>Controller: Wrap in ApiResponse
    Controller-->>SpringBoot: ResponseEntity<ApiResponse>
    SpringBoot-->>Nginx: HTTP 200 + JSON
    Nginx-->>VueApp: HTTP 200 + JSON
    VueApp->>VueApp: Update Vuex store
    VueApp->>Browser: Render grades table
```

---

## Component Interaction Diagram

### Major System Components and Their Interactions

```mermaid
graph TB
    subgraph "Frontend Components"
        A1[Login Component]
        A2[Student Dashboard]
        A3[Course Registration]
        A4[Grade View]
        A5[Payment Submit]
        A6[Faculty Grade Entry]
        A7[Admin Payment Validation]
    end

    subgraph "API Endpoints"
        B1[POST /auth/login]
        B2[GET /students/me]
        B3[POST /enrollments]
        B4[GET /students/:id/grades]
        B5[POST /payments]
        B6[POST /grades]
        B7[POST /payments/:id/validate]
    end

    subgraph "Business Services"
        C1[AuthService]
        C2[StudentService]
        C3[EnrollmentService]
        C4[GradeService]
        C5[PaymentService]
    end

    subgraph "Data Repositories"
        D1[UserRepository]
        D2[StudentRepository]
        D3[EnrollmentRepository]
        D4[GradeRepository]
        D5[PaymentRepository]
    end

    A1 --> B1
    A2 --> B2
    A3 --> B3
    A4 --> B4
    A5 --> B5
    A6 --> B6
    A7 --> B7

    B1 --> C1
    B2 --> C2
    B3 --> C3
    B4 --> C4
    B5 --> C5
    B6 --> C4
    B7 --> C5

    C1 --> D1
    C2 --> D2
    C3 --> D3
    C4 --> D4
    C5 --> D5

    D1 -.-> DB[(Database)]
    D2 -.-> DB
    D3 -.-> DB
    D4 -.-> DB
    D5 -.-> DB

    style A1 fill:#42b983
    style B1 fill:#f0ad4e
    style C1 fill:#5bc0de
    style D1 fill:#5cb85c
    style DB fill:#336791
```

---

## Technology Stack Diagram

```mermaid
graph TD
    subgraph "Frontend Stack"
        A[Vue.js 3.3+]
        B[Vue Router 4.x]
        C[Vuex 4.x / Pinia]
        D[Axios HTTP Client]
        E[Element Plus UI Library]
        F[Vite Build Tool]
    end

    subgraph "Backend Stack"
        G[Java 17 LTS]
        H[Spring Boot 3.x]
        I[Spring Security 6.x]
        J[Spring Data JPA]
        K[Hibernate 6.x]
        L[JWT jjwt 0.11+]
        M[Lombok]
        N[Maven Build Tool]
    end

    subgraph "Database Stack"
        O[PostgreSQL 13+]
        P[JDBC Driver]
        Q[HikariCP Connection Pool]
    end

    subgraph "DevOps & Tools"
        R[Git Version Control]
        S[Postman API Testing]
        T[Docker Development]
        U[Nginx Web Server]
        V[IntelliJ IDEA]
        W[VS Code]
    end

    A --> F
    B --> A
    C --> A
    D --> A
    E --> A

    H --> G
    I --> H
    J --> H
    K --> J
    L --> I
    M --> H
    N --> H

    J --> P
    P --> Q
    Q --> O

    style A fill:#42b983
    style H fill:#6db33f
    style O fill:#336791
```

---

## Security Architecture

```mermaid
graph TD
    subgraph "Security Layers"
        A[HTTPS/TLS Layer]
        B[CORS Configuration]
        C[JWT Authentication]
        D[Role-Based Authorization RBAC]
        E[Input Validation]
        F[SQL Injection Prevention]
        G[XSS Protection]
        H[CSRF Protection]
    end

    subgraph "Authentication Flow"
        I[Login Request] --> J[Validate Credentials]
        J --> K[BCrypt Password Check]
        K --> L[Generate JWT Token]
        L --> M[Return Token to Client]
        M --> N[Client stores in localStorage]
        N --> O[Include in Authorization header]
    end

    subgraph "Authorization Flow"
        P[Incoming Request] --> Q[Extract JWT from header]
        Q --> R[Validate JWT signature]
        R --> S[Extract user role from claims]
        S --> T[Check @PreAuthorize annotation]
        T --> U{Has Permission?}
        U -->|Yes| V[Process Request]
        U -->|No| W[Return 403 Forbidden]
    end

    A --> B
    B --> C
    C --> D
    D --> E
    E --> F
    F --> G
    G --> H

    style C fill:#f0ad4e
    style D fill:#5bc0de
    style K fill:#d9534f
```

**Security Features:**
- **HTTPS Only**: All production traffic encrypted
- **JWT Tokens**: Stateless authentication
- **BCrypt Hashing**: Password encryption (strength 12)
- **Role-Based Access**: STUDENT, FACULTY, ADMIN roles
- **Input Validation**: Bean Validation + custom validators
- **Prepared Statements**: JPA prevents SQL injection
- **CORS**: Configured for specific origins only

---

## Data Flow Architecture

### Write Operation (Example: Enroll in Course)

```mermaid
graph LR
    A[User Action] --> B[Vue Component]
    B --> C[Vuex Action]
    C --> D[API Service]
    D --> E[POST /enrollments]
    E --> F[Spring Controller]
    F --> G[EnrollmentService]
    G --> H{Validate Business Rules}
    H -->|Prerequisites Met?| I[Check Course Capacity]
    I -->|Has Space?| J[Create Enrollment]
    J --> K[Repository.save]
    K --> L[JPA persist]
    L --> M[SQL INSERT]
    M --> N[(Database)]
    N --> O[Trigger: Update course count]
    O --> N
    N --> P[Return saved entity]
    P --> Q[Map to DTO]
    Q --> R[Return API Response]
    R --> S[Update Vuex State]
    S --> T[Render UI Update]

    style A fill:#42b983
    style G fill:#5bc0de
    style N fill:#336791
```

### Read Operation (Example: View Grades)

```mermaid
graph LR
    A[Page Load] --> B[Vue Component mounted]
    B --> C[Dispatch fetchGrades]
    C --> D[API Service]
    D --> E[GET /students/:id/grades]
    E --> F[Spring Controller]
    F --> G{Check Authorization}
    G -->|Authorized| H[GradeService]
    H --> I[Repository.findByStudentId]
    I --> J[JPA query]
    J --> K[SQL SELECT with JOINs]
    K --> L[(Database)]
    L --> M[Result Set]
    M --> N[Map to entities]
    N --> O[Map to DTOs]
    O --> P[Return API Response]
    P --> Q[Store in Vuex]
    Q --> R[Component re-renders]
    R --> S[Display grades table]

    style A fill:#42b983
    style H fill:#5bc0de
    style L fill:#336791
```

---

## Integration Points

### Current Integrations

| Component | Integration Type | Protocol | Purpose |
|-----------|------------------|----------|---------|
| Frontend → Backend | REST API | HTTP/HTTPS + JSON | All client-server communication |
| Backend → Database | JDBC | TCP/IP | Data persistence and retrieval |
| Client → Auth | JWT Bearer Token | HTTP Header | Stateless authentication |

### Future Integrations (Phase 2)

| Component | Integration Type | Protocol | Purpose |
|-----------|------------------|----------|---------|
| Email Service | SMTP | SMTP/TLS | Notifications, password reset |
| Payment Gateway | REST API | HTTPS + JSON | Online payment processing |
| File Storage | S3 API | HTTPS | Document uploads (transcripts, proofs) |
| SMS Gateway | REST API | HTTPS | SMS notifications |
| Analytics | REST API | HTTPS | Usage analytics and reporting |

---

## Architectural Patterns Used

### 1. **Layered Architecture (N-Tier)**
- Clear separation of concerns
- Presentation → Business → Data layers
- Each layer depends only on layer below

### 2. **Model-View-Controller (MVC)**
- Vue.js: View layer (Components)
- Spring Controllers: Controller layer
- Services + Repositories: Model layer

### 3. **Repository Pattern**
- Abstraction over data access
- Spring Data JPA repositories
- Decouples business logic from persistence

### 4. **Service Layer Pattern**
- Business logic encapsulation
- Transaction boundaries
- Orchestrates repository calls

### 5. **Data Transfer Object (DTO) Pattern**
- Decouples API contracts from entities
- Prevents over-fetching/under-fetching
- API versioning flexibility

### 6. **Dependency Injection**
- Spring's IoC container
- Constructor injection (preferred)
- Promotes testability

---

## Performance Optimizations

### Frontend Optimizations
- **Lazy Loading**: Routes loaded on-demand
- **Component Code Splitting**: Vite chunks
- **Asset Optimization**: Minification, compression
- **Caching**: Service Worker (future), browser cache

### Backend Optimizations
- **Connection Pooling**: HikariCP (20 connections)
- **JPA Fetch Strategies**: JOIN FETCH to prevent N+1
- **Query Optimization**: Proper indexing
- **Caching**: Spring Cache (in-memory)
- **Async Processing**: @Async for non-blocking operations

### Database Optimizations
- **Indexes**: 45+ strategic indexes
- **Denormalization**: GPA, enrollment counts stored
- **Triggers**: Auto-update calculated fields
- **Views**: Pre-computed complex queries
- **Partitioning**: Future - partition by semester

---

## Scalability Considerations

### Horizontal Scaling
- **Stateless Backend**: JWT enables multiple instances
- **Load Balancer**: Nginx distributes traffic
- **Session-less**: No server-side session state
- **Database Pooling**: Each instance has own pool

### Vertical Scaling
- **JVM Tuning**: Heap size, GC configuration
- **Database Resources**: CPU, RAM, storage
- **Connection Limits**: Increase pool size

### Caching Strategy
- **Application Cache**: Spring Cache (courses, students)
- **Database Cache**: PostgreSQL shared buffers
- **CDN**: Static assets (Vue.js build)
- **Future: Redis**: Distributed caching

---

## Monitoring & Logging

### Application Monitoring
- **Logging**: SLF4J + Logback
- **Metrics**: Spring Actuator (future)
- **Health Checks**: `/actuator/health`
- **Performance**: Method execution time logging

### Database Monitoring
- **Slow Query Log**: Queries > 1 second
- **Connection Pool Stats**: HikariCP metrics
- **Index Usage**: pg_stat_user_indexes

---

## Disaster Recovery

### Backup Strategy
- **Database Backups**: Daily full, hourly incremental
- **Application Logs**: Retained for 30 days
- **Configuration**: Version controlled (Git)

### High Availability (Future)
- **Database Replication**: Primary + Read replica
- **Failover**: Automatic promotion of replica
- **Geographic Distribution**: Multi-region deployment

---

## Compliance with SRS Requirements

| Architectural Decision | SRS Requirement Satisfied |
|------------------------|---------------------------|
| Three-tier architecture | NFR-5.4 (Modularity and Maintainability) |
| JWT authentication | NFR-4.1 (Security and Data Protection) |
| RESTful API design | NFR-5.1 (Code Quality and Best Practices) |
| PostgreSQL with triggers | NFR-3.2 (Fault Tolerance - data consistency) |
| Stateless backend | NFR-1.4 (Scalability) |
| Connection pooling | NFR-1.1 (Response Time < 3 seconds) |
| Role-based authorization | FR-1.2 (Authorization and Access Control) |
| Vue.js SPA | NFR-2.1 (Cross-Platform Compatibility) |

---

**Document Status:** Complete
**Architecture Type:** Three-Tier Layered Architecture
**Scalability:** Horizontal and Vertical scaling ready
**Next Step:** Component specifications documentation
