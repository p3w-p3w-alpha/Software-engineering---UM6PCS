# Complete Development Environment Setup Guide

This guide walks you through setting up everything you need to run the SAMS project on your machine.

## Prerequisites Installation

### 1. Java Development Kit (JDK) 17

**Why Java 17?**
- Spring Boot 3.x requires Java 17 or higher
- It's the current Long-Term Support (LTS) version
- Provides modern language features and performance improvements

**Installation Steps:**

**Windows:**
```bash
# Download from Oracle or use OpenJDK
# Visit: https://adoptium.net/
# Download Eclipse Temurin JDK 17
# Run the installer and follow the wizard
```

**Verify Installation:**
```bash
java -version
# Should show: openjdk version "17.x.x" or similar
```

**Set JAVA_HOME:**
- Windows: Control Panel → System → Advanced System Settings → Environment Variables
- Add new system variable: `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-17.x.x`
- Add to Path: `%JAVA_HOME%\bin`

### 2. IntelliJ IDEA

**Why IntelliJ IDEA?**
- Best IDE for Java development
- Excellent Spring Boot support
- Built-in Maven integration
- Smart code completion and refactoring

**Installation Steps:**
1. Download from: https://www.jetbrains.com/idea/download/
2. Choose Community Edition (free) or Ultimate (paid with student license)
3. Run installer
4. During setup, select:
   - Maven plugin (should be included by default)
   - Spring plugin (Ultimate edition)

### 3. PostgreSQL Database

**Why PostgreSQL?**
- Free and open-source
- Robust and reliable for production
- Excellent performance
- Great documentation and community support

**Installation Steps:**

**Windows:**
```bash
# Download from: https://www.postgresql.org/download/windows/
# Run the installer
# During installation:
# - Set password for postgres user (remember this!)
# - Port: 5432 (default)
# - Install pgAdmin 4 (database management tool)
```

**Verify Installation:**
```bash
psql --version
# Should show: psql (PostgreSQL) 15.x or 16.x
```

**Create Database:**
```sql
-- Open pgAdmin or use psql command line
-- Login as postgres user

CREATE DATABASE sams_db;

-- Verify
\l  -- Lists all databases
```

### 4. Maven (Build Tool)

**Why Maven?**
- Industry standard for Java projects
- Manages dependencies automatically
- Consistent build process
- Huge repository of libraries (Maven Central)

**Installation Steps:**

**Windows:**
```bash
# Download from: https://maven.apache.org/download.cgi
# Extract to C:\Program Files\Apache\maven
# Add to Path: C:\Program Files\Apache\maven\bin
```

**Verify Installation:**
```bash
mvn -version
# Should show Maven version 3.x.x
```

### 5. Git Version Control

**Installation:**
```bash
# Download from: https://git-scm.com/downloads
# Run installer with default settings
```

**Verify:**
```bash
git --version
```

**Configure Git:**
```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 6. Postman (API Testing)

**Why Postman?**
- Easy to test REST APIs
- Save and organize requests
- Visualize responses
- Essential for API development

**Installation:**
```bash
# Download from: https://www.postman.com/downloads/
# Install and create free account
```

## Project Setup

### Step 1: Open Project in IntelliJ

1. Open IntelliJ IDEA
2. Click "Open" or "File → Open"
3. Navigate to `week3-development` folder
4. Select the folder and click OK
5. IntelliJ will detect pom.xml and import as Maven project

**Wait for Indexing:**
- IntelliJ will download all dependencies from Maven Central
- This may take 5-10 minutes the first time
- You'll see progress in the bottom status bar

### Step 2: Configure Database Connection

**Update application.properties if needed:**

```properties
# Located at: src/main/resources/application.properties

# Change these if your PostgreSQL settings are different:
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD_HERE  # Change this!
```

### Step 3: Verify Maven Dependencies

**Check pom.xml is loaded:**
- Look at the right side of IntelliJ for "Maven" tab
- Click it to see project structure
- You should see all dependencies listed

**Reload Maven Project (if needed):**
- Right-click on pom.xml
- Select "Maven → Reload Project"

### Step 4: Run the Application

**Method 1: Using IntelliJ**
1. Open `SamsApplication.java`
2. Look for the green play button next to `public class SamsApplication`
3. Click it and select "Run 'SamsApplication'"

**Method 2: Using Maven Command**
```bash
# In terminal/command prompt at project root:
mvn spring-boot:run
```

**Success Indicators:**
```
Started SamsApplication in X.XXX seconds
Tomcat started on port(s): 8080
```

**If you see this, congratulations! Your server is running!**

### Step 5: Test Database Connection

**Application will create table automatically:**
- Thanks to `spring.jpa.hibernate.ddl-auto=update` in application.properties
- Check pgAdmin to see the `users` table created

**Verify in pgAdmin:**
1. Open pgAdmin
2. Connect to local PostgreSQL
3. Navigate: Servers → PostgreSQL → Databases → sams_db → Schemas → public → Tables
4. You should see `users` table

## Environment Variables (Optional but Recommended)

**For security, avoid hardcoding passwords:**

Create `application-local.properties`:
```properties
spring.datasource.password=${DB_PASSWORD}
```

Set environment variable:
```bash
# Windows
set DB_PASSWORD=your_password

# Linux/Mac
export DB_PASSWORD=your_password
```

## Common Setup Issues

### Issue: "JAVA_HOME not set"
**Solution:**
- Set JAVA_HOME environment variable
- Restart IntelliJ/Terminal after setting

### Issue: "Could not connect to database"
**Solution:**
- Verify PostgreSQL is running (check services)
- Check username/password in application.properties
- Verify database `sams_db` exists
- Check port 5432 is not blocked

### Issue: "Port 8080 already in use"
**Solution:**
- Change port in application.properties: `server.port=8081`
- Or kill process using port 8080

### Issue: Dependencies not downloading
**Solution:**
- Check internet connection
- Try: File → Invalidate Caches → Restart
- Delete `.m2` repository folder and reload

## Next Steps

Once setup is complete:
1. Read [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) to understand the code organization
2. Check [API_DOCUMENTATION.md](API_DOCUMENTATION.md) to test your first endpoint
3. Review [DATABASE_GUIDE.md](DATABASE_GUIDE.md) to understand database integration

## Quick Reference Commands

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Package as JAR
mvn package
```

---
**Estimated Setup Time: 1-2 hours (including downloads)**
