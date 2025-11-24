# Complete Guide: VS Code + WSL for SAMS Project

**Run and test your Spring Boot backend using VS Code with WSL (Windows Subsystem for Linux)**

---

## 🎯 Why WSL in VS Code?

**Advantages:**
- ✅ Linux environment on Windows (better for development)
- ✅ Native bash commands (curl, grep, etc.)
- ✅ Better performance for Java/Maven
- ✅ Same environment as many production servers
- ✅ Integrated seamlessly in VS Code

---

## 📋 Part 1: Setup (One-Time, ~15 minutes)

### Step 1.1: Install WSL Extension in VS Code

1. Open **VS Code**
2. Press `Ctrl+Shift+X` (Extensions)
3. Search: **WSL**
4. Install: **"WSL" by Microsoft** (official extension)
5. Search: **Remote Development**
6. Install: **"Remote Development" by Microsoft**

### Step 1.2: Connect VS Code to WSL

1. Press `Ctrl+Shift+P`
2. Type: **WSL: Connect to WSL**
3. Select your WSL distribution (probably Ubuntu)
4. **New VS Code window opens** - this is running in WSL!

You'll see in bottom-left corner: **"WSL: Ubuntu"** (or your distro name)

### Step 1.3: Install Java Extension in WSL

**Important:** Extensions must be installed separately for WSL!

1. In the WSL VS Code window, press `Ctrl+Shift+X`
2. Search: **Extension Pack for Java**
3. Click **"Install in WSL: Ubuntu"** (not just "Install")
4. Wait for installation

You'll also want:
- **Spring Boot Extension Pack** (optional, helpful)

### Step 1.4: Open Project Folder in WSL

1. In WSL VS Code window, press `Ctrl+K, Ctrl+O`
2. Navigate to: `/mnt/c/Users/nassi/Desktop/ART - CL1 - 01/Software-engineering-UM6PCS/Project/Project Implementation/week3-4-5-implementation`
3. Click "OK"
4. **Trust the workspace** when prompted

VS Code will now:
- Detect Maven project
- Download dependencies (2-5 minutes first time)
- Index Java files

**Wait for this to complete** (watch bottom-right progress)

---

## Part 2: Install Required Tools in WSL

### Step 2.1: Open WSL Terminal in VS Code

Press `Ctrl+` ` (Control + Backtick)

You should see a bash prompt:
```bash
nassim@DESKTOP-XXX:/mnt/c/Users/nassi/.../week3-4-5-implementation$
```

### Step 2.2: Update System Packages

```bash
sudo apt update
sudo apt upgrade -y
```

### Step 2.3: Install Java 17

**Check if Java is installed:**
```bash
java -version
```

**If not installed or wrong version:**
```bash
# Install OpenJDK 17
sudo apt install openjdk-17-jdk -y

# Verify installation
java -version
javac -version
```

**Expected output:**
```
openjdk version "17.0.x"
```

### Step 2.4: Install Maven

**Check if Maven is installed:**
```bash
mvn -version
```

**If not installed:**
```bash
# Install Maven
sudo apt install maven -y

# Verify installation
mvn -version
```

**Expected output:**
```
Apache Maven 3.x.x
```

### Step 2.5: Install curl (for API testing)

```bash
sudo apt install curl -y

# Verify
curl --version
```

### Step 2.6: Install PostgreSQL Client

**For database management from WSL:**
```bash
sudo apt install postgresql-client -y

# Verify
psql --version
```

---

## Part 3: Setup PostgreSQL Database

### Option A: Use Existing Windows PostgreSQL (Recommended)

Your PostgreSQL is running on Windows. We'll connect from WSL.

**Test connection from WSL:**
```bash
# Get Windows host IP (usually works with localhost)
psql -h localhost -U postgres -c "SELECT version();"
```

**If it asks for password**, enter your PostgreSQL password.

**Create database:**
```bash
psql -h localhost -U postgres -c "CREATE DATABASE sams_db;"

# Verify database exists
psql -h localhost -U postgres -c "\l" | grep sams_db
```

### Option B: Install PostgreSQL in WSL (Alternative)

If you prefer everything in WSL:

```bash
# Install PostgreSQL
sudo apt install postgresql postgresql-contrib -y

