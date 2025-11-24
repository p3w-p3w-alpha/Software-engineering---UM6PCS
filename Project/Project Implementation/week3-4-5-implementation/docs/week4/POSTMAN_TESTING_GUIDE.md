# Postman Testing Guide

## What is Postman?

**Postman** is a tool for testing APIs. Think of it as a way to send HTTP requests to your server without writing code.

### Why Use Postman?

- **Visual Interface**: Easy to use, no command-line needed
- **Save Requests**: Create collections of all your API calls
- **Test Different Scenarios**: Success cases, error cases, edge cases
- **Share with Team**: Export/import collections
- **Documentation**: Auto-generate API docs

---

## Installing Postman

### Option 1: Desktop App (Recommended)
1. Go to [https://www.postman.com/downloads/](https://www.postman.com/downloads/)
2. Download for your OS (Windows/Mac/Linux)
3. Install and create a free account

### Option 2: Web Version
1. Go to [https://web.postman.co/](https://web.postman.co/)
2. Sign in with Google or create account
3. Use in browser (requires account)

---

## Postman Basics

### Interface Overview

```
┌─────────────────────────────────────────────────┐
│  Collections    │   Request Builder             │
│  ============   │   ========================     │
│                 │   GET  http://localhost:8080  │
│  SAMS API       │   [▼]  [URL field      ] Send │
│    Create User  │                               │
│    Get Users    │   Params  Headers  Body       │
│    Update User  │   ─────────────────────────   │
│                 │                               │
│                 │   Response                    │
│                 │   ─────────────────────────   │
│                 │   Status: 200 OK              │
│                 │   { "id": 1, "name": "..." }  │
└─────────────────────────────────────────────────┘
```

### Key Components

1. **Collections**: Folders to organize requests
2. **Request Builder**: Where you create requests
3. **HTTP Method Dropdown**: GET, POST, PUT, DELETE
4. **URL Field**: Endpoint address
5. **Tabs**: Params, Headers, Body
6. **Send Button**: Execute request
7. **Response Pane**: Shows results

---

## Creating Your First Request

### Step 1: Create a Collection

1. Click **"Collections"** in left sidebar
2. Click **"+"** button or **"Create Collection"**
3. Name it: **"SAMS User Management"**
4. Click **"Create"**

### Step 2: Add a Request

1. Click on your collection
2. Click **"Add Request"**
3. Name it: **"Create User - Student"**
4. Click **"Save"**

### Step 3: Configure the Request

**Method:** Select **POST** from dropdown

**URL:** Enter:
```
http://localhost:8080/api/users
```

**Headers:** Click **"Headers"** tab, add:
- Key: `Content-Type`
- Value: `application/json`

**Body:** Click **"Body"** tab:
1. Select **"raw"**
2. Select **"JSON"** from dropdown
3. Enter:
```json
{
  "username": "john_doe",
  "email": "john.doe@student.com",
  "password": "password123",
  "role": "STUDENT"
}
```

### Step 4: Send Request

1. Make sure your Spring Boot app is running!
2. Click **"Send"** button
3. See response below

**Expected Response:**
```json
Status: 201 Created

{
  "id": 1,
  "username": "john_doe",
  "email": "john.doe@student.com",
  "role": "STUDENT",
  "createdAt": "2024-11-03T10:30:00"
}
```

---

## Testing All SAMS Endpoints

### 1. Create User (POST)

**Request:**
- Method: `POST`
- URL: `http://localhost:8080/api/users`
- Headers: `Content-Type: application/json`
- Body:
```json
{
  "username": "john_doe",
  "email": "john.doe@student.com",
  "password": "password123",
  "role": "STUDENT"
}
```

**Expected:** `201 Created` with user object

**Tips:**
- Try creating users with different roles: STUDENT, FACULTY, ADMIN
- Save the returned `id` for later tests

---

### 2. Get All Users (GET)

**Request:**
- Method: `GET`
- URL: `http://localhost:8080/api/users`
- No headers or body needed

**Expected:** `200 OK` with array of users

---

### 3. Get User by ID (GET)

**Request:**
- Method: `GET`
- URL: `http://localhost:8080/api/users/1`
- Replace `1` with actual user ID
- No headers or body needed

**Expected:** `200 OK` with single user object

**Error Test:**
Try: `http://localhost:8080/api/users/9999`
Expected: `404 Not Found`

---

### 4. Update User (PUT)

**Request:**
- Method: `PUT`
- URL: `http://localhost:8080/api/users/1`
- Headers: `Content-Type: application/json`
- Body:
```json
{
  "username": "john_doe_updated",
  "email": "john.doe@student.com",
  "password": "newpassword123",
  "role": "STUDENT"
}
```

**Expected:** `200 OK` with updated user object

---

### 5. Delete User (DELETE)

**Request:**
- Method: `DELETE`
- URL: `http://localhost:8080/api/users/1`
- No headers or body needed

**Expected:** `204 No Content` (empty response)

**Verify Deletion:**
Try getting the same user:
- `GET /api/users/1`
- Expected: `404 Not Found`

---

### 6. Get User by Email (GET)

**Request:**
- Method: `GET`
- URL: `http://localhost:8080/api/users/email/john.doe@student.com`
- No headers or body needed

**Expected:** `200 OK` with user object

---

### 7. Get Users by Role (GET)

**Request:**
- Method: `GET`
- URL: `http://localhost:8080/api/users/role/STUDENT`
- Try: STUDENT, FACULTY, ADMIN
- No headers or body needed

**Expected:** `200 OK` with array of users

---

## Testing Error Scenarios

### Test 1: Duplicate Email

**Steps:**
1. Create a user with email `test@example.com`
2. Try to create another user with same email

**Expected:** `400 Bad Request`
```json
{
  "status": 400,
  "message": "Email already exists: test@example.com",
  "timestamp": "2024-11-03T10:30:00"
}
```

---

### Test 2: Invalid User ID

**Steps:**
1. Try to get user with ID `99999`
2. `GET /api/users/99999`

**Expected:** `404 Not Found`
```json
{
  "status": 404,
  "message": "User not found with id: 99999",
  "timestamp": "2024-11-03T10:30:00"
}
```

---

### Test 3: Missing Required Fields

**Steps:**
1. Create user with empty email
2. POST with body:
```json
{
  "username": "test",
  "email": "",
  "password": ""
}
```

**Expected:** `400 Bad Request` with validation errors
```json
{
  "status": 400,
  "timestamp": "2024-11-03T10:30:00",
  "errors": {
    "email": "Email is required",
    "password": "Password must be at least 6 characters"
  }
}
```

---

### Test 4: Invalid Email Format

**Steps:**
1. Create user with invalid email
```json
{
  "username": "test",
  "email": "not-an-email",
  "password": "password123"
}
```

**Expected:** `400 Bad Request` with validation error

---

## Organizing Tests in Postman

### Create Folders

1. Right-click on collection
2. **"Add Folder"**
3. Create folders:
   - **Success Scenarios**
   - **Error Scenarios**
   - **CRUD Operations**

### Drag Requests into Folders

Organize by type:
```
SAMS User Management
├── CRUD Operations
│   ├── Create User
│   ├── Get All Users
│   ├── Get User by ID
│   ├── Update User
│   └── Delete User
├── Search Operations
│   ├── Get User by Email
│   └── Get Users by Role
└── Error Tests
    ├── Duplicate Email
    ├── Invalid ID
    └── Missing Fields
```

---

## Using Environment Variables

### Why Use Variables?

Instead of hardcoding `http://localhost:8080` everywhere, use a variable!

### Creating an Environment

1. Click **"Environments"** (gear icon)
2. Click **"Add"**
3. Name: **"SAMS Local"**
4. Add variable:
   - Variable: `base_url`
   - Initial Value: `http://localhost:8080`
   - Current Value: `http://localhost:8080`
5. Click **"Save"**

### Using Variables in Requests

**Old way:**
```
http://localhost:8080/api/users
```

**New way:**
```
{{base_url}}/api/users
```

Now you can switch between local/dev/prod easily!

---

## Importing the SAMS Collection

We've already created a collection for you!

### Import Steps

1. Click **"Import"** button (top left)
2. Click **"Upload Files"**
3. Select: `postman/SAMS_User_Management.postman_collection.json`
4. Click **"Import"**
5. Collection appears in left sidebar!

### What's Included

- ✅ All 7 main endpoints
- ✅ Success scenarios with example data
- ✅ Error scenarios (duplicate, not found, validation)
- ✅ Organized in logical order
- ✅ Pre-filled request bodies

---

## Reading Responses

### Status Code

Located top-right of response:
- **Green** (200, 201, 204): Success ✅
- **Orange** (400, 404): Client error ⚠️
- **Red** (500): Server error ❌

### Response Body

**Pretty View:** Formatted JSON (easy to read)
**Raw View:** Unformatted (copy-paste)

### Response Time

Shows how fast your API is:
- Under 100ms: Excellent
- 100-500ms: Good
- Over 500ms: Needs optimization

### Response Size

Shows response data size:
- Useful for checking pagination

---

## Complete Testing Workflow

### Step-by-Step Test Sequence

**1. Start Fresh**
```
DELETE all users (if any exist from previous tests)
```

**2. Create Users**
```
POST /api/users - Create Student
POST /api/users - Create Faculty
POST /api/users - Create Admin
```

**3. Verify Creation**
```
GET /api/users - Should see 3 users
```

**4. Test Retrieval**
```
GET /api/users/1 - Get first user
GET /api/users/email/john.doe@student.com
GET /api/users/role/STUDENT
```

**5. Test Update**
```
PUT /api/users/1 - Update student username
GET /api/users/1 - Verify update worked
```

**6. Test Delete**
```
DELETE /api/users/3 - Delete admin
GET /api/users - Should see 2 users now
GET /api/users/3 - Should get 404
```

**7. Test Errors**
```
POST /api/users - Duplicate email (should fail)
GET /api/users/999 - Invalid ID (should fail)
POST /api/users - Missing fields (should fail)
```

---

## Tips for Effective Testing

### 1. Test in Order
- Create before Update
- Create before Delete
- Create before Get by ID

### 2. Save Request Examples
- Click **"Save Response"** → **"Save as Example"**
- Helps document expected responses

### 3. Use Descriptive Names
- ✅ "Create User - Student (Happy Path)"
- ✅ "Get User - Invalid ID (Error)"
- ❌ "Test 1"
- ❌ "Request"

### 4. Add Descriptions
- Click **"..."** → **"Edit"**
- Add description explaining what the request tests

### 5. Check All Status Codes
- Success responses should be 200/201/204
- Errors should be 400/404/500

---

## Common Issues & Solutions

### Issue: Cannot Connect to Server

**Error:** `Error: connect ECONNREFUSED`

**Solution:**
1. Check Spring Boot app is running
2. Check URL is correct: `http://localhost:8080`
3. Check port 8080 is not blocked

---

### Issue: 404 Not Found on Valid Endpoint

**Error:** `404 Not Found` on `POST /api/users`

**Solution:**
1. Check URL spelling
2. Verify controller has `@RequestMapping("/api/users")`
3. Check app started without errors

---

### Issue: 400 Bad Request on POST

**Error:** `400 Bad Request` when creating user

**Solution:**
1. Check `Content-Type: application/json` header is set
2. Verify JSON syntax is valid (no trailing commas)
3. Check all required fields are included

---

### Issue: 500 Internal Server Error

**Error:** `500 Internal Server Error`

**Solution:**
1. Check Spring Boot console for stack trace
2. Verify database is running
3. Check service layer for bugs

---

## Exporting Results

### Share with Team

1. Click **"..."** next to collection
2. **"Export"**
3. Choose **Collection v2.1**
4. Save file
5. Share file with team

### Generate Documentation

1. Click **"..."** next to collection
2. **"View Documentation"**
3. Click **"Publish"**
4. Get shareable link

---

## Next Steps

After mastering Postman:
- Learn to write **automated tests** in Postman
- Use **Newman** to run collections from command line
- Integrate with **CI/CD pipelines**
- Use **Pre-request Scripts** for dynamic data
- Use **Tests** tab to write assertions

---

## Summary Checklist

After completing Week 4, you should be able to:

- ✅ Install and navigate Postman
- ✅ Create and organize collections
- ✅ Send GET, POST, PUT, DELETE requests
- ✅ Set headers and request bodies
- ✅ Read and interpret responses
- ✅ Test success scenarios
- ✅ Test error scenarios
- ✅ Import/export collections
- ✅ Use environment variables
- ✅ Debug API issues using Postman

---

## Resources

- [Postman Learning Center](https://learning.postman.com/)
- [Postman Tutorials](https://www.youtube.com/c/postman)
- SAMS Collection: `postman/SAMS_User_Management.postman_collection.json`
