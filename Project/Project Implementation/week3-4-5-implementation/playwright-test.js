const { chromium } = require('playwright');

const BASE_URL = 'http://localhost:5173';
const issues = [];

async function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function logIssue(category, description, severity = 'medium') {
  issues.push({ category, description, severity });
  console.log(`[ISSUE - ${severity.toUpperCase()}] ${category}: ${description}`);
}

async function logSuccess(category, description) {
  console.log(`[OK] ${category}: ${description}`);
}

async function testAdminPortal(page) {
  console.log('\n========== TESTING ADMIN PORTAL ==========\n');

  // Login as admin
  await page.goto(`${BASE_URL}/login`);
  await delay(1000);

  try {
    await page.fill('input[type="text"], input[name="username"], input#username', 'superadmin');
    await page.fill('input[type="password"], input[name="password"], input#password', 'Admin@123');
    await page.click('button[type="submit"]');
    await delay(2000);

    const currentUrl = page.url();
    if (currentUrl.includes('dashboard') || currentUrl.includes('admin')) {
      logSuccess('Admin Login', 'Successfully logged in as admin');
    } else {
      logIssue('Admin Login', 'Login did not redirect to dashboard', 'high');
    }
  } catch (e) {
    logIssue('Admin Login', `Login failed: ${e.message}`, 'high');
    return;
  }

  // Test Dashboard
  console.log('\n--- Testing Admin Dashboard ---');
  try {
    await page.goto(`${BASE_URL}/admin/dashboard`);
    await delay(1500);

    const dashboardContent = await page.content();
    if (dashboardContent.includes('Dashboard') || dashboardContent.includes('Statistics')) {
      logSuccess('Admin Dashboard', 'Dashboard page loaded');
    }

    // Check for analytics cards
    const statsCards = await page.$$('.stat-card, .analytics-card, .dashboard-card, [class*="card"]');
    if (statsCards.length > 0) {
      logSuccess('Admin Dashboard', `Found ${statsCards.length} dashboard cards`);
    } else {
      logIssue('Admin Dashboard', 'No statistics cards found on dashboard', 'medium');
    }
  } catch (e) {
    logIssue('Admin Dashboard', `Dashboard error: ${e.message}`, 'medium');
  }

  // Test User Management
  console.log('\n--- Testing User Management ---');
  try {
    await page.goto(`${BASE_URL}/admin/users`);
    await delay(1500);

    const userTable = await page.$('table, [class*="table"], [class*="user-list"]');
    if (userTable) {
      logSuccess('User Management', 'User list table found');
    }

    // Check for user data
    const userRows = await page.$$('table tbody tr, [class*="user-item"]');
    if (userRows.length > 0) {
      logSuccess('User Management', `Found ${userRows.length} users in the list`);
    } else {
      logIssue('User Management', 'No users displayed in user management', 'medium');
    }

    // Check for Add User button
    const addUserBtn = await page.$('button:has-text("Add"), button:has-text("Create"), [class*="add-user"]');
    if (addUserBtn) {
      logSuccess('User Management', 'Add user button available');
    }
  } catch (e) {
    logIssue('User Management', `User management error: ${e.message}`, 'medium');
  }

  // Test Course Management
  console.log('\n--- Testing Course Management ---');
  try {
    await page.goto(`${BASE_URL}/admin/courses`);
    await delay(1500);

    const courseTable = await page.$('table, [class*="table"], [class*="course-list"]');
    if (courseTable) {
      logSuccess('Course Management', 'Course list displayed');
    }

    const courseRows = await page.$$('table tbody tr');
    console.log(`  Found ${courseRows.length} courses`);

    // Test instructor dropdown
    const addBtn = await page.$('button:has-text("Add"), button:has-text("Create")');
    if (addBtn) {
      await addBtn.click();
      await delay(500);

      const instructorSelect = await page.$('select[name*="instructor"], select#instructor, [class*="instructor"] select');
      if (instructorSelect) {
        logSuccess('Course Management', 'Instructor dropdown available in form');
      } else {
        logIssue('Course Management', 'Instructor dropdown not found in course form', 'medium');
      }

      // Close modal
      const closeBtn = await page.$('button:has-text("Cancel"), button:has-text("Close"), [class*="close"]');
      if (closeBtn) await closeBtn.click();
    }
  } catch (e) {
    logIssue('Course Management', `Course management error: ${e.message}`, 'medium');
  }

  // Test Attendance Management
  console.log('\n--- Testing Attendance Management ---');
  try {
    await page.goto(`${BASE_URL}/admin/attendance`);
    await delay(1500);

    const pageContent = await page.content();
    if (pageContent.includes('Attendance') || pageContent.includes('attendance')) {
      logSuccess('Attendance Management', 'Attendance page loaded');
    }

    // Check for course filter
    const courseFilter = await page.$('select, [class*="filter"]');
    if (courseFilter) {
      logSuccess('Attendance Management', 'Course filter available');
    }
  } catch (e) {
    logIssue('Attendance Management', `Attendance error: ${e.message}`, 'medium');
  }

  // Test Payment Approval
  console.log('\n--- Testing Payment Management ---');
  try {
    await page.goto(`${BASE_URL}/admin/payments`);
    await delay(1500);

    const paymentTable = await page.$('table, [class*="payment"]');
    if (paymentTable) {
      logSuccess('Payment Management', 'Payment list displayed');
    }

    // Check for approve/reject buttons
    const actionBtns = await page.$$('button:has-text("Approve"), button:has-text("Reject")');
    console.log(`  Found ${actionBtns.length} action buttons`);
  } catch (e) {
    logIssue('Payment Management', `Payment error: ${e.message}`, 'medium');
  }

  // Test Reports
  console.log('\n--- Testing Reports ---');
  try {
    await page.goto(`${BASE_URL}/admin/reports`);
    await delay(1500);

    const pageContent = await page.content();
    if (pageContent.includes('Report') || pageContent.includes('report')) {
      logSuccess('Reports', 'Reports page loaded');
    }

    // Check for report types
    const reportOptions = await page.$$('select option, [class*="report-type"], button[class*="report"]');
    console.log(`  Found ${reportOptions.length} report options`);
  } catch (e) {
    logIssue('Reports', `Reports error: ${e.message}`, 'medium');
  }

  // Logout
  try {
    const logoutBtn = await page.$('button:has-text("Logout"), a:has-text("Logout"), [class*="logout"]');
    if (logoutBtn) {
      await logoutBtn.click();
      await delay(1000);
    }
  } catch (e) {
    console.log('  Logout button not found, navigating to login');
  }
}

