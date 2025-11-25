import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

// Create axios instance
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default {
  // Authentication
  login(credentials) {
    return apiClient.post('/auth/login', credentials)
  },

  validateToken(token) {
    return apiClient.get('/auth/validate', {
      headers: { Authorization: `Bearer ${token}` }
    })
  },

  // Admin - User Management
  getAllUsers() {
    return apiClient.get('/admin/users')
  },

  getUserById(id) {
    return apiClient.get(`/admin/users/${id}`)
  },

  createUser(userData) {
    return apiClient.post('/admin/users', userData)
  },

  updateUser(id, userData) {
    return apiClient.put(`/admin/users/${id}`, userData)
  },

  deleteUser(id) {
    return apiClient.delete(`/admin/users/${id}`)
  },

  toggleUserActive(id) {
    return apiClient.patch(`/admin/users/${id}/toggle-active`)
  },

  getAvailablePermissions() {
    return apiClient.get('/admin/users/permissions')
  },

  // Courses
  getAllCourses() {
    return apiClient.get('/courses')
  },

  getCourseById(id) {
    return apiClient.get(`/courses/${id}`)
  },

  createCourse(courseData) {
    return apiClient.post('/courses', courseData)
  },

  updateCourse(id, courseData) {
    return apiClient.put(`/courses/${id}`, courseData)
  },

  deleteCourse(id) {
    return apiClient.delete(`/courses/${id}`)
  },

  // Enrollments
  getStudentEnrollments(studentId) {
    return apiClient.get(`/enrollments/student/${studentId}`)
  },

  getCourseEnrollments(courseId) {
    return apiClient.get(`/enrollments/course/${courseId}`)
  },

  createEnrollment(enrollmentData) {
    return apiClient.post('/enrollments', enrollmentData)
  },

  updateEnrollmentStatus(id, status) {
    return apiClient.patch(`/enrollments/${id}/status`, { status })
  },

  // Assignments
  getStudentAssignments(studentId) {
    return apiClient.get(`/assignments/student/${studentId}`)
  },

  getCourseAssignments(courseId) {
    return apiClient.get(`/assignments/course/${courseId}`)
  },

  getFacultyAssignments(facultyId) {
    return apiClient.get(`/assignments/faculty/${facultyId}`)
  },

  createAssignment(assignmentData) {
    return apiClient.post('/assignments', assignmentData)
  },

  // Grades
  getStudentGrades(studentId) {
    return apiClient.get(`/grades/student/${studentId}`)
  },

  getCourseGrades(courseId) {
    return apiClient.get(`/grades/course/${courseId}`)
  },

  assignGrade(gradeData) {
    return apiClient.post('/grades/assign', gradeData)
  },

  // Notifications
  getUserNotifications(userId, page = 0, size = 10) {
    return apiClient.get(`/notifications?userId=${userId}&page=${page}&size=${size}`)
  },

  getUnreadNotificationCount(userId) {
    return apiClient.get(`/notifications/unread-count?userId=${userId}`)
  },

  markNotificationAsRead(id) {
    return apiClient.patch(`/notifications/${id}/read`)
  },

  // Study Groups
  getUserStudyGroups(userId) {
    return apiClient.get(`/study-groups/user/${userId}/groups`)
  },

  getStudyGroupById(id) {
    return apiClient.get(`/study-groups/${id}`)
  },

  createStudyGroup(groupData) {
    return apiClient.post('/study-groups', groupData)
  },

  // Connections
  getUserConnections(userId) {
    return apiClient.get(`/connections/user/${userId}`)
  },

  getConnectionCount(userId) {
    return apiClient.get(`/connections/user/${userId}/count`)
  },

  // Messages
  getUnreadMessageCount(userId) {
    return apiClient.get(`/messages/user/${userId}/unread-count`)
  },

  // Payments
  createPayment(studentId, semesterId) {
    return apiClient.post('/payments/create', { studentId, semesterId })
  },

  submitPayment(paymentId, paymentData) {
    return apiClient.post(`/payments/${paymentId}/submit`, paymentData)
  },

  approvePayment(paymentId) {
    return apiClient.post(`/payments/${paymentId}/approve`)
  },

  rejectPayment(paymentId, reason) {
    return apiClient.post(`/payments/${paymentId}/reject`, { reason })
  },

  getStudentPayments(studentId) {
    return apiClient.get(`/payments/student/${studentId}`)
  },

  getPaymentById(id) {
    return apiClient.get(`/payments/${id}`)
  },

  getPaymentHistory(paymentId) {
    return apiClient.get(`/payments/${paymentId}/history`)
  },

  getPendingApprovalPayments() {
    return apiClient.get('/payments/pending-approval')
  },

  getAllPayments() {
    return apiClient.get('/payments')
  },

  // Degree Progress
  getAllDegreePrograms() {
    return apiClient.get('/degree-progress/programs')
  },

  createDegreeProgram(programData) {
    return apiClient.post('/degree-progress/programs', programData)
  },

  enrollStudentInProgram(studentId, programId, startDate, expectedGraduationDate) {
    return apiClient.post(`/degree-progress/students/${studentId}/enroll`, {
      programId,
      startDate,
      expectedGraduationDate
    })
  },

  getStudentProgress(studentId) {
    return apiClient.get(`/degree-progress/students/${studentId}/progress`)
  },

  updateStudentProgress(studentId) {
    return apiClient.post(`/degree-progress/students/${studentId}/update-progress`)
  },

  markStudentAsGraduated(studentId) {
    return apiClient.post(`/degree-progress/students/${studentId}/graduate`)
  },

  getEligibleForGraduation() {
    return apiClient.get('/degree-progress/eligible-graduation')
  },

  getStudentsAtRisk() {
    return apiClient.get('/degree-progress/at-risk')
  },

  // File Upload
  uploadAssignmentFile(file, studentId, assignmentId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('studentId', studentId)
    formData.append('assignmentId', assignmentId)

    return apiClient.post('/files/upload/assignment', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  downloadFile(filePath) {
    return apiClient.get('/files/download', {
      params: { filePath },
      responseType: 'blob'
    })
  },

  deleteFile(filePath) {
    return apiClient.delete('/files/delete', {
      params: { filePath }
    })
  },

  // Semesters
  getAllSemesters() {
    return apiClient.get('/semesters')
  },

  getActiveSemester() {
    return apiClient.get('/semesters/active')
  },

  createSemester(semesterData) {
    return apiClient.post('/semesters', semesterData)
  },

  updateSemester(id, semesterData) {
    return apiClient.put(`/semesters/${id}`, semesterData)
  },

  // Waitlist
  getCourseWaitlist(courseId) {
    return apiClient.get(`/enrollments/waitlist/${courseId}`)
  },

  dropEnrollment(enrollmentId) {
    return apiClient.delete(`/enrollments/${enrollmentId}/drop`)
  },

  // Attendance Management
  markAttendance(attendanceData) {
    return apiClient.post('/attendance/mark', attendanceData)
  },

  markBulkAttendance(bulkData) {
    return apiClient.post('/attendance/mark-bulk', bulkData)
  },

  getAttendanceByDate(date) {
    return apiClient.get(`/attendance/date/${date}`)
  },

  getAttendanceByDateRange(startDate, endDate) {
    return apiClient.get(`/attendance/range?startDate=${startDate}&endDate=${endDate}`)
  },

  getAttendanceByUser(userId) {
    return apiClient.get(`/attendance/user/${userId}`)
  },

  getAttendanceByUserAndDateRange(userId, startDate, endDate) {
    return apiClient.get(`/attendance/user/${userId}/range?startDate=${startDate}&endDate=${endDate}`)
  },

  getAttendanceStatisticsByDate(date) {
    return apiClient.get(`/attendance/statistics/date/${date}`)
  },

  getAttendanceStatisticsByDateRange(startDate, endDate) {
    return apiClient.get(`/attendance/statistics/range?startDate=${startDate}&endDate=${endDate}`)
  },

  getAttendanceStatisticsByUser(userId, startDate, endDate) {
    return apiClient.get(`/attendance/statistics/user/${userId}?startDate=${startDate}&endDate=${endDate}`)
  },

  getAttendanceStatisticsByRole(startDate, endDate) {
    return apiClient.get(`/attendance/statistics/by-role?startDate=${startDate}&endDate=${endDate}`)
  },

  getAttendancePercentage(userId, startDate, endDate) {
    return apiClient.get(`/attendance/percentage/${userId}?startDate=${startDate}&endDate=${endDate}`)
  },

  deleteAttendance(id) {
    return apiClient.delete(`/attendance/${id}`)
  },

  // Dashboard Analytics
  getCompleteDashboard() {
    return apiClient.get('/dashboard/complete')
  },

  getDashboardStats() {
    return apiClient.get('/dashboard/stats')
  },

  getEnrollmentTrends(months = 6) {
    return apiClient.get(`/dashboard/enrollment-trends?months=${months}`)
  },

  getGradeDistribution() {
    return apiClient.get('/dashboard/grade-distribution')
  },

  getFinancialSummary() {
    return apiClient.get('/dashboard/financial-summary')
  },

  getGenderDemographics() {
    return apiClient.get('/dashboard/gender-demographics')
  },

  getRecentActivities(limit = 10) {
    return apiClient.get(`/dashboard/recent-activities?limit=${limit}`)
  },

  // Fee Management - Fee Structure
  createFeeStructure(data) {
    return apiClient.post('/fees/structures', data)
  },

  updateFeeStructure(id, data) {
    return apiClient.put(`/fees/structures/${id}`, data)
  },

  deleteFeeStructure(id) {
    return apiClient.delete(`/fees/structures/${id}`)
  },

  getFeeStructureById(id) {
    return apiClient.get(`/fees/structures/${id}`)
  },

  getAllFeeStructures() {
    return apiClient.get('/fees/structures')
  },

  getActiveFeeStructures() {
    return apiClient.get('/fees/structures/active')
  },

  getFeeStructuresForProgram(programId, semester = null) {
    const params = semester ? `?semester=${semester}` : ''
    return apiClient.get(`/fees/structures/program/${programId}${params}`)
  },

  getFeeStructuresByCategory(category) {
    return apiClient.get(`/fees/structures/category/${category}`)
  },

  // Fee Management - Fee Items
  getFeeItemsByPayment(paymentId) {
    return apiClient.get(`/fees/items/payment/${paymentId}`)
  },

  getFeeItemsByStudent(studentId) {
    return apiClient.get(`/fees/items/student/${studentId}`)
  },

  getFeeItemsByStudentAndSemester(studentId, semesterId) {
    return apiClient.get(`/fees/items/student/${studentId}/semester/${semesterId}`)
  },

  applyFeeDiscount(feeItemId, amount, reason) {
    return apiClient.post(`/fees/items/${feeItemId}/discount?amount=${amount}&reason=${encodeURIComponent(reason)}`)
  },

  waiveFee(feeItemId, reason) {
    return apiClient.post(`/fees/items/${feeItemId}/waive?reason=${encodeURIComponent(reason)}`)
  },

  removeWaiver(feeItemId) {
    return apiClient.post(`/fees/items/${feeItemId}/remove-waiver`)
  },

  // Fee Management - Reports
  getFeeBreakdown(studentId, semesterId) {
    return apiClient.get(`/fees/breakdown/student/${studentId}/semester/${semesterId}`)
  },

  getFeeReport(semesterId, reportType = 'SUMMARY') {
    return apiClient.get(`/fees/reports/semester/${semesterId}?reportType=${reportType}`)
  },

  // ==================== Teacher Management ====================

  // Teacher Profiles
  createTeacherProfile(data) {
    return apiClient.post('/teachers/profiles', data)
  },

  updateTeacherProfile(profileId, data) {
    return apiClient.put(`/teachers/profiles/${profileId}`, data)
  },

  getTeacherProfile(profileId) {
    return apiClient.get(`/teachers/profiles/${profileId}`)
  },

  getTeacherProfileByUserId(userId) {
    return apiClient.get(`/teachers/profiles/user/${userId}`)
  },

  getAllTeacherProfiles() {
    return apiClient.get('/teachers/profiles')
  },

  getActiveTeacherProfiles() {
    return apiClient.get('/teachers/profiles/active')
  },

  searchTeachers(query) {
    return apiClient.get(`/teachers/profiles/search?query=${encodeURIComponent(query)}`)
  },

  getTeachersByDepartment(department) {
    return apiClient.get(`/teachers/profiles/department/${department}`)
  },

  getTeachersByFilters(filters) {
    const params = new URLSearchParams()
    if (filters.department) params.append('department', filters.department)
    if (filters.designation) params.append('designation', filters.designation)
    if (filters.active !== undefined) params.append('active', filters.active)
    if (filters.availableForConsultation !== undefined) params.append('availableForConsultation', filters.availableForConsultation)
    return apiClient.get(`/teachers/profiles/filter?${params.toString()}`)
  },

  getTopRatedTeachers() {
    return apiClient.get('/teachers/profiles/top-rated')
  },

  toggleTeacherProfileStatus(profileId) {
    return apiClient.patch(`/teachers/profiles/${profileId}/toggle-status`)
  },

  deleteTeacherProfile(profileId) {
    return apiClient.delete(`/teachers/profiles/${profileId}`)
  },

  // Office Hours
  createOfficeHours(data) {
    return apiClient.post('/teachers/office-hours', data)
  },

  updateOfficeHours(officeHoursId, data) {
    return apiClient.put(`/teachers/office-hours/${officeHoursId}`, data)
  },

  getOfficeHours(officeHoursId) {
    return apiClient.get(`/teachers/office-hours/${officeHoursId}`)
  },

  getTeacherOfficeHours(teacherId) {
    return apiClient.get(`/teachers/office-hours/teacher/${teacherId}`)
  },

  getActiveTeacherOfficeHours(teacherId) {
    return apiClient.get(`/teachers/office-hours/teacher/${teacherId}/active`)
  },

  getAvailableOfficeHoursForDate(teacherId, date) {
    return apiClient.get(`/teachers/office-hours/teacher/${teacherId}/date/${date}`)
  },

  cancelOfficeHours(officeHoursId, reason, specificDate = null) {
    const params = new URLSearchParams()
    params.append('reason', reason)
    if (specificDate) params.append('specificDate', specificDate)
    return apiClient.patch(`/teachers/office-hours/${officeHoursId}/cancel?${params.toString()}`)
  },

  reactivateOfficeHours(officeHoursId) {
    return apiClient.patch(`/teachers/office-hours/${officeHoursId}/reactivate`)
  },

  deleteOfficeHours(officeHoursId) {
    return apiClient.delete(`/teachers/office-hours/${officeHoursId}`)
  },

  // Teacher Statistics and Schedule
  getTeacherStatistics(teacherId) {
    return apiClient.get(`/teachers/statistics/${teacherId}`)
  },

  getTeacherSchedule(teacherId) {
    return apiClient.get(`/teachers/schedule/${teacherId}`)
  }
}
