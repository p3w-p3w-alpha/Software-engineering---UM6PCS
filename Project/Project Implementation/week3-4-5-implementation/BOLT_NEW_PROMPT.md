# 🚀 BOLT.NEW PROMPT - SAMS Frontend Build

## 📋 **PROJECT OVERVIEW**

Build a **complete, production-ready frontend** for a **Student Academic Management System (SAMS)** with a stunning, creative, and visually astonishing UI that will captivate all users.

---

## 🎯 **CORE REQUIREMENTS**

### **Tech Stack (MUST USE EXACTLY)**:
- **Framework**: Vue 3 (Composition API with `<script setup>`)
- **State Management**: Pinia
- **Router**: Vue Router 4
- **UI Library**: PrimeVue (latest version)
- **CSS Framework**: Tailwind CSS
- **Icons**: PrimeIcons
- **Build Tool**: Vite
- **HTTP Client**: Axios
- **Animations**: @vueuse/motion, Animate.css
- **Utilities**: @vueuse/core

### **Backend API Details**:
- **Base URL**: `http://localhost:8080/api`
- **Authentication**: JWT Bearer Token (sent in `Authorization` header)
- **Response Format**: JSON
- **CORS**: Enabled for `http://localhost:5173`

---

## 🎨 **DESIGN PHILOSOPHY**

### **YOU HAVE COMPLETE CREATIVE FREEDOM!**

Create an **absolutely stunning, unique, and visually breathtaking** interface that:
- **Captures attention immediately** with bold, modern design
- **Uses cutting-edge design patterns** and trends
- **Feels premium and professional** like top-tier SaaS products
- **Delights users** with smooth animations and micro-interactions
- **Stands out** from generic academic systems

### **Suggested Design Elements** (Feel free to innovate beyond these):

#### **Visual Style**:
- **Glassmorphism** (frosted glass effects with backdrop-blur)
- **Neomorphism** (soft 3D elements with subtle shadows)
- **Gradient Meshes** (complex, animated gradients)
- **Aurora Effects** (flowing, colorful background animations)
- **Particle Systems** (floating elements, confetti, sparkles)
- **3D Transformations** (cards that tilt, lift, and rotate)
- **Holographic Effects** (iridescent, color-shifting elements)
- **Liquid Animations** (smooth, organic transitions)

#### **Color Schemes** (Choose or combine):
- **Cyberpunk**: Neon purples, electric blues, hot pinks
- **Sunset**: Deep oranges, warm yellows, soft purples
- **Ocean**: Deep blues, teals, seafoam greens
- **Forest**: Deep greens, earthy browns, golden accents
- **Galaxy**: Deep purples, blues, pinks with starfield effects
- **Minimalist**: Monochrome with one bold accent color
- **Dark Mode**: True black with vibrant accent colors

#### **Animation Ideas**:
- **Page Transitions**: Morphing shapes, sliding panels, zoom effects
- **Micro-interactions**: Button ripples, hover reveals, magnetic cursors
- **Loading States**: Skeleton screens, shimmer effects, progress particles
- **Data Visualization**: Animated charts, progress rings, stat counters
- **Scroll Effects**: Parallax, fade-in reveals, sticky headers
- **Background**: Animated gradients, floating shapes, particle effects

#### **Modern UI Patterns**:
- **Bento Grid Layouts** (Pinterest-style masonry)
- **Card-Based Design** (elevated, interactive cards)
- **Split Screens** (dual-pane layouts)
- **Floating Action Buttons** (SpeedDial with radial menu)
- **Command Palette** (Cmd+K style quick actions)
- **Toast Notifications** (Elegant, animated messages)
- **Modal Overlays** (Backdrop blur, smooth transitions)
- **Infinite Scroll** (Lazy loading with smooth reveals)

---

## 🏗️ **APPLICATION ARCHITECTURE**

### **Project Structure**:
```
sams-frontend/
├── src/
│   ├── main.js                    # App entry, PrimeVue config
│   ├── App.vue                    # Root component
│   ├── router/
│   │   └── index.js               # Route definitions
│   ├── stores/
│   │   ├── auth.js                # Authentication state
│   │   ├── student.js             # Student data
│   │   ├── faculty.js             # Faculty data
│   │   └── admin.js               # Admin data
│   ├── layouts/
│   │   ├── DashboardLayout.vue    # Persistent sidebar/navbar
│   │   └── AuthLayout.vue         # Login/auth pages
│   ├── views/
│   │   ├── Login.vue              # Stunning login page
│   │   ├── student/               # Student views (15 pages)
│   │   ├── faculty/               # Faculty views (12 pages)
│   │   ├── admin/                 # Admin views (14 pages)
│   │   └── shared/                # Shared views (10 pages)
│   ├── components/
│   │   ├── common/                # Reusable components
│   │   ├── charts/                # Data visualization
│   │   └── ui/                    # Custom UI elements
│   ├── services/
│   │   ├── api.js                 # Axios instance, interceptors
│   │   └── websocket.js           # WebSocket connection
│   ├── utils/
│   │   ├── formatters.js          # Date, currency formatting
│   │   └── validators.js          # Form validation
│   └── assets/
│       ├── styles/
│       │   ├── main.css           # Global styles
│       │   └── animations.css     # Custom animations
│       └── images/
├── package.json
├── vite.config.js
├── tailwind.config.js
└── index.html
```

---

## 🔐 **AUTHENTICATION SYSTEM**

### **Login Page** (BE CREATIVE!):
**Required Features**:
- Email/username input
- Password input with visibility toggle
- "Remember Me" checkbox
- "Forgot Password" link
- Login button with loading state
- Demo account quick-select cards

**Creative Ideas**:
- Animated gradient background with floating shapes
- Glass-morphism login card with backdrop blur
- Social login buttons (Google, GitHub, Microsoft) - UI only
- Particle background that reacts to mouse movement
- 3D card flip animation on submit
- Confetti animation on successful login

