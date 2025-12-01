/*
 * auth.js - Authentication State Management
 *
 * handles user login, logout, and auth state
 * stores user info and token in localStorage so it persists across page refreshes
 * this was tricky to get right with teh websocket integration
 */

import { defineStore } from 'pinia'
import api from '../services/api'
import websocketService from '../services/websocket'

export const useAuthStore = defineStore('auth', {
  // state - stores user data and auth status
  state: () => ({
    user: JSON.parse(localStorage.getItem('user')) || null, // restore user from localStorage
    token: localStorage.getItem('token') || null, // jwt token for api calls
    isAuthenticated: !!localStorage.getItem('token') // check if user is logged in
  }),

  // getters - computed properties for checking user roles and permissions
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

  // actions - methods for login, logout, etc
  actions: {
    // login - authenticates user and saves thier info
    async login(credentials) {
      try {
        // call backend login endpoint
        const response = await api.login(credentials)
        const data = response.data

        // save token and user info to state
        this.token = data.token
        this.user = {
          userId: data.userId,
          username: data.username,
          email: data.email,
          role: data.role,
          permissions: data.permissions || []
        }
        this.isAuthenticated = true

        // persist to localStorage so user stays logged in
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

    // logout - clears user session and disconnects websocket
    logout() {
      // disconnect websocket before clearing auth
      // TODO: fix later - sometimes this throws an error but works anyway
      if (websocketService) {
        websocketService.disconnect()
      }

      // clear all user data
      this.user = null
      this.token = null
      this.isAuthenticated = false
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    // validateToken - checks if current token is still valid
    async validateToken() {
      if (!this.token) return false

      try {
        // ask backend if token is still good
        const response = await api.validateToken(this.token)
        return response.data.valid
      } catch (error) {
        // token is invalid or expired - log user out
        this.logout()
        return false
      }
    },

    // hasPermission - check if user has a specific permission
    hasPermission(permission) {
      // super admins can do anything - definately useful
      if (this.isSuperAdmin) return true

      // check if user has teh specific permission
      return this.userPermissions.includes(permission)
    }
  }
})
