# VS Code + PowerShell Quick Start ⚡

**Get SAMS running in 10 minutes**

---

## ✅ Step-by-Step Checklist

### □ Step 1: Install VS Code Java Extension (2 minutes)

1. Open **VS Code**
2. Press `Ctrl+Shift+X` (Extensions)
3. Search: **Extension Pack for Java**
4. Click **Install**
5. Wait for installation to complete

---

### □ Step 2: Verify Java (30 seconds)

Open **PowerShell** (Win+X → Windows PowerShell):

```powershell
java -version
```

**Expected:** `java version "17"` or higher

**If not installed:**
- Download: https://adoptium.net/
- Get: Temurin 17 (LTS)
- Install and restart PowerShell

---

### □ Step 3: Install Maven (3 minutes)

In **PowerShell as Administrator**:

```powershell
# Install Chocolatey (package manager)
Set-ExecutionPolicy Bypass -Scope Process -Force
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Install Maven
choco install maven -y
```

**Close and reopen PowerShell**, then verify:

```powershell
mvn -version
```

**Expected:** Maven version 3.x.x

---

### □ Step 4: Create Database (1 minute)

**Option A - pgAdmin (Easy):**
1. Open pgAdmin
2. Right-click "Databases" → Create → Database
3. Name: `sams_db`
4. Save

**Option B - Command Line:**
```powershell
psql -U postgres
CREATE DATABASE sams_db;
\q
```

---

### □ Step 5: Open Project in VS Code (2 minutes)

1. Open **VS Code**
2. Press `Ctrl+K, Ctrl+O`
3. Navigate to: `C:\Users\nassi\Desktop\ART - CL1 - 01\Software-engineering-UM6PCS\Project\Project Implementation\week3-4-5-implementation`
4. Click "Select Folder"
5. **Wait** for Maven to download dependencies (watch bottom right)

---

### □ Step 6: Configure Database Password (30 seconds)

1. In VS Code, open: `src → main → resources → application.properties`
2. Line 10: Change password to YOUR PostgreSQL password
   ```properties
   spring.datasource.password=YourPasswordHere
   ```
3. Save (Ctrl+S)

---

### □ Step 7: Set PowerShell as Default Terminal (30 seconds)

1. In VS Code, press `Ctrl+Shift+P`
2. Type: "Terminal: Select Default Profile"
3. Select: **PowerShell**

---

### □ Step 8: Run Application (1 minute)

**Option A - Click Run (Easiest):**
1. Open: `src → main → java → com → sams → SamsApplication.java`
2. Click **"Run"** above the main method
3. Watch terminal output

**Option B - PowerShell Command:**
```powershell
# Press Ctrl+` to open terminal
mvn spring-boot:run
```

**Wait for:**
```
Started SamsApplication in X.XXX seconds
```

---

### □ Step 9: Test in Browser (30 seconds)

Open browser → Go to:
```
http://localhost:8080/api/users
```

**Expected:** `[]`

**✅ SUCCESS! Your API is running!**

---

### □ Step 10: Run Automated Tests (1 minute)

In VS Code terminal:

```powershell
.\test-api.ps1
```

**Expected:** All 9 tests pass with green checkmarks ✅

---

## 🎯 Quick Reference

### Start Application:
```powershell
# In VS Code terminal (Ctrl+`)
mvn spring-boot:run
```

### Stop Application:
```
Ctrl+C
```

### Run Tests:
```powershell
mvn test
```

### Test API:
```powershell
.\test-api.ps1
```

---

## 🔍 Verify Everything Works

### ✅ Checklist:

- [ ] VS Code opens project without errors
- [ ] Terminal shows PowerShell prompt
- [ ] `mvn -version` works in terminal
- [ ] Application starts without errors
- [ ] Browser shows `[]` at http://localhost:8080/api/users
- [ ] `.\test-api.ps1` shows all tests passing
- [ ] `mvn test` shows 29 tests passing

**All checked? You're ready to go! 🚀**

---

## 🆘 Common Issues

### "mvn: command not found"
→ Maven not installed. Go back to Step 3.

### "Port 8080 already in use"
→ Change port in `application.properties`: `server.port=8081`

### "Connection refused" in browser
→ Application not running. Check VS Code terminal for errors.

### "Database does not exist"
→ Create `sams_db` in pgAdmin (Step 4)

### "Password authentication failed"
→ Update password in `application.properties` (Step 6)

---

## 📚 Full Documentation

- **Complete Guide:** `VSCODE_POWERSHELL_GUIDE.md`
- **All Commands:** `COMMANDS_CHEAT_SHEET.md`
- **Testing Guide:** `TESTING_WALKTHROUGH.md`
- **Documentation Index:** `DOCUMENTATION_INDEX.md`

---

## 🎉 You're Done!

Your SAMS project is now running in VS Code with PowerShell!

**Next steps:**
- Test with Postman (import collections from `postman/` folder)
- Read the learning guides in `docs/week5/`
- Explore the 29 API endpoints
- Run unit tests: `mvn test`

**Happy coding! 💻**
