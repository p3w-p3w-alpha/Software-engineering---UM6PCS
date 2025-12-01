/*
 * websocket.js - WebSocket Service for Real-time Communication
 *
 * handles websocket connections using STOMP over SockJS
 * manages subscriptions for notifications, messages, study groups, etc
 * includes auto-reconnect logic with exponential backoff
 * this was wierd to debug at first - took forever to get reconnection working
 */

import SockJS from 'sockjs-client/dist/sockjs'
import { Stomp } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    // connection state
    this.stompClient = null
    this.connected = false
    this.connecting = false

    // subscription management - keeps track of all active subscriptions
    this.subscriptions = new Map()

    // reconnection logic with exponential backoff - works for now
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 1000 // start with 1 second, gets longer each time

    // connection status listeners for ui updates
    this.connectionListeners = []

    // auth token for reconnection
    this.pendingToken = null

    // suppress warnings when intentionally disconnected
    this.silentMode = false

    // queue subscriptions when not connected - handles timing issues
    this.pendingSubscriptions = []
  }

  // add connection status listener - components can subscribe to connection changes
  addConnectionListener(callback) {
    this.connectionListeners.push(callback)
    // immediately notify of current status so ui updates right away
    callback(this.connected)
  }

  removeConnectionListener(callback) {
    // remove listener when component unmounts - prevents memory leaks
    this.connectionListeners = this.connectionListeners.filter(cb => cb !== callback)
  }

  // notify all listeners about connection status change
  notifyConnectionStatus() {
    this.connectionListeners.forEach(callback => {
      try {
        callback(this.connected)
      } catch (e) {
        console.error('Error in connection listener:', e)
      }
    })
  }

  // connect to websocket server with auth token
  // handles reconnection automatically if connection drops
  connect(token) {
    // prevent multiple simultaneous connection attempts - causes wierd issues
    if (this.connecting) {
      return Promise.resolve()
    }

    // already connected - no need to reconnect
    if (this.connected && this.stompClient) {
      return Promise.resolve()
    }

    this.pendingToken = token
    this.connecting = true

    return new Promise((resolve, reject) => {
      try {
        const socket = new SockJS('http://localhost:8080/ws')
        this.stompClient = Stomp.over(socket)

        // disable verbose STOMP debug logging in production - too noisy
        this.stompClient.debug = () => {}

        // add auth token to connection headers
        const headers = token ? { Authorization: `Bearer ${token}` } : {}

        // attempt connection with callbacks for success/error
        this.stompClient.connect(
          headers,
          (frame) => {
            // connection successful - reset all reconnection state
            this.connected = true
            this.connecting = false
            this.reconnectAttempts = 0
            this.reconnectDelay = 1000
            this.silentMode = false
            this.notifyConnectionStatus()
            // process any pending subscriptions that were queued
            this.processPendingSubscriptions()
            resolve(frame)
          },
          (error) => {
            console.error('WebSocket connection error:', error)
            this.connected = false
            this.connecting = false
            this.notifyConnectionStatus()

            // attempt reconnection with exponential backoff - definately helps
            if (this.reconnectAttempts < this.maxReconnectAttempts && this.pendingToken) {
              this.scheduleReconnect()
            }
            reject(error)
          }
        )

        // handle connection lost unexpectedly
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

  // schedule reconnection attempt with exponential backoff
  // delay increases each time: 1s, 2s, 4s, 8s, 16s, max 30s
  scheduleReconnect() {
    this.reconnectAttempts++
    const delay = Math.min(this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1), 30000)

    setTimeout(() => {
      if (!this.connected && this.pendingToken) {
        this.connect(this.pendingToken).catch(() => {
          // error already handled in connect method
        })
      }
    }, delay)
  }

  // disconnect from websocket - called on logout
  disconnect() {
    this.pendingToken = null
    this.reconnectAttempts = this.maxReconnectAttempts // prevent auto-reconnect
    this.silentMode = true // suppress warnings after intentional disconnect
    this.pendingSubscriptions = [] // clear pending subscriptions

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

  // process pending subscriptions after reconnection
  // resubscribes to everything that was subscribed before disconnect
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

  // subscribe to user-specific notifications channel
  // recieves notifications in real-time for current user
  subscribeToNotifications(userId, callback) {
    if (!this.connected || !this.stompClient) {
      // queue subscription for when connected - handles timing issues
      if (!this.silentMode) {
        this.pendingSubscriptions.push({ type: 'notifications', args: [userId, callback] })
      }
      return null
    }

    // subscribe to user-specific queue - backend sends messages here
    const destination = `/user/${userId}/queue/notifications`
    try {
      const subscription = this.stompClient.subscribe(destination, (message) => {
        try {
          const notification = JSON.parse(message.body)
          callback(notification) // trigger callback with notification data
        } catch (e) {
          console.error('Error parsing notification:', e)
        }
      })

      // save subscription for later unsubscribe
      this.subscriptions.set('notifications', subscription)
      return subscription
    } catch (e) {
      console.error('Error subscribing to notifications:', e)
      return null
    }
  }

  // subscribe to user-specific messages channel for private messaging
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

  // subscribe to study group messages - all group members recieve these
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

  // subscribe to broadcast notifications - system-wide announcements
  // everyone who's connected gets these messages
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

  // send message to study group via websocket
  // fallback to http api if websocket not connected
  sendGroupMessage(groupId, message) {
    if (!this.connected || !this.stompClient) {
      // silent fail when not connected - messages should go through HTTP API fallback
      return false
    }

    try {
      // send to backend endpoint that broadcasts to group
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

  // send private message via websocket
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

  // subscribe to specific conversation between two users
  // gets real-time updates when other person sends message
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

  // subscribe to typing indicators for a conversation
  // shows when other person is typing - not critical, just nice to have
  subscribeToTyping(otherUserId, callback) {
    if (!this.connected || !this.stompClient) {
      // typing indicators are not critical, don't queue them
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

  // send typing indicator to other user
  // lets them know we're typing a message
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

  // unsubscribe from a specific subscription
  // called when component unmounts or user navigates away
  unsubscribe(key) {
    const subscription = this.subscriptions.get(key)
    if (subscription) {
      try {
        subscription.unsubscribe()
      } catch (e) {
        // ignore unsubscribe errors - probably already disconnected
      }
      this.subscriptions.delete(key)
    }
  }

  // check if websocket is currently connected
  isConnected() {
    return this.connected
  }

  // get detailed connection status for ui display
  getConnectionStatus() {
    if (this.connected) return 'connected'
    if (this.connecting) return 'connecting'
    if (this.reconnectAttempts > 0 && this.reconnectAttempts < this.maxReconnectAttempts) return 'reconnecting'
    return 'disconnected'
  }
}

export default new WebSocketService()
