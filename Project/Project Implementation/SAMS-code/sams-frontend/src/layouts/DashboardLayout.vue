<template>
  <div class="dashboard-layout min-h-screen" style="background: #f8fafc;">
    <!-- Toast for notifications -->
    <Toast position="top-right" />

    <!-- Overlay for mobile -->
    <div
      v-if="!sidebarCollapsed && isMobile"
      @click="sidebarCollapsed = true"
      class="fixed inset-0 bg-black/70 backdrop-blur-sm z-30 md:hidden transition-all duration-300"
    ></div>

    <!-- glassmorphic floating sidebar - dark theme with gradients and blur effects -->
    <!-- this was tricky to get right with all teh animations and 3D effects -->
    <div
      :class="[
        'sidebar-wrapper fixed z-40 transition-all duration-500 ease-out',
        sidebarCollapsed ? '-translate-x-full md:translate-x-0' : 'translate-x-0'
      ]"
      :style="{
        left: '12px',
        top: '12px',
        height: 'calc(100vh - 24px)',
        width: sidebarCollapsed ? '76px' : '280px'
      }"
    >
      <!-- glass container - dark theme matching login page for consistency -->
      <div
        class="glass-sidebar h-full relative overflow-hidden"
        style="
          background: linear-gradient(180deg, #050510 0%, #0a0a1f 50%, #12122f 100%);
          backdrop-filter: blur(20px);
          -webkit-backdrop-filter: blur(20px);
          border: 1px solid rgba(99, 102, 241, 0.2);
          border-radius: 24px;
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.6), inset 0 1px 0 rgba(255, 255, 255, 0.05), 0 0 60px rgba(99, 102, 241, 0.1);
        "
      >
        <!-- animated background gradient - rotating glows for visual interest -->
        <div class="absolute inset-0 overflow-hidden rounded-3xl pointer-events-none">
          <div class="sidebar-glow-1"></div>
          <div class="sidebar-glow-2"></div>
        </div>

        <!-- logo section - SAMS branding -->
        <div class="relative p-5 border-b border-white/5">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <!-- Animated Logo -->
              <div class="logo-container relative">
                <div class="logo-glow absolute inset-0 rounded-2xl"></div>
                <div
                  class="relative w-11 h-11 rounded-2xl flex items-center justify-center transition-transform duration-300 hover:scale-110"
                  style="background: linear-gradient(135deg, rgba(99, 102, 241, 0.3) 0%, rgba(139, 92, 246, 0.3) 100%); border: 1px solid rgba(255, 255, 255, 0.15);"
                >
                  <i class="pi pi-graduation-cap text-white text-xl"></i>
                </div>
              </div>
              <Transition name="fade-slide">
                <div v-if="!sidebarCollapsed" class="flex flex-col">
                  <span class="text-white font-bold text-xl tracking-wide">SAMS</span>
                  <span class="text-white/40 text-xs">Academic Portal</span>
                </div>
              </Transition>
            </div>
            <button
              @click="sidebarCollapsed = !sidebarCollapsed"
              class="collapse-btn w-9 h-9 rounded-xl flex items-center justify-center text-white/60 hover:text-white hover:bg-white/10 transition-all duration-300"
              :class="{ 'rotate-180': sidebarCollapsed }"
            >
              <i class="pi pi-angle-double-left text-sm"></i>
            </button>
          </div>
        </div>

        <!-- user profile section with 3D card effect - shows current user info -->
        <!-- card tilts on hover for cool effect - definately adds personality -->
        <div class="relative p-4">
          <div
            class="user-card relative p-3 rounded-2xl transition-all duration-300 overflow-hidden"
            style="background: linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(168, 85, 247, 0.12) 50%, rgba(236, 72, 153, 0.08) 100%); border: 1px solid rgba(255, 255, 255, 0.1);"
            @mouseenter="handleUserCardHover"
            @mouseleave="handleUserCardLeave"
            @mousemove="handleUserCardMove"
            ref="userCardRef"
          >
            <!-- Subtle shimmer effect -->
            <div class="absolute inset-0 bg-gradient-to-r from-transparent via-white/5 to-transparent -translate-x-full user-card-shimmer"></div>

            <div class="flex items-center gap-3 relative z-10">
              <!-- Floating Avatar -->
              <div class="avatar-wrapper relative">
                <div class="avatar-glow absolute inset-0 rounded-full"></div>
                <Avatar
                  :label="userInitials"
                  :image="userAvatar"
                  class="relative z-10 ring-2 ring-white/20"
                  size="large"
                  shape="circle"
                  :style="{ background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 50%, #ec4899 100%)' }"
                />
                <!-- Online indicator -->
                <div class="absolute -bottom-0.5 -right-0.5 w-4 h-4 bg-emerald-500 rounded-full border-2 border-[#0a0a1f] z-20">
                  <div class="absolute inset-0 bg-emerald-400 rounded-full animate-ping opacity-75"></div>
                </div>
              </div>
              <Transition name="fade-slide">
                <div v-if="!sidebarCollapsed" class="flex-1 min-w-0">
                  <p class="text-white font-semibold truncate">{{ userName }}</p>
                  <p class="text-white/60 text-sm flex items-center gap-1.5">
                    <span class="w-1.5 h-1.5 bg-emerald-400 rounded-full"></span>
                    {{ userRole }}
                  </p>
                </div>
              </Transition>
            </div>
          </div>
        </div>

        <!-- navigation menu with 3D tilt effect - menu items tilt on hover -->
        <!-- includes dock magnification effect when collapsed - like macos dock -->
        <nav class="relative p-3 overflow-y-auto custom-scrollbar flex-1" :style="{ maxHeight: 'calc(100% - 240px)' }">
          <ul class="space-y-1.5">
            <li
              v-for="(item, index) in menuItems"
              :key="item.path"
              :style="{ '--item-index': index }"
              class="menu-item-wrapper"
            >
              <!-- Item with children (submenu) -->
              <div v-if="item.children">
                <!-- Parent Item with 3D Tilt -->
                <div
                  :ref="el => setItemRef(el, 'parent-' + item.path)"
                  @click="toggleSubmenu(item.path)"
                  @mouseenter="handleMenuItemEnter($event, 'parent-' + item.path, index)"
                  @mouseleave="handleMenuItemLeave('parent-' + item.path)"
                  @mousemove="handleMenuItemMove($event, 'parent-' + item.path)"
                  :class="[
                    'menu-item relative flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-all duration-300',
                    isMenuActive(item) ? 'active-item' : ''
                  ]"
                  :style="getMenuItemStyle('parent-' + item.path, isMenuActive(item))"
                >
                  <!-- Active indicator glow -->
                  <div v-if="isMenuActive(item)" class="active-glow"></div>

                  <!-- Hover glow that follows cursor -->
                  <div
                    class="item-hover-glow"
                    :style="getHoverGlowStyle('parent-' + item.path)"
                  ></div>

                  <!-- Icon with dock magnification in collapsed mode -->
                  <div
                    :class="['icon-wrapper relative z-10 transition-all duration-300', { 'mx-auto': sidebarCollapsed }]"
                    :style="getDockItemStyle(index)"
                  >
                    <i :class="[item.icon, 'text-xl transition-colors duration-300']"
                       :style="{ color: isMenuActive(item) ? '#fff' : 'rgba(255,255,255,0.7)' }"></i>
                  </div>

                  <Transition name="fade-slide">
                    <span v-if="!sidebarCollapsed" class="relative z-10 font-medium text-white/90">{{ item.label }}</span>
                  </Transition>

                  <i v-if="!sidebarCollapsed"
                     :class="['pi relative z-10 ml-auto text-xs text-white/50 transition-transform duration-300', expandedMenus.includes(item.path) ? 'pi-chevron-down rotate-0' : 'pi-chevron-right']">
                  </i>

                  <!-- Tooltip for collapsed mode -->
                  <div v-if="sidebarCollapsed" class="dock-tooltip">
                    {{ item.label }}
                  </div>
                </div>

                <!-- Animated Submenu -->
                <Transition name="submenu-expand">
                  <ul v-if="expandedMenus.includes(item.path) && !sidebarCollapsed" class="submenu mt-1.5 ml-3 pl-4 space-y-1 border-l border-white/10">
                    <li
                      v-for="(child, childIndex) in item.children"
                      :key="child.path"
                      :style="{ '--child-index': childIndex, animationDelay: `${childIndex * 0.05}s` }"
                      class="submenu-item"
                    >
                      <router-link :to="child.path" v-slot="{ isActive }" custom>
                        <div
                          @click="navigateTo(child.path)"
                          :class="[
                            'flex items-center gap-3 px-3 py-2.5 rounded-lg cursor-pointer transition-all duration-200',
                            isActive ? 'bg-white/10 text-white' : 'text-white/50 hover:bg-white/5 hover:text-white/80'
                          ]"
                        >
                          <i :class="[child.icon, 'text-sm']"></i>
                          <span class="text-sm">{{ child.label }}</span>
                        </div>
                      </router-link>
                    </li>
                  </ul>
                </Transition>
              </div>

              <!-- Regular item without children -->
              <router-link v-else :to="item.path" v-slot="{ isActive }" custom>
                <div
                  :ref="el => setItemRef(el, item.path)"
                  @click="navigateTo(item.path)"
                  @mouseenter="handleMenuItemEnter($event, item.path, index)"
                  @mouseleave="handleMenuItemLeave(item.path)"
                  @mousemove="handleMenuItemMove($event, item.path)"
                  :class="[
                    'menu-item relative flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer transition-all duration-300',
                    isActive ? 'active-item' : ''
                  ]"
                  :style="getMenuItemStyle(item.path, isActive)"
                >
                  <!-- Active indicator glow -->
                  <div v-if="isActive" class="active-glow"></div>

                  <!-- Hover glow that follows cursor -->
                  <div
                    class="item-hover-glow"
                    :style="getHoverGlowStyle(item.path)"
                  ></div>

                  <!-- Icon with dock magnification -->
                  <div
                    :class="['icon-wrapper relative z-10 transition-all duration-300', { 'mx-auto': sidebarCollapsed }]"
                    :style="getDockItemStyle(index)"
                  >
                    <i :class="[item.icon, 'text-xl transition-colors duration-300']"
                       :style="{ color: isActive ? '#fff' : 'rgba(255,255,255,0.7)' }"></i>
                  </div>

                  <Transition name="fade-slide">
                    <span v-if="!sidebarCollapsed" class="relative z-10 font-medium text-white/90">{{ item.label }}</span>
                  </Transition>

                  <!-- Redesigned Badge with glow -->
                  <Transition name="badge-pop">
                    <div v-if="item.badge" class="badge-wrapper relative z-10" :class="{ 'ml-auto': !sidebarCollapsed }">
                      <div class="badge-glow"></div>
                      <span class="badge-pill">{{ item.badge }}</span>
                    </div>
                  </Transition>

                  <!-- Tooltip for collapsed mode -->
                  <div v-if="sidebarCollapsed" class="dock-tooltip">
                    {{ item.label }}
                    <span v-if="item.badge" class="tooltip-badge">{{ item.badge }}</span>
                  </div>
                </div>
              </router-link>
            </li>
          </ul>
        </nav>

        <!-- bottom actions - logout button -->
        <div class="absolute bottom-0 left-0 right-0 p-3 border-t border-white/5">
          <div class="space-y-1.5">
            <!-- Logout Button -->
            <button
              @click="handleLogout"
              class="logout-btn w-full flex items-center gap-3 px-4 py-3 rounded-xl text-white/60 hover:text-white hover:bg-white/10 transition-all duration-300 group"
              :class="{ 'justify-center': sidebarCollapsed }"
            >
              <i class="pi pi-sign-out text-xl group-hover:scale-110 transition-transform duration-300"></i>
              <Transition name="fade-slide">
                <span v-if="!sidebarCollapsed" class="font-medium">Logout</span>
              </Transition>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- main content area - where page content renders -->
    <!-- adjusts margin based on sidebar collapse state -->
    <div
      :class="[
        'main-content transition-all duration-500 min-h-screen',
        sidebarCollapsed ? 'md:ml-[100px]' : 'md:ml-[304px]'
      ]"
      style="background: #ffffff; color: #1e293b;"
    >
      <!-- top navbar with glassmorphism - sticky at top with blur effect -->
      <nav
        class="navbar sticky top-0 z-30 mx-3 mt-3 rounded-2xl transition-all duration-300"
        style="
          background: rgba(255, 255, 255, 0.9);
          backdrop-filter: blur(20px);
          -webkit-backdrop-filter: blur(20px);
          border: 1px solid rgba(0, 0, 0, 0.08);
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
        "
      >
        <div class="px-6 py-4">
          <div class="flex items-center justify-between">
            <!-- breadcrumb navigation - shows current page path -->
            <Breadcrumb :model="breadcrumbItems" class="text-sm breadcrumb-glass">
              <template #item="{ item }">
                <router-link
                  v-if="item.route"
                  :to="item.route"
                  class="text-gray-500 hover:text-gray-800 transition-colors"
                >
                  {{ item.label }}
                </router-link>
                <span v-else class="text-gray-800 font-medium">{{ item.label }}</span>
              </template>
              <template #separator>
                <i class="pi pi-angle-right text-gray-400"></i>
              </template>
            </Breadcrumb>

            <!-- right actions - search, notifications, user menu -->
            <div class="flex items-center gap-2 md:gap-3">
              <!-- global search (hidden on small screens) - searches users and courses -->
              <div class="relative hidden md:block search-container">
                <InputText
                  v-model="searchQuery"
                  placeholder="Search users, courses..."
                  class="pl-10 pr-10 py-2 w-48 lg:w-72 rounded-xl transition-all duration-300 search-input-light"
                  @keyup.enter="performSearch"
                  @focus="searchQuery.length >= 2 && searchResults.totalResults > 0 ? showSearchResults = true : null"
                />
                <i class="pi pi-search absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"></i>
                <button
                  v-if="searchQuery"
                  @click="clearSearch"
                  class="absolute right-3 top-1/2 -translate-y-1/2 transition-colors text-gray-400 hover:text-gray-600"
                >
                  <i class="pi pi-times text-sm"></i>
                </button>

                <!-- search results dropdown - light theme panel with users and courses -->
                <!-- shows results grouped by type - works for now -->
                <Transition name="scale">
                  <div
                    v-if="showSearchResults"
                    class="absolute top-12 left-0 w-80 lg:w-96 rounded-xl overflow-hidden z-50 search-results-panel"
                    style="background: rgba(255, 255, 255, 0.98); backdrop-filter: blur(20px); border: 1px solid rgba(0,0,0,0.08); box-shadow: 0 8px 32px rgba(0,0,0,0.12);"
                  >
                    <!-- Loading State -->
                    <div v-if="searchLoading" class="p-6 text-center">
                      <i class="pi pi-spin pi-spinner text-2xl text-indigo-500"></i>
                      <p class="mt-2 text-gray-500">Searching...</p>
                    </div>

                    <!-- Results -->
                    <div v-else-if="searchResults.totalResults > 0" class="max-h-96 overflow-y-auto custom-scrollbar-light">
                      <!-- Users Section -->
                      <div v-if="searchResults.users && searchResults.users.length > 0">
                        <div class="px-4 py-2 border-b border-gray-100 bg-gradient-to-r from-indigo-50 to-purple-50">
                          <span class="text-xs font-semibold text-gray-600 uppercase">Users</span>
                        </div>
                        <div
                          v-for="user in searchResults.users"
                          :key="'user-' + user.id"
                          @click="navigateToSearchResult('user', user)"
                          class="px-4 py-3 hover:bg-gray-50 cursor-pointer border-b border-gray-100 flex items-center gap-3 transition-colors"
                        >
                          <Avatar
                            :label="user.username ? user.username.substring(0, 2).toUpperCase() : '?'"
                            size="small"
                            shape="circle"
                            :style="{ background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)' }"
                          />
                          <div class="flex-1 min-w-0">
                            <p class="font-medium text-gray-800 truncate">{{ user.username }}</p>
                            <p class="text-sm text-gray-500 truncate">{{ user.email }}</p>
                          </div>
                          <Badge :value="user.role" severity="info" class="text-xs" />
                        </div>
                      </div>

                      <!-- Courses Section -->
                      <div v-if="searchResults.courses && searchResults.courses.length > 0">
                        <div class="px-4 py-2 border-b border-gray-100 bg-gradient-to-r from-emerald-50 to-teal-50">
                          <span class="text-xs font-semibold text-gray-600 uppercase">Courses</span>
                        </div>
                        <div
                          v-for="course in searchResults.courses"
                          :key="'course-' + course.id"
                          @click="navigateToSearchResult('course', course)"
                          class="px-4 py-3 hover:bg-gray-50 cursor-pointer border-b border-gray-100 flex items-center gap-3 transition-colors"
                        >
                          <div class="w-10 h-10 bg-gradient-to-br from-emerald-500 to-teal-500 rounded-lg flex items-center justify-center">
                            <i class="pi pi-book text-white"></i>
                          </div>
                          <div class="flex-1 min-w-0">
                            <p class="font-medium text-gray-800 truncate">{{ course.name }}</p>
                            <p class="text-sm text-gray-500">{{ course.code }} - {{ course.credits }} credits</p>
                          </div>
                        </div>
                      </div>

                      <!-- Total Results Footer -->
                      <div class="px-4 py-2 text-center bg-gray-50">
                        <span class="text-xs text-gray-500">Found {{ searchResults.totalResults }} result(s)</span>
                      </div>
                    </div>

                    <!-- No Results -->
                    <div v-else class="p-6 text-center">
                      <i class="pi pi-search text-3xl text-gray-300"></i>
                      <p class="mt-2 text-gray-500">No results found for "{{ searchQuery }}"</p>
                      <p class="text-sm text-gray-400">Try different keywords</p>
                    </div>
                  </div>
                </Transition>
              </div>

              <!-- Search button for mobile -->
              <Button
                icon="pi pi-search"
                class="p-button-rounded p-button-text md:hidden transition-colors text-gray-600 hover:text-gray-800 hover:bg-gray-100"
                @click="performSearch"
              />

              <!-- notifications - bell icon with badge count -->
              <div class="relative">
                <Button
                  icon="pi pi-bell"
                  :badge="unreadNotifications > 0 ? unreadNotifications.toString() : null"
                  class="p-button-rounded p-button-text notification-btn transition-colors text-gray-600 hover:text-gray-800 hover:bg-gray-100"
                  @click="toggleNotifications"
                />
                <Transition name="scale">
                  <div
                    v-if="showNotifications"
                    class="absolute right-0 top-12 w-80 rounded-xl overflow-hidden notification-panel"
                    style="background: rgba(255, 255, 255, 0.98); backdrop-filter: blur(20px); border: 1px solid rgba(0,0,0,0.08); box-shadow: 0 8px 32px rgba(0,0,0,0.12);"
                  >
                    <NotificationPanel
                      :notifications="notifications"
                      :loading="notificationsLoading"
                      :is-open="showNotifications"
                      @close="showNotifications = false"
                      @mark-read="handleMarkRead"
                      @mark-all-read="handleMarkAllRead"
                      @notification-click="handleNotificationClick"
                    />
                  </div>
                </Transition>
              </div>

              <!-- user menu - dropdown with profile, settings, logout -->
              <div class="relative user-menu-container">
                <button
                  @click.stop="showUserMenu = !showUserMenu"
                  class="user-menu-btn flex items-center gap-2 px-3 py-2 rounded-xl transition-all duration-200 hover:bg-gray-100 hover:scale-105"
                >
                  <Avatar
                    :label="userInitials"
                    :image="userAvatar"
                    size="small"
                    shape="circle"
                    :style="{ background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)' }"
                  />
                  <i class="pi pi-angle-down text-gray-500 transition-transform duration-200" :class="{ 'rotate-180': showUserMenu }"></i>
                </button>
                <Transition name="scale">
                  <div
                    v-if="showUserMenu"
                    class="user-menu-panel absolute right-0 top-12 w-56 rounded-xl overflow-hidden z-50"
                    style="background: rgba(255, 255, 255, 0.98); backdrop-filter: blur(20px); border: 1px solid rgba(0,0,0,0.08); box-shadow: 0 8px 32px rgba(0,0,0,0.12);"
                  >
                    <div class="p-4 border-b border-gray-100 bg-gradient-to-r from-indigo-50 to-purple-50">
                      <p class="font-semibold text-gray-800">{{ userName }}</p>
                      <p class="text-sm text-gray-500">{{ userEmail }}</p>
                    </div>
                    <div class="p-2">
                      <router-link
                        :to="`/profile/${authStore.userId}`"
                        class="flex items-center gap-3 px-3 py-2 rounded-lg transition-all duration-200 hover:bg-gray-50 hover:translate-x-1"
                        @click="showUserMenu = false"
                      >
                        <i class="pi pi-user text-gray-500"></i>
                        <span class="text-gray-700">View Profile</span>
                      </router-link>
                      <router-link
                        to="/connections"
                        class="flex items-center gap-3 px-3 py-2 rounded-lg transition-all duration-200 hover:bg-gray-50 hover:translate-x-1"
                        @click="showUserMenu = false"
                      >
                        <i class="pi pi-users text-gray-500"></i>
                        <span class="text-gray-700">My Connections</span>
                      </router-link>
                      <router-link
                        to="/messages"
                        class="flex items-center gap-3 px-3 py-2 rounded-lg transition-all duration-200 hover:bg-gray-50 hover:translate-x-1"
                        @click="showUserMenu = false"
                      >
                        <i class="pi pi-envelope text-gray-500"></i>
                        <span class="text-gray-700">Messages</span>
                      </router-link>
                      <Divider class="my-2 border-gray-100" />
                      <router-link
                        to="/settings"
                        class="flex items-center gap-3 px-3 py-2 rounded-lg transition-all duration-200 hover:bg-gray-50 hover:translate-x-1"
                        @click="showUserMenu = false"
                      >
                        <i class="pi pi-cog text-gray-500"></i>
                        <span class="text-gray-700">Settings</span>
                      </router-link>
                      <Divider class="my-2 border-gray-100" />
                      <button
                        @click="handleLogout"
                        class="w-full flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-red-50 text-red-500 transition-all duration-200 hover:translate-x-1"
                      >
                        <i class="pi pi-sign-out"></i>
                        <span>Sign Out</span>
                      </button>
                    </div>
                  </div>
                </Transition>
              </div>
            </div>
          </div>
        </div>
      </nav>

      <!-- page content - router view renders here -->
      <main class="p-6">
        <router-view />
      </main>

    </div>
  </div>