### **Authentication API Endpoint**:
```
POST /api/auth/login
Request Body:
{
  "username": "string",
  "password": "string"
}

Response:
{
  "token": "jwt_token_here",
  "user": {
    "id": number,
    "username": "string",
    "name": "string",
    "email": "string",
    "role": "STUDENT" | "FACULTY" | "ADMIN" | "SUPER_ADMIN",
    "avatar": "string (optional)"
  }
}
```

### **Token Management**:
- Store JWT in Pinia auth store
- Add `Authorization: Bearer {token}` to all API requests
- Implement auto-refresh if token expires
- Handle 401 errors by redirecting to login

### **Demo Accounts** (for testing):
```javascript
const demoAccounts = [
  { username: 'superadmin', password: 'Admin@123', role: 'Super Admin' },
  { username: 'student1', password: 'password123', role: 'Student' },
  { username: 'faculty1', password: 'password123', role: 'Faculty' }
]
```

---

## 🎓 **STUDENT MODULE** (15 Pages)

### **1. Student Dashboard** (`/student`)
**Data Sources**:
- `GET /api/student/{studentId}/enrollments` - Active courses
- `GET /api/student/{studentId}/assignments` - Upcoming assignments
- `GET /api/student/{studentId}/grades` - Recent grades
- `GET /api/studygroups/user/{userId}` - Study groups

**Display Elements**:
- Welcome header with student name and avatar
- 4 Stat cards: Enrolled Courses, Pending Assignments, GPA, Study Groups
- Course enrollment cards (status: ACTIVE, COMPLETED, DROPPED)
- Upcoming assignments list (due dates, status)
- Recent grades with GPA calculation
- Quick action buttons (Browse Courses, Submit Assignment, Check Grades)

**Creative Ideas**:
- Animated stat counters that count up on load
- Progress rings for GPA (circular progress)
- Course cards with hover 3D tilt effect
- Timeline view for assignments with due date markers
- Sparkline charts for grade trends

### **2. Course Browse** (`/student/courses/browse`)
**API**: `GET /api/courses/browse?semester={semesterId}`

**Features**:
- Search and filter courses
- Course cards with: name, code, credits, instructor, schedule, capacity
- Enrollment status indicators
- "Enroll Now" button (calls `POST /api/enrollments`)

**Creative Ideas**:
- Bento grid layout (masonry style)
- Animated filter pills that morph
- Hover effect reveals course description
- Skeleton loading with shimmer effect

### **3. My Courses** (`/student/courses`)
**API**: `GET /api/student/{studentId}/enrollments`

**Features**:
- List of enrolled courses
- Click to view course details
- Progress indicators
- Quick links: Assignments, Grades, Materials

**Creative Ideas**:
- Card carousel/slider for courses
- Progress bar with animated gradient fill
- Parallax scrolling effect

### **4. Course Details** (`/student/courses/:id`)
**APIs**:
- `GET /api/courses/{courseId}`
- `GET /api/courses/{courseId}/assignments`
- `GET /api/enrollments/{enrollmentId}/grades`

**Features**:
- Course information
- Instructor details with avatar
- Assignments list
- Grades for this course
- Attendance records

### **5. Assignments** (`/student/assignments`)
**API**: `GET /api/student/{studentId}/assignments`

**Features**:
- Filter: All, Pending, Submitted, Graded, Overdue
- Assignment cards: title, course, due date, status, points
- "Submit" button for pending assignments
- Visual indicators for late submissions

**Creative Ideas**:
- Kanban board view (columns: To Do, In Progress, Submitted, Graded)
- Countdown timers for upcoming due dates
- Color-coded status badges with glow effects

### **6. Assignment Submission** (`/student/assignments/:id/submit`)
**APIs**:
- `GET /api/assignments/{assignmentId}`
- `POST /api/submissions` (with file upload)
- `POST /api/files/upload` (for attachments)

**Features**:
- Assignment details and requirements
- File upload drag-and-drop zone
- Text submission area (rich text editor)
- Comments/notes field
- Submit button with confirmation

**Creative Ideas**:
- Animated file upload with progress bar
- Preview uploaded files
- Confetti animation on successful submission
- Auto-save draft functionality

### **7. Submission History** (`/student/submissions`)
**API**: `GET /api/student/{studentId}/submissions`

**Features**:
- List of all submissions
- Status: Pending, Graded, Returned
- Grades and feedback
- Download submitted files

### **8. Grades** (`/student/grades`)
**APIs**:
- `GET /api/student/{studentId}/grades`
- `GET /api/student/{studentId}/gpa`

**Features**:
- Grades table (Course, Assignment, Grade, Date)
- GPA calculation (overall, per semester)
- Grade distribution chart
- Filter by semester, course

**Creative Ideas**:
- Animated donut chart for GPA
- Bar chart for grade distribution
- Grade trend line chart
- Celebrate high grades with particle effects

### **9. Transcript** (`/student/transcript`)
**API**: `GET /api/student/{studentId}/transcript`

**Features**:
- Official transcript view
- Semester-wise grades
- Cumulative GPA
- Print/Download PDF button

**Creative Ideas**:
- Clean, printable layout
- Watermark effect for authenticity
- Download animation

### **10. Attendance** (`/student/attendance`)
**API**: `GET /api/student/{studentId}/attendance`

**Features**:
- Attendance records by course
- Calendar view with present/absent indicators
- Attendance percentage
- Warning for low attendance

**Creative Ideas**:
- Heat map calendar (green = present, red = absent)
- Circular progress for attendance percentage
- Monthly view with interactive calendar

### **11. Schedule** (`/student/schedule`)
**API**: `GET /api/student/{studentId}/schedule`

**Features**:
- Weekly class schedule
- Day/Week/Month views
- Course timings and locations
- Upcoming classes highlight

**Creative Ideas**:
- Interactive calendar with drag-to-scroll
- Color-coded courses
- Timeline view with current time indicator
- Export to Google Calendar

### **12. Fees/Payments** (`/student/fees`)
**APIs**:
- `GET /api/fees/student/{studentId}`
- `GET /api/payments/student/{studentId}`
- `POST /api/payments/initiate`

