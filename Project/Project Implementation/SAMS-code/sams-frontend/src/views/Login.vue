<template>
  <div class="login-page" @mousemove="handleMouseMove" @mouseleave="resetParallax">
    <!-- Split Screen Container -->
    <div class="login-container">

      <!-- LEFT PANEL: 3D Scene -->
      <div class="scene-panel" style="background: linear-gradient(180deg, #050510 0%, #0a0a1f 50%, #12122f 100%);">
        <div class="scene-3d-wrapper" :style="sceneTransformStyle">
          <!-- Background Gradient Layers -->
          <div class="scene-bg-gradient" style="background: radial-gradient(ellipse at 20% 20%, rgba(99, 102, 241, 0.15) 0%, transparent 50%), radial-gradient(ellipse at 80% 80%, rgba(139, 92, 246, 0.12) 0%, transparent 50%), radial-gradient(ellipse at 50% 50%, rgba(236, 72, 153, 0.08) 0%, transparent 60%);"></div>
          <div class="scene-bg-glow"></div>

          <!-- Animated Perspective Grid -->
          <div class="perspective-grid"></div>

          <!-- Floating 3D Shapes -->
          <div class="floating-shapes">
            <!-- Cubes -->
            <div class="shape-cube cube-1" style="--delay: 0s; --size: 60px;">
              <div class="face front"></div>
              <div class="face back"></div>
              <div class="face left"></div>
              <div class="face right"></div>
              <div class="face top"></div>
              <div class="face bottom"></div>
            </div>
            <div class="shape-cube cube-2" style="--delay: 2s; --size: 45px;">
              <div class="face front"></div>
              <div class="face back"></div>
              <div class="face left"></div>
              <div class="face right"></div>
              <div class="face top"></div>
              <div class="face bottom"></div>
            </div>

            <!-- Octahedrons -->
            <div class="shape-octahedron octa-1" style="--delay: 1s; --size: 50px;"></div>
            <div class="shape-octahedron octa-2" style="--delay: 3s; --size: 35px;"></div>

            <!-- Pyramids -->
            <div class="shape-pyramid pyramid-1" style="--delay: 0.5s; --size: 55px;"></div>
            <div class="shape-pyramid pyramid-2" style="--delay: 2.5s; --size: 40px;"></div>

            <!-- Spheres -->
            <div class="shape-sphere sphere-1" style="--delay: 1.5s; --size: 40px;"></div>
            <div class="shape-sphere sphere-2" style="--delay: 3.5s; --size: 30px;"></div>
            <div class="shape-sphere sphere-3" style="--delay: 4s; --size: 25px;"></div>
          </div>

          <!-- Orbital Rings -->
          <div class="orbital-ring ring-1"></div>
          <div class="orbital-ring ring-2"></div>

          <!-- Branding Content -->
          <div class="branding-content">
            <div class="logo-3d-container">
              <div class="logo-glow"></div>
              <div class="logo-icon">
                <i class="pi pi-graduation-cap"></i>
              </div>
            </div>
            <h1 class="title-3d">SAMS</h1>
            <p class="tagline">Student Academic Management System</p>
            <div class="decorative-line">
              <span class="line-segment"></span>
              <span class="line-dot"></span>
              <span class="line-segment"></span>
            </div>
          </div>

          <!-- Particle Effects -->
          <div class="particles">
            <div v-for="n in 20" :key="n" class="particle" :style="getParticleStyle(n)"></div>
          </div>
        </div>
      </div>

      <!-- RIGHT PANEL: Login Form -->
      <div class="form-panel">
        <div class="form-panel-bg"></div>
        <div class="form-glass-container" :class="{ 'loaded': isLoaded }">
          <!-- Form Header -->
          <div class="form-header">
            <div class="welcome-icon">
              <i class="pi pi-unlock"></i>
            </div>
            <h2 class="welcome-title">Welcome Back</h2>
            <p class="welcome-subtitle">Sign in to continue your journey</p>
          </div>

          <!-- Error Message -->
          <Transition name="shake-fade">
            <div v-if="errorMessage" class="error-banner">
              <i class="pi pi-exclamation-triangle"></i>
              <span>{{ errorMessage }}</span>
              <button @click="errorMessage = ''" class="error-close">
                <i class="pi pi-times"></i>
              </button>
            </div>
          </Transition>

          <!-- Login Form -->
          <form @submit.prevent="handleLogin" class="login-form">
            <!-- Username Field -->
            <div class="input-group" :class="{ 'focused': fieldFocus.username, 'has-value': credentials.username }">
              <div class="input-icon">
                <i class="pi pi-user"></i>
              </div>
              <input
                type="text"
                v-model="credentials.username"
                @focus="fieldFocus.username = true"
                @blur="fieldFocus.username = false"
                required
                autocomplete="username"
                placeholder=" "
                class="glass-input"
              />
              <label class="floating-label">Username</label>
              <div class="input-glow"></div>
              <div class="input-border"></div>
            </div>

            <!-- Password Field -->
            <div class="input-group" :class="{ 'focused': fieldFocus.password, 'has-value': credentials.password }">
              <div class="input-icon">
                <i class="pi pi-lock"></i>
              </div>
              <input
                :type="showPassword ? 'text' : 'password'"
                v-model="credentials.password"
                @focus="fieldFocus.password = true"
                @blur="fieldFocus.password = false"
                required
                autocomplete="current-password"
                placeholder=" "
                class="glass-input"
              />
              <label class="floating-label">Password</label>
              <button type="button" @click="showPassword = !showPassword" class="password-toggle">
                <i :class="showPassword ? 'pi pi-eye-slash' : 'pi pi-eye'"></i>
              </button>
              <div class="input-glow"></div>
              <div class="input-border"></div>
            </div>

            <!-- Options Row -->
            <div class="options-row">
              <label class="checkbox-wrapper">
                <input type="checkbox" v-model="rememberMe" />
                <span class="checkbox-custom"></span>
                <span class="checkbox-label">Remember me</span>
              </label>
              <a @click.prevent="handleForgotPassword" href="#" class="forgot-link">
                Forgot password?
              </a>
            </div>

            <!-- Submit Button -->
            <button type="submit" class="btn-submit" :class="{ 'loading': loading }" :disabled="loading">
              <span class="btn-bg"></span>
              <span class="btn-content">
                <span class="btn-text">{{ loading ? 'Signing in...' : 'Sign In' }}</span>
                <i v-if="!loading" class="pi pi-arrow-right btn-arrow"></i>
                <span v-else class="btn-spinner"></span>
              </span>
              <span class="btn-shine"></span>
            </button>
          </form>

          <!-- Divider -->
          <div class="divider">
            <span class="divider-line"></span>
            <span class="divider-text">Quick Access</span>
            <span class="divider-line"></span>
          </div>

          <!-- Demo Credentials -->
          <button @click="showTestAccounts = !showTestAccounts" class="demo-toggle">
            <i class="pi pi-info-circle"></i>
            <span>Demo Credentials</span>
            <i :class="showTestAccounts ? 'pi pi-chevron-up' : 'pi pi-chevron-down'" class="toggle-arrow"></i>
          </button>

          <Transition name="expand">
            <div v-if="showTestAccounts" class="demo-accounts">
              <div
                v-for="account in testAccounts"
                :key="account.username"
                @click="fillCredentials(account)"
                class="demo-card"
              >
                <div class="demo-icon" :style="{ '--accent': account.color }">
                  <i :class="account.icon"></i>
                </div>
                <div class="demo-info">
                  <span class="demo-role">{{ account.role }}</span>
                  <span class="demo-creds">{{ account.username }} / {{ account.password }}</span>
                </div>
                <i class="pi pi-arrow-right demo-arrow"></i>
              </div>
            </div>
          </Transition>

          <!-- Footer -->
          <div class="form-footer">
            <p>&copy; 2024 SAMS. All rights reserved.</p>
            <div class="footer-links">
              <a href="#"><i class="pi pi-question-circle"></i> Help</a>
              <a href="#"><i class="pi pi-shield"></i> Privacy</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useToast } from 'primevue/usetoast'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