</template>

<script setup>
/*
 * dashboard layout script - handles all the state and interactions
 * includes 3D effects, dock magnification, search, notifications, etc
 * this took forever to implement - so many moving parts lol
 */
import { ref, computed, onMounted, onUnmounted, reactive, onErrorCaptured } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notifications'
import { useToast } from 'primevue/usetoast'
import api from '../services/api'
import Avatar from 'primevue/avatar'
import Badge from 'primevue/badge'
import Button from 'primevue/button'
import Breadcrumb from 'primevue/breadcrumb'
import InputText from 'primevue/inputtext'
import Divider from 'primevue/divider'
import Toast from 'primevue/toast'
import NotificationPanel from '../components/NotificationPanel.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()
const toast = useToast()

// error boundary for async component loading - catches route loading errors
// definately important for UX - prevents blank pages on error
onErrorCaptured((error, instance, info) => {
  console.error('Component error captured:', error, info)
  toast.add({
    severity: 'error',
    summary: 'Page Load Error',
    detail: 'Failed to load the page. Please try refreshing.',
    life: 5000
  })
  // Return false to prevent error propagation
  return false
})

// state - component reactive variables
const sidebarCollapsed = ref(false)
const showNotifications = ref(false)
const showUserMenu = ref(false)
const searchQuery = ref('')
const isMobile = ref(window.innerWidth < 768)
const expandedMenus = ref(['/admin/users']) // keep users menu expanded by default
const showSearchResults = ref(false)
const searchLoading = ref(false)
const searchResults = ref({ users: [], courses: [], totalResults: 0 })