async function testStudentPortal(page) {
  console.log('\n========== TESTING STUDENT PORTAL ==========\n');

  // Login as student
  await page.goto(`${BASE_URL}/login`);
  await delay(1000);

  try {
    await page.fill('input[type="text"], input[name="username"], input#username', 'nassim');
    await page.fill('input[type="password"], input[name="password"], input#password', 'password123');
    await page.click('button[type="submit"]');
    await delay(2000);

    const currentUrl = page.url();
    if (currentUrl.includes('dashboard') || currentUrl.includes('student')) {
      logSuccess('Student Login', 'Successfully logged in as student');
    } else {
      logIssue('Student Login', 'Login did not redirect properly', 'high');
    }
  } catch (e) {
    logIssue('Student Login', `Login failed: ${e.message}`, 'high');
    return;
  }

  // Test Student Dashboard
  console.log('\n--- Testing Student Dashboard ---');
  try {
    await page.goto(`${BASE_URL}/student/dashboard`);
    await delay(1500);

    const dashboardContent = await page.content();
    if (dashboardContent.includes('Dashboard') || dashboardContent.includes('Welcome')) {
      logSuccess('Student Dashboard', 'Dashboard loaded');
    }
  } catch (e) {
    logIssue('Student Dashboard', `Dashboard error: ${e.message}`, 'medium');
  }

  // Test Courses
  console.log('\n--- Testing Student Courses ---');
  try {
    await page.goto(`${BASE_URL}/student/courses`);
    await delay(1500);

    const courseList = await page.$('table, [class*="course"], [class*="card"]');
    if (courseList) {
      logSuccess('Student Courses', 'Courses page loaded');
    }
  } catch (e) {
    logIssue('Student Courses', `Courses error: ${e.message}`, 'medium');
  }

  // Test Attendance
  console.log('\n--- Testing Student Attendance ---');
  try {
    await page.goto(`${BASE_URL}/student/attendance`);
    await delay(1500);

    const attendanceContent = await page.content();
    if (attendanceContent.includes('Attendance') || attendanceContent.includes('attendance')) {
      logSuccess('Student Attendance', 'Attendance page loaded');
    }
  } catch (e) {
    logIssue('Student Attendance', `Attendance error: ${e.message}`, 'medium');
  }

  // Test Grades
  console.log('\n--- Testing Student Grades ---');
  try {
    await page.goto(`${BASE_URL}/student/grades`);
    await delay(1500);

    const gradesContent = await page.content();
    if (gradesContent.includes('Grade') || gradesContent.includes('grade')) {
      logSuccess('Student Grades', 'Grades page loaded');
    }
  } catch (e) {
    logIssue('Student Grades', `Grades error: ${e.message}`, 'medium');
  }

  // Test Assignments
  console.log('\n--- Testing Student Assignments ---');
  try {
    await page.goto(`${BASE_URL}/student/assignments`);
    await delay(1500);

    const assignmentList = await page.$('table, [class*="assignment"], [class*="card"]');
    if (assignmentList) {
      logSuccess('Student Assignments', 'Assignments page loaded');
    }

    // Check for file upload capability
    const uploadInput = await page.$('input[type="file"]');
    if (uploadInput) {
      logSuccess('Student Assignments', 'File upload input available');
    }
  } catch (e) {
    logIssue('Student Assignments', `Assignments error: ${e.message}`, 'medium');
  }

  // Test Payments
  console.log('\n--- Testing Student Payments ---');
  try {
    await page.goto(`${BASE_URL}/student/payments`);
    await delay(1500);

    const paymentContent = await page.content();
    if (paymentContent.includes('Payment') || paymentContent.includes('payment')) {
      logSuccess('Student Payments', 'Payments page loaded');
    }
  } catch (e) {
    logIssue('Student Payments', `Payments error: ${e.message}`, 'medium');
  }

  // Test Schedule
  console.log('\n--- Testing Student Schedule ---');
  try {
    await page.goto(`${BASE_URL}/student/schedule`);
    await delay(1500);

    const scheduleContent = await page.content();
    if (scheduleContent.includes('Schedule') || scheduleContent.includes('schedule')) {
      logSuccess('Student Schedule', 'Schedule page loaded');
    }
  } catch (e) {
    logIssue('Student Schedule', `Schedule error: ${e.message}`, 'medium');
  }

  // Test Messages
  console.log('\n--- Testing Messaging ---');
  try {
    await page.goto(`${BASE_URL}/messages`);
    await delay(1500);

    const messagesContent = await page.content();
    if (messagesContent.includes('Message') || messagesContent.includes('message') || messagesContent.includes('Conversation')) {
      logSuccess('Messaging', 'Messages page loaded');
    }
  } catch (e) {
    logIssue('Messaging', `Messages error: ${e.message}`, 'medium');
  }

  // Test Study Groups
  console.log('\n--- Testing Study Groups ---');
  try {
    await page.goto(`${BASE_URL}/study-groups`);
    await delay(1500);

    const studyGroupContent = await page.content();
    if (studyGroupContent.includes('Study') || studyGroupContent.includes('Group')) {
      logSuccess('Study Groups', 'Study Groups page loaded');
    }
  } catch (e) {
    logIssue('Study Groups', `Study Groups error: ${e.message}`, 'medium');
  }

  // Test Settings/Profile
  console.log('\n--- Testing Settings/Profile ---');
  try {
    await page.goto(`${BASE_URL}/settings`);
    await delay(1500);

    const settingsContent = await page.content();
    if (settingsContent.includes('Settings') || settingsContent.includes('Profile')) {
      logSuccess('Settings', 'Settings page loaded');
    }

    // Check for profile picture upload
    const profileUpload = await page.$('input[type="file"]');
    if (profileUpload) {
      logSuccess('Settings', 'Profile picture upload available');
    }
  } catch (e) {
    logIssue('Settings', `Settings error: ${e.message}`, 'medium');
  }
}

