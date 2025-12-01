<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  variant: {
    type: String,
    default: 'glass',
    validator: (v) => ['glass', 'solid', 'gradient', 'outline'].includes(v)
  },
  glowColor: {
    type: String,
    default: 'rgba(99, 102, 241, 0.4)'
  },
  intensity: {
    type: Number,
    default: 15
  },
  perspective: {
    type: Number,
    default: 1000
  }
})

const card = ref(null)
const cardInner = ref(null)
const shine = ref(null)

let bounds = null

const handleMouseMove = (e) => {
  if (!bounds) return

  const mouseX = e.clientX
  const mouseY = e.clientY
  const leftX = mouseX - bounds.x
  const topY = mouseY - bounds.y
  const center = {
    x: leftX - bounds.width / 2,
    y: topY - bounds.height / 2
  }
  const distance = Math.sqrt(center.x ** 2 + center.y ** 2)

  gsap.to(cardInner.value, {
    rotateX: -(center.y / props.intensity),
    rotateY: center.x / props.intensity,
    duration: 0.3,
    ease: 'power2.out'
  })

  // Shine effect follows mouse
  if (shine.value) {
    gsap.to(shine.value, {
      x: leftX - bounds.width / 2,
      y: topY - bounds.height / 2,
      duration: 0.3,
      ease: 'power2.out'
    })
  }
}

const handleMouseEnter = () => {
  bounds = card.value.getBoundingClientRect()

  gsap.to(cardInner.value, {
    scale: 1.02,
    duration: 0.3,
    ease: 'power2.out'
  })

  if (shine.value) {
    gsap.to(shine.value, {
      opacity: 1,
      duration: 0.3
    })
  }
}

const handleMouseLeave = () => {
  gsap.to(cardInner.value, {
    rotateX: 0,
    rotateY: 0,
    scale: 1,
    duration: 0.5,
    ease: 'power2.out'
  })

  if (shine.value) {
    gsap.to(shine.value, {
      opacity: 0,
      duration: 0.5
    })
  }
}

onMounted(() => {
  if (cardInner.value) {
    gsap.set(cardInner.value, {
      transformStyle: 'preserve-3d',
      transformPerspective: props.perspective
    })
  }
})
</script>

<template>
  <div
    ref="card"
    class="card-3d"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
    @mousemove="handleMouseMove"
  >
    <div ref="cardInner" class="card-inner" :class="[`card-${variant}`]">
      <div ref="shine" class="card-shine"></div>
      <div class="card-content">
        <slot />
      </div>
    </div>
  </div>
</template>

<style scoped>
.card-3d {
  perspective: v-bind('perspective + "px"');
}

.card-inner {
  position: relative;
  border-radius: 24px;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
  will-change: transform;
}

.card-glass {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow:
    0 8px 32px 0 rgba(31, 38, 135, 0.2),
    inset 0 0 0 1px rgba(255, 255, 255, 0.05);
}

.card-glass:hover {
  box-shadow:
    0 20px 60px 0 rgba(31, 38, 135, 0.35),
    inset 0 0 0 1px rgba(255, 255, 255, 0.15),
    0 0 40px v-bind('glowColor');
  border-color: rgba(255, 255, 255, 0.2);
}

.card-solid {
  background: linear-gradient(135deg, rgba(30, 27, 75, 0.9) 0%, rgba(49, 46, 129, 0.8) 100%);
  border: 1px solid rgba(99, 102, 241, 0.3);
}

.card-solid:hover {
  box-shadow: 0 20px 60px rgba(99, 102, 241, 0.3);
}

.card-gradient {
  background: linear-gradient(135deg,
    rgba(99, 102, 241, 0.2) 0%,
    rgba(139, 92, 246, 0.2) 50%,
    rgba(236, 72, 153, 0.2) 100%);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

.card-gradient:hover {
  box-shadow: 0 20px 60px rgba(139, 92, 246, 0.3);
}

.card-outline {
  background: transparent;
  border: 2px solid rgba(99, 102, 241, 0.3);
}

.card-outline:hover {
  border-color: rgba(99, 102, 241, 0.6);
  box-shadow: 0 0 30px rgba(99, 102, 241, 0.2);
}

.card-shine {
  position: absolute;
  top: 0;
  left: 0;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 60%);
  opacity: 0;
  pointer-events: none;
  transform: translate(-50%, -50%);
  filter: blur(40px);
}

.card-content {
  position: relative;
  z-index: 1;
}
</style>