// 3D tilt & dock effect state - tracks hover positions and tilts
// this was wierd to debug at first but works great now
const menuItemRefs = ref({})
const menuItemTilts = reactive({})
const menuItemHoverGlows = reactive({})
const hoveredItemIndex = ref(-1)
const userCardRef = ref(null)
const userCardTilt = ref({ rotateX: 0, rotateY: 0 })

// set item ref for 3D tilt tracking - saves element references
const setItemRef = (el, path) => {
  if (el) {
    menuItemRefs.value[path] = el
  }
}

// 3D tilt effect handlers - creates card tilt based on mouse position
// took a while to figure out but looks really cool
const handleMenuItemEnter = (event, path, index) => {
  hoveredItemIndex.value = index
  if (!menuItemTilts[path]) {
    menuItemTilts[path] = { rotateX: 0, rotateY: 0, isHovered: false }
  }
  menuItemTilts[path].isHovered = true
}

const handleMenuItemLeave = (path) => {
  hoveredItemIndex.value = -1
  if (menuItemTilts[path]) {
    menuItemTilts[path] = { rotateX: 0, rotateY: 0, isHovered: false }
  }
  if (menuItemHoverGlows[path]) {
    menuItemHoverGlows[path] = { x: 50, y: 50, opacity: 0 }
  }
}

