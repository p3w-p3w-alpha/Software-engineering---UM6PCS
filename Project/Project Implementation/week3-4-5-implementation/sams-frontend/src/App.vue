<template>
  <div id="app" class="min-h-screen">
    <router-view v-slot="{ Component, route }">
      <Transition :name="route.meta.transition || 'fade'" mode="out-in">
        <component :is="Component" />
      </Transition>
    </router-view>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  // Add global navigation guards for smooth transitions
  router.beforeEach((to, from, next) => {
    // Add loading state if needed
    document.body.classList.add('page-loading')
    next()
  })

  router.afterEach(() => {
    // Remove loading state
    setTimeout(() => {
      document.body.classList.remove('page-loading')
    }, 100)
  })
})
</script>

<style>
/* Page loading indicator */
.page-loading::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #6366f1 0%, #8b5cf6 50%, #ec4899 100%);
  z-index: 9999;
  animation: loading-bar 1s linear infinite;
}

@keyframes loading-bar {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* Global transition styles */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Smooth scroll behavior */
html {
  scroll-behavior: smooth;
}

/* Focus styles for accessibility */
:focus-visible {
  outline: 2px solid #6366f1;
  outline-offset: 2px;
}
</style>