<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  color: {
    type: String,
    default: 'primary'
  },
  size: {
    type: String,
    default: 'md',
    validator: (v) => ['sm', 'md', 'lg', 'xl'].includes(v)
  },
  animate: {
    type: Boolean,
    default: true
  },
  pulse: {
    type: Boolean,
    default: false
  }
})

const badge = ref(null)
const ring = ref(null)

const colorMap = {
  primary: { bg: 'rgba(99, 102, 241, 0.2)', ring: 'rgba(99, 102, 241, 0.3)' },
  green: { bg: 'rgba(34, 197, 94, 0.2)', ring: 'rgba(34, 197, 94, 0.3)' },
  blue: { bg: 'rgba(59, 130, 246, 0.2)', ring: 'rgba(59, 130, 246, 0.3)' },
  yellow: { bg: 'rgba(234, 179, 8, 0.2)', ring: 'rgba(234, 179, 8, 0.3)' },
  red: { bg: 'rgba(239, 68, 68, 0.2)', ring: 'rgba(239, 68, 68, 0.3)' },
  purple: { bg: 'rgba(168, 85, 247, 0.2)', ring: 'rgba(168, 85, 247, 0.3)' },
  pink: { bg: 'rgba(236, 72, 153, 0.2)', ring: 'rgba(236, 72, 153, 0.3)' },
  cyan: { bg: 'rgba(6, 182, 212, 0.2)', ring: 'rgba(6, 182, 212, 0.3)' },
  orange: { bg: 'rgba(249, 115, 22, 0.2)', ring: 'rgba(249, 115, 22, 0.3)' }
}

onMounted(() => {
  if (props.animate && badge.value) {
    gsap.from(badge.value, {
      scale: 0,
      opacity: 0,
      duration: 0.6,
      ease: 'back.out(1.7)'
    })
  }

  if (props.pulse && ring.value) {
    gsap.to(ring.value, {
      scale: 1.3,
      opacity: 0,
      duration: 1.5,
      repeat: -1,
      ease: 'power1.out'
    })
  }
})

const getBgColor = () => colorMap[props.color]?.bg || colorMap.primary.bg
const getRingColor = () => colorMap[props.color]?.ring || colorMap.primary.ring
</script>

<template>
  <div
    ref="badge"
    class="icon-badge"
    :class="[`size-${size}`]"
    :style="{ backgroundColor: getBgColor() }"
  >
    <div
      v-if="pulse"
      ref="ring"
      class="pulse-ring"
      :style="{ backgroundColor: getRingColor() }"
    />
    <slot />
  </div>
</template>

<style scoped>
.icon-badge {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.icon-badge:hover {
  transform: scale(1.05);
}

.size-sm {
  width: 40px;
  height: 40px;
  border-radius: 12px;
}

.size-md {
  width: 56px;
  height: 56px;
}

.size-lg {
  width: 72px;
  height: 72px;
  border-radius: 20px;
}

.size-xl {
  width: 88px;
  height: 88px;
  border-radius: 24px;
}

.pulse-ring {
  position: absolute;
  inset: 0;
  border-radius: inherit;
  pointer-events: none;
}
</style>