const handleMenuItemMove = (event, path) => {
  const el = menuItemRefs.value[path]
  if (!el || sidebarCollapsed.value) return

  const rect = el.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2

  // Calculate tilt angles (max ±12 degrees)
  const rotateX = ((y - centerY) / centerY) * -12
  const rotateY = ((x - centerX) / centerX) * 12

  menuItemTilts[path] = { rotateX, rotateY, isHovered: true }

  // Update hover glow position
  const glowX = (x / rect.width) * 100
  const glowY = (y / rect.height) * 100
  menuItemHoverGlows[path] = { x: glowX, y: glowY, opacity: 1 }
}

// get menu item style with 3D transform - applies rotation angles
const getMenuItemStyle = (path, isActive) => {
  const tilt = menuItemTilts[path]
  if (!tilt || !tilt.isHovered || sidebarCollapsed.value) {
    return {
      transform: 'perspective(1000px) rotateX(0deg) rotateY(0deg) translateZ(0px)',
      transition: 'all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94)',
      background: isActive ? 'rgba(255, 255, 255, 0.12)' : 'transparent'
    }
  }

  return {
    transform: `perspective(1000px) rotateX(${tilt.rotateX}deg) rotateY(${tilt.rotateY}deg) translateZ(8px)`,
    transition: 'transform 0.1s ease-out',
    background: isActive ? 'rgba(255, 255, 255, 0.15)' : 'rgba(255, 255, 255, 0.08)'
  }
}

