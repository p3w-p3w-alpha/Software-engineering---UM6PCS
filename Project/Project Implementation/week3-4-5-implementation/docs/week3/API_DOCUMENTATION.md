# API Documentation

Complete guide to testing and using the SAMS REST API endpoints.

## Base URL

```
http://localhost:8080
```

Make sure your application is running before testing!

---

## Endpoints

### 1. User Registration

**Register a new user account**

#### Endpoint Details

- **URL:** `/api/users/register`
- **Method:** `POST`
- **Auth Required:** No (Public endpoint)
- **Content-Type:** `application/json`

#### Request Body

```json
{
  "username": "string (3-50 characters, required)",
  "email": "string (valid email, required)",
  "password": "string (min 6 characters, required)",
  "role": "string (optional, defaults to STUDENT)"
}
```

#### Success Response

**Code:** `201 CREATED`

**Response Body:**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "STUDENT",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Note:** Password is NOT included in response (security best practice)

#### Error Responses

**Username Already Exists**

**Code:** `400 BAD REQUEST`

```json
"Error: Username is already taken"
```

---

**Email Already Exists**

**Code:** `400 BAD REQUEST`

```json
"Error: Email is already in use"
```

---

**Validation Failed**

**Code:** `400 BAD REQUEST`

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "errors": {
    "username": "Username is required",
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

---

## Testing the API

### Method 1: Using Postman (Recommended for Beginners)

#### Setup Postman

1. Open Postman
2. Click "New" → "Request"
3. Name it "Register User"
4. Save to a collection (create "SAMS API" collection)

#### Configure Request

1. **Method:** Select `POST` from dropdown
2. **URL:** Enter `http://localhost:8080/api/users/register`
3. **Headers:**
   - Click "Headers" tab
   - Add: `Content-Type` = `application/json`
4. **Body:**
   - Click "Body" tab
   - Select "raw"
   - Select "JSON" from dropdown
   - Enter request body:

```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepass123",
  "role": "STUDENT"
}
```

5. **Send:** Click blue "Send" button

#### Expected Result

- Status: `201 Created`
- Response shows user data without password
- Time taken displayed (should be < 1 second)

#### Test Different Scenarios

**Test 1: Successful Registration**
```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "password123"
}
```
Expected: 201 Created ✓

**Test 2: Duplicate Username**
```json
{
  "username": "alice",
  "email": "alice2@example.com",
  "password": "password123"
}
```
Expected: 400 Bad Request - "Username is already taken" ✗

**Test 3: Invalid Email**
```json
{
  "username": "bob",
  "email": "not-an-email",
  "password": "password123"
}
```
Expected: 400 Bad Request - Validation error ✗

**Test 4: Password Too Short**
```json
{
  "username": "charlie",
  "email": "charlie@example.com",
  "password": "123"
}
```
Expected: 400 Bad Request - "Password must be at least 6 characters" ✗

**Test 5: Missing Required Fields**
```json
{
  "username": "dave"
}
```
Expected: 400 Bad Request - Multiple validation errors ✗

---

### Method 2: Using cURL (Command Line)

#### Basic Registration

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepass123",
    "role": "STUDENT"
  }'
```

#### Pretty Print Response (Linux/Mac)

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "janedoe",
    "email": "jane@example.com",
    "password": "securepass456"
  }' | json_pp
```

#### Show Response Headers

```bash
curl -i -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "password123"
  }'
```

Output:
```
HTTP/1.1 201
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 15 Jan 2024 10:30:00 GMT

{"id":1,"username":"alice","email":"alice@example.com","role":"STUDENT","createdAt":"2024-01-15T10:30:00"}
```

#### Windows PowerShell

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/register" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "securepass123"
  }'
```

---

### Method 3: Using IntelliJ HTTP Client

Create a file: `api-tests.http` in your project root

```http
### Register User - Success
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepass123",
  "role": "STUDENT"
}

### Register User - Duplicate Username (Should fail)
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "different@example.com",
  "password": "anotherpass"
}

### Register User - Invalid Email (Should fail)
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "not-an-email",
  "password": "password123"
}
```

Click the green ▶️ icon next to each request to execute it.

---

### Method 4: Using JavaScript (Fetch API)

For frontend integration:

```javascript
async function registerUser(username, email, password) {
  try {
    const response = await fetch('http://localhost:8080/api/users/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        username: username,
        email: email,
        password: password,
        role: 'STUDENT'
      })
    });

    if (response.ok) {
      const user = await response.json();
      console.log('Registration successful:', user);
      return user;
    } else {
      const error = await response.text();
      console.error('Registration failed:', error);
      throw new Error(error);
    }
  } catch (error) {
    console.error('Network error:', error);
    throw error;
  }
}

// Usage
registerUser('johndoe', 'john@example.com', 'securepass123')
  .then(user => console.log('User created:', user))
  .catch(err => console.error('Error:', err));
