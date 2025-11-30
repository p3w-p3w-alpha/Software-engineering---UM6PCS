/**
 * COMPREHENSIVE FRONTEND-BACKEND INTEGRATION TEST
 * Tests all major functionality categories and their communication with backend
 */

const axios = require('axios');

const API_BASE_URL = 'http://localhost:8080/api';
const FRONTEND_URL = 'http://localhost:5173';

let authToken = '';
let testUser = {
    id: null,
    username: 'superadmin',
    password: 'Admin@123',
    role: null
};

// Color codes for console output
const colors = {
    reset: '\x1b[0m',
    green: '\x1b[32m',
    red: '\x1b[31m',
    yellow: '\x1b[33m',
    blue: '\x1b[34m',
    magenta: '\x1b[35m'
};

function log(message, color = 'reset') {
    console.log(`${colors[color]}${message}${colors.reset}`);
}

function logSuccess(message) {
    log(`✅ ${message}`, 'green');
}

function logError(message) {
    log(`❌ ${message}`, 'red');
}

function logInfo(message) {
    log(`ℹ️  ${message}`, 'blue');
}

function logWarning(message) {
    log(`⚠️  ${message}`, 'yellow');
}

function logSection(message) {
    log(`\n${'='.repeat(80)}`, 'magenta');
    log(`  ${message}`, 'magenta');
    log('='.repeat(80), 'magenta');
}

// Create axios instance
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add auth token to requests
api.interceptors.request.use((config) => {
    if (authToken) {
        config.headers.Authorization = `Bearer ${authToken}`;
    }
    return config;
});

// Test results tracker
const testResults = {
    total: 0,
    passed: 0,
    failed: 0,
    categories: {}
};

function recordTest(category, testName, passed, error = null) {
    testResults.total++;
    if (passed) {
        testResults.passed++;
        logSuccess(`${category} - ${testName}`);
    } else {
        testResults.failed++;
        logError(`${category} - ${testName}: ${error?.message || 'Unknown error'}`);
    }

    if (!testResults.categories[category]) {
        testResults.categories[category] = { passed: 0, failed: 0 };
    }
    if (passed) {
        testResults.categories[category].passed++;
    } else {
        testResults.categories[category].failed++;
    }
}

// Helper function to test an endpoint
async function testEndpoint(category, name, apiCall) {
    try {
        const response = await apiCall();
        recordTest(category, name, true);
        return response.data;
    } catch (error) {
        recordTest(category, name, false, error);
        return null;
    }
}

// =============================================================================
// TEST CATEGORIES
// =============================================================================

async function testAuthentication() {
    logSection('1. AUTHENTICATION');

    try {
        const response = await api.post('/auth/login', {
            username: testUser.username,
            password: testUser.password
        });

        if (response.data && response.data.token) {
            authToken = response.data.token;
            // Backend returns userId directly, not user.id
            testUser.id = response.data.userId;
            testUser.role = response.data.role;
            recordTest('Authentication', 'Login with credentials', true);
            logInfo(`Logged in as: ${testUser.username} (ID: ${testUser.id}, Role: ${testUser.role})`);
        } else {
            recordTest('Authentication', 'Login with credentials', false, new Error('No token received'));
        }
    } catch (error) {
        recordTest('Authentication', 'Login with credentials', false, error);
        logError('Authentication failed - cannot continue tests');
        logError(`Error details: ${error.response?.data?.message || error.message}`);
        process.exit(1);
    }

    // Test token validation
    await testEndpoint('Authentication', 'Token validation', () =>
        api.get('/auth/validate', {
            headers: { Authorization: `Bearer ${authToken}` }
        })
    );
}

async function testUserManagement() {
    logSection('2. USER MANAGEMENT');

    await testEndpoint('User Management', 'Get all users', () =>
        api.get('/admin/users')
    );

    await testEndpoint('User Management', 'Get user by ID', () =>
        api.get(`/admin/users/${testUser.id}`)
    );

    await testEndpoint('User Management', 'Get available permissions', () =>
        api.get('/admin/users/permissions')
    );

    await testEndpoint('User Management', 'Get users by role', () =>
        api.get('/users/role/STUDENT')
    );
}

