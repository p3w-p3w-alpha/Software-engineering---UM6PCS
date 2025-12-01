<!--
  LoadingSpinner - animated loading indicator component
  supports multiple types: ring, dots, pulse, gradient
  fullScreen mode covers entire viewport - this was tricky
-->

<template>
  <div class="loading-spinner-wrapper" :class="{ 'full-screen': fullScreen }" role="status" aria-live="polite">
    <div class="loading-spinner" :class="sizeClass" aria-hidden="true">
      <!-- Spinner Type: Ring - classic circular spinner -->
      <div v-if="type === 'ring'" class="spinner-ring" :style="{ borderTopColor: color }">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>

      <!-- Spinner Type: Dots -->
      <div v-else-if="type === 'dots'" class="spinner-dots">
        <div v-for="i in 3" :key="i" :style="{ backgroundColor: color }"></div>
      </div>

      <!-- Spinner Type: Pulse -->
      <div v-else-if="type === 'pulse'" class="spinner-pulse">
        <div :style="{ backgroundColor: color }"></div>
        <div :style="{ backgroundColor: color }"></div>
      </div>

      <!-- Spinner Type: Gradient -->
      <div v-else class="spinner-gradient"></div>
    </div>

    <!-- Screen reader only text when no message provided -->
    <span v-if="!message" class="sr-only">Loading...</span>
    <p v-if="message" class="loading-message" :class="messageClass">{{ message }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// props for customizing spinner appearance and behavior
const props = defineProps({
  type: {
    type: String,
    default: 'gradient', // ring, dots, pulse, gradient
    validator: (value) => ['ring', 'dots', 'pulse', 'gradient'].includes(value)
  },
  size: {
    type: String,
    default: 'md', // sm, md, lg, xl - definately enough sizes
    validator: (value) => ['sm', 'md', 'lg', 'xl'].includes(value)
  },
  color: {
    type: String,
    default: '#3B82F6'  // nice blue by default
  },
  message: {
    type: String,
    default: ''  // optional loading message
  },
  fullScreen: {
    type: Boolean,
    default: false  // covers entire screen with backdrop
  }
})

// compute classes for dynamic sizing
const sizeClass = computed(() => `spinner-${props.size}`)
const messageClass = computed(() => `message-${props.size}`)
</script>

<style scoped>
.loading-spinner-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 24px;
}

.loading-spinner-wrapper.full-screen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  z-index: 9999;
}

.loading-spinner {
  position: relative;
}

/* Size Classes */
.spinner-sm { width: 24px; height: 24px; }
.spinner-md { width: 40px; height: 40px; }
.spinner-lg { width: 60px; height: 60px; }
.spinner-xl { width: 80px; height: 80px; }

/* Ring Spinner */
.spinner-ring {
  display: inline-block;
  width: 100%;
  height: 100%;
  border: 4px solid rgba(59, 130, 246, 0.1);
  border-radius: 50%;
  border-top-color: #3B82F6;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Dots Spinner */
.spinner-dots {
  display: flex;
  gap: 8px;
  align-items: center;
}

.spinner-dots div {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  animation: bounce-dots 1.4s infinite ease-in-out both;
}

.spinner-dots div:nth-child(1) { animation-delay: -0.32s; }
.spinner-dots div:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce-dots {
  0%, 80%, 100% { transform: scale(0); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* Pulse Spinner */
.spinner-pulse {
  position: relative;
  width: 100%;
  height: 100%;
}

.spinner-pulse div {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  opacity: 0.6;
  animation: pulse-grow 2s infinite ease-in-out;
}

.spinner-pulse div:nth-child(2) {
  animation-delay: -1s;
}

@keyframes pulse-grow {
  0%, 100% {
    transform: scale(0);
    opacity: 1;
  }
  50% {
    transform: scale(1);
    opacity: 0;
  }
}

/* Gradient Spinner */
.spinner-gradient {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(
    from 0deg,
    #3B82F6,
    #8B5CF6,
    #EC4899,
    #3B82F6
  );
  animation: spin 1.5s linear infinite;
  mask: radial-gradient(farthest-side, transparent calc(100% - 4px), #000 0);
  -webkit-mask: radial-gradient(farthest-side, transparent calc(100% - 4px), #000 0);
}

/* Loading Message */
.loading-message {
  color: #64748b;
  font-weight: 500;
  text-align: center;
  animation: fadeIn 0.3s ease-in;
}

.message-sm { font-size: 12px; }
.message-md { font-size: 14px; }
.message-lg { font-size: 16px; }
.message-xl { font-size: 18px; }

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Screen reader only */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}
</style>
