import SockJS from 'sockjs-client/dist/sockjs'
import { Stomp } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.connected = false
    this.connecting = false
    this.subscriptions = new Map()
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 1000 // Start with 1 second
    this.connectionListeners = []
    this.pendingToken = null
    this.silentMode = false // Suppress warnings when intentionally disconnected
    this.pendingSubscriptions = [] // Queue subscriptions when not connected
  }

  // Add connection status listener
  addConnectionListener(callback) {
    this.connectionListeners.push(callback)
    // Immediately notify of current status
    callback(this.connected)
  }

  removeConnectionListener(callback) {
    this.connectionListeners = this.connectionListeners.filter(cb => cb !== callback)
  }

  notifyConnectionStatus() {
    this.connectionListeners.forEach(callback => {
      try {
        callback(this.connected)
      } catch (e) {
        console.error('Error in connection listener:', e)
      }
    })
  }

  connect(token) {
    // Prevent multiple simultaneous connection attempts
    if (this.connecting) {
      return Promise.resolve()
    }

    if (this.connected && this.stompClient) {
      return Promise.resolve()
    }

    this.pendingToken = token
    this.connecting = true

    return new Promise((resolve, reject) => {
      try {
        const socket = new SockJS('http://localhost:8080/ws')
        this.stompClient = Stomp.over(socket)

        // Disable verbose STOMP debug logging in production
        this.stompClient.debug = () => {}

        const headers = token ? { Authorization: `Bearer ${token}` } : {}

        this.stompClient.connect(
          headers,
          (frame) => {
            this.connected = true
            this.connecting = false
            this.reconnectAttempts = 0
            this.reconnectDelay = 1000
            this.silentMode = false
            this.notifyConnectionStatus()
            // Process any pending subscriptions
            this.processPendingSubscriptions()
            resolve(frame)
          },
          (error) => {
            console.error('WebSocket connection error:', error)
            this.connected = false
            this.connecting = false
            this.notifyConnectionStatus()

            // Attempt reconnection with exponential backoff
            if (this.reconnectAttempts < this.maxReconnectAttempts && this.pendingToken) {
              this.scheduleReconnect()
            }
            reject(error)
          }
        )

        // Handle connection lost
        socket.onclose = () => {
          if (this.connected) {
            this.connected = false
            this.connecting = false
            this.notifyConnectionStatus()

            // Attempt reconnection
            if (this.reconnectAttempts < this.maxReconnectAttempts && this.pendingToken) {
              this.scheduleReconnect()
            }
          }
        }
      } catch (error) {
        console.error('Error creating WebSocket:', error)
        this.connecting = false
        reject(error)
      }
    })
  }

  scheduleReconnect() {
    this.reconnectAttempts++
    const delay = Math.min(this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1), 30000)

    setTimeout(() => {
      if (!this.connected && this.pendingToken) {
        this.connect(this.pendingToken).catch(() => {
          // Error already handled in connect
        })
      }
    }, delay)
  }

  disconnect() {
    this.pendingToken = null
    this.reconnectAttempts = this.maxReconnectAttempts // Prevent auto-reconnect
    this.silentMode = true // Suppress warnings after intentional disconnect
    this.pendingSubscriptions = [] // Clear pending subscriptions

    if (this.stompClient) {
      // Unsubscribe from all subscriptions
      this.subscriptions.forEach((subscription) => {
        try {
          subscription.unsubscribe()
        } catch (e) {
          // Ignore unsubscribe errors
        }
      })
      this.subscriptions.clear()

      if (this.connected) {
        try {
          this.stompClient.disconnect(() => {
            this.connected = false
            this.notifyConnectionStatus()
          })
        } catch (e) {
          // Ignore disconnect errors
        }
      }

      this.stompClient = null
      this.connected = false
      this.connecting = false
      this.notifyConnectionStatus()
    }
  }

  // Process pending subscriptions after reconnection
  processPendingSubscriptions() {
    const pending = [...this.pendingSubscriptions]
    this.pendingSubscriptions = []
    pending.forEach(({ type, args }) => {
      switch (type) {
        case 'notifications':
          this.subscribeToNotifications(...args)
          break
        case 'messages':
          this.subscribeToMessages(...args)
          break
        case 'group':
          this.subscribeToGroup(...args)
          break
        case 'broadcast':
          this.subscribeToBroadcast(...args)
          break
        case 'conversation':
          this.subscribeToConversation(...args)
          break
      }
    })
  }

  // Subscribe to user-specific notifications
  subscribeToNotifications(userId, callback) {
    if (!this.connected || !this.stompClient) {
      // Queue subscription for when connected
      if (!this.silentMode) {
        this.pendingSubscriptions.push({ type: 'notifications', args: [userId, callback] })
      }
      return null
    }

    const destination = `/user/${userId}/queue/notifications`
    try {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          const notification = JSON.parse(message.body)
          callback(notification)
        } catch (e) {
          console.error('Error parsing notification:', e)
        }
      })

      this.subscriptions.set('notifications', subscription)
      return subscription
    } catch (e) {
      console.error('Error subscribing to notifications:', e)
      return null
    }
  }

  // Subscribe to user-specific messages
  subscribeToMessages(userId, callback) {
    if (!this.connected || !this.stompClient) {
      if (!this.silentMode) {
        this.pendingSubscriptions.push({ type: 'messages', args: [userId, callback] })
      }
      return null
    }

    const destination = `/user/${userId}/queue/messages`
    try {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          const msg = JSON.parse(message.body)
          callback(msg)
        } catch (e) {
          console.error('Error parsing message:', e)
        }
      })

      this.subscriptions.set('messages', subscription)
      return subscription
    } catch (e) {
      console.error('Error subscribing to messages:', e)
      return null
    }
  }

  // Subscribe to study group messages
  subscribeToGroup(groupId, callback) {
    if (!this.connected || !this.stompClient) {
      if (!this.silentMode) {
        this.pendingSubscriptions.push({ type: 'group', args: [groupId, callback] })
      }
      return null
    }

    const destination = `/topic/group/${groupId}`
    try {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          const groupMessage = JSON.parse(message.body)
          callback(groupMessage)
        } catch (e) {
          console.error('Error parsing group message:', e)
        }
      })

      this.subscriptions.set(`group-${groupId}`, subscription)
      return subscription
    } catch (e) {
      console.error('Error subscribing to group:', e)
      return null
    }
  }

  // Subscribe to broadcast notifications
  subscribeToBroadcast(callback) {
    if (!this.connected || !this.stompClient) {
      if (!this.silentMode) {
        this.pendingSubscriptions.push({ type: 'broadcast', args: [callback] })
      }
      return null
    }

    const destination = '/topic/notifications'
    try {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          const notification = JSON.parse(message.body)
          callback(notification)
        } catch (e) {
          console.error('Error parsing broadcast:', e)
        }
      })

      this.subscriptions.set('broadcast', subscription)
      return subscription
    } catch (e) {
      console.error('Error subscribing to broadcast:', e)
      return null
    }
  }

  // Send message to study group
  sendGroupMessage(groupId, message) {
    if (!this.connected || !this.stompClient) {
      // Silent fail when not connected - messages should go through HTTP API fallback
      return false
    }

    try {
      this.stompClient.send('/app/group/send', {}, JSON.stringify({
        groupId,
        ...message
      }))
      return true
    } catch (e) {
      console.error('Error sending group message:', e)
      return false
    }
  }

  // Send private message
  sendPrivateMessage(recipientId, message) {
    if (!this.connected || !this.stompClient) {
      // Silent fail when not connected - messages should go through HTTP API fallback
      return false
    }

    try {
      this.stompClient.send('/app/private/send', {}, JSON.stringify({
        recipientId,
        ...message
      }))
      return true
    } catch (e) {
      console.error('Error sending private message:', e)
      return false
    }
  }

  // Subscribe to specific conversation
  subscribeToConversation(userId, otherUserId, callback) {
    if (!this.connected || !this.stompClient) {
      if (!this.silentMode) {
        this.pendingSubscriptions.push({ type: 'conversation', args: [userId, otherUserId, callback] })
      }
      return null
    }

    const destination = `/user/${userId}/queue/conversation/${otherUserId}`
    try {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          const msg = JSON.parse(message.body)
          callback(msg)
        } catch (e) {
          console.error('Error parsing conversation message:', e)
        }
      })

      this.subscriptions.set(`conversation-${otherUserId}`, subscription)
      return subscription
    } catch (e) {
      console.error('Error subscribing to conversation:', e)
      return null
    }
  }

  // Subscribe to typing indicators
  subscribeToTyping(otherUserId, callback) {
    if (!this.connected || !this.stompClient) {
      // Typing indicators are not critical, don't queue them
      return null
    }

    const destination = `/user/queue/typing/${otherUserId}`
    try {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          const typingData = JSON.parse(message.body)
          callback(typingData.isTyping)
        } catch (e) {
          console.error('Error parsing typing indicator:', e)
        }
      })

      this.subscriptions.set(`typing-${otherUserId}`, subscription)
      return subscription
    } catch (e) {
      console.error('Error subscribing to typing:', e)
      return null
    }
  }

  // Send typing indicator
  sendTypingIndicator(senderId, recipientId, isTyping) {
    if (!this.connected || !this.stompClient) {
      return false
    }

    try {
      this.stompClient.send('/app/typing', {}, JSON.stringify({
        senderId,
        recipientId,
        isTyping
      }))
      return true
    } catch (e) {
      console.error('Error sending typing indicator:', e)
      return false
    }
  }

  unsubscribe(key) {
    const subscription = this.subscriptions.get(key)
    if (subscription) {
      try {
        subscription.unsubscribe()
      } catch (e) {
        // Ignore unsubscribe errors
      }
      this.subscriptions.delete(key)
    }
  }

  isConnected() {
    return this.connected
  }

  getConnectionStatus() {
    if (this.connected) return 'connected'
    if (this.connecting) return 'connecting'
    if (this.reconnectAttempts > 0 && this.reconnectAttempts < this.maxReconnectAttempts) return 'reconnecting'
    return 'disconnected'
  }
}

export default new WebSocketService()
