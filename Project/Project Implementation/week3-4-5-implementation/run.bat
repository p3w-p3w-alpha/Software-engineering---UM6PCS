@echo off
REM ============================================
REM SAMS - Quick Start Script (Windows)
REM ============================================

echo.
echo ========================================
echo   SAMS - Student Academic Management
echo ========================================
echo.

REM Check if Java is installed
echo [1/4] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java is not installed or not in PATH
    echo Please install Java 17 or higher from: https://adoptium.net/
    pause
    exit /b 1
)
echo [OK] Java is installed
echo.

REM Check if Maven is installed
echo [2/4] Checking Maven installation...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven is not installed or not in PATH
    echo Please install Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo [OK] Maven is installed
echo.

REM Check if PostgreSQL is running
echo [3/4] Checking PostgreSQL...
netstat -an | findstr ":5432" >nul 2>&1
if errorlevel 1 (
    echo [WARNING] PostgreSQL might not be running on port 5432
    echo Please make sure PostgreSQL is running and database 'sams_db' exists
    echo.
    echo Press any key to continue anyway, or Ctrl+C to exit...
    pause >nul
)
echo [OK] PostgreSQL appears to be running
echo.

REM Build and Run
echo [4/4] Starting SAMS application...
echo This may take a minute on first run...
echo.

mvn spring-boot:run

REM If the application exits
echo.
echo ========================================
echo Application stopped
echo ========================================
pause
