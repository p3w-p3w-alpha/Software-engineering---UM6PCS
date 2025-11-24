# SAMS - Student Academic Management System
## Complete Setup and Running Guide

This guide will walk you through setting up and running the SAMS application from scratch.

---

## Prerequisites

Before running the project, ensure you have the following installed:

### 1. **Java Development Kit (JDK) 17 or higher**
- Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
- Verify installation:
  ```bash
  java -version
  ```
  Should show: `java version "17.x.x"` or higher

### 2. **Apache Maven 3.6+**
- Download from: https://maven.apache.org/download.cgi
- Verify installation:
  ```bash
  mvn -version
  ```

### 3. **PostgreSQL Database 12+**
- Download from: https://www.postgresql.org/download/
- During installation, remember your PostgreSQL password (default user is `postgres`)

### 4. **A Web Browser** (Chrome, Firefox, Edge, etc.)

### 5. **A Code Editor** (Optional, for viewing code)
- IntelliJ IDEA, Eclipse, VS Code, etc.

---

## Step 1: Database Setup

### Option A: Using pgAdmin (GUI)

1. **Open pgAdmin** (installed with PostgreSQL)

2. **Connect to PostgreSQL Server**
   - Use the password you set during installation

3. **Create Database**
   - Right-click on "Databases" → "Create" → "Database"
   - Database name: `sams_db`
   - Owner: `postgres`
   - Click "Save"

### Option B: Using Command Line (psql)

1. **Open Command Prompt/Terminal**

2. **Connect to PostgreSQL**
   ```bash
   psql -U postgres
   ```
   Enter your PostgreSQL password when prompted

3. **Create Database**
   ```sql
   CREATE DATABASE sams_db;
   ```

4. **Verify Database Created**
   ```sql
   \l
   ```
   You should see `sams_db` in the list

5. **Exit psql**
   ```sql
   \q
   ```

---

## Step 2: Configure Database Connection (If Needed)

The default configuration is:
- **Database Name:** `sams_db`
- **Username:** `postgres`
- **Password:** `postgres`
- **Port:** `5432`

**If your PostgreSQL uses different credentials:**

1. Open `src/main/resources/application.properties`

2. Update these lines:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
   spring.datasource.username=YOUR_USERNAME
   spring.datasource.password=YOUR_PASSWORD
   ```

---

## Step 3: Build the Project

Open a Command Prompt/Terminal in the project root directory:

```bash
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"
```

### Clean and Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run tests (if any)
- Create an executable JAR file

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

---

## Step 4: Run the Application

### Option A: Using Maven (Recommended for Development)

```bash
mvn spring-boot:run
```

### Option B: Using the JAR file

```bash
java -jar target/student-academic-management-0.0.1-SNAPSHOT.jar
```

### Option C: Using an IDE (IntelliJ IDEA / Eclipse)

1. Import the project as a Maven project
2. Find `src/main/java/com/sams/SamsApplication.java`
3. Right-click → "Run 'SamsApplication'"

---

## Step 5: Verify Application is Running

When the application starts successfully, you should see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

...
Started SamsApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

**Important:** Keep this terminal/command prompt window open while using the application!

---

## Step 6: Access the Web Application

### Open Your Web Browser and Navigate to:

```
http://localhost:8080/
```

or directly open:

```
http://localhost:8080/index.html
```

You should see the **SAMS Login Page**!

---

## Step 7: Create Your First Account

Since the database is empty, you need to register:

1. **On the Login Page**, click the **"Register"** tab

2. **Fill in the Registration Form:**
   - Username: (e.g., `john_doe`)
   - Password: (e.g., `password123`)
   - Email: (e.g., `john@example.com`)
   - First Name: John
   - Last Name: Doe
   - Role: Select **STUDENT** (or FACULTY if you want to test faculty features)

3. **Click "Register"**

4. **Login** with your new credentials

---

## Step 8: Explore the Application

After logging in, you'll see the **Dashboard**. Navigate through all the features:

### Available Pages:

1. **Dashboard** (`/dashboard.html`)
   - View statistics (enrollments, assignments, groups, connections)
   - Recent notifications
   - Upcoming deadlines

2. **Courses** (`/courses.html`)
   - Browse available courses
   - Enroll in courses
   - Filter by department and semester

3. **My Enrollments** (`/enrollments.html`)
   - View your active enrollments
   - Monitor credit hours
   - Drop courses

4. **Assignments** (`/assignments.html`)
   - View pending assignments
   - Submit assignments with file upload
   - Check grades

5. **Grades** (`/grades.html`)
   - View your GPA (Cumulative & Semester)
   - See all grades with details
   - View grade history

6. **Study Groups** (`/study-groups.html`)
   - Create study groups
   - Join existing groups
   - Chat with group members

7. **Connections** (`/connections.html`)
   - Send connection requests
   - Accept/reject requests
   - Search for users
   - Block/unblock users

8. **Messages** (`/messages.html`)
   - Private messaging with connections
   - Real-time chat interface
   - Mark messages as read

9. **Notifications** (`/notifications.html`)
   - View all notifications
   - Configure notification preferences
   - Mark as read/delete

---

## Step 9: Create Sample Data (Optional)

For better testing, you might want to create:

### Multiple Users:
1. Register 2-3 more accounts with role **STUDENT**
2. Register 1 account with role **FACULTY**

### Add Courses (as FACULTY or ADMIN):
- You'll need to use API endpoints or create an admin interface
- Or manually insert data into PostgreSQL

**Quick SQL to add sample courses:**
```sql
-- Connect to the database
\c sams_db

