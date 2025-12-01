<!--
  App.vue - Root Component

  This is teh main app container - handles page transitions and routing
  keeps things simple and just wraps the router view
  transitions make page changes look smoother
-->
<template>
  <!-- main app wrapper - takes full screen height -->
  <div id="app" class="min-h-screen">
    <!-- router-view renders current page component -->
    <router-view v-slot="{ Component, route }">
      <!-- transition wrapper for smooth page changes -->
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

// setup navigation guards when component mounts
onMounted(() => {
  // add loading indicator before navigation
  router.beforeEach((to, from, next) => {
    // show loading bar at top of page
    document.body.classList.add('page-loading')
    next()
  })

  // remove loading indicator after navigation
  router.afterEach(() => {
    // small delay to make transition smoother
    setTimeout(() => {
      document.body.classList.remove('page-loading')
    }, 100)
  })
})
</script>

<style>
/* loading bar at top of page - shows during navigation */
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

/* animated loading bar effect */
@keyframes loading-bar {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* fade transition for page changes - works for now */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* smooth scrolling - makes anchor links nicer */
html {
  scroll-behavior: smooth;
}

/* focus outline for accessibility - important for keyboard navigation */
:focus-visible {
  outline: 2px solid #6366f1;
  outline-offset: 2px;
}
</style>