**Features**:
- Fee structure breakdown
- Payment history
- Pending payments with due dates
- Pay now button (initiates payment)
- Download receipt

**Creative Ideas**:
- Stacked bar chart for fee breakdown
- Payment timeline
- Receipt preview modal

### **13. Study Groups** (`/student/study-groups`)
**APIs**:
- `GET /api/studygroups/browse`
- `GET /api/studygroups/user/{userId}`
- `POST /api/studygroups` (create)
- `POST /api/studygroups/{groupId}/join`

**Features**:
- Browse public study groups
- My study groups
- Create new study group
- Join/leave groups
- Group chat (WebSocket)

**Creative Ideas**:
- Grid of group cards with member avatars
- Live member count animation
- Chat interface with message bubbles
- Typing indicators

### **14. Messages** (`/student/messages`)
**APIs**:
- `GET /api/messages/conversations/{userId}`
- `GET /api/messages/{conversationId}`
- `POST /api/messages/send`
- WebSocket: `ws://localhost:8080/ws` (for real-time)

**Features**:
- Conversation list (sidebar)
- Message thread
- Send message (text, attachments)
- Read/unread indicators
- Real-time message delivery (WebSocket)

**Creative Ideas**:
- iMessage-style bubbles
- Typing animation dots
- Message reactions
- Smooth scroll to bottom on new message

### **15. Profile** (`/student/profile`)
**APIs**:
- `GET /api/users/{userId}`
- `PUT /api/users/{userId}/profile`
- `POST /api/files/upload` (avatar)

**Features**:
- View/edit profile information
- Upload profile picture
- Change password
- Notification preferences
- Privacy settings

**Creative Ideas**:
- Avatar upload with crop tool
- Profile completion percentage
- Settings organized in tabs
- Dark mode toggle

---

## 👨‍🏫 **FACULTY MODULE** (12 Pages)

### **1. Faculty Dashboard** (`/faculty`)
**APIs**:
- `GET /api/faculty/{facultyId}/courses`
- `GET /api/faculty/{facultyId}/assignments`
- `GET /api/faculty/{facultyId}/submissions`
- `GET /api/faculty/{facultyId}/students`

**Display**:
- 4 Stat cards: My Courses, Total Students, Assignments, Pending Grades
- Course cards with student counts
- Recent submissions to grade
- Quick actions (Create Assignment, Take Attendance, Post Grades)

**Creative Ideas**:
- TabView for organizing content (Courses, Assignments, Submissions, Students)
- Student directory with avatars and info cards
- Calendar widget for upcoming classes

### **2. My Courses** (`/faculty/courses`)
**API**: `GET /api/faculty/{facultyId}/courses`

**Features**:
- List of assigned courses
- Student enrollment count
- Manage course materials
- View course schedule

### **3. Course Details** (`/faculty/courses/:id`)
**APIs**:
- `GET /api/courses/{courseId}`
- `GET /api/courses/{courseId}/enrollments`
- `GET /api/courses/{courseId}/assignments`

**Features**:
- Course information
- Enrolled students list
- Assignments for this course
- Attendance records
- Grade book

### **4. Assignments** (`/faculty/assignments`)
**API**: `GET /api/faculty/{facultyId}/assignments`

**Features**:
- List of all assignments
- Filter by course, status
- Create new assignment button
- Edit/delete assignments

### **5. Create/Edit Assignment** (`/faculty/assignments/create`, `/faculty/assignments/:id/edit`)
**APIs**:
- `POST /api/assignments`
- `PUT /api/assignments/{assignmentId}`
- `POST /api/files/upload`

**Features**:
- Form: title, description, course, due date, total points
- File attachments
- Submission type (file, text, both)
- Late submission policy

**Creative Ideas**:
- Rich text editor for description
- Date picker with time selector
- File upload drag-and-drop

### **6. Submissions** (`/faculty/submissions`)
**API**: `GET /api/faculty/{facultyId}/submissions`

**Features**:
- List of student submissions
- Filter: Pending, Graded, Late
- Quick grade entry
- Bulk actions

**Creative Ideas**:
- Table with inline grade input
- Status badges with colors
- Download all submissions button

### **7. Grade Submission** (`/faculty/submissions/:id/grade`)
**APIs**:
- `GET /api/submissions/{submissionId}`
- `PUT /api/submissions/{submissionId}/grade`

**Features**:
- View submission details
- Download submitted files
- Grade input (0-100 or letter grade)
- Feedback textarea
- Save/submit grade

**Creative Ideas**:
- Side-by-side: submission view | grading form
- Rubric-based grading (if implemented in backend)
- Auto-save draft grades

### **8. Grade Management** (`/faculty/grades`)
**APIs**:
- `GET /api/faculty/{facultyId}/grades`
- `POST /api/grades/bulk`

**Features**:
- Grade book view
- Filter by course, semester
- Bulk grade entry
- Export grades (CSV)

**Creative Ideas**:
- Spreadsheet-like table
- Inline editing
- Color-coded grades (red < 60, yellow 60-79, green 80+)

### **9. Attendance** (`/faculty/attendance`)
**APIs**:
- `GET /api/faculty/{facultyId}/attendance`
- `POST /api/attendance/mark`

**Features**:
- Select course and date
- Student roster with checkboxes
- Mark present/absent/late
- Submit attendance

**Creative Ideas**:
- Quick "Mark All Present" button
- Visual roster with student photos
- Attendance percentage per student

### **10. Schedule** (`/faculty/schedule`)
**API**: `GET /api/faculty/{facultyId}/schedule`

**Features**:
- Teaching schedule (weekly view)
- Office hours
- Upcoming classes

### **11. Students** (`/faculty/students`)
**API**: `GET /api/faculty/{facultyId}/students`

**Features**:
- List of all students in faculty's courses
- Search and filter
- View student profile
- Contact student (message)

**Creative Ideas**:
- Grid of student cards with avatars
- Alphabetical quick jump
- Export student list

### **12. Messages** (`/faculty/messages`)
**Same as Student Messages**

---

## 🔧 **ADMIN MODULE** (14 Pages)

