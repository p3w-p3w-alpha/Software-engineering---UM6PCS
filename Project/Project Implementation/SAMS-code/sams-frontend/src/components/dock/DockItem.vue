<!--
  DockItem - individual item in the macOS-style dock navigation
  handles hover animations, active states, and notification badges
  the GSAP animations were wierd to setup but look smooth now
-->

<template>
  <div
    class="dock-item-wrapper"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
  >
    <router-link
      v-if="item.path"
      :to="item.path"
      class="dock-item"
      :class="{
        'active': isActive,
        'hovered': isHovered
      }"
    >
      <!-- Icon -->
      <component
        :is="item.icon"
        class="dock-icon"
        :class="{ 'icon-active': isActive }"
      />

      <!-- Notification Badge -->
      <span
        v-if="item.badge && item.badge > 0"
        class="dock-badge"
      >
        {{ item.badge > 99 ? '99+' : item.badge }}
      </span>

      <!-- Active Indicator Dot -->
      <span v-if="isActive" class="dock-active-dot"></span>

      <!-- Tooltip -->
      <span class="dock-tooltip">{{ item.label }}</span>

      <!-- Glow Effect -->
      <span class="dock-glow"></span>
    </router-link>

    <button
      v-else
      @click="handleClick"
      class="dock-item"
      :class="{
        'active': isActive,
        'hovered': isHovered
      }"
    >
      <!-- Icon -->
      <component
        :is="item.icon"
        class="dock-icon"
        :class="{ 'icon-active': isActive }"
      />

      <!-- Notification Badge -->
      <span
        v-if="item.badge && item.badge > 0"
        class="dock-badge"
      >
        {{ item.badge > 99 ? '99+' : item.badge }}
      </span>

      <!-- Tooltip -->
      <span class="dock-tooltip">{{ item.label }}</span>

      <!-- Glow Effect -->
      <span class="dock-glow"></span>
    </button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { gsap } from 'gsap'

// dock item props - recieve item config and index
const props = defineProps({
  item: {
    type: Object,
    required: true  // contains path, label, icon, badge
  },
  index: {
    type: Number,
    default: 0  // position in dock
  }
})

const emit = defineEmits(['click', 'hover', 'leave'])

const route = useRoute()
const isHovered = ref(false)
const itemRef = ref(null)

// check if this dock item is currently active
const isActive = computed(() => {
  if (!props.item.path) return false
  // Check for exact match or if current route starts with item path
  return route.path === props.item.path ||
         (props.item.path !== '/' && route.path.startsWith(props.item.path))
})

const handleMouseEnter = (event) => {
  isHovered.value = true
  emit('hover', props.index)

  // GSAP animation for hover
  const target = event.currentTarget.querySelector('.dock-item')
  gsap.to(target, {
    y: -12,
    scale: 1.2,
    duration: 0.3,
    ease: 'back.out(1.7)'
  })

  // Glow animation
  const glow = event.currentTarget.querySelector('.dock-glow')
  gsap.to(glow, {
    opacity: 1,
    scale: 1.5,
    duration: 0.3
  })
}

const handleMouseLeave = (event) => {
  isHovered.value = false
  emit('leave', props.index)

  // Reset animation
  const target = event.currentTarget.querySelector('.dock-item')
  gsap.to(target, {
    y: 0,
    scale: 1,
    duration: 0.3,
    ease: 'power2.out'
  })

  // Reset glow
  const glow = event.currentTarget.querySelector('.dock-glow')
  gsap.to(glow, {
    opacity: 0,
    scale: 1,
    duration: 0.3
  })
}

const handleClick = () => {
  if (props.item.action) {
    props.item.action()
  }
  emit('click', props.item)
}
</script>

<style scoped>
.dock-item-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dock-item {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: background 0.3s ease;
  will-change: transform;
}

.dock-item:hover {
  background: rgba(0, 212, 255, 0.1);
}

.dock-item.active {
  background: rgba(0, 212, 255, 0.15);
}

.dock-icon {
  width: 26px;
  height: 26px;
  color: rgba(248, 250, 252, 0.7);
  transition: color 0.3s ease;
}

.dock-item:hover .dock-icon {
  color: #00d4ff;
}

.dock-icon.icon-active {
  color: #00d4ff;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.6));
}

.dock-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  padding: 0 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: 700;
  color: white;
  background: linear-gradient(135deg, #ec4899, #ef4444);
  border-radius: 9px;
  box-shadow: 0 0 10px rgba(236, 72, 153, 0.5);
  animation: pulse-slow 2s infinite;
}

.dock-active-dot {
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #00d4ff;
  box-shadow: 0 0 10px #00d4ff, 0 0 20px rgba(0, 212, 255, 0.5);
}

.dock-tooltip {
  position: absolute;
  bottom: calc(100% + 16px);
  left: 50%;
  transform: translateX(-50%) translateY(10px);
  padding: 8px 14px;
  font-size: 12px;
  font-weight: 600;
  color: #f8fafc;
  background: rgba(18, 18, 26, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.4);
  z-index: 100;
}

.dock-tooltip::after {
  content: '';
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  border: 6px solid transparent;
  border-top-color: rgba(18, 18, 26, 0.95);
}

.dock-item-wrapper:hover .dock-tooltip {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
}

.dock-glow {
  position: absolute;
  inset: 0;
  border-radius: 14px;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.3) 0%, transparent 70%);
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.3s ease;
}

@keyframes pulse-slow {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.05); }
}
</style>