async function testCourseManagement() {
    logSection('3. COURSE MANAGEMENT');

    const courses = await testEndpoint('Course Management', 'Get all courses', () =>
        api.get('/courses')
    );

    if (courses && courses.length > 0) {
        const courseId = courses[0].id;

        await testEndpoint('Course Management', 'Get course by ID', () =>
            api.get(`/courses/${courseId}`)
        );

        await testEndpoint('Course Management', 'Get course enrollments', () =>
            api.get(`/enrollments/course/${courseId}`)
        );

        await testEndpoint('Course Management', 'Get course prerequisites', () =>
            api.get(`/courses/${courseId}/prerequisites`)
        );

        await testEndpoint('Course Management', 'Search courses by name', () =>
            api.get('/courses/search/name?query=Computer')
        );
    }
}

async function testEnrollmentManagement() {
    logSection('4. ENROLLMENT MANAGEMENT');

    if (testUser.id) {
        await testEndpoint('Enrollment Management', 'Get student enrollments', () =>
            api.get(`/enrollments/student/${testUser.id}`)
        );
    }

    await testEndpoint('Enrollment Management', 'Get enrollments by status', () =>
        api.get('/enrollments/status/ENROLLED')
    );
}

async function testAssignmentManagement() {
    logSection('5. ASSIGNMENT MANAGEMENT');

    const assignments = await testEndpoint('Assignment Management', 'Get all assignments', () =>
        api.get('/assignments')
    );

    if (testUser.role === 'STUDENT' && testUser.id) {
        await testEndpoint('Assignment Management', 'Get student assignments', () =>
            api.get(`/assignments/student/${testUser.id}`)
        );
    }

    await testEndpoint('Assignment Management', 'Get upcoming assignments', () =>
        api.get('/assignments/upcoming')
    );

    await testEndpoint('Assignment Management', 'Get overdue assignments', () =>
        api.get('/assignments/overdue')
    );

    if (assignments && assignments.length > 0) {
        const assignmentId = assignments[0].id;

        await testEndpoint('Assignment Management', 'Get assignment by ID', () =>
            api.get(`/assignments/${assignmentId}`)
        );

        await testEndpoint('Assignment Management', 'Get assignment submissions', () =>
            api.get(`/submissions/assignment/${assignmentId}`)
        );
    }
}

async function testSubmissionManagement() {
    logSection('6. SUBMISSION MANAGEMENT');

    if (testUser.role === 'STUDENT' && testUser.id) {
        const submissions = await testEndpoint('Submission Management', 'Get student submissions', () =>
            api.get(`/submissions/student/${testUser.id}`)
        );

        if (submissions && submissions.length > 0) {
            const submissionId = submissions[0].id;

            await testEndpoint('Submission Management', 'Get submission by ID', () =>
                api.get(`/submissions/${submissionId}`)
            );
        }
    }

    await testEndpoint('Submission Management', 'Get submissions by status', () =>
        api.get('/submissions/status/SUBMITTED')
    );
}

async function testGradeManagement() {
    logSection('7. GRADE MANAGEMENT');

    if (testUser.id) {
        await testEndpoint('Grade Management', 'Get student grades', () =>
            api.get(`/grades/student/${testUser.id}`)
        );

        await testEndpoint('Grade Management', 'Calculate student GPA', () =>
            api.get(`/grades/student/${testUser.id}/gpa`)
        );

        await testEndpoint('Grade Management', 'Get student GPA summary', () =>
            api.get(`/grades/student/${testUser.id}/summary`)
        );
    }

    await testEndpoint('Grade Management', 'Get grade scale', () =>
        api.get('/grades/scale')
    );

    await testEndpoint('Grade Management', 'Get all grades', () =>
        api.get('/grades')
    );
}