// get hover glow style - creates radial gradient that follows cursor
const getHoverGlowStyle = (path) => {
  const glow = menuItemHoverGlows[path]
  if (!glow || glow.opacity === 0) {
    return { opacity: 0 }
  }

  return {
    opacity: glow.opacity,
    background: `radial-gradient(circle at ${glow.x}% ${glow.y}%, rgba(99, 102, 241, 0.3) 0%, transparent 60%)`,
    transition: 'opacity 0.3s ease'
  }
}

// dock magnification effect - icons scale up when hovered (like macos dock)
// only works when sidebar is collapsed - definately makes it more interactive
const getDockItemStyle = (index) => {
  if (!sidebarCollapsed.value || hoveredItemIndex.value === -1) {
    return { transform: 'scale(1) translateY(0)', transition: 'all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1)' }
  }

  const distance = Math.abs(hoveredItemIndex.value - index)
  let scale = 1
  let translateY = 0

  if (distance === 0) {
    scale = 1.4
    translateY = -4
  } else if (distance === 1) {
    scale = 1.15
    translateY = -2
  } else if (distance === 2) {
    scale = 1.05
  }

  return {
    transform: `scale(${scale}) translateY(${translateY}px)`,
    transition: 'all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1)'
  }
}

// user card 3D effect - same as menu items but for user profile card
const handleUserCardHover = () => {
  // initialize hover state
}

const handleUserCardLeave = () => {
  userCardTilt.value = { rotateX: 0, rotateY: 0 }
}

const handleUserCardMove = (event) => {
  if (!userCardRef.value || sidebarCollapsed.value) return

  const rect = userCardRef.value.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2

  userCardTilt.value = {
    rotateX: ((y - centerY) / centerY) * -8,
    rotateY: ((x - centerX) / centerX) * 8
  }
}

// computed notifications from store - reactive data from pinia store
const unreadNotifications = computed(() => notificationStore.unreadCount)
const notifications = computed(() => notificationStore.recentNotifications)
const notificationsLoading = computed(() => notificationStore.loading)

// user info - extracts from auth store
const userName = computed(() => authStore.user?.username || authStore.userName || 'User')
const userEmail = computed(() => authStore.user?.email || 'user@example.com')
const userRole = computed(() => authStore.user?.role || authStore.userRole || 'User')
const userAvatar = computed(() => authStore.user?.avatar || null)
const userInitials = computed(() => {
  const name = userName.value
  if (name.length < 2) return name.toUpperCase()
  return name.substring(0, 2).toUpperCase()
})

