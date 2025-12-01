<!--
  AuroraBackground - animated gradient background effect
  creates beautiful flowing aurora-like animations with blobs and mesh gradients
  the animation math was wierd but looks amazing now
-->

<template>
  <div class="aurora-background" :class="{ 'aurora-interactive': interactive }">
    <!-- Base Gradient layer -->
    <div class="aurora-base"></div>

    <!-- Animated Blobs - main visual effect -->
    <div class="aurora-blobs">
      <div
        v-for="(blob, index) in blobs"
        :key="index"
        class="aurora-blob"
        :style="blob.style"
      ></div>
    </div>

    <!-- Mesh Gradient Overlay -->
    <div class="aurora-mesh"></div>

    <!-- Grid Pattern (optional) -->
    <div v-if="showGrid" class="aurora-grid"></div>

    <!-- Noise Texture -->
    <div class="aurora-noise"></div>

    <!-- Particles (optional) -->
    <div v-if="showParticles" class="aurora-particles">
      <div
        v-for="(particle, index) in particles"
        :key="`particle-${index}`"
        class="aurora-particle"
        :style="particle.style"
      ></div>
    </div>

    <!-- Vignette Effect -->
    <div class="aurora-vignette"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// aurora background props for customization
const props = defineProps({
  variant: {
    type: String,
    default: 'default', // 'default', 'login', 'minimal', 'vibrant'
    validator: (v) => ['default', 'login', 'minimal', 'vibrant'].includes(v)
  },
  interactive: {
    type: Boolean,
    default: false  // responds to mouse movement
  },
  showGrid: {
    type: Boolean,
    default: false  // optional grid overlay
  },
  showParticles: {
    type: Boolean,
    default: false  // floating particles effect
  },
  intensity: {
    type: Number,
    default: 1,  // 0-2 range for effect strength
    validator: (v) => v >= 0 && v <= 2
  }
})

// Generate blob configurations based on variant - this was tricky
const blobs = computed(() => {
  const baseBlobs = [
    {
      style: {
        '--blob-color': 'rgba(0, 212, 255, 0.4)',
        '--blob-size': '500px',
        '--blob-x': '-5%',
        '--blob-y': '-10%',
        '--animation-duration': '20s',
        '--animation-delay': '0s'
      }
    },
    {
      style: {
        '--blob-color': 'rgba(168, 85, 247, 0.35)',
        '--blob-size': '450px',
        '--blob-x': '75%',
        '--blob-y': '15%',
        '--animation-duration': '25s',
        '--animation-delay': '-5s'
      }
    },
    {
      style: {
        '--blob-color': 'rgba(34, 197, 94, 0.3)',
        '--blob-size': '400px',
        '--blob-x': '30%',
        '--blob-y': '70%',
        '--animation-duration': '22s',
        '--animation-delay': '-10s'
      }
    }
  ]

  if (props.variant === 'login') {
    return [
      ...baseBlobs,
      {
        style: {
          '--blob-color': 'rgba(236, 72, 153, 0.25)',
          '--blob-size': '350px',
          '--blob-x': '60%',
          '--blob-y': '50%',
          '--animation-duration': '18s',
          '--animation-delay': '-3s'
        }
      }
    ]
  }

  if (props.variant === 'vibrant') {
    return baseBlobs.map(blob => ({
      style: {
        ...blob.style,
        '--blob-color': blob.style['--blob-color'].replace(/[\d.]+\)$/, match =>
          `${parseFloat(match) * 1.5})`
        )
      }
    }))
  }

  return baseBlobs
})

// Generate particle configurations
const particles = computed(() => {
  if (!props.showParticles) return []

  return Array.from({ length: 30 }, (_, i) => ({
    style: {
      '--particle-x': `${Math.random() * 100}%`,
      '--particle-y': `${Math.random() * 100}%`,
      '--particle-size': `${2 + Math.random() * 4}px`,
      '--particle-opacity': `${0.3 + Math.random() * 0.5}`,
      '--particle-duration': `${3 + Math.random() * 4}s`,
      '--particle-delay': `${Math.random() * 5}s`
    }
  }))
})

// Interactive mouse tracking
const mousePosition = ref({ x: 0, y: 0 })

const handleMouseMove = (event) => {
  if (!props.interactive) return

  mousePosition.value = {
    x: (event.clientX / window.innerWidth) * 100,
    y: (event.clientY / window.innerHeight) * 100
  }
}

onMounted(() => {
  if (props.interactive) {
    window.addEventListener('mousemove', handleMouseMove)
  }
})