// Form State
const credentials = ref({ username: '', password: '' })
const loading = ref(false)
const errorMessage = ref('')
const showTestAccounts = ref(false)
const rememberMe = ref(false)
const showPassword = ref(false)
const fieldFocus = ref({ username: false, password: false })
const isLoaded = ref(false)

// Parallax State
const mousePosition = ref({ x: 0, y: 0 })
const targetPosition = ref({ x: 0, y: 0 })
let animationFrame = null

// Demo Accounts
const testAccounts = [
  {
    role: 'Super Admin',
    username: 'superadmin',
    password: 'Admin@123',
    icon: 'pi pi-shield',
    color: '#f43f5e'
  },
  {
    role: 'Student',
    username: 'nassim',
    password: 'password123',
    icon: 'pi pi-graduation-cap',
    color: '#6366f1'
  },
  {
    role: 'Faculty',
    username: 'karim',
    password: 'password123',
    icon: 'pi pi-briefcase',
    color: '#8b5cf6'
  }
]

// Computed Styles
const sceneTransformStyle = computed(() => ({
  transform: `
    rotateY(${mousePosition.value.x * 5}deg)
    rotateX(${-mousePosition.value.y * 5}deg)
  `
}))

// Parallax Handler with smooth interpolation
const handleMouseMove = (event) => {
  const { clientX, clientY } = event
  const centerX = window.innerWidth / 2
  const centerY = window.innerHeight / 2

  targetPosition.value = {
    x: (clientX - centerX) / centerX,
    y: (clientY - centerY) / centerY
  }
}

