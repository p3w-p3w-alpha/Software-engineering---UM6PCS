# SAMS Project - Complete Documentation Index

**Project:** Student Academic Management System (SAMS)
**Weeks Covered:** Week 3, Week 4, Week 5
**Last Updated:** November 17, 2025

---

## Quick Navigation

- [Week 3 Documentation](#week-3-documentation) - User Management System
- [Week 4 Documentation](#week-4-documentation) - Enhanced User Management
- [Week 5 Documentation](#week-5-documentation) - Course & Enrollment Management
- [Getting Started](#getting-started)

---

## Week 3 Documentation

**Location:** `docs/week3/`

**Focus:** Basic User Management System (CRUD operations, authentication setup, database integration)

### Core Documentation Files

1. **SETUP_GUIDE.md** - Complete setup instructions for the project
2. **PROJECT_STRUCTURE.md** - Detailed explanation of the project structure
3. **DATABASE_GUIDE.md** - PostgreSQL database setup and configuration
4. **SECURITY_EXPLAINED.md** - Spring Security and password hashing explained
5. **API_DOCUMENTATION.md** - User management API endpoints documentation
6. **WEEK3_LEARNING_SUMMARY.md** - Comprehensive learning summary for Week 3
7. **PRESENTATION_PREP.md** - Presentation preparation guide
8. **TROUBLESHOOTING.md** - Common issues and solutions
9. **TESTING_GUIDE.md** - Testing guide for Week 3

### Key Features Implemented
- User entity and database schema
- User repository with custom queries
- User service with business logic
- User controller with REST endpoints
- Password hashing with BCrypt
- Global exception handling
- DTO pattern for request/response
- Unit tests with JUnit and Mockito

---

## Week 4 Documentation

**Location:** `docs/week4/`

**Focus:** Enhanced User Management (Improved service layer, testing, and API design)

### Core Documentation Files

1. **WEEK4_OVERVIEW.md** - Overview of Week 4 enhancements
2. **REPOSITORY_PATTERN_EXPLAINED.md** - Deep dive into repository pattern
3. **SERVICE_LAYER_GUIDE.md** - Service layer architecture guide
4. **REST_API_BASICS.md** - REST API fundamentals explained
5. **API_ENDPOINTS_DOCUMENTATION.md** - Detailed API endpoint documentation
6. **TESTING_EXPLAINED.md** - Testing concepts and practices
7. **POSTMAN_TESTING_GUIDE.md** - Guide for testing with Postman
8. **WEEK4_LEARNING_SUMMARY.md** - Comprehensive learning summary for Week 4
9. **PRESENTATION_PREP_WEEK4.md** - Week 4 presentation guide
10. **CODE_STRUCTURE_DIAGRAM.md** - Visual diagrams of code structure
11. **TROUBLESHOOTING_WEEK4.md** - Week 4 specific troubleshooting
12. **INDEX.md** - Week 4 documentation index

### Key Features Implemented
- Enhanced password hashing in UserService
- Improved error handling
- Additional custom repository queries
- Expanded unit test coverage
- Postman collection for user management

---

## Week 5 Documentation

**Location:** `docs/week5/`

**Focus:** Course & Enrollment Management System (Full course lifecycle, student enrollment, capacity management)

### Core Documentation Files

1. **WEEK_4_AND_5_LEARNING_GUIDE.md** (60+ pages)
   - Comprehensive learning guide covering both Week 4 and Week 5
   - Technology explanations in simple language
   - System architecture with ASCII diagrams
   - Feature-by-feature walkthroughs
   - Database structure explanation
   - All 29 API endpoints documented
   - Testing guide and presentation tips

2. **WEEK_4_AND_5_IMPLEMENTATION_SUMMARY.md**
   - Complete summary of all work done
   - Statistics and metrics
   - File lists and verification checklists

3. **QUICK_START_GUIDE.md**
   - 5-minute setup guide
   - Quick testing instructions
   - Essential commands reference

### Key Features Implemented

#### New Entities
- **Course Entity** - Courses with instructor relationships
- **Enrollment Entity** - Many-to-many relationship between students and courses

#### New Repositories
- **CourseRepository** - Course database operations with search
- **EnrollmentRepository** - Enrollment database operations

#### New Services
- **CourseService** - Course business logic (duplicate prevention, instructor assignment, capacity management)
- **EnrollmentService** - Enrollment business logic (enrollment validation, capacity checking, status management)

#### New Controllers
- **CourseController** - 11 REST endpoints for course management
- **EnrollmentController** - 11 REST endpoints for enrollment management

#### New DTOs
- CourseRequest, CourseResponse
- EnrollmentRequest, EnrollmentResponse

#### New Exceptions
- CourseNotFoundException
- DuplicateCourseCodeException
- EnrollmentNotFoundException
- CourseFullException
- AlreadyEnrolledException

#### Testing
- CourseServiceTest - 10 unit tests
- EnrollmentServiceTest - 11 unit tests
- Postman collection with 28 requests (14 for courses, 14 for enrollments)

---

## Getting Started

### For Week 3 Only
```bash
# Read in this order:
1. docs/week3/SETUP_GUIDE.md
2. docs/week3/PROJECT_STRUCTURE.md
3. docs/week3/DATABASE_GUIDE.md
4. docs/week3/WEEK3_LEARNING_SUMMARY.md
```

### For Week 4 Only
```bash
# Read in this order:
1. docs/week4/WEEK4_OVERVIEW.md
2. docs/week4/SERVICE_LAYER_GUIDE.md
3. docs/week4/API_ENDPOINTS_DOCUMENTATION.md
4. docs/week4/WEEK4_LEARNING_SUMMARY.md
```

### For Week 5 Only
```bash
# Read in this order:
1. docs/week5/QUICK_START_GUIDE.md
2. docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md
3. docs/week5/WEEK_4_AND_5_IMPLEMENTATION_SUMMARY.md
```

### For Complete Understanding (All Weeks)
```bash
# Recommended reading order:
1. docs/week3/SETUP_GUIDE.md - Get the project running
2. docs/week3/WEEK3_LEARNING_SUMMARY.md - Understand Week 3
3. docs/week4/WEEK4_LEARNING_SUMMARY.md - Understand Week 4
4. docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md - Deep dive into Week 5
5. docs/week5/QUICK_START_GUIDE.md - Quick reference
```

---

## Running the Project

### Prerequisites
- Java 17+
- PostgreSQL 12+
- Maven 3.6+
- Postman (for API testing)

### Quick Start
```bash
# 1. Setup database (see docs/week3/DATABASE_GUIDE.md)
createdb sams_db

# 2. Configure application.properties
# Edit src/main/resources/application.properties with your DB credentials

# 3. Run the application
mvn spring-boot:run

# 4. Test with Postman
# Import collections from postman/ directory
```

---

## Testing

### Unit Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest
mvn test -Dtest=CourseServiceTest
mvn test -Dtest=EnrollmentServiceTest
```

### API Testing with Postman
- **Week 3:** Import `postman/SAMS_User_Management.postman_collection.json`
- **Week 5:** Import `postman/SAMS_Course_Enrollment_Management.postman_collection.json`

See `docs/week4/POSTMAN_TESTING_GUIDE.md` for detailed instructions.

---

## Presentation Preparation

### Week 3 Presentation
- **Guide:** `docs/week3/PRESENTATION_PREP.md`
- **Focus:** User management, security basics, repository pattern

### Week 4 Presentation
- **Guide:** `docs/week4/PRESENTATION_PREP_WEEK4.md`
- **Focus:** Service layer architecture, API design, testing practices

### Week 5 Presentation
- **Guide:** `docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md` (Section 14)
- **Focus:** Course management, enrollment system, entity relationships

---

## Troubleshooting

### Week 3 Issues
See: `docs/week3/TROUBLESHOOTING.md`

### Week 4 Issues
See: `docs/week4/TROUBLESHOOTING_WEEK4.md`

### Week 5 Issues
See: `docs/week5/WEEK_4_AND_5_LEARNING_GUIDE.md` (Section 13)

---

## Project Statistics

### Code Coverage
- **Entities:** 4 (User, Course, Enrollment, plus DTOs)
- **Repositories:** 3 (UserRepository, CourseRepository, EnrollmentRepository)
- **Services:** 3 (UserService, CourseService, EnrollmentService)
- **Controllers:** 3 (UserController, CourseController, EnrollmentController)
- **Exception Classes:** 10 custom exceptions
- **Unit Tests:** 31+ tests total
- **API Endpoints:** 29 total (User: 7, Course: 11, Enrollment: 11)

### Documentation
- **Total Documentation Files:** 25+ markdown files
- **Total Pages:** 200+ pages of documentation
- **Diagrams:** Multiple ASCII diagrams for architecture and data flow

---

## Directory Structure

```
week3-4-5-implementation/
├── docs/
│   ├── week3/          # Week 3 documentation (9 files)
│   ├── week4/          # Week 4 documentation (12 files)
│   └── week5/          # Week 5 documentation (3 comprehensive files)
├── src/
│   ├── main/
│   │   └── java/com/sams/
│   │       ├── entity/         # Domain entities
│   │       ├── repository/     # Data access layer
│   │       ├── service/        # Business logic layer
│   │       ├── controller/     # REST API layer
│   │       ├── dto/            # Data transfer objects
│   │       └── exception/      # Custom exceptions
│   └── test/
│       └── java/com/sams/
│           └── service/        # Service unit tests
├── postman/                    # Postman collections
├── DOCUMENTATION_INDEX.md      # This file
└── README.md                   # Project readme

```

---

## Additional Resources

### External Documentation
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- PostgreSQL: https://www.postgresql.org/docs/

### Code Style Notes
- Intentional typos in comments (e.g., "chekc" instead of "check") for humanly-made code style
- Constructor-based dependency injection throughout
- Comprehensive JavaDoc comments
- Consistent naming conventions

---

## Contact & Support

For questions or issues:
1. Check the troubleshooting guides first
2. Review the learning summaries for each week
3. Consult the comprehensive Week 5 learning guide

---

**Happy Learning! 🎓**
