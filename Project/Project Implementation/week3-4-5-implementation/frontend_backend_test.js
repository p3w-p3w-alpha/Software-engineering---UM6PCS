// SAMS Frontend-Backend Integration Test Script
// Tests all major functionalities to ensure 100% backend integration

const axios = require('axios');
const API_BASE = 'http://localhost:8080/api';
let authToken = null;

// Test Results Storage
const testResults = {
  passed: [],
  failed: [],
  skipped: [],
  totalEndpoints: 0
};

// Color output helpers
const colors = {
  green: '\x1b[32m',
  red: '\x1b[31m',
  yellow: '\x1b[33m',
  cyan: '\x1b[36m',
  reset: '\x1b[0m'
};

// Helper functions
function logSuccess(message) {
  console.log(`${colors.green}✓${colors.reset} ${message}`);
  testResults.passed.push(message);
}

function logError(message) {
  console.log(`${colors.red}✗${colors.reset} ${message}`);
  testResults.failed.push(message);
}

function logInfo(message) {
  console.log(`${colors.cyan}ℹ${colors.reset} ${message}`);
}

function logSection(title) {
  console.log(`\n${colors.yellow}${'='.repeat(60)}${colors.reset}`);
  console.log(`${colors.yellow}${title}${colors.reset}`);
  console.log(`${colors.yellow}${'='.repeat(60)}${colors.reset}`);
}

// Create axios instance with auth
function getAxios() {
  const instance = axios.create({
    baseURL: API_BASE,
    headers: authToken ? { Authorization: `Bearer ${authToken}` } : {}
  });

  instance.interceptors.response.use(
    response => response,
    error => {
      if (error.response) {
        return Promise.reject({
          status: error.response.status,
          data: error.response.data,
          message: error.message
        });
      }
      return Promise.reject(error);
    }
  );

  return instance;
}

// Test Authentication
async function testAuthentication() {
  logSection('1. AUTHENTICATION TESTS');

  try {
    // Test login with each role
    const testUsers = [
      { username: 'superadmin', password: 'password123', role: 'SUPER_ADMIN' },
      { username: 'nassim', password: 'password123', role: 'STUDENT' },
      { username: 'aya', password: 'password123', role: 'FACULTY' }
    ];

    for (const user of testUsers) {
      try {
        const response = await axios.post(`${API_BASE}/auth/login`, {
          username: user.username,
          password: user.password
        });

        if (response.data.token) {
          logSuccess(`Login successful for ${user.username} (${user.role})`);
          if (user.role === 'SUPER_ADMIN') {
            authToken = response.data.token;
          }
        }
      } catch (error) {
        logError(`Login failed for ${user.username}: ${error.data || error.message}`);
      }
    }

    // Test token validation
    if (authToken) {
      try {
        await getAxios().get('/auth/validate');
        logSuccess('Token validation successful');
      } catch (error) {
        logError(`Token validation failed: ${error.message}`);
      }
    }

  } catch (error) {
    logError(`Authentication tests failed: ${error.message}`);
  }
}

// Test Student Features
async function testStudentFeatures() {
  logSection('2. STUDENT FEATURE TESTS');

  // First login as student
  try {
    const loginRes = await axios.post(`${API_BASE}/auth/login`, {
      username: 'nassim',
      password: 'password123'
    });
    const studentToken = loginRes.data.token;
    const studentId = loginRes.data.id;

    const api = axios.create({
      baseURL: API_BASE,
      headers: { Authorization: `Bearer ${studentToken}` }
    });

    // Test enrollments
    try {
      await api.get(`/enrollments/student/${studentId}`);
      logSuccess('Get student enrollments');
    } catch (error) {
      logError(`Get student enrollments: ${error.message}`);
    }

    // Test assignments
    try {
      await api.get(`/assignments/student/${studentId}`);
      logSuccess('Get student assignments');
    } catch (error) {
      logError(`Get student assignments: ${error.message}`);
    }

    // Test grades
    try {
      await api.get(`/grades/student/${studentId}`);
      logSuccess('Get student grades');
    } catch (error) {
      logError(`Get student grades: ${error.message}`);
    }

    // Test study groups
    try {
      await api.get(`/study-groups/user/${studentId}`);
      logSuccess('Get student study groups');
    } catch (error) {
      logError(`Get student study groups: ${error.message}`);
    }

    // Test course search
    try {
      await api.get('/courses/search?query=CS');
      logSuccess('Search courses');
    } catch (error) {
      logError(`Search courses: ${error.message}`);
    }

  } catch (error) {
    logError(`Student login failed: ${error.message}`);
  }
}

