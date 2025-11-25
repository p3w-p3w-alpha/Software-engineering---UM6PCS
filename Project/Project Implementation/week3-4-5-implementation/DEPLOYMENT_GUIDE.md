# SAMS - Complete Deployment Guide

## Production-Ready Student Academic Management System

### Overview
This is a comprehensive, enterprise-grade Student Academic Management System with complete frontend and backend integration.

---

## Prerequisites

### Backend Requirements
- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### Frontend Requirements
- Node.js 18+
- npm 9+

---

## Quick Start Guide

### 1. Database Setup

```bash
# Create PostgreSQL database
psql -U postgres

CREATE DATABASE sams_db;
CREATE USER sams_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE sams_db TO sams_user;
\q
```

### 2. Backend Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/sams_db
spring.datasource.username=sams_user
spring.datasource.password=your_secure_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

jwt.secret=your-256-bit-secret-key-here-make-it-very-long-and-random
jwt.expiration=86400000

spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

enrollment.max.credits.per.semester=18
enrollment.min.credits.full.time=12
```

### 3. Start Backend

```bash
# From project root directory
./mvnw clean install
./mvnw spring-boot:run
```

Backend will start on `http://localhost:8080`

### 4. Start Frontend

```bash
# Navigate to frontend directory
cd sams-frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

Frontend will start on `http://localhost:5173`

---

## Default Credentials

### Super Admin Account
- **Username:** `superadmin`
- **Password:** `Admin@123`
- **Role:** SUPER_ADMIN
- **Note:** This account is created automatically on first startup and CANNOT be deleted

### Test Accounts
You need to create these through the admin panel after logging in as superadmin:

**Sample Student:**
- Username: `student1`
- Password: (set by admin)
- Role: STUDENT

**Sample Faculty:**
- Username: `faculty1`
- Password: (set by admin)
- Role: FACULTY

---

## System Features

### For Students
✅ **Course Enrollment**
- Browse available courses
- Enroll with prerequisite validation
- Schedule conflict detection
- Waitlist management
- Credit limit enforcement (max 18 credits)

✅ **Payment Management**
- View payment status
- Submit payment with transaction reference
- Payment history tracking
- Real-time status updates

✅ **Academic Tracking**
- View grades and GPA
- Track degree progress
- Completion percentage visualization
- Graduation eligibility status

✅ **Assignments**
- View assignments
- File upload (PDF, ZIP)
- Submission tracking

### For Faculty
✅ **Course Management**
- View assigned courses
- Monitor enrollment
- Track student performance

✅ **Grade Management**
- Enter grades
- View class statistics

✅ **Assignment Management**
- Create assignments
- Review submissions

### For Admins
✅ **User Management**
- Create STUDENT and FACULTY accounts
- Update user information
- Activate/deactivate users
- Delete users (with restrictions)
- SUPER_ADMIN can create ADMIN accounts

✅ **Payment Approval**
- Review submitted payments
- Approve/reject with audit trail
- View payment history
- Automatic enrollment activation on approval

✅ **System Management**
- Semester configuration
- Course management
- Degree program setup
- System reports

### For Super Admin
All admin features PLUS:
- Create ADMIN accounts
- Manage admin permissions
- Cannot be deleted (system protection)

---

## User Workflow

### 1. Admin Creates Accounts
1. Login as `superadmin` / `Admin@123`
2. Navigate to User Management
3. Create STUDENT and FACULTY accounts
4. Set initial passwords

### 2. Student Enrollment Process
1. Student logs in
2. Browses courses → Enrolls (status: PENDING_PAYMENT)
3. System validates:
   - Prerequisites completed
   - No schedule conflicts
   - Credit limit not exceeded
   - Enrollment period open
4. Student navigates to Payments
5. Submits payment with transaction reference (status: PAID)
6. Admin reviews and approves payment (status: APPROVED)
7. Enrollments automatically become ACTIVE
8. Student can access course materials

### 3. Grade Entry and Degree Tracking
1. Faculty enters grades for students
2. System automatically calculates GPA
3. Student degree progress updates automatically
4. System tracks completion percentage
5. Students can view graduation eligibility

---

## API Endpoints Reference

### Authentication
- `POST /api/auth/login` - Login with username/password
- `GET /api/auth/validate` - Validate JWT token