const resetParallax = () => {
  targetPosition.value = { x: 0, y: 0 }
}

const animateParallax = () => {
  mousePosition.value.x += (targetPosition.value.x - mousePosition.value.x) * 0.08
  mousePosition.value.y += (targetPosition.value.y - mousePosition.value.y) * 0.08
  animationFrame = requestAnimationFrame(animateParallax)
}

// Particle Styles Generator
const getParticleStyle = (n) => {
  const size = Math.random() * 4 + 2
  const left = Math.random() * 100
  const top = Math.random() * 100
  const duration = Math.random() * 10 + 10
  const delay = Math.random() * 5

  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    top: `${top}%`,
    animationDuration: `${duration}s`,
    animationDelay: `${delay}s`
  }
}

// Form Handlers
const fillCredentials = (account) => {
  credentials.value.username = account.username
  credentials.value.password = account.password
  showTestAccounts.value = false

  toast.add({
    severity: 'info',
    summary: 'Demo Account',
    detail: `${account.role} credentials loaded`,
    life: 2000
  })
}

const handleForgotPassword = () => {
  toast.add({
    severity: 'info',
    summary: 'Coming Soon',
    detail: 'Password reset will be available soon. Contact admin for help.',
    life: 4000
  })
}

const handleLogin = async () => {
  if (!credentials.value.username || !credentials.value.password) {
    errorMessage.value = 'Please enter both username and password'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const result = await authStore.login(credentials.value)

    if (result.success) {
      toast.add({
        severity: 'success',
        summary: 'Welcome!',
        detail: 'Login successful. Redirecting...',
        life: 2000
      })

      setTimeout(() => {
        if (authStore.isAdmin) router.push('/admin')
        else if (authStore.isStudent) router.push('/student')
        else if (authStore.isFaculty) router.push('/faculty')
      }, 500)
    } else {
      errorMessage.value = result.message || 'Invalid credentials'
    }
  } catch (error) {
    errorMessage.value = 'Connection error. Please try again.'
  } finally {
    loading.value = false
  }
}

// Lifecycle
onMounted(() => {
  setTimeout(() => {
    isLoaded.value = true
  }, 100)
  animationFrame = requestAnimationFrame(animateParallax)
})

onUnmounted(() => {
  if (animationFrame) {
    cancelAnimationFrame(animationFrame)
  }
})
</script>

<style scoped>
/* ============================================
   FONTS & CSS VARIABLES
   ============================================ */
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@300;400;500;600;700;800&family=Space+Grotesk:wght@400;500;600;700&display=swap');

:root {
  --font-display: 'Outfit', sans-serif;
  --font-body: 'Space Grotesk', sans-serif;

  /* Scene Colors */
  --scene-dark: #050510;
  --scene-mid: #0a0a1f;
  --scene-light: #12122f;

  /* Accent Colors */
  --accent-primary: #6366f1;
  --accent-secondary: #8b5cf6;
  --accent-tertiary: #ec4899;
  --accent-cyan: #06b6d4;

  /* Glass Effects */
  --glass-bg: rgba(255, 255, 255, 0.03);
  --glass-border: rgba(255, 255, 255, 0.08);
  --glass-highlight: rgba(255, 255, 255, 0.12);
}

