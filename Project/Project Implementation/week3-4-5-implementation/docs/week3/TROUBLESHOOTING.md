# Troubleshooting Guide

Common issues and their solutions when running the SAMS application.

---

## Application Won't Start

### Issue: "Port 8080 already in use"

**Error message:**
```
Web server failed to start. Port 8080 was already in use.
```

**Cause:**
Another application is using port 8080 (maybe previous run didn't stop properly).

**Solutions:**

**Option 1: Change the port**
```properties
# Add to application.properties
server.port=8081
```

**Option 2: Kill the process using port 8080**

*Windows:*
```bash
# Find process
netstat -ano | findstr :8080

# Kill process (replace PID with actual number)
taskkill /PID <PID> /F
```

*Linux/Mac:*
```bash
# Find and kill process
lsof -i :8080
kill -9 <PID>
```

**Option 3: Stop from IntelliJ**
- Look for red stop button (■) in toolbar
- Click to stop all running processes

---

### Issue: "Failed to configure a DataSource"

**Error message:**
```
Failed to configure a DataSource: 'url' attribute is not specified
```

**Cause:**
Database configuration is missing or incorrect in application.properties.

**Solution:**

Check `src/main/resources/application.properties` contains:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=your_password_here
spring.datasource.driver-class-name=org.postgresql.Driver
```

**Verify:**
1. Database name is correct (sams_db exists)
2. Username is correct (default: postgres)
3. Password matches your PostgreSQL installation
4. PostgreSQL is running

---

### Issue: "Driver org.postgresql.Driver not found"

**Error message:**
```
java.lang.ClassNotFoundException: org.postgresql.Driver
```

**Cause:**
PostgreSQL dependency missing or not downloaded.

**Solution:**

1. Check pom.xml has PostgreSQL dependency:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

2. Reload Maven:
   - Right-click pom.xml in IntelliJ
   - Select "Maven" → "Reload Project"

3. Wait for dependencies to download (check bottom status bar)

---

### Issue: "Could not create connection to database"

**Error message:**
```
Could not open JDBC Connection for transaction
org.postgresql.util.PSQLException: Connection refused
```

**Cause:**
PostgreSQL server is not running.

**Solution:**

**Windows:**
```bash
# Check if PostgreSQL is running
services.msc
# Look for "postgresql-x64-XX" service
# If not running, right-click → Start
```

**Alternative - Using pgAdmin:**
- Open pgAdmin
- If it connects, PostgreSQL is running
- If it can't connect, start PostgreSQL service

**Linux:**
```bash
# Check status
sudo systemctl status postgresql

# Start if not running
sudo systemctl start postgresql
```

**Mac:**
```bash
# Using Homebrew
brew services start postgresql
```

---

### Issue: "Database 'sams_db' does not exist"

**Error message:**
```
org.postgresql.util.PSQLException: FATAL: database "sams_db" does not exist
```

**Solution:**

**Option 1: Create database via pgAdmin**
1. Open pgAdmin
2. Right-click "Databases"
3. Create → Database
4. Name: sams_db
5. Click Save

**Option 2: Create via psql**
```bash
psql -U postgres
CREATE DATABASE sams_db;
\q
```

**Option 3: Let Spring create it (add to application.properties)**
```properties
spring.jpa.hibernate.ddl-auto=create
# WARNING: This recreates database on every start (deletes data!)
```

---

### Issue: "Authentication failed for user 'postgres'"

**Error message:**
```
org.postgresql.util.PSQLException: FATAL: password authentication failed
```

**Cause:**
Wrong password in application.properties.

**Solution:**

1. **Check password:**
   - Open pgAdmin
   - If it asks for password, that's your password
   - Update application.properties

2. **Reset PostgreSQL password (if forgotten):**
   ```bash
   # Windows - edit pg_hba.conf (C:\Program Files\PostgreSQL\XX\data\)
   # Change "md5" to "trust" temporarily
   # Restart PostgreSQL service
   # Run: psql -U postgres
   # Change password: ALTER USER postgres PASSWORD 'new_password';
   # Change pg_hba.conf back to "md5"
   # Restart PostgreSQL
   ```

---

## Maven / Dependency Issues

### Issue: "Cannot resolve symbol 'SpringApplication'"

**Cause:**
Maven dependencies not downloaded or indexed.

**Solution:**

1. **Reload Maven project:**
   - Right-click pom.xml
   - Maven → Reload Project

2. **Invalidate caches:**
   - File → Invalidate Caches → Invalidate and Restart

3. **Manually download dependencies:**
   ```bash
   mvn clean install
   ```

4. **Check internet connection** (Maven needs to download from Maven Central)

---

### Issue: "Unresolved dependencies"

**Error in pom.xml:**
Red underlines under dependencies

**Solution:**

1. **Wait for Maven to finish downloading**
   - Look at bottom right of IntelliJ
   - "Resolving Maven dependencies..."

2. **Check Maven settings:**
   - File → Settings → Build Tools → Maven
   - Verify Maven home directory is correct

3. **Clear Maven cache:**
   ```bash
   # Delete .m2 repository folder
   # Windows: C:\Users\YourName\.m2\repository
   # Linux/Mac: ~/.m2/repository
   # Then reload Maven project
   ```

---

## Runtime Errors

### Issue: 404 Not Found when calling API

**Error:**
```
404 - Not Found
```

**Causes & Solutions:**

**1. Wrong URL:**
```bash
# Wrong
curl http://localhost:8080/register

# Correct
curl http://localhost:8080/api/users/register
```

**2. Controller not found by Spring:**

Check that UserController is in package `com.sams` or subpackage:
```java
package com.sams.controller;  // ✓ Will be found

package controller;  // ✗ Won't be found (not under com.sams)
```

**3. Application not started:**
- Check IntelliJ console for "Started SamsApplication"

---

### Issue: 400 Bad Request - No error message

**Possible causes:**

**1. Missing Content-Type header:**
```bash
# Wrong
curl -X POST http://localhost:8080/api/users/register \
  -d '{"username":"john"}'

# Correct
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123"}'
```

**2. Invalid JSON:**
```json
// Wrong - missing comma
{
  "username": "john"
  "email": "john@example.com"
}

// Correct
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

**3. Missing required fields:**
- Check all @NotBlank fields are included

---

### Issue: 500 Internal Server Error

**Error:**
```
500 - Internal Server Error
```

**Solution:**

**Always check the console/logs!**

1. Look at IntelliJ console for stack trace
2. Find the root cause (scroll up)
3. Common causes:
   - NullPointerException
   - Database connection lost
   - Constraint violation

**Example debugging:**
```
Caused by: java.lang.NullPointerException
    at com.sams.controller.UserController.registerUser(UserController.java:45)
```
Go to line 45 of UserController and check for null values.

---

### Issue: "Table 'users' doesn't exist"

**Error:**
```
org.postgresql.util.PSQLException: ERROR: relation "users" does not exist
```

**Causes:**

1. **JPA didn't create table:**
   - Check `spring.jpa.hibernate.ddl-auto=update` in application.properties
   - Restart application

2. **Entity not recognized:**
   - Verify User class has @Entity annotation
   - Verify User class is in com.sams package or subpackage

3. **Wrong database schema:**
   - Check if table exists in different schema
   - In pgAdmin: Navigate to correct database

---

### Issue: Validation not working

**Symptoms:**
Invalid data is being saved to database.

**Solutions:**

1. **Missing @Valid annotation:**
```java
// Wrong
public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request)

// Correct
public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request)
```

2. **Missing validation dependency:**
Check pom.xml has:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

3. **Validation annotations on wrong class:**
- Put @NotBlank, @Email, etc. on DTO (RegisterRequest)
- Not on Entity (User)

---

## Security Issues

### Issue: "Access Denied" on registration endpoint

**Error:**
```
403 - Forbidden
```

**Cause:**
Registration endpoint not configured as public.

**Solution:**

Check SecurityConfig.java:
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/users/register").permitAll()  // Must be before anyRequest()
    .anyRequest().authenticated()
)
```

**Order matters!** permitAll() must come before authenticated().

---

### Issue: Passwords stored as plain text

**Symptoms:**
In database, password column shows "mypassword123" instead of "$2a$10$..."

**Cause:**
Password not being encoded before save.

**Solution:**

Check UserController:
```java
// Must encode password!
user.setPassword(passwordEncoder.encode(request.getPassword()));