### User Management (ADMIN/SUPER_ADMIN)
- `GET /api/admin/users` - Get all users
- `POST /api/admin/users` - Create user
- `PUT /api/admin/users/{id}` - Update user
- `DELETE /api/admin/users/{id}` - Delete user
- `PATCH /api/admin/users/{id}/toggle-active` - Activate/deactivate

### Courses
- `GET /api/courses` - Get all courses
- `POST /api/courses` - Create course
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course

### Enrollments
- `GET /api/enrollments/student/{id}` - Get student enrollments
- `POST /api/enrollments` - Create enrollment
- `DELETE /api/enrollments/{id}/drop` - Drop enrollment
- `GET /api/enrollments/waitlist/{courseId}` - Get waitlist

### Payments
- `POST /api/payments/create` - Create payment for semester
- `POST /api/payments/{id}/submit` - Submit payment
- `POST /api/payments/{id}/approve` - Approve payment (ADMIN)
- `POST /api/payments/{id}/reject` - Reject payment (ADMIN)
- `GET /api/payments/student/{id}` - Get student payments
- `GET /api/payments/pending-approval` - Get pending approvals

### Grades
- `GET /api/grades/student/{id}` - Get student grades
- `POST /api/grades/assign` - Assign grade
- `GET /api/grades/course/{id}` - Get course grades

### Degree Progress
- `GET /api/degree-progress/programs` - Get degree programs
- `POST /api/degree-progress/programs` - Create program (ADMIN)
- `GET /api/degree-progress/students/{id}/progress` - Get student progress
- `POST /api/degree-progress/students/{id}/graduate` - Mark graduated (ADMIN)

### File Upload
- `POST /api/files/upload/assignment` - Upload assignment file
- `GET /api/files/download?filePath={path}` - Download file
- `DELETE /api/files/delete?filePath={path}` - Delete file

### WebSocket
- Connect: `ws://localhost:8080/ws`
- Subscribe to notifications: `/user/{userId}/queue/notifications`
- Subscribe to messages: `/user/{userId}/queue/messages`
- Send group message: `/app/group/send`

---

## Security Features

### Authentication & Authorization
- JWT-based stateless authentication
- Role-based access control (RBAC)
- Method-level security with @PreAuthorize
- Token expiration (24 hours default)
- Password encryption (BCrypt)

### Data Protection
- SUPER_ADMIN protection (cannot be deleted)
- Permission-based admin actions
- Soft delete for historical data
- Audit trail for payments

### API Security
- CORS configuration
- CSRF protection
- Input validation
- File type restrictions (PDF, ZIP only)
- File size limits (10MB max)

---

## Production Deployment

### Backend Deployment

1. **Build for Production**
```bash
./mvnw clean package -DskipTests
```

2. **Run with Production Profile**
```bash
java -jar target/student-academic-management-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

3. **Environment Variables**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/sams_db
export SPRING_DATASOURCE_USERNAME=sams_user
export SPRING_DATASOURCE_PASSWORD=your_secure_password
export JWT_SECRET=your-production-secret-key-256-bits
```

### Frontend Deployment

1. **Build for Production**
```bash
cd sams-frontend
npm run build
```

2. **Deploy `dist` folder** to:
   - Nginx
   - Apache
   - Netlify
   - Vercel
   - AWS S3 + CloudFront

**Sample Nginx Configuration:**
```nginx
server {
    listen 80;
    server_name yourdomain.com;
    root /var/www/sams-frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /ws {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
```

---

## Database Schema

The system uses the following main entities:

- **users** - User accounts with roles
- **courses** - Course catalog
- **enrollments** - Student course enrollments
- **grades** - Student grades
- **payments** - Payment records
- **payment_history** - Payment audit trail
- **semesters** - Academic semesters
- **degree_programs** - Degree programs
- **degree_requirements** - Program requirements
- **student_degree_progress** - Student progress tracking
- **assignments** - Course assignments
- **notifications** - User notifications
- **study_groups** - Collaborative groups

---

## Troubleshooting

### Backend Issues

**Database Connection Failed**
- Verify PostgreSQL is running
- Check credentials in application.properties
- Ensure database exists

**Port 8080 Already in Use**
- Change port in application.properties: `server.port=8081`
- Update frontend API URL accordingly

**JWT Token Invalid**
- Check JWT secret is set
- Verify token hasn't expired
- Clear browser localStorage

### Frontend Issues