/* ============================================
   BASE LAYOUT
   ============================================ */
.login-page {
  min-height: 100vh;
  overflow: hidden;
  background: var(--scene-dark);
  font-family: var(--font-body);
}

.login-container {
  display: grid;
  grid-template-columns: 55% 45%;
  min-height: 100vh;
}

/* ============================================
   LEFT PANEL: 3D SCENE
   ============================================ */
.scene-panel {
  position: relative;
  overflow: hidden;
  perspective: 1500px;
  perspective-origin: center center;
  background: linear-gradient(180deg, #050510 0%, #0a0a1f 50%, #12122f 100%);
}

.scene-3d-wrapper {
  position: absolute;
  inset: 0;
  transform-style: preserve-3d;
  transition: transform 0.05s ease-out;
}

/* Background Layers */
.scene-bg-gradient {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse at 20% 20%, rgba(99, 102, 241, 0.15) 0%, transparent 50%), radial-gradient(ellipse at 80% 80%, rgba(139, 92, 246, 0.12) 0%, transparent 50%), radial-gradient(ellipse at 50% 50%, rgba(236, 72, 153, 0.08) 0%, transparent 60%), linear-gradient(180deg, #050510 0%, #0a0a1f 50%, #12122f 100%);
}

.scene-bg-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 600px;
  height: 600px;
  transform: translate(-50%, -50%);
  background: radial-gradient(circle, rgba(99, 102, 241, 0.2) 0%, transparent 70%);
  filter: blur(60px);
  animation: glow-breathe 8s ease-in-out infinite;
}

@keyframes glow-breathe {
  0%, 100% { opacity: 0.5; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.2); }
}

/* Perspective Grid */
.perspective-grid {
  position: absolute;
  bottom: 0;
  left: -100%;
  width: 300%;
  height: 50%;
  background:
    linear-gradient(90deg, rgba(99, 102, 241, 0.1) 1px, transparent 1px),
    linear-gradient(180deg, rgba(99, 102, 241, 0.1) 1px, transparent 1px);
  background-size: 80px 80px;
  transform: rotateX(75deg) translateZ(-50px);
  transform-origin: center bottom;
  animation: grid-scroll 25s linear infinite;
  mask-image: linear-gradient(to top, black 0%, black 40%, transparent 100%);
  -webkit-mask-image: linear-gradient(to top, black 0%, black 40%, transparent 100%);
}

@keyframes grid-scroll {
  from { background-position: 0 0; }
  to { background-position: 80px 80px; }
}

/* ============================================
   3D SHAPES
   ============================================ */
.floating-shapes {
  position: absolute;
  inset: 0;
  transform-style: preserve-3d;
}

/* 3D Cube */
.shape-cube {
  position: absolute;
  width: var(--size);
  height: var(--size);
  transform-style: preserve-3d;
  animation:
    cube-float 12s ease-in-out infinite var(--delay),
    cube-rotate 20s linear infinite var(--delay);
}

.shape-cube .face {
  position: absolute;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.2) 0%, rgba(139, 92, 246, 0.05) 100%);
  border: 1px solid rgba(99, 102, 241, 0.3);
  box-shadow: inset 0 0 30px rgba(99, 102, 241, 0.1);
  backdrop-filter: blur(2px);
}

.shape-cube .front { transform: translateZ(calc(var(--size) / 2)); }
.shape-cube .back { transform: rotateY(180deg) translateZ(calc(var(--size) / 2)); }
.shape-cube .left { transform: rotateY(-90deg) translateZ(calc(var(--size) / 2)); }
.shape-cube .right { transform: rotateY(90deg) translateZ(calc(var(--size) / 2)); }
.shape-cube .top { transform: rotateX(90deg) translateZ(calc(var(--size) / 2)); }
.shape-cube .bottom { transform: rotateX(-90deg) translateZ(calc(var(--size) / 2)); }