### **1. Admin Dashboard** (`/admin`)
**APIs**:
- `GET /api/admin/stats`
- `GET /api/admin/analytics`

**Display**:
- 6 Stat cards: Total Students, Total Faculty, Total Courses, Active Enrollments, Pending Payments, System Health
- Charts: Enrollment trends, Payment status, User activity
- Recent activity feed
- Quick actions (Add User, Create Course, Approve Payment)

**Creative Ideas**:
- Animated dashboard widgets
- Real-time data updates
- Interactive charts (Chart.js or Recharts)

### **2. User Management** (`/admin/users`)
**APIs**:
- `GET /api/admin/users`
- `POST /api/admin/users`
- `PUT /api/admin/users/{userId}`
- `DELETE /api/admin/users/{userId}`

**Features**:
- User table with: name, email, role, status
- Add new user
- Edit user details
- Activate/deactivate users
- Reset password

**Creative Ideas**:
- Advanced filters (role, status, date)
- Bulk actions (select multiple, change role)
- User details modal

### **3. Course Management** (`/admin/courses`)
**APIs**:
- `GET /api/courses`
- `POST /api/courses`
- `PUT /api/courses/{courseId}`
- `DELETE /api/courses/{courseId}`

**Features**:
- Course table
- Add/edit/delete courses
- Assign faculty to courses
- Set course capacity

### **4. Enrollment Management** (`/admin/enrollments`)
**APIs**:
- `GET /api/enrollments`
- `POST /api/enrollments`
- `PUT /api/enrollments/{enrollmentId}`
- `DELETE /api/enrollments/{enrollmentId}`

**Features**:
- Enrollment records
- Bulk enrollment (import CSV)
- Manual enrollment
- Drop students from courses

### **5. Fee Management** (`/admin/fees`)
**APIs**:
- `GET /api/fees`
- `POST /api/fees/structure`
- `PUT /api/fees/{feeId}`

**Features**:
- Fee structure setup
- Set fees per program/semester
- Fee waivers and discounts
- Fee report

### **6. Payment Approval** (`/admin/payments`)
**APIs**:
- `GET /api/payments/pending`
- `PUT /api/payments/{paymentId}/approve`
- `PUT /api/payments/{paymentId}/reject`

**Features**:
- Pending payments list
- Approve/reject payments
- Payment history
- Generate receipts

### **7. Attendance Management** (`/admin/attendance`)
**API**: `GET /api/admin/attendance`

**Features**:
- View attendance reports
- Filter by course, date range
- Identify low attendance students

### **8. Reports** (`/admin/reports`)
**APIs**:
- `GET /api/admin/reports/enrollment`
- `GET /api/admin/reports/grades`
- `GET /api/admin/reports/fees`
- `GET /api/admin/reports/attendance`

**Features**:
- Generate various reports
- Export to PDF/CSV
- Date range filters

**Creative Ideas**:
- Report templates
- Downloadable charts
- Scheduled reports (email)

### **9. Analytics Dashboard** (`/admin/analytics`)
**API**: `GET /api/admin/analytics`

**Features**:
- Enrollment trends (line chart)
- Grade distribution (bar chart)
- Payment status (pie chart)
- User activity (heat map)
- Department-wise stats

**Creative Ideas**:
- Interactive charts with drill-down
- Comparison views (current vs previous semester)
- Exportable dashboards

### **10. Teacher Management** (`/admin/teachers`)
**APIs**:
- `GET /api/teachers`
- `POST /api/teachers`
- `PUT /api/teachers/{teacherId}`

**Features**:
- Faculty directory
- Assign courses to faculty
- View teaching load
- Office hours management

### **11. Semester Management** (`/admin/semesters`)
**APIs**:
- `GET /api/semesters`
- `POST /api/semesters`
- `PUT /api/semesters/{semesterId}`

**Features**:
- Create/edit semesters
- Set start/end dates
- Activate/deactivate semesters
- Set current semester

### **12. System Settings** (`/admin/settings`)
**APIs**:
- `GET /api/admin/settings`
- `PUT /api/admin/settings`

**Features**:
- General settings (app name, logo, timezone)
- Email settings
- Notification settings
- Security settings

### **13. System Health Monitor** (`/admin/system-health`)
**APIs**:
- `GET /api/admin/health`
- `GET /api/actuator/health` (Spring Boot Actuator)

**Features**:
- Server status
- Database connection
- API response times
- Error logs
- Performance metrics

**Creative Ideas**:
- Real-time status indicators (green/yellow/red)
- Performance graphs
- Alert system for issues

### **14. Notifications** (`/admin/notifications`)
**APIs**:
- `POST /api/notifications/broadcast`
- `GET /api/notifications/templates`

**Features**:
- Send broadcast notifications
- Create notification templates
- Schedule notifications
- View notification history

---

## 🌐 **SHARED VIEWS** (10 Pages)

### **1. User Profile** (`/profile`, `/profile/:id`)
**APIs**:
- `GET /api/users/{userId}`
- `PUT /api/users/{userId}/profile`

**Features**:
- View/edit own profile or view others
- Avatar, bio, contact info
- Social connections

### **2. Settings** (`/settings`)
**API**: `GET /api/users/{userId}/preferences`

**Features**:
- Account settings
- Notification preferences
- Privacy settings
- Change password
- Dark mode toggle

### **3. Notifications** (`/notifications`)
**API**: `GET /api/notifications/user/{userId}`

**Features**:
- List of all notifications
- Mark as read/unread
- Filter by type
- Delete notifications

### **4. Social Connections** (`/connections`)
**APIs**:
- `GET /api/connections/user/{userId}`
- `POST /api/connections/request`
- `PUT /api/connections/{connectionId}/accept`

**Features**:
- Connection requests
- Friends/connections list
- Search users
- Send connection request

### **5. Study Groups** (`/studygroups`)
**Same as Student Study Groups** (accessible to all roles)

### **6. Messages** (`/messages`)
**Shared messaging system**

### **7. Events/Calendar** (`/events`)
**APIs**:
- `GET /api/events`
- `POST /api/events` (admin/faculty)