async function testAttendanceManagement() {
    logSection('8. ATTENDANCE MANAGEMENT');

    const today = new Date().toISOString().split('T')[0];

    await testEndpoint('Attendance Management', 'Get attendance by date', () =>
        api.get(`/attendance/date/${today}`)
    );

    if (testUser.id) {
        await testEndpoint('Attendance Management', 'Get user attendance', () =>
            api.get(`/attendance/user/${testUser.id}`)
        );
    }

    const startDate = new Date();
    startDate.setDate(startDate.getDate() - 30);
    const formattedStartDate = startDate.toISOString().split('T')[0];

    await testEndpoint('Attendance Management', 'Get attendance statistics', () =>
        api.get(`/attendance/statistics/range?startDate=${formattedStartDate}&endDate=${today}`)
    );
}

async function testPaymentManagement() {
    logSection('9. PAYMENT MANAGEMENT');

    await testEndpoint('Payment Management', 'Get all payments', () =>
        api.get('/payments')
    );

    if (testUser.role === 'STUDENT' && testUser.id) {
        await testEndpoint('Payment Management', 'Get student payments', () =>
            api.get(`/payments/student/${testUser.id}`)
        );
    }

    await testEndpoint('Payment Management', 'Get pending approval payments', () =>
        api.get('/payments/pending-approval')
    );

    await testEndpoint('Payment Management', 'Get overdue payments', () =>
        api.get('/payments/overdue')
    );
}

async function testFeeManagement() {
    logSection('10. FEE MANAGEMENT');

    await testEndpoint('Fee Management', 'Get all fee structures', () =>
        api.get('/fees/structures')
    );

    await testEndpoint('Fee Management', 'Get active fee structures', () =>
        api.get('/fees/structures/active')
    );

    if (testUser.id) {
        await testEndpoint('Fee Management', 'Get student fee items', () =>
            api.get(`/fees/items/student/${testUser.id}`)
        );
    }
}

async function testNotificationManagement() {
    logSection('11. NOTIFICATION MANAGEMENT');

    if (testUser.id) {
        await testEndpoint('Notification Management', 'Get user notifications', () =>
            api.get(`/notifications?userId=${testUser.id}&page=0&size=10`)
        );

        await testEndpoint('Notification Management', 'Get unread notification count', () =>
            api.get(`/notifications/unread-count?userId=${testUser.id}`)
        );

        await testEndpoint('Notification Management', 'Get unread notifications', () =>
            api.get(`/notifications/unread?userId=${testUser.id}`)
        );
    }
}

async function testMessagingSystem() {
    logSection('12. MESSAGING SYSTEM');

    if (testUser.id) {
        await testEndpoint('Messaging', 'Get user conversations', () =>
            api.get(`/messages/user/${testUser.id}/conversations`)
        );

        await testEndpoint('Messaging', 'Get unread message count', () =>
            api.get(`/messages/user/${testUser.id}/unread-count`)
        );

        await testEndpoint('Messaging', 'Get unread messages', () =>
            api.get(`/messages/user/${testUser.id}/unread`)
        );
    }
}

async function testStudyGroups() {
    logSection('13. STUDY GROUPS');

    const groups = await testEndpoint('Study Groups', 'Get all study groups', () =>
        api.get('/study-groups')
    );

    await testEndpoint('Study Groups', 'Get public study groups', () =>
        api.get('/study-groups/public')
    );

    if (testUser.id) {
        await testEndpoint('Study Groups', 'Get user study groups', () =>
            api.get(`/study-groups/user/${testUser.id}/groups`)
        );
    }

    if (groups && groups.length > 0) {
        const groupId = groups[0].id;

        await testEndpoint('Study Groups', 'Get study group by ID', () =>
            api.get(`/study-groups/${groupId}`)
        );

        await testEndpoint('Study Groups', 'Get study group members', () =>
            api.get(`/study-groups/${groupId}/members`)
        );

        if (testUser.id) {
            await testEndpoint('Study Groups', 'Get study group messages', () =>
                api.get(`/study-groups/${groupId}/messages?userId=${testUser.id}&page=0&size=50`)
            );
        }
    }
}