```

---

## Verifying Data in Database

### Using pgAdmin

1. Open pgAdmin
2. Navigate to: Servers → PostgreSQL → Databases → sams_db
3. Right-click "sams_db" → Query Tool
4. Run query:

```sql
SELECT id, username, email, role, created_at, updated_at
FROM users
ORDER BY created_at DESC;
```

**Notice:**
- `password` is hashed (starts with $2a$10$...)
- `created_at` and `updated_at` are set automatically
- IDs auto-increment (1, 2, 3, ...)

### Using psql (Command Line)

```bash
# Connect to database
psql -U postgres -d sams_db

# View all users
SELECT * FROM users;

# View specific user
SELECT * FROM users WHERE username = 'johndoe';

# Count total users
SELECT COUNT(*) FROM users;

# Exit
\q
```

---

## Understanding REST API Conventions

### HTTP Methods

- **POST** - Create new resource (registration)
- **GET** - Retrieve resource (fetch user data)
- **PUT** - Update entire resource
- **PATCH** - Update part of resource
- **DELETE** - Remove resource

### HTTP Status Codes

#### Success Codes (2xx)
- **200 OK** - Request succeeded (GET, PUT, PATCH)
- **201 Created** - Resource created successfully (POST)
- **204 No Content** - Succeeded, no response body (DELETE)

#### Client Error Codes (4xx)
- **400 Bad Request** - Invalid data/validation failed
- **401 Unauthorized** - Authentication required
- **403 Forbidden** - Authenticated but no permission
- **404 Not Found** - Resource doesn't exist
- **409 Conflict** - Conflict with current state (duplicate)

#### Server Error Codes (5xx)
- **500 Internal Server Error** - Server crashed
- **503 Service Unavailable** - Server temporarily down

### RESTful URL Design

Our API follows REST conventions:

```
/api/users              - User collection
/api/users/register     - Registration action
/api/users/{id}         - Specific user (future)
/api/users/{id}/courses - User's courses (future)
```

**Best Practices:**
- Use plural nouns (`/users` not `/user`)
- Use kebab-case for multi-word (`/course-enrollments`)
- Avoid verbs in URLs (use HTTP methods instead)
- Use nesting for relationships (`/users/1/courses`)

---

## Common Issues and Solutions

### Issue: Connection Refused

**Error:**
```
Error: connect ECONNREFUSED 127.0.0.1:8080
```

**Solution:**
- Make sure Spring Boot application is running
- Check console for "Started SamsApplication"
- Verify port 8080 is correct

---

### Issue: 404 Not Found

**Error:**
```
404 - Not Found
```

**Solutions:**
- Check URL spelling: `/api/users/register` (case-sensitive!)
- Verify controller is in `com.sams` package or subpackage
- Check `@RequestMapping` annotations

---

### Issue: 400 Bad Request

**Error:**
```
400 - Bad Request
```

**Common Causes:**
1. Missing `Content-Type: application/json` header
2. Invalid JSON syntax (missing comma, quote, etc.)
3. Validation failed (check error message)

**Fix:**
```bash
# Wrong - no Content-Type
curl -X POST http://localhost:8080/api/users/register \
  -d '{"username":"john"}'

# Correct - with Content-Type
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123"}'
```

---

### Issue: 500 Internal Server Error

**Error:**
```
500 - Internal Server Error
```

**Solutions:**
- Check Spring Boot console for stack trace
- Common causes:
  - Database not running
  - Wrong database credentials
  - Code exception

---

## API Testing Checklist

Before submitting/presenting:

- [ ] Application starts without errors
- [ ] Can register a new user successfully
- [ ] Duplicate username returns error
- [ ] Duplicate email returns error
- [ ] Invalid email format returns error
- [ ] Short password returns error
- [ ] Missing fields return validation errors
- [ ] Response does NOT include password
- [ ] User data appears in database
- [ ] Password is hashed in database
- [ ] Timestamps (createdAt, updatedAt) are set

---

## Future Endpoints (Week 4+)

```
POST   /api/auth/login              - User login
POST   /api/auth/logout             - User logout
GET    /api/users/{id}              - Get user by ID
PUT    /api/users/{id}              - Update user
DELETE /api/users/{id}              - Delete user
GET    /api/users                   - List all users (admin)
GET    /api/users/me                - Get current user profile
```

---

## Sample Postman Collection (JSON)

Save this as `SAMS.postman_collection.json`:

```json
{
  "info": {
    "name": "SAMS API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Register User",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"johndoe\",\n  \"email\": \"john@example.com\",\n  \"password\": \"securepass123\",\n  \"role\": \"STUDENT\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/users/register",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "users", "register"]
        }
      }
    }
  ]
}
```

Import this file into Postman: File → Import → Select file

---

**Next:** Read [WEEK3_LEARNING_SUMMARY.md](WEEK3_LEARNING_SUMMARY.md) to review everything you've learned!
