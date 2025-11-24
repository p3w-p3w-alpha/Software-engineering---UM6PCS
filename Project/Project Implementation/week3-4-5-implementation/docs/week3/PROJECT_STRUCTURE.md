# Project Structure Explained

This document explains every folder and file in the SAMS project and WHY they exist.

## Complete Project Tree

```
week3-development/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── sams/
│   │   │           ├── SamsApplication.java
│   │   │           ├── entity/
│   │   │           │   └── User.java
│   │   │           ├── repository/
│   │   │           │   └── UserRepository.java
│   │   │           ├── controller/
│   │   │           │   └── UserController.java
│   │   │           ├── config/
│   │   │           │   └── SecurityConfig.java
│   │   │           ├── dto/
│   │   │           │   ├── RegisterRequest.java
│   │   │           │   └── UserResponse.java
│   │   │           └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── sams/
├── docs/
├── pom.xml
├── .gitignore
└── README.md
```

## Understanding the Structure

### Root Level Files

#### pom.xml
**What it is:** Project Object Model - Maven's configuration file
**What it does:**
- Defines project metadata (name, version, description)
- Lists all dependencies (Spring Boot, PostgreSQL driver, Security, etc.)
- Configures build settings
- Tells Maven which Java version to use

**Think of it as:** A shopping list that tells Maven what libraries to download and how to build your project

**Key sections:**
```xml
<dependencies>  <!-- Libraries your project needs -->
<build>        <!-- How to compile and package your code -->
<properties>   <!-- Configuration like Java version -->
```

#### .gitignore
**What it is:** Instructions for Git on what NOT to track
**What it does:**
- Prevents IDE-specific files from being committed (`.idea/`, `*.iml`)
- Excludes compiled files (`target/`, `.class`)
- Keeps sensitive data out of version control

**Why it matters:** Keeps your repository clean and prevents accidentally committing passwords or IDE settings

#### README.md
**What it is:** The project's front page documentation
**What it does:** Provides quick-start instructions for new developers

---

## Source Code Structure (src/)

### The "main" Folder

This is where all your production code lives - the actual application that will run.

#### java/com/sams/ - The Package Structure

**Why "com.sams"?**
- Java convention: `domain.company.project`
- Prevents naming conflicts with other projects
- Organizes code hierarchically

#### SamsApplication.java
**Location:** `src/main/java/com/sams/`
**Purpose:** The entry point of your application

**What it does:**
```java
@SpringBootApplication  // This magic annotation does 3 things:
// 1. @Configuration - Marks this as a config source
// 2. @EnableAutoConfiguration - Spring Boot auto-configures based on dependencies
// 3. @ComponentScan - Finds and registers all Spring components in com.sams package

public static void main(String[] args) {
    SpringApplication.run(SamsApplication.class, args);
    // This starts the Spring container, sets up the database, launches the web server
}
```

**Think of it as:** The "on" button for your entire application

---

### Package Organization (Why We Split Code into Folders)

#### 1. entity/ Package
**Files:** `User.java`
**Purpose:** Database table representations

**What entities are:**
- Java classes that map directly to database tables
- Each field in the class = a column in the table
- Each instance of the class = a row in the table

**Why separate folder?**
- Keeps database models organized
- Easy to find what tables exist
- Clear separation from business logic

**Real-world analogy:** Think of entities as blueprints for database records - User.java is the blueprint for a user record

#### 2. repository/ Package
**Files:** `UserRepository.java`
**Purpose:** Database access layer

**What repositories do:**
- Provide methods to interact with database
- Handle CRUD operations (Create, Read, Update, Delete)
- Abstract away SQL complexity

**Why separate folder?**
- Centralized data access
- Easy to test database operations
- Follows the Repository pattern (industry standard)

**Real-world analogy:** A repository is like a librarian - you ask for data, and it handles finding it in the database

#### 3. dto/ Package
**Files:** `RegisterRequest.java`, `UserResponse.java`
**Purpose:** Data Transfer Objects