async function testConnectionManagement() {
    logSection('14. CONNECTION MANAGEMENT');

    if (testUser.id) {
        await testEndpoint('Connection Management', 'Get user connections', () =>
            api.get(`/connections/user/${testUser.id}`)
        );

        await testEndpoint('Connection Management', 'Get connection count', () =>
            api.get(`/connections/user/${testUser.id}/count`)
        );

        await testEndpoint('Connection Management', 'Get sent connection requests', () =>
            api.get(`/connections/user/${testUser.id}/sent`)
        );

        await testEndpoint('Connection Management', 'Get received connection requests', () =>
            api.get(`/connections/user/${testUser.id}/received`)
        );

        await testEndpoint('Connection Management', 'Get blocked users', () =>
            api.get(`/connections/user/${testUser.id}/blocked`)
        );
    }
}

async function testDegreeProgress() {
    logSection('15. DEGREE PROGRESS');

    await testEndpoint('Degree Progress', 'Get all degree programs', () =>
        api.get('/degree-progress/programs')
    );

    await testEndpoint('Degree Progress', 'Get active degree programs', () =>
        api.get('/degree-progress/programs/active')
    );

    if (testUser.role === 'STUDENT' && testUser.id) {
        await testEndpoint('Degree Progress', 'Get student progress', () =>
            api.get(`/degree-progress/students/${testUser.id}/progress`)
        );

        await testEndpoint('Degree Progress', 'Check graduation eligibility', () =>
            api.get(`/degree-progress/students/${testUser.id}/graduation-eligible`)
        );
    }

    await testEndpoint('Degree Progress', 'Get eligible for graduation', () =>
        api.get('/degree-progress/eligible-graduation')
    );

    await testEndpoint('Degree Progress', 'Get students at risk', () =>
        api.get('/degree-progress/at-risk')
    );
}

async function testTeacherManagement() {
    logSection('16. TEACHER MANAGEMENT');

    await testEndpoint('Teacher Management', 'Get all teacher profiles', () =>
        api.get('/teachers/profiles')
    );

    await testEndpoint('Teacher Management', 'Get active teacher profiles', () =>
        api.get('/teachers/profiles/active')
    );

    await testEndpoint('Teacher Management', 'Get top rated teachers', () =>
        api.get('/teachers/profiles/top-rated')
    );

    const teachers = await testEndpoint('Teacher Management', 'Search teachers', () =>
        api.get('/teachers/profiles/search?query=')
    );

    if (teachers && teachers.length > 0) {
        const teacherId = teachers[0].id;

        await testEndpoint('Teacher Management', 'Get teacher profile', () =>
            api.get(`/teachers/profiles/${teacherId}`)
        );

        await testEndpoint('Teacher Management', 'Get teacher office hours', () =>
            api.get(`/teachers/office-hours/teacher/${teacherId}`)
        );

        await testEndpoint('Teacher Management', 'Get teacher statistics', () =>
            api.get(`/teachers/statistics/${teacherId}`)
        );

        await testEndpoint('Teacher Management', 'Get teacher schedule', () =>
            api.get(`/teachers/schedule/${teacherId}`)
        );
    }
}

async function testDashboardAnalytics() {
    logSection('17. DASHBOARD ANALYTICS');

    await testEndpoint('Dashboard Analytics', 'Get complete dashboard', () =>
        api.get('/dashboard/complete')
    );

    await testEndpoint('Dashboard Analytics', 'Get dashboard stats', () =>
        api.get('/dashboard/stats')
    );

    await testEndpoint('Dashboard Analytics', 'Get enrollment trends', () =>
        api.get('/dashboard/enrollment-trends?months=6')
    );

    await testEndpoint('Dashboard Analytics', 'Get grade distribution', () =>
        api.get('/dashboard/grade-distribution')
    );

    await testEndpoint('Dashboard Analytics', 'Get financial summary', () =>
        api.get('/dashboard/financial-summary')
    );

    await testEndpoint('Dashboard Analytics', 'Get gender demographics', () =>
        api.get('/dashboard/gender-demographics')
    );

    await testEndpoint('Dashboard Analytics', 'Get recent activities', () =>
        api.get('/dashboard/recent-activities?limit=10')
    );
}