**Features**:
- Campus events calendar
- Event details
- RSVP to events
- Add to personal calendar

### **8. File Manager** (`/files`)
**API**: `GET /api/files/user/{userId}`

**Features**:
- View uploaded files
- Download files
- Delete files
- File sharing

### **9. Help/Support** (`/help`)
**Features**:
- FAQ section
- Contact support form
- User guide/documentation
- Video tutorials

### **10. 404 Page** (`/*`)
**Features**:
- Beautiful 404 design
- Search bar
- Helpful links
- Back to home button

---

## 🧩 **REUSABLE COMPONENTS**

### **Common Components**:
1. **Navbar** - Top navigation with logo, search, notifications, user menu
2. **Sidebar** - Persistent navigation menu (role-based)
3. **Footer** - App info, links, copyright
4. **StatCard** - Dashboard statistic card
5. **CourseCard** - Display course info
6. **AssignmentCard** - Display assignment
7. **UserAvatar** - User profile picture with fallback initials
8. **Badge** - Status badges (color-coded)
9. **LoadingSpinner** - Loading indicator
10. **EmptyState** - No data placeholder
11. **ConfirmDialog** - Confirmation modal
12. **Toast** - Notification toast
13. **Breadcrumbs** - Navigation breadcrumb trail
14. **SearchBar** - Global search
15. **DataTable** - Sortable, filterable table
16. **Pagination** - Page navigation
17. **FileUpload** - Drag-and-drop file upload
18. **RichTextEditor** - WYSIWYG editor
19. **DatePicker** - Date/time selector
20. **ProgressBar** - Progress indicator

### **Chart Components**:
1. **LineChart** - Trend visualization
2. **BarChart** - Comparison bars
3. **PieChart** - Distribution pie/donut
4. **AreaChart** - Filled area chart
5. **RadarChart** - Multi-axis comparison

---

## 🔌 **API INTEGRATION GUIDE**

### **Axios Setup** (`src/services/api.js`):
```javascript
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - Add auth token
api.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor - Handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

export default api
```

### **API Service Functions** (Examples):
```javascript
// Authentication
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  logout: () => api.post('/auth/logout'),
  validate: () => api.get('/auth/validate')
}

// Student
export const studentAPI = {
  getEnrollments: (studentId) => api.get(`/student/${studentId}/enrollments`),
  getAssignments: (studentId) => api.get(`/student/${studentId}/assignments`),
  getGrades: (studentId) => api.get(`/student/${studentId}/grades`),
  getGPA: (studentId) => api.get(`/student/${studentId}/gpa`)
}

// Courses
export const courseAPI = {
  browse: (params) => api.get('/courses/browse', { params }),
  getById: (courseId) => api.get(`/courses/${courseId}`),
  enroll: (data) => api.post('/enrollments', data)
}

// Assignments
export const assignmentAPI = {
  getById: (assignmentId) => api.get(`/assignments/${assignmentId}`),
  create: (data) => api.post('/assignments', data),
  update: (assignmentId, data) => api.put(`/assignments/${assignmentId}`, data),
  delete: (assignmentId) => api.delete(`/assignments/${assignmentId}`)
}

// Submissions
export const submissionAPI = {
  submit: (data) => api.post('/submissions', data),
  getById: (submissionId) => api.get(`/submissions/${submissionId}`),
  grade: (submissionId, gradeData) => api.put(`/submissions/${submissionId}/grade`, gradeData)
}

// File uploads
export const fileAPI = {
  upload: (formData) => api.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  download: (fileId) => api.get(`/files/${fileId}`, { responseType: 'blob' })
}
```

---

## 🔄 **STATE MANAGEMENT** (Pinia Stores)

### **Auth Store** (`src/stores/auth.js`):
```javascript
import { defineStore } from 'pinia'
import { authAPI } from '@/services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    user: JSON.parse(localStorage.getItem('user')) || null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    isStudent: (state) => state.user?.role === 'STUDENT',
    isFaculty: (state) => state.user?.role === 'FACULTY',
    isAdmin: (state) => ['ADMIN', 'SUPER_ADMIN'].includes(state.user?.role),
    userName: (state) => state.user?.name || '',
    userRole: (state) => state.user?.role || '',
    userId: (state) => state.user?.id
  },

  actions: {
    async login(credentials) {
      const { data } = await authAPI.login(credentials)
      this.token = data.token
      this.user = data.user
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
    },

    logout() {
      this.token = null
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
```

### **Student Store** (Example):
```javascript
import { defineStore } from 'pinia'
import { studentAPI } from '@/services/api'

export const useStudentStore = defineStore('student', {
  state: () => ({
    enrollments: [],
    assignments: [],
    grades: [],
    gpa: null,
    loading: false
  }),

  actions: {
    async fetchDashboardData(studentId) {
      this.loading = true
      try {
        const [enrollments, assignments, grades, gpa] = await Promise.all([
          studentAPI.getEnrollments(studentId),
          studentAPI.getAssignments(studentId),
          studentAPI.getGrades(studentId),
          studentAPI.getGPA(studentId)
        ])
        this.enrollments = enrollments.data
        this.assignments = assignments.data
        this.grades = grades.data
        this.gpa = gpa.data
      } finally {
        this.loading = false
      }
    }
  }
})
```

---

## 🧭 **ROUTING** (Vue Router)

### **Route Structure**:
```javascript
const routes = [
  {
    path: '/login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },

  // Student Routes
  {
    path: '/student',
    component: DashboardLayout,
    meta: { requiresAuth: true, roles: ['STUDENT'] },
    children: [
      { path: '', component: () => import('@/views/student/Dashboard.vue') },
      { path: 'courses', component: () => import('@/views/student/Courses.vue') },
      { path: 'courses/browse', component: () => import('@/views/student/CourseBrowse.vue') },
      // ... more routes
    ]
  },

  // Faculty Routes
  {
    path: '/faculty',
    component: DashboardLayout,
    meta: { requiresAuth: true, roles: ['FACULTY'] },
    children: [
      { path: '', component: () => import('@/views/faculty/Dashboard.vue') },
      // ... more routes
    ]
  },

  // Admin Routes
  {
    path: '/admin',
    component: DashboardLayout,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] },
    children: [
      { path: '', component: () => import('@/views/admin/Dashboard.vue') },
      // ... more routes
    ]
  },

  // 404
  {
    path: '/:pathMatch(.*)*',
    component: () => import('@/views/NotFound.vue')
  }
]
```

