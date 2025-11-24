# Complete Commands to Run SAMS Project

**Goal:** Run the Spring Boot application and test all functionality

---

## ✅ VERIFIED: Your System Status

Based on checks:
- ✅ **Java 25** installed (OpenJDK) - Compatible with project
- ⚠️ **Maven** not installed in WSL
- ✅ **PostgreSQL** should be installed on Windows
- ✅ **Project files** present

---

## Method 1: Using IntelliJ IDEA (EASIEST - RECOMMENDED)

This is the easiest method and doesn't require Maven installation.

### Step 1: Open Project in IntelliJ

1. Open **IntelliJ IDEA**
2. Click **"Open"** or **"File → Open"**
3. Navigate to:
   ```
   C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation
   ```
4. Click **"Open"**
5. Wait for IntelliJ to:
   - Detect Maven project
   - Download dependencies (watch bottom right corner)
   - Index files (this may take 2-3 minutes first time)

### Step 2: Verify Database Configuration

1. In IntelliJ, navigate to: `src/main/resources/application.properties`
2. Check line 10:
   ```properties
   spring.datasource.password=postgres
   ```
3. **IMPORTANT:** If your PostgreSQL password is different, change it here!

### Step 3: Create Database

Open **pgAdmin** (or PostgreSQL command line):

**Option A: Using pgAdmin (GUI)**
1. Open pgAdmin
2. Connect to PostgreSQL server (localhost)
3. Right-click **"Databases"** → **"Create"** → **"Database"**
4. Name: `sams_db`
5. Click **"Save"**

**Option B: Using psql (Command Line)**

Open **Windows Command Prompt** (not WSL):
```cmd
psql -U postgres
```
Enter your PostgreSQL password, then:
```sql
CREATE DATABASE sams_db;
\l
```
You should see `sams_db` in the list.

Type `\q` to exit.

### Step 4: Run Application in IntelliJ

1. In IntelliJ Project Explorer, navigate to:
   ```
   src/main/java/com/sams/SamsApplication.java
   ```

2. **Right-click** on `SamsApplication.java`

3. Click **"Run 'SamsApplication'"** (or press **Shift + F10**)

4. Watch the **Run** console at the bottom

### Step 5: Verify Application Started Successfully

Look for these lines in the console:

```
✅ Hibernate: create table if not exists users ...
✅ Hibernate: create table if not exists courses ...
✅ Hibernate: create table if not exists enrollments ...
✅ Started SamsApplication in X.XXX seconds (JVM running for X.XXX)
✅ Tomcat started on port(s): 8080 (http) with context path ''
```

**SUCCESS!** Your application is running! 🎉

### Step 6: Test with Browser (Quick Test)

Open your browser and go to:
```
http://localhost:8080/api/users
```

**Expected Result:**
```json
[]
```
(Empty array because no users exist yet)

If you see this, your API is working! ✅

---

## Method 2: Install Maven and Run from Command Line

If you prefer command line or don't have IntelliJ.

### Step 1: Install Maven

**Option A: Using Chocolatey (Windows Package Manager)**

Open **PowerShell as Administrator**:
```powershell
# Install Chocolatey (if not installed)
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven -y

# Verify installation
mvn -version
```

**Option B: Manual Installation**

1. Download Maven from: https://maven.apache.org/download.cgi
   - Get the `apache-maven-3.x.x-bin.zip` file

2. Extract to: `C:\Program Files\Apache\maven`

3. Add to System PATH:
   - Right-click **"This PC"** → **Properties**
   - Click **"Advanced system settings"**
   - Click **"Environment Variables"**
   - Under **"System variables"**, find **"Path"**
   - Click **"Edit"** → **"New"**
   - Add: `C:\Program Files\Apache\maven\bin`
   - Click **"OK"** on all windows

4. **Restart** Command Prompt

5. Verify:
   ```cmd
   mvn -version
   ```

### Step 2: Create Database

Same as Method 1, Step 3 above.

### Step 3: Configure Application

Open in any text editor:
```
C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation\src\main\resources\application.properties
```

Check line 10 has your PostgreSQL password:
```properties
spring.datasource.password=YOUR_PASSWORD_HERE
```

### Step 4: Run with Maven Commands

Open **Command Prompt** (Windows) and navigate to project:

```cmd
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"
```

**First time: Download dependencies**
```cmd
mvn clean install
```
This will take 2-5 minutes. You'll see Maven downloading many files.

**Run the application:**
```cmd
mvn spring-boot:run
```

Watch for success messages (same as Method 1, Step 5).

---

## Method 3: Create Executable JAR and Run

Build once, run anytime without Maven.

### Step 1: Build JAR

In **Command Prompt**:
```cmd
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"

mvn clean package -DskipTests
```

This creates: `target\student-academic-management-0.0.1-SNAPSHOT.jar`

### Step 2: Run JAR

```cmd
java -jar target\student-academic-management-0.0.1-SNAPSHOT.jar
```

This runs the application without needing Maven!

---

## Testing the Running Application

Once application is running (any method above), you have 3 ways to test:

### Option 1: Postman (Recommended)

1. Open **Postman**

2. Click **"Import"**

3. Import both collections:
   ```
   C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation\postman\SAMS_User_Management.postman_collection.json

   C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation\postman\SAMS_Course_Enrollment_Management.postman_collection.json
   ```

4. **Test 1: Create a Student**
   - Expand: **SAMS User Management**
   - Click: **Create User (Student)**
   - Click **"Send"**
   - Expected: `201 Created`