async function testFacultyPortal(page) {
  console.log('\n========== TESTING FACULTY PORTAL ==========\n');

  // First logout and login as faculty
  await page.goto(`${BASE_URL}/login`);
  await delay(1000);

  try {
    // Try to login as faculty - need to find faculty credentials
    await page.fill('input[type="text"], input[name="username"], input#username', 'faculty');
    await page.fill('input[type="password"], input[name="password"], input#password', 'Faculty@123');
    await page.click('button[type="submit"]');
    await delay(2000);

    const currentUrl = page.url();
    if (currentUrl.includes('dashboard') || currentUrl.includes('faculty')) {
      logSuccess('Faculty Login', 'Successfully logged in as faculty');
    } else {
      logIssue('Faculty Login', 'Login may have failed - checking page', 'medium');
    }
  } catch (e) {
    logIssue('Faculty Login', `Login failed: ${e.message}`, 'high');
    return;
  }

  // Test Faculty Dashboard
  console.log('\n--- Testing Faculty Dashboard ---');
  try {
    await page.goto(`${BASE_URL}/faculty/dashboard`);
    await delay(1500);

    const dashboardContent = await page.content();
    if (dashboardContent.includes('Dashboard') || dashboardContent.includes('Faculty')) {
      logSuccess('Faculty Dashboard', 'Dashboard loaded');
    }
  } catch (e) {
    logIssue('Faculty Dashboard', `Dashboard error: ${e.message}`, 'medium');
  }

  // Test Faculty Courses
  console.log('\n--- Testing Faculty Courses ---');
  try {
    await page.goto(`${BASE_URL}/faculty/courses`);
    await delay(1500);

    const courseContent = await page.content();
    if (courseContent.includes('Course') || courseContent.includes('course')) {
      logSuccess('Faculty Courses', 'Courses page loaded');
    }
  } catch (e) {
    logIssue('Faculty Courses', `Courses error: ${e.message}`, 'medium');
  }

  // Test Faculty Attendance
  console.log('\n--- Testing Faculty Attendance ---');
  try {
    await page.goto(`${BASE_URL}/faculty/attendance`);
    await delay(1500);

    const attendanceContent = await page.content();
    if (attendanceContent.includes('Attendance') || attendanceContent.includes('attendance')) {
      logSuccess('Faculty Attendance', 'Attendance page loaded');
    }

    // Check for mark attendance button
    const markBtn = await page.$('button:has-text("Mark"), button:has-text("Save")');
    if (markBtn) {
      logSuccess('Faculty Attendance', 'Mark attendance functionality available');
    }
  } catch (e) {
    logIssue('Faculty Attendance', `Attendance error: ${e.message}`, 'medium');
  }

  // Test Faculty Assignments
  console.log('\n--- Testing Faculty Assignments ---');
  try {
    await page.goto(`${BASE_URL}/faculty/assignments`);
    await delay(1500);

    const assignmentContent = await page.content();
    if (assignmentContent.includes('Assignment') || assignmentContent.includes('assignment')) {
      logSuccess('Faculty Assignments', 'Assignments page loaded');
    }

    // Check for create assignment button
    const createBtn = await page.$('button:has-text("Create"), button:has-text("Add")');
    if (createBtn) {
      logSuccess('Faculty Assignments', 'Create assignment button available');
    }
  } catch (e) {
    logIssue('Faculty Assignments', `Assignments error: ${e.message}`, 'medium');
  }

  // Test Faculty Schedule
  console.log('\n--- Testing Faculty Schedule ---');
  try {
    await page.goto(`${BASE_URL}/faculty/schedule`);
    await delay(1500);

    const scheduleContent = await page.content();
    if (scheduleContent.includes('Schedule') || scheduleContent.includes('schedule')) {
      logSuccess('Faculty Schedule', 'Schedule page loaded');
    }
  } catch (e) {
    logIssue('Faculty Schedule', `Schedule error: ${e.message}`, 'medium');
  }
}

