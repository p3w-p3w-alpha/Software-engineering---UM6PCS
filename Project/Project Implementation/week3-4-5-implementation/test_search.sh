#!/bin/bash

# Get auth token
echo "Getting auth token..."
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"superadmin","password":"Admin@123"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "Token: ${TOKEN:0:20}..."
echo ""

# Test 1: /api/courses/search
echo "Test 1: /api/courses/search?query=test"
curl -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/courses/search?query=test"
echo ""
echo "---"

# Test 2: /api/search (global search)
echo "Test 2: /api/search?query=test"
curl -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/search?query=test"
echo ""
echo "---"

# Test 3: /api/users/search
echo "Test 3: /api/users/search?query=student"
curl -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/users/search?query=student"
echo ""
echo "---"

# Test 4: /api/courses/search/name (should work)
echo "Test 4: /api/courses/search/name?query=test"
curl -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/courses/search/name?query=test"
echo ""
echo "---"

# Test 5: /api/attendance/faculty
echo "Test 5: /api/attendance/faculty"
curl -s -w "\nHTTP Status: %{http_code}\n" \
  -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/attendance/faculty"
echo ""
