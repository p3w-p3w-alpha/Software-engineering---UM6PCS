import SockJS from 'sockjs-client/dist/sockjs'
import { Stomp } from '@stomp/stompjs'

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.connected = false
    this.subscriptions = new Map()
  }

  connect(token) {
    return new Promise((resolve, reject) => {
      const socket = new SockJS('http://localhost:8080/ws')
      this.stompClient = Stomp.over(socket)

      // Set auth header
      const headers = token ? { Authorization: `Bearer ${token}` } : {}

      this.stompClient.connect(
        headers,
        (frame) => {
          console.log('Connected to WebSocket:', frame)
          this.connected = true
          resolve(frame)
        },
        (error) => {
          console.error('WebSocket connection error:', error)
          this.connected = false
          reject(error)
        }
      )
    })
  }

  disconnect() {
    if (this.stompClient && this.connected) {
      // Unsubscribe from all subscriptions
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe()
      })
      this.subscriptions.clear()

      this.stompClient.disconnect(() => {
        console.log('Disconnected from WebSocket')
        this.connected = false
      })
    }
  }

  // Subscribe to user-specific notifications
  subscribeToNotifications(userId, callback) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = `/user/${userId}/queue/notifications`
    const subscription = this.stompClient.subscribe(destination, (message) => {
      const notification = JSON.parse(message.body)
      callback(notification)
    })

    this.subscriptions.set('notifications', subscription)
    return subscription
  }

  // Subscribe to user-specific messages
  subscribeToMessages(userId, callback) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = `/user/${userId}/queue/messages`
    const subscription = this.stompClient.subscribe(destination, (message) => {
      const msg = JSON.parse(message.body)
      callback(msg)
    })

    this.subscriptions.set('messages', subscription)
    return subscription
  }

  // Subscribe to study group messages
  subscribeToGroup(groupId, callback) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = `/topic/group/${groupId}`
    const subscription = this.stompClient.subscribe(destination, (message) => {
      const groupMessage = JSON.parse(message.body)
      callback(groupMessage)
    })

    this.subscriptions.set(`group-${groupId}`, subscription)
    return subscription
  }

  // Subscribe to broadcast notifications
  subscribeToBroadcast(callback) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return null
    }

    const destination = '/topic/notifications'
    const subscription = this.stompClient.subscribe(destination, (message) => {
      const notification = JSON.parse(message.body)
      callback(notification)
    })

    this.subscriptions.set('broadcast', subscription)
    return subscription
  }

  // Send message to study group
  sendGroupMessage(groupId, message) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return
    }

    this.stompClient.send('/app/group/send', {}, JSON.stringify({
      groupId,
      ...message
    }))
  }

  // Send private message
  sendPrivateMessage(recipientId, message) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket not connected')
      return
    }

    this.stompClient.send('/app/private/send', {}, JSON.stringify({
      recipientId,
      ...message
    }))
  }

  unsubscribe(key) {
    const subscription = this.subscriptions.get(key)
    if (subscription) {
      subscription.unsubscribe()
      this.subscriptions.delete(key)
    }
  }

  isConnected() {
    return this.connected
  }
}

export default new WebSocketService()