async function generateReport() {
  console.log('\n\n========================================');
  console.log('       COMPREHENSIVE TEST REPORT');
  console.log('========================================\n');

  console.log(`Total Issues Found: ${issues.length}`);
  console.log('');

  const highIssues = issues.filter(i => i.severity === 'high');
  const mediumIssues = issues.filter(i => i.severity === 'medium');
  const lowIssues = issues.filter(i => i.severity === 'low');

  console.log(`High Priority Issues: ${highIssues.length}`);
  console.log(`Medium Priority Issues: ${mediumIssues.length}`);
  console.log(`Low Priority Issues: ${lowIssues.length}`);
  console.log('');

  if (highIssues.length > 0) {
    console.log('--- HIGH PRIORITY ISSUES ---');
    highIssues.forEach((issue, i) => {
      console.log(`${i+1}. [${issue.category}] ${issue.description}`);
    });
    console.log('');
  }

  if (mediumIssues.length > 0) {
    console.log('--- MEDIUM PRIORITY ISSUES ---');
    mediumIssues.forEach((issue, i) => {
      console.log(`${i+1}. [${issue.category}] ${issue.description}`);
    });
    console.log('');
  }

  if (lowIssues.length > 0) {
    console.log('--- LOW PRIORITY ISSUES ---');
    lowIssues.forEach((issue, i) => {
      console.log(`${i+1}. [${issue.category}] ${issue.description}`);
    });
    console.log('');
  }

  console.log('========================================');
  console.log('          END OF REPORT');
  console.log('========================================\n');
}

async function main() {
  console.log('Starting SAMS Application Testing with Playwright...\n');
  console.log('Frontend URL:', BASE_URL);
  console.log('');

  const browser = await chromium.launch({
    headless: true,
    args: ['--no-sandbox', '--disable-setuid-sandbox']
  });

  const context = await browser.newContext({
    viewport: { width: 1280, height: 720 }
  });

  const page = await context.newPage();

  try {
    // Test Admin Portal
    await testAdminPortal(page);

    // Test Student Portal
    await testStudentPortal(page);

    // Test Faculty Portal
    await testFacultyPortal(page);

  } catch (e) {
    console.error('Test execution error:', e.message);
  } finally {
    await browser.close();
  }

  // Generate final report
  await generateReport();
}

main().catch(console.error);
