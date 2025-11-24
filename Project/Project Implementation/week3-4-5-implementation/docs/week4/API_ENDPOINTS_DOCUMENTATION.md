# API Endpoints Documentation

Complete reference for all SAMS User Management API endpoints.

---

## Base URL

```
http://localhost:8080
```

All endpoints start with `/api/users`

---

## 1. Create User

**Endpoint:** `POST /api/users`

**Description:** Create a new user in the system

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john.doe@student.com",
  "password": "password123",
  "role": "STUDENT"
}
```

**Field Requirements:**
- `username` (required): 3-50 characters
- `email` (required): Valid email format, must be unique
- `password` (required): Minimum 6 characters
- `role` (optional): STUDENT, FACULTY, or ADMIN (default: STUDENT)

**Success Response:**

**Status Code:** `201 Created`

```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john.doe@student.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

**Error Responses:**

**400 Bad Request** - Validation failed
```json
{
  "status": 400,
  "timestamp": "2024-11-03T10:30:00",
  "errors": {
    "email": "Email should be valid",
    "password": "Password must be at least 6 characters"
  }
}
```

**400 Bad Request** - Duplicate email
```json
{
  "status": 400,
  "message": "Email already exists: john.doe@student.com",
  "timestamp": "2024-11-03T10:30:00"
}
```

**cURL Command:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john.doe@student.com",
    "password": "password123",
    "role": "STUDENT"
  }'
```

---

## 2. Get All Users

**Endpoint:** `GET /api/users`

**Description:** Retrieve list of all users

**Request Headers:** None required

**Request Body:** None

**Success Response:**

**Status Code:** `200 OK`

```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john.doe@student.com",
    "role": "STUDENT",
    "createdAt": "2024-11-03T10:30:00"
  },
  {
    "id": 2,
    "username": "prof_smith",
    "email": "smith@faculty.com",
    "role": "FACULTY",
    "createdAt": "2024-11-03T11:00:00"
  }
]
```

**Notes:**
- Returns empty array `[]` if no users exist
- Password is never included in response

**cURL Command:**
```bash
curl -X GET http://localhost:8080/api/users
```

---

## 3. Get User by ID

**Endpoint:** `GET /api/users/{id}`

**Description:** Retrieve a specific user by their ID

**Path Parameters:**
- `id` (required): User ID (Long)

**Example:** `GET /api/users/1`

**Success Response:**

**Status Code:** `200 OK`

```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john.doe@student.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

**Error Response:**

**404 Not Found** - User doesn't exist
```json
{
  "status": 404,
  "message": "User not found with id: 999",
  "timestamp": "2024-11-03T10:30:00"
}
```

**cURL Command:**
```bash
curl -X GET http://localhost:8080/api/users/1
```

---

## 4. Update User

**Endpoint:** `PUT /api/users/{id}`

**Description:** Update an existing user's details

**Path Parameters:**
- `id` (required): User ID to update

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "john_doe_updated",
  "email": "john.doe@student.com",
  "password": "newpassword123",
  "role": "STUDENT"
}
```

**Success Response:**

**Status Code:** `200 OK`

```json
{
  "id": 1,
  "username": "john_doe_updated",
  "email": "john.doe@student.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

**Error Responses:**

**404 Not Found** - User doesn't exist
```json
{
  "status": 404,
  "message": "User not found with id: 999",
  "timestamp": "2024-11-03T10:30:00"
}
```

**400 Bad Request** - Email already taken by another user
```json
{
  "status": 400,
  "message": "Email already exists: existing@example.com",
  "timestamp": "2024-11-03T10:30:00"
}
```

**cURL Command:**
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe_updated",
    "email": "john.doe@student.com",
    "password": "newpassword123",
    "role": "STUDENT"
  }'
```

---

## 5. Delete User

**Endpoint:** `DELETE /api/users/{id}`

**Description:** Delete a user from the system

**Path Parameters:**
- `id` (required): User ID to delete

**Success Response:**

**Status Code:** `204 No Content`

**Body:** Empty (no content returned)

**Error Response:**

**404 Not Found** - User doesn't exist
```json
{
  "status": 404,
  "message": "User not found with id: 999",
  "timestamp": "2024-11-03T10:30:00"
}
```

**cURL Command:**
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

---

## 6. Get User by Email

**Endpoint:** `GET /api/users/email/{email}`

**Description:** Retrieve a user by their email address

**Path Parameters:**
- `email` (required): User's email address

**Example:** `GET /api/users/email/john.doe@student.com`

**Success Response:**

**Status Code:** `200 OK`

```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john.doe@student.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

