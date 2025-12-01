<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  variant: {
    type: String,
    default: 'primary',
    validator: (v) => ['primary', 'secondary', 'ghost', 'outline'].includes(v)
  },
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md', 'lg'].includes(v)
  },
  magnetic: {
    type: Boolean,
    default: true
  },
  strength: {
    type: Number,
    default: 0.3
  }
})

const button = ref(null)
const buttonContent = ref(null)

const handleMouseMove = (e) => {
  if (!props.magnetic || !button.value) return

  const rect = button.value.getBoundingClientRect()
  const x = e.clientX - rect.left - rect.width / 2
  const y = e.clientY - rect.top - rect.height / 2

  gsap.to(button.value, {
    x: x * props.strength,
    y: y * props.strength,
    duration: 0.3,
    ease: 'power2.out'
  })

  gsap.to(buttonContent.value, {
    x: x * props.strength * 0.5,
    y: y * props.strength * 0.5,
    duration: 0.3,
    ease: 'power2.out'
  })
}

const handleMouseLeave = () => {
  gsap.to(button.value, {
    x: 0,
    y: 0,
    duration: 0.5,
    ease: 'elastic.out(1, 0.5)'
  })

  gsap.to(buttonContent.value, {
    x: 0,
    y: 0,
    duration: 0.5,
    ease: 'elastic.out(1, 0.5)'
  })
}
</script>

<template>
  <button
    ref="button"
    class="magnetic-button"
    :class="[`btn-${variant}`, `btn-${size}`]"
    @mousemove="handleMouseMove"
    @mouseleave="handleMouseLeave"
    data-cursor-hover
  >
    <span ref="buttonContent" class="btn-content">
      <slot />
    </span>
    <span class="btn-bg"></span>
    <span class="btn-glow"></span>
  </button>
</template>

<style scoped>
.magnetic-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  border-radius: 50px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
  will-change: transform;
}

.btn-content {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 8px;
  will-change: transform;
}

.btn-bg {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  transition: transform 0.3s ease;
  z-index: 1;
}

.btn-glow {
  position: absolute;
  inset: -2px;
  border-radius: inherit;
  opacity: 0;
  transition: opacity 0.3s ease;
  filter: blur(10px);
  z-index: 0;
}

/* Sizes */
.btn-sm {
  padding: 10px 20px;
  font-size: 14px;
}

.btn-md {
  padding: 14px 28px;
  font-size: 16px;
}

.btn-lg {
  padding: 18px 36px;
  font-size: 18px;
}

/* Variants */
.btn-primary {
  color: white;
}

.btn-primary .btn-bg {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.btn-primary .btn-glow {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.btn-primary:hover {
  box-shadow: 0 10px 40px rgba(99, 102, 241, 0.4);
}

.btn-primary:hover .btn-glow {
  opacity: 0.6;
}

.btn-primary:hover .btn-bg {
  transform: scale(1.05);
}

.btn-secondary {
  color: white;
}

.btn-secondary .btn-bg {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-secondary:hover .btn-bg {
  background: rgba(255, 255, 255, 0.15);
}

.btn-ghost {
  color: white;
}

.btn-ghost .btn-bg {
  background: transparent;
}

.btn-ghost:hover .btn-bg {
  background: rgba(255, 255, 255, 0.1);
}

.btn-outline {
  color: white;
}

.btn-outline .btn-bg {
  background: transparent;
  border: 2px solid rgba(99, 102, 241, 0.5);
}

.btn-outline:hover .btn-bg {
  border-color: rgba(99, 102, 241, 1);
  background: rgba(99, 102, 241, 0.1);
}

/* Active state */
.magnetic-button:active .btn-bg {
  transform: scale(0.98);
}
</style>
