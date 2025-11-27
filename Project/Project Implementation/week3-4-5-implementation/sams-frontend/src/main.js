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
import './style.css'
import 'primeicons/primeicons.css'
import 'animate.css'
import App from './App.vue'

// Custom theme preset
const SAMSPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '{indigo.50}',
      100: '{indigo.100}',
      200: '{indigo.200}',
      300: '{indigo.300}',
      400: '{indigo.400}',
      500: '{indigo.500}',
      600: '{indigo.600}',
      700: '{indigo.700}',
      800: '{indigo.800}',
      900: '{indigo.900}',
      950: '{indigo.950}'
    },
    colorScheme: {
      light: {
        primary: {
          color: '{indigo.500}',
          inverseColor: '#ffffff',
          hoverColor: '{indigo.600}',
          activeColor: '{indigo.700}'
        },
        highlight: {
          background: 'rgba(99, 102, 241, 0.16)',
          focusBackground: 'rgba(99, 102, 241, 0.24)',
          color: 'rgba(99, 102, 241, 1)',
          focusColor: 'rgba(99, 102, 241, 1)'
        }
      },
      dark: {
        primary: {
          color: '{indigo.400}',
          inverseColor: '{gray.900}',
          hoverColor: '{indigo.300}',
          activeColor: '{indigo.200}'
        },
        highlight: {
          background: 'rgba(99, 102, 241, 0.16)',
          focusBackground: 'rgba(99, 102, 241, 0.24)',
          color: 'rgba(165, 180, 252, 1)',
          focusColor: 'rgba(165, 180, 252, 1)'
        }
      }
    }
  }
})

const app = createApp(App)

// Initialize Pinia store
app.use(createPinia())

// Initialize Vue Router
app.use(router)

// Initialize PrimeVue with custom theme
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

// PrimeVue services
app.use(ToastService)
app.use(ConfirmationService)

// PrimeVue directives
app.directive('tooltip', Tooltip)
app.directive('ripple', Ripple)
app.directive('animateonscroll', AnimateOnScroll)

// Mount the app
app.mount('#app')
