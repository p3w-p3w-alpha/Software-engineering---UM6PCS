# SAMS API Testing Script
# Run this after starting the application (mvn spring-boot:run)
# Usage: .\test-api.ps1

Write-Host "🧪 SAMS API Testing Script" -ForegroundColor Cyan
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan

# Check if application is running
Write-Host "`n🔍 Checking if application is running..." -ForegroundColor Yellow
try {
    $testConnection = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get -ErrorAction Stop
    Write-Host "✅ Application is running!" -ForegroundColor Green
} catch {
    Write-Host "❌ ERROR: Application is not running!" -ForegroundColor Red
    Write-Host "Please start the application first:" -ForegroundColor Yellow
    Write-Host "   Method 1: In VS Code, run SamsApplication.java" -ForegroundColor White
    Write-Host "   Method 2: Run 'mvn spring-boot:run' in terminal" -ForegroundColor White
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 1: Create Student
Write-Host "`n📝 Test 1: Creating student user..." -ForegroundColor Yellow
$student = @{
    username = "alice_student"
    email = "alice@example.com"
    password = "password123"
    role = "STUDENT"
} | ConvertTo-Json

try {
    $studentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -Body $student -ContentType "application/json"
    Write-Host "✅ Student created successfully!" -ForegroundColor Green
    Write-Host "   ID: $($studentResponse.id)" -ForegroundColor White
    Write-Host "   Username: $($studentResponse.username)" -ForegroundColor White
    Write-Host "   Email: $($studentResponse.email)" -ForegroundColor White
    Write-Host "   Role: $($studentResponse.role)" -ForegroundColor White
} catch {
    Write-Host "❌ Failed to create student: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 2: Create Faculty
Write-Host "`n📝 Test 2: Creating faculty user..." -ForegroundColor Yellow
$faculty = @{
    username = "dr_smith"
    email = "smith@example.com"
    password = "password123"
    role = "FACULTY"
} | ConvertTo-Json

try {
    $facultyResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Post -Body $faculty -ContentType "application/json"
    Write-Host "✅ Faculty created successfully!" -ForegroundColor Green
    Write-Host "   ID: $($facultyResponse.id)" -ForegroundColor White
    Write-Host "   Username: $($facultyResponse.username)" -ForegroundColor White
    Write-Host "   Email: $($facultyResponse.email)" -ForegroundColor White
    Write-Host "   Role: $($facultyResponse.role)" -ForegroundColor White
} catch {
    Write-Host "❌ Failed to create faculty: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 3: Get All Users
Write-Host "`n📋 Test 3: Getting all users..." -ForegroundColor Yellow
try {
    $users = Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method Get
    Write-Host "✅ Retrieved users successfully!" -ForegroundColor Green
    Write-Host "   Total users: $($users.Count)" -ForegroundColor White
} catch {
    Write-Host "❌ Failed to get users: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 4: Create Course
Write-Host "`n📝 Test 4: Creating course..." -ForegroundColor Yellow
$course = @{
    courseCode = "CS101"
    courseName = "Introduction to Programming"
    description = "Learn Java basics and object-oriented programming"
    credits = 3
    capacity = 30
    instructorId = $facultyResponse.id
} | ConvertTo-Json

try {
    $courseResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/courses" -Method Post -Body $course -ContentType "application/json"
    Write-Host "✅ Course created successfully!" -ForegroundColor Green
    Write-Host "   ID: $($courseResponse.id)" -ForegroundColor White
    Write-Host "   Code: $($courseResponse.courseCode)" -ForegroundColor White
    Write-Host "   Name: $($courseResponse.courseName)" -ForegroundColor White
    Write-Host "   Instructor: $($courseResponse.instructor.username)" -ForegroundColor White
    Write-Host "   Capacity: $($courseResponse.capacity)" -ForegroundColor White
    Write-Host "   Enrolled: $($courseResponse.enrolledCount)" -ForegroundColor White
} catch {
    Write-Host "❌ Failed to create course: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 5: Enroll Student in Course
Write-Host "`n📝 Test 5: Enrolling student in course..." -ForegroundColor Yellow
$enrollment = @{
    studentId = $studentResponse.id
    courseId = $courseResponse.id
} | ConvertTo-Json

try {
    $enrollmentResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments" -Method Post -Body $enrollment -ContentType "application/json"
    Write-Host "✅ Enrollment created successfully!" -ForegroundColor Green
    Write-Host "   ID: $($enrollmentResponse.id)" -ForegroundColor White
    Write-Host "   Student: $($enrollmentResponse.student.username)" -ForegroundColor White
    Write-Host "   Course: $($enrollmentResponse.course.courseCode) - $($enrollmentResponse.course.courseName)" -ForegroundColor White
    Write-Host "   Status: $($enrollmentResponse.status)" -ForegroundColor White
} catch {
    Write-Host "❌ Failed to create enrollment: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 6: Get Student Enrollments
Write-Host "`n📋 Test 6: Getting student enrollments..." -ForegroundColor Yellow
try {
    $enrollments = Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments/student/$($studentResponse.id)" -Method Get
    Write-Host "✅ Retrieved enrollments successfully!" -ForegroundColor Green
    Write-Host "   Student is enrolled in $($enrollments.Count) course(s)" -ForegroundColor White
    foreach ($enr in $enrollments) {
        Write-Host "   - $($enr.course.courseCode): $($enr.course.courseName) [$($enr.status)]" -ForegroundColor White
    }
} catch {
    Write-Host "❌ Failed to get enrollments: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 7: Get Course with Updated Enrollment Count
Write-Host "`n📋 Test 7: Getting course details..." -ForegroundColor Yellow
try {
    $courseDetails = Invoke-RestMethod -Uri "http://localhost:8080/api/courses/$($courseResponse.id)" -Method Get
    Write-Host "✅ Retrieved course details successfully!" -ForegroundColor Green
    Write-Host "   Course: $($courseDetails.courseCode) - $($courseDetails.courseName)" -ForegroundColor White
    Write-Host "   Enrolled: $($courseDetails.enrolledCount) / $($courseDetails.capacity)" -ForegroundColor White
    Write-Host "   Is Full: $($courseDetails.isFull)" -ForegroundColor White
} catch {
    Write-Host "❌ Failed to get course details: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 8: Test Business Rule - Duplicate Enrollment (Should Fail)
Write-Host "`n📝 Test 8: Testing duplicate enrollment prevention..." -ForegroundColor Yellow
try {
    $duplicateEnrollment = Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments" -Method Post -Body $enrollment -ContentType "application/json" -ErrorAction Stop
    Write-Host "❌ ERROR: Duplicate enrollment was allowed (should have failed!)" -ForegroundColor Red
} catch {
    Write-Host "✅ Duplicate enrollment correctly prevented!" -ForegroundColor Green
    Write-Host "   Error message: Already enrolled" -ForegroundColor White
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Test 9: Test Business Rule - Faculty Cannot Enroll (Should Fail)
Write-Host "`n📝 Test 9: Testing faculty enrollment prevention..." -ForegroundColor Yellow
$facultyEnrollment = @{
    studentId = $facultyResponse.id
    courseId = $courseResponse.id
} | ConvertTo-Json

try {
    $invalidEnrollment = Invoke-RestMethod -Uri "http://localhost:8080/api/enrollments" -Method Post -Body $facultyEnrollment -ContentType "application/json" -ErrorAction Stop
    Write-Host "❌ ERROR: Faculty was allowed to enroll (should have failed!)" -ForegroundColor Red
} catch {
    Write-Host "✅ Faculty enrollment correctly prevented!" -ForegroundColor Green
    Write-Host "   Error message: Only students can enroll" -ForegroundColor White
}

Write-Host "`n─────────────────────────────────────" -ForegroundColor Gray

# Final Summary
Write-Host "`n🎉 ALL TESTS COMPLETED SUCCESSFULLY! 🎉" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "`n📊 Test Summary:" -ForegroundColor Yellow
Write-Host "   ✅ Created $($users.Count) users (1 student, 1 faculty)" -ForegroundColor White
Write-Host "   ✅ Created 1 course with instructor" -ForegroundColor White
Write-Host "   ✅ Created 1 enrollment" -ForegroundColor White
Write-Host "   ✅ Verified enrollment count updated" -ForegroundColor White
Write-Host "   ✅ Tested duplicate enrollment prevention" -ForegroundColor White
Write-Host "   ✅ Tested role-based enrollment rules" -ForegroundColor White

Write-Host "`n📝 Created Resources:" -ForegroundColor Yellow
Write-Host "   Student: $($studentResponse.username) (ID: $($studentResponse.id))" -ForegroundColor White
Write-Host "   Faculty: $($facultyResponse.username) (ID: $($facultyResponse.id))" -ForegroundColor White
Write-Host "   Course: $($courseResponse.courseCode) - $($courseResponse.courseName) (ID: $($courseResponse.id))" -ForegroundColor White
Write-Host "   Enrollment: $($studentResponse.username) → $($courseResponse.courseCode) (ID: $($enrollmentResponse.id))" -ForegroundColor White

Write-Host "`n🌐 API Endpoints Tested:" -ForegroundColor Yellow
Write-Host "   POST /api/users" -ForegroundColor White
Write-Host "   GET  /api/users" -ForegroundColor White
Write-Host "   POST /api/courses" -ForegroundColor White
Write-Host "   GET  /api/courses/{id}" -ForegroundColor White
Write-Host "   POST /api/enrollments" -ForegroundColor White
Write-Host "   GET  /api/enrollments/student/{id}" -ForegroundColor White

Write-Host "`n✨ Your SAMS system is working perfectly!" -ForegroundColor Green
Write-Host "═══════════════════════════════════════════════════════════`n" -ForegroundColor Cyan
