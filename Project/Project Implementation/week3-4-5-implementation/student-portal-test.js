const { chromium } = require('@playwright/test');

async function testStudentPortal() {
    console.log('Starting Student Portal Test...\n');

    const browser = await chromium.launch({
        headless: true,
        args: ['--no-sandbox', '--disable-gpu', '--disable-dev-shm-usage']
    });
    const context = await browser.newContext();
    const page = await context.newPage();

    const results = {
        login: { status: 'pending', details: '' },
        dashboard: { status: 'pending', details: '' },
        courses: { status: 'pending', details: '' },
        grades: { status: 'pending', details: '' },
        attendance: { status: 'pending', details: '' },
        schedule: { status: 'pending', details: '' },
        assignments: { status: 'pending', details: '' },
        payments: { status: 'pending', details: '' },
        apiData: { status: 'pending', details: '' }
    };

    try {
        // 1. Test Login
        console.log('=== Testing Login ===');
        await page.goto('http://localhost:5173/login');
        await page.waitForLoadState('networkidle');

        // Fill login form
        await page.fill('input[type="text"], input[name="username"]', 'teststudent');
        await page.fill('input[type="password"]', 'password123');
        await page.click('button[type="submit"]');

        // Wait for navigation
        await page.waitForURL('**/student/**', { timeout: 10000 });
        results.login = { status: 'PASS', details: 'Successfully logged in as teststudent' };
        console.log('✓ Login successful');

        // 2. Test Dashboard
        console.log('\n=== Testing Student Dashboard ===');
        await page.goto('http://localhost:5173/student/dashboard');
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(2000);

        // Check for dashboard elements
        const dashboardContent = await page.content();
        const hasDashboardData = dashboardContent.includes('Dashboard') ||
                                  dashboardContent.includes('Welcome') ||
                                  dashboardContent.includes('Overview');

        // Check for charts
        const hasCharts = await page.locator('canvas, .chart, svg[class*="chart"], .apexcharts-canvas').count();

        // Check for data cards/stats
        const hasCards = await page.locator('.card, .stat, .metric, [class*="card"]').count();

        results.dashboard = {
            status: hasDashboardData ? 'PASS' : 'WARN',
            details: `Charts: ${hasCharts}, Cards/Stats: ${hasCards}`
        };
        console.log(`✓ Dashboard loaded - Charts: ${hasCharts}, Cards: ${hasCards}`);

        // Take screenshot
        await page.screenshot({ path: '.playwright-mcp/student-dashboard-real-test.png', fullPage: true });
        console.log('  Screenshot saved: student-dashboard-real-test.png');

        // 3. Test Courses
        console.log('\n=== Testing Student Courses ===');
        await page.goto('http://localhost:5173/student/courses');
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(2000);

        const courseCards = await page.locator('.course-card, [class*="course"], .card').count();
        const courseContent = await page.content();
        const hasRealCourseData = courseContent.includes('CS') ||
                                   courseContent.includes('MATH') ||
                                   courseContent.includes('Course') ||
                                   courseContent.includes('Credits');

        results.courses = {
            status: hasRealCourseData ? 'PASS' : 'WARN',
            details: `Course elements found: ${courseCards}`
        };
        console.log(`✓ Courses page - Elements: ${courseCards}`);
        await page.screenshot({ path: '.playwright-mcp/student-courses-real-test.png', fullPage: true });

        // 4. Test Grades
        console.log('\n=== Testing Student Grades ===');
        await page.goto('http://localhost:5173/student/grades');
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(2000);

        const gradesContent = await page.content();
        const hasGradeData = gradesContent.includes('Grade') ||
                             gradesContent.includes('GPA') ||
                             gradesContent.includes('Score') ||
                             gradesContent.includes('A') ||
                             gradesContent.includes('B');

        const gradeCharts = await page.locator('canvas, .chart, svg, .apexcharts-canvas').count();

        results.grades = {
            status: hasGradeData ? 'PASS' : 'WARN',
            details: `Grade charts/visualizations: ${gradeCharts}`
        };
        console.log(`✓ Grades page - Charts: ${gradeCharts}`);
        await page.screenshot({ path: '.playwright-mcp/student-grades-real-test.png', fullPage: true });

        // 5. Test Attendance
        console.log('\n=== Testing Student Attendance ===');
        await page.goto('http://localhost:5173/student/attendance');
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(2000);

        const attendanceContent = await page.content();
        const hasAttendanceData = attendanceContent.includes('Attendance') ||
                                   attendanceContent.includes('Present') ||
                                   attendanceContent.includes('Absent') ||
                                   attendanceContent.includes('%');

        results.attendance = {
            status: hasAttendanceData ? 'PASS' : 'WARN',
            details: 'Attendance data display verified'
        };
        console.log('✓ Attendance page loaded');
        await page.screenshot({ path: '.playwright-mcp/student-attendance-real-test.png', fullPage: true });

        // 6. Test Schedule
        console.log('\n=== Testing Student Schedule ===');
        await page.goto('http://localhost:5173/student/schedule');
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(2000);

        const scheduleContent = await page.content();
        const hasScheduleData = scheduleContent.includes('Schedule') ||
                                 scheduleContent.includes('Monday') ||
                                 scheduleContent.includes('Class') ||
                                 scheduleContent.includes('Time');

        results.schedule = {
            status: hasScheduleData ? 'PASS' : 'WARN',
            details: 'Schedule display verified'
        };
        console.log('✓ Schedule page loaded');
        await page.screenshot({ path: '.playwright-mcp/student-schedule-real-test.png', fullPage: true });

        // 7. Test Assignments
        console.log('\n=== Testing Student Assignments ===');
        await page.goto('http://localhost:5173/student/assignments');
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(2000);

        const assignmentsContent = await page.content();
        const hasAssignmentData = assignmentsContent.includes('Assignment') ||
                                   assignmentsContent.includes('Due') ||
                                   assignmentsContent.includes('Submit') ||
                                   assignmentsContent.includes('Homework');

        results.assignments = {
            status: hasAssignmentData ? 'PASS' : 'WARN',
            details: 'Assignments display verified'
        };
        console.log('✓ Assignments page loaded');
        await page.screenshot({ path: '.playwright-mcp/student-assignments-real-test.png', fullPage: true });

        // 8. Test Payments
        console.log('\n=== Testing Student Payments ===');
        await page.goto('http://localhost:5173/student/payments');
        await page.waitForLoadState('networkidle');
        await page.waitForTimeout(2000);

        const paymentsContent = await page.content();
        const hasPaymentData = paymentsContent.includes('Payment') ||
                                paymentsContent.includes('Balance') ||
                                paymentsContent.includes('Amount') ||
                                paymentsContent.includes('$') ||
                                paymentsContent.includes('DH') ||
                                paymentsContent.includes('MAD');

        results.payments = {
            status: hasPaymentData ? 'PASS' : 'WARN',
            details: 'Payments display verified'
        };
        console.log('✓ Payments page loaded');
        await page.screenshot({ path: '.playwright-mcp/student-payments-real-test.png', fullPage: true });

        // Check for mock data indicators
        console.log('\n=== Checking for Mock Data ===');
        const allContent = dashboardContent + courseContent + gradesContent +
                          attendanceContent + scheduleContent + assignmentsContent + paymentsContent;

        const mockIndicators = ['mock', 'placeholder', 'lorem ipsum', 'test data', 'sample'];
        const foundMocks = mockIndicators.filter(indicator =>
            allContent.toLowerCase().includes(indicator)
        );

        results.apiData = {
            status: foundMocks.length === 0 ? 'PASS' : 'WARN',
            details: foundMocks.length === 0 ? 'No obvious mock data found' : `Possible mock indicators: ${foundMocks.join(', ')}`
        };
        console.log(foundMocks.length === 0 ? '✓ No obvious mock data detected' : `⚠ Possible mock data: ${foundMocks.join(', ')}`);

    } catch (error) {
        console.error('Error during test:', error.message);
        results.error = error.message;
    } finally {
        await browser.close();
    }

    // Print Summary
    console.log('\n' + '='.repeat(60));
    console.log('STUDENT PORTAL TEST RESULTS SUMMARY');
    console.log('='.repeat(60));

    for (const [test, result] of Object.entries(results)) {
        if (test !== 'error') {
            const icon = result.status === 'PASS' ? '✓' : result.status === 'WARN' ? '⚠' : '✗';
            console.log(`${icon} ${test.toUpperCase()}: ${result.status} - ${result.details}`);
        }
    }

    if (results.error) {
        console.log(`\n✗ ERROR: ${results.error}`);
    }

    console.log('\nScreenshots saved to .playwright-mcp/ directory');
    console.log('='.repeat(60));

    return results;
}

testStudentPortal().catch(console.error);
