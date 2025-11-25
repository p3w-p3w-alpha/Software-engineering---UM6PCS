import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import Login from '../views/Login.vue'

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
    meta: { requiresAuth: false }
  },

  // Student Routes
  {
    path: '/student',
    name: 'StudentDashboard',
    component: StudentDashboard,
    meta: { requiresAuth: true, roles: ['STUDENT'] }
  },
  {
    path: '/student/courses/browse',
    name: 'CourseBrowse',
    component: CourseBrowse,
    meta: { requiresAuth: true, roles: ['STUDENT'] }
  },
  {
    path: '/student/payments',
    name: 'StudentPayments',
    component: StudentPayments,
    meta: { requiresAuth: true, roles: ['STUDENT'] }
  },
  {
    path: '/student/grades',
    name: 'StudentGrades',
    component: StudentGrades,
    meta: { requiresAuth: true, roles: ['STUDENT'] }
  },
  {
    path: '/student/degree-progress',
    name: 'DegreeProgress',
    component: DegreeProgress,
    meta: { requiresAuth: true, roles: ['STUDENT'] }
  },

  // Admin Routes
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '/admin/users',
    name: 'UserManagement',
    component: UserManagement,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '/admin/payments',
    name: 'PaymentApproval',
    component: PaymentApproval,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '/admin/attendance',
    name: 'AttendanceManagement',
    component: AttendanceManagement,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN', 'FACULTY'] }
  },
  {
    path: '/admin/analytics',
    name: 'DashboardAnalytics',
    component: DashboardAnalytics,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '/admin/fees',
    name: 'FeeManagement',
    component: FeeManagement,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '/admin/fee-reports',
    name: 'FeeReports',
    component: FeeReports,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '/admin/teachers',
    name: 'TeacherManagement',
    component: TeacherManagement,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },
  {
    path: '/admin/teacher-schedule',
    name: 'TeacherSchedule',
    component: TeacherSchedule,
    meta: { requiresAuth: true, roles: ['ADMIN', 'SUPER_ADMIN'] }
  },

  // Faculty Routes
  {
    path: '/faculty',
    name: 'FacultyDashboard',
    component: FacultyDashboard,
    meta: { requiresAuth: true, roles: ['FACULTY'] }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
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
