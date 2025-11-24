# Complete Guide: VS Code + Windows PowerShell

**Run SAMS project using Visual Studio Code and Windows PowerShell terminal**

---

## 📋 Prerequisites Checklist

Before starting, ensure you have:

- [ ] **Visual Studio Code** installed
- [ ] **Java JDK 17+** installed
- [ ] **Maven** installed (we'll verify/install)
- [ ] **PostgreSQL** installed
- [ ] **PowerShell** (comes with Windows)

---

## Step 1: Install Required Tools

### 1.1 Install VS Code Extensions

Open **VS Code**, go to Extensions (Ctrl+Shift+X), and install:

1. **Extension Pack for Java** (by Microsoft)
   - Includes: Language Support, Debugging, Maven, Project Manager
   - This is ALL you need for Java development!

2. **Spring Boot Extension Pack** (by VMware) - Optional but helpful
   - Better Spring Boot support
   - Application dashboard

**How to install:**
1. Press `Ctrl+Shift+X`
2. Search: "Extension Pack for Java"
3. Click "Install"
4. Wait for all extensions to install
5. Restart VS Code if prompted

### 1.2 Verify Java Installation

Open **Windows PowerShell** (Win+X → Windows PowerShell):

```powershell
java -version
```

**Expected output:**
```
java version "17.x.x" or higher
```

**If not installed:**
1. Download from: https://adoptium.net/
2. Download: "Temurin 17 (LTS)" for Windows
3. Run installer
4. Restart PowerShell
5. Verify again

### 1.3 Install Maven

**Check if Maven is installed:**
```powershell
mvn -version
```

**If you see "mvn : The term 'mvn' is not recognized"**, install Maven:

#### Option A: Using Chocolatey (Easiest)

**Install Chocolatey first** (if not installed):
```powershell
# Run PowerShell as Administrator (Right-click → Run as Administrator)
Set-ExecutionPolicy Bypass -Scope Process -Force
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

**Then install Maven:**
```powershell
choco install maven -y
```

**Close and reopen PowerShell**, then verify:
```powershell
mvn -version
```

#### Option B: Manual Installation

1. Download Maven from: https://maven.apache.org/download.cgi
   - Get: `apache-maven-3.9.x-bin.zip`

2. Extract to: `C:\Program Files\Apache\Maven`

3. Add to PATH:
   - Press `Win+X` → System
   - Click "Advanced system settings"
   - Click "Environment Variables"
   - Under "System variables", find "Path"
   - Click "Edit" → "New"
   - Add: `C:\Program Files\Apache\Maven\bin`
   - Click OK on all windows

4. **Restart PowerShell**

5. Verify:
```powershell
mvn -version
```

---

## Step 2: Open Project in VS Code

### 2.1 Open Project Folder

1. Open **VS Code**

2. Press `Ctrl+K, Ctrl+O` (or File → Open Folder)

3. Navigate to:
   ```
   C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation
   ```

4. Click "Select Folder"

5. **Trust the workspace** when prompted

### 2.2 Wait for Java/Maven Setup

VS Code will automatically:
- Detect it's a Maven project (because of `pom.xml`)
- Download dependencies (watch bottom right corner)
- Index Java files
- Configure classpath

**This may take 2-5 minutes first time.** Be patient!

You'll see progress in:
- Bottom right corner: "Synchronizing ..."
- Bottom bar: Java project status

### 2.3 Verify Project Loaded

Check bottom left corner of VS Code:
- Should show: "Java Projects" section
- Should list: "student-academic-management"

---

## Step 3: Configure PowerShell as Default Terminal

### 3.1 Set PowerShell as Default

1. In VS Code, press `Ctrl+Shift+P`
2. Type: "Terminal: Select Default Profile"
3. Select: **"PowerShell"**

### 3.2 Open Terminal

Press `Ctrl+` ` (Control + Backtick) or View → Terminal

You should see:
```powershell
PS C:\Users\nassi\Desktop\...\week3-4-5-implementation>
```

**Perfect!** You're now using PowerShell in VS Code.

---

## Step 4: Setup Database

### 4.1 Open pgAdmin

1. Open **pgAdmin** (from Start menu)
2. Connect to PostgreSQL server (default: localhost)
3. Enter your PostgreSQL password

### 4.2 Create Database

1. In pgAdmin left panel, expand "Servers" → "PostgreSQL"
2. Right-click "Databases" → "Create" → "Database..."
3. Enter Database name: `sams_db`
4. Click "Save"

**Verify:** You should see `sams_db` in the databases list

### 4.3 Alternative: Create Database via PowerShell

In VS Code terminal (or any PowerShell):

```powershell
# Connect to PostgreSQL (enter your password when prompted)
psql -U postgres

# In psql prompt, run:
CREATE DATABASE sams_db;

# List databases to verify
\l

# Exit psql
\q
```

---

## Step 5: Configure Application

### 5.1 Update Database Password

1. In VS Code Explorer (left panel), navigate to:
   ```
   src → main → resources → application.properties
   ```

2. Click to open the file

3. Find line 10:
   ```properties
   spring.datasource.password=postgres
   ```

4. **Change `postgres` to your actual PostgreSQL password**

5. Save file (Ctrl+S)

**Example:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=YourActualPassword123
```

---

## Step 6: Build and Run Project

### Method 1: Using VS Code Run Button (Easiest)

1. In VS Code Explorer, navigate to:
   ```
   src → main → java → com → sams → SamsApplication.java
   ```

2. Open the file

3. You'll see "Run | Debug" above the `main` method

4. Click **"Run"**

5. Watch the terminal output

**Look for:**
```
Started SamsApplication in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 8080 (http)
```

**Success!** ✅

### Method 2: Using PowerShell Terminal Commands

In VS Code terminal (`Ctrl+`` `):

**First time - Download dependencies:**
```powershell
mvn clean install
```

This takes 2-5 minutes. You'll see Maven downloading packages.

**Run the application:**
```powershell
mvn spring-boot:run
```

**Wait for:**
```
Started SamsApplication in X.XXX seconds
```

### Method 3: Using VS Code Spring Boot Dashboard (If installed)

1. Look for "Spring Boot Dashboard" in left panel (icon looks like a boot)
2. You'll see your application listed
3. Click the "Play" icon next to "student-academic-management"
4. Application starts in terminal

---

## Step 7: Verify Application is Running

### Test 1: Using Web Browser

Open any browser and go to:
```
http://localhost:8080/api/users
```

**Expected:** `[]` (empty array)

**Success!** Your API is working! ✅

### Test 2: Using PowerShell

In a **NEW PowerShell terminal** (Ctrl+Shift+` in VS Code):