.cube-1 { top: 15%; left: 15%; }
.cube-2 { top: 65%; left: 70%; }

@keyframes cube-float {
  0%, 100% { transform: translateY(0) translateX(0); }
  25% { transform: translateY(-40px) translateX(20px); }
  50% { transform: translateY(-20px) translateX(-15px); }
  75% { transform: translateY(-50px) translateX(10px); }
}

@keyframes cube-rotate {
  from { transform: rotateX(0deg) rotateY(0deg) rotateZ(0deg); }
  to { transform: rotateX(360deg) rotateY(360deg) rotateZ(180deg); }
}

/* Octahedron (Diamond) */
.shape-octahedron {
  position: absolute;
  width: var(--size);
  height: var(--size);
  animation:
    octa-float 15s ease-in-out infinite var(--delay),
    octa-spin 25s linear infinite var(--delay);
}

.shape-octahedron::before,
.shape-octahedron::after {
  content: '';
  position: absolute;
  width: 0;
  height: 0;
  left: 50%;
  transform: translateX(-50%);
  filter: drop-shadow(0 0 15px rgba(168, 85, 247, 0.5));
}

.shape-octahedron::before {
  top: 0;
  border-left: calc(var(--size) / 2) solid transparent;
  border-right: calc(var(--size) / 2) solid transparent;
  border-bottom: var(--size) solid rgba(168, 85, 247, 0.4);
}

.shape-octahedron::after {
  bottom: 0;
  border-left: calc(var(--size) / 2) solid transparent;
  border-right: calc(var(--size) / 2) solid transparent;
  border-top: var(--size) solid rgba(236, 72, 153, 0.4);
}

.octa-1 { top: 35%; left: 75%; }
.octa-2 { top: 75%; left: 25%; }

@keyframes octa-float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-35px); }
}

@keyframes octa-spin {
  from { transform: rotateY(0deg); }
  to { transform: rotateY(360deg); }
}

/* Pyramid */
.shape-pyramid {
  position: absolute;
  width: var(--size);
  height: var(--size);
  animation: pyramid-float 18s ease-in-out infinite var(--delay);
}

.shape-pyramid::before {
  content: '';
  position: absolute;
  width: 0;
  height: 0;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border-left: calc(var(--size) / 2) solid transparent;
  border-right: calc(var(--size) / 2) solid transparent;
  border-bottom: var(--size) solid rgba(6, 182, 212, 0.35);
  filter: drop-shadow(0 0 20px rgba(6, 182, 212, 0.5));
  animation: pyramid-rotate 30s linear infinite;
}

.pyramid-1 { top: 55%; left: 20%; }
.pyramid-2 { top: 20%; left: 60%; }

@keyframes pyramid-float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.05); }
}

@keyframes pyramid-rotate {
  from { transform: translate(-50%, -50%) rotateY(0deg); }
  to { transform: translate(-50%, -50%) rotateY(360deg); }
}

/* Glowing Spheres */
.shape-sphere {
  position: absolute;
  width: var(--size);
  height: var(--size);
  border-radius: 50%;
  background: radial-gradient(
    circle at 30% 30%,
    rgba(255, 255, 255, 0.4) 0%,
    rgba(99, 102, 241, 0.3) 30%,
    rgba(139, 92, 246, 0.1) 70%,
    transparent 100%
  );
  box-shadow:
    0 0 30px rgba(99, 102, 241, 0.4),
    0 0 60px rgba(99, 102, 241, 0.2),
    inset 0 0 20px rgba(255, 255, 255, 0.1);
  animation:
    sphere-float 10s ease-in-out infinite var(--delay),
    sphere-glow 4s ease-in-out infinite;
}

.sphere-1 { top: 25%; left: 45%; }
.sphere-2 { top: 70%; left: 55%; }
.sphere-3 { top: 45%; left: 85%; }

@keyframes sphere-float {
  0%, 100% { transform: translate(0, 0); }
  25% { transform: translate(15px, -25px); }
  50% { transform: translate(-10px, -40px); }
  75% { transform: translate(20px, -15px); }
}

@keyframes sphere-glow {
  0%, 100% { box-shadow: 0 0 30px rgba(99, 102, 241, 0.4), 0 0 60px rgba(99, 102, 241, 0.2); }
  50% { box-shadow: 0 0 50px rgba(99, 102, 241, 0.6), 0 0 100px rgba(99, 102, 241, 0.3); }
}

