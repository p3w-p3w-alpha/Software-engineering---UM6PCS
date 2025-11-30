import { defineStore } from 'pinia'
import api from '../services/api'
import websocketService from '../services/websocket'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('token') || null,
    isAuthenticated: !!localStorage.getItem('token')
  }),

  getters: {
    isAdmin: (state) => ['ADMIN', 'SUPER_ADMIN'].includes(state.user?.role),
    isSuperAdmin: (state) => state.user?.role === 'SUPER_ADMIN',
    isStudent: (state) => state.user?.role === 'STUDENT',
    isFaculty: (state) => state.user?.role === 'FACULTY',
    userRole: (state) => state.user?.role || '',
    userName: (state) => state.user?.username || '',
    userId: (state) => state.user?.userId || null,
    userPermissions: (state) => state.user?.permissions || []
  },

  actions: {
    async login(credentials) {
      try {
        const response = await api.login(credentials)
        const data = response.data

        this.token = data.token
        this.user = {
          userId: data.userId,
          username: data.username,
          email: data.email,
          role: data.role,
          permissions: data.permissions || []
        }
        this.isAuthenticated = true

        // Store in localStorage
        localStorage.setItem('token', data.token)
        localStorage.setItem('user', JSON.stringify(this.user))

        return { success: true, data }
      } catch (error) {
        console.error('Login error:', error)
        return {
          success: false,
          message: error.response?.data || 'Login failed'
        }
      }
    },

    logout() {
      // Disconnect WebSocket before clearing auth state
      if (websocketService) {
        websocketService.disconnect()
      }

      this.user = null
      this.token = null
      this.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    async validateToken() {
      if (!this.token) return false

      try {
        const response = await api.validateToken(this.token)
        return response.data.valid
      } catch (error) {
        this.logout()
        return false
      }
    },

    hasPermission(permission) {
      // Super admin has all permissions
      if (this.isSuperAdmin) return true

      // Check if user has the specific permission
      return this.userPermissions.includes(permission)
    }
  }
})
