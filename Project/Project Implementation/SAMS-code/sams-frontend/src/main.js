/*
 * main.js - Application Entry Point
 *
 * This is where everything starts - sets up teh vue app with all its plugins
 * handles theme configuration, animations, and global services
 * took a while to get all these imports working together lol
 */

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import PrimeVue from 'primevue/config'
import { definePreset } from '@primevue/themes'
import Aura from '@primevue/themes/aura'
import ToastService from 'primevue/toastservice'
import ConfirmationService from 'primevue/confirmationservice'
import Tooltip from 'primevue/tooltip'
import Ripple from 'primevue/ripple'
import AnimateOnScroll from 'primevue/animateonscroll'
import Vue3Toastify from 'vue3-toastify'
import 'vue3-toastify/dist/index.css'
import { autoAnimatePlugin } from '@formkit/auto-animate/vue'
import { MotionPlugin } from '@vueuse/motion'
import './style.css'
import 'primeicons/primeicons.css'
import 'animate.css'
import App from './App.vue'

// custom theme preset - defines our color scheme
// based on PrimeVue Aura but with our blue colors
const SAMSPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{blue.50}',
      100: '{blue.100}',
      200: '{blue.200}',
      300: '{blue.300}',
      400: '{blue.400}',
      500: '{blue.500}',
      600: '{blue.600}',
      700: '{blue.700}',
      800: '{blue.800}',
      900: '{blue.900}',
      950: '{blue.950}'
    },
    colorScheme: {
      light: {
        primary: {
          color: 'rgb(59, 130, 246)',
          inverseColor: '#ffffff',
          hoverColor: 'rgb(37, 99, 235)',
          activeColor: 'rgb(29, 78, 216)'
        },
        highlight: {
          background: 'rgba(59, 130, 246, 0.1)',
          focusBackground: 'rgba(59, 130, 246, 0.2)',
          color: 'rgba(59, 130, 246, 1)',
          focusColor: 'rgba(37, 99, 235, 1)'
        },
        surface: {
          0: '#ffffff',
          50: '{gray.50}',
          100: '{gray.100}',
          200: '{gray.200}',
          300: '{gray.300}',
          400: '{gray.400}',
          500: '{gray.500}',
          600: '{gray.600}',
          700: '{gray.700}',
          800: '{gray.800}',
          900: '{gray.900}',
          950: '{gray.950}'
        }
      },
      dark: {
        primary: {
          color: 'rgb(96, 165, 250)',
          inverseColor: '{gray.950}',
          hoverColor: 'rgb(147, 197, 253)',
          activeColor: 'rgb(191, 219, 254)'
        },
        highlight: {
          background: 'rgba(96, 165, 250, 0.16)',
          focusBackground: 'rgba(96, 165, 250, 0.24)',
          color: 'rgba(147, 197, 253, 1)',
          focusColor: 'rgba(191, 219, 254, 1)'
        },
        surface: {
          0: '#18181b',
          50: '{zinc.50}',
          100: '{zinc.100}',
          200: '{zinc.200}',
          300: '{zinc.300}',
          400: '{zinc.400}',
          500: '{zinc.500}',
          600: '{zinc.600}',
          700: '{zinc.700}',
          800: '{zinc.800}',
          900: '{zinc.900}',
          950: '{zinc.950}'
        }
      }
    }
  }
})

// toast notification settings - position, timing, etc
// these settings control how notifications appear on screen
const toastOptions = {
  position: 'top-right',
  autoClose: 4000,
  closeOnClick: true,
  pauseOnFocusLoss: true,
  pauseOnHover: true,
  draggable: true,
  hideProgressBar: false,
  closeButton: true,
  icon: true,
  rtl: false,
  transition: 'bounce',
  limit: 5,
  newestOnTop: true
}

// create the main vue app instance
const app = createApp(App)

// setup state management with pinia - handles auth, notifications, etc
app.use(createPinia())

// setup routing - handles navigation between pages
app.use(router)

// motion plugin for fancy animations (definately makes things look nicer)
app.use(MotionPlugin)

// auto-animate makes list transitions smooth automatically - works like magic
app.use(autoAnimatePlugin)

// toast service for showing success/error messages
app.use(Vue3Toastify, toastOptions)

// primevue ui library with our custom theme
app.use(PrimeVue, {
  theme: {
    preset: SAMSPreset,
    options: {
      darkModeSelector: '.dark-mode',
      cssLayer: false
    }
  },
  ripple: true
})

// additional primevue services for toasts and confirmations
app.use(ToastService)
app.use(ConfirmationService)

// register global directives - can use these in any component
app.directive('tooltip', Tooltip) // v-tooltip for hover tips
app.directive('ripple', Ripple) // v-ripple for button effects
app.directive('animateonscroll', AnimateOnScroll) // v-animateonscroll for scroll animations

// mount teh app to the DOM - everything starts here
app.mount('#app')
