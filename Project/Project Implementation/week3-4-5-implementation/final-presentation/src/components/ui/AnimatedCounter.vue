<script setup>
import { ref, onMounted, watch } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  value: { type: [Number, String], required: true },
  duration: { type: Number, default: 2 },
  delay: { type: Number, default: 0 },
  suffix: { type: String, default: '' },
  prefix: { type: String, default: '' }
})

const displayValue = ref(0)
const container = ref(null)
const isVisible = ref(false)

const animateValue = () => {
  const numericValue = typeof props.value === 'string'
    ? parseFloat(props.value.replace(/[^0-9.]/g, '')) || 0
    : props.value

  gsap.fromTo(
    displayValue,
    { value: 0 },
    {
      value: numericValue,
      duration: props.duration,
      delay: props.delay,
      ease: 'power2.out',
      onUpdate: () => {
        displayValue.value = Math.round(displayValue.value)
      }
    }
  )

  // Scale animation
  gsap.fromTo(
    container.value,
    { scale: 0.5, opacity: 0 },
    {
      scale: 1,
      opacity: 1,
      duration: 0.6,
      delay: props.delay,
      ease: 'back.out(1.7)'
    }
  )
}

onMounted(() => {
  // Intersection observer for triggering animation when visible
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting && !isVisible.value) {
          isVisible.value = true
          animateValue()
        }
      })
    },
    { threshold: 0.5 }
  )

  if (container.value) {
    observer.observe(container.value)
  }
})

watch(() => props.value, () => {
  if (isVisible.value) {
    animateValue()
  }
})
</script>

<template>
  <div ref="container" class="counter-container">
    <span class="counter-value">
      {{ prefix }}{{ displayValue }}{{ suffix }}
    </span>
  </div>
</template>

<style scoped>
.counter-container {
  display: inline-block;
}

.counter-value {
  font-family: 'Clash Display', sans-serif;
  font-size: 4rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1;
}
</style>
