# 🚀 QUICK START GUIDE
## Get Your SAMS Project Running in 5 Minutes

---

## ⚡ Prerequisites

Make sure you have installed:
- ✅ Java 17 or higher
- ✅ PostgreSQL
- ✅ IntelliJ IDEA (or any Java IDE)
- ✅ Postman (for API testing)

---

## 📋 Step-by-Step Setup

### Step 1: Set Up Database

1. Open PostgreSQL and create a database:
```sql
CREATE DATABASE sams_db;
```

2. Open `src/main/resources/application.properties` and update if needed:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

### Step 2: Open Project in IntelliJ

1. Open IntelliJ IDEA
2. Click "Open" and select the `week3-development` folder
3. Wait for Maven to download dependencies (may take a few minutes)

---

### Step 3: Run the Application

1. Find `SamsApplication.java` in `src/main/java/com/sams/`
2. Right-click and select "Run 'SamsApplication'"
3. Wait for the application to start (you'll see "Started SamsApplication" in console)
4. Application runs on: `http://localhost:8080`

---

### Step 4: Test with Postman

1. Open Postman
2. Import collections from `postman/` folder:
   - `SAMS_User_Management.postman_collection.json`
   - `SAMS_Course_Enrollment_Management.postman_collection.json`

3. Try these requests in order:

**Create a Student**:
```
POST http://localhost:8080/api/users
Body:
{
  "username": "john_doe",
  "email": "john@student.com",
  "password": "student123",
  "role": "STUDENT"
}
```

**Create a Faculty**:
```
POST http://localhost:8080/api/users
Body:
{
  "username": "prof_smith",
  "email": "smith@faculty.com",
  "password": "faculty123",
  "role": "FACULTY"
}
```

**Create a Course**:
```
POST http://localhost:8080/api/courses
Body:
{
  "courseCode": "CS101",
  "courseName": "Introduction to Computer Science",
  "description": "Basic programming",
  "credits": 3,
  "capacity": 30,
  "instructorId": 2
}
```

**Enroll Student**:
```
POST http://localhost:8080/api/enrollments
Body:
{
  "studentId": 1,
  "courseId": 1
}
```

---

### Step 5: Run Unit Tests

1. In IntelliJ, right-click on `src/test/java`
2. Click "Run 'All Tests'"
3. All 18 tests should pass ✅

---

## 🎯 What to Test

### User Management
1. ✅ Create users (student, faculty, admin)
2. ✅ Get all users
3. ✅ Search by email
4. ✅ Try duplicate email (should fail)

### Course Management
1. ✅ Create courses
2. ✅ Assign instructor
3. ✅ Search courses
4. ✅ Try duplicate course code (should fail)

### Enrollment
1. ✅ Enroll student in course
2. ✅ View enrollments
3. ✅ Try to enroll twice (should fail)
4. ✅ Drop enrollment

---

## 📚 Documentation

- **Full Learning Guide**: `WEEK_4_AND_5_LEARNING_GUIDE.md`
- **Implementation Summary**: `WEEK_4_AND_5_IMPLEMENTATION_SUMMARY.md`
- **This Guide**: `QUICK_START_GUIDE.md`

---

## ❓ Troubleshooting

### Problem: "Application failed to start"
**Solution**: Check if PostgreSQL is running and database exists

### Problem: "Port 8080 already in use"
**Solution**: Stop other applications using port 8080, or change port in `application.properties`

### Problem: "Tests failing"
**Solution**: Make sure application is NOT running while running tests

### Problem: "Cannot connect to database"
**Solution**: Verify PostgreSQL credentials in `application.properties`

---

## 🎉 You're All Set!

Your SAMS project is now running with:
- ✅ 29 REST API endpoints
- ✅ 3 database tables
- ✅ Complete user, course, and enrollment management
- ✅ Password hashing for security
- ✅ 18 passing unit tests

**Next**: Read `WEEK_4_AND_5_LEARNING_GUIDE.md` to understand how everything works!

---

*Need help? Check the learning guide or implementation summary for detailed explanations!*
