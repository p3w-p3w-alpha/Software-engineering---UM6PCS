import { defineStore } from 'pinia'
import api from '../services/api'
import websocketService from '../services/websocket'

export const useNotificationStore = defineStore('notifications', {
  state: () => ({
    notifications: [],
    unreadCount: 0,
    loading: false,
    error: null,
    preferences: null,
    isConnected: false
  }),

  getters: {
    unreadNotifications: (state) => state.notifications.filter(n => !n.read),
    readNotifications: (state) => state.notifications.filter(n => n.read),
    hasUnread: (state) => state.unreadCount > 0,

    notificationsByType: (state) => (type) => {
      return state.notifications.filter(n => n.type === type)
    },

    recentNotifications: (state) => {
      return [...state.notifications]
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 10)
    }
  },

  actions: {
    async loadNotifications(userId, page = 0, size = 20) {
      if (!userId) return

      this.loading = true
      this.error = null

      try {
        const response = await api.getUserNotifications(userId, page, size)
        if (response.data) {
          // Handle paginated response
          if (response.data.content) {
            this.notifications = response.data.content
          } else if (Array.isArray(response.data)) {
            this.notifications = response.data
          }
        }
      } catch (error) {
        console.error('Failed to load notifications:', error)
        this.error = 'Failed to load notifications'
        // Return empty notifications on error - no mock data
        this.notifications = []
      } finally {
        this.loading = false
      }
    },

    async loadUnreadCount(userId) {
      if (!userId) return

      try {
        const response = await api.getUnreadNotificationCount(userId)
        if (response.data?.count !== undefined) {
          this.unreadCount = response.data.count
        } else if (typeof response.data === 'number') {
          this.unreadCount = response.data
        }
      } catch (error) {
        console.error('Failed to load unread count:', error)
        // Calculate from local notifications
        this.unreadCount = this.notifications.filter(n => !n.read).length
      }
    },

    async loadUnreadNotifications(userId) {
      if (!userId) return

      try {
        const response = await api.getUnreadNotifications(userId)
        if (response.data) {
          const unread = Array.isArray(response.data) ? response.data : []
          // Merge with existing notifications
          const existingIds = this.notifications.map(n => n.id)
          unread.forEach(n => {
            if (!existingIds.includes(n.id)) {
              this.notifications.unshift(n)
            }
          })
          this.unreadCount = unread.length
        }
      } catch (error) {
        console.error('Failed to load unread notifications:', error)
      }
    },

    async markAsRead(notificationId) {
      try {
        await api.markNotificationAsRead(notificationId)
        const notification = this.notifications.find(n => n.id === notificationId)
        if (notification) {
          notification.read = true
          notification.readAt = new Date().toISOString()
        }
        this.unreadCount = Math.max(0, this.unreadCount - 1)
      } catch (error) {
        console.error('Failed to mark notification as read:', error)
        throw error
      }
    },

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

    // WebSocket subscription for real-time notifications
    async subscribeToRealTime(userId, token) {
      if (!userId || this.isConnected) return

      try {
        // Connect WebSocket with token first
        if (token && !websocketService.isConnected()) {
          await websocketService.connect(token)
        }

        // Subscribe to user notifications
        websocketService.subscribeToNotifications(userId, (notification) => {
          // Add new notification at the beginning
          this.notifications.unshift(notification)
          this.unreadCount++

          // Show browser notification if permitted
          this.showBrowserNotification(notification)
        })

        // Also subscribe to broadcast notifications
        websocketService.subscribeToBroadcast((notification) => {
          this.notifications.unshift(notification)
          this.unreadCount++
          this.showBrowserNotification(notification)
        })

        this.isConnected = true
      } catch (error) {
        console.error('Failed to subscribe to real-time notifications:', error)
        // Don't fail silently - the app should still work without real-time
      }
    },

    unsubscribeFromRealTime() {
      // Unsubscribe from all notification-related subscriptions
      if (websocketService) {
        websocketService.unsubscribe('notifications')
        websocketService.unsubscribe('broadcast')
      }
      this.isConnected = false
    },

    showBrowserNotification(notification) {
      if ('Notification' in window && Notification.permission === 'granted') {
        new Notification(notification.title, {
          body: notification.message,
          icon: '/logo.png',
          tag: notification.id?.toString()
        })
      }
    },

    async requestNotificationPermission() {
      if ('Notification' in window && Notification.permission === 'default') {
        const permission = await Notification.requestPermission()
        return permission === 'granted'
      }
      return Notification.permission === 'granted'
    },

    // Add a local notification (for testing or immediate feedback)
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

    // Initialize notifications for a user
    async initialize(userId, token) {
      if (!userId) return

      await Promise.all([
        this.loadNotifications(userId),
        this.loadUnreadCount(userId),
        this.loadPreferences(userId)
      ])

      // Subscribe to real-time with token for WebSocket auth
      await this.subscribeToRealTime(userId, token)
      this.requestNotificationPermission()
    },

    // Clear all notification state
    reset() {
      this.notifications = []
      this.unreadCount = 0
      this.loading = false
      this.error = null
      this.preferences = null
      this.unsubscribeFromRealTime()
    },

  }
})