### **Navigation Guards**:
```javascript
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.roles && !to.meta.roles.includes(authStore.userRole)) {
    // Redirect to appropriate dashboard
    if (authStore.isStudent) next('/student')
    else if (authStore.isFaculty) next('/faculty')
    else if (authStore.isAdmin) next('/admin')
    else next('/login')
  } else {
    next()
  }
})
```

---

## 🎨 **STYLING GUIDELINES**

### **Tailwind Config** (`tailwind.config.js`):
```javascript
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: { /* Your custom colors */ },
        secondary: { /* Your custom colors */ },
        accent: { /* Your custom colors */ }
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-in-out',
        'slide-up': 'slideUp 0.3s ease-out',
        'bounce-in': 'bounceIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55)'
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' }
        },
        slideUp: {
          '0%': { transform: 'translateY(100%)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' }
        },
        bounceIn: {
          '0%': { transform: 'scale(0.3)', opacity: '0' },
          '50%': { transform: 'scale(1.05)' },
          '70%': { transform: 'scale(0.9)' },
          '100%': { transform: 'scale(1)', opacity: '1' }
        }
      }
    }
  },
  plugins: []
}
```

### **Global Styles** (`src/assets/styles/main.css`):
```css
@tailwind base;
@tailwind components;
@tailwind utilities;

/* Custom utility classes */
@layer components {
  .glass-card {
    @apply bg-white/80 backdrop-blur-xl border border-white/20 shadow-xl rounded-2xl;
  }

  .btn-primary {
    @apply bg-gradient-to-r from-indigo-600 to-purple-600 text-white px-6 py-3 rounded-xl font-medium hover:shadow-lg transition-all duration-300 hover:scale-105;
  }

  .card-hover {
    @apply transition-all duration-300 hover:-translate-y-1 hover:shadow-2xl;
  }
}

/* Smooth scrolling */
html {
  scroll-behavior: smooth;
}

/* Hide scrollbar but keep functionality */
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}
.hide-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
```

---

## 🌐 **WEBSOCKET INTEGRATION**

### **WebSocket Service** (`src/services/websocket.js`):
```javascript
import { useAuthStore } from '@/stores/auth'

class WebSocketService {
  constructor() {
    this.ws = null
    this.reconnectInterval = 5000
    this.handlers = {}
  }

  connect() {
    const authStore = useAuthStore()
    const token = authStore.token

    this.ws = new WebSocket(`ws://localhost:8080/ws?token=${token}`)

    this.ws.onopen = () => {
      console.log('WebSocket connected')
    }

    this.ws.onmessage = (event) => {
      const message = JSON.parse(event.data)
      const handler = this.handlers[message.type]
      if (handler) handler(message.payload)
    }

    this.ws.onerror = (error) => {
      console.error('WebSocket error:', error)
    }

    this.ws.onclose = () => {
      console.log('WebSocket disconnected')
      setTimeout(() => this.connect(), this.reconnectInterval)
    }
  }

  on(type, handler) {
    this.handlers[type] = handler
  }

  send(type, payload) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({ type, payload }))
    }
  }

  disconnect() {
    this.ws?.close()
  }
}

export default new WebSocketService()
```

### **Usage in Components**:
```javascript
import websocket from '@/services/websocket'

// In component setup
onMounted(() => {
  websocket.on('NEW_MESSAGE', (message) => {
    // Handle new message
  })

  websocket.on('NOTIFICATION', (notification) => {
    // Show toast notification
  })
})
```

---

## 📱 **RESPONSIVE DESIGN**

### **Breakpoints** (Tailwind):
- `sm`: 640px
- `md`: 768px
- `lg`: 1024px
- `xl`: 1280px
- `2xl`: 1536px

### **Mobile-First Approach**:
- Design for mobile first, then scale up
- Sidebar: Drawer on mobile, fixed on desktop
- Tables: Horizontal scroll or card view on mobile
- Forms: Full-width on mobile, multi-column on desktop

### **Example Responsive Classes**:
```html
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
  <!-- 1 column on mobile, 2 on tablet, 4 on desktop -->
</div>

<div class="text-sm md:text-base lg:text-lg">
  <!-- Responsive text size -->
</div>

<div class="hidden md:block">
  <!-- Hide on mobile, show on desktop -->