// NOT
user.setPassword(request.getPassword());
```

---

### Issue: "There is no PasswordEncoder mapped for the id 'null'"

**Cause:**
PasswordEncoder bean not configured.

**Solution:**

Add to SecurityConfig.java:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## IntelliJ Issues

### Issue: Red underlines everywhere, but code compiles

**Cause:**
IntelliJ index corrupted.

**Solution:**
```
File → Invalidate Caches → Invalidate and Restart
```

---

### Issue: "Cannot resolve symbol" for Java classes

**Cause:**
Project SDK not set.

**Solution:**
1. File → Project Structure
2. Project Settings → Project
3. SDK: Select JDK 17
4. Language level: 17
5. Click OK

---

### Issue: Green run button not showing

**Cause:**
IntelliJ doesn't recognize main method.

**Solution:**

1. **Right-click SamsApplication.java**
2. Select "Run 'SamsApplication'"

**Or manually create configuration:**
1. Run → Edit Configurations
2. Add New → Application
3. Main class: com.sams.SamsApplication
4. Module: select your module

---

## Database Connection Issues

### Issue: "Too many connections"

**Error:**
```
org.postgresql.util.PSQLException: FATAL: sorry, too many clients already
```

**Cause:**
PostgreSQL connection limit reached.

**Solution:**

1. **Restart PostgreSQL service**

2. **Configure connection pool:**
```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
```

3. **Close unused connections in pgAdmin**

---

## Git Issues

### Issue: Cannot commit - files not staged

**Solution:**
```bash
# Stage all files
git add .

