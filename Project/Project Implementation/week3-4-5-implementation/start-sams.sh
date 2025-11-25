#!/bin/bash

# SAMS - Complete Startup Script
# This script starts both backend and frontend servers

echo "=================================================="
echo "  SAMS - Student Academic Management System"
echo "  Complete Startup Script"
echo "=================================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if PostgreSQL is running
echo -e "${BLUE}[1/5]${NC} Checking PostgreSQL status..."
if command -v psql &> /dev/null; then
    if psql -U postgres -c '\l' &> /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} PostgreSQL is running"
    else
        echo -e "${YELLOW}⚠${NC} PostgreSQL is installed but may not be running"
        echo "Please start PostgreSQL manually if needed"
    fi
else
    echo -e "${YELLOW}⚠${NC} PostgreSQL not found in PATH"
fi

# Check if Java is installed
echo -e "${BLUE}[2/5]${NC} Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo -e "${GREEN}✓${NC} Java version: $JAVA_VERSION"
else
    echo -e "${RED}✗${NC} Java not found. Please install Java 17 or higher."
    exit 1
fi

# Check if Node.js is installed
echo -e "${BLUE}[3/5]${NC} Checking Node.js installation..."
if command -v node &> /dev/null; then
    NODE_VERSION=$(node -v)
    echo -e "${GREEN}✓${NC} Node.js version: $NODE_VERSION"
else
    echo -e "${RED}✗${NC} Node.js not found. Please install Node.js 18 or higher."
    exit 1
fi

# Install frontend dependencies if needed
echo -e "${BLUE}[4/5]${NC} Checking frontend dependencies..."
if [ ! -d "sams-frontend/node_modules" ]; then
    echo "Installing frontend dependencies..."
    cd sams-frontend
    npm install
    cd ..
    echo -e "${GREEN}✓${NC} Frontend dependencies installed"
else
    echo -e "${GREEN}✓${NC} Frontend dependencies already installed"
fi

# Start backend server
echo -e "${BLUE}[5/5]${NC} Starting servers..."
echo ""
echo -e "${GREEN}Starting Backend Server...${NC}"
echo "Backend will be available at: http://localhost:8080"

# Start backend in background
./mvnw spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"

# Wait for backend to start
echo "Waiting for backend to start (this may take 30-60 seconds)..."
sleep 5

# Check if backend started successfully
for i in {1..30}; do
    if curl -s http://localhost:8080/api/auth/validate > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Backend is ready!"
        break
    fi
    echo -n "."
    sleep 2
done
echo ""

# Start frontend server
echo ""
echo -e "${GREEN}Starting Frontend Server...${NC}"
echo "Frontend will be available at: http://localhost:5173"
cd sams-frontend
npm run dev &
FRONTEND_PID=$!
echo "Frontend PID: $FRONTEND_PID"
cd ..

echo ""
echo "=================================================="
echo -e "${GREEN}✓ SAMS is now running!${NC}"
echo "=================================================="
echo ""
echo "Access the application:"
echo "  Frontend: http://localhost:5173"
echo "  Backend:  http://localhost:8080"
echo ""
echo "Default Login Credentials:"
echo "  Username: superadmin"
echo "  Password: Admin@123"
echo ""
echo "To stop the servers:"
echo "  kill $BACKEND_PID $FRONTEND_PID"
echo ""
echo "Logs:"
echo "  Backend: backend.log"
echo "  Frontend: Terminal output"
echo ""
echo "=================================================="

# Save PIDs to file for easy cleanup
echo "$BACKEND_PID" > .backend.pid
echo "$FRONTEND_PID" > .frontend.pid

# Wait for user to stop
echo "Press Ctrl+C to stop all servers..."
trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; rm -f .backend.pid .frontend.pid; echo ''; echo 'Servers stopped.'; exit" INT TERM

# Keep script running
wait
