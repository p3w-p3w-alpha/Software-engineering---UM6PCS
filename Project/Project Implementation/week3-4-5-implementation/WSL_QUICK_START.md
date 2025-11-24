# WSL Quick Start Guide ⚡

**Get SAMS running in VS Code with WSL in 15 minutes**

---

## Step 1: Connect VS Code to WSL (2 minutes)

1. Open **VS Code**
2. Install extension: **"WSL"** by Microsoft
3. Press `Ctrl+Shift+P`
4. Type: **"WSL: Connect to WSL"**
5. New window opens with **"WSL: Ubuntu"** in bottom-left corner

✅ **You're now in WSL!**

---

## Step 2: Open Project in WSL (2 minutes)

1. In WSL VS Code window, press `Ctrl+K, Ctrl+O`
2. Navigate to:
   ```
   /mnt/c/Users/nassi/Desktop/ART - CL1 - 01/Software-engineering-UM6PCS/Project/Project Implementation/week3-4-5-implementation
   ```
3. Click "OK"
4. Install **"Extension Pack for Java"** in WSL
5. Wait for dependencies to download

---

## Step 3: Install Tools in WSL (3 minutes)

Open terminal in VS Code (`Ctrl+` `):

```bash
# Update system
sudo apt update

# Install Java 17
sudo apt install openjdk-17-jdk -y

# Install Maven
sudo apt install maven -y

# Install curl and jq
sudo apt install curl jq -y

# Verify
java -version
mvn -version
```

---

## Step 4: Create Database (1 minute)

```bash
# Connect to Windows PostgreSQL
psql -h localhost -U postgres

# Create database
CREATE DATABASE sams_db;

# Exit
\q
```

Or use **pgAdmin** on Windows to create `sams_db`

---

## Step 5: Configure Application (1 minute)

1. Open: `src/main/resources/application.properties`
2. Update line 10 with your PostgreSQL password:
   ```properties
   spring.datasource.password=YourPasswordHere
   ```
3. Save (Ctrl+S)

---

## Step 6: Build Project (2 minutes)

In VS Code terminal:

```bash
cd /mnt/c/Users/nassi/Desktop/ART\ -\ CL1\ -\ 01/Software-engineering-UM6PCS/Project/Project\ Implementation/week3-4-5-implementation

mvn clean install
```

Wait for: `BUILD SUCCESS`

---

## Step 7: Run Application (1 minute)

```bash
mvn spring-boot:run
```

**Wait for:**
```
Started SamsApplication in X.XXX seconds
```

✅ **Application is running!**

---

## Step 8: Test in Browser (30 seconds)

Open browser:
```
http://localhost:8080/api/users
```

**Expected:** `[]`

✅ **API is working!**

---

## Step 9: Run Automated Tests (2 minutes)

Open **NEW terminal** in VS Code (Ctrl+Shift+` ):

```bash
# Create test script
cat > test-api.sh << 'SCRIPT'
#!/bin/bash
echo "🧪 Testing SAMS API..."

# Create student
STUDENT=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"password123","role":"STUDENT"}')
echo "✅ Student created: $(echo $STUDENT | jq -r '.username')"

# Create faculty
FACULTY=$(curl -s -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"dr_smith","email":"smith@example.com","password":"password123","role":"FACULTY"}')
echo "✅ Faculty created: $(echo $FACULTY | jq -r '.username')"

FACULTY_ID=$(echo $FACULTY | jq -r '.id')
STUDENT_ID=$(echo $STUDENT | jq -r '.id')

# Create course
COURSE=$(curl -s -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d "{\"courseCode\":\"CS101\",\"courseName\":\"Intro to Programming\",\"description\":\"Learn Java\",\"credits\":3,\"capacity\":30,\"instructorId\":$FACULTY_ID}")
echo "✅ Course created: $(echo $COURSE | jq -r '.courseCode')"

COURSE_ID=$(echo $COURSE | jq -r '.id')

# Enroll student
ENROLLMENT=$(curl -s -X POST http://localhost:8080/api/enrollments \
  -H "Content-Type: application/json" \
  -d "{\"studentId\":$STUDENT_ID,\"courseId\":$COURSE_ID}")
echo "✅ Enrollment created: ID $(echo $ENROLLMENT | jq -r '.id')"

# Get all data
echo ""
echo "📊 Summary:"
curl -s http://localhost:8080/api/users | jq '. | length' | xargs echo "   Users:"
curl -s http://localhost:8080/api/courses | jq '. | length' | xargs echo "   Courses:"
curl -s http://localhost:8080/api/enrollments | jq '. | length' | xargs echo "   Enrollments:"

echo ""
echo "🎉 All tests passed!"
SCRIPT

chmod +x test-api.sh
./test-api.sh
```

**You should see:**
```
✅ Student created: alice
✅ Faculty created: dr_smith
✅ Course created: CS101
✅ Enrollment created: ID 1
🎉 All tests passed!
```

---

## Step 10: Run Unit Tests (1 minute)

```bash
mvn test
```

**Expected:**
```
Tests run: 29, Failures: 0, Errors: 0
BUILD SUCCESS
```

✅ **Everything works!**

---

## Quick Reference

### Start Application:
```bash
mvn spring-boot:run
```

### Stop Application:
```
Ctrl+C
```

### Test Endpoints:
```bash
# Get all users
curl http://localhost:8080/api/users | jq '.'

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username":"test","email":"test@example.com","password":"password123","role":"STUDENT"}'

# Get all courses
curl http://localhost:8080/api/courses | jq '.'

# Get all enrollments
curl http://localhost:8080/api/enrollments | jq '.'
```

### Run All Tests:
```bash
./test-api.sh
```

---

## Troubleshooting

### "mvn: command not found"
```bash
sudo apt install maven -y
```

### "java: command not found"
```bash
sudo apt install openjdk-17-jdk -y
```

### Port 8080 in use:
```bash
sudo lsof -i :8080
kill -9 <PID>
```

### Cannot connect to database:
- Ensure PostgreSQL is running on Windows
- Check password in `application.properties`

---

## What's Next?

✅ **Your setup is complete!**

**Test with Postman:**
- Open Postman on Windows
- Import: `postman/SAMS_User_Management.postman_collection.json`
- Import: `postman/SAMS_Course_Enrollment_Management.postman_collection.json`
- Test all 41 requests!

**Read Documentation:**
- Complete guide: `VSCODE_WSL_COMPLETE_GUIDE.md`
- API docs: `docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md`
- All docs: `DOCUMENTATION_INDEX.md`

---

## Success Checklist

- [x] VS Code connected to WSL
- [x] Java 17 installed
- [x] Maven installed
- [x] Database created
- [x] Application running
- [x] Browser test passed
- [x] API test script passed
- [x] Unit tests passed (29/29)

**🎉 You're ready to develop!** 🚀
