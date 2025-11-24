# Complete Testing Walkthrough - SAMS Project

**Goal:** Test the complete workflow from creating users to enrolling students in courses.

**What we'll test:**
1. Create a STUDENT user
2. Create a FACULTY user (instructor)
3. Create a COURSE and assign the instructor
4. ENROLL the student in the course
5. Verify everything works

---

## Prerequisites Checklist

Before starting, make sure you have:

- [ ] PostgreSQL installed and running
- [ ] Database `sams_db` created
- [ ] Java 17+ installed
- [ ] Maven installed
- [ ] IntelliJ IDEA or VS Code (optional, can use command line)
- [ ] Postman installed (recommended) OR curl available

---

## Step 1: Verify Database Setup

### Option A: Using pgAdmin (Windows GUI)

1. Open **pgAdmin** (should be installed with PostgreSQL)
2. Connect to your PostgreSQL server (default password: postgres)
3. Check if database `sams_db` exists in the left panel
4. If NOT exists:
   - Right-click on "Databases"
   - Click "Create" → "Database"
   - Name: `sams_db`
   - Click "Save"

### Option B: Using Command Line (Windows CMD or PowerShell)

Open **Command Prompt** or **PowerShell** and run:

```cmd
# Check PostgreSQL is running
psql -U postgres -c "SELECT version();"

# Create database if needed
psql -U postgres -c "CREATE DATABASE sams_db;"

# Verify database exists
psql -U postgres -c "\l" | findstr sams_db
```

**Expected Output:** You should see `sams_db` in the list of databases.

---

## Step 2: Configure Application Properties

1. Navigate to: `src/main/resources/application.properties`

2. Verify the database credentials match your PostgreSQL setup:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD_HERE
```

3. **IMPORTANT:** Replace `YOUR_POSTGRES_PASSWORD_HERE` with your actual PostgreSQL password!

---

## Step 3: Start the Application

### Option A: Using IntelliJ IDEA (Recommended)

1. Open IntelliJ IDEA
2. Open the project folder: `week3-4-5-implementation`
3. Wait for Maven to download dependencies (watch bottom right corner)
4. Find `SamsApplication.java` in: `src/main/java/com/sams/SamsApplication.java`
5. Right-click on the file → **Run 'SamsApplication'**
6. Watch the console output

### Option B: Using Command Line

Open **Command Prompt** or **PowerShell** in the project directory:

```bash
# Navigate to project
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"

# Run with Maven
mvn spring-boot:run
```

### What to Look For (Success Indicators):

```
✅ Hibernate: create table users ...
✅ Hibernate: create table courses ...
✅ Hibernate: create table enrollments ...
✅ Started SamsApplication in X.XXX seconds (JVM running for X.XXX)
✅ Tomcat started on port(s): 8080 (http)
```

### Common Errors and Fixes:

**Error:** `Connection refused` or `database "sams_db" does not exist`
- **Fix:** Go back to Step 1 and create the database

**Error:** `password authentication failed for user "postgres"`
- **Fix:** Update the password in `application.properties`

**Error:** `Port 8080 already in use`
- **Fix:** Change port in `application.properties`: `server.port=8081`

---

## Step 4: Test with Postman (Recommended Method)

### Import Postman Collections

1. Open **Postman**
2. Click **Import** (top left)
3. Import both files:
   - `postman/SAMS_User_Management.postman_collection.json`
   - `postman/SAMS_Course_Enrollment_Management.postman_collection.json`

You should now see two collections in the left sidebar.

---

## Step 5: Complete Workflow Test

### Test 1: Create a Student User

**In Postman:**
1. Expand collection: **SAMS User Management**
2. Click: **Create User (Student)**
3. Check the request body (should look like this):

```json
{
    "username": "john_student",
    "email": "john@student.com",
    "password": "password123",
    "role": "STUDENT"
}
```

4. Click **Send**
5. **Expected Response:** `201 Created`

```json
{
    "id": 1,
    "username": "john_student",
    "email": "john@student.com",
    "role": "STUDENT",
    "createdAt": "2024-11-17T15:30:00"
}
```

✅ **What just happened:**
- Created a student user in the database
- Password was automatically hashed with BCrypt
- Notice: Password is NOT in the response (security!)

**Save the student ID** (example: `1`) - we'll need it later!

---

### Test 2: Create a Faculty User (Instructor)

**In Postman:**
1. Click: **Create User (Faculty)**
2. Check the request body:

```json
{
    "username": "dr_smith",
    "email": "smith@faculty.com",
    "password": "password123",
    "role": "FACULTY"
}
```

3. Click **Send**
4. **Expected Response:** `201 Created`

```json
{
    "id": 2,
    "username": "dr_smith",
    "email": "smith@faculty.com",
    "role": "FACULTY",
    "createdAt": "2024-11-17T15:31:00"
}
```

✅ **What just happened:**
- Created a faculty user who can be an instructor
- Password hashed automatically

**Save the faculty ID** (example: `2`) - we'll need it for the course!

---

### Test 3: Create a Course with Instructor

**In Postman:**
1. Expand collection: **SAMS Course & Enrollment Management**
2. Click: **Create Course (with Instructor)**
3. **IMPORTANT:** Update the request body with your faculty ID:

```json
{
    "courseCode": "CS101",
    "courseName": "Introduction to Programming",
    "description": "Learn the basics of programming with Java",
    "credits": 3,
    "capacity": 30,
    "instructorId": 2    ← USE YOUR FACULTY ID HERE
}
```

4. Click **Send**
5. **Expected Response:** `201 Created`

```json
{
    "id": 1,
    "courseCode": "CS101",
    "courseName": "Introduction to Programming",
    "description": "Learn the basics of programming with Java",
    "credits": 3,
    "capacity": 30,
    "enrolledCount": 0,
    "isFull": false,
    "instructor": {
        "id": 2,
        "username": "dr_smith",
        "email": "smith@faculty.com"
    },
    "createdAt": "2024-11-17T15:32:00"
}
```

✅ **What just happened:**
- Created a course with code CS101
- Assigned Dr. Smith as instructor
- Set capacity to 30 students
- Currently 0 students enrolled

**Save the course ID** (example: `1`) - we'll need it for enrollment!

---

### Test 4: Enroll Student in Course

**In Postman:**
1. Click: **Create Enrollment**
2. **IMPORTANT:** Update the request body with your IDs:

```json
{
    "studentId": 1,    ← USE YOUR STUDENT ID HERE
    "courseId": 1      ← USE YOUR COURSE ID HERE
}
```

3. Click **Send**
4. **Expected Response:** `201 Created`

```json
{
    "id": 1,
    "student": {
        "id": 1,
        "username": "john_student",
        "email": "john@student.com"
    },
    "course": {
        "id": 1,
        "courseCode": "CS101",
        "courseName": "Introduction to Programming"
    },
    "status": "ACTIVE",
    "enrolledAt": "2024-11-17T15:33:00"
}
```

✅ **What just happened:**
- John (student) is now enrolled in CS101
- Enrollment status is ACTIVE
- System validated:
  - ✅ User is a STUDENT (not FACULTY)
  - ✅ Student not already enrolled
  - ✅ Course has capacity (not full)

---

### Test 5: Verify the Enrollment

**In Postman:**
1. Click: **Get Enrollments by Student**
2. Update the URL parameter with your student ID:
   - URL: `http://localhost:8080/api/enrollments/student/1` ← YOUR STUDENT ID