/* Orbital Rings */
.orbital-ring {
  position: absolute;
  border: 1px solid rgba(99, 102, 241, 0.15);
  border-radius: 50%;
  transform-style: preserve-3d;
}

.orbital-ring::before {
  content: '';
  position: absolute;
  inset: 15px;
  border: 1px solid rgba(139, 92, 246, 0.1);
  border-radius: 50%;
}

.ring-1 {
  width: 350px;
  height: 350px;
  top: 10%;
  left: 5%;
  animation: ring-orbit 40s linear infinite;
}

.ring-2 {
  width: 250px;
  height: 250px;
  bottom: 15%;
  right: 10%;
  animation: ring-orbit 30s linear infinite reverse;
}

@keyframes ring-orbit {
  from { transform: rotateX(70deg) rotateZ(0deg); }
  to { transform: rotateX(70deg) rotateZ(360deg); }
}

/* Particles */
.particles {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.particle {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  animation: particle-drift 15s ease-in-out infinite;
}

@keyframes particle-drift {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0;
  }
  10% { opacity: 1; }
  90% { opacity: 1; }
  50% {
    transform: translate(50px, -100px) scale(0.5);
  }
}

/* ============================================
   BRANDING CONTENT
   ============================================ */
.branding-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  z-index: 10;
}

.logo-3d-container {
  position: relative;
  display: inline-block;
  margin-bottom: 1.5rem;
}

.logo-glow {
  position: absolute;
  inset: -30px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.4) 0%, transparent 70%);
  border-radius: 50%;
  animation: logo-pulse 4s ease-in-out infinite;
}

@keyframes logo-pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.logo-icon {
  position: relative;
  width: 90px;
  height: 90px;
  background: linear-gradient(135deg, var(--accent-primary) 0%, var(--accent-secondary) 100%);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 20px 50px rgba(99, 102, 241, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2),
    inset 0 -1px 0 rgba(0, 0, 0, 0.1);
  animation: logo-float 6s ease-in-out infinite;
}

@keyframes logo-float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-10px) rotate(2deg); }
}

.logo-icon i {
  font-size: 2.5rem;
  color: white;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.title-3d {
  font-family: var(--font-display);
  font-size: 5rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.7) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
  filter: drop-shadow(0 4px 20px rgba(255, 255, 255, 0.2));
}

.tagline {
  font-family: var(--font-body);
  color: rgba(255, 255, 255, 0.6);
  font-size: 1.1rem;
  font-weight: 400;
  letter-spacing: 0.05em;
}

.decorative-line {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 2rem;
}

.line-segment {
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(99, 102, 241, 0.5), transparent);
}

.line-dot {
  width: 8px;
  height: 8px;
  background: var(--accent-primary);
  border-radius: 50%;
  animation: dot-pulse 2s ease-in-out infinite;
}

@keyframes dot-pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0.5; }
}

/* ============================================
   RIGHT PANEL: FORM
   ============================================ */
.form-panel {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  z-index: 10;
}

.form-panel-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, rgba(10, 10, 31, 0.98) 0%, rgba(18, 18, 47, 0.95) 100%);
  backdrop-filter: blur(20px);
}

.form-glass-container {
  position: relative;
  width: 100%;
  max-width: 420px;
  padding: 2.5rem;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.05) 0%,
    rgba(255, 255, 255, 0.02) 100%
  );
  border: 1px solid var(--glass-border);
  border-radius: 28px;
  box-shadow:
    0 25px 50px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.05);

  /* Entrance Animation */
  opacity: 0;
  transform: translateX(60px) scale(0.95);
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

.form-glass-container.loaded {
  opacity: 1;
  transform: translateX(0) scale(1);
}

/* Form Header */
.form-header {
  text-align: center;
  margin-bottom: 2rem;
}

.welcome-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 1rem;
  background: linear-gradient(135deg, var(--accent-primary), var(--accent-secondary));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 30px rgba(99, 102, 241, 0.3);
}

.welcome-icon i {
  font-size: 1.5rem;
  color: white;
}

