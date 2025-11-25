#!/bin/bash

# SAMS Mock Data Population Script
# This script populates the database with comprehensive mock data

BASE_URL="http://localhost:8080/api"

# Login and get token
echo "=== Logging in as admin ==="
LOGIN_RESPONSE=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Failed to get authentication token"
  exit 1
fi

echo "✓ Authentication successful"
echo ""

# Create Faculty Users
echo "=== Creating Faculty Users ==="

# Faculty 1 - Dr. Sarah Johnson
curl -s -X POST "${BASE_URL}/admin/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "sjohnson",
    "email": "sarah.johnson@university.edu",
    "password": "faculty123",
    "firstName": "Sarah",
    "lastName": "Johnson",
    "role": "FACULTY",
    "active": true
  }' > /dev/null

echo "✓ Created faculty: Dr. Sarah Johnson"

# Faculty 2 - Prof. Michael Chen
curl -s -X POST "${BASE_URL}/admin/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "mchen",
    "email": "michael.chen@university.edu",
    "password": "faculty123",
    "firstName": "Michael",
    "lastName": "Chen",
    "role": "FACULTY",
    "active": true
  }' > /dev/null

echo "✓ Created faculty: Prof. Michael Chen"

# Faculty 3 - Dr. Emily Rodriguez
curl -s -X POST "${BASE_URL}/admin/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "erodriguez",
    "email": "emily.rodriguez@university.edu",
    "password": "faculty123",
    "firstName": "Emily",
    "lastName": "Rodriguez",
    "role": "FACULTY",
    "active": true
  }' > /dev/null

echo "✓ Created faculty: Dr. Emily Rodriguez"

# Faculty 4 - Prof. David Thompson
curl -s -X POST "${BASE_URL}/admin/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dthompson",
    "email": "david.thompson@university.edu",
    "password": "faculty123",
    "firstName": "David",
    "lastName": "Thompson",
    "role": "FACULTY",
    "active": true
  }' > /dev/null

echo "✓ Created faculty: Prof. David Thompson"

# Faculty 5 - Dr. Jennifer Lee
curl -s -X POST "${BASE_URL}/admin/users" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "jlee",
    "email": "jennifer.lee@university.edu",
    "password": "faculty123",
    "firstName": "Jennifer",
    "lastName": "Lee",
    "role": "FACULTY",
    "active": true
  }' > /dev/null

echo "✓ Created faculty: Dr. Jennifer Lee"

echo ""

# Get faculty user IDs
echo "=== Getting Faculty User IDs ==="
USERS=$(curl -s -H "Authorization: Bearer $TOKEN" "${BASE_URL}/admin/users")

# Extract user IDs (assuming users are created with IDs 2-6 after admin)
FACULTY_IDS=(2 3 4 5 6)

echo "✓ Faculty users retrieved"
echo ""

# Create Teacher Profiles
echo "=== Creating Teacher Profiles ==="

# Teacher Profile 1 - Dr. Sarah Johnson
curl -s -X POST "${BASE_URL}/teachers/profiles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "qualification": "Ph.D. in Computer Science",
    "specialization": "Machine Learning, Artificial Intelligence",
    "department": "Computer Science",
    "designation": "Professor",
    "employeeId": "FAC-001",
    "yearsOfExperience": 15,
    "officeRoom": "CS-305",
    "officePhone": "+1-555-0101",
    "officeEmail": "sarah.johnson@university.edu",
    "bio": "Dr. Sarah Johnson is a renowned expert in machine learning with over 15 years of teaching and research experience. She has published numerous papers in top-tier conferences and journals.",
    "researchInterests": "Deep Learning, Neural Networks, Computer Vision",
    "maxCoursesPerSemester": 3,
    "maxStudentsPerCourse": 40,
    "availableForConsultation": true,
    "active": true
  }' > /dev/null

echo "✓ Created profile for Dr. Sarah Johnson"