async function testSemesterManagement() {
    logSection('18. SEMESTER MANAGEMENT');

    await testEndpoint('Semester Management', 'Get all semesters', () =>
        api.get('/semesters')
    );

    await testEndpoint('Semester Management', 'Get current semester', () =>
        api.get('/semesters/current')
    );

    const semesters = await testEndpoint('Semester Management', 'Get active semester', () =>
        api.get('/semesters/current')
    );

    if (semesters) {
        const semesterId = semesters.id;

        await testEndpoint('Semester Management', 'Get semester by ID', () =>
            api.get(`/semesters/${semesterId}`)
        );
    }
}

async function testFileManagement() {
    logSection('19. FILE MANAGEMENT');

    // These are mostly helper endpoints
    await testEndpoint('File Management', 'Check file exists (test)', () =>
        api.get('/files/exists?filePath=test.txt')
    );
}

async function testFrontendConnectivity() {
    logSection('20. FRONTEND CONNECTIVITY');

    try {
        const response = await axios.get(FRONTEND_URL);
        if (response.status === 200) {
            recordTest('Frontend', 'Frontend server is accessible', true);
        } else {
            recordTest('Frontend', 'Frontend server is accessible', false, new Error(`Status: ${response.status}`));
        }
    } catch (error) {
        recordTest('Frontend', 'Frontend server is accessible', false, error);
    }
}

// =============================================================================
// MAIN TEST RUNNER
// =============================================================================

async function runAllTests() {
    logSection('🔬 COMPREHENSIVE FRONTEND-BACKEND INTEGRATION TEST');
    logInfo('Testing all major functionality categories...\n');

    try {
        await testAuthentication();
        await testUserManagement();
        await testCourseManagement();
        await testEnrollmentManagement();
        await testAssignmentManagement();
        await testSubmissionManagement();
        await testGradeManagement();
        await testAttendanceManagement();
        await testPaymentManagement();
        await testFeeManagement();
        await testNotificationManagement();
        await testMessagingSystem();
        await testStudyGroups();
        await testConnectionManagement();
        await testDegreeProgress();
        await testTeacherManagement();
        await testDashboardAnalytics();
        await testSemesterManagement();
        await testFileManagement();
        await testFrontendConnectivity();

        // Print final results
        printTestResults();
    } catch (error) {
        logError(`Fatal error during testing: ${error.message}`);
        console.error(error);
        process.exit(1);
    }
}

function printTestResults() {
    logSection('📊 TEST RESULTS SUMMARY');

    console.log('\nCategory Breakdown:');
    console.log('─'.repeat(80));

    for (const [category, results] of Object.entries(testResults.categories)) {
        const total = results.passed + results.failed;
        const percentage = total > 0 ? ((results.passed / total) * 100).toFixed(1) : 0;
        const status = results.failed === 0 ? '✅' : '⚠️';

        log(`${status} ${category}:`, results.failed === 0 ? 'green' : 'yellow');
        log(`   Passed: ${results.passed}/${total} (${percentage}%)`, results.failed === 0 ? 'green' : 'yellow');
        if (results.failed > 0) {
            log(`   Failed: ${results.failed}`, 'red');
        }
    }

    console.log('\n' + '='.repeat(80));

    const overallPercentage = ((testResults.passed / testResults.total) * 100).toFixed(1);

    log(`\n📈 OVERALL RESULTS:`, 'magenta');
    log(`   Total Tests: ${testResults.total}`, 'blue');
    log(`   Passed: ${testResults.passed}`, 'green');
    log(`   Failed: ${testResults.failed}`, testResults.failed > 0 ? 'red' : 'green');
    log(`   Success Rate: ${overallPercentage}%`, overallPercentage >= 95 ? 'green' : 'yellow');

    if (testResults.failed === 0) {
        log('\n🎉 ALL TESTS PASSED! Frontend-Backend integration is working perfectly!', 'green');
    } else if (overallPercentage >= 90) {
        log('\n✅ Most tests passed! Frontend-Backend integration is mostly functional.', 'yellow');
    } else {
        log('\n⚠️  Significant issues detected. Please review failed tests.', 'red');
    }

    console.log('\n' + '='.repeat(80) + '\n');
}

// Run the tests
runAllTests().catch(error => {
    logError(`Unhandled error: ${error.message}`);
    console.error(error);
    process.exit(1);
});