// dynamic menu items based on user role - shows different menus for different users
// this took a while to organize properly lol
const menuItems = computed(() => {
  // normalize role for path: SUPER_ADMIN -> admin, ADMIN -> admin, STUDENT -> student, FACULTY -> faculty
  const rolePath = userRole.value === 'SUPER_ADMIN' || userRole.value === 'ADMIN'
    ? 'admin'
    : userRole.value.toLowerCase()

  const baseItems = [
    {
      label: 'Dashboard',
      icon: 'pi pi-home',
      path: `/${rolePath}`,
      badge: null
    }
  ]

  if (authStore.isAdmin) {
    return [
      ...baseItems,
      {
        label: 'Users',
        icon: 'pi pi-users',
        path: '/admin/users',
        children: [
          { label: 'All Users', icon: 'pi pi-list', path: '/admin/users' },
          { label: 'Students', icon: 'pi pi-user', path: '/admin/students' },
          { label: 'Faculty', icon: 'pi pi-briefcase', path: '/admin/faculty' },
          { label: 'Admins', icon: 'pi pi-shield', path: '/admin/admins' }
        ]
      },
      { label: 'Courses', icon: 'pi pi-book', path: '/admin/courses' },
      { label: 'Fees', icon: 'pi pi-dollar', path: '/admin/fees', badge: '5' },
      { label: 'Payments', icon: 'pi pi-wallet', path: '/admin/payments', badge: '3' },
      { label: 'Attendance', icon: 'pi pi-calendar-check', path: '/admin/attendance' },
      { label: 'Messages', icon: 'pi pi-envelope', path: '/admin/messages' },
      { label: 'Analytics', icon: 'pi pi-chart-bar', path: '/admin/analytics' },
      { label: 'Reports', icon: 'pi pi-chart-line', path: '/admin/reports' },
      { label: 'System', icon: 'pi pi-server', path: '/admin/system-health' }
    ]
  }

  if (authStore.isStudent) {
    return [
      ...baseItems,
      { label: 'My Courses', icon: 'pi pi-book', path: '/student/courses' },
      { label: 'Assignments', icon: 'pi pi-file-edit', path: '/student/assignments', badge: '2' },
      { label: 'Grades', icon: 'pi pi-chart-line', path: '/student/grades' },
      { label: 'Attendance', icon: 'pi pi-calendar-check', path: '/student/attendance' },
      { label: 'Fees', icon: 'pi pi-dollar', path: '/student/fees' },
      { label: 'Schedule', icon: 'pi pi-calendar', path: '/student/schedule' },
      { label: 'Study Groups', icon: 'pi pi-users', path: '/student/study-groups' },
      { label: 'Messages', icon: 'pi pi-envelope', path: '/student/messages', badge: '3' }
    ]
  }

  if (authStore.isFaculty) {
    return [
      ...baseItems,
      { label: 'My Courses', icon: 'pi pi-book', path: '/faculty/courses' },
      { label: 'Assignments', icon: 'pi pi-file-edit', path: '/faculty/assignments' },
      { label: 'Grading', icon: 'pi pi-pencil', path: '/faculty/grading', badge: '8' },
      { label: 'Attendance', icon: 'pi pi-calendar-check', path: '/faculty/attendance' },
      { label: 'Schedule', icon: 'pi pi-calendar', path: '/faculty/schedule' },
      { label: 'Messages', icon: 'pi pi-envelope', path: '/faculty/messages', badge: '1' }
    ]
  }

  return baseItems
})

// breadcrumb items - generates breadcrumb from current route path
const breadcrumbItems = computed(() => {
  const paths = route.path.split('/').filter(p => p)
  const items = [{ label: 'Home', route: '/' }]

  let currentPath = ''
  paths.forEach((path, index) => {
    currentPath += `/${path}`
    items.push({
      label: path.charAt(0).toUpperCase() + path.slice(1).replace(/-/g, ' '),
      route: index === paths.length - 1 ? null : currentPath
    })
  })

  return items
})

// notification refresh interval - polls for new notifications periodically
let notificationRefreshInterval = null

// methods - component functions
const navigateTo = async (path) => {
  // close mobile sidebar if open
  if (isMobile.value) {
    sidebarCollapsed.value = true
  }

  // close any open dropdowns
  showNotifications.value = false
  showUserMenu.value = false
  showSearchResults.value = false

  // navigate and wait for route change
  try {
    await router.push(path)
  } catch (error) {
    // navigation cancelled or redirected - this is normal behavior
    if (error.name !== 'NavigationDuplicated') {
      console.warn('Navigation error:', error)
    }
  }
}

const toggleSubmenu = (path) => {
  const index = expandedMenus.value.indexOf(path)
  if (index > -1) {
    expandedMenus.value.splice(index, 1)
  } else {
    expandedMenus.value.push(path)
  }
}

const isMenuActive = (item) => {
  if (route.path === item.path) return true
  if (item.children) {
    return item.children.some(child => route.path === child.path)
  }
  return false
}

const toggleNotifications = () => {
  showNotifications.value = !showNotifications.value
  showUserMenu.value = false
}

// notification handlers - mark notifications as read
const handleMarkRead = async (notificationId) => {
  try {
    await notificationStore.markAsRead(notificationId)
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Failed to mark notification as read',
      life: 3000
    })
  }
}

const handleMarkAllRead = async () => {
  try {
    await notificationStore.markAllAsRead(authStore.userId)
    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: 'All notifications marked as read',
      life: 2000
    })
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'Failed to mark all notifications as read',
      life: 3000
    })
  }
}

const handleNotificationClick = async (notification) => {
  // mark as read if not already
  if (!notification.read) {
    await handleMarkRead(notification.id)
  }

  // navigate to the action URL if provided
  if (notification.actionUrl) {
    router.push(notification.actionUrl)
    showNotifications.value = false
  }
}

// search functionality - searches users and courses globally
const performSearch = async () => {
  if (!searchQuery.value || searchQuery.value.trim().length < 2) {
    toast.add({
      severity: 'warn',
      summary: 'Search',
      detail: 'Please enter at least 2 characters to search',
      life: 2000
    })
    return
  }

  searchLoading.value = true
  showSearchResults.value = true

  try {
    const response = await api.globalSearch(searchQuery.value.trim())
    searchResults.value = response.data
  } catch (error) {
    console.error('Search error:', error)
    toast.add({
      severity: 'error',
      summary: 'Search Failed',
      detail: 'Unable to perform search. Please try again.',
      life: 3000
    })
    searchResults.value = { users: [], courses: [], totalResults: 0 }
  } finally {
    searchLoading.value = false
  }
}

const clearSearch = () => {
  searchQuery.value = ''
  showSearchResults.value = false
  searchResults.value = { users: [], courses: [], totalResults: 0 }
}

const navigateToSearchResult = (type, item) => {
  showSearchResults.value = false
  searchQuery.value = ''

  if (type === 'user') {
    router.push(`/profile/${item.id}`)
  } else if (type === 'course') {
    if (authStore.isAdmin) {
      router.push(`/admin/courses`)
    } else if (authStore.isFaculty) {
      router.push(`/faculty/courses`)
    } else {
      router.push(`/student/courses`)
    }
  }
}