**Error Response:**

**404 Not Found** - No user with that email
```json
{
  "status": 404,
  "message": "User not found with email: nonexistent@example.com",
  "timestamp": "2024-11-03T10:30:00"
}
```

**cURL Command:**
```bash
curl -X GET http://localhost:8080/api/users/email/john.doe@student.com
```

**Note:** If email contains special characters, URL-encode them:
- `@` becomes `%40`
- Example: `john.doe%40student.com`

---

## 7. Get Users by Role

**Endpoint:** `GET /api/users/role/{role}`

**Description:** Retrieve all users with a specific role

**Path Parameters:**
- `role` (required): User role (STUDENT, FACULTY, or ADMIN)

**Example:** `GET /api/users/role/STUDENT`

**Success Response:**

**Status Code:** `200 OK`

```json
[
  {
    "id": 1,
    "username": "john_doe",
    "email": "john.doe@student.com",
    "role": "STUDENT",
    "createdAt": "2024-11-03T10:30:00"
  },
  {
    "id": 3,
    "username": "jane_smith",
    "email": "jane@student.com",
    "role": "STUDENT",
    "createdAt": "2024-11-03T12:00:00"
  }
]
```

**Notes:**
- Returns empty array `[]` if no users have that role
- Role parameter is case-sensitive (use uppercase)

**cURL Command:**
```bash
curl -X GET http://localhost:8080/api/users/role/STUDENT
```

---

## Summary Table

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/users` | Create user | No (Week 4) |
| GET | `/api/users` | Get all users | No (Week 4) |
| GET | `/api/users/{id}` | Get user by ID | No (Week 4) |
| PUT | `/api/users/{id}` | Update user | No (Week 4) |
| DELETE | `/api/users/{id}` | Delete user | No (Week 4) |
| GET | `/api/users/email/{email}` | Get user by email | No (Week 4) |
| GET | `/api/users/role/{role}` | Get users by role | No (Week 4) |

---

## Common HTTP Status Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| 200 | OK | Successful GET, PUT |
| 201 | Created | Successful POST |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation failed, duplicate data |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Unexpected server error |

---

## Testing Workflow

### Basic CRUD Test Sequence

1. **Create** a student user
   ```bash
   POST /api/users
   ```

2. **Verify** creation by getting all users
   ```bash
   GET /api/users
   ```

3. **Retrieve** specific user by ID
   ```bash
   GET /api/users/1
   ```

4. **Update** user details
   ```bash
   PUT /api/users/1
   ```

5. **Search** by email
   ```bash
   GET /api/users/email/john.doe@student.com
   ```

6. **Filter** by role
   ```bash
   GET /api/users/role/STUDENT
   ```

7. **Delete** user
   ```bash
   DELETE /api/users/1
   ```

8. **Verify** deletion (should return 404)
   ```bash
   GET /api/users/1
   ```

---

## Error Testing Scenarios

### Test Duplicate Email
```bash
# Create first user
POST /api/users
{
  "username": "user1",
  "email": "test@example.com",
  "password": "password123"
}

# Try to create another user with same email
POST /api/users
{
  "username": "user2",
  "email": "test@example.com",  ← Duplicate!
  "password": "password456"
}

# Expected: 400 Bad Request
```

### Test Invalid ID
```bash
GET /api/users/99999

# Expected: 404 Not Found
```

### Test Missing Required Fields
```bash
POST /api/users
{
  "username": "test"
  // Missing email and password
}

# Expected: 400 Bad Request with validation errors
```

---

## Postman Collection

Import the Postman collection for easy testing:
**File:** `postman/SAMS_User_Management.postman_collection.json`

The collection includes:
- ✅ All 7 main endpoints
- ✅ Success scenarios
- ✅ Error scenarios (duplicate email, invalid ID, missing fields)
- ✅ Pre-filled example requests

---

## Notes for Week 4

- **No authentication required**: All endpoints are publicly accessible for testing
- **Password not hashed**: Passwords are stored as plain text (encryption comes in Week 5)
- **No pagination**: GET /api/users returns all users (pagination comes later)
- **Basic validation**: Only essential field validation implemented

---

## Next Steps (Future Weeks)

- Add JWT authentication
- Implement role-based authorization
- Add pagination and sorting
- Implement password encryption
- Add advanced search filters
- Implement audit logging
