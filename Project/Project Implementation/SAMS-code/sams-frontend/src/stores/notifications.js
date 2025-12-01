/*
 * notifications.js - Notification State Management
 *
 * manages all user notifications - loading, marking as read, deleting, etc
 * handles real-time notifications through websocket connection
 * took forever to get the websocket stuff working properly lol
 */

import { defineStore } from 'pinia'
import api from '../services/api'
import websocketService from '../services/websocket'

export const useNotificationStore = defineStore('notifications', {
  // state - stores notification data and preferences
  state: () => ({
    notifications: [], // all notifications for current user
    unreadCount: 0, // count of unread notifications
    loading: false, // loading state for ui
    error: null, // error message if something went wrong
    preferences: null, // user notification preferences
    isConnected: false // websocket connection status
  }),

  // getters - computed properties for filtering notifications
  getters: {
    unreadNotifications: (state) => state.notifications.filter(n => !n.read),
    readNotifications: (state) => state.notifications.filter(n => n.read),
    hasUnread: (state) => state.unreadCount > 0,

    // filter notifications by type - useful for showing specific categories
    notificationsByType: (state) => (type) => {
      return state.notifications.filter(n => n.type === type)
    },

    // get most recent 10 notifications for quick view
    recentNotifications: (state) => {
      return [...state.notifications]
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 10)
    }
  },

  // actions - methods for managing notifications
  actions: {
    // loadNotifications - fetches notifications from backend with pagination
    async loadNotifications(userId, page = 0, size = 20) {
      if (!userId) return

      this.loading = true
      this.error = null

      try {
        const response = await api.getUserNotifications(userId, page, size)
        if (response.data) {
          // backend can return paginated or array response - handle both
          if (response.data.content) {
            this.notifications = response.data.content
          } else if (Array.isArray(response.data)) {
            this.notifications = response.data
          }
        }
      } catch (error) {
        console.error('Failed to load notifications:', error)
        this.error = 'Failed to load notifications'
        // return empty array on error - no fake data
        this.notifications = []
      } finally {
        this.loading = false
      }
    },

    // loadUnreadCount - gets count of unread notifications
    async loadUnreadCount(userId) {
      if (!userId) return

      try {
        const response = await api.getUnreadNotificationCount(userId)
        // backend response format varies - handle both
        if (response.data?.count !== undefined) {
          this.unreadCount = response.data.count
        } else if (typeof response.data === 'number') {
          this.unreadCount = response.data
        }
      } catch (error) {
        console.error('Failed to load unread count:', error)
        // fallback - calculate from local data
        this.unreadCount = this.notifications.filter(n => !n.read).length
      }
    },

    // loadUnreadNotifications - fetches only unread notifications
    async loadUnreadNotifications(userId) {
      if (!userId) return

      try {
        const response = await api.getUnreadNotifications(userId)
        if (response.data) {
          const unread = Array.isArray(response.data) ? response.data : []
          // merge with existing notifications - avoid duplicates
          const existingIds = this.notifications.map(n => n.id)
          unread.forEach(n => {
            if (!existingIds.includes(n.id)) {
              this.notifications.unshift(n) // add to beginning
            }
          })
          this.unreadCount = unread.length
        }
      } catch (error) {
        console.error('Failed to load unread notifications:', error)
      }
    },

    // markAsRead - marks single notification as read
    async markAsRead(notificationId) {
      try {
        await api.markNotificationAsRead(notificationId)
        // update local state immediately for better ux
        const notification = this.notifications.find(n => n.id === notificationId)
        if (notification) {
          notification.read = true
          notification.readAt = new Date().toISOString()
        }
        this.unreadCount = Math.max(0, this.unreadCount - 1) // prevent negative counts
      } catch (error) {
        console.error('Failed to mark notification as read:', error)
        throw error
      }
    },

    // markAllAsRead - marks all notifications as read at once
    async markAllAsRead(userId) {
      if (!userId) return

      try {
        await api.markAllNotificationsAsRead(userId)
        this.notifications.forEach(n => {
          n.read = true
          n.readAt = new Date().toISOString()
        })
        this.unreadCount = 0
      } catch (error) {
        console.error('Failed to mark all as read:', error)
        throw error
      }
    },

    async deleteNotification(notificationId) {
      try {
        await api.deleteNotification(notificationId)
        const index = this.notifications.findIndex(n => n.id === notificationId)
        if (index > -1) {
          const notification = this.notifications[index]
          if (!notification.read) {
            this.unreadCount = Math.max(0, this.unreadCount - 1)
          }
          this.notifications.splice(index, 1)
        }
      } catch (error) {
        console.error('Failed to delete notification:', error)
        throw error
      }
    },

    async deleteReadNotifications(userId) {
      if (!userId) return

      try {
        await api.deleteReadNotifications(userId)
        this.notifications = this.notifications.filter(n => !n.read)
      } catch (error) {
        console.error('Failed to delete read notifications:', error)
        throw error
      }
    },

    async loadPreferences(userId) {
      if (!userId) return

      try {
        const response = await api.getNotificationPreferences(userId)
        this.preferences = response.data
      } catch (error) {
        console.error('Failed to load notification preferences:', error)
        // Default preferences
        this.preferences = {
          emailNotifications: true,
          enrollmentAlerts: true,
          gradeAlerts: true,
          assignmentAlerts: true,
          messageAlerts: true,
          connectionAlerts: true,
          deadlineReminders: true,
          reminderDaysBefore: 3
        }
      }
    },

    async updatePreferences(userId, preferences) {
      if (!userId) return

      try {
        const response = await api.updateNotificationPreferences(userId, preferences)
        this.preferences = response.data
      } catch (error) {
        console.error('Failed to update notification preferences:', error)
        throw error
      }
    },

    // subscribeToRealTime - connects to websocket for live notifications
    // this was really tricky to get working with teh reconnection logic
    async subscribeToRealTime(userId, token) {
      if (!userId || this.isConnected) return

      try {
        // connect websocket first with auth token
        if (token && !websocketService.isConnected()) {
          await websocketService.connect(token)
        }

        // subscribe to user-specific notifications
        websocketService.subscribeToNotifications(userId, (notification) => {
          // add new notification to beginning of list
          this.notifications.unshift(notification)
          this.unreadCount++

          // show browser notification if user allowed it
          this.showBrowserNotification(notification)
        })

        // also subscribe to system-wide broadcast notifications
        websocketService.subscribeToBroadcast((notification) => {
          this.notifications.unshift(notification)
          this.unreadCount++
          this.showBrowserNotification(notification)
        })

        this.isConnected = true
      } catch (error) {
        console.error('Failed to subscribe to real-time notifications:', error)
        // app should still work without real-time updates - just slower
      }
    },

    // unsubscribeFromRealTime - disconnect from websocket notifications
    unsubscribeFromRealTime() {
      // clean up subscriptions
      if (websocketService) {
        websocketService.unsubscribe('notifications')
        websocketService.unsubscribe('broadcast')
      }
      this.isConnected = false
    },

    // showBrowserNotification - shows native browser notification
    showBrowserNotification(notification) {
      // only show if user has granted permission
      if ('Notification' in window && Notification.permission === 'granted') {
        new Notification(notification.title, {
          body: notification.message,
          icon: '/logo.png', // TODO: fix later - add actual logo path
          tag: notification.id?.toString()
        })
      }
    },

    // requestNotificationPermission - asks user for browser notification permission
    async requestNotificationPermission() {
      if ('Notification' in window && Notification.permission === 'default') {
        const permission = await Notification.requestPermission()
        return permission === 'granted'
      }
      return Notification.permission === 'granted'
    },

    // addNotification - adds local notification without backend call
    // useful for immediate feedback or testing
    addNotification(notification) {
      const newNotification = {
        id: Date.now(),
        read: false,
        createdAt: new Date().toISOString(),
        ...notification
      }
      this.notifications.unshift(newNotification)
      this.unreadCount++
      return newNotification
    },

    // initialize - sets up notifications when user logs in
    // loads all notification data and connects websocket
    async initialize(userId, token) {
      if (!userId) return

      // load all notification data in parallel for speed
      await Promise.all([
        this.loadNotifications(userId),
        this.loadUnreadCount(userId),
        this.loadPreferences(userId)
      ])

      // connect to real-time updates with auth token
      await this.subscribeToRealTime(userId, token)
      this.requestNotificationPermission() // ask for browser notifications
    },

    // reset - clears all notification state (used on logout)
    reset() {
      this.notifications = []
      this.unreadCount = 0
      this.loading = false
      this.error = null
      this.preferences = null
      this.unsubscribeFromRealTime() // disconnect websocket
    },

  }
})
