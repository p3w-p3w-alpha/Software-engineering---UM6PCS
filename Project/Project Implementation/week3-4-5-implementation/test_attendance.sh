#!/bin/bash

# Login and get token
echo "Logging in..."
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' > /tmp/auth_response.json

TOKEN=$(grep -o '"token":"[^"]*' /tmp/auth_response.json | cut -d'"' -f4)

echo "Token: ${TOKEN:0:50}..."
echo ""

# Test 1: Get attendance by date
echo "TEST 1: Get Attendance by Date (2025-11-25)"
curl -s -X GET "http://localhost:8080/api/attendance/date/2025-11-25" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 2: Get attendance statistics
echo "TEST 2: Get Attendance Statistics"
curl -s -X GET "http://localhost:8080/api/attendance/statistics/range?startDate=2025-11-20&endDate=2025-11-25" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 3: Mark another attendance
echo "TEST 3: Mark Attendance for Yesterday"
curl -s -X POST http://localhost:8080/api/attendance/mark \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": 1,
    "date": "2025-11-24",
    "status": "LATE",
    "checkInTime": "2025-11-24T09:15:00",
    "notes": "Arrived late"
  }'
echo -e "\n"

# Test 4: Get attendance by user
echo "TEST 4: Get Attendance by User (ID=1)"
curl -s -X GET "http://localhost:8080/api/attendance/user/1" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "All tests completed!"
