# SAMS - Student Academic Management System

A full-stack web application for managing student academic activities, built with Spring Boot (Java 17) and Vue.js 3.

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL
- **Security:** Spring Security with JWT Authentication
- **Real-time:** WebSocket (STOMP)
- **Build Tool:** Maven

### Frontend
- **Framework:** Vue.js 3.5
- **State Management:** Pinia
- **UI Components:** PrimeVue, Tailwind CSS
- **Charts:** Chart.js with vue-chartjs
- **Animations:** GSAP, AOS, Framer Motion
- **Build Tool:** Vite

## Features

- User authentication (Admin, Faculty, Student roles)
- Course management and enrollment
- Grade tracking and transcripts
- Attendance management
- Assignment submission system
- Payment/Fee management
- Real-time notifications via WebSocket
- Study groups with messaging
- Analytics dashboards
- Report generation

---

## Prerequisites

Before running this project, make sure you have the following installed:

| Requirement | Version | Check Command |
|-------------|---------|---------------|
| Java JDK | 17+ | `java -version` |
| Maven | 3.8+ | `mvn -version` |
| Node.js | 18+ | `node -version` |
| npm | 9+ | `npm -version` |
| PostgreSQL | 14+ | `psql --version` |

---

## Setup Instructions

### Option 1: Using WSL (Windows Subsystem for Linux)

#### Step 1: Install PostgreSQL in WSL

```bash
# Update packages
sudo apt update

# Install PostgreSQL
sudo apt install postgresql postgresql-contrib

# Start PostgreSQL service
sudo service postgresql start

# Switch to postgres user and create database
sudo -u postgres psql
```

Inside the PostgreSQL prompt:
```sql
CREATE DATABASE sams_db;
ALTER USER postgres WITH PASSWORD 'postgres';
\q
```

#### Step 2: Clone and Setup Backend

```bash
# Navigate to project directory
cd /mnt/c/Users/YOUR_USERNAME/path/to/SAMS-code

# Compile the backend
mvn clean compile

# Run the Spring Boot application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

#### Step 3: Setup Frontend (in a new WSL terminal)

```bash
# Navigate to frontend directory
cd /mnt/c/Users/YOUR_USERNAME/path/to/SAMS-code/sams-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

---

### Option 2: Using Windows PowerShell

#### Step 1: Install PostgreSQL on Windows

1. Download PostgreSQL from https://www.postgresql.org/download/windows/
2. Run the installer and remember your password (default: `postgres`)
3. Open pgAdmin or use psql from PowerShell:

```powershell
# Open psql (adjust path if needed)
& "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres
```

Inside psql:
```sql
CREATE DATABASE sams_db;
\q
```

#### Step 2: Clone and Setup Backend

```powershell
# Navigate to project directory
cd "C:\Users\YOUR_USERNAME\path\to\SAMS-code"

# Compile the backend
mvn clean compile

# Run the Spring Boot application
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

#### Step 3: Setup Frontend (in a new PowerShell window)

```powershell
# Navigate to frontend directory
cd "C:\Users\YOUR_USERNAME\path\to\SAMS-code\sams-frontend"

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

---

## Database Configuration

The application expects PostgreSQL with these default settings:

| Setting | Value |
|---------|-------|
| Host | localhost |
| Port | 5432 |
| Database | sams_db |
| Username | postgres |
| Password | postgres |

To modify these settings, edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## Running the Application

### Quick Start (Both Terminals)

**Terminal 1 - Backend:**
```bash
# WSL
cd /mnt/c/path/to/SAMS-code && mvn spring-boot:run

# PowerShell
cd "C:\path\to\SAMS-code"; mvn spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
# WSL
cd /mnt/c/path/to/SAMS-code/sams-frontend && npm run dev

# PowerShell
cd "C:\path\to\SAMS-code\sams-frontend"; npm run dev
```

### Access the Application

| Service | URL |
|---------|-----|
| Frontend | http://localhost:5173 |
| Backend API | http://localhost:8080 |
| WebSocket | ws://localhost:8080/ws |

---

## Default User Accounts

After starting the application, the following default accounts are created:

| Role | Username | Password |
|------|----------|----------|
| Super Admin | superadmin | admin123 |
| Admin | admin | admin123 |
| Faculty | faculty1 | password123 |
| Student | student1 | password123 |

---

## Build for Production

### Backend
```bash
mvn clean package -DskipTests
java -jar target/student-academic-management-0.0.1-SNAPSHOT.jar
```

### Frontend
```bash
cd sams-frontend
npm run build
npm run preview
```

The production build will be in `sams-frontend/dist/`

---

## Project Structure

```
SAMS-code/
├── src/
│   └── main/
│       ├── java/com/sams/
│       │   ├── config/          # Security, WebSocket config
│       │   ├── controller/      # REST API endpoints
│       │   ├── dto/             # Data transfer objects
│       │   ├── entity/          # JPA entities
│       │   ├── exception/       # Custom exceptions
│       │   ├── repository/      # Data access layer
│       │   ├── security/        # JWT authentication
│       │   └── service/         # Business logic
│       └── resources/
│           └── application.properties
├── sams-frontend/
│   ├── src/
│   │   ├── components/          # Reusable Vue components
│   │   ├── views/               # Page components
│   │   ├── stores/              # Pinia state stores
│   │   ├── services/            # API and WebSocket services
│   │   ├── router/              # Vue Router config
│   │   └── layouts/             # Layout components
│   ├── package.json
│   └── vite.config.js
├── setup-database.sql           # Database setup script
├── pom.xml                      # Maven config
└── README.md
```

---

## Troubleshooting

### PostgreSQL Connection Failed
```
# WSL: Make sure PostgreSQL is running
sudo service postgresql status
sudo service postgresql start

# Windows: Check PostgreSQL service in Services app
```

### Port Already in Use
```bash
# Find process using port 8080
# WSL/Linux
lsof -i :8080
kill -9 <PID>

# PowerShell
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Node Modules Issues
```bash
# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Maven Build Fails
```bash
# Clean and rebuild
mvn clean install -DskipTests
```

---

## API Documentation

The backend exposes RESTful APIs at `http://localhost:8080/api/`

Key endpoints:
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/courses` - List courses
- `GET /api/enrollments` - List enrollments
- `GET /api/grades` - List grades
- `GET /api/dashboard/stats` - Dashboard statistics

All protected endpoints require JWT token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

---

## License

This project was developed for educational purposes as part of the Software Engineering course at UM6P.