**Cannot Connect to Backend**
- Verify backend is running on port 8080
- Check CORS configuration in SecurityConfig
- Update API_BASE_URL in services/api.js if needed

**Build Fails**
- Delete node_modules and package-lock.json
- Run `npm install` again
- Check Node.js version (must be 18+)

**WebSocket Connection Failed**
- Verify WebSocket dependency in pom.xml
- Check SecurityConfig allows /ws/** endpoints
- Ensure backend is running

---

## Key Business Rules

### Enrollment Rules
1. Only STUDENT role can enroll in courses
2. Must not already be enrolled in the course
3. Semester must be active with open registration
4. Prerequisites must be completed
5. No schedule conflicts with existing courses
6. Credit limit: max 18 credits per semester
7. Enrollments start as PENDING_PAYMENT

### Payment Workflow
1. Student enrolls → Creates PENDING_PAYMENT enrollment
2. System creates/updates payment for semester
3. Student submits payment with transaction reference
4. Payment status: PAID (awaiting approval)
5. Admin reviews and approves → Status: APPROVED
6. All associated enrollments become ACTIVE
7. Student gains access to course materials

### User Management
1. Students and Faculty CANNOT self-register
2. Only ADMIN/SUPER_ADMIN can create accounts
3. SUPER_ADMIN can create ADMIN accounts
4. SUPER_ADMIN account cannot be deleted
5. Only SUPER_ADMIN can delete ADMIN accounts

---

## Technology Stack

### Backend
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- WebSocket (STOMP)
- Hibernate Validator

### Frontend
- Vue 3 (Composition API)
- Vue Router 4
- Pinia (State Management)
- Tailwind CSS 4
- Axios (HTTP Client)
- STOMP WebSocket Client
- Headless UI
- Heroicons
- Chart.js (for visualizations)

---

## Support & Maintenance

### Logs
- Backend logs: `logs/spring-boot-application.log`
- Check console output for errors

### Monitoring
- Check `/actuator/health` endpoint (if Spring Actuator enabled)
- Monitor database connections
- Track payment approval times

### Backups
- Regular database backups recommended
- Backup uploaded files in `uploads/` directory
- Export payment records for audit compliance

---

## Next Steps for Production

1. **Security Hardening**
   - Change default SUPER_ADMIN password
   - Use strong JWT secret (256-bit minimum)
   - Enable HTTPS/SSL
   - Configure proper CORS origins
   - Set up rate limiting

2. **Performance Optimization**
   - Enable database connection pooling
   - Configure caching (Redis)
   - Optimize database indexes
   - Enable gzip compression

3. **Monitoring & Logging**
   - Set up application monitoring (e.g., Prometheus)
   - Configure centralized logging (ELK stack)
   - Set up error tracking (Sentry)

4. **Scalability**
   - Use cloud file storage (AWS S3)
   - Configure load balancer
   - Set up database replication
   - Implement Redis for WebSocket scaling

5. **Compliance**
   - Regular security audits
   - GDPR compliance (if applicable)
   - Data retention policies
   - Regular backups

---

## Features Implemented

### ✅ Complete Backend (100%)
- Authentication & Authorization (JWT + RBAC)
- User Management with SUPER_ADMIN protection
- Course Management with prerequisites
- Enrollment System with business rules
- Payment & Billing System with admin approval
- Grade Management with GPA calculation
- Degree Progress Tracking
- File Upload/Download Service
- WebSocket Infrastructure
- Notification System
- Global Exception Handling
- Audit Trail (Payment History)

### ✅ Complete Frontend (100%)
- Login & Authentication
- Student Portal
  - Dashboard with stats
  - Course browsing and enrollment
  - Payment submission interface
  - Grades view with GPA
  - Degree progress tracker with visualization
- Admin Portal
  - Dashboard with system stats
  - User management CRUD
  - Payment approval workflow
- Faculty Portal
  - Dashboard with course overview
  - Assignment management
  - Grade entry
- Real-time WebSocket integration
- Responsive design (mobile-friendly)
- Professional UI/UX with Tailwind CSS

---

## License
Educational Project - UM6P Computer Science

## Contributors
Academic Management System Team

---

## Additional Resources

- API Documentation: See Postman collection in `/postman` directory
- Backend Code: `/src/main/java/com/sams`
- Frontend Code: `/sams-frontend/src`
- Database Schema: Auto-generated by Hibernate

For issues or questions, refer to the project documentation or contact the development team.
