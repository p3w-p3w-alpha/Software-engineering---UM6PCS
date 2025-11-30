/**
 * Frontend-Backend Integration Test Script
 * 
 * This script tests all frontend API calls against the backend
 * Run with: node test-frontend-backend-integration.js
 * 
 * Prerequisites:
 * 1. Backend must be running on http://localhost:8080
 * 2. Frontend API service must be accessible
 */

const axios = require('axios');

const API_BASE_URL = 'http://localhost:8080/api';
let authToken = null;
let testUserId = null;
let testCourseId = null;
let testEnrollmentId = null;

// Test results
const results = {
  passed: 0,
  failed: 0,
  errors: []
};

// Helper function to make API calls
async function apiCall(method, endpoint, data = null, params = null) {
  try {
    const config = {
      method,
      url: `${API_BASE_URL}${endpoint}`,
      headers: {
        'Content-Type': 'application/json',
        ...(authToken && { 'Authorization': `Bearer ${authToken}` })
      }
    };

    if (data) {
      config.data = data;
    }

    if (params) {
      config.params = params;
    }

    const response = await axios(config);
    return { success: true, data: response.data, status: response.status };
  } catch (error) {
    return {
      success: false,
      error: error.response?.data || error.message,
      status: error.response?.status
    };
  }
}

// Test function
async function test(name, testFn) {
  try {
    console.log(`\n🧪 Testing: ${name}`);
    const result = await testFn();
    if (result.success) {
      console.log(`✅ PASSED: ${name}`);
      results.passed++;
      return result;
    } else {
      console.log(`❌ FAILED: ${name}`);
      console.log(`   Error: ${JSON.stringify(result.error)}`);
      results.failed++;
      results.errors.push({ test: name, error: result.error });
      return result;
    }
  } catch (error) {
    console.log(`❌ FAILED: ${name}`);
    console.log(`   Exception: ${error.message}`);
    results.failed++;
    results.errors.push({ test: name, error: error.message });
    return { success: false, error: error.message };
  }
}

// ============================================
// AUTHENTICATION TESTS
// ============================================

async function testAuthentication() {
  console.log('\n' + '='.repeat(60));
  console.log('AUTHENTICATION TESTS');
  console.log('='.repeat(60));

  // Test login
  await test('POST /api/auth/login', async () => {
    const result = await apiCall('POST', '/auth/login', {
      username: 'admin',
      password: 'admin123'
    });
    
    if (result.success && result.data.token) {
      authToken = result.data.token;
      testUserId = result.data.user?.id || 1;
    }
    
    return result;
  });

  // Test token validation
  await test('GET /api/auth/validate', async () => {
    return await apiCall('GET', '/auth/validate');
  });
}

// ============================================
// USER MANAGEMENT TESTS
// ============================================

async function testUserManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('USER MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/users', async () => {
    return await apiCall('GET', '/users');
  });

  await test('GET /api/users/{id}', async () => {
    return await apiCall('GET', `/users/${testUserId || 1}`);
  });

  await test('GET /api/users/email/{email}', async () => {
    return await apiCall('GET', '/users/email/admin@example.com');
  });

  await test('GET /api/users/role/{role}', async () => {
    return await apiCall('GET', '/users/role/STUDENT');
  });
}

// ============================================
// ADMIN USER MANAGEMENT TESTS
// ============================================

async function testAdminUserManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('ADMIN USER MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/admin/users', async () => {
    return await apiCall('GET', '/admin/users');
  });

  await test('GET /api/admin/users/{id}', async () => {
    return await apiCall('GET', `/admin/users/${testUserId || 1}`);
  });

  await test('GET /api/admin/users/permissions', async () => {
    return await apiCall('GET', '/admin/users/permissions');
  });
}

// ============================================
// COURSE MANAGEMENT TESTS
// ============================================