.welcome-title {
  font-family: var(--font-display);
  font-size: 1.75rem;
  font-weight: 700;
  color: white;
  margin-bottom: 0.5rem;
}

.welcome-subtitle {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.95rem;
}

/* Error Banner */
.error-banner {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: rgba(244, 63, 94, 0.1);
  border: 1px solid rgba(244, 63, 94, 0.2);
  border-radius: 12px;
  margin-bottom: 1.5rem;
  color: #f87171;
  font-size: 0.9rem;
}

.error-banner i:first-child {
  font-size: 1.1rem;
}

.error-banner span {
  flex: 1;
}

.error-close {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  padding: 4px;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.error-close:hover {
  opacity: 1;
}

/* Input Groups */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.input-group {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.3);
  transition: color 0.3s;
  z-index: 2;
}

.input-group.focused .input-icon,
.input-group.has-value .input-icon {
  color: var(--accent-primary);
}

.glass-input {
  width: 100%;
  padding: 1rem 1rem 1rem 3rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  color: white;
  font-family: var(--font-body);
  font-size: 1rem;
  outline: none;
  transition: all 0.3s ease;
}

.glass-input:focus {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(99, 102, 241, 0.5);
}

.glass-input::placeholder {
  color: transparent;
}

.floating-label {
  position: absolute;
  left: 3rem;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(255, 255, 255, 0.4);
  font-size: 1rem;
  pointer-events: none;
  transition: all 0.3s ease;
}

.input-group.focused .floating-label,
.input-group.has-value .floating-label {
  top: 0;
  left: 1rem;
  transform: translateY(-50%);
  font-size: 0.75rem;
  color: var(--accent-primary);
  background: var(--scene-mid);
  padding: 0 0.5rem;
}

.input-glow {
  position: absolute;
  inset: 0;
  border-radius: 14px;
  opacity: 0;
  background: radial-gradient(circle at center, rgba(99, 102, 241, 0.15), transparent 70%);
  transition: opacity 0.3s;
  pointer-events: none;
}

.input-group.focused .input-glow {
  opacity: 1;
}

.input-border {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-secondary));
  transition: all 0.3s ease;
  transform: translateX(-50%);
  border-radius: 2px;
}

.input-group.focused .input-border {
  width: calc(100% - 2rem);
}

.password-toggle {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  padding: 4px;
  transition: color 0.2s;
  z-index: 2;
}

.password-toggle:hover {
  color: var(--accent-primary);
}

/* Options Row */
.options-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 0.5rem;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  user-select: none;
}

.checkbox-wrapper input {
  display: none;
}

.checkbox-custom {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-radius: 5px;
  position: relative;
  transition: all 0.2s;
}

.checkbox-wrapper input:checked + .checkbox-custom {
  background: var(--accent-primary);
  border-color: var(--accent-primary);
}

.checkbox-wrapper input:checked + .checkbox-custom::after {
  content: '';
  position: absolute;
  left: 5px;
  top: 2px;
  width: 4px;
  height: 8px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-label {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.875rem;
}

.forgot-link {
  color: var(--accent-primary);
  font-size: 0.875rem;
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: var(--accent-secondary);
}

/* Submit Button */
.btn-submit {
  position: relative;
  width: 100%;
  padding: 1rem 2rem;
  margin-top: 0.5rem;
  border: none;
  border-radius: 14px;
  cursor: pointer;
  overflow: hidden;
  font-family: var(--font-body);
  font-size: 1rem;
  font-weight: 600;
  color: white;
  background: transparent;
}

.btn-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, var(--accent-primary) 0%, var(--accent-secondary) 100%);
  transition: transform 0.3s ease;
}

.btn-submit:hover .btn-bg {
  transform: scale(1.02);
}

.btn-submit:active .btn-bg {
  transform: scale(0.98);
}

.btn-content {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  z-index: 1;
}

.btn-arrow {
  transition: transform 0.3s ease;
}

