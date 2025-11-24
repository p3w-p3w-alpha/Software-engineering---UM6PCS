#!/bin/bash
# ============================================
# SAMS - Quick Start Script (Linux/Mac)
# ============================================

echo ""
echo "========================================"
echo "  SAMS - Student Academic Management"
echo "========================================"
echo ""

# Check if Java is installed
echo "[1/4] Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "[ERROR] Java is not installed or not in PATH"
    echo "Please install Java 17 or higher from: https://adoptium.net/"
    exit 1
fi
echo "[OK] Java is installed"
echo ""

# Check if Maven is installed
echo "[2/4] Checking Maven installation..."
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven is not installed or not in PATH"
    echo "Please install Maven from: https://maven.apache.org/download.cgi"
    exit 1
fi
echo "[OK] Maven is installed"
echo ""

# Check if PostgreSQL is running
echo "[3/4] Checking PostgreSQL..."
if ! nc -z localhost 5432 2>/dev/null; then
    echo "[WARNING] PostgreSQL might not be running on port 5432"
    echo "Please make sure PostgreSQL is running and database 'sams_db' exists"
    echo ""
    read -p "Press Enter to continue anyway, or Ctrl+C to exit..."
fi
echo "[OK] PostgreSQL appears to be running"
echo ""

# Build and Run
echo "[4/4] Starting SAMS application..."
echo "This may take a minute on first run..."
echo ""

mvn spring-boot:run

# If the application exits
echo ""
echo "========================================"
echo "Application stopped"
echo "========================================"
