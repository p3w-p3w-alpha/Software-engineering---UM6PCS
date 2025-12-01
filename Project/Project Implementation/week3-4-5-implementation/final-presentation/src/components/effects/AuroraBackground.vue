<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const aurora1 = ref(null)
const aurora2 = ref(null)
const aurora3 = ref(null)

onMounted(() => {
  // Animate aurora layers
  gsap.to(aurora1.value, {
    x: 100,
    y: -50,
    duration: 20,
    repeat: -1,
    yoyo: true,
    ease: 'sine.inOut'
  })

  gsap.to(aurora2.value, {
    x: -80,
    y: 60,
    duration: 25,
    repeat: -1,
    yoyo: true,
    ease: 'sine.inOut'
  })

  gsap.to(aurora3.value, {
    x: 60,
    y: 40,
    duration: 30,
    repeat: -1,
    yoyo: true,
    ease: 'sine.inOut'
  })
})
</script>

<template>
  <div class="aurora-container">
    <div ref="aurora1" class="aurora aurora-1"></div>
    <div ref="aurora2" class="aurora aurora-2"></div>
    <div ref="aurora3" class="aurora aurora-3"></div>
    <div class="noise-overlay"></div>
    <div class="vignette"></div>
  </div>
</template>

<style scoped>
.aurora-container {
  position: fixed;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.aurora {
  position: absolute;
  width: 150%;
  height: 150%;
  filter: blur(100px);
  opacity: 0.5;
  mix-blend-mode: screen;
}

.aurora-1 {
  top: -30%;
  left: -25%;
  background: radial-gradient(ellipse at center,
    rgba(99, 102, 241, 0.4) 0%,
    rgba(139, 92, 246, 0.2) 30%,
    transparent 70%);
  animation: aurora-pulse-1 8s ease-in-out infinite;
}

.aurora-2 {
  bottom: -30%;
  right: -25%;
  background: radial-gradient(ellipse at center,
    rgba(236, 72, 153, 0.3) 0%,
    rgba(167, 139, 250, 0.15) 30%,
    transparent 70%);
  animation: aurora-pulse-2 10s ease-in-out infinite;
}

.aurora-3 {
  top: 20%;
  left: 30%;
  width: 80%;
  height: 80%;
  background: radial-gradient(ellipse at center,
    rgba(34, 211, 238, 0.2) 0%,
    rgba(59, 130, 246, 0.1) 30%,
    transparent 70%);
  animation: aurora-pulse-3 12s ease-in-out infinite;
}

@keyframes aurora-pulse-1 {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.1); }
}

@keyframes aurora-pulse-2 {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.15); }
}

@keyframes aurora-pulse-3 {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50% { opacity: 0.35; transform: scale(1.08); }
}

.noise-overlay {
  position: absolute;
  inset: 0;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
  opacity: 0.025;
  pointer-events: none;
}

.vignette {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at center, transparent 0%, rgba(10, 10, 18, 0.4) 100%);
  pointer-events: none;
}
</style>