async function testCourseManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('COURSE MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/courses', async () => {
    const result = await apiCall('GET', '/courses');
    if (result.success && result.data && result.data.length > 0) {
      testCourseId = result.data[0].id;
    }
    return result;
  });

  await test('GET /api/courses/{id}', async () => {
    return await apiCall('GET', `/courses/${testCourseId || 1}`);
  });

  await test('GET /api/courses/code/{courseCode}', async () => {
    return await apiCall('GET', '/courses/code/CS101');
  });

  await test('GET /api/courses/instructor/{instructorId}', async () => {
    return await apiCall('GET', '/courses/instructor/1');
  });

  await test('GET /api/courses/search/name?query=...', async () => {
    return await apiCall('GET', '/courses/search/name', null, { query: 'Computer' });
  });

  await test('GET /api/courses/search/code?query=...', async () => {
    return await apiCall('GET', '/courses/search/code', null, { query: 'CS' });
  });

  await test('GET /api/courses/credits/{credits}', async () => {
    return await apiCall('GET', '/courses/credits/3');
  });

  await test('GET /api/courses/{courseId}/prerequisites', async () => {
    return await apiCall('GET', `/courses/${testCourseId || 1}/prerequisites`);
  });

  await test('GET /api/courses/{courseId}/waitlist', async () => {
    return await apiCall('GET', `/courses/${testCourseId || 1}/waitlist`);
  });
}

// ============================================
// ENROLLMENT MANAGEMENT TESTS
// ============================================

async function testEnrollmentManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('ENROLLMENT MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/enrollments', async () => {
    return await apiCall('GET', '/enrollments');
  });

  await test('GET /api/enrollments/student/{studentId}', async () => {
    const result = await apiCall('GET', `/enrollments/student/${testUserId || 1}`);
    if (result.success && result.data && result.data.length > 0) {
      testEnrollmentId = result.data[0].id;
    }
    return result;
  });

  await test('GET /api/enrollments/course/{courseId}', async () => {
    return await apiCall('GET', `/enrollments/course/${testCourseId || 1}`);
  });

  await test('GET /api/enrollments/status/{status}', async () => {
    return await apiCall('GET', '/enrollments/status/ACTIVE');
  });

  await test('GET /api/enrollments/course/{courseId}/count', async () => {
    return await apiCall('GET', `/enrollments/course/${testCourseId || 1}/count`);
  });

  await test('GET /api/enrollments/check?studentId=...&courseId=...', async () => {
    return await apiCall('GET', '/enrollments/check', null, {
      studentId: testUserId || 1,
      courseId: testCourseId || 1
    });
  });
}

// ============================================
// ASSIGNMENT MANAGEMENT TESTS
// ============================================

async function testAssignmentManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('ASSIGNMENT MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/assignments', async () => {
    return await apiCall('GET', '/assignments');
  });

  await test('GET /api/assignments/student/{studentId}', async () => {
    return await apiCall('GET', `/assignments/student/${testUserId || 1}`);
  });

  await test('GET /api/assignments/course/{courseId}', async () => {
    return await apiCall('GET', `/assignments/course/${testCourseId || 1}`);
  });

  await test('GET /api/assignments/faculty/{facultyId}', async () => {
    return await apiCall('GET', '/assignments/faculty/1');
  });

  await test('GET /api/assignments/upcoming', async () => {
    return await apiCall('GET', '/assignments/upcoming');
  });

  await test('GET /api/assignments/overdue', async () => {
    return await apiCall('GET', '/assignments/overdue');
  });
}

// ============================================
// GRADE MANAGEMENT TESTS
// ============================================

async function testGradeManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('GRADE MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/grades', async () => {
    return await apiCall('GET', '/grades');
  });

  await test('GET /api/grades/student/{studentId}', async () => {
    return await apiCall('GET', `/grades/student/${testUserId || 1}`);
  });

  await test('GET /api/grades/course/{courseId}', async () => {
    return await apiCall('GET', `/grades/course/${testCourseId || 1}`);
  });

  await test('GET /api/grades/student/{studentId}/gpa', async () => {
    return await apiCall('GET', `/grades/student/${testUserId || 1}/gpa`);
  });

  await test('GET /api/grades/student/{studentId}/summary', async () => {
    return await apiCall('GET', `/grades/student/${testUserId || 1}/summary`);
  });

  await test('GET /api/grades/scale', async () => {
    return await apiCall('GET', '/grades/scale');
  });
}

// ============================================
// PAYMENT MANAGEMENT TESTS
// ============================================

async function testPaymentManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('PAYMENT MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/payments', async () => {
    return await apiCall('GET', '/payments');
  });

  await test('GET /api/payments/student/{studentId}', async () => {
    return await apiCall('GET', `/payments/student/${testUserId || 1}`);
  });

  await test('GET /api/payments/pending-approval', async () => {
    return await apiCall('GET', '/payments/pending-approval');
  });
}

// ============================================
// ATTENDANCE MANAGEMENT TESTS
// ============================================

