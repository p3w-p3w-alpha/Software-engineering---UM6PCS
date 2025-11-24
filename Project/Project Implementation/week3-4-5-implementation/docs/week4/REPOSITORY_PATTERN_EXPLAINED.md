# Repository Pattern Explained

## What is the Repository Pattern?

The **Repository Pattern** is a design pattern that creates an abstraction layer between your business logic and data access logic. Think of it as a "collection" of all your database operations for a specific entity.

### Simple Analogy

Imagine you're running a library:
- **Without Repository**: Every time you need a book, you go into the storage room, search through shelves, handle the cataloging system yourself
- **With Repository**: You ask the librarian (repository) to get the book for you. The librarian knows where everything is and handles all the details

## Why Use the Repository Pattern?

### 1. **Abstraction**
You don't need to write SQL queries. The repository handles database operations for you.

### 2. **Maintainability**
All database logic is in one place. If you switch from PostgreSQL to MySQL, you only change the repository.

### 3. **Testability**
Easy to mock repositories in unit tests without needing a real database.

### 4. **Consistency**
Everyone on your team uses the same methods to access data.

## Spring Data JPA Magic

In Spring Boot, you don't even need to implement the repository! Just create an interface that extends `JpaRepository`:

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring automatically implements basic CRUD methods!
}
```

### What Does JpaRepository Give You for Free?

When you extend `JpaRepository<User, Long>`, Spring automatically provides:

```java
// Saving
User save(User user)          // Insert or update user
List<User> saveAll(...)       // Save multiple users

// Finding
Optional<User> findById(Long id)    // Find by primary key
List<User> findAll()                // Get all users
List<User> findAllById(...)         // Find by multiple IDs

// Deleting
void delete(User user)              // Delete a user
void deleteById(Long id)            // Delete by ID
void deleteAll()                    // Delete all users

// Counting & Checking
long count()                        // Count all users
boolean existsById(Long id)         // Check if user exists
```

**You get ~20 methods without writing any code!**

## Custom Query Methods

Spring Data JPA has a powerful feature: **method name parsing**. You write method names following a convention, and Spring generates the SQL automatically.

### How It Works

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring generates: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Spring generates: SELECT * FROM users WHERE role = ?
    List<User> findByRole(String role);

    // Spring generates: SELECT * FROM users WHERE email = ? AND role = ?
    Optional<User> findByEmailAndRole(String email, String role);

    // Spring generates: SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)
    boolean existsByEmail(String email);
}
```

### Method Naming Conventions

| Method Name Pattern | Generated SQL | Example |
|---------------------|---------------|---------|
| `findBy[Field]` | `WHERE field = ?` | `findByEmail(String email)` |
| `findBy[Field1]And[Field2]` | `WHERE field1 = ? AND field2 = ?` | `findByEmailAndRole(...)` |
| `findBy[Field1]Or[Field2]` | `WHERE field1 = ? OR field2 = ?` | `findByUsernameOrEmail(...)` |
| `existsBy[Field]` | `SELECT EXISTS WHERE field = ?` | `existsByEmail(String email)` |
| `countBy[Field]` | `SELECT COUNT(*) WHERE field = ?` | `countByRole(String role)` |
| `deleteBy[Field]` | `DELETE WHERE field = ?` | `deleteByEmail(String email)` |

### More Advanced Examples

```java
// Find users with username containing a string
List<User> findByUsernameContaining(String substring);

// Find users created after a date
List<User> findByCreatedAtAfter(LocalDateTime date);

// Find users ordered by creation date
List<User> findByRoleOrderByCreatedAtDesc(String role);

// Find top 5 users
List<User> findTop5ByRole(String role);
```

## Understanding Optional<T>

You'll notice many repository methods return `Optional<User>` instead of just `User`. Why?

### The Problem Optional Solves

**Without Optional:**
```java
User user = userRepository.findById(1L);
// What if user doesn't exist? user would be null
// If you forget to check, you get NullPointerException!
```

