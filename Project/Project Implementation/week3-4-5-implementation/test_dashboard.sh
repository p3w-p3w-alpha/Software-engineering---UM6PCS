#!/bin/bash

# Login and get token
echo "Logging in..."
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' > /tmp/dashboard_auth.json

TOKEN=$(grep -o '"token":"[^"]*' /tmp/dashboard_auth.json | cut -d'"' -f4)

echo "Token: ${TOKEN:0:50}..."
echo ""

# Test 1: Get Dashboard Stats
echo "TEST 1: Get Dashboard Stats"
curl -s -X GET "http://localhost:8080/api/dashboard/stats" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 2: Get Complete Dashboard
echo "TEST 2: Get Complete Dashboard"
curl -s -X GET "http://localhost:8080/api/dashboard/complete" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 3: Get Enrollment Trends
echo "TEST 3: Get Enrollment Trends"
curl -s -X GET "http://localhost:8080/api/dashboard/enrollment-trends?months=6" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

# Test 4: Get Financial Summary
echo "TEST 4: Get Financial Summary"
curl -s -X GET "http://localhost:8080/api/dashboard/financial-summary" \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "All dashboard tests completed!"