# Start PostgreSQL
sudo service postgresql start

# Create database
sudo -u postgres psql -c "CREATE DATABASE sams_db;"

# Create user (optional)
sudo -u postgres psql -c "CREATE USER samsuser WITH PASSWORD 'password123';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE sams_db TO samsuser;"
```

---

## Part 4: Configure Application

### Step 4.1: Update application.properties

In VS Code (WSL window), navigate to:
```
src/main/resources/application.properties
```

**If using Windows PostgreSQL (Option A):**
```properties
# Application Configuration
spring.application.name=sams

# Server Port
server.port=8080

# Database Configuration - Windows PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_WINDOWS_POSTGRES_PASSWORD
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.com.sams=DEBUG
```

**If using WSL PostgreSQL (Option B):**
Same configuration, just ensure PostgreSQL service is running in WSL.

**Save the file** (Ctrl+S)

---

## Part 5: Build and Run the Application

### Step 5.1: Navigate to Project (if not already there)

In VS Code WSL terminal:
```bash
cd /mnt/c/Users/nassi/Desktop/ART\ -\ CL1\ -\ 01/Software-engineering-UM6PCS/Project/Project\ Implementation/week3-4-5-implementation
```

**Tip:** Use Tab key for auto-completion!

### Step 5.2: Clean and Install Dependencies

**First time only:**
```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run tests
- Create JAR file

**Takes 2-5 minutes first time.**

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX s
```

### Step 5.3: Run the Application

**Method 1: Using Maven (Recommended for development)**
```bash
mvn spring-boot:run
```

**Method 2: Using JAR file**
```bash
# Build JAR first
mvn clean package -DskipTests

# Run JAR
java -jar target/student-academic-management-0.0.1-SNAPSHOT.jar
```

**Method 3: Using VS Code Run Button**
1. Open: `src/main/java/com/sams/SamsApplication.java`
2. Look for "Run | Debug" above the main method
3. Click **"Run"**

### Step 5.4: Verify Application Started

**Look for these lines in terminal:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

✅ Hibernate: create table if not exists users ...
✅ Hibernate: create table if not exists courses ...
✅ Hibernate: create table if not exists enrollments ...
✅ Started SamsApplication in X.XXX seconds (JVM running for X.XXX)
✅ Tomcat started on port(s): 8080 (http) with context path ''
```

**🎉 SUCCESS! Your application is running!**

---

## Part 6: Test the Application

### Test 6.1: Quick Browser Test

Open any browser on Windows:
```
http://localhost:8080/api/users
```

**Expected:** `[]` (empty array)

**✅ If you see this, your API is working!**

### Test 6.2: Test from WSL Terminal

Open **NEW terminal** in VS Code (don't close the running app):
- Click the "+" icon in terminal panel
- Or press `Ctrl+Shift+` `

**Test endpoint:**
```bash
curl http://localhost:8080/api/users
```

**Expected:** `[]`

---

## Part 7: Complete Functionality Test

### Test 7.1: Create Test Data

**Create Student User:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_student",
    "email": "alice@example.com",
    "password": "password123",
    "role": "STUDENT"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "username": "alice_student",
  "email": "alice@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-17T..."
}
```

**Note the ID (should be 1)**

**Create Faculty User:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dr_smith",
    "email": "smith@example.com",
    "password": "password123",
    "role": "FACULTY"
  }'
```

**Note the ID (should be 2)**

**Get All Users:**
```bash
curl http://localhost:8080/api/users
```

**Should show both users**

**Create Course:**
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS101",
    "courseName": "Introduction to Programming",
    "description": "Learn Java basics and OOP",
    "credits": 3,
    "capacity": 30,
    "instructorId": 2
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "courseCode": "CS101",
  "courseName": "Introduction to Programming",
  "credits": 3,
  "capacity": 30,
  "enrolledCount": 0,
  "isFull": false,
  "instructor": {
    "id": 2,
    "username": "dr_smith",
    "email": "smith@example.com"
  }
}
```

**Get All Courses:**
```bash
curl http://localhost:8080/api/courses
```