**With Optional:**
```java
Optional<User> userOptional = userRepository.findById(1L);
// Forces you to handle the "not found" case
```

### How to Use Optional

```java
// Method 1: Check if present
Optional<User> userOpt = userRepository.findById(1L);
if (userOpt.isPresent()) {
    User user = userOpt.get();
    System.out.println(user.getEmail());
} else {
    System.out.println("User not found");
}

// Method 2: Throw exception if not found (best for services)
User user = userRepository.findById(1L)
    .orElseThrow(() -> new UserNotFoundException(1L));

// Method 3: Provide default value
User user = userRepository.findById(1L)
    .orElse(new User()); // Returns empty User if not found

// Method 4: Execute alternative action
userRepository.findByEmail(email).ifPresent(user -> {
    System.out.println("Found user: " + user.getUsername());
});
```

## save() vs saveAndFlush()

Both methods save entities, but with a subtle difference:

### save()
```java
User savedUser = userRepository.save(user);
// Schedules the insert/update
// Might not hit database immediately
// Uses Hibernate's transaction batching for performance
```

### saveAndFlush()
```java
User savedUser = userRepository.saveAndFlush(user);
// Immediately sends SQL to database
// Flushes Hibernate cache
// Use when you need the database-generated ID right away
```

**When to use which?**
- **save()**: 95% of the time (better performance)
- **saveAndFlush()**: When you need the auto-generated ID immediately or need to ensure constraints are checked right away

## Real Example from SAMS

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email - returns Optional in case not found
    Optional<User> findByEmail(String email);

    // Get all users with specific role - returns List (can be empty)
    List<User> findByRole(String role);

    // Find by email AND role
    Optional<User> findByEmailAndRole(String email, String role);

    // Check if email exists - returns boolean
    boolean existsByEmail(String email);

    // Inherited from JpaRepository - auto-implemented:
    // - save(User)
    // - findById(Long)
    // - findAll()
    // - delete(User)
    // - deleteById(Long)
    // - count()
    // etc.
}
```

## How Spring Implements These Methods

You might wonder: "If I only write the method signature, who writes the implementation?"

**Spring does it at runtime!** Here's what happens:

1. When your app starts, Spring scans for `@Repository` interfaces
2. For each interface extending `JpaRepository`, Spring creates a **proxy implementation**
3. Spring analyzes method names like `findByEmail`
4. Spring generates the appropriate SQL query
5. At runtime, when you call `userRepository.findByEmail("test@example.com")`, the proxy executes the generated SQL

## Common Mistakes to Avoid

### ❌ Wrong
```java
// Typo in method name - Spring won't understand
Optional<User> findByEmial(String email);  // "Emial" is wrong

// Missing "By" keyword
Optional<User> findEmail(String email);  // Won't work
```

### ✅ Correct
```java
Optional<User> findByEmail(String email);
```

### ❌ Wrong
```java
// Not handling Optional properly
User user = userRepository.findById(1L);  // Compiler error!
```

### ✅ Correct
```java
Optional<User> userOpt = userRepository.findById(1L);
User user = userOpt.orElseThrow(() -> new UserNotFoundException(1L));
```

## Summary

| Concept | What It Means |
|---------|---------------|
| **Repository Pattern** | Abstraction layer for data access |
| **JpaRepository** | Spring interface providing CRUD methods automatically |
| **Method Name Parsing** | Spring generates SQL from method names |
| **Optional<T>** | Container that may or may not contain a value (prevents null errors) |
| **save()** | Insert or update entity (scheduled) |
| **saveAndFlush()** | Insert or update entity (immediate) |

## Key Takeaway

The Repository Pattern with Spring Data JPA is like having a smart assistant that:
1. Knows how to talk to your database
2. Writes SQL queries for you
3. Handles all the low-level JDBC code
4. Gives you a clean, simple API

You focus on **what data you want**, not **how to get it**.