3. Click **Send**
4. **Expected Response:** `200 OK`

```json
[
    {
        "id": 1,
        "student": {
            "id": 1,
            "username": "john_student",
            "email": "john@student.com"
        },
        "course": {
            "id": 1,
            "courseCode": "CS101",
            "courseName": "Introduction to Programming"
        },
        "status": "ACTIVE",
        "enrolledAt": "2024-11-17T15:33:00"
    }
]
```

✅ **Perfect!** John's enrollment is confirmed!

---

### Test 6: Verify Course Enrollment Count

**In Postman:**
1. Click: **Get Course by ID**
2. Update URL: `http://localhost:8080/api/courses/1` ← YOUR COURSE ID
3. Click **Send**

```json
{
    "id": 1,
    "courseCode": "CS101",
    "courseName": "Introduction to Programming",
    "description": "Learn the basics of programming with Java",
    "credits": 3,
    "capacity": 30,
    "enrolledCount": 1,    ← NOW IT'S 1!
    "isFull": false,       ← STILL HAS SPACE
    "instructor": {
        "id": 2,
        "username": "dr_smith",
        "email": "smith@faculty.com"
    },
    "createdAt": "2024-11-17T15:32:00"
}
```

✅ **Success!** The course now shows 1 enrolled student!

---

## Step 6: Test Business Logic (Error Cases)

### Test 7: Try to Enroll Same Student Again (Should Fail)

**In Postman:**
1. Click: **Create Enrollment** again
2. Use the SAME studentId and courseId
3. Click **Send**
4. **Expected Response:** `400 Bad Request`

```json
{
    "statusCode": 400,
    "message": "Student john_student is already enrolled in CS101",
    "timestamp": "2024-11-17T15:35:00"
}
```

✅ **Perfect!** System prevented duplicate enrollment!

---

### Test 8: Try to Make Faculty Enroll (Should Fail)

**In Postman:**
1. Click: **Create Enrollment**
2. Try to enroll the FACULTY user:

```json
{
    "studentId": 2,    ← FACULTY ID (Dr. Smith)
    "courseId": 1
}
```

3. Click **Send**
4. **Expected Response:** `400 Bad Request`

```json
{
    "statusCode": 400,
    "message": "Only students can enroll in courses",
    "timestamp": "2024-11-17T15:36:00"
}
```

✅ **Perfect!** System validated that only STUDENT role can enroll!

---

### Test 9: Try to Create Duplicate Course Code (Should Fail)

**In Postman:**
1. Click: **Create Course (Basic)**
2. Try to create another course with code CS101:

