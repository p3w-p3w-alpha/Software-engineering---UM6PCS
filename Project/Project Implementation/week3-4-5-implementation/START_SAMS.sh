#!/bin/bash

# SAMS - Complete Startup and Testing Script
# This script will start both backend and frontend and verify everything works

echo "========================================="
echo "SAMS - Student Academic Management System"
echo "Complete Startup Script"
echo "========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Step 1: Check Prerequisites
print_status "Checking prerequisites..."

# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        print_success "Java $JAVA_VERSION found"
    else
        print_error "Java 17 or higher required, found Java $JAVA_VERSION"
        exit 1
    fi
else
    print_error "Java not found. Please install Java 17 or higher"
    exit 1
fi

# Check PostgreSQL
if command -v psql &> /dev/null; then
    print_success "PostgreSQL found"
else
    print_error "PostgreSQL not found. Please install PostgreSQL 14 or higher"
    exit 1
fi

# Check Node.js
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -ge 16 ]; then
        print_success "Node.js v$(node -v) found"
    else
        print_error "Node.js 16 or higher required"
        exit 1
    fi
else
    print_error "Node.js not found. Please install Node.js 16 or higher"
    exit 1
fi

# Check npm
if command -v npm &> /dev/null; then
    print_success "npm $(npm -v) found"
else
    print_error "npm not found"
    exit 1
fi

echo ""
print_success "All prerequisites met!"
echo ""

# Step 2: Check PostgreSQL Status
print_status "Checking PostgreSQL service..."
if pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    print_success "PostgreSQL is running on port 5432"
else
    print_error "PostgreSQL is not running"
    print_status "Attempting to start PostgreSQL..."
    sudo service postgresql start
    sleep 2
    if pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
        print_success "PostgreSQL started successfully"
    else
        print_error "Failed to start PostgreSQL. Please start it manually"
        exit 1
    fi
fi

echo ""

# Step 3: Check/Create Database
print_status "Checking database..."
if PGPASSWORD=postgres psql -U postgres -h localhost -lqt 2>/dev/null | cut -d \| -f 1 | grep -qw sams_db; then
    print_success "Database 'sams_db' exists"
else
    print_warning "Database 'sams_db' does not exist. Creating..."
    PGPASSWORD=postgres psql -U postgres -h localhost -c "CREATE DATABASE sams_db;" 2>/dev/null
    if [ $? -eq 0 ]; then
        print_success "Database 'sams_db' created successfully"
    else
        print_error "Failed to create database. Please create manually:"
        echo "  createdb -U postgres sams_db"
        exit 1
    fi
fi

echo ""

# Step 4: Backend Compilation
print_status "Compiling backend..."
cd "$(dirname "$0")"
mvn clean compile -DskipTests -q
if [ $? -eq 0 ]; then
    print_success "Backend compiled successfully"
else
    print_error "Backend compilation failed"
    print_status "Trying with verbose output..."
    mvn clean compile -DskipTests
    exit 1
fi

echo ""

# Step 5: Start Backend
print_status "Starting Spring Boot backend..."
print_warning "This will run in the background. Check backend.log for output"
mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > backend.pid
print_status "Backend PID: $BACKEND_PID"

# Wait for backend to start
print_status "Waiting for backend to start (this may take 30-60 seconds)..."
MAX_WAIT=60
WAITED=0
while [ $WAITED -lt $MAX_WAIT ]; do
    if curl -s http://localhost:8080/api/auth/validate > /dev/null 2>&1; then
        print_success "Backend is running on http://localhost:8080"
        break
    fi
    sleep 2
    WAITED=$((WAITED + 2))
    echo -n "."
done
echo ""

if [ $WAITED -ge $MAX_WAIT ]; then
    print_error "Backend failed to start within $MAX_WAIT seconds"
    print_status "Check backend.log for errors"
    print_status "Backend PID: $BACKEND_PID"
    print_warning "You can manually kill it with: kill $BACKEND_PID"
    exit 1
fi

echo ""

# Step 6: Create Demo Data
print_status "Would you like to create demo data? (y/n)"
read -t 10 -p "Enter choice (default: y): " CREATE_DEMO
CREATE_DEMO=${CREATE_DEMO:-y}

if [[ "$CREATE_DEMO" == "y" || "$CREATE_DEMO" == "Y" ]]; then
    print_status "Creating demo data..."
    if [ -f "CREATE_DEMO_DATA.sql" ]; then
        PGPASSWORD=postgres psql -U postgres -h localhost -d sams_db -f CREATE_DEMO_DATA.sql > /dev/null 2>&1
        if [ $? -eq 0 ]; then
            print_success "Demo data created successfully"
            echo ""
            print_status "Demo Credentials:"
            echo "  Admin: admin / password123"
            echo "  Faculty: jsmith / password123"
            echo "  Student: adavis / password123"
        else
            print_warning "Failed to create demo data (tables may not exist yet)"
            print_status "You can run it manually later:"
            echo "  psql -U postgres -d sams_db -f CREATE_DEMO_DATA.sql"
        fi
    else
        print_warning "CREATE_DEMO_DATA.sql not found"
    fi
fi

echo ""

# Step 7: Frontend Setup
print_status "Setting up frontend..."
cd sams-frontend

if [ ! -d "node_modules" ]; then
    print_status "Installing frontend dependencies..."
    npm install
    if [ $? -eq 0 ]; then
        print_success "Frontend dependencies installed"
    else
        print_error "Failed to install frontend dependencies"
        exit 1
    fi
else
    print_success "Frontend dependencies already installed"
fi

echo ""

# Step 8: Start Frontend
print_status "Starting Vue.js frontend..."
print_warning "This will run in the foreground. Press Ctrl+C to stop"
print_status "Frontend will be available at: http://localhost:5173"
echo ""
print_status "========================================="
print_success "SAMS is starting up!"
print_status "Backend: http://localhost:8080"
print_status "Frontend: http://localhost:5173 (starting...)"
print_status "========================================="
echo ""
sleep 2

# Start frontend (this will run in foreground)
npm run dev

# Cleanup on exit
trap cleanup EXIT

cleanup() {
    echo ""
    print_status "Shutting down SAMS..."
    if [ -f "../backend.pid" ]; then
        BACKEND_PID=$(cat ../backend.pid)
        print_status "Stopping backend (PID: $BACKEND_PID)..."
        kill $BACKEND_PID 2>/dev/null
        rm ../backend.pid
    fi
    print_success "SAMS stopped"
}