// logout handler - logs user out and redirects to login page
const handleLogout = async () => {
  try {
    await authStore.logout()
    toast.add({
      severity: 'success',
      summary: 'Logged Out',
      detail: 'You have been successfully logged out',
      life: 2000
    })
    setTimeout(() => {
      router.push('/login')
    }, 500)
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Logout Failed',
      detail: 'An error occurred during logout',
      life: 3000
    })
  }
}

// lifecycle - component mount and unmount
onMounted(async () => {
  // ensure light mode is always active (remove any dark mode classes)
  document.documentElement.classList.remove('dark-mode')
  document.documentElement.classList.remove('dark')

  // initialize sidebar state for mobile - collapsed by default on small screens
  if (window.innerWidth < 768) {
    sidebarCollapsed.value = true
  }

  // handle window resize for responsive behavior
  const handleResize = () => {
    isMobile.value = window.innerWidth < 768
    if (isMobile.value) {
      sidebarCollapsed.value = true
    }
  }
  window.addEventListener('resize', handleResize)

  // close dropdowns on outside click - improves ux
  document.addEventListener('click', (e) => {
    if (!e.target.closest('.notification-btn') && !e.target.closest('.notification-panel')) {
      showNotifications.value = false
    }
    if (!e.target.closest('.user-menu-btn') && !e.target.closest('.user-menu-panel')) {
      showUserMenu.value = false
    }
    if (!e.target.closest('.search-container') && !e.target.closest('.search-results-panel')) {
      showSearchResults.value = false
    }
  })

  // initialize notifications with token for websocket
  const userId = authStore.userId
  const token = authStore.token
  if (userId) {
    await notificationStore.initialize(userId, token)

    // refresh notification count periodically (every 30 seconds) - TODO: fix later - maybe use websocket only?
    notificationRefreshInterval = setInterval(() => {
      notificationStore.loadUnreadCount(userId)
    }, 30000)
  }
})

// cleanup on unmount - clear intervals and unsubscribe from websocket
onUnmounted(() => {
  if (notificationRefreshInterval) {
    clearInterval(notificationRefreshInterval)
  }
  notificationStore.unsubscribeFromRealTime()
})
</script>

<style scoped>
/* Custom scrollbar */
.custom-scrollbar::-webkit-scrollbar {
  width: 4px;
}

.custom-scrollbar::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
}

.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.25);
}

/* Light scrollbar for search results */
.custom-scrollbar-light::-webkit-scrollbar {
  width: 6px;
}

.custom-scrollbar-light::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 10px;
}

.custom-scrollbar-light::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 10px;
}

.custom-scrollbar-light::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* Sidebar animated background glows */
.sidebar-glow-1 {
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 30%, rgba(99, 102, 241, 0.15) 0%, transparent 50%);
  animation: glow-rotate 20s linear infinite;
}

.sidebar-glow-2 {
  position: absolute;
  bottom: -50%;
  right: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 70% 70%, rgba(236, 72, 153, 0.1) 0%, transparent 50%);
  animation: glow-rotate 25s linear infinite reverse;
}

@keyframes glow-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* Logo glow effect */
.logo-glow {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.4) 0%, rgba(139, 92, 246, 0.3) 100%);
  filter: blur(12px);
  animation: logo-pulse 3s ease-in-out infinite;
}

@keyframes logo-pulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.1); }
}

/* Avatar glow */
.avatar-glow {
  background: linear-gradient(135deg, #6366f1 0%, #ec4899 100%);
  filter: blur(15px);
  opacity: 0.4;
  animation: avatar-pulse 4s ease-in-out infinite;
}

@keyframes avatar-pulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.05); }
}

/* User card shimmer effect */
.user-card-shimmer {
  animation: user-card-shimmer 3s ease-in-out infinite;
}

@keyframes user-card-shimmer {
  0% { transform: translateX(-100%); }
  50%, 100% { transform: translateX(200%); }
}

/* User card hover enhancement */
.user-card:hover {
  border-color: rgba(255, 255, 255, 0.2) !important;
  box-shadow: 0 8px 32px rgba(99, 102, 241, 0.15);
}

/* Menu item hover glow that follows cursor */
.item-hover-glow {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  pointer-events: none;
  z-index: 0;
}

