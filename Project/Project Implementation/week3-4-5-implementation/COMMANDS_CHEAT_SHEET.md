# Commands Cheat Sheet - SAMS Project

**Quick reference for running and testing the project**

---

## 🚀 Method 1: IntelliJ IDEA (EASIEST)

### Start Application:
1. Open IntelliJ IDEA
2. Open folder: `week3-4-5-implementation`
3. Find: `src/main/java/com/sams/SamsApplication.java`
4. Right-click → **"Run 'SamsApplication'"**
5. Wait for: `Started SamsApplication in X.XXX seconds`

### Stop Application:
- Click red **Stop** button

---

## 🚀 Method 2: Command Line (Maven)

### Navigate to Project:
```cmd
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"
```

### First Time Setup:
```cmd
mvn clean install
```

### Run Application:
```cmd
mvn spring-boot:run
```

### Stop Application:
```
Ctrl + C
```

---

## 📦 Database Setup (One Time)

### Using pgAdmin (GUI):
1. Open pgAdmin
2. Right-click "Databases" → "Create" → "Database"
3. Name: `sams_db`
4. Save

### Using Command Line:
```cmd
psql -U postgres
CREATE DATABASE sams_db;
\q
```

---

## 🧪 Test Commands (Application Must Be Running)

### Create Student:
```cmd
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"john\",\"email\":\"john@example.com\",\"password\":\"password123\",\"role\":\"STUDENT\"}"
```

### Create Faculty:
```cmd
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"dr_smith\",\"email\":\"smith@example.com\",\"password\":\"password123\",\"role\":\"FACULTY\"}"
```

### Get All Users:
```cmd
curl http://localhost:8080/api/users
```

### Create Course:
```cmd
curl -X POST http://localhost:8080/api/courses -H "Content-Type: application/json" -d "{\"courseCode\":\"CS101\",\"courseName\":\"Programming\",\"description\":\"Learn Java\",\"credits\":3,\"capacity\":30,\"instructorId\":2}"
```

### Get All Courses:
```cmd
curl http://localhost:8080/api/courses
```

### Enroll Student (use actual IDs):
```cmd
curl -X POST http://localhost:8080/api/enrollments -H "Content-Type: application/json" -d "{\"studentId\":1,\"courseId\":1}"
```

### Get Student Enrollments:
```cmd
curl http://localhost:8080/api/enrollments/student/1
```

---

## 🌐 Browser Testing (Simple)

Open browser and visit:
- **All Users:** http://localhost:8080/api/users
- **All Courses:** http://localhost:8080/api/courses
- **All Enrollments:** http://localhost:8080/api/enrollments

---

## ✅ Run Unit Tests

### In IntelliJ:
- Right-click `src/test/java` → "Run 'All Tests'"

### Command Line:
```cmd
mvn test
```

**Expected:** `Tests run: 29, Failures: 0`

---

## 🔧 Troubleshooting

### Check if Application is Running:
```cmd
curl http://localhost:8080/api/users
```
If you get JSON response → Running ✅
If "connection refused" → Not running ❌

### Check Java:
```cmd
java -version
```
Expected: Java 17 or higher

### Check Maven:
```cmd
mvn -version
```

### Check PostgreSQL Service:
**Windows:**
- Task Manager → Services → Look for "postgresql"

### Check Database Exists:
```cmd
psql -U postgres -l | findstr sams_db
```

### Port 8080 Already in Use:
Edit `src/main/resources/application.properties`:
```properties
server.port=8081
```

---

## 📊 Success Indicators

### Application Started Successfully:
```
✅ Hibernate: create table users
✅ Hibernate: create table courses
✅ Hibernate: create table enrollments
✅ Started SamsApplication in X.XXX seconds
✅ Tomcat started on port(s): 8080
```

### Test Successful:
```
✅ POST requests return 201 Created
✅ GET requests return 200 OK
✅ Valid JSON in response
✅ No errors in console
```

---

## 🎯 Complete Workflow (5 Minutes)

```cmd
# 1. Navigate to project
cd "C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation"

# 2. Run application (or use IntelliJ)
mvn spring-boot:run

# Wait for: "Started SamsApplication"

# 3. In NEW terminal, create student
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"alice\",\"email\":\"alice@example.com\",\"password\":\"password123\",\"role\":\"STUDENT\"}"

# 4. Create faculty
curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{\"username\":\"dr_smith\",\"email\":\"smith@example.com\",\"password\":\"password123\",\"role\":\"FACULTY\"}"

# 5. Create course (use faculty ID from step 4)
curl -X POST http://localhost:8080/api/courses -H "Content-Type: application/json" -d "{\"courseCode\":\"CS101\",\"courseName\":\"Intro to Programming\",\"description\":\"Learn Java\",\"credits\":3,\"capacity\":30,\"instructorId\":2}"

# 6. Enroll student (use student ID and course ID)
curl -X POST http://localhost:8080/api/enrollments -H "Content-Type: application/json" -d "{\"studentId\":1,\"courseId\":1}"

# 7. Verify enrollment
curl http://localhost:8080/api/enrollments/student/1

# SUCCESS! ✅
```

---

## 📱 Postman Alternative (Recommended)

Instead of cURL, use Postman:

1. Import: `postman/SAMS_User_Management.postman_collection.json`
2. Import: `postman/SAMS_Course_Enrollment_Management.postman_collection.json`
3. Click requests → Send
4. All 41 requests ready to use!

---

## 📚 More Help

- **Full Guide:** `RUN_PROJECT_COMMANDS.md`
- **Testing Guide:** `TESTING_WALKTHROUGH.md`
- **Quick Test:** `QUICK_TEST_REFERENCE.md`
- **Documentation:** `DOCUMENTATION_INDEX.md`

---

**Happy Testing! 🎉**
