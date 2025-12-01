<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import gsap from 'gsap'

const cursor = ref(null)
const cursorDot = ref(null)
const cursorRing = ref(null)

let mouseX = 0
let mouseY = 0
let cursorX = 0
let cursorY = 0
let isHovering = ref(false)

const updateCursor = () => {
  // Smooth follow for outer ring
  cursorX += (mouseX - cursorX) * 0.1
  cursorY += (mouseY - cursorY) * 0.1

  if (cursorRing.value) {
    cursorRing.value.style.transform = `translate(${cursorX}px, ${cursorY}px)`
  }

  // Instant follow for dot
  if (cursorDot.value) {
    cursorDot.value.style.transform = `translate(${mouseX}px, ${mouseY}px)`
  }

  requestAnimationFrame(updateCursor)
}

const handleMouseMove = (e) => {
  mouseX = e.clientX
  mouseY = e.clientY
}

const handleMouseEnter = (e) => {
  const target = e.target.closest('button, a, [data-cursor-hover]')
  if (target) {
    isHovering.value = true
    gsap.to(cursorRing.value, {
      scale: 1.5,
      opacity: 0.5,
      duration: 0.3,
      ease: 'power2.out'
    })
    gsap.to(cursorDot.value, {
      scale: 0.5,
      duration: 0.3,
      ease: 'power2.out'
    })
  }
}

const handleMouseLeave = (e) => {
  const target = e.target.closest('button, a, [data-cursor-hover]')
  if (target) {
    isHovering.value = false
    gsap.to(cursorRing.value, {
      scale: 1,
      opacity: 1,
      duration: 0.3,
      ease: 'power2.out'
    })
    gsap.to(cursorDot.value, {
      scale: 1,
      duration: 0.3,
      ease: 'power2.out'
    })
  }
}

const handleMouseDown = () => {
  gsap.to(cursorRing.value, {
    scale: 0.8,
    duration: 0.2,
    ease: 'power2.out'
  })
}

const handleMouseUp = () => {
  gsap.to(cursorRing.value, {
    scale: isHovering.value ? 1.5 : 1,
    duration: 0.3,
    ease: 'elastic.out(1, 0.5)'
  })
}

onMounted(() => {
  requestAnimationFrame(updateCursor)

  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseover', handleMouseEnter)
  document.addEventListener('mouseout', handleMouseLeave)
  document.addEventListener('mousedown', handleMouseDown)
  document.addEventListener('mouseup', handleMouseUp)

  // Hide default cursor
  document.body.style.cursor = 'none'
})

onUnmounted(() => {
  document.removeEventListener('mousemove', handleMouseMove)
  document.removeEventListener('mouseover', handleMouseEnter)
  document.removeEventListener('mouseout', handleMouseLeave)
  document.removeEventListener('mousedown', handleMouseDown)
  document.removeEventListener('mouseup', handleMouseUp)
  document.body.style.cursor = 'auto'
})
</script>

<template>
  <div ref="cursor" class="custom-cursor">
    <div ref="cursorDot" class="cursor-dot"></div>
    <div ref="cursorRing" class="cursor-ring"></div>
  </div>
</template>

<style scoped>
.custom-cursor {
  pointer-events: none;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
  mix-blend-mode: difference;
}

.cursor-dot {
  position: fixed;
  top: -4px;
  left: -4px;
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
  pointer-events: none;
  will-change: transform;
}

.cursor-ring {
  position: fixed;
  top: -20px;
  left: -20px;
  width: 40px;
  height: 40px;
  border: 2px solid white;
  border-radius: 50%;
  pointer-events: none;
  will-change: transform;
  opacity: 0.8;
}

@media (max-width: 768px) {
  .custom-cursor {
    display: none;
  }
}
</style>