// Test Faculty Features
async function testFacultyFeatures() {
  logSection('3. FACULTY FEATURE TESTS');

  // First login as faculty
  try {
    const loginRes = await axios.post(`${API_BASE}/auth/login`, {
      username: 'aya',
      password: 'password123'
    });
    const facultyToken = loginRes.data.token;
    const facultyId = loginRes.data.id;

    const api = axios.create({
      baseURL: API_BASE,
      headers: { Authorization: `Bearer ${facultyToken}` }
    });

    // Test faculty courses
    try {
      await api.get(`/courses/instructor/${facultyId}`);
      logSuccess('Get faculty courses');
    } catch (error) {
      logError(`Get faculty courses: ${error.message}`);
    }

    // Test assignments
    try {
      await api.get(`/assignments/faculty/${facultyId}`);
      logSuccess('Get faculty assignments');
    } catch (error) {
      logError(`Get faculty assignments: ${error.message}`);
    }

    // Test attendance
    try {
      await api.get('/attendance/faculty');
      logSuccess('Get faculty attendance');
    } catch (error) {
      logError(`Get faculty attendance: ${error.message}`);
    }

    // Test grade submission
    try {
      await api.get('/grades/pending');
      logSuccess('Get pending grades');
    } catch (error) {
      logError(`Get pending grades: ${error.message}`);
    }

  } catch (error) {
    logError(`Faculty login failed: ${error.message}`);
  }
}

// Test Admin Features
async function testAdminFeatures() {
  logSection('4. ADMIN FEATURE TESTS');

  const api = getAxios();

  // Dashboard stats
  try {
    await api.get('/dashboard/stats');
    logSuccess('Get dashboard statistics');
  } catch (error) {
    logError(`Get dashboard statistics: ${error.message}`);
  }

  // User management
  try {
    await api.get('/users');
    logSuccess('Get all users');
  } catch (error) {
    logError(`Get all users: ${error.message}`);
  }

  // Course management
  try {
    await api.get('/courses');
    logSuccess('Get all courses');
  } catch (error) {
    logError(`Get all courses: ${error.message}`);
  }

  // Semester management
  try {
    await api.get('/semesters');
    logSuccess('Get all semesters');
  } catch (error) {
    logError(`Get all semesters: ${error.message}`);
  }

  // Reports
  try {
    await api.get('/reports/enrollment-summary');
    logSuccess('Get enrollment summary report');
  } catch (error) {
    logError(`Get enrollment summary report: ${error.message}`);
  }

  // System health
  try {
    await api.get('/system/health');
    logSuccess('Get system health');
  } catch (error) {
    logError(`Get system health: ${error.message}`);
  }
}

// Test Real-time Features
async function testRealtimeFeatures() {
  logSection('5. REAL-TIME FEATURES');

  // WebSocket connection would be tested here
  logInfo('WebSocket testing requires browser environment');
  testResults.skipped.push('WebSocket notifications');
  testResults.skipped.push('Real-time messaging');
}

// Test File Operations
async function testFileOperations() {
  logSection('6. FILE OPERATIONS');

  const api = getAxios();

  // Test file upload (assignments)
  logInfo('File upload testing requires multipart form data');
  testResults.skipped.push('Assignment file upload');
  testResults.skipped.push('Profile picture upload');

  // Test transcript generation
  try {
    const loginRes = await axios.post(`${API_BASE}/auth/login`, {
      username: 'nassim',
      password: 'password123'
    });
    const studentId = loginRes.data.id;

    const studentApi = axios.create({
      baseURL: API_BASE,
      headers: { Authorization: `Bearer ${loginRes.data.token}` }
    });

    await studentApi.get(`/transcripts/student/${studentId}`);
    logSuccess('Generate student transcript');
  } catch (error) {
    logError(`Generate transcript: ${error.message}`);
  }
}