```json
{
    "courseCode": "CS101",    ← DUPLICATE CODE
    "courseName": "Another Course",
    "description": "This should fail",
    "credits": 3,
    "capacity": 20
}
```

3. Click **Send**
4. **Expected Response:** `400 Bad Request`

```json
{
    "statusCode": 400,
    "message": "Course code CS101 already exists",
    "timestamp": "2024-11-17T15:37:00"
}
```

✅ **Perfect!** System prevented duplicate course codes!

---

## Step 7: Verify in Database (Optional but Recommended)

### Using pgAdmin:

1. Open **pgAdmin**
2. Navigate to: `sams_db` → `Schemas` → `public` → `Tables`
3. Right-click on `users` → **View/Edit Data** → **All Rows**

**You should see:**
- 2 users (john_student and dr_smith)
- Password column shows encrypted hashes (not plain text!)

4. Right-click on `courses` → **View/Edit Data** → **All Rows**

**You should see:**
- 1 course (CS101)
- instructor_id = 2 (Dr. Smith)

5. Right-click on `enrollments` → **View/Edit Data** → **All Rows**

**You should see:**
- 1 enrollment (student_id=1, course_id=1, status=ACTIVE)

---

## Alternative: Testing with cURL (Command Line)

If you don't have Postman, you can use **curl** in Command Prompt:

### Create Student:
```bash
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"john_student\",\"email\":\"john@student.com\",\"password\":\"password123\",\"role\":\"STUDENT\"}"
```

### Create Faculty:
```bash
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"dr_smith\",\"email\":\"smith@faculty.com\",\"password\":\"password123\",\"role\":\"FACULTY\"}"
```

### Create Course:
```bash
curl -X POST http://localhost:8080/api/courses ^
  -H "Content-Type: application/json" ^
  -d "{\"courseCode\":\"CS101\",\"courseName\":\"Intro to Programming\",\"description\":\"Learn Java\",\"credits\":3,\"capacity\":30,\"instructorId\":2}"
```

### Create Enrollment:
```bash
curl -X POST http://localhost:8080/api/enrollments ^
  -H "Content-Type: application/json" ^
  -d "{\"studentId\":1,\"courseId\":1}"
```

---

## Summary: What You Just Tested

✅ **User Management (Week 3-4):**
- Create users with different roles (STUDENT, FACULTY)
- Password hashing with BCrypt
- Duplicate email prevention
- DTO pattern (password not exposed in responses)

✅ **Course Management (Week 5):**
- Create courses
- Assign instructors (only FACULTY role)
- Duplicate course code prevention
- Track enrollment count and capacity

✅ **Enrollment Management (Week 5):**
- Enroll students in courses
- Role validation (only STUDENT can enroll)
- Duplicate enrollment prevention
- Capacity management
- Status tracking (ACTIVE)

✅ **Architecture Patterns:**
- Repository-Service-Controller layers
- Business logic validation
- Global exception handling
- Entity relationships (One-to-Many, Many-to-Many)

---

## Troubleshooting

### Application won't start?
1. Check PostgreSQL is running (Task Manager → Services → postgresql)
2. Verify database `sams_db` exists
3. Check password in `application.properties`
4. Look for error messages in console

### Postman request fails?
1. Make sure application is running (check console)
2. Verify URL: `http://localhost:8080` (check port)
3. Check request body has correct JSON format
4. Verify IDs match your created resources

### "Port 8080 already in use"?
1. Stop other applications using port 8080
2. OR change port in `application.properties`: `server.port=8081`
3. Restart application

### Database connection error?
1. Check PostgreSQL service is running
2. Verify credentials in `application.properties`
3. Try connecting with pgAdmin first

---

## Next Steps

After successful testing, you can:

1. **Test More Scenarios:**
   - Create multiple courses
   - Enroll multiple students
   - Test course capacity (create 30 enrollments, then try 31st)
   - Update enrollment status to "DROPPED" or "COMPLETED"
   - Search courses by name, code, credits

2. **Run Unit Tests:**
   ```bash
   mvn test
   ```
   Should see 29 tests pass!

3. **Explore All API Endpoints:**
   - Check the Postman collections (41 total requests)
   - Try all CRUD operations
   - Test error scenarios

4. **Review Documentation:**
   - `docs/week5/QUICK_START_GUIDE.md`
   - `docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md`
   - `DOCUMENTATION_INDEX.md`

---

## Success Criteria ✅

You've successfully tested the system if:

- ✅ Created users with STUDENT and FACULTY roles
- ✅ Created a course with an instructor
- ✅ Enrolled a student in the course
- ✅ Verified enrollment count updated
- ✅ Tested duplicate enrollment prevention
- ✅ Tested role validation (FACULTY can't enroll)
- ✅ Saw proper error messages for invalid operations

**Congratulations! Your SAMS system is working perfectly!** 🎉

---

**Need Help?**
- Check console output for error messages
- Review `docs/week3/TROUBLESHOOTING.md`
- Review `docs/week4/TROUBLESHOOTING_WEEK4.md`
- Check database with pgAdmin to verify data