# Or specific files
git add src/main/java/com/sams/entity/User.java

# Then commit
git commit -m "Add User entity"
```

---

### Issue: Accidentally committed sensitive data

**If you committed passwords:**

1. **Immediately change the password**
2. **Remove from Git history:**
```bash
# Remove file from Git (keeps local copy)
git rm --cached src/main/resources/application.properties

# Add to .gitignore
echo "application-local.properties" >> .gitignore

# Create new commit
git commit -m "Remove sensitive config from Git"
```

3. **Use environment variables instead**

---

## Postman Issues

### Issue: Request returns HTML instead of JSON

**Cause:**
Request going to wrong URL or Spring Security returning login page.

**Solution:**

1. **Check URL is correct**
2. **Check endpoint is public in SecurityConfig**
3. **Check Headers tab has Content-Type: application/json**

---

### Issue: "Could not get response"

**Cause:**
Application not running or wrong host/port.

**Solution:**

1. Verify application is running (check IntelliJ console)
2. Check URL: http://localhost:8080 (not https)
3. Check port matches `server.port` in application.properties

---

## Common Mistakes

### ❌ Package structure wrong
```
src/main/java/
└── controller/
    └── UserController.java
```

**Should be:**
```
src/main/java/
└── com/
    └── sams/
        └── controller/
            └── UserController.java
```

---

### ❌ Using wrong annotations
```java
import javax.persistence.Entity;  // ✗ Old (Java EE)
```

**Should be:**
```java
import jakarta.persistence.Entity;  // ✓ New (Jakarta EE)
```

---

### ❌ Returning entity in API response
```java
return ResponseEntity.ok(user);  // ✗ Exposes password!
```

**Should be:**
```java
UserResponse response = new UserResponse(...);
return ResponseEntity.ok(response);  // ✓ DTO without password
```

---

## Debugging Tips

### Enable Debug Logging

Add to application.properties:
```properties
logging.level.org.springframework=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

This shows:
- Spring bean initialization
- SQL queries generated
- Parameter values in queries

---

### Check What Tables Exist

```sql
-- In pgAdmin or psql
\dt

-- Or
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public';
```

---

### Verify Data in Database

```sql
-- See all users
SELECT * FROM users;

-- Check password is hashed
SELECT username, password FROM users;
-- Password should start with $2a$10$...

-- Count users
SELECT COUNT(*) FROM users;
```

---

### Test Endpoint from Command Line

```bash
# Quick test
curl http://localhost:8080/api/users/register

# Should return 405 Method Not Allowed (GET not allowed)
# Or 400 Bad Request
# NOT 404 Not Found (that means endpoint doesn't exist)
```

---

## Getting Help

### Before asking for help:

1. **Check the console/logs**
   - Read the error message
   - Look for the root cause
   - Search for the exception online

2. **Verify the basics:**
   - Is PostgreSQL running?
   - Is the application running?
   - Are dependencies downloaded?

3. **Try the obvious:**
   - Restart application
   - Restart database
   - Restart IntelliJ
   - Reload Maven

4. **Search online:**
   - Copy exact error message to Google
   - Check Stack Overflow
   - Check Spring Boot documentation

### When asking for help, provide:

1. **Exact error message** (don't paraphrase)
2. **Full stack trace** (not just first line)
3. **What you've tried**
4. **Relevant code**
5. **Configuration** (application.properties, pom.xml)

---

## Prevention

### Best Practices to Avoid Issues:

1. **Commit often** - Small commits are easier to debug
2. **Test after each change** - Don't make multiple changes at once
3. **Read error messages** - They usually tell you exactly what's wrong
4. **Keep dependencies updated** - But test after updating
5. **Use version control** - You can always roll back
6. **Document as you go** - Future you will thank present you

---

**Remember: Every developer encounters these issues. The difference is knowing how to debug them systematically!**
