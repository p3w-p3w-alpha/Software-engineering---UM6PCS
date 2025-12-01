<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const canvas = ref(null)
let animationId = null
let particles = []
let mouseX = 0
let mouseY = 0

const props = defineProps({
  particleCount: { type: Number, default: 80 },
  speed: { type: Number, default: 0.3 },
  connectionDistance: { type: Number, default: 150 },
  particleColor: { type: String, default: 'rgba(99, 102, 241, 0.6)' },
  lineColor: { type: String, default: 'rgba(99, 102, 241, 0.15)' }
})

class Particle {
  constructor(canvasWidth, canvasHeight, speed) {
    this.x = Math.random() * canvasWidth
    this.y = Math.random() * canvasHeight
    this.vx = (Math.random() - 0.5) * speed
    this.vy = (Math.random() - 0.5) * speed
    this.radius = Math.random() * 2 + 1
    this.originalRadius = this.radius
    this.pulseSpeed = Math.random() * 0.02 + 0.01
    this.pulsePhase = Math.random() * Math.PI * 2
  }

  update(canvasWidth, canvasHeight, mouseX, mouseY) {
    this.x += this.vx
    this.y += this.vy

    // Pulse effect
    this.pulsePhase += this.pulseSpeed
    this.radius = this.originalRadius + Math.sin(this.pulsePhase) * 0.5

    // Mouse interaction
    const dx = mouseX - this.x
    const dy = mouseY - this.y
    const dist = Math.sqrt(dx * dx + dy * dy)
    if (dist < 150) {
      const force = (150 - dist) / 150
      this.vx -= (dx / dist) * force * 0.02
      this.vy -= (dy / dist) * force * 0.02
    }

    // Boundary check with smooth wrapping
    if (this.x < 0) this.x = canvasWidth
    if (this.x > canvasWidth) this.x = 0
    if (this.y < 0) this.y = canvasHeight
    if (this.y > canvasHeight) this.y = 0

    // Damping
    this.vx *= 0.999
    this.vy *= 0.999
  }

  draw(ctx, color) {
    ctx.beginPath()
    ctx.arc(this.x, this.y, this.radius, 0, Math.PI * 2)
    ctx.fillStyle = color
    ctx.fill()

    // Glow effect
    ctx.shadowBlur = 15
    ctx.shadowColor = color
    ctx.fill()
    ctx.shadowBlur = 0
  }
}

const initParticles = (width, height) => {
  particles = []
  for (let i = 0; i < props.particleCount; i++) {
    particles.push(new Particle(width, height, props.speed))
  }
}

const drawConnections = (ctx, connectionDistance, lineColor) => {
  for (let i = 0; i < particles.length; i++) {
    for (let j = i + 1; j < particles.length; j++) {
      const dx = particles[i].x - particles[j].x
      const dy = particles[i].y - particles[j].y
      const dist = Math.sqrt(dx * dx + dy * dy)

      if (dist < connectionDistance) {
        const opacity = 1 - dist / connectionDistance
        ctx.beginPath()
        ctx.moveTo(particles[i].x, particles[i].y)
        ctx.lineTo(particles[j].x, particles[j].y)
        ctx.strokeStyle = lineColor.replace('0.15', (opacity * 0.15).toFixed(2))
        ctx.lineWidth = opacity * 1.5
        ctx.stroke()
      }
    }
  }
}

const animate = () => {
  const ctx = canvas.value?.getContext('2d')
  if (!ctx) return

  const width = canvas.value.width
  const height = canvas.value.height

  ctx.clearRect(0, 0, width, height)

  // Draw connections
  drawConnections(ctx, props.connectionDistance, props.lineColor)

  // Update and draw particles
  particles.forEach(particle => {
    particle.update(width, height, mouseX, mouseY)
    particle.draw(ctx, props.particleColor)
  })

  animationId = requestAnimationFrame(animate)
}

const handleResize = () => {
  if (canvas.value) {
    canvas.value.width = window.innerWidth
    canvas.value.height = window.innerHeight
    initParticles(canvas.value.width, canvas.value.height)
  }
}

const handleMouseMove = (e) => {
  mouseX = e.clientX
  mouseY = e.clientY
}

onMounted(() => {
  if (canvas.value) {
    canvas.value.width = window.innerWidth
    canvas.value.height = window.innerHeight
    initParticles(canvas.value.width, canvas.value.height)
    animate()

    window.addEventListener('resize', handleResize)
    window.addEventListener('mousemove', handleMouseMove)
  }
})

onUnmounted(() => {
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('mousemove', handleMouseMove)
})
</script>

<template>
  <canvas
    ref="canvas"
    class="particle-canvas"
  />
</template>

<style scoped>
.particle-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  opacity: 0.8;
}
</style>
