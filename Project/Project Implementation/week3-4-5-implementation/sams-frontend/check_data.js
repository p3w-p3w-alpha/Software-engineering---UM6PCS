import axios from 'axios';

const API_URL = 'http://localhost:8080/api';
const ADMIN_CREDENTIALS = {
    username: 'superadmin',
    password: 'Admin@123'
};

async function checkData() {
    try {
        console.log('🔑 Logging in...');
        const loginRes = await axios.post(`${API_URL}/auth/login`, ADMIN_CREDENTIALS);
        const token = loginRes.data.token;
        const headers = { Authorization: `Bearer ${token}` };

        console.log('🔍 Checking student progress...');
        // Find a student
        const usersRes = await axios.get(`${API_URL}/admin/users`, { headers });
        const students = usersRes.data.filter(u => u.role === 'STUDENT');

        if (students.length > 0) {
            const student = students[0];
            console.log(`Checking student: ${student.username} (${student.id})`);

            try {
                const progressRes = await axios.get(`${API_URL}/degree-progress/students/${student.id}/progress`, { headers });
                console.log('✅ Student progress found!');
                console.log('Status:', progressRes.data.status);
                console.log('Credits:', progressRes.data.creditsCompleted);
            } catch (error) {
                console.log('❌ Student progress NOT found or error:', error.message);
                if (error.response) console.log('Response:', error.response.status);
            }
        }

        console.log('🔍 Checking eligible for graduation...');
        try {
            const eligibleRes = await axios.get(`${API_URL}/degree-progress/students/graduation-eligible`, { headers });
            console.log('✅ Eligible endpoint worked! Count:', eligibleRes.data.length);
        } catch (error) {
            console.log('❌ Eligible endpoint failed:', error.message);
            if (error.response) console.log('Response:', error.response.status);
        }

    } catch (error) {
        console.error('❌ Error:', error.message);
    }
}

checkData();
