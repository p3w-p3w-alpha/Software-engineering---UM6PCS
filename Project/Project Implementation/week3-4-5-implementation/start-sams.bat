@echo off
REM SAMS - Complete Startup Script for Windows
REM This script starts both backend and frontend servers

echo ==================================================
echo   SAMS - Student Academic Management System
echo   Complete Startup Script
echo ==================================================
echo.

REM Check Java
echo [1/4] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java not found. Please install Java 17 or higher.
    pause
    exit /b 1
)
echo [OK] Java is installed
echo.

REM Check Node.js
echo [2/4] Checking Node.js installation...
node -v >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Node.js not found. Please install Node.js 18 or higher.
    pause
    exit /b 1
)
echo [OK] Node.js is installed
echo.

REM Install frontend dependencies if needed
echo [3/4] Checking frontend dependencies...
if not exist "sams-frontend\node_modules" (
    echo Installing frontend dependencies...
    cd sams-frontend
    call npm install
    cd ..
    echo [OK] Frontend dependencies installed
) else (
    echo [OK] Frontend dependencies already installed
)
echo.

REM Start servers
echo [4/4] Starting servers...
echo.
echo Starting Backend Server...
echo Backend will be available at: http://localhost:8080
start "SAMS Backend" cmd /c "mvnw.cmd spring-boot:run"

timeout /t 10 /nobreak >nul

echo.
echo Starting Frontend Server...
echo Frontend will be available at: http://localhost:5173
cd sams-frontend
start "SAMS Frontend" cmd /c "npm run dev"
cd ..

echo.
echo ==================================================
echo [OK] SAMS is starting!
echo ==================================================
echo.
echo Access the application:
echo   Frontend: http://localhost:5173
echo   Backend:  http://localhost:8080
echo.
echo Default Login Credentials:
echo   Username: superadmin
echo   Password: Admin@123
echo.
echo Two new windows have opened for backend and frontend.
echo Close those windows to stop the servers.
echo.
echo ==================================================
pause
