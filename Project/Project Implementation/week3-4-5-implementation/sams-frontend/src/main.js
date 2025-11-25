import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import './style.css'
import App from './App.vue'

const app = createApp(App)

// Initialize Pinia store
app.use(createPinia())

// Initialize Vue Router
app.use(router)

// Mount the app
app.mount('#app')
