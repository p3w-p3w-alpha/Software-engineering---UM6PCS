import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

// Admin credentials
const ADMIN_CREDENTIALS = {
    username: 'superadmin',
    password: 'Admin@123'
};

async function populateData() {
    try {
        console.log('🔄 Starting data population...');

        // 1. Login
        console.log('🔑 Logging in...');
        const loginRes = await axios.post(`${API_URL}/auth/login`, ADMIN_CREDENTIALS);
        const token = loginRes.data.token;
        const headers = { Authorization: `Bearer ${token}` };
        console.log('✅ Login successful');

        // 2. Fix Semester (Create Active Semester)
        console.log('📅 Checking active semester...');
        try {
            await axios.get(`${API_URL}/semesters/current`, { headers });
            console.log('✅ Active semester already exists');
        } catch (error) {
            if (error.response && error.response.status === 404) {
                console.log('⚠️ No active semester found. Creating one...');
                const semesterData = {
                    name: 'Fall 2025',
                    code: 'FALL2025',
                    startDate: '2025-09-01',
                    endDate: '2025-12-15',
                    enrollmentStartDate: '2025-08-15',
                    enrollmentEndDate: '2025-09-15',
                    active: true,
                    registrationOpen: true
                };
                await axios.post(`${API_URL}/semesters`, semesterData, { headers });
                console.log('✅ Active semester created: Fall 2025');
            } else {
                console.error('❌ Error checking semester:', error.message);
            }
        }

        // 3. Fix Degree Progress (Ensure Data Exists)
        console.log('🎓 Checking degree programs...');
        let programId;
        const programsRes = await axios.get(`${API_URL}/degree-progress/programs`, { headers });
        if (programsRes.data.length === 0) {
            console.log('⚠️ No degree programs found. Creating one...');
            const programData = {
                name: 'Bachelor of Computer Science',
                code: 'BCS',
                description: 'Computer Science Degree',
                totalCreditsRequired: 120,
                minimumGpa: 2.0,
                department: 'Computer Science',
                typicalDurationSemesters: 8,
                active: true
            };
            const createProgRes = await axios.post(`${API_URL}/degree-progress/programs`, programData, { headers });
            programId = createProgRes.data.id;
            console.log('✅ Degree program created');
        } else {
            programId = programsRes.data[0].id;
            console.log(`✅ Using existing degree program ID: ${programId}`);
        }

        // 4. Enroll a Student
        console.log('bust Check for students to enroll...');
        const usersRes = await axios.get(`${API_URL}/admin/users`, { headers });
        const students = usersRes.data.filter(u => u.role === 'STUDENT');

        if (students.length > 0) {
            const student = students[0];
            console.log(`👤 Found student: ${student.username} (ID: ${student.id})`);

            try {
                // Check if already enrolled
                await axios.get(`${API_URL}/degree-progress/students/${student.id}/progress`, { headers });
                console.log('✅ Student already enrolled in a program');
            } catch (error) {
                console.log('⚠️ Student not enrolled. Enrolling now...');
                await axios.post(`${API_URL}/degree-progress/students/${student.id}/enroll`, null, {
                    params: {
                        degreeProgramId: programId,
                        startDate: '2025-09-01'
                    },
                    headers
                });
                console.log('✅ Student enrolled successfully');
            }

            // 5. Update Progress (Calculates GPA, etc.)
            console.log('🔄 Updating student progress stats...');
            await axios.post(`${API_URL}/degree-progress/students/${student.id}/update-progress`, {}, { headers });
            console.log('✅ Student progress updated');

        } else {
            console.log('❌ No students found to enroll!');
        }

        console.log('🎉 Data population complete!');

    } catch (error) {
        console.error('❌ Error:', error.response ? error.response.data : error.message);
    }
}

populateData();
