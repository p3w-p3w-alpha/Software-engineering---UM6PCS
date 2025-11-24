# Quick Testing Reference Card

**One-page guide for testing SAMS system**

---

## 🚀 Quick Start (3 Steps)

### 1️⃣ Start Database
```
Open pgAdmin → Verify sams_db exists
```

### 2️⃣ Start Application
```bash
# In project folder
mvn spring-boot:run

# OR in IntelliJ: Run SamsApplication.java
```

### 3️⃣ Test with Postman
```
Import: postman/SAMS_User_Management.postman_collection.json
Import: postman/SAMS_Course_Enrollment_Management.postman_collection.json
```

---

## 📋 Complete Workflow (Copy-Paste Ready)

### Step 1: Create Student
**POST** `http://localhost:8080/api/users`
```json
{
    "username": "john_student",
    "email": "john@student.com",
    "password": "password123",
    "role": "STUDENT"
}
```
**Save the returned `id` → Let's say it's `1`**

---

### Step 2: Create Faculty
**POST** `http://localhost:8080/api/users`
```json
{
    "username": "dr_smith",
    "email": "smith@faculty.com",
    "password": "password123",
    "role": "FACULTY"
}
```
**Save the returned `id` → Let's say it's `2`**

---

### Step 3: Create Course
**POST** `http://localhost:8080/api/courses`
```json
{
    "courseCode": "CS101",
    "courseName": "Introduction to Programming",
    "description": "Learn the basics of programming with Java",
    "credits": 3,
    "capacity": 30,
    "instructorId": 2
}
```
**Save the returned `id` → Let's say it's `1`**

---

### Step 4: Enroll Student
**POST** `http://localhost:8080/api/enrollments`
```json
{
    "studentId": 1,
    "courseId": 1
}
```

---

### Step 5: Verify Enrollment
**GET** `http://localhost:8080/api/enrollments/student/1`

**Expected:** List of enrollments for the student

---

### Step 6: Check Course
**GET** `http://localhost:8080/api/courses/1`

**Expected:** `enrolledCount: 1`, `isFull: false`

---

## ✅ Success Indicators

When application starts, you should see:
```
✅ Hibernate: create table users
✅ Hibernate: create table courses
✅ Hibernate: create table enrollments
✅ Started SamsApplication in X.XXX seconds
✅ Tomcat started on port(s): 8080
```

When testing:
```
✅ POST responses return 201 Created
✅ GET responses return 200 OK
✅ Response includes all expected fields
✅ Password NOT visible in responses
✅ enrolledCount increases after enrollment
```

---

## ❌ Error Tests (Should Fail)

### Duplicate Enrollment
**POST** `http://localhost:8080/api/enrollments`
```json
{
    "studentId": 1,
    "courseId": 1
}
```
**Expected:** `400 Bad Request` - "already enrolled"

---

### Faculty Can't Enroll
**POST** `http://localhost:8080/api/enrollments`
```json
{
    "studentId": 2,
    "courseId": 1
}
```
**Expected:** `400 Bad Request` - "Only students can enroll"

---

### Duplicate Course Code
**POST** `http://localhost:8080/api/courses`
```json
{
    "courseCode": "CS101",
    "courseName": "Another Course",
    "credits": 3,
    "capacity": 20
}
```
**Expected:** `400 Bad Request` - "Course code already exists"

---

## 🔍 Quick Database Check

### Using pgAdmin:
1. Open pgAdmin
2. Navigate to `sams_db` → `Tables`
3. Right-click `users` → View Data → Should see 2 users
4. Right-click `courses` → View Data → Should see 1 course
5. Right-click `enrollments` → View Data → Should see 1 enrollment

---

## 🐛 Common Issues

| Problem | Solution |
|---------|----------|
| Port 8080 in use | Change to 8081 in `application.properties` |
| Database connection failed | Check PostgreSQL is running, verify password |
| Application won't start | Check console for errors, verify Java 17+ |
| Postman 404 error | Verify application is running, check URL |

---

## 📊 What's Being Tested

✅ **3 Entities:** User, Course, Enrollment
✅ **29 API Endpoints:** 7 Users + 11 Courses + 11 Enrollments
✅ **Business Logic:** Role validation, capacity limits, duplicates
✅ **Security:** Password hashing, DTO pattern
✅ **Relationships:** One-to-Many (Instructor→Courses), Many-to-Many (Students↔Courses)

---

## 📚 More Details

Full walkthrough: `TESTING_WALKTHROUGH.md`
Documentation: `DOCUMENTATION_INDEX.md`
Quick setup: `docs/week5/QUICK_START_GUIDE.md`

---

**Happy Testing! 🎉**