**Enroll Student in Course:**
```bash
curl -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "courseId": 1
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "student": {
    "id": 1,
    "username": "alice_student",
    "email": "alice@example.com"
  },
  "course": {
    "id": 1,
    "courseCode": "CS101",
    "courseName": "Introduction to Programming"
  },
  "status": "ACTIVE",
  "enrolledAt": "2024-11-17T..."
}
```

**Get Student's Enrollments:**
```bash
curl http://localhost:8080/api/enrollments/student/1
```

**Get Course (check enrollment count updated):**
```bash
curl http://localhost:8080/api/courses/1
```

**Should show:** `"enrolledCount": 1`

### Test 7.2: Test Business Logic (Error Cases)

**Try Duplicate Enrollment (Should Fail):**
```bash
curl -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "courseId": 1
  }'
```

**Expected:** `400 Bad Request` with message "already enrolled"

**Try Faculty Enrolling (Should Fail):**
```bash
curl -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 2,
    "courseId": 1
  }'
```

**Expected:** `400 Bad Request` with message "Only students can enroll"

**Try Duplicate Course Code (Should Fail):**
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS101",
    "courseName": "Another Course",
    "credits": 3,
    "capacity": 20
  }'
```

**Expected:** `400 Bad Request` with message "Course code already exists"

---

## Part 8: Automated Testing Script for WSL

### Step 8.1: Create Test Script

In VS Code terminal:

```bash
# Create test script
cat > test-api.sh << 'EOF'
#!/bin/bash

# SAMS API Testing Script for WSL
# Run after starting the application

echo "🧪 SAMS API Testing Script"
echo "═══════════════════════════════════════════════════════════"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Check if application is running
echo -e "\n${YELLOW}🔍 Checking if application is running...${NC}"
if curl -s http://localhost:8080/api/users > /dev/null; then
    echo -e "${GREEN}✅ Application is running!${NC}"
else
    echo -e "${RED}❌ ERROR: Application is not running!${NC}"
    echo "Please start the application first: mvn spring-boot:run"
    exit 1
fi

echo -e "\n─────────────────────────────────────"

# Test 1: Create Student
echo -e "\n${YELLOW}📝 Test 1: Creating student user...${NC}"
STUDENT=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_student",
    "email": "alice@example.com",
    "password": "password123",
    "role": "STUDENT"
  }')

STUDENT_ID=$(echo $STUDENT | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}✅ Student created with ID: $STUDENT_ID${NC}"
echo "$STUDENT" | jq '.'

echo -e "\n─────────────────────────────────────"

# Test 2: Create Faculty
echo -e "\n${YELLOW}📝 Test 2: Creating faculty user...${NC}"
FACULTY=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dr_smith",
    "email": "smith@example.com",
    "password": "password123",
    "role": "FACULTY"
  }')

FACULTY_ID=$(echo $FACULTY | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}✅ Faculty created with ID: $FACULTY_ID${NC}"
echo "$FACULTY" | jq '.'

echo -e "\n─────────────────────────────────────"

