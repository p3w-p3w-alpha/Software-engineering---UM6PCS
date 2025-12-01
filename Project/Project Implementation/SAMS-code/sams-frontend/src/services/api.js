/*
 * api.js - API Service Layer
 *
 * handles all backend communication using axios
 * includes authentication, error handling, and interceptors
 * this is a huge file with all the api endpoints - took ages to organize
 */

import axios from 'axios'

// backend api url - change this for production deployment
const API_BASE_URL = 'http://localhost:8080/api'

// create axios instance with default config
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// request interceptor - automatically adds auth token to all requests
// this was wierd to debug at first but works great now
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

// response interceptor - handles errors globally
// logs out user if token expires
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // token expired or invalid - redirect to login
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }

    // extract error message from different response formats
    // backend can return errors in various formats so we handle all of them
    if (error.response?.data) {
      const data = error.response.data
      error.userMessage = data.message || data.error || data.detail ||
        (typeof data === 'string' ? data : null) ||
        `Request failed with status ${error.response.status}`
    } else if (error.request) {
      error.userMessage = 'Network error - please check your connection'
    } else {
      error.userMessage = error.message || 'An unexpected error occured'
    }

    return Promise.reject(error)
  }
)

// getErrorMessage - helper function to extract error messages
// makes error handling in components easier
export function getErrorMessage(error, fallbackMessage = 'An error occurred') {
  if (error.userMessage) {
    return error.userMessage
  }
  if (error.response?.data?.message) {
    return error.response.data.message
  }
  if (error.response?.data?.error) {
    return error.response.data.error
  }
  if (typeof error.response?.data === 'string') {
    return error.response.data
  }
  if (error.message) {
    return error.message
  }
  return fallbackMessage
}