async function testAttendanceManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('ATTENDANCE MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/attendance/user/{userId}', async () => {
    return await apiCall('GET', `/attendance/user/${testUserId || 1}`);
  });

  await test('GET /api/attendance/date/{date}', async () => {
    const today = new Date().toISOString().split('T')[0];
    return await apiCall('GET', `/attendance/date/${today}`);
  });
}

// ============================================
// DASHBOARD TESTS
// ============================================

async function testDashboard() {
  console.log('\n' + '='.repeat(60));
  console.log('DASHBOARD TESTS');
  console.log('='.repeat(60));

  await test('GET /api/dashboard/complete', async () => {
    return await apiCall('GET', '/dashboard/complete');
  });

  await test('GET /api/dashboard/stats', async () => {
    return await apiCall('GET', '/dashboard/stats');
  });

  await test('GET /api/dashboard/enrollment-trends', async () => {
    return await apiCall('GET', '/dashboard/enrollment-trends', null, { months: 6 });
  });
}

// ============================================
// SEMESTER MANAGEMENT TESTS
// ============================================

async function testSemesterManagement() {
  console.log('\n' + '='.repeat(60));
  console.log('SEMESTER MANAGEMENT TESTS');
  console.log('='.repeat(60));

  await test('GET /api/semesters', async () => {
    return await apiCall('GET', '/semesters');
  });

  await test('GET /api/semesters/current', async () => {
    return await apiCall('GET', '/semesters/current');
  });
}

// ============================================
// MAIN TEST RUNNER
// ============================================

async function runAllTests() {
  console.log('\n' + '='.repeat(60));
  console.log('FRONTEND-BACKEND INTEGRATION TEST SUITE');
  console.log('='.repeat(60));
  console.log(`Backend URL: ${API_BASE_URL}`);
  console.log(`Test started at: ${new Date().toISOString()}`);

  try {
    // Run all test suites
    await testAuthentication();
    await testUserManagement();
    await testAdminUserManagement();
    await testCourseManagement();
    await testEnrollmentManagement();
    await testAssignmentManagement();
    await testGradeManagement();
    await testPaymentManagement();
    await testAttendanceManagement();
    await testDashboard();
    await testSemesterManagement();

    // Print summary
    console.log('\n' + '='.repeat(60));
    console.log('TEST SUMMARY');
    console.log('='.repeat(60));
    console.log(`✅ Passed: ${results.passed}`);
    console.log(`❌ Failed: ${results.failed}`);
    console.log(`📊 Total: ${results.passed + results.failed}`);
    console.log(`📈 Success Rate: ${((results.passed / (results.passed + results.failed)) * 100).toFixed(2)}%`);

    if (results.errors.length > 0) {
      console.log('\n' + '='.repeat(60));
      console.log('ERRORS');
      console.log('='.repeat(60));
      results.errors.forEach((err, index) => {
        console.log(`\n${index + 1}. ${err.test}`);
        console.log(`   ${JSON.stringify(err.error, null, 2)}`);
      });
    }

    console.log('\n' + '='.repeat(60));
    console.log(`Test completed at: ${new Date().toISOString()}`);
    console.log('='.repeat(60));

    // Exit with appropriate code
    process.exit(results.failed > 0 ? 1 : 0);
  } catch (error) {
    console.error('\n❌ Fatal error during testing:', error);
    process.exit(1);
  }
}

// Check if backend is accessible
async function checkBackend() {
  try {
    // Try a simple API endpoint (may return 403 which is fine - means server is running)
    try {
      await axios.get(`${API_BASE_URL}/courses`, { 
        timeout: 5000,
        validateStatus: function (status) {
          return status < 500; // Accept any status < 500 (including 403)
        }
      });
      console.log('✅ Backend is accessible');
      return true;
    } catch (err) {
      // If we get a connection error, backend is not running
      if (err.code === 'ECONNREFUSED' || err.code === 'ETIMEDOUT') {
        console.error('❌ Backend is not accessible. Please ensure the backend is running on http://localhost:8080');
        return false;
      }
      // Other errors (like 403) mean backend is running
      console.log('✅ Backend is accessible (authentication may be required)');
      return true;
    }
  } catch (error) {
    console.error('❌ Backend is not accessible. Please ensure the backend is running on http://localhost:8080');
    return false;
  }
}

// Start tests
(async () => {
  const backendAvailable = await checkBackend();
  if (backendAvailable) {
    await runAllTests();
  } else {
    process.exit(1);
  }
})();

