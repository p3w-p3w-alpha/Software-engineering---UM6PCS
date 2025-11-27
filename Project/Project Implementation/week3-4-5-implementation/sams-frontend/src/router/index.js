import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import Login from '../views/Login.vue'
import DashboardLayout from '../layouts/DashboardLayout.vue'

// Student Views
import StudentDashboard from '../views/StudentDashboard.vue'
import CourseBrowse from '../views/student/CourseBrowse.vue'
import StudentPayments from '../views/student/StudentPayments.vue'
import StudentGrades from '../views/student/StudentGrades.vue'
import DegreeProgress from '../views/student/DegreeProgress.vue'

// Admin Views
import AdminDashboard from '../views/AdminDashboard.vue'
import UserManagement from '../views/admin/UserManagement.vue'
import PaymentApproval from '../views/admin/PaymentApproval.vue'
import AttendanceManagement from '../views/admin/AttendanceManagement.vue'
import DashboardAnalytics from '../views/admin/DashboardAnalytics.vue'
import FeeManagement from '../views/admin/FeeManagement.vue'
import FeeReports from '../views/admin/FeeReports.vue'
import TeacherManagement from '../views/admin/TeacherManagement.vue'
import TeacherSchedule from '../views/admin/TeacherSchedule.vue'

// Faculty Views
import FacultyDashboard from '../views/FacultyDashboard.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false, transition: 'fade' }
  },

  // ========================================
  // STUDENT ROUTES WITH LAYOUT
  // ========================================
  {
    path: '/student',
    component: DashboardLayout,
    meta: { requiresAuth: true, roles: ['STUDENT'] },
    children: [
      {
        path: '',
        name: 'StudentDashboard',
        component: StudentDashboard,
        meta: { transition: 'slide-left' }
      },
      {
        path: 'courses',
        children: [
          {
            path: 'browse',
            name: 'CourseBrowse',
            component: CourseBrowse
          }
        ]
      },
      {
        path: 'payments',
        name: 'StudentPayments',
        component: StudentPayments
      },
      {
        path: 'grades',
        name: 'StudentGrades',
        component: StudentGrades
      },
      {
        path: 'degree-progress',
        name: 'DegreeProgress',
        component: DegreeProgress
      },
      {
        path: 'assignments',
        name: 'StudentAssignments',
        component: () => import('../views/student/StudentAssignments.vue')
      },
      {
        path: 'assignments/:id/submit',
        name: 'AssignmentSubmission',
        component: () => import('../views/student/AssignmentSubmission.vue')
      },
      {
        path: 'submissions',
        name: 'SubmissionHistory',
        component: () => import('../views/student/SubmissionHistory.vue')
      },
      {
        path: 'submissions/:id',
        name: 'SubmissionDetail',
        component: () => import('../views/student/SubmissionHistory.vue')
      },
      {
        path: 'transcript',
        name: 'TranscriptView',
        component: () => import('../views/student/TranscriptView.vue')
      },
      {
        path: 'attendance',
        name: 'StudentAttendance',
        component: () => import('../views/student/StudentAttendance.vue')
      },
      {
        path: 'fees',
        name: 'StudentFees',
        component: StudentPayments
      },
      {
        path: 'schedule',
        name: 'StudentSchedule',
        component: () => import('../views/student/StudentSchedule.vue')
      },
      {
        path: 'study-groups',
        name: 'StudentStudyGroups',
        component: () => import('../views/studygroups/StudyGroupBrowser.vue')
      },
      {
        path: 'messages',
        name: 'StudentMessages',
        component: () => import('../views/messages/MessagesInbox.vue')
      }
    ]
  },

  // ========================================
  // ADMIN ROUTES WITH LAYOUT
  // ========================================
  {
    path: '/admin',
    component: DashboardLayout,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] },
    children: [
      {
        path: '',
        name: 'AdminDashboard',
        component: AdminDashboard,
        meta: { transition: 'slide-left' }
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: UserManagement
      },
      {
        path: 'courses',
        name: 'AdminCourses',
        component: () => import('../views/admin/CourseManagement.vue')
      },
      {
        path: 'payments',
        name: 'PaymentApproval',
        component: PaymentApproval
      },
      {
        path: 'attendance',
        name: 'AttendanceManagement',
        component: AttendanceManagement
      },
      {
        path: 'analytics',
        name: 'DashboardAnalytics',
        component: DashboardAnalytics
      },
      {
        path: 'fees',
        name: 'FeeManagement',
        component: FeeManagement
      },
      {
        path: 'fee-reports',
        name: 'FeeReports',
        component: FeeReports
      },
      {
        path: 'teachers',
        name: 'TeacherManagement',
        component: TeacherManagement
      },
      {
        path: 'teacher-schedule',
        name: 'TeacherSchedule',
        component: TeacherSchedule
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: () => import('../views/admin/Reports.vue')
      },
      {
        path: 'settings',
        name: 'AdminSettings',
        component: () => import('../views/admin/Settings.vue')
      },
      {
        path: 'advanced-analytics',
        name: 'AdminAnalyticsDashboard',
        component: () => import('../views/admin/AdminAnalyticsDashboard.vue')
      },
      {
        path: 'system-health',
        name: 'SystemHealthMonitor',
        component: () => import('../views/admin/SystemHealthMonitor.vue')
      }
    ]
  },

  // ========================================
  // FACULTY ROUTES WITH LAYOUT
  // ========================================
  {
    path: '/faculty',
    component: DashboardLayout,
    meta: { requiresAuth: true, roles: ['FACULTY'] },
    children: [
      {
        path: '',
        name: 'FacultyDashboard',
        component: FacultyDashboard,
        meta: { transition: 'slide-left' }
      },
      {
        path: 'courses',
        name: 'FacultyCourses',
        component: () => import('../views/faculty/FacultyCourses.vue')
      },
      {
        path: 'assignments',
        name: 'FacultyAssignments',
        component: () => import('../views/faculty/FacultyAssignments.vue')
      },
      {
        path: 'submissions',
        name: 'FacultySubmissions',
        component: () => import('../views/faculty/FacultySubmissions.vue')
      },
      {
        path: 'submissions/:id/grade',
        name: 'GradeSubmission',
        component: () => import('../views/faculty/GradeSubmission.vue')
      },
      {
        path: 'grading',
        name: 'FacultyGrading',
        component: () => import('../views/faculty/FacultyGrades.vue')
      },
      {
        path: 'grades',
        name: 'FacultyGrades',
        component: () => import('../views/faculty/FacultyGrades.vue')
      },
      {
        path: 'grades/:enrollmentId',
        name: 'GradeEntry',
        component: () => import('../views/faculty/GradeEntry.vue')
      },
      {
        path: 'attendance',
        name: 'FacultyAttendance',
        component: () => import('../views/faculty/FacultyAttendance.vue')
      },
      {
        path: 'schedule',
        name: 'FacultySchedule',
        component: () => import('../views/faculty/FacultySchedule.vue')
      },
      {
        path: 'messages',
        name: 'FacultyMessages',
        component: () => import('../views/messages/MessagesInbox.vue')
      }
    ]
  },

  // ========================================
  // SHARED ROUTES WITH LAYOUT
  // ========================================
  {
    path: '/',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('../views/social/UserProfile.vue')
      },
      {
        path: 'profile/:id',
        name: 'ViewProfile',
        component: () => import('../views/social/UserProfile.vue')
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: () => import('../views/Settings.vue')
      },
      {
        path: 'messages',
        name: 'MessagesInbox',
        component: () => import('../views/messages/MessagesInbox.vue')
      },
      {
        path: 'messages/:userId',
        name: 'ConversationView',
        component: () => import('../views/messages/MessagesInbox.vue')
      },
      {
        path: 'studygroups',
        name: 'StudyGroupBrowser',
        component: () => import('../views/studygroups/StudyGroupBrowser.vue')
      },
      {
        path: 'studygroups/:id',
        name: 'StudyGroupDetail',
        component: () => import('../views/studygroups/StudyGroupDetail.vue')
      },
      {
        path: 'connections',
        name: 'SocialConnections',
        component: () => import('../views/social/SocialConnections.vue')
      },
      {
        path: 'notifications',
        name: 'NotificationsPage',
        component: () => import('../views/messages/MessagesInbox.vue')
      }
    ]
  },

  // 404 Route
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { transition: 'fade' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else if (to.hash) {
      return { el: to.hash, behavior: 'smooth' }
    } else {
      return { top: 0, behavior: 'smooth' }
    }
  }
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      next('/login')
    } else if (to.meta.roles && !to.meta.roles.includes(authStore.userRole)) {
      // User doesn't have permission for this route
      // Redirect to their appropriate dashboard
      if (authStore.isAdmin) {
        next('/admin')
      } else if (authStore.isStudent) {
        next('/student')
      } else if (authStore.isFaculty) {
        next('/faculty')
      } else {
        next('/login')
      }
    } else {
      next()
    }
  } else {
    // Non-protected route
    if (to.path === '/login' && authStore.isAuthenticated) {
      // Redirect authenticated users away from login
      if (authStore.isAdmin) {
        next('/admin')
      } else if (authStore.isStudent) {
        next('/student')
      } else if (authStore.isFaculty) {
        next('/faculty')
      } else {
        next()
      }
    } else {
      next()
    }
  }
})

export default router