**What DTOs are:**
- Objects specifically designed for API communication
- Different from entities (don't expose database structure)
- Can include validation rules

**Why we use DTOs instead of entities directly:**
- **Security:** Don't send password field to client
- **Validation:** Can validate input before creating entity
- **Flexibility:** API structure can differ from database structure
- **Clean API:** Only include fields the client needs

**Example:**
```
User Entity (database):     RegisterRequest DTO (API input):
- id                        - username
- username                  - email
- email                     - password
- password (hashed)         - role
- role
- createdAt
- updatedAt

UserResponse DTO (API output):
- id
- username
- email
- role
- createdAt
(Notice: no password!)
```

#### 4. controller/ Package
**Files:** `UserController.java`
**Purpose:** Handle HTTP requests

**What controllers do:**
- Define API endpoints (URLs)
- Receive requests from clients
- Call appropriate services/repositories
- Return responses

**Why separate folder?**
- All API endpoints in one place
- Easy to see what routes exist
- Follows MVC pattern (Model-View-Controller)

**Real-world analogy:** Controllers are like receptionists - they receive requests and direct them to the right department

#### 5. config/ Package
**Files:** `SecurityConfig.java`
**Purpose:** Application configuration

**What config classes do:**
- Set up Spring Security
- Configure beans (objects Spring manages)
- Define application-wide settings

**Why separate folder?**
- Easy to find configuration
- Keeps setup code separate from business logic
- One place to change settings

#### 6. service/ Package (Empty for now, but important)
**Purpose:** Business logic layer

**What services do:**
- Contain business rules
- Coordinate between repositories and controllers
- Keep controllers thin

**When to use:**
- Complex operations involving multiple entities
- Business validation
- Transactions spanning multiple repositories

**Example (future):**
```java
public class EnrollmentService {
    // Business logic: A student can only enroll in 6 courses max
    // This logic doesn't belong in controller or repository
}
```

---

### resources/ Folder

#### application.properties
**Purpose:** External configuration

**What it contains:**
- Database connection details
- Server port
- JPA/Hibernate settings
- Logging levels

**Why not hardcode in Java?**
- Easy to change without recompiling
- Different settings for development/production
- Can use environment variables for security

**How Spring uses it:**
- Reads on startup
- Injects values into Spring components
- Can have multiple files (application-dev.properties, application-prod.properties)

---

## Design Patterns Used

### 1. Layered Architecture
```
Controller Layer (API)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database
```

**Benefits:**
- Each layer has one responsibility
- Easy to test each layer independently
- Can swap implementations (e.g., change database)

### 2. Dependency Injection
```java
@Autowired
private UserRepository userRepository;
// Spring automatically creates and injects the repository
```

**What it means:**
- You don't create objects with `new`
- Spring manages object lifecycle
- Easy to swap implementations for testing

### 3. Repository Pattern
```java
userRepository.save(user);  // Abstract SQL INSERT
userRepository.findById(1);  // Abstract SQL SELECT
```

**Benefits:**
- Don't write SQL manually
- Database-agnostic code
- Easier testing with mock repositories

---

## Package Naming Conventions

### Why plural vs singular?

- **entity** (singular) - Contains entity classes
- **controller** (singular) - Contains controller classes
- **repository** (singular) - Contains repository interfaces
- **dto** (singular) - Contains DTO classes
- **service** (singular) - Contains service classes
- **config** (singular) - Contains configuration classes

**Reason:** Java convention - package names are lowercase singular nouns

---

## Growth Path - How This Structure Scales

As the project grows, you'll add:

```
com/sams/
├── entity/
│   ├── User.java
│   ├── Course.java          ← NEW
│   ├── Enrollment.java      ← NEW
│   └── Grade.java           ← NEW
├── repository/
│   ├── UserRepository.java
│   ├── CourseRepository.java    ← NEW
│   └── EnrollmentRepository.java ← NEW
├── service/
│   ├── UserService.java         ← NEW
│   ├── EnrollmentService.java   ← NEW
│   └── GradeService.java        ← NEW
├── controller/
│   ├── UserController.java
│   ├── CourseController.java    ← NEW
│   └── EnrollmentController.java ← NEW
└── dto/
    ├── request/                 ← NEW (organized by purpose)
    │   ├── RegisterRequest.java
    │   └── EnrollRequest.java
    └── response/                ← NEW
        ├── UserResponse.java
        └── CourseResponse.java
```

---

## Common Questions

**Q: Why so many packages for such a small project?**
A: We're following industry best practices. This structure scales well - when you have 50+ classes, you'll be glad they're organized!

**Q: Can I put everything in one file?**
A: Technically yes, but it becomes unmaintainable quickly. Separation = maintainability.

**Q: What if I need to add a new feature?**
A: Follow the same pattern:
1. Create entity (if new database table)
2. Create repository
3. Create DTOs
4. Create controller
5. Add service if complex logic

**Q: Where do utility classes go?**
A: Create a `util` package at the same level as entity, controller, etc.

---

## Best Practices We're Following

1. **Package by Feature** (for larger apps) or **Package by Layer** (what we're doing)
2. **Single Responsibility** - Each class does one thing
3. **Don't Repeat Yourself (DRY)** - Reuse code via services
4. **Dependency Injection** - Let Spring manage objects
5. **Clear Naming** - File name tells you what it does

---

**Next:** Read [DATABASE_GUIDE.md](DATABASE_GUIDE.md) to understand how entities become database tables!
