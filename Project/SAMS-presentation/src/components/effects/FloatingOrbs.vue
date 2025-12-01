<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const orbs = ref([])
const orbCount = 6

const orbColors = [
  'from-indigo-500/30 to-purple-600/20',
  'from-pink-500/25 to-rose-600/15',
  'from-cyan-500/25 to-blue-600/15',
  'from-violet-500/30 to-fuchsia-600/20',
  'from-emerald-500/20 to-teal-600/10',
  'from-amber-500/20 to-orange-600/10'
]

onMounted(() => {
  orbs.value.forEach((orb, index) => {
    if (!orb) return

    // Random starting position
    gsap.set(orb, {
      x: Math.random() * window.innerWidth * 0.8 - window.innerWidth * 0.4,
      y: Math.random() * window.innerHeight * 0.8 - window.innerHeight * 0.4,
      scale: 0.5 + Math.random() * 0.5
    })

    // Floating animation
    gsap.to(orb, {
      x: `+=${(Math.random() - 0.5) * 200}`,
      y: `+=${(Math.random() - 0.5) * 200}`,
      duration: 15 + Math.random() * 10,
      repeat: -1,
      yoyo: true,
      ease: 'sine.inOut',
      delay: index * 0.5
    })

    // Scale pulse
    gsap.to(orb, {
      scale: 0.8 + Math.random() * 0.4,
      duration: 8 + Math.random() * 4,
      repeat: -1,
      yoyo: true,
      ease: 'sine.inOut',
      delay: index * 0.3
    })
  })
})
</script>

<template>
  <div class="floating-orbs-container">
    <div
      v-for="(color, index) in orbColors"
      :key="index"
      :ref="el => orbs[index] = el"
      class="floating-orb"
      :class="`bg-gradient-radial ${color}`"
      :style="{
        width: `${300 + index * 100}px`,
        height: `${300 + index * 100}px`,
      }"
    />
  </div>
</template>

<style scoped>
.floating-orbs-container {
  position: fixed;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.floating-orb {
  position: absolute;
  left: 50%;
  top: 50%;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.6;
  background: radial-gradient(circle at center, var(--tw-gradient-from), var(--tw-gradient-to));
}

.bg-gradient-radial {
  background: radial-gradient(circle at center, var(--tw-gradient-stops));
}
</style>