/* Active menu item glow */
.active-glow {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 60%;
  background: linear-gradient(180deg, #6366f1 0%, #a855f7 50%, #ec4899 100%);
  border-radius: 0 4px 4px 0;
  box-shadow: 0 0 12px rgba(99, 102, 241, 0.6), 0 0 24px rgba(99, 102, 241, 0.3);
  animation: glow-pulse-indicator 2s ease-in-out infinite;
}

@keyframes glow-pulse-indicator {
  0%, 100% { opacity: 1; box-shadow: 0 0 12px rgba(99, 102, 241, 0.6), 0 0 24px rgba(99, 102, 241, 0.3); }
  50% { opacity: 0.8; box-shadow: 0 0 20px rgba(99, 102, 241, 0.8), 0 0 40px rgba(99, 102, 241, 0.4); }
}

/* Badge redesign */
.badge-wrapper {
  position: relative;
}

.badge-glow {
  position: absolute;
  inset: -2px;
  background: linear-gradient(135deg, #ef4444 0%, #f97316 100%);
  border-radius: 999px;
  filter: blur(6px);
  opacity: 0.5;
  animation: badge-pulse 2s ease-in-out infinite;
}

@keyframes badge-pulse {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.7; transform: scale(1.1); }
}

.badge-pill {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 20px;
  height: 20px;
  padding: 0 6px;
  font-size: 11px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #ef4444 0%, #f97316 100%);
  border-radius: 999px;
}

/* Dock tooltip */
.dock-tooltip {
  position: absolute;
  left: calc(100% + 12px);
  top: 50%;
  transform: translateY(-50%) scale(0.9);
  padding: 8px 14px;
  background: rgba(15, 15, 30, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  white-space: nowrap;
  font-size: 13px;
  font-weight: 500;
  color: white;
  opacity: 0;
  pointer-events: none;
  transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  z-index: 100;
}

.dock-tooltip .tooltip-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 18px;
  height: 18px;
  margin-left: 8px;
  padding: 0 5px;
  font-size: 10px;
  background: linear-gradient(135deg, #ef4444 0%, #f97316 100%);
  border-radius: 999px;
}

.menu-item:hover .dock-tooltip {
  opacity: 1;
  transform: translateY(-50%) scale(1);
}

/* Bottom action buttons */
.bottom-action-btn:hover {
  background: rgba(255, 255, 255, 0.08);
}

.logout-btn:hover {
  background: rgba(239, 68, 68, 0.15);
  color: #f87171;
}

/* Collapse button rotation */
.collapse-btn {
  transition: transform 0.3s ease;
}

.collapse-btn.rotate-180 {
  transform: rotate(180deg);
}

/* Search input glass style */
.search-input-glass {
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important;
  color: white !important;
}

.search-input-glass::placeholder {
  color: rgba(255, 255, 255, 0.4) !important;
}

.search-input-glass:focus {
  background: rgba(255, 255, 255, 0.08) !important;
  border-color: rgba(99, 102, 241, 0.5) !important;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2) !important;
}

/* Search input light mode */
.search-input-light {
  background: #f1f5f9 !important;
  border: 1px solid #e2e8f0 !important;
  color: #1e293b !important;
}

.search-input-light::placeholder {
  color: #94a3b8 !important;
}

.search-input-light:focus {
  background: #ffffff !important;
  border-color: #6366f1 !important;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.15) !important;
}

/* Breadcrumb glass style */
:deep(.breadcrumb-glass .p-breadcrumb) {
  background: transparent;
  border: none;
  padding: 0;
}

/* Notification Badge Animation */
.notification-btn {
  position: relative;
}

/* Transition Animations */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  transform: translateX(-12px);
  opacity: 0;
}

.fade-slide-leave-to {
  transform: translateX(-12px);
  opacity: 0;
}

.scale-enter-active,
.scale-leave-active {
  transition: all 0.25s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.scale-enter-from,
.scale-leave-to {
  transform: scale(0.95);
  opacity: 0;
}

/* Enhanced Page Transitions with Creative Effects */
.page-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.page-leave-active {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.98);
  filter: blur(4px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.99);
  filter: blur(2px);
}

/* Navbar entrance animation */
.navbar {
  animation: navbar-slide-down 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}

@keyframes navbar-slide-down {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Button hover micro-interactions */
.p-button-rounded {
  transition: all 0.2s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.p-button-rounded:hover {
  transform: scale(1.08);
}

.p-button-rounded:active {
  transform: scale(0.95);
}

/* Card hover lift effect */
.card-hover-lift {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.card-hover-lift:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(99, 102, 241, 0.15);
}

/* Ripple effect for interactive elements */
.ripple-effect {
  position: relative;
  overflow: hidden;
}

.ripple-effect::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.3) 0%, transparent 70%);
  transform: scale(0);
  opacity: 0;
  transition: transform 0.5s ease, opacity 0.5s ease;
}

.ripple-effect:active::after {
  transform: scale(2);
  opacity: 1;
  transition: transform 0s, opacity 0s;
}

/* Floating animation for interactive elements */
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-6px);
  }
}

.float-animation {
  animation: float 3s ease-in-out infinite;
}

/* Shimmer loading effect */
@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.shimmer-loading {
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

/* Notification bell shake animation */
@keyframes bell-shake {
  0%, 100% { transform: rotate(0); }
  10%, 30%, 50%, 70%, 90% { transform: rotate(-8deg); }
  20%, 40%, 60%, 80% { transform: rotate(8deg); }
}

.notification-btn:has(.p-badge):hover i {
  animation: bell-shake 0.5s ease-in-out;
}

/* User avatar pulse on hover */
.user-menu-btn:hover .p-avatar {
  animation: avatar-hover-pulse 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes avatar-hover-pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

/* Search input focus animation */
.search-input-light:focus {
  animation: search-focus 0.3s ease-out forwards;
}

@keyframes search-focus {
  0% { box-shadow: 0 0 0 0 rgba(99, 102, 241, 0.4); }
  100% { box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.15); }
}

/* Main content entrance animation */
.main-content {
  animation: main-content-fade-in 0.6s ease-out forwards;
}

@keyframes main-content-fade-in {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Submenu expand animation */
.submenu-expand-enter-active,
.submenu-expand-leave-active {
  transition: all 0.3s ease-out;
  overflow: hidden;
}

.submenu-expand-enter-from,
.submenu-expand-leave-to {
  opacity: 0;
  transform: translateY(-8px);
  max-height: 0;
}

.submenu-expand-enter-to,
.submenu-expand-leave-from {
  max-height: 500px;
}

/* Submenu item stagger animation */
.submenu-item {
  animation: submenu-item-enter 0.3s ease-out forwards;
  opacity: 0;
}

@keyframes submenu-item-enter {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Badge pop animation */
.badge-pop-enter-active {
  animation: badge-pop-in 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.badge-pop-leave-active {
  animation: badge-pop-out 0.2s ease-out;
}

@keyframes badge-pop-in {
  0% { transform: scale(0); opacity: 0; }
  50% { transform: scale(1.2); }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes badge-pop-out {
  from { transform: scale(1); opacity: 1; }
  to { transform: scale(0); opacity: 0; }
}

/* Menu item wrapper entrance animation */
.menu-item-wrapper {
  animation: menu-item-slide-in 0.4s ease-out forwards;
  animation-delay: calc(var(--item-index, 0) * 0.05s);
  opacity: 0;
}

@keyframes menu-item-slide-in {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* Sidebar entrance animation */
.sidebar-wrapper {
  animation: sidebar-entrance 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes sidebar-entrance {
  from {
    opacity: 0;
    transform: translateX(-30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

/* Reduced motion support */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }

  .sidebar-glow-1,
  .sidebar-glow-2,
  .logo-glow,
  .avatar-glow,
  .active-glow,
  .badge-glow {
    animation: none;
  }
}
</style>