export default {
  // ========================================
  // GENERIC HTTP METHODS
  // ========================================
  get(url, config) {
    return apiClient.get(url, config)
  },

  post(url, data, config) {
    return apiClient.post(url, data, config)
  },

  put(url, data, config) {
    return apiClient.put(url, data, config)
  },

  patch(url, data, config) {
    return apiClient.patch(url, data, config)
  },

  delete(url, config) {
    return apiClient.delete(url, config)
  },

  // ========================================
  // AUTHENTICATION ENDPOINTS
  // ========================================

  // login - authenticates user and returns token
  login(credentials) {
    return apiClient.post('/auth/login', credentials)
  },

  // validateToken - checks if token is still valid
  validateToken(token) {
    return apiClient.get('/auth/validate', {
      headers: { Authorization: `Bearer ${token}` }
    })
  },

  // ========================================
  // GLOBAL SEARCH
  // ========================================
  globalSearch(query) {
    return apiClient.get(`/search?query=${encodeURIComponent(query)}`)
  },

  searchUsers(query) {
    return apiClient.get(`/users/search?query=${encodeURIComponent(query)}`)
  },

  searchCourses(query) {
    return apiClient.get(`/courses/search?query=${encodeURIComponent(query)}`)
  },

  // Admin - User Management
  getAllUsers() {
    return apiClient.get('/admin/users')
  },

  getUserById(id) {
    return apiClient.get(`/users/${id}`)
  },

  createUser(userData) {
    return apiClient.post('/admin/users', userData)
  },

  updateUser(id, userData) {
    return apiClient.put(`/admin/users/${id}`, userData)
  },

  // Update user's own profile (authenticated user)
  updateProfile(id, profileData) {
    return apiClient.put(`/users/${id}/profile`, profileData)
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

  getCoursesByInstructor(instructorId) {
    return apiClient.get(`/courses/instructor/${instructorId}`)
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

  assignInstructor(courseId, instructorId) {
    return apiClient.put(`/courses/${courseId}/instructor/${instructorId}`)
  },

  removeInstructor(courseId) {
    return apiClient.delete(`/courses/${courseId}/instructor`)
  },

  getFacultyUsers() {
    return apiClient.get('/admin/users').then(response => {
      const facultyUsers = response.data.filter(user => user.role === 'FACULTY')
      return { data: facultyUsers }
    })
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
    return apiClient.patch(`/enrollments/${id}/status`, null, {
      params: { status }
    })
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
    // Backend expects { enrollmentId, gradeValue }
    return apiClient.post('/grades', {
      enrollmentId: gradeData.enrollmentId,
      gradeValue: gradeData.letterGrade || gradeData.gradeValue
    })
  },

  finalizeGrade(gradeId) {
    return apiClient.post(`/grades/${gradeId}/finalize`)
  },

  unfinalizeGrade(gradeId, reason = 'Admin override') {
    return apiClient.post(`/grades/${gradeId}/unfinalize`, null, {
      params: { reason }
    })
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
    const { creatorId, ...requestBody } = groupData
    return apiClient.post(`/study-groups?creatorId=${creatorId}`, requestBody)
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
    return apiClient.post(`/payments/${paymentId}/reject`, null, {
      params: { reason }
    })
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
    return apiClient.get('/degree-progress/students/graduation-eligible')
  },

  getStudentsAtRisk() {
    return apiClient.get('/degree-progress/students/at-risk')
  },

  // File Upload
  uploadAssignmentFile(file, studentId, assignmentId) {
    const formData = new FormData()
    formData.append('file', file)

    // Pass studentId and assignmentId as query parameters (as expected by backend @RequestParam)
    return apiClient.post(`/files/upload/assignment?studentId=${studentId}&assignmentId=${assignmentId}`, formData, {
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

  // Study Group File Upload
  uploadStudyGroupFile(file, groupId, userId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('groupId', groupId)
    formData.append('userId', userId)

    return apiClient.post('/files/upload/studygroup', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Semesters
  getAllSemesters() {
    return apiClient.get('/semesters')
  },

  getActiveSemester() {
    return apiClient.get('/semesters/current')
  },

  createSemester(semesterData) {
    return apiClient.post('/semesters', semesterData)
  },

  updateSemester(id, semesterData) {
    return apiClient.put(`/semesters/${id}`, semesterData)
  },

  // Waitlist
  getCourseWaitlist(courseId) {
    return apiClient.get(`/courses/${courseId}/waitlist`)
  },

  promoteFromWaitlist(courseId, studentId) {
    return apiClient.post(`/courses/${courseId}/waitlist/${studentId}/promote`)
  },

  removeFromWaitlist(courseId, studentId) {
    return apiClient.delete(`/courses/${courseId}/waitlist/${studentId}`)
  },

  dropEnrollment(enrollmentId) {
    return apiClient.put(`/enrollments/${enrollmentId}/drop`)
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

  // Alias for student dashboard
  getStudentAttendance(studentId) {
    return apiClient.get(`/attendance/user/${studentId}`)
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
  },

  // ========================================
  // PHASE 2: ASSIGNMENT SUBMISSIONS
  // ========================================
  getAssignmentById(assignmentId) {
    return apiClient.get(`/assignments/${assignmentId}`)
  },

  getStudentSubmissions(studentId) {
    return apiClient.get(`/submissions/student/${studentId}`)
  },

  getAssignmentSubmissions(assignmentId) {
    return apiClient.get(`/submissions/assignment/${assignmentId}`)
  },

  getSubmissionById(submissionId) {
    return apiClient.get(`/submissions/${submissionId}`)
  },

  submitAssignment(data) {
    // Use the new endpoint that accepts pre-uploaded file paths (JSON body)
    return apiClient.post('/submissions/submit-with-files', data)
  },

  gradeSubmission(submissionId, data) {
    return apiClient.post(`/submissions/${submissionId}/grade`, data)
  },

  getAssignmentsByCourse(courseId) {
    return apiClient.get(`/assignments/course/${courseId}`)
  },

  // ========================================
  // PHASE 2: PRIVATE MESSAGES
  // ========================================
  sendMessage(data) {
    return apiClient.post('/messages/send', { content: data.content }, {
      params: { senderId: data.senderId, receiverId: data.receiverId }
    })
  },

  getConversation(userId1, userId2) {
    return apiClient.get('/messages/conversation', {
      params: { user1Id: userId1, user2Id: userId2 }
    })
  },

  getUserConversations(userId) {
    return apiClient.get(`/messages/user/${userId}/conversations`)
  },

  markMessageAsRead(messageId, userId) {
    return apiClient.post(`/messages/${messageId}/read`, null, {
      params: { userId }
    })
  },

  markConversationAsRead(userId, otherUserId) {
    return apiClient.post('/messages/conversation/read', null, {
      params: { user1Id: userId, user2Id: otherUserId }
    })
  },

  deleteMessage(messageId) {
    return apiClient.delete(`/messages/${messageId}`)
  },

  // ========================================
  // PHASE 2: GRADE MANAGEMENT
  // ========================================
  getEnrollmentById(enrollmentId) {
    return apiClient.get(`/enrollments/${enrollmentId}`)
  },

  updateEnrollmentGrades(enrollmentId, data) {
    return apiClient.put(`/enrollments/${enrollmentId}/grades`, data)
  },

  getSemesterById(semesterId) {
    return apiClient.get(`/semesters/${semesterId}`)
  },

  // ========================================
  // PHASE 2: STUDY GROUPS
  // ========================================
  getAllStudyGroups() {
    return apiClient.get('/study-groups')
  },

  getPublicStudyGroups() {
    return apiClient.get('/study-groups/public')
  },

  getStudyGroupsCreatedByUser(userId) {
    return apiClient.get(`/study-groups/created-by/${userId}`)
  },

  getStudyGroupsByCourse(courseId) {
    return apiClient.get(`/study-groups/course/${courseId}`)
  },

  searchStudyGroups(name) {
    return apiClient.get(`/study-groups/search?name=${encodeURIComponent(name)}`)
  },

  updateStudyGroup(groupId, userId, data) {
    return apiClient.put(`/study-groups/${groupId}?userId=${userId}`, data)
  },

  joinStudyGroup(groupId, userId) {
    return apiClient.post(`/study-groups/${groupId}/join?userId=${userId}`)
  },

  leaveStudyGroup(groupId, userId) {
    return apiClient.post(`/study-groups/${groupId}/leave?userId=${userId}`)
  },

  approveJoinRequest(groupId, requesterId, approverId) {
    return apiClient.post(`/study-groups/${groupId}/approve?requesterId=${requesterId}&approverId=${approverId}`)
  },

  rejectJoinRequest(groupId, requesterId, rejecterId) {
    return apiClient.post(`/study-groups/${groupId}/reject?requesterId=${requesterId}&rejecterId=${rejecterId}`)
  },

  getPendingJoinRequests(groupId) {
    return apiClient.get(`/study-groups/${groupId}/pending-requests`)
  },

  promoteToModerator(groupId, memberId, adminId) {
    return apiClient.post(`/study-groups/${groupId}/promote-moderator?memberId=${memberId}&adminId=${adminId}`)
  },

  promoteToAdmin(groupId, memberId, adminId) {
    return apiClient.post(`/study-groups/${groupId}/promote-admin?memberId=${memberId}&adminId=${adminId}`)
  },

  demoteMember(groupId, memberId, adminId) {
    return apiClient.post(`/study-groups/${groupId}/demote?memberId=${memberId}&adminId=${adminId}`)
  },

  deleteStudyGroup(groupId, userId) {
    return apiClient.delete(`/study-groups/${groupId}?userId=${userId}`)
  },

  getStudyGroupMembers(groupId) {
    return apiClient.get(`/study-groups/${groupId}/members`)
  },

  removeStudyGroupMember(groupId, memberId, removerId) {
    return apiClient.delete(`/study-groups/${groupId}/members/${memberId}?removerId=${removerId}`)
  },

  getStudyGroupMessages(groupId, userId, page = 0, size = 50) {
    return apiClient.get(`/study-groups/${groupId}/messages?userId=${userId}&page=${page}&size=${size}`)
  },

  getRecentStudyGroupMessages(groupId, userId, limit = 50) {
    return apiClient.get(`/study-groups/${groupId}/recent-messages?userId=${userId}&limit=${limit}`)
  },

  sendStudyGroupMessage(groupId, senderId, content) {
    return apiClient.post(`/study-groups/${groupId}/messages?senderId=${senderId}`, { content })
  },

  deleteStudyGroupMessage(messageId, userId) {
    return apiClient.delete(`/study-groups/messages/${messageId}?userId=${userId}`)
  },

  searchStudyGroupMessages(groupId, userId, query) {
    return apiClient.get(`/study-groups/${groupId}/messages/search?userId=${userId}&query=${encodeURIComponent(query)}`)
  },

  getStudyGroupFiles(groupId, userId) {
    return apiClient.get(`/study-groups/${groupId}/files?userId=${userId}`)
  },

  sendGroupMessage(groupId, senderId, content) {
    return apiClient.post(`/study-groups/${groupId}/messages?senderId=${senderId}`, {
      content: content,
      messageType: 'TEXT'
    })
  },

  getStudyGroupResources(groupId) {
    return apiClient.get(`/study-groups/${groupId}/resources`)
  },

  addStudyGroupResource(groupId, data) {
    return apiClient.post(`/study-groups/${groupId}/resources`, data)
  },

  // ========================================
  // MISSING: CONNECTION MANAGEMENT
  // ========================================
  sendConnectionRequest(requesterId, receiverId) {
    return apiClient.post(`/connections/send?requesterId=${requesterId}&receiverId=${receiverId}`)
  },

  acceptConnectionRequest(connectionId, receiverId) {
    return apiClient.post(`/connections/${connectionId}/accept?receiverId=${receiverId}`)
  },

  rejectConnectionRequest(connectionId, receiverId) {
    return apiClient.post(`/connections/${connectionId}/reject?receiverId=${receiverId}`)
  },

  cancelConnectionRequest(connectionId, requesterId) {
    return apiClient.delete(`/connections/${connectionId}/cancel?requesterId=${requesterId}`)
  },

  removeConnection(connectionId, userId) {
    return apiClient.delete(`/connections/${connectionId}?userId=${userId}`)
  },

  blockUser(blockerId, blockedUserId) {
    return apiClient.post(`/connections/block?blockerId=${blockerId}&blockedUserId=${blockedUserId}`)
  },

  unblockUser(blockerId, blockedUserId) {
    return apiClient.post(`/connections/unblock?blockerId=${blockerId}&blockedUserId=${blockedUserId}`)
  },

  getConnectionById(connectionId) {
    return apiClient.get(`/connections/${connectionId}`)
  },

  getSentConnectionRequests(userId) {
    return apiClient.get(`/connections/user/${userId}/sent`)
  },

  getReceivedConnectionRequests(userId) {
    return apiClient.get(`/connections/user/${userId}/received`)
  },

  getBlockedUsers(userId) {
    return apiClient.get(`/connections/user/${userId}/blocked`)
  },

  checkIfConnected(user1Id, user2Id) {
    return apiClient.get(`/connections/check?user1Id=${user1Id}&user2Id=${user2Id}`)
  },

  searchConnectedUsers(userId, query) {
    return apiClient.get(`/connections/user/${userId}/search?query=${encodeURIComponent(query)}`)
  },

  // Get mutual connections between two users
  getMutualConnections(user1Id, user2Id) {
    return apiClient.get(`/connections/mutual?user1Id=${user1Id}&user2Id=${user2Id}`)
  },

  // Get detailed connection status between two users
  getConnectionStatus(user1Id, user2Id) {
    return apiClient.get(`/connections/status?user1Id=${user1Id}&user2Id=${user2Id}`)
  },

  // ========================================
  // MISSING: ADVANCED COURSE MANAGEMENT
  // ========================================
  searchCoursesByName(name) {
    return apiClient.get(`/courses/search/name?query=${encodeURIComponent(name)}`)
  },

  searchCoursesByCode(code) {
    return apiClient.get(`/courses/search/code?query=${encodeURIComponent(code)}`)
  },

  getCoursesByCredits(credits) {
    return apiClient.get(`/courses/credits/${credits}`)
  },

  addCoursePrerequisite(courseId, prerequisiteId) {
    return apiClient.post(`/courses/${courseId}/prerequisites/${prerequisiteId}`)
  },

  removeCoursePrerequisite(courseId, prerequisiteId) {
    return apiClient.delete(`/courses/${courseId}/prerequisites/${prerequisiteId}`)
  },

  getCoursePrerequisites(courseId) {
    return apiClient.get(`/courses/${courseId}/prerequisites`)
  },

  updateCourseSchedule(courseId, daysOfWeek, startTime, endTime) {
    return apiClient.put(`/courses/${courseId}/schedule`, null, {
      params: { daysOfWeek, startTime, endTime }
    })
  },

  // ========================================
  // MISSING: ADVANCED ENROLLMENT MANAGEMENT
  // ========================================
  checkIfEnrolled(studentId, courseId) {
    return apiClient.get(`/enrollments/check?studentId=${studentId}&courseId=${courseId}`)
  },

  getEnrollmentsByStatus(status) {
    return apiClient.get(`/enrollments/status/${status}`)
  },

  getCourseEnrollmentCount(courseId) {
    return apiClient.get(`/enrollments/course/${courseId}/count`)
  },

  // ========================================
  // MISSING: ADVANCED ASSIGNMENT MANAGEMENT
  // ========================================
  getUpcomingAssignments() {
    return apiClient.get('/assignments/upcoming')
  },

  getOverdueAssignments() {
    return apiClient.get('/assignments/overdue')
  },

  getAssignmentsDueBetween(startDate, endDate) {
    return apiClient.get(`/assignments/due-between?startDate=${startDate}&endDate=${endDate}`)
  },

  searchAssignments(title) {
    return apiClient.get(`/assignments/search?title=${encodeURIComponent(title)}`)
  },

  updateAssignment(assignmentId, facultyId, data) {
    return apiClient.put(`/assignments/${assignmentId}?facultyId=${facultyId}`, data)
  },

  deleteAssignment(assignmentId, facultyId) {
    return apiClient.delete(`/assignments/${assignmentId}?facultyId=${facultyId}`)
  },

  getAllActiveAssignments() {
    return apiClient.get('/assignments')
  },

  // ========================================
  // MISSING: ADVANCED SUBMISSION MANAGEMENT
  // ========================================
  resubmitAssignment(assignmentId, studentId, data) {
    return apiClient.post(`/submissions/resubmit?assignmentId=${assignmentId}&studentId=${studentId}`, data)
  },

  returnGradedSubmission(submissionId, facultyId, data) {
    return apiClient.post(`/submissions/${submissionId}/return?facultyId=${facultyId}`, data)
  },

  downloadSubmissionFile(submissionId, userId) {
    return apiClient.get(`/submissions/${submissionId}/download?userId=${userId}`, {
      responseType: 'blob'
    })
  },

  deleteSubmission(submissionId, userId) {
    return apiClient.delete(`/submissions/${submissionId}?userId=${userId}`)
  },

  getStudentSubmissionForAssignment(studentId, assignmentId) {
    return apiClient.get(`/submissions/student/${studentId}/assignment/${assignmentId}`)
  },

  getLateSubmissions(assignmentId) {
    return apiClient.get(`/submissions/assignment/${assignmentId}/late`)
  },

  getUngradedSubmissionsForCourse(courseId) {
    return apiClient.get(`/submissions/course/${courseId}/ungraded`)
  },

  getSubmissionsByStatus(status) {
    return apiClient.get(`/submissions/status/${status}`)
  },

  getSubmissionsByStudentAndCourse(studentId, courseId) {
    return apiClient.get(`/submissions/student/${studentId}/course/${courseId}`)
  },

  checkIfSubmitted(studentId, assignmentId) {
    return apiClient.get(`/submissions/check?studentId=${studentId}&assignmentId=${assignmentId}`)
  },

  getSubmissionCounts(assignmentId) {
    return apiClient.get(`/submissions/assignment/${assignmentId}/count`)
  },

  // ========================================
  // MISSING: ADVANCED GRADE MANAGEMENT
  // ========================================
  getGradeById(gradeId) {
    return apiClient.get(`/grades/${gradeId}`)
  },

  getAllGrades() {
    return apiClient.get('/grades')
  },

  updateGrade(gradeId, gradeValue) {
    return apiClient.put(`/grades/${gradeId}?gradeValue=${gradeValue}`)
  },

  deleteGrade(gradeId) {
    return apiClient.delete(`/grades/${gradeId}`)
  },

  getGradeScale() {
    return apiClient.get('/grades/scale')
  },

  getStudentGPASummary(studentId) {
    return apiClient.get(`/grades/student/${studentId}/summary`)
  },

  calculateStudentGPA(studentId) {
    return apiClient.get(`/grades/student/${studentId}/gpa`)
  },

  // ========================================
  // MISSING: ADVANCED DEGREE PROGRESS
  // ========================================
  updateDegreeProgram(programId, data) {
    return apiClient.put(`/degree-progress/programs/${programId}`, data)
  },

  getActiveDegreePrograms() {
    return apiClient.get('/degree-progress/programs/active')
  },

  getDegreeProgramById(programId) {
    return apiClient.get(`/degree-progress/programs/${programId}`)
  },

  addProgramRequirement(programId, requirementData) {
    return apiClient.post(`/degree-progress/programs/${programId}/requirements`, requirementData)
  },

  updateProgramRequirement(requirementId, data) {
    return apiClient.put(`/degree-progress/requirements/${requirementId}`, data)
  },

  getProgramRequirements(programId) {
    return apiClient.get(`/degree-progress/programs/${programId}/requirements`)
  },

  deleteProgramRequirement(requirementId) {
    return apiClient.delete(`/degree-progress/requirements/${requirementId}`)
  },

  checkGraduationEligibility(studentId) {
    return apiClient.get(`/degree-progress/students/${studentId}/graduation-eligible`)
  },

  getStudentsInProgram(programId) {
    return apiClient.get(`/degree-progress/programs/${programId}/students`)
  },

  updateStudentProgressStatus(studentId, status) {
    return apiClient.patch(`/degree-progress/students/${studentId}/status`, { status })
  },

  getStudentProgressReport(studentId) {
    return apiClient.get(`/degree-progress/students/${studentId}/report`)
  },

  // ========================================
  // MISSING: ADVANCED NOTIFICATION MANAGEMENT
  // ========================================
  getUnreadNotifications(userId) {
    return apiClient.get(`/notifications/unread?userId=${userId}`)
  },

  markAllNotificationsAsRead(userId) {
    return apiClient.patch(`/notifications/read-all?userId=${userId}`)
  },

  deleteNotification(notificationId) {
    return apiClient.delete(`/notifications/${notificationId}`)
  },

  deleteReadNotifications(userId) {
    return apiClient.delete(`/notifications/read?userId=${userId}`)
  },

  getNotificationPreferences(userId) {
    return apiClient.get(`/notifications/preferences?userId=${userId}`)
  },

  updateNotificationPreferences(userId, preferences) {
    return apiClient.put(`/notifications/preferences?userId=${userId}`, preferences)
  },

  // ========================================
  // MISSING: ADVANCED MESSAGE MANAGEMENT
  // ========================================
  markMessageAsReadByUser(messageId, userId) {
    return apiClient.post(`/messages/${messageId}/read?userId=${userId}`)
  },

  markConversationAsReadByUsers(user1Id, user2Id) {
    return apiClient.post(`/messages/conversation/read?user1Id=${user1Id}&user2Id=${user2Id}`)
  },

  getMessageById(messageId) {
    return apiClient.get(`/messages/${messageId}`)
  },

  getConversationBetweenUsers(user1Id, user2Id) {
    return apiClient.get(`/messages/conversation?user1Id=${user1Id}&user2Id=${user2Id}`)
  },

  getPaginatedConversation(user1Id, user2Id, page = 0, size = 50) {
    return apiClient.get(`/messages/conversation/paginated?user1Id=${user1Id}&user2Id=${user2Id}&page=${page}&size=${size}`)
  },

  getRecentConversationMessages(user1Id, user2Id, limit = 50) {
    return apiClient.get(`/messages/conversation/recent?user1Id=${user1Id}&user2Id=${user2Id}&limit=${limit}`)
  },

  getUnreadMessagesForUser(userId) {
    return apiClient.get(`/messages/user/${userId}/unread`)
  },

  getUnreadCountFromUser(receiverId, senderId) {
    return apiClient.get(`/messages/unread-count?receiverId=${receiverId}&senderId=${senderId}`)
  },

  searchConversation(user1Id, user2Id, query) {
    return apiClient.get(`/messages/conversation/search?user1Id=${user1Id}&user2Id=${user2Id}&query=${encodeURIComponent(query)}`)
  },

  deleteMessageByUser(messageId, userId) {
    return apiClient.delete(`/messages/${messageId}?userId=${userId}`)
  },

  // ========================================
  // MISSING: ADVANCED PAYMENT MANAGEMENT
  // ========================================
  deletePayment(paymentId) {
    return apiClient.delete(`/payments/${paymentId}`)
  },

  getPaymentsBySemester(semesterId) {
    return apiClient.get(`/payments/semester/${semesterId}`)
  },

  getStudentPaymentForSemester(studentId, semesterId) {
    return apiClient.get(`/payments/student/${studentId}/semester/${semesterId}`)
  },

  checkIfPaymentApproved(studentId, semesterId) {
    return apiClient.get(`/payments/student/${studentId}/semester/${semesterId}/approved`)
  },

  getOverduePayments() {
    return apiClient.get('/payments/overdue')
  },

  // ========================================
  // MISSING: ADVANCED FILE MANAGEMENT
  // ========================================
  deleteSubmissionFiles(assignmentId, studentId) {
    return apiClient.delete(`/files/delete-submission?assignmentId=${assignmentId}&studentId=${studentId}`)
  },

  checkFileExists(filePath) {
    return apiClient.get(`/files/exists?filePath=${encodeURIComponent(filePath)}`)
  },

  getFileSize(filePath) {
    return apiClient.get(`/files/size?filePath=${encodeURIComponent(filePath)}`)
  },

  // ========================================
  // COURSE MATERIALS MANAGEMENT
  // ========================================
  uploadCourseMaterial(file, courseId, materialType, title) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('courseId', courseId)
    formData.append('materialType', materialType)
    formData.append('title', title)

    return apiClient.post('/files/upload/course-material', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  getCourseMaterials(courseId) {
    return apiClient.get(`/files/course/${courseId}/materials`)
  },

  deleteCourseMaterial(materialId) {
    return apiClient.delete(`/files/course-material/${materialId}`)
  },

  downloadCourseMaterial(materialId) {
    return apiClient.get(`/files/course-material/${materialId}/download`, {
      responseType: 'blob'
    })
  },

  // ========================================
  // COURSE GRADE ANALYTICS
  // ========================================
  getCourseGradeAnalytics(courseId) {
    return apiClient.get(`/grades/course/${courseId}/analytics`)
  },

  getStudentCoursePerformance(studentId, courseId) {
    return apiClient.get(`/grades/student/${studentId}/course/${courseId}/performance`)
  },

  getCourseAttendanceStats(courseId) {
    return apiClient.get(`/attendance/course/${courseId}/statistics`)
  },

  getStudentCourseAttendance(studentId, courseId) {
    return apiClient.get(`/attendance/student/${studentId}/course/${courseId}`)
  },

  // ========================================
  // MISSING: ADVANCED SEMESTER MANAGEMENT
  // ========================================
  getSemesterByCode(code) {
    return apiClient.get(`/semesters/code/${code}`)
  },

  getCurrentSemester() {
    return apiClient.get('/semesters/current')
  },

  activateSemester(semesterId) {
    return apiClient.post(`/semesters/${semesterId}/activate`)
  },

  openSemesterRegistration(semesterId) {
    return apiClient.post(`/semesters/${semesterId}/open-registration`)
  },

  closeSemesterRegistration(semesterId) {
    return apiClient.post(`/semesters/${semesterId}/close-registration`)
  },

  deleteSemester(semesterId) {
    return apiClient.delete(`/semesters/${semesterId}`)
  },

  searchSemesters(name) {
    return apiClient.get(`/semesters/search?name=${encodeURIComponent(name)}`)
  },

  // ========================================
  // MISSING: ADVANCED USER MANAGEMENT
  // ========================================
  getUserByEmail(email) {
    return apiClient.get(`/users/email/${encodeURIComponent(email)}`)
  },

  getUsersByRole(role) {
    return apiClient.get(`/users/role/${role}`)
  },

  // ========================================
  // GENERIC HTTP METHODS
  // ========================================
  get(url, config) {
    return apiClient.get(url, config)
  },

  post(url, data, config) {
    return apiClient.post(url, data, config)
  },

  put(url, data, config) {
    return apiClient.put(url, data, config)
  },

  patch(url, data, config) {
    return apiClient.patch(url, data, config)
  },

  delete(url, config) {
    return apiClient.delete(url, config)
  },

  // ========================================
  // PASSWORD MANAGEMENT
  // ========================================
  changePassword(userId, passwordData) {
    return apiClient.put(`/users/${userId}/change-password`, passwordData)
  },

  // ========================================
  // PROFILE PICTURE UPLOAD
  // ========================================
  uploadProfilePicture(userId, file) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId)

    return apiClient.post('/files/upload/profile', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  getProfilePictureUrl(userId) {
    return `${API_BASE_URL}/files/profile/${userId}`
  },

  // ========================================
  // SYSTEM MONITORING
  // ========================================
  getSystemMetrics() {
    return apiClient.get('/system/metrics')
  },

  getSystemHealth() {
    return apiClient.get('/system/health')
  }
}