```powershell
curl http://localhost:8080/api/users
```

**Expected:** `[]` or JSON array

---

## Step 8: Test the Application

### 8.1 Using PowerShell Commands

In VS Code terminal (make sure app is running):

**Create Student User:**
```powershell
$body = @{
    username = "alice_student"
    email = "alice@example.com"
    password = "password123"
    role = "STUDENT"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -Body $body -ContentType "application/json"
```

**Expected Output:** JSON with user details and ID

**Create Faculty User:**
```powershell
$body = @{
    username = "dr_smith"
    email = "smith@example.com"
    password = "password123"
    role = "FACULTY"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -Body $body -ContentType "application/json"
```

**Get All Users:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get
```

**Create Course:**
```powershell
$body = @{
    courseCode = "CS101"
    courseName = "Introduction to Programming"
    description = "Learn Java basics"
    credits = 3
    capacity = 30
    instructorId = 2
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method Post -Body $body -ContentType "application/json"
```

**Create Enrollment:**
```powershell
$body = @{
    studentId = 1
    courseId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments" -Method Post -Body $body -ContentType "application/json"
```

**Get Student Enrollments:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments/student/1" -Method Get
```

**Get Course Details:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/courses/1" -Method Get
```

### 8.2 Alternative: Using curl in PowerShell

If you have `curl` available:

```powershell
# Create student
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d '{\"username\":\"alice\",\"email\":\"alice@example.com\",\"password\":\"password123\",\"role\":\"STUDENT\"}'

# Get all users
curl http://localhost:8080/api/users

# Create course
curl -X POST http://localhost:8080/api/courses -H "Content-Type: application/json" -d '{\"courseCode\":\"CS101\",\"courseName\":\"Intro to Programming\",\"description\":\"Learn Java\",\"credits\":3,\"capacity\":30,\"instructorId\":2}'

# Create enrollment
curl -X POST http://localhost:8080/api/enrollments -H "Content-Type: application/json" -d '{\"studentId\":1,\"courseId\":1}'
```

**Note:** In PowerShell, you may need to escape quotes with backslashes.

### 8.3 Best Option: Using Postman

This is the easiest way to test:

1. Open **Postman**

2. Click "Import"

3. Navigate to your project folder and import:
   - `postman\SAMS_User_Management.postman_collection.json`
   - `postman\SAMS_Course_Enrollment_Management.postman_collection.json`

4. **Test workflow:**
   - Click "Create User (Student)" → Send
   - Click "Create User (Faculty)" → Send
   - Click "Create Course (with Instructor)" → Update instructorId → Send
   - Click "Create Enrollment" → Update IDs → Send

5. **Done!** All 41 requests ready to use!

---

## Step 9: Run Unit Tests

### In VS Code:

1. Navigate to `src/test/java` folder

2. Right-click on `java` folder

3. Select "Run Tests"

4. Watch Test Explorer panel show results

**Expected:** 29 tests pass ✅

### In PowerShell Terminal:

```powershell
mvn test
```

**Expected output:**
```
Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Step 10: Stop the Application

### Method 1: VS Code Terminal
- Click in the terminal where app is running
- Press `Ctrl+C`
- Confirm with `Y` if prompted

### Method 2: VS Code Run Panel
- Look for "Run and Debug" panel (Ctrl+Shift+D)
- Click red square "Stop" button

### Method 3: Spring Boot Dashboard
- Click the stop icon next to your running application

---

## Complete PowerShell Testing Script

Save this as `test-api.ps1` in your project folder:

```powershell
# SAMS API Testing Script
# Run this after starting the application

Write-Host "🧪 Testing SAMS API..." -ForegroundColor Cyan

# Test 1: Create Student
Write-Host "`n📝 Creating student..." -ForegroundColor Yellow
$student = @{
    username = "alice_student"
    email = "alice@example.com"
    password = "password123"
    role = "STUDENT"
} | ConvertTo-Json

$studentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -Body $student -ContentType "application/json"
Write-Host "✅ Student created with ID: $($studentResponse.id)" -ForegroundColor Green

# Test 2: Create Faculty
Write-Host "`n📝 Creating faculty..." -ForegroundColor Yellow
$faculty = @{
    username = "dr_smith"
    email = "smith@example.com"
    password = "password123"
    role = "FACULTY"
} | ConvertTo-Json

$facultyResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -Body $faculty -ContentType "application/json"
Write-Host "✅ Faculty created with ID: $($facultyResponse.id)" -ForegroundColor Green

# Test 3: Get All Users
Write-Host "`n📋 Getting all users..." -ForegroundColor Yellow
$users = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get
Write-Host "✅ Total users: $($users.Count)" -ForegroundColor Green

# Test 4: Create Course
Write-Host "`n📝 Creating course..." -ForegroundColor Yellow
$course = @{
    courseCode = "CS101"
    courseName = "Introduction to Programming"
    description = "Learn Java basics"
    credits = 3
    capacity = 30
    instructorId = $facultyResponse.id
} | ConvertTo-Json

$courseResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method Post -Body $course -ContentType "application/json"
Write-Host "✅ Course created with ID: $($courseResponse.id)" -ForegroundColor Green

# Test 5: Enroll Student
Write-Host "`n📝 Enrolling student in course..." -ForegroundColor Yellow
$enrollment = @{
    studentId = $studentResponse.id
    courseId = $courseResponse.id
} | ConvertTo-Json

$enrollmentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments" -Method Post -Body $enrollment -ContentType "application/json"
Write-Host "✅ Enrollment created with ID: $($enrollmentResponse.id)" -ForegroundColor Green

# Test 6: Verify Enrollment
Write-Host "`n📋 Getting student enrollments..." -ForegroundColor Yellow
$enrollments = Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments/student/$($studentResponse.id)" -Method Get
Write-Host "✅ Student enrolled in $($enrollments.Count) course(s)" -ForegroundColor Green

# Test 7: Get Course with Enrollment Count
Write-Host "`n📋 Getting course details..." -ForegroundColor Yellow
$courseDetails = Invoke-RestMethod -Uri "http://localhost:8080/api/courses/$($courseResponse.id)" -Method Get
Write-Host "✅ Course '$($courseDetails.courseName)' has $($courseDetails.enrolledCount) student(s)" -ForegroundColor Green

# Summary
Write-Host "`n🎉 All tests completed successfully!" -ForegroundColor Green
Write-Host "─────────────────────────────────────" -ForegroundColor Cyan
Write-Host "Created: $($users.Count) users, 1 course, 1 enrollment" -ForegroundColor White
Write-Host "Student: $($studentResponse.username) (ID: $($studentResponse.id))" -ForegroundColor White
Write-Host "Faculty: $($facultyResponse.username) (ID: $($facultyResponse.id))" -ForegroundColor White
Write-Host "Course: $($courseResponse.courseCode) - $($courseResponse.courseName)" -ForegroundColor White
Write-Host "Enrollment: Student $($studentResponse.username) → Course $($courseResponse.courseCode)" -ForegroundColor White
Write-Host "─────────────────────────────────────" -ForegroundColor Cyan
```

**To run the script:**
```powershell
.\test-api.ps1
```

---

## Quick Reference: Essential PowerShell Commands

```powershell
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

# Test API
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get

# Stop running process
Ctrl+C
```

---

## Troubleshooting

### Issue: "mvn is not recognized"

**Solution:**
1. Install Maven (see Step 1.3)
2. Restart PowerShell
3. Verify: `mvn -version`

### Issue: Port 8080 already in use

**Check what's using the port:**
```powershell
Get-NetTCPConnection -LocalPort 8080
```

**Kill the process:**
```powershell
Stop-Process -Id <PID> -Force
```

**Or change port in `application.properties`:**
```properties
server.port=8081
```

### Issue: PostgreSQL connection failed

**Check PostgreSQL service:**
```powershell
Get-Service -Name postgresql*
```

**Start if stopped:**
```powershell
Start-Service -Name postgresql-x64-XX
```

### Issue: Database doesn't exist

```powershell
psql -U postgres -c "CREATE DATABASE sams_db;"
```

### Issue: VS Code Java extension not working

1. Press `Ctrl+Shift+P`
2. Type: "Java: Clean Java Language Server Workspace"
3. Click it and restart VS Code

### Issue: Tests fail to run

```powershell
# Clean and rebuild
mvn clean install

# Run tests with more info
mvn test -X
```

---

## VS Code Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+` ` | Toggle terminal |
| `Ctrl+Shift+` ` | New terminal |
| `Ctrl+Shift+P` | Command palette |
| `Ctrl+P` | Quick file open |
| `F5` | Start debugging |
| `Shift+F5` | Stop debugging |
| `Ctrl+Shift+D` | Debug panel |
| `Ctrl+Shift+E` | Explorer panel |
| `Ctrl+B` | Toggle sidebar |

---

## Success Indicators

### Application Started:
```
✅ Hibernate: create table users
✅ Hibernate: create table courses
✅ Hibernate: create table enrollments
✅ Started SamsApplication in X.XXX seconds
✅ Tomcat started on port(s): 8080
```

### Tests Passed:
```
✅ Tests run: 29, Failures: 0, Errors: 0
✅ BUILD SUCCESS
```

### API Working:
```
✅ Browser shows JSON at http://localhost:8080/api/users
✅ PowerShell commands return JSON responses
✅ No errors in VS Code terminal
```

---

## Summary: Your Complete Workflow

1. **Open VS Code** → Open project folder
2. **Open Terminal** (`Ctrl+`` `) → Ensure it's PowerShell
3. **Start Application** → Click "Run" in `SamsApplication.java`
4. **Test API** → Use Postman or PowerShell commands
5. **Stop Application** → `Ctrl+C` in terminal

**Everything runs smoothly in VS Code with PowerShell!** ✅

---

## Next Steps

- ✅ Project running in VS Code
- ✅ Using Windows PowerShell terminal
- ✅ Testing with PowerShell commands or Postman
- ✅ All 29 unit tests passing
- ✅ Complete workflow tested

**You're all set! Happy coding! 🎉**
