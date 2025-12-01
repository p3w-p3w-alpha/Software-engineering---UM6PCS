<!--
  DockNavbar - macOS-style dock navigation bar at bottom of screen
  shows role-based menu items with magnification effect on hover
  took forever to get teh glassmorphism and animations right lol
-->

<template>
  <nav class="dock-navbar" ref="dockRef">
    <!-- Main Dock Container with glassmorphism -->
    <div class="dock-container">
      <!-- Dock Background with Glassmorphism -->
      <div class="dock-background"></div>

      <!-- Dock Items -->
      <div class="dock-items">
        <DockItem
          v-for="(item, index) in menuItems"
          :key="item.path || item.id"
          :item="item"
          :index="index"
          @hover="handleItemHover"
          @leave="handleItemLeave"
        />

        <!-- Separator -->
        <div class="dock-separator" v-if="secondaryItems.length > 0"></div>

        <!-- Secondary Items (Settings, Profile, etc.) -->
        <DockItem
          v-for="(item, index) in secondaryItems"
          :key="item.path || item.id"
          :item="item"
          :index="menuItems.length + index"
          @hover="handleItemHover"
          @leave="handleItemLeave"
        />
      </div>

      <!-- Dock Reflection -->
      <div class="dock-reflection"></div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notifications'
import DockItem from './DockItem.vue'
import { gsap } from 'gsap'  // for smooth animations
import {
  HomeIcon,
  AcademicCapIcon,
  BookOpenIcon,
  ClipboardDocumentListIcon,
  CalendarDaysIcon,
  ChartBarIcon,
  UsersIcon,
  CreditCardIcon,
  Cog6ToothIcon,
  UserCircleIcon,
  BellIcon,
  ChatBubbleLeftRightIcon,
  UserGroupIcon,
  DocumentTextIcon,
  ClockIcon,
  BanknotesIcon,
  DocumentChartBarIcon,
  ServerStackIcon,
  WrenchScrewdriverIcon,
  FolderIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

const dockRef = ref(null)  // for magnification effect
const hoveredIndex = ref(-1)

// Compute menu items based on user role - definately seperate items per role
const menuItems = computed(() => {
  const role = authStore.userRole

  // Admin users get thier own set of menu items
  if (role === 'SUPER_ADMIN' || role === 'ADMIN') {
    return [
      { path: '/admin', label: 'Dashboard', icon: HomeIcon },
      { path: '/admin/users', label: 'Users', icon: UsersIcon },
      { path: '/admin/courses', label: 'Courses', icon: BookOpenIcon },
      { path: '/admin/payments', label: 'Payments', icon: CreditCardIcon },
      { path: '/admin/attendance', label: 'Attendance', icon: ClockIcon },
      { path: '/admin/analytics', label: 'Analytics', icon: ChartBarIcon },
      { path: '/admin/reports', label: 'Reports', icon: DocumentChartBarIcon },
      { path: '/admin/fees', label: 'Fees', icon: BanknotesIcon },
      { path: '/admin/system-health', label: 'System', icon: ServerStackIcon },
    ]
  }

  if (role === 'FACULTY') {
    return [
      { path: '/faculty', label: 'Dashboard', icon: HomeIcon },
      { path: '/faculty/courses', label: 'My Courses', icon: BookOpenIcon },
      { path: '/faculty/assignments', label: 'Assignments', icon: ClipboardDocumentListIcon },
      { path: '/faculty/grading', label: 'Grading', icon: DocumentTextIcon },
      { path: '/faculty/attendance', label: 'Attendance', icon: ClockIcon },
      { path: '/faculty/schedule', label: 'Schedule', icon: CalendarDaysIcon },
    ]
  }

  // Default: STUDENT
  return [
    { path: '/student', label: 'Dashboard', icon: HomeIcon },
    { path: '/student/courses', label: 'My Courses', icon: BookOpenIcon },
    { path: '/student/assignments', label: 'Assignments', icon: ClipboardDocumentListIcon },
    { path: '/student/grades', label: 'Grades', icon: AcademicCapIcon },
    { path: '/student/attendance', label: 'Attendance', icon: ClockIcon },
    { path: '/student/schedule', label: 'Schedule', icon: CalendarDaysIcon },
    { path: '/student/payments', label: 'Payments', icon: CreditCardIcon },
  ]
})

// Secondary items (common across all roles)
const secondaryItems = computed(() => {
  const unreadCount = notificationStore.unreadCount || 0

  return [
    { path: '/messages', label: 'Messages', icon: ChatBubbleLeftRightIcon },
    { path: '/studygroups', label: 'Study Groups', icon: UserGroupIcon },
    {
      path: '/notifications',
      label: 'Notifications',
      icon: BellIcon,
      badge: unreadCount
    },
  ]
})

const handleItemHover = (index) => {
  hoveredIndex.value = index
}

const handleItemLeave = () => {
  hoveredIndex.value = -1
}

// Dock magnification effect (macOS style)
const applyMagnification = (event) => {
  if (!dockRef.value) return

  const items = dockRef.value.querySelectorAll('.dock-item-wrapper')
  const dockRect = dockRef.value.getBoundingClientRect()
  const mouseX = event.clientX - dockRect.left

  items.forEach((item, index) => {
    const itemRect = item.getBoundingClientRect()
    const itemCenter = itemRect.left - dockRect.left + itemRect.width / 2
    const distance = Math.abs(mouseX - itemCenter)
    const maxDistance = 150

    if (distance < maxDistance) {
      const scale = 1 + (1 - distance / maxDistance) * 0.3
      gsap.to(item, { scale, duration: 0.15, ease: 'power2.out' })
    } else {
      gsap.to(item, { scale: 1, duration: 0.15, ease: 'power2.out' })
    }
  })
}

const resetMagnification = () => {
  if (!dockRef.value) return

  const items = dockRef.value.querySelectorAll('.dock-item-wrapper')
  items.forEach((item) => {
    gsap.to(item, { scale: 1, duration: 0.2, ease: 'power2.out' })
  })
}

onMounted(() => {
  if (dockRef.value) {
    dockRef.value.addEventListener('mousemove', applyMagnification)
    dockRef.value.addEventListener('mouseleave', resetMagnification)
  }
})

onUnmounted(() => {
  if (dockRef.value) {
    dockRef.value.removeEventListener('mousemove', applyMagnification)
    dockRef.value.removeEventListener('mouseleave', resetMagnification)
  }
})
</script>

<style scoped>
.dock-navbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  padding-bottom: 16px;
  z-index: 50;
  pointer-events: none;
}