# Teacher Profile 2 - Prof. Michael Chen
curl -s -X POST "${BASE_URL}/teachers/profiles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 3,
    "qualification": "Ph.D. in Mathematics",
    "specialization": "Applied Mathematics, Numerical Analysis",
    "department": "Mathematics",
    "designation": "Associate Professor",
    "employeeId": "FAC-002",
    "yearsOfExperience": 10,
    "officeRoom": "MATH-201",
    "officePhone": "+1-555-0102",
    "officeEmail": "michael.chen@university.edu",
    "bio": "Prof. Michael Chen specializes in applied mathematics and has extensive experience in numerical methods and computational mathematics.",
    "researchInterests": "Numerical Analysis, Optimization, Computational Methods",
    "maxCoursesPerSemester": 4,
    "maxStudentsPerCourse": 35,
    "availableForConsultation": true,
    "active": true
  }' > /dev/null

echo "✓ Created profile for Prof. Michael Chen"

# Teacher Profile 3 - Dr. Emily Rodriguez
curl -s -X POST "${BASE_URL}/teachers/profiles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 4,
    "qualification": "Ph.D. in Physics",
    "specialization": "Quantum Mechanics, Particle Physics",
    "department": "Physics",
    "designation": "Associate Professor",
    "employeeId": "FAC-003",
    "yearsOfExperience": 12,
    "officeRoom": "PHY-402",
    "officePhone": "+1-555-0103",
    "officeEmail": "emily.rodriguez@university.edu",
    "bio": "Dr. Emily Rodriguez is an accomplished physicist with research focus on quantum mechanics and particle physics. She has received multiple research grants.",
    "researchInterests": "Quantum Computing, High Energy Physics, Condensed Matter",
    "maxCoursesPerSemester": 3,
    "maxStudentsPerCourse": 30,
    "availableForConsultation": true,
    "active": true
  }' > /dev/null

echo "✓ Created profile for Dr. Emily Rodriguez"

# Teacher Profile 4 - Prof. David Thompson
curl -s -X POST "${BASE_URL}/teachers/profiles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 5,
    "qualification": "M.Sc. in Software Engineering",
    "specialization": "Software Development, System Design",
    "department": "Engineering",
    "designation": "Lecturer",
    "employeeId": "FAC-004",
    "yearsOfExperience": 7,
    "officeRoom": "ENG-105",
    "officePhone": "+1-555-0104",
    "officeEmail": "david.thompson@university.edu",
    "bio": "Prof. David Thompson brings industry experience to academia, focusing on practical software engineering and system architecture.",
    "researchInterests": "Software Architecture, Cloud Computing, DevOps",
    "maxCoursesPerSemester": 4,
    "maxStudentsPerCourse": 50,
    "availableForConsultation": true,
    "active": true
  }' > /dev/null

echo "✓ Created profile for Prof. David Thompson"

# Teacher Profile 5 - Dr. Jennifer Lee
curl -s -X POST "${BASE_URL}/teachers/profiles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 6,
    "qualification": "Ph.D. in Data Science",
    "specialization": "Big Data Analytics, Statistical Learning",
    "department": "Computer Science",
    "designation": "Assistant Professor",
    "employeeId": "FAC-005",
    "yearsOfExperience": 5,
    "officeRoom": "CS-210",
    "officePhone": "+1-555-0105",
    "officeEmail": "jennifer.lee@university.edu",
    "bio": "Dr. Jennifer Lee is a rising star in data science, with expertise in big data analytics and machine learning applications.",
    "researchInterests": "Big Data, Machine Learning, Data Visualization",
    "maxCoursesPerSemester": 3,
    "maxStudentsPerCourse": 45,
    "availableForConsultation": true,
    "active": true
  }' > /dev/null

echo "✓ Created profile for Dr. Jennifer Lee"

echo ""

# Create Office Hours
echo "=== Creating Office Hours ==="

# Office Hours for Dr. Sarah Johnson (Faculty ID 2)
curl -s -X POST "${BASE_URL}/teachers/office-hours" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 2,
    "dayOfWeek": "MONDAY",
    "startTime": "10:00:00",
    "endTime": "12:00:00",
    "location": "CS-305",
    "consultationType": "BOTH",
    "meetingLink": "https://zoom.us/j/12345",
    "maxStudentsPerSlot": 2,
    "slotDurationMinutes": 30,
    "bookingRequired": true,
    "advanceBookingDays": 7,
    "recurring": true,
    "notes": "Preference for email appointment booking",
    "active": true
  }' > /dev/null