-- Insert a sample course
INSERT INTO courses (course_code, course_name, description, credit_hours, capacity, active, created_at, updated_at)
VALUES ('CS101', 'Introduction to Computer Science', 'Basic programming concepts', 3, 30, true, NOW(), NOW());

INSERT INTO courses (course_code, course_name, description, credit_hours, capacity, active, created_at, updated_at)
VALUES ('MATH201', 'Calculus I', 'Differential and integral calculus', 4, 25, true, NOW(), NOW());
```

---

## Troubleshooting

### Problem: "Port 8080 is already in use"

**Solution 1:** Change the port in `application.properties`:
```properties
server.port=8081
```
Then access at: `http://localhost:8081/`

**Solution 2:** Stop the application using port 8080

On Windows:
```bash
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F
```

### Problem: "Unable to connect to database"

**Solutions:**
1. Verify PostgreSQL is running:
   - Windows: Check Services → PostgreSQL should be running
   - Or run: `pg_ctl status`

2. Verify database exists:
   ```bash
   psql -U postgres -l
   ```

3. Check credentials in `application.properties`

4. Check PostgreSQL is listening on port 5432:
   ```bash
   netstat -an | findstr :5432
   ```

### Problem: "BUILD FAILURE" during mvn clean install

**Solutions:**
1. Check Java version: `java -version` (must be 17+)
2. Check Maven version: `mvn -version`
3. Clear Maven cache:
   ```bash
   mvn clean
   mvn dependency:purge-local-repository
   ```

### Problem: "Cannot find main manifest attribute"

**Solution:** Use the correct command:
```bash
mvn spring-boot:run
```
Instead of:
```bash
java -jar target/student-academic-management-0.0.1-SNAPSHOT.jar
```

### Problem: "CORS Error" in Browser Console

**Solution:** The backend already has CORS configured for `http://localhost:8080`. If you changed the port, update the CORS configuration in the SecurityConfig class.

---

## File Upload Directory

The application stores uploaded assignment files in:
```
./uploads/assignments/
```

This directory will be created automatically when you submit your first assignment.

---

## Stopping the Application

In the terminal/command prompt where the app is running:
- Press **`Ctrl + C`**
- Confirm with **`Y`** if prompted

---

## Quick Reference

| Component | Value |
|-----------|-------|
| Application URL | http://localhost:8080/ |
| Database Name | sams_db |
| Database Port | 5432 |
| Application Port | 8080 |
| Default DB User | postgres |
| Max File Upload | 10MB |
| JWT Expiration | 24 hours |

---

## API Endpoints (for testing with Postman/curl)

Base URL: `http://localhost:8080/api`

### Authentication:
- POST `/auth/register` - Register new user
- POST `/auth/login` - Login and get JWT token

### Courses:
- GET `/courses` - Get all courses
- GET `/courses/{id}` - Get course by ID
- POST `/courses` - Create course (FACULTY/ADMIN)

### Enrollments:
- GET `/enrollments/student/{studentId}` - Get student enrollments
- POST `/enrollments?studentId={id}&courseId={id}` - Enroll in course

### Assignments:
- GET `/assignments/student/{studentId}` - Get student assignments
- POST `/submissions/submit?assignmentId={id}&studentId={id}` - Submit assignment

### Study Groups:
- GET `/study-groups/public` - Get public groups
- POST `/study-groups?creatorId={id}` - Create group
- POST `/study-groups/{id}/join?userId={id}` - Join group

### Messages:
- GET `/messages/user/{userId}/conversations` - Get conversations
- POST `/messages/send` - Send message

### Notifications:
- GET `/notifications?userId={id}` - Get notifications
- PUT `/notifications/{id}/read` - Mark as read

---

## Need Help?

If you encounter issues:
1. Check the terminal/console for error messages
2. Check the browser console (F12) for JavaScript errors
3. Verify PostgreSQL is running
4. Ensure port 8080 is not in use
5. Check application.properties configuration

---

## Project Structure

```
week3-4-5-implementation/
├── src/
│   ├── main/
│   │   ├── java/com/sams/
│   │   │   ├── controller/      # REST API Controllers
│   │   │   ├── service/         # Business Logic
│   │   │   ├── repository/      # Database Access
│   │   │   ├── model/           # Entity Classes
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── security/        # JWT & Security Config
│   │   │   └── SamsApplication.java
│   │   ├── resources/
│   │   │   └── application.properties
│   │   └── webapp/              # Frontend (HTML/CSS/JS)
│   │       ├── index.html
│   │       ├── dashboard.html
│   │       ├── courses.html
│   │       ├── assignments.html
│   │       ├── grades.html
│   │       ├── study-groups.html
│   │       ├── connections.html
│   │       ├── messages.html
│   │       ├── notifications.html
│   │       ├── enrollments.html
│   │       ├── css/main.css
│   │       └── js/api.js
│   └── test/
├── uploads/                     # File storage (auto-created)
├── pom.xml
└── RUNNING_GUIDE.md (this file)
```

---

## Success!

You should now have a fully functional Student Academic Management System running! 🎉

Enjoy exploring all the features!