// Test Search and Filter Features
async function testSearchAndFilters() {
  logSection('7. SEARCH AND FILTER TESTS');

  const api = getAxios();

  // Global search
  try {
    await api.get('/search?query=computer');
    logSuccess('Global search functionality');
  } catch (error) {
    logError(`Global search: ${error.message}`);
  }

  // Course filters
  try {
    await api.get('/courses?department=Computer Science&credits=3');
    logSuccess('Course filtering');
  } catch (error) {
    logError(`Course filtering: ${error.message}`);
  }

  // User search
  try {
    await api.get('/users/search?query=nas');
    logSuccess('User search');
  } catch (error) {
    logError(`User search: ${error.message}`);
  }
}

// Generate comprehensive report
function generateReport() {
  logSection('TEST SUMMARY REPORT');

  const total = testResults.passed.length + testResults.failed.length + testResults.skipped.length;
  const passRate = ((testResults.passed.length / total) * 100).toFixed(1);

  console.log(`\n${colors.cyan}Total Tests:${colors.reset} ${total}`);
  console.log(`${colors.green}Passed:${colors.reset} ${testResults.passed.length}`);
  console.log(`${colors.red}Failed:${colors.reset} ${testResults.failed.length}`);
  console.log(`${colors.yellow}Skipped:${colors.reset} ${testResults.skipped.length}`);
  console.log(`${colors.cyan}Pass Rate:${colors.reset} ${passRate}%`);

  if (testResults.failed.length > 0) {
    console.log(`\n${colors.red}Failed Tests:${colors.reset}`);
    testResults.failed.forEach(test => console.log(`  - ${test}`));
  }

  if (testResults.skipped.length > 0) {
    console.log(`\n${colors.yellow}Skipped Tests:${colors.reset}`);
    testResults.skipped.forEach(test => console.log(`  - ${test}`));
  }

  // API Coverage Analysis
  console.log(`\n${colors.cyan}API ENDPOINT COVERAGE:${colors.reset}`);
  console.log('Based on frontend api.js file:');
  console.log('- Total API endpoints defined: ~300+');
  console.log(`- Endpoints tested: ${testResults.passed.length + testResults.failed.length}`);
  console.log(`- Coverage: ~${((testResults.passed.length / 300) * 100).toFixed(1)}%`);

  // Recommendations
  console.log(`\n${colors.yellow}RECOMMENDATIONS:${colors.reset}`);
  if (testResults.failed.length > 0) {
    console.log('1. Fix failed authentication endpoints');
    console.log('2. Ensure all database tables are created');
    console.log('3. Verify BCrypt password hashing');
  }
  console.log('4. Complete WebSocket testing in browser');
  console.log('5. Test file upload operations manually');
  console.log('6. Perform UI testing for visual components');

  // Final Status
  const status = testResults.failed.length === 0 ? 'PASSING' : 'FAILING';
  const statusColor = status === 'PASSING' ? colors.green : colors.red;
  console.log(`\n${statusColor}OVERALL STATUS: ${status}${colors.reset}`);
}

// Main test runner
async function runAllTests() {
  console.log(colors.cyan);
  console.log('╔════════════════════════════════════════════════════════╗');
  console.log('║    SAMS FRONTEND-BACKEND INTEGRATION TEST SUITE       ║');
  console.log('╚════════════════════════════════════════════════════════╝');
  console.log(colors.reset);

  logInfo('Starting comprehensive integration tests...');
  logInfo(`Backend URL: ${API_BASE}`);
  logInfo('Testing with users: superadmin, nassim, aya\n');

  await testAuthentication();
  await testStudentFeatures();
  await testFacultyFeatures();
  await testAdminFeatures();
  await testRealtimeFeatures();
  await testFileOperations();
  await testSearchAndFilters();

  generateReport();
}

// Check if axios is installed
const { exec } = require('child_process');
exec('npm list axios', (error) => {
  if (error) {
    console.log('Installing axios...');
    exec('npm install axios', (err) => {
      if (err) {
        console.error('Failed to install axios. Please run: npm install axios');
        process.exit(1);
      } else {
        runAllTests().catch(console.error);
      }
    });
  } else {
    runAllTests().catch(console.error);
  }
});