# Test 3: Get All Users
echo -e "\n${YELLOW}📋 Test 3: Getting all users...${NC}"
USERS=$(curl -s http://localhost:8080/api/users)
USER_COUNT=$(echo $USERS | jq '. | length')
echo -e "${GREEN}✅ Total users: $USER_COUNT${NC}"

echo -e "\n─────────────────────────────────────"

# Test 4: Create Course
echo -e "\n${YELLOW}📝 Test 4: Creating course...${NC}"
COURSE=$(curl -s -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d "{
    \"courseCode\": \"CS101\",
    \"courseName\": \"Introduction to Programming\",
    \"description\": \"Learn Java basics\",
    \"credits\": 3,
    \"capacity\": 30,
    \"instructorId\": $FACULTY_ID
  }")

COURSE_ID=$(echo $COURSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}✅ Course created with ID: $COURSE_ID${NC}"
echo "$COURSE" | jq '.'

echo -e "\n─────────────────────────────────────"

# Test 5: Enroll Student
echo -e "\n${YELLOW}📝 Test 5: Enrolling student in course...${NC}"
ENROLLMENT=$(curl -s -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -d "{
    \"studentId\": $STUDENT_ID,
    \"courseId\": $COURSE_ID
  }")

ENROLLMENT_ID=$(echo $ENROLLMENT | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}✅ Enrollment created with ID: $ENROLLMENT_ID${NC}"
echo "$ENROLLMENT" | jq '.'

echo -e "\n─────────────────────────────────────"

# Test 6: Get Student Enrollments
echo -e "\n${YELLOW}📋 Test 6: Getting student enrollments...${NC}"
STUDENT_ENROLLMENTS=$(curl -s http://localhost:8080/api/enrollments/student/$STUDENT_ID)
ENROLLMENT_COUNT=$(echo $STUDENT_ENROLLMENTS | jq '. | length')
echo -e "${GREEN}✅ Student enrolled in $ENROLLMENT_COUNT course(s)${NC}"

echo -e "\n─────────────────────────────────────"

# Test 7: Get Course Details
echo -e "\n${YELLOW}📋 Test 7: Getting course details...${NC}"
COURSE_DETAILS=$(curl -s http://localhost:8080/api/courses/$COURSE_ID)
ENROLLED_COUNT=$(echo $COURSE_DETAILS | jq '.enrolledCount')
echo -e "${GREEN}✅ Course has $ENROLLED_COUNT student(s) enrolled${NC}"

echo -e "\n─────────────────────────────────────"

# Test 8: Try Duplicate Enrollment
echo -e "\n${YELLOW}📝 Test 8: Testing duplicate enrollment prevention...${NC}"
DUPLICATE=$(curl -s -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -d "{
    \"studentId\": $STUDENT_ID,
    \"courseId\": $COURSE_ID
  }")

if echo $DUPLICATE | grep -q "already enrolled"; then
    echo -e "${GREEN}✅ Duplicate enrollment correctly prevented!${NC}"
else
    echo -e "${RED}❌ ERROR: Duplicate enrollment was allowed!${NC}"
fi

echo -e "\n─────────────────────────────────────"

# Final Summary
echo -e "\n${GREEN}🎉 ALL TESTS COMPLETED SUCCESSFULLY! 🎉${NC}"
echo "═══════════════════════════════════════════════════════════"
echo -e "\n${YELLOW}📊 Test Summary:${NC}"
echo "   ✅ Created $USER_COUNT users"
echo "   ✅ Created 1 course"
echo "   ✅ Created 1 enrollment"
echo "   ✅ Verified enrollment count"
echo "   ✅ Tested duplicate prevention"
echo ""
echo -e "${YELLOW}📝 Created Resources:${NC}"
echo "   Student: alice_student (ID: $STUDENT_ID)"
echo "   Faculty: dr_smith (ID: $FACULTY_ID)"
echo "   Course: CS101 (ID: $COURSE_ID)"
echo "   Enrollment: alice_student → CS101 (ID: $ENROLLMENT_ID)"
echo ""
echo -e "${GREEN}✨ Your SAMS system is working perfectly!${NC}"
echo "═══════════════════════════════════════════════════════════"
EOF

# Make script executable
chmod +x test-api.sh

echo "✅ Test script created: test-api.sh"
```

### Step 8.2: Install jq (for JSON parsing)

```bash
sudo apt install jq -y
```

### Step 8.3: Run Test Script

```bash
./test-api.sh
```

**You should see all tests passing with green checkmarks!** ✅

---

## Part 9: Run Unit Tests

### In VS Code:

1. Navigate to `src/test/java` in Explorer
2. Right-click on any test file
3. Select "Run Tests"
4. Watch Test Results panel

### In Terminal:

```bash
mvn test
```

**Expected output:**
```
Tests run: 29, Failures: 0, Errors: 0, Skipped: 0

[INFO] BUILD SUCCESS
```

---

## Part 10: Using Postman with WSL

Postman runs on Windows, but can test your WSL application!

1. **Open Postman** (on Windows)

2. **Import Collections:**
   - Click "Import"
   - Navigate to project folder (Windows path)
   - Import both JSON files in `postman/` folder

3. **Test endpoints:**
   - URL: `http://localhost:8080/api/users` (works from Windows!)
   - Click "Send"

**WSL application accessible from Windows!** ✅

---

## Part 11: Database Verification

### View Data in Database:

```bash
# Connect to PostgreSQL
psql -h localhost -U postgres -d sams_db

# View users
SELECT * FROM users;

# View courses
SELECT * FROM courses;

# View enrollments
SELECT * FROM enrollments;

# Exit
\q
```

---

## Part 12: Stop the Application

### In Terminal:
```
Ctrl+C
```

### If Running in Background:
```bash
# Find process
ps aux | grep java

# Kill process (replace PID with actual process ID)
kill -9 PID
```

---

## Quick Reference Commands

### Navigate to Project:
```bash
cd /mnt/c/Users/nassi/Desktop/ART\ -\ CL1\ -\ 01/Software-engineering-UM6PCS/Project/Project\ Implementation/week3-4-5-implementation
```

### Start Application:
```bash
mvn spring-boot:run
```

### Run Tests:
```bash
mvn test
```

### Run API Test Script:
```bash
./test-api.sh
```

### Create User:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"password123","role":"STUDENT"}'
```

### Get All Users:
```bash
curl http://localhost:8080/api/users
```

### Get All Courses:
```bash
curl http://localhost:8080/api/courses
```

### Get All Enrollments:
```bash
curl http://localhost:8080/api/enrollments
```

### Pretty Print JSON:
```bash
curl http://localhost:8080/api/users | jq '.'
```

---

## Troubleshooting

### Port 8080 Already in Use:
```bash
# Find process using port 8080
sudo lsof -i :8080

# Kill process
kill -9 PID
```

### PostgreSQL Connection Issues:
```bash
# Test connection
psql -h localhost -U postgres -c "SELECT version();"

# Check Windows PostgreSQL service is running
# In Windows: services.msc → Look for PostgreSQL
```

### Maven Not Found:
```bash
sudo apt install maven -y
```

### Java Not Found:
```bash
sudo apt install openjdk-17-jdk -y
```

### Cannot Access Windows Files:
```bash
# Windows C: drive is mounted at /mnt/c/
ls /mnt/c/Users/nassi/Desktop/
```

---

## VS Code Tips for WSL

### Keyboard Shortcuts:
- `Ctrl+` ` - Toggle terminal
- `Ctrl+Shift+` ` - New terminal
- `Ctrl+P` - Quick file open
- `Ctrl+Shift+P` - Command palette
- `F5` - Debug
- `Ctrl+F5` - Run without debug

### Check WSL Connection:
- Bottom-left corner should show: **"WSL: Ubuntu"**
- If not, click it and select "Reopen in WSL"

### File Sync:
- Files edited in VS Code (WSL) are automatically synced
- Accessible from Windows Explorer at: `\\wsl$\Ubuntu\mnt\c\...`

---

## Summary Checklist

✅ **Setup:**
- [ ] WSL extension installed in VS Code
- [ ] Connected to WSL in VS Code
- [ ] Java 17 installed in WSL
- [ ] Maven installed in WSL
- [ ] Project opened in WSL VS Code

✅ **Configuration:**
- [ ] Database created (`sams_db`)
- [ ] `application.properties` configured with correct password

✅ **Running:**
- [ ] Application starts without errors
- [ ] Browser shows `[]` at http://localhost:8080/api/users
- [ ] curl commands work from WSL terminal

✅ **Testing:**
- [ ] Created users (student + faculty)
- [ ] Created course with instructor
- [ ] Enrolled student in course
- [ ] Verified business logic (duplicates prevented)
- [ ] Unit tests pass (29 tests)
- [ ] `./test-api.sh` runs successfully

✅ **Everything Working!** 🎉

---

## What You've Accomplished

**You now have:**
- ✅ VS Code connected to WSL
- ✅ Full Linux development environment
- ✅ Spring Boot application running
- ✅ PostgreSQL database connected
- ✅ 29 API endpoints working
- ✅ All 29 unit tests passing
- ✅ Automated testing script
- ✅ Complete CRUD operations for users, courses, enrollments

**Ready to develop and test like a pro!** 🚀