</div>
```

---

## ⚡ **PERFORMANCE OPTIMIZATIONS**

### **Must Implement**:
1. **Lazy Loading**: Load route components on demand
2. **Skeleton Screens**: Show loading placeholders
3. **Debounce/Throttle**: Search inputs, scroll events
4. **Virtual Scrolling**: For long lists
5. **Image Optimization**: Lazy load images, use WebP
6. **Code Splitting**: Split vendor and app code
7. **Caching**: Cache API responses with proper invalidation
8. **Memoization**: Use `computed` for derived state

### **Vite Config** (`vite.config.js`):
```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    }
  },
  build: {
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor': ['vue', 'vue-router', 'pinia'],
          'primevue': ['primevue/config', 'primeicons']
        }
      }
    }
  }
})
```

---

## 🎯 **MUST-HAVE FEATURES**

### **Essential UX Elements**:
1. ✅ **Loading States** - Skeleton loaders for all async operations
2. ✅ **Error Handling** - Toast notifications for errors
3. ✅ **Empty States** - Helpful messages when no data
4. ✅ **Confirmation Dialogs** - Before destructive actions
5. ✅ **Form Validation** - Real-time validation with clear errors
6. ✅ **Success Feedback** - Animations/toasts on successful actions
7. ✅ **Keyboard Shortcuts** - Common actions (Ctrl+K for search)
8. ✅ **Breadcrumbs** - Clear navigation hierarchy
9. ✅ **Search** - Global search with keyboard focus
10. ✅ **Dark Mode** - Toggle between light/dark themes

### **Accessibility**:
1. ✅ **Semantic HTML** - Proper heading hierarchy
2. ✅ **ARIA Labels** - Screen reader support
3. ✅ **Keyboard Navigation** - All actions accessible via keyboard
4. ✅ **Focus Indicators** - Visible focus states
5. ✅ **Color Contrast** - WCAG AA compliance

---

## 🚀 **CREATIVE FREEDOM EXAMPLES**

### **You can implement (but not limited to)**:

#### **Advanced Animations**:
- Page transition morphing (FLIP animations)
- Parallax scrolling effects
- Particle systems on hover/click
- Confetti on achievements (high grades, completed course)
- Loading animations (custom spinners, progress bars)
- Chart animations (count-up numbers, animated bars)

#### **Unique Layouts**:
- Bento grid dashboard (asymmetric cards)
- Kanban board for assignments
- Timeline view for submissions
- Calendar with custom event cards
- Split-screen layouts
- Masonry grid for course browse

#### **Interactive Elements**:
- Magnetic buttons (follow cursor)
- Tilt cards (3D effect on hover)
- Swipe gestures on mobile
- Drag-and-drop file uploads
- Sortable lists
- Color pickers for themes

#### **Visual Enhancements**:
- Gradient meshes (animated backgrounds)
- Blob shapes (organic, flowing elements)
- SVG illustrations
- Lottie animations
- Neumorphism buttons
- Glassmorphism panels
- Holographic effects

#### **Smart Features**:
- Command palette (Cmd+K quick actions)
- Keyboard shortcuts overlay
- Quick filters with chips
- Bulk actions with selection
- Export to PDF/CSV with styling
- Print-friendly views
- PWA capabilities (installable app)

---

## 📋 **COMPLETE BACKEND API ENDPOINTS**

### **Authentication**:
- `POST /api/auth/login` - Login
- `POST /api/auth/logout` - Logout
- `GET /api/auth/validate` - Validate token

### **User Management**:
- `GET /api/users/{userId}` - Get user by ID
- `PUT /api/users/{userId}/profile` - Update profile
- `GET /api/admin/users` - Get all users (admin)
- `POST /api/admin/users` - Create user (admin)
- `PUT /api/admin/users/{userId}` - Update user (admin)
- `DELETE /api/admin/users/{userId}` - Delete user (admin)

### **Courses**:
- `GET /api/courses` - Get all courses
- `GET /api/courses/{courseId}` - Get course by ID
- `GET /api/courses/browse` - Browse courses (with filters)
- `POST /api/courses` - Create course (admin)
- `PUT /api/courses/{courseId}` - Update course (admin)
- `DELETE /api/courses/{courseId}` - Delete course (admin)
- `GET /api/courses/{courseId}/assignments` - Get course assignments
- `GET /api/courses/{courseId}/enrollments` - Get course enrollments

### **Enrollments**:
- `GET /api/enrollments` - Get all enrollments
- `GET /api/enrollments/{enrollmentId}` - Get enrollment by ID
- `GET /api/student/{studentId}/enrollments` - Get student enrollments
- `POST /api/enrollments` - Enroll in course
- `PUT /api/enrollments/{enrollmentId}` - Update enrollment
- `DELETE /api/enrollments/{enrollmentId}` - Drop course

### **Assignments**:
- `GET /api/assignments` - Get all assignments
- `GET /api/assignments/{assignmentId}` - Get assignment by ID
- `GET /api/student/{studentId}/assignments` - Get student assignments
- `GET /api/faculty/{facultyId}/assignments` - Get faculty assignments
- `POST /api/assignments` - Create assignment (faculty)
- `PUT /api/assignments/{assignmentId}` - Update assignment (faculty)
- `DELETE /api/assignments/{assignmentId}` - Delete assignment (faculty)

### **Submissions**:
- `GET /api/submissions/{submissionId}` - Get submission by ID
- `GET /api/student/{studentId}/submissions` - Get student submissions
- `GET /api/faculty/{facultyId}/submissions` - Get faculty submissions
- `POST /api/submissions` - Submit assignment
- `PUT /api/submissions/{submissionId}/grade` - Grade submission (faculty)

### **Grades**:
- `GET /api/grades` - Get all grades
- `GET /api/grades/{gradeId}` - Get grade by ID
- `GET /api/student/{studentId}/grades` - Get student grades
- `GET /api/student/{studentId}/gpa` - Get student GPA
- `GET /api/faculty/{facultyId}/grades` - Get faculty grades
- `POST /api/grades` - Create grade (faculty)
- `PUT /api/grades/{gradeId}` - Update grade (faculty)
- `POST /api/grades/bulk` - Bulk grade entry (faculty)

### **Attendance**:
- `GET /api/attendance` - Get all attendance records
- `GET /api/student/{studentId}/attendance` - Get student attendance
- `GET /api/faculty/{facultyId}/attendance` - Get faculty attendance
- `POST /api/attendance/mark` - Mark attendance (faculty)

### **Fees & Payments**:
- `GET /api/fees` - Get all fee structures
- `GET /api/fees/student/{studentId}` - Get student fees
- `POST /api/fees/structure` - Create fee structure (admin)
- `PUT /api/fees/{feeId}` - Update fee (admin)
- `GET /api/payments` - Get all payments
- `GET /api/payments/student/{studentId}` - Get student payments
- `GET /api/payments/pending` - Get pending payments (admin)
- `POST /api/payments/initiate` - Initiate payment
- `PUT /api/payments/{paymentId}/approve` - Approve payment (admin)
- `PUT /api/payments/{paymentId}/reject` - Reject payment (admin)

### **Study Groups**:
- `GET /api/studygroups` - Get all study groups
- `GET /api/studygroups/{groupId}` - Get study group by ID
- `GET /api/studygroups/browse` - Browse public groups
- `GET /api/studygroups/user/{userId}` - Get user's groups
- `POST /api/studygroups` - Create study group
- `PUT /api/studygroups/{groupId}` - Update study group
- `DELETE /api/studygroups/{groupId}` - Delete study group
- `POST /api/studygroups/{groupId}/join` - Join group
- `POST /api/studygroups/{groupId}/leave` - Leave group

### **Messages**:
- `GET /api/messages/conversations/{userId}` - Get user conversations
- `GET /api/messages/{conversationId}` - Get messages in conversation
- `POST /api/messages/send` - Send message
- `PUT /api/messages/{messageId}/read` - Mark as read

### **Notifications**:
- `GET /api/notifications/user/{userId}` - Get user notifications
- `PUT /api/notifications/{notificationId}/read` - Mark as read
- `POST /api/notifications/broadcast` - Send broadcast (admin)

### **Files**:
- `POST /api/files/upload` - Upload file
- `GET /api/files/{fileId}` - Download file
- `GET /api/files/user/{userId}` - Get user files
- `DELETE /api/files/{fileId}` - Delete file

### **Dashboard**:
- `GET /api/dashboard/student/{studentId}` - Student dashboard data
- `GET /api/dashboard/faculty/{facultyId}` - Faculty dashboard data
- `GET /api/dashboard/admin` - Admin dashboard data

### **Admin Analytics**:
- `GET /api/admin/stats` - System statistics
- `GET /api/admin/analytics` - Analytics data
- `GET /api/admin/reports/enrollment` - Enrollment report
- `GET /api/admin/reports/grades` - Grades report
- `GET /api/admin/reports/fees` - Fees report
- `GET /api/admin/reports/attendance` - Attendance report

### **Degree Progress**:
- `GET /api/degree/student/{studentId}/progress` - Get degree progress
- `GET /api/degree/programs` - Get degree programs
- `GET /api/degree/requirements/{programId}` - Get degree requirements

### **Teachers**:
- `GET /api/teachers` - Get all teachers
- `GET /api/teachers/{teacherId}` - Get teacher by ID
- `POST /api/teachers` - Create teacher (admin)
- `PUT /api/teachers/{teacherId}` - Update teacher (admin)
- `GET /api/teachers/{teacherId}/schedule` - Get teacher schedule

### **Semesters**:
- `GET /api/semesters` - Get all semesters
- `GET /api/semesters/{semesterId}` - Get semester by ID
- `POST /api/semesters` - Create semester (admin)
- `PUT /api/semesters/{semesterId}` - Update semester (admin)

### **Connections**:
- `GET /api/connections/user/{userId}` - Get user connections
- `POST /api/connections/request` - Send connection request
- `PUT /api/connections/{connectionId}/accept` - Accept request
- `PUT /api/connections/{connectionId}/reject` - Reject request

### **Events**:
- `GET /api/events` - Get all events
- `POST /api/events` - Create event (admin/faculty)
- `PUT /api/events/{eventId}` - Update event
- `DELETE /api/events/{eventId}` - Delete event

### **WebSocket**:
- `ws://localhost:8080/ws` - WebSocket endpoint for real-time updates

