# Database Integration Guide

This guide explains how Java code becomes database tables, and how Spring Boot/JPA makes database operations incredibly easy.

## The Big Picture: ORM (Object-Relational Mapping)

### The Problem ORM Solves

**Without ORM (The Old Way):**
```java
// You had to write SQL manually for every operation
String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setString(1, username);
stmt.setString(2, email);
stmt.setString(3, password);
stmt.executeUpdate();
// And then handle exceptions, close connections, etc.
```

**With ORM (The Modern Way):**
```java
// Just treat database rows as Java objects!
User user = new User(username, email, password);
userRepository.save(user);  // That's it!
// JPA handles all SQL generation and execution
```

### What is JPA?

**JPA = Java Persistence API**

Think of JPA as a **translator** between:
- Java objects (User, Course, etc.) ↔ Database tables (users, courses, etc.)

**What JPA does:**
1. Maps Java classes to database tables
2. Generates SQL automatically
3. Converts query results back to Java objects
4. Manages database connections
5. Handles transactions

### What is Hibernate?

**Hibernate** is the actual implementation of JPA that we're using.

**Analogy:**
- JPA = Interface (the rules/specification)
- Hibernate = Implementation (the actual code that does the work)

Spring Boot automatically includes Hibernate when you add `spring-boot-starter-data-jpa`.

---

## Understanding the User Entity

Let's break down `User.java` line by line:

### Class Declaration
```java
@Entity
@Table(name = "users")
public class User {
```

**@Entity:**
- Tells JPA: "This class represents a database table"
- JPA will create/update the table when app starts

**@Table(name = "users"):**
- Specifies the actual table name in the database
- Without this, JPA would name it "user" (class name)
- We use "users" because "user" is a reserved keyword in some databases

### Primary Key
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

**@Id:**
- Marks this field as the primary key
- Every entity must have an @Id field

**@GeneratedValue:**
- Database auto-generates this value
- `IDENTITY` strategy = use database's auto-increment feature

**Why Long instead of int?**
- Long can hold bigger numbers (up to 9,223,372,036,854,775,807)
- Can be null (important for new entities not yet saved)
- Industry standard for IDs

**What happens:**
```
Before save: user.getId() = null
After save:  user.getId() = 1, 2, 3, etc. (assigned by database)
```

### Field Mappings
```java
@NotBlank(message = "Username is required")
@Size(min = 3, max = 50)
@Column(unique = true, nullable = false)
private String username;
```

**@NotBlank:**
- **Validation annotation** (not JPA!)
- Checks that field is not null or empty before saving
- Part of Bean Validation (Jakarta Validation)

**@Size:**
- Validates length (3-50 characters)
- Happens before database operation

**@Column:**
- **JPA annotation** - configures database column
- `unique = true` → Creates unique constraint in database
- `nullable = false` → Creates NOT NULL constraint

**How it translates to SQL:**
```sql
CREATE TABLE users (
    username VARCHAR(50) NOT NULL UNIQUE,
    -- @Column + @Size = VARCHAR(50) NOT NULL UNIQUE
);
```

### Timestamp Fields
```java
@Column(name = "created_at")
private LocalDateTime createdAt;

@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

**@Column(name = "created_at"):**
- Maps field `createdAt` to column `created_at` (snake_case)
- Java uses camelCase, databases often use snake_case

**@PrePersist:**
- Lifecycle callback
- Runs automatically BEFORE entity is saved for the first time
- Perfect for setting creation timestamps

**@PreUpdate:**
- Runs automatically BEFORE entity is updated
- Perfect for updating the "last modified" timestamp

**Flow:**
```
1. Create user: new User(...)
2. Call: userRepository.save(user)
3. JPA triggers: @PrePersist → onCreate()
4. Sets: createdAt = now, updatedAt = now
5. JPA executes: INSERT INTO users (...)
```

---

## Database Configuration (application.properties)

Let's understand each line:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
```
**Breaking it down:**
- `jdbc:postgresql://` = Use PostgreSQL JDBC driver
- `localhost` = Database is on same machine
- `5432` = PostgreSQL default port
- `sams_db` = Database name

```properties
spring.datasource.username=postgres
spring.datasource.password=postgres
```
**Database credentials**
- In production, use environment variables!
- Never commit real passwords to Git

```properties
spring.jpa.hibernate.ddl-auto=update
```
**SUPER IMPORTANT - Controls table creation:**