curl -s -X POST "${BASE_URL}/teachers/office-hours" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 2,
    "dayOfWeek": "WEDNESDAY",
    "startTime": "14:00:00",
    "endTime": "16:00:00",
    "location": "CS-305",
    "consultationType": "IN_PERSON",
    "maxStudentsPerSlot": 1,
    "slotDurationMinutes": 30,
    "bookingRequired": true,
    "advanceBookingDays": 5,
    "recurring": true,
    "active": true
  }' > /dev/null

echo "✓ Created office hours for Dr. Sarah Johnson"

# Office Hours for Prof. Michael Chen (Faculty ID 3)
curl -s -X POST "${BASE_URL}/teachers/office-hours" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 3,
    "dayOfWeek": "TUESDAY",
    "startTime": "13:00:00",
    "endTime": "15:00:00",
    "location": "MATH-201",
    "consultationType": "IN_PERSON",
    "maxStudentsPerSlot": 3,
    "slotDurationMinutes": 20,
    "bookingRequired": false,
    "recurring": true,
    "notes": "Walk-in welcome, group sessions available",
    "active": true
  }' > /dev/null

curl -s -X POST "${BASE_URL}/teachers/office-hours" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 3,
    "dayOfWeek": "THURSDAY",
    "startTime": "10:00:00",
    "endTime": "11:30:00",
    "consultationType": "ONLINE",
    "meetingLink": "https://meet.google.com/abc-defg-hij",
    "maxStudentsPerSlot": 1,
    "slotDurationMinutes": 30,
    "bookingRequired": true,
    "advanceBookingDays": 3,
    "recurring": true,
    "active": true
  }' > /dev/null

echo "✓ Created office hours for Prof. Michael Chen"

# Office Hours for Dr. Emily Rodriguez (Faculty ID 4)
curl -s -X POST "${BASE_URL}/teachers/office-hours" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 4,
    "dayOfWeek": "MONDAY",
    "startTime": "15:00:00",
    "endTime": "17:00:00",
    "location": "PHY-402",
    "consultationType": "IN_PERSON",
    "maxStudentsPerSlot": 2,
    "slotDurationMinutes": 40,
    "bookingRequired": true,
    "advanceBookingDays": 7,
    "recurring": true,
    "active": true
  }' > /dev/null

echo "✓ Created office hours for Dr. Emily Rodriguez"

# Office Hours for Prof. David Thompson (Faculty ID 5)
curl -s -X POST "${BASE_URL}/teachers/office-hours" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 5,
    "dayOfWeek": "FRIDAY",
    "startTime": "09:00:00",
    "endTime": "11:00:00",
    "location": "ENG-105",
    "consultationType": "BOTH",
    "meetingLink": "https://teams.microsoft.com/abc123",
    "maxStudentsPerSlot": 1,
    "slotDurationMinutes": 30,
    "bookingRequired": true,
    "advanceBookingDays": 5,
    "recurring": true,
    "active": true
  }' > /dev/null

echo "✓ Created office hours for Prof. David Thompson"

# Office Hours for Dr. Jennifer Lee (Faculty ID 6)
curl -s -X POST "${BASE_URL}/teachers/office-hours" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "teacherId": 6,
    "dayOfWeek": "WEDNESDAY",
    "startTime": "11:00:00",
    "endTime": "13:00:00",
    "location": "CS-210",
    "consultationType": "BOTH",
    "meetingLink": "https://zoom.us/j/67890",
    "maxStudentsPerSlot": 2,
    "slotDurationMinutes": 30,
    "bookingRequired": true,
    "advanceBookingDays": 7,
    "recurring": true,
    "active": true
  }' > /dev/null

echo "✓ Created office hours for Dr. Jennifer Lee"

echo ""
echo "==================================="
echo "Mock Data Population Complete!"
echo "==================================="
echo ""
echo "Summary:"
echo "- 5 Faculty users created"
echo "- 5 Teacher profiles created"
echo "- 7 Office hours schedules created"
echo ""
echo "You can now test the system with:"
echo "- Login: admin / admin123"
echo "- Faculty logins: sjohnson, mchen, erodriguez, dthompson, jlee / faculty123"
echo ""
echo "Access the application at: http://localhost:5173"
echo ""