.dock-container {
  position: relative;
  display: flex;
  align-items: center;
  pointer-events: auto;
}

.dock-background {
  position: absolute;
  inset: 0;
  background: rgba(18, 18, 26, 0.85);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 22px;
  box-shadow:
    0 -10px 60px rgba(0, 0, 0, 0.4),
    0 0 80px rgba(0, 212, 255, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.dock-items {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  z-index: 1;
}

.dock-separator {
  width: 1px;
  height: 36px;
  background: linear-gradient(
    to bottom,
    transparent,
    rgba(255, 255, 255, 0.15),
    transparent
  );
  margin: 0 8px;
}

.dock-reflection {
  position: absolute;
  bottom: -30px;
  left: 10%;
  right: 10%;
  height: 30px;
  background: linear-gradient(
    to bottom,
    rgba(18, 18, 26, 0.3),
    transparent
  );
  border-radius: 0 0 20px 20px;
  filter: blur(10px);
  opacity: 0.5;
  pointer-events: none;
}

/* Dock hover lift effect */
.dock-container:hover .dock-background {
  box-shadow:
    0 -15px 70px rgba(0, 0, 0, 0.5),
    0 0 100px rgba(0, 212, 255, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.08);
}

/* Animation for dock appearance */
@keyframes dock-appear {
  from {
    opacity: 0;
    transform: translateY(100px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dock-navbar {
  animation: dock-appear 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}
</style>