onUnmounted(() => {
  if (props.interactive) {
    window.removeEventListener('mousemove', handleMouseMove)
  }
})
</script>

<style scoped>
.aurora-background {
  position: fixed;
  inset: 0;
  z-index: -1;
  overflow: hidden;
  background: #0a0a0f;
}

/* Base gradient layer */
.aurora-base {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    180deg,
    #0a0a0f 0%,
    #0f0f1a 30%,
    #12121f 60%,
    #0a0a0f 100%
  );
}

/* Animated blobs container */
.aurora-blobs {
  position: absolute;
  inset: 0;
  filter: blur(80px);
}

.aurora-blob {
  position: absolute;
  width: var(--blob-size, 400px);
  height: var(--blob-size, 400px);
  left: var(--blob-x, 0);
  top: var(--blob-y, 0);
  background: var(--blob-color, rgba(0, 212, 255, 0.3));
  border-radius: 50%;
  animation: blob-float var(--animation-duration, 20s) ease-in-out infinite;
  animation-delay: var(--animation-delay, 0s);
  will-change: transform;
}

@keyframes blob-float {
  0%, 100% {
    transform: translate(0, 0) scale(1) rotate(0deg);
    border-radius: 40% 60% 70% 30% / 40% 50% 60% 50%;
  }
  25% {
    transform: translate(30px, -50px) scale(1.05) rotate(90deg);
    border-radius: 70% 30% 50% 50% / 30% 30% 70% 70%;
  }
  50% {
    transform: translate(-20px, 20px) scale(0.95) rotate(180deg);
    border-radius: 30% 60% 70% 40% / 50% 60% 30% 60%;
  }
  75% {
    transform: translate(50px, 30px) scale(1.1) rotate(270deg);
    border-radius: 50% 50% 30% 70% / 70% 40% 60% 40%;
  }
}

/* Mesh gradient overlay */
.aurora-mesh {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(at 40% 20%, rgba(0, 212, 255, 0.08) 0px, transparent 50%),
    radial-gradient(at 80% 0%, rgba(168, 85, 247, 0.06) 0px, transparent 50%),
    radial-gradient(at 0% 50%, rgba(34, 197, 94, 0.05) 0px, transparent 50%),
    radial-gradient(at 80% 50%, rgba(236, 72, 153, 0.04) 0px, transparent 50%),
    radial-gradient(at 0% 100%, rgba(0, 212, 255, 0.06) 0px, transparent 50%),
    radial-gradient(at 80% 100%, rgba(168, 85, 247, 0.05) 0px, transparent 50%);
  opacity: 0.8;
}

/* Grid pattern */
.aurora-grid {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
  background-size: 50px 50px;
  mask-image: radial-gradient(ellipse at center, black 30%, transparent 70%);
  -webkit-mask-image: radial-gradient(ellipse at center, black 30%, transparent 70%);
}

/* Noise texture */
.aurora-noise {
  position: absolute;
  inset: 0;
  opacity: 0.03;
  background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E");
  background-repeat: repeat;
  pointer-events: none;
}

/* Particles */
.aurora-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.aurora-particle {
  position: absolute;
  left: var(--particle-x, 50%);
  top: var(--particle-y, 50%);
  width: var(--particle-size, 3px);
  height: var(--particle-size, 3px);
  background: rgba(255, 255, 255, var(--particle-opacity, 0.5));
  border-radius: 50%;
  animation: particle-float var(--particle-duration, 4s) ease-in-out infinite;
  animation-delay: var(--particle-delay, 0s);
}

@keyframes particle-float {
  0%, 100% {
    transform: translateY(0) translateX(0);
    opacity: var(--particle-opacity, 0.5);
  }
  25% {
    transform: translateY(-20px) translateX(10px);
    opacity: calc(var(--particle-opacity, 0.5) * 1.5);
  }
  50% {
    transform: translateY(-10px) translateX(-5px);
    opacity: var(--particle-opacity, 0.5);
  }
  75% {
    transform: translateY(-30px) translateX(15px);
    opacity: calc(var(--particle-opacity, 0.5) * 0.7);
  }
}

/* Vignette effect */
.aurora-vignette {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    ellipse at center,
    transparent 0%,
    transparent 50%,
    rgba(0, 0, 0, 0.4) 100%
  );
  pointer-events: none;
}

/* Interactive state - shifts based on mouse position */
.aurora-interactive .aurora-blobs {
  transition: transform 0.3s ease-out;
}
</style>
