<script setup>
import { ref, onMounted, watch } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  text: { type: String, required: true },
  tag: { type: String, default: 'h1' },
  variant: {
    type: String,
    default: 'gradient',
    validator: (v) => ['gradient', 'glow', 'typewriter', 'split', 'wave'].includes(v)
  },
  delay: { type: Number, default: 0 },
  duration: { type: Number, default: 0.8 },
  stagger: { type: Number, default: 0.03 }
})

const container = ref(null)
const chars = ref([])

const splitText = (text) => {
  return text.split('').map((char, i) => ({
    char: char === ' ' ? '\u00A0' : char,
    index: i
  }))
}

const animateChars = () => {
  if (!chars.value.length) return

  gsap.killTweensOf(chars.value)

  if (props.variant === 'split' || props.variant === 'wave') {
    gsap.fromTo(
      chars.value,
      {
        opacity: 0,
        y: props.variant === 'wave' ? 0 : 50,
        rotateX: props.variant === 'wave' ? 0 : -90,
        scale: props.variant === 'wave' ? 0.5 : 1
      },
      {
        opacity: 1,
        y: 0,
        rotateX: 0,
        scale: 1,
        duration: props.duration,
        stagger: props.stagger,
        delay: props.delay,
        ease: 'back.out(1.7)'
      }
    )
  }

  if (props.variant === 'wave') {
    // Continuous wave animation
    chars.value.forEach((char, i) => {
      gsap.to(char, {
        y: -5,
        duration: 0.5,
        repeat: -1,
        yoyo: true,
        ease: 'sine.inOut',
        delay: i * 0.05
      })
    })
  }
}

onMounted(() => {
  if (props.variant === 'split' || props.variant === 'wave') {
    // Wait for DOM to render chars
    setTimeout(() => {
      chars.value = container.value?.querySelectorAll('.char') || []
      animateChars()
    }, 50)
  }

  if (props.variant === 'typewriter') {
    gsap.fromTo(
      container.value,
      { width: 0 },
      {
        width: '100%',
        duration: props.text.length * 0.05,
        delay: props.delay,
        ease: 'steps(' + props.text.length + ')'
      }
    )
  }
})

watch(() => props.text, () => {
  if (props.variant === 'split' || props.variant === 'wave') {
    setTimeout(() => {
      chars.value = container.value?.querySelectorAll('.char') || []
      animateChars()
    }, 50)
  }
})
</script>

<template>
  <component
    :is="tag"
    ref="container"
    :class="[
      'glowing-text',
      `variant-${variant}`
    ]"
  >
    <template v-if="variant === 'split' || variant === 'wave'">
      <span
        v-for="item in splitText(text)"
        :key="item.index"
        class="char"
        :style="{ display: 'inline-block' }"
      >{{ item.char }}</span>
    </template>
    <template v-else>
      {{ text }}
    </template>
  </component>
</template>

<style scoped>
.glowing-text {
  position: relative;
}

.variant-gradient {
  background: linear-gradient(
    135deg,
    #667eea 0%,
    #764ba2 25%,
    #f093fb 50%,
    #667eea 75%,
    #764ba2 100%
  );
  background-size: 200% auto;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: gradient-flow 4s linear infinite;
}

@keyframes gradient-flow {
  0% { background-position: 0% center; }
  100% { background-position: 200% center; }
}

.variant-glow {
  color: white;
  text-shadow:
    0 0 10px rgba(99, 102, 241, 0.8),
    0 0 20px rgba(99, 102, 241, 0.6),
    0 0 30px rgba(99, 102, 241, 0.4),
    0 0 40px rgba(99, 102, 241, 0.2);
  animation: glow-pulse 2s ease-in-out infinite;
}

@keyframes glow-pulse {
  0%, 100% {
    text-shadow:
      0 0 10px rgba(99, 102, 241, 0.8),
      0 0 20px rgba(99, 102, 241, 0.6),
      0 0 30px rgba(99, 102, 241, 0.4),
      0 0 40px rgba(99, 102, 241, 0.2);
  }
  50% {
    text-shadow:
      0 0 20px rgba(99, 102, 241, 1),
      0 0 40px rgba(99, 102, 241, 0.8),
      0 0 60px rgba(99, 102, 241, 0.6),
      0 0 80px rgba(99, 102, 241, 0.4);
  }
}

.variant-typewriter {
  overflow: hidden;
  white-space: nowrap;
  border-right: 3px solid rgba(99, 102, 241, 0.8);
  animation: blink-caret 0.75s step-end infinite;
}

@keyframes blink-caret {
  from, to { border-color: transparent; }
  50% { border-color: rgba(99, 102, 241, 0.8); }
}

.variant-split .char,
.variant-wave .char {
  display: inline-block;
  will-change: transform, opacity;
}

.variant-split {
  perspective: 1000px;
}

.variant-wave {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>