---

## 🎨 **DESIGN INSPIRATION SOURCES**

Use these for inspiration (don't copy exactly):
- **Vercel Dashboard** - Clean, minimal, fast
- **Linear** - Beautiful animations, dark mode
- **Notion** - Intuitive, organized, smooth
- **Stripe Dashboard** - Professional, data-heavy
- **Figma** - Collaborative, modern UI
- **Discord** - Chat interface, user-friendly
- **Dribbble/Behance** - Search "academic dashboard" for ideas

---

## ✅ **FINAL CHECKLIST**

### **Before Starting**:
- [ ] Review all API endpoints
- [ ] Understand the entity relationships
- [ ] Plan the color scheme and design system
- [ ] Set up project with Vite + Vue 3

### **During Development**:
- [ ] Implement authentication first
- [ ] Build reusable components early
- [ ] Test API integration as you go
- [ ] Add loading states for all async operations
- [ ] Handle errors gracefully with toasts
- [ ] Make it responsive (mobile-first)

### **Before Completion**:
- [ ] Test all user flows (student, faculty, admin)
- [ ] Verify all API endpoints are integrated
- [ ] Check responsive design on multiple screen sizes
- [ ] Ensure dark mode works everywhere
- [ ] Test keyboard navigation
- [ ] Add empty states and error handling
- [ ] Optimize performance (lazy loading, etc.)
- [ ] Add smooth animations and transitions

---

## 🎯 **SUCCESS CRITERIA**

Your frontend is complete when:
1. ✅ All 41+ pages are implemented and functional
2. ✅ All backend API endpoints are integrated
3. ✅ Authentication and routing work perfectly
4. ✅ Design is visually stunning and unique
5. ✅ Responsive on mobile, tablet, desktop
6. ✅ Smooth animations throughout
7. ✅ Loading states, error handling, empty states
8. ✅ Dark mode fully implemented
9. ✅ WebSocket for real-time features
10. ✅ Performance optimized (fast load times)

---

## 🚀 **FINAL NOTES**

### **REMEMBER**:
- **BE CREATIVE!** This is your chance to shine
- **USER EXPERIENCE FIRST** - Make it delightful to use
- **ATTENTION TO DETAIL** - Polish every interaction
- **MODERN & BOLD** - Don't be afraid of bold design choices
- **SMOOTH & FAST** - Optimize for performance
- **ACCESSIBLE** - Make it usable for everyone

### **PRIORITIZE**:
1. Core functionality (login, dashboards, main features)
2. Beautiful, unique design (this is what will impress)
3. Smooth animations and transitions
4. Responsive design
5. Advanced features (charts, real-time, etc.)

---

## 🎉 **LET'S BUILD SOMETHING AMAZING!**

You have complete creative freedom to make this the most visually stunning, smooth, and impressive SAMS frontend ever built. Use cutting-edge design patterns, bold animations, and modern UI trends to create something truly special.

**Make it beautiful. Make it fast. Make it unforgettable.** 🚀✨

---

**Good luck! Can't wait to see what you create!** 🎨🔥
