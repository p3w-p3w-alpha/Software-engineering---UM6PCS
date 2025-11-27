#!/bin/bash

# SAMS - Clean Start Script
# This script kills all existing processes and starts fresh

echo "========================================="
echo "SAMS - Clean Startup Script"
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

# Step 1: Kill all existing processes
print_status "Stopping all existing SAMS processes..."

# Kill backend (port 8080)
print_status "Killing processes on port 8080..."
lsof -ti:8080 | xargs kill -9 2>/dev/null && print_success "Killed backend process" || print_warning "No process on port 8080"

# Kill frontend (port 5173)
print_status "Killing processes on port 5173..."
lsof -ti:5173 | xargs kill -9 2>/dev/null && print_success "Killed frontend process" || print_warning "No process on port 5173"

# Kill any Maven processes
print_status "Killing Maven processes..."
pkill -f "mvn spring-boot:run" 2>/dev/null && print_success "Killed Maven processes" || print_warning "No Maven processes found"

# Kill any npm/vite processes
print_status "Killing npm/vite processes..."
pkill -f "vite" 2>/dev/null && print_success "Killed Vite processes" || print_warning "No Vite processes found"
pkill -f "npm run dev" 2>/dev/null && print_success "Killed npm processes" || print_warning "No npm processes found"

echo ""
print_success "All existing processes stopped!"
echo ""

# Wait a moment for ports to be released
sleep 2

# Step 2: Check PostgreSQL
print_status "Checking PostgreSQL service..."
if pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    print_success "PostgreSQL is running"
else
    print_warning "PostgreSQL is not running. Attempting to start..."
    sudo service postgresql start
    sleep 3
    if pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
        print_success "PostgreSQL started successfully"
    else
        print_error "Failed to start PostgreSQL. Please start it manually."
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
        print_error "Failed to create database"
        exit 1
    fi
fi

echo ""

# Step 4: Start Backend
print_status "Starting Spring Boot backend..."
print_warning "Backend will run in the background. Check backend_clean.log for output"

# Navigate to project directory
cd "$(dirname "$0")"

# Clean and start backend
mvn clean compile -DskipTests -q
if [ $? -ne 0 ]; then
    print_error "Backend compilation failed"
    exit 1
fi

# Start backend in background
nohup mvn spring-boot:run > backend_clean.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > backend.pid
print_status "Backend starting with PID: $BACKEND_PID"

# Wait for backend to start
print_status "Waiting for backend to start (this may take 30-60 seconds)..."
MAX_WAIT=60
WAITED=0
while [ $WAITED -lt $MAX_WAIT ]; do
    if curl -s http://localhost:8080/api/auth/validate > /dev/null 2>&1; then
        echo ""
        print_success "Backend is running on http://localhost:8080"
        break
    fi
    sleep 3
    WAITED=$((WAITED + 3))
    echo -n "."
done

echo ""

if [ $WAITED -ge $MAX_WAIT ]; then
    print_error "Backend failed to start within $MAX_WAIT seconds"
    print_status "Check backend_clean.log for errors"
    print_status "Backend PID: $BACKEND_PID"
    print_warning "You can manually kill it with: kill $BACKEND_PID"
    exit 1
fi

echo ""

# Step 5: Start Frontend
print_status "Starting Vue.js frontend..."
cd sams-frontend

# Install dependencies if needed
if [ ! -d "node_modules" ]; then
    print_status "Installing frontend dependencies..."
    npm install
    if [ $? -ne 0 ]; then
        print_error "Failed to install frontend dependencies"
        exit 1
    fi
fi

print_success "Frontend dependencies ready"
echo ""

print_status "========================================="
print_success "SAMS is starting!"
print_status "========================================="
echo ""
print_status "Backend:  http://localhost:8080"
print_status "Frontend: http://localhost:5173 (starting...)"
echo ""
print_status "Demo Credentials:"
echo "  Admin:   superadmin / Admin@123"
echo "  Student: student1 / password123"
echo "  Faculty: faculty1 / password123"
echo ""
print_status "========================================="
echo ""

# Start frontend (this will run in foreground)
print_status "Starting frontend development server..."
npm run dev

# Cleanup function
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

# Set trap for cleanup on exit
trap cleanup EXIT INT TERM