Options:
- `none` = Don't touch database structure
- `validate` = Check if tables match entities, error if not
- `update` = Create/modify tables to match entities (safe, doesn't delete)
- `create` = Drop and recreate tables every time (DELETES DATA!)
- `create-drop` = Like create, but drops tables on shutdown

**For development:** `update` is perfect
**For production:** Use migrations (Flyway/Liquibase) instead

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```
**Debugging helpers:**
- `show-sql` = Print SQL queries to console
- `format_sql` = Make SQL pretty and readable

**Example output:**
```sql
Hibernate:
    insert
    into
        users
        (created_at, email, password, role, updated_at, username, id)
    values
        (?, ?, ?, ?, ?, ?, default)
```

---

## The Repository Layer

### Understanding UserRepository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

### Why it's an interface, not a class?

**Spring Data JPA magic:**
- You write the interface
- Spring automatically creates the implementation at runtime
- You never write the actual code for save(), findById(), etc.

**What JpaRepository gives you for free:**
```java
userRepository.save(user)           // INSERT or UPDATE
userRepository.findById(1L)         // SELECT by ID
userRepository.findAll()            // SELECT all
userRepository.deleteById(1L)       // DELETE
userRepository.count()              // COUNT
// And many more!
```

### Method Name Query Derivation

**This is SUPER COOL:**

Spring Data JPA can create queries from method names!

```java
Optional<User> findByUsername(String username);
```

Spring sees this method name and automatically generates:
```sql
SELECT * FROM users WHERE username = ?
```

**Naming rules:**
- `findBy` + `FieldName` = SELECT WHERE field =
- `existsBy` + `FieldName` = Returns boolean (does record exist?)
- `countBy` + `FieldName` = Returns count
- `deleteBy` + `FieldName` = DELETE WHERE

**More examples:**
```java
List<User> findByRole(String role);
// → SELECT * FROM users WHERE role = ?

List<User> findByEmailContaining(String keyword);
// → SELECT * FROM users WHERE email LIKE %keyword%

List<User> findByCreatedAtAfter(LocalDateTime date);
// → SELECT * FROM users WHERE created_at > ?

List<User> findByUsernameAndRole(String username, String role);
// → SELECT * FROM users WHERE username = ? AND role = ?
```

### Why Optional<User>?

```java
Optional<User> findByUsername(String username);
```

**Optional** = Container that may or may not contain a value

**Without Optional (old way):**
```java
User user = findByUsername("john");
if (user != null) {  // Risk of NullPointerException
    // do something
}
```

**With Optional (modern way):**
```java
Optional<User> userOpt = findByUsername("john");
if (userOpt.isPresent()) {
    User user = userOpt.get();
}
// Or even better:
userOpt.ifPresent(user -> {
    // do something with user
});
```

**Why it's better:**
- Makes it clear the value might not exist
- Prevents NullPointerException
- Encourages better error handling

---

## How Database Operations Work

### Creating a User (INSERT)

**Step-by-step flow:**

1. **Controller receives request:**
```java
@PostMapping("/register")
public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
```

2. **Create entity:**
```java
User user = new User();
user.setUsername(request.getUsername());
user.setEmail(request.getEmail());
user.setPassword(encodedPassword);
```

3. **Save to database:**
```java
User savedUser = userRepository.save(user);
```

4. **What happens behind the scenes:**
```
→ JPA sees this is a new entity (id is null)
→ Triggers @PrePersist → onCreate() runs
→ Generates SQL INSERT statement
→ Executes: INSERT INTO users (username, email, ...) VALUES (?, ?, ...)
→ Database returns generated ID
→ JPA sets the ID on the user object
→ Returns the saved user (now with ID)
```

5. **SQL Generated (roughly):**
```sql
INSERT INTO users (username, email, password, role, created_at, updated_at)
VALUES ('john', 'john@example.com', 'hashed_password', 'STUDENT', '2024-01-15 10:30:00', '2024-01-15 10:30:00');
```

### Finding a User (SELECT)

```java
Optional<User> userOpt = userRepository.findByUsername("john");
```

**Behind the scenes:**
```
→ Spring Data JPA parses method name
→ Generates: SELECT * FROM users WHERE username = ?
→ Executes query with "john" as parameter
→ Converts result row to User object
→ Returns Optional<User>
```

### Checking if User Exists

```java
boolean exists = userRepository.existsByUsername("john");
```

**SQL Generated:**
```sql
SELECT COUNT(*) > 0 FROM users WHERE username = 'john';
```

**Why use existsBy instead of findBy?**
- More efficient (doesn't retrieve all data)
- Clearer intent (just checking existence)
- Returns simple boolean

---

## Database Connection Pooling

### What is it?

Opening/closing database connections is slow. Connection pooling reuses connections.

**Without pooling:**
```
Request 1: Open connection → Query → Close connection
Request 2: Open connection → Query → Close connection
(Slow! Opening connections takes time)
```

**With pooling:**
```
Startup: Create 10 connections (pool)
Request 1: Borrow connection → Query → Return to pool
Request 2: Borrow connection → Query → Return to pool
(Fast! Connections already open and ready)
```

**Spring Boot automatically configures HikariCP** (fastest connection pool for Java)

Default settings (can customize in application.properties):
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

---

## Viewing Database Changes

### Using pgAdmin

1. Open pgAdmin
2. Navigate: Servers → PostgreSQL → Databases → sams_db
3. Right-click on Tables → Refresh
4. Click on `users` table
5. Right-click → View/Edit Data → All Rows

### Using SQL Queries

```sql
-- See all users
SELECT * FROM users;

-- See table structure
\d users

-- Count users
SELECT COUNT(*) FROM users;

-- Find specific user
SELECT * FROM users WHERE username = 'john';
```

---

## Common Questions

**Q: When does the table get created?**
A: When the application starts, if `ddl-auto=update`

**Q: What if I change the User entity?**
A: Restart the app, Hibernate will update the table structure

**Q: Can I use raw SQL instead of JPA?**
A: Yes, with @Query annotation:
```java
@Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
List<User> findByEmailDomain(@Param("domain") String domain);
```

**Q: How do I handle relationships (e.g., User has many Courses)?**
A: Use @OneToMany, @ManyToOne, @ManyToMany annotations (Week 4 topic!)

**Q: Is JPA slow?**
A: Not if used correctly. It's highly optimized and used by major companies.

**Q: Should I use JPA for everything?**
A: For complex queries, sometimes raw SQL is clearer. Use @Query for those cases.

---

## Best Practices

1. **Always use repository layer** - Don't create EntityManager beans
2. **Use Optional** - For find methods that might return nothing
3. **Validate at multiple levels:**
   - DTO validation (@NotBlank, @Email)
   - Database constraints (@Column unique, nullable)
4. **Never expose entities directly in API** - Use DTOs
5. **Use transactions** - For operations that modify multiple entities
6. **Index frequently queried columns** - Will learn in advanced topics

---

**Next:** Read [SECURITY_EXPLAINED.md](SECURITY_EXPLAINED.md) to understand authentication and password hashing!