.btn-submit:hover .btn-arrow {
  transform: translateX(4px);
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn-submit:hover .btn-shine {
  left: 100%;
}

.btn-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.btn-submit:disabled {
  cursor: not-allowed;
}

.btn-submit.loading .btn-bg {
  opacity: 0.7;
}

/* Divider */
.divider {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin: 1.5rem 0;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
}

.divider-text {
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.8rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

/* Demo Toggle */
.demo-toggle {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.6);
  font-family: var(--font-body);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.demo-toggle:hover {
  background: rgba(255, 255, 255, 0.06);
  color: white;
}

.toggle-arrow {
  margin-left: auto;
  transition: transform 0.3s;
}

/* Demo Accounts */
.demo-accounts {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1rem;
  overflow: hidden;
}

.demo-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.demo-card:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.12);
  transform: translateX(4px);
}

.demo-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, var(--accent, var(--accent-primary)), transparent);
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.demo-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.demo-role {
  color: white;
  font-weight: 600;
  font-size: 0.95rem;
}

.demo-creds {
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.8rem;
  font-family: monospace;
}

.demo-arrow {
  color: rgba(255, 255, 255, 0.3);
  transition: all 0.3s;
}

.demo-card:hover .demo-arrow {
  color: var(--accent-primary);
  transform: translateX(4px);
}

/* Footer */
.form-footer {
  margin-top: 2rem;
  text-align: center;
}

.form-footer p {
  color: rgba(255, 255, 255, 0.3);
  font-size: 0.8rem;
  margin-bottom: 0.75rem;
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 1.5rem;
}

.footer-links a {
  color: rgba(255, 255, 255, 0.4);
  font-size: 0.8rem;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.35rem;
  transition: color 0.2s;
}

.footer-links a:hover {
  color: var(--accent-primary);
}

/* ============================================
   TRANSITIONS
   ============================================ */
.shake-fade-enter-active {
  animation: shake-in 0.5s ease-out;
}

.shake-fade-leave-active {
  animation: fade-out 0.3s ease-out;
}

@keyframes shake-in {
  0% { opacity: 0; transform: translateX(-10px); }
  25% { transform: translateX(10px); }
  50% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
  100% { opacity: 1; transform: translateX(0); }
}

@keyframes fade-out {
  from { opacity: 1; }
  to { opacity: 0; }
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  max-height: 0;
  margin-top: 0;
}

.expand-enter-to,
.expand-leave-from {
  opacity: 1;
  max-height: 300px;
  margin-top: 1rem;
}

/* ============================================
   RESPONSIVE DESIGN
   ============================================ */
@media (max-width: 1200px) {
  .login-container {
    grid-template-columns: 50% 50%;
  }

  .title-3d {
    font-size: 4rem;
  }
}

@media (max-width: 1024px) {
  .login-container {
    grid-template-columns: 1fr;
    grid-template-rows: 35vh 1fr;
  }

  .scene-panel {
    min-height: 35vh;
  }

  .branding-content {
    transform: translate(-50%, -50%) scale(0.85);
  }

  .form-glass-container {
    max-width: 100%;
    margin: 0 auto;
  }

  .title-3d {
    font-size: 3.5rem;
  }
}

@media (max-width: 768px) {
  .login-container {
    grid-template-rows: 1fr;
  }

  .scene-panel {
    position: fixed;
    inset: 0;
    z-index: 0;
  }

  .scene-3d-wrapper {
    opacity: 0.4;
  }

  .branding-content {
    display: none;
  }

  .form-panel {
    background: transparent;
    min-height: 100vh;
    padding: 1rem;
  }

  .form-panel-bg {
    background: rgba(5, 5, 16, 0.85);
    backdrop-filter: blur(30px);
  }

  .form-glass-container {
    padding: 2rem 1.5rem;
    border-radius: 20px;
  }

  .perspective-grid {
    opacity: 0.3;
  }

  /* Reduce animations on mobile */
  .shape-cube,
  .shape-octahedron,
  .shape-pyramid,
  .shape-sphere {
    animation-duration: 30s !important;
  }
}

@media (max-width: 480px) {
  .form-glass-container {
    padding: 1.5rem 1.25rem;
  }

  .welcome-title {
    font-size: 1.5rem;
  }

  .glass-input {
    padding: 0.875rem 0.875rem 0.875rem 2.75rem;
  }
}

/* Reduced Motion */
@media (prefers-reduced-motion: reduce) {
  *,
  *::before,
  *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }

  .scene-3d-wrapper {
    transform: none !important;
  }
}
</style>