5. **Test 2: Get All Users**
   - Click: **Get All Users**
   - Click **"Send"**
   - Expected: `200 OK` with user array

6. Continue with other requests!

### Option 2: Using cURL (Command Line)

Open **Command Prompt** or **PowerShell**:

**Create a student:**
```cmd
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"john_student\",\"email\":\"john@student.com\",\"password\":\"password123\",\"role\":\"STUDENT\"}"
```

**Get all users:**
```cmd
curl -X GET http://localhost:8080/api/users
```

**Create a course:**
```cmd
curl -X POST http://localhost:8080/api/courses -H "Content-Type: application/json" -d "{\"courseCode\":\"CS101\",\"courseName\":\"Intro to Programming\",\"description\":\"Learn Java\",\"credits\":3,\"capacity\":30}"
```

**Get all courses:**
```cmd
curl -X GET http://localhost:8080/api/courses
```

### Option 3: Browser (Simple GET Requests Only)

Open browser and visit:

- All users: http://localhost:8080/api/users
- All courses: http://localhost:8080/api/courses
- All enrollments: http://localhost:8080/api/enrollments

---

## Complete Testing Workflow Commands

Here's a **complete workflow** using cURL (copy-paste ready):

### 1. Create Student User
```cmd
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"alice_student\",\"email\":\"alice@example.com\",\"password\":\"password123\",\"role\":\"STUDENT\"}"
```

**Expected Output:**
```json
{
  "id": 1,
  "username": "alice_student",
  "email": "alice@example.com",
  "role": "STUDENT",
  "createdAt": "2024-11-17T..."
}
```

**Note the ID (e.g., 1)**

### 2. Create Faculty User
```cmd
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"dr_smith\",\"email\":\"smith@example.com\",\"password\":\"password123\",\"role\":\"FACULTY\"}"
```

**Note the ID (e.g., 2)**

### 3. Get All Users
```cmd
curl -X GET http://localhost:8080/api/users
```

**Expected:** Array with both users

### 4. Create Course with Instructor
```cmd
curl -X POST http://localhost:8080/api/courses -H "Content-Type: application/json" -d "{\"courseCode\":\"CS101\",\"courseName\":\"Introduction to Programming\",\"description\":\"Learn Java basics\",\"credits\":3,\"capacity\":30,\"instructorId\":2}"
```

**Expected Output:**
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

**Note the course ID (e.g., 1)**

### 5. Enroll Student in Course
```cmd
curl -X POST http://localhost:8080/api/enrollments -H "Content-Type: application/json" -d "{\"studentId\":1,\"courseId\":1}"
```

**Expected Output:**
```json
{
  "id": 1,
  "student": {
    "id": 1,
    "username": "alice_student"
  },
  "course": {
    "id": 1,
    "courseCode": "CS101"
  },
  "status": "ACTIVE"
}
```

### 6. Get Student's Enrollments
```cmd
curl -X GET http://localhost:8080/api/enrollments/student/1
```

### 7. Get Course Details (Check Enrollment Count)
```cmd
curl -X GET http://localhost:8080/api/courses/1
```

**Expected:** `enrolledCount: 1`

### 8. Test Duplicate Enrollment (Should Fail)
```cmd
curl -X POST http://localhost:8080/api/enrollments -H "Content-Type: application/json" -d "{\"studentId\":1,\"courseId\":1}"
```

**Expected:** `400 Bad Request` - "already enrolled"

---

## Running Unit Tests

### In IntelliJ:
1. Right-click on `src/test/java` folder
2. Click **"Run 'All Tests'"**
3. Watch test results (29 tests should pass)

### With Maven Command:
```cmd
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"

mvn test
```

**Expected Output:**
```
Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Stopping the Application

### In IntelliJ:
- Click the red **Stop** button in the Run panel

### In Command Prompt:
- Press **Ctrl + C**

---

## Troubleshooting Commands

### Check if port 8080 is in use:

**Windows PowerShell:**
```powershell
Get-NetTCPConnection -LocalPort 8080
```

**Windows Command Prompt:**
```cmd
netstat -ano | findstr :8080
```

If port is in use, either:
1. Kill the process using that port
2. Change port in `application.properties`: `server.port=8081`

### Check PostgreSQL is running:

**Windows Services:**
```cmd
sc query postgresql-x64-XX
```

Or open **Task Manager** → **Services** tab → Look for "postgresql"

### Check database exists:

```cmd
psql -U postgres -c "\l" | findstr sams_db
```

### View application logs:

IntelliJ: Check the **Run** console
Command line: Logs appear in terminal

Look for errors starting with `ERROR` or `Exception`

---

## Quick Reference: All Essential Commands

```cmd
# Navigate to project
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"

# Check Java
java -version

# Check Maven
mvn -version

# Download dependencies (first time)
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target\student-academic-management-0.0.1-SNAPSHOT.jar

# Test API (get all users)
curl http://localhost:8080/api/users

# Create user
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"test\",\"email\":\"test@example.com\",\"password\":\"password123\",\"role\":\"STUDENT\"}"
```

---

## Summary: Recommended Path

**If you have IntelliJ IDEA:**
1. Open project in IntelliJ
2. Wait for dependencies to download
3. Create database `sams_db` in pgAdmin
4. Run `SamsApplication.java`
5. Test with Postman or browser

**If using command line only:**
1. Install Maven
2. Create database `sams_db`
3. Run `mvn clean install`
4. Run `mvn spring-boot:run`
5. Test with cURL commands above

**Both work perfectly - choose what you're comfortable with!** ✅
