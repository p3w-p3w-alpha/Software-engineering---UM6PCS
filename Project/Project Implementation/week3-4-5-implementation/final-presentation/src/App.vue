<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import gsap from 'gsap'
import { useSlideNavigation } from './composables/useSlideNavigation'

// Effects Components
import ParticleField from './components/effects/ParticleField.vue'
import AuroraBackground from './components/effects/AuroraBackground.vue'
import FloatingOrbs from './components/effects/FloatingOrbs.vue'
import CustomCursor from './components/effects/CustomCursor.vue'

// UI Components
import Card3D from './components/ui/Card3D.vue'
import GlowingText from './components/ui/GlowingText.vue'
import AnimatedCounter from './components/ui/AnimatedCounter.vue'
import MagneticButton from './components/ui/MagneticButton.vue'
import IconBadge from './components/ui/IconBadge.vue'

// Lucide Icons
import {
  GraduationCap, BookOpen, Code2, Database, Server,
  Users, Shield, Zap, Layout, Layers, GitBranch,
  CheckCircle, AlertTriangle, Lightbulb, ArrowRight,
  ArrowLeft, ChevronRight, ChevronDown, Monitor,
  Smartphone, Globe, Lock, Settings, BarChart3,
  FileText, Calendar, CreditCard, MessageSquare,
  UserCheck, ClipboardList, Award, Target, Rocket,
  Heart, Coffee, Bug, Wrench, Star, Play, Sparkles,
  Terminal, Cpu, Cloud, Share2, Palette
} from 'lucide-vue-next'

const TOTAL_SLIDES = 12
const { currentSlide, progress, goToSlide, nextSlide, prevSlide, canGoNext, canGoPrev } = useSlideNavigation(TOTAL_SLIDES)

const slideNames = [
  'Title',
  'Feasibility',
  'Requirements',
  'Architecture',
  'API Design',
  'Project Structure',
  'Student Features',
  'Faculty Features',
  'Admin Features',
  'Technologies',
  'Challenges',
  'Conclusion'
]

// GSAP animations for slide content
const slideContent = ref(null)

watch(currentSlide, () => {
  // Re-trigger stagger animations on slide change
  const elements = document.querySelectorAll('.stagger-item')
  gsap.killTweensOf(elements)
  gsap.fromTo(elements,
    { opacity: 0, y: 40, scale: 0.95 },
    {
      opacity: 1,
      y: 0,
      scale: 1,
      duration: 0.8,
      stagger: 0.1,
      ease: 'power3.out',
      delay: 0.2
    }
  )
})

onMounted(() => {
  // Initial animation
  gsap.fromTo('.stagger-item',
    { opacity: 0, y: 40, scale: 0.95 },
    {
      opacity: 1,
      y: 0,
      scale: 1,
      duration: 0.8,
      stagger: 0.1,
      ease: 'power3.out',
      delay: 0.5
    }
  )
})
</script>

<template>
  <div class="presentation-container relative w-full h-screen overflow-hidden bg-[#06060f]">
    <!-- Background Effects Layer -->
    <AuroraBackground />
    <FloatingOrbs />
    <ParticleField :particle-count="60" :speed="0.2" />

    <!-- Custom Cursor -->
    <CustomCursor />

    <!-- Grain Overlay -->
    <div class="grain-overlay"></div>

    <!-- Progress Bar -->
    <div class="fixed top-0 left-0 right-0 z-50 h-1 bg-white/5">
      <div
        class="progress-bar h-full transition-all duration-500 ease-out"
        :style="{ width: `${progress}%` }"
      ></div>
    </div>

    <!-- Navigation Dots -->
    <div class="fixed right-8 top-1/2 -translate-y-1/2 z-50 flex flex-col gap-3">
      <button
        v-for="(name, index) in slideNames"
        :key="index"
        @click="goToSlide(index)"
        class="nav-dot group relative"
        :class="{ active: currentSlide === index }"
        :title="name"
        data-cursor-hover
      >
        <span class="absolute right-full mr-4 px-4 py-2 rounded-xl glass-dark text-sm text-white/90 opacity-0 group-hover:opacity-100 transition-all duration-300 whitespace-nowrap font-medium">
          {{ name }}
        </span>
      </button>
    </div>

    <!-- Slide Counter -->
    <div class="fixed bottom-6 left-6 z-50 font-mono text-sm flex items-center gap-2 glass-subtle px-4 py-2 rounded-xl">
      <span class="text-gradient-subtle text-xl font-bold">{{ String(currentSlide + 1).padStart(2, '0') }}</span>
      <span class="text-white/30">/</span>
      <span class="text-white/40">{{ String(TOTAL_SLIDES).padStart(2, '0') }}</span>
    </div>

    <!-- Slides Container -->
    <div ref="slideContent" class="relative w-full h-full">
      <Transition name="slide-morph" mode="out-in">
        <!-- SLIDE 0: Title -->
        <div v-if="currentSlide === 0" :key="0" class="slide flex items-center justify-center">
          <div class="text-center max-w-5xl">
            <div class="stagger-item inline-flex items-center gap-3 px-6 py-3 rounded-full glass-subtle mb-10">
              <Sparkles class="w-5 h-5 text-primary-400" />
              <span class="text-white/80 text-sm font-semibold tracking-widest uppercase">Software Engineering Project</span>
            </div>

            <div class="stagger-item mb-8">
              <GlowingText
                text="SAMS"
                tag="h1"
                variant="split"
                class="font-display text-[8rem] md:text-[12rem] font-bold leading-none tracking-tight"
              />
            </div>

            <p class="stagger-item text-2xl md:text-4xl text-white/80 font-light mb-4 tracking-wide">
              Student Academic Management System
            </p>

            <p class="stagger-item text-lg text-white/40 mb-16">
              A comprehensive multi-role academic platform built with modern technologies
            </p>

            <div class="stagger-item flex flex-wrap justify-center gap-4 mb-16">
              <div class="tech-badge-premium">
                <div class="tech-badge-icon bg-emerald-500/20">
                  <Code2 class="w-4 h-4 text-emerald-400" />
                </div>
                <span>Vue.js 3</span>
              </div>
              <div class="tech-badge-premium">
                <div class="tech-badge-icon bg-green-500/20">
                  <Server class="w-4 h-4 text-green-400" />
                </div>
                <span>Spring Boot</span>
              </div>
              <div class="tech-badge-premium">
                <div class="tech-badge-icon bg-blue-500/20">
                  <Database class="w-4 h-4 text-blue-400" />
                </div>
                <span>PostgreSQL</span>
              </div>
            </div>

            <Card3D class="stagger-item inline-block" variant="glass">
              <div class="p-10">
                <p class="text-white/50 text-xs uppercase tracking-[0.3em] mb-3">Presented by</p>
                <p class="text-3xl font-display font-bold text-gradient-primary mb-2">LAZREK NASSIM</p>
                <p class="text-white/50">UM6P - Computer Science</p>
              </div>
            </Card3D>

            <div class="stagger-item mt-16 flex items-center justify-center gap-3 text-white/30 text-sm">
              <span>Press</span>
              <kbd class="px-3 py-1.5 rounded-lg bg-white/5 border border-white/10 font-mono text-xs">Space</kbd>
              <span>or</span>
              <kbd class="px-3 py-1.5 rounded-lg bg-white/5 border border-white/10 font-mono text-xs">→</kbd>
              <span>to continue</span>
            </div>
          </div>
        </div>

        <!-- SLIDE 1: Feasibility Study -->
        <div v-else-if="currentSlide === 1" :key="1" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="green" size="lg" :animate="true">
                <CheckCircle class="w-8 h-8 text-green-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Phase 1</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Feasibility Study</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">Comprehensive analysis ensuring project viability across multiple dimensions</p>

            <div class="grid-2-cols">
              <!-- Technical Feasibility -->
              <Card3D class="stagger-item" variant="glass">
                <div class="p-8">
                  <div class="flex items-center gap-4 mb-6">
                    <IconBadge color="primary" size="md">
                      <Code2 class="w-6 h-6 text-primary-400" />
                    </IconBadge>
                    <div>
                      <h3 class="text-xl font-semibold text-white">Technical Feasibility</h3>
                      <span class="text-green-400 text-sm font-bold uppercase tracking-wider">Approved</span>
                    </div>
                  </div>
                  <ul class="space-y-4">
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Modern tech stack with proven scalability</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Spring Boot + Vue.js ecosystem maturity</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>PostgreSQL for complex relational data</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>JWT authentication for secure access</span>
                    </li>
                  </ul>
                </div>
              </Card3D>

              <!-- Economic Feasibility -->
              <Card3D class="stagger-item" variant="glass">
                <div class="p-8">
                  <div class="flex items-center gap-4 mb-6">
                    <IconBadge color="yellow" size="md">
                      <CreditCard class="w-6 h-6 text-yellow-400" />
                    </IconBadge>
                    <div>
                      <h3 class="text-xl font-semibold text-white">Economic Feasibility</h3>
                      <span class="text-green-400 text-sm font-bold uppercase tracking-wider">Viable</span>
                    </div>
                  </div>
                  <ul class="space-y-4">
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Open-source technologies - zero licensing costs</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Reduces administrative overhead by 60%</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Scalable cloud deployment options</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Low maintenance requirements</span>
                    </li>
                  </ul>
                </div>
              </Card3D>

              <!-- Operational Feasibility -->
              <Card3D class="stagger-item" variant="glass">
                <div class="p-8">
                  <div class="flex items-center gap-4 mb-6">
                    <IconBadge color="blue" size="md">
                      <Users class="w-6 h-6 text-blue-400" />
                    </IconBadge>
                    <div>
                      <h3 class="text-xl font-semibold text-white">Operational Feasibility</h3>
                      <span class="text-green-400 text-sm font-bold uppercase tracking-wider">Feasible</span>
                    </div>
                  </div>
                  <ul class="space-y-4">
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Intuitive user interface design</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Role-based access for all user types</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Minimal training required</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>24/7 system availability</span>
                    </li>
                  </ul>
                </div>
              </Card3D>

              <!-- Schedule Feasibility -->
              <Card3D class="stagger-item" variant="glass">
                <div class="p-8">
                  <div class="flex items-center gap-4 mb-6">
                    <IconBadge color="purple" size="md">
                      <Calendar class="w-6 h-6 text-purple-400" />
                    </IconBadge>
                    <div>
                      <h3 class="text-xl font-semibold text-white">Schedule Feasibility</h3>
                      <span class="text-green-400 text-sm font-bold uppercase tracking-wider">On Track</span>
                    </div>
                  </div>
                  <ul class="space-y-4">
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Week 1-2: Requirements & Design</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Week 3-4: Backend Development</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Week 5: Frontend & Integration</span>
                    </li>
                    <li class="flex items-start gap-3 text-white/70">
                      <CheckCircle class="w-5 h-5 text-green-400 mt-0.5 flex-shrink-0" />
                      <span>Iterative development approach</span>
                    </li>
                  </ul>
                </div>
              </Card3D>
            </div>
          </div>
        </div>

        <!-- SLIDE 2: Requirements Analysis -->
        <div v-else-if="currentSlide === 2" :key="2" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="blue" size="lg" :animate="true">
                <FileText class="w-8 h-8 text-blue-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">SRS Document</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Requirements</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">Functional & non-functional specifications defining system behavior</p>

            <div class="grid-2-cols">
              <!-- Functional Requirements -->
              <div class="stagger-item">
                <h3 class="text-2xl font-semibold text-white mb-8 flex items-center gap-3">
                  <ClipboardList class="w-6 h-6 text-primary-400" />
                  Functional Requirements
                </h3>

                <div class="space-y-5">
                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <UserCheck class="w-5 h-5 text-green-400" />
                        <span class="font-semibold text-white">User Management</span>
                      </div>
                      <p class="text-white/50 text-sm">Authentication, authorization, role-based access control for students, faculty, and administrators</p>
                    </div>
                  </Card3D>

                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <BookOpen class="w-5 h-5 text-blue-400" />
                        <span class="font-semibold text-white">Course Management</span>
                      </div>
                      <p class="text-white/50 text-sm">Course creation, enrollment, scheduling, prerequisite validation, and capacity management</p>
                    </div>
                  </Card3D>

                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <Award class="w-5 h-5 text-yellow-400" />
                        <span class="font-semibold text-white">Grade Management</span>
                      </div>
                      <p class="text-white/50 text-sm">Grade entry, GPA calculation, transcript generation, and academic progress tracking</p>
                    </div>
                  </Card3D>

                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <CreditCard class="w-5 h-5 text-purple-400" />
                        <span class="font-semibold text-white">Payment Processing</span>
                      </div>
                      <p class="text-white/50 text-sm">Fee management, payment tracking, approval workflows, and financial reporting</p>
                    </div>
                  </Card3D>
                </div>
              </div>

              <!-- Non-Functional Requirements -->
              <div class="stagger-item">
                <h3 class="text-2xl font-semibold text-white mb-8 flex items-center gap-3">
                  <Settings class="w-6 h-6 text-primary-400" />
                  Non-Functional Requirements
                </h3>

                <div class="space-y-5">
                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <Zap class="w-5 h-5 text-yellow-400" />
                        <span class="font-semibold text-white">Performance</span>
                      </div>
                      <p class="text-white/50 text-sm">Response time &lt; 2 seconds, support for 1000+ concurrent users</p>
                    </div>
                  </Card3D>

                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <Shield class="w-5 h-5 text-red-400" />
                        <span class="font-semibold text-white">Security</span>
                      </div>
                      <p class="text-white/50 text-sm">JWT authentication, BCrypt encryption, HTTPS, SQL injection prevention</p>
                    </div>
                  </Card3D>

                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <Globe class="w-5 h-5 text-green-400" />
                        <span class="font-semibold text-white">Availability</span>
                      </div>
                      <p class="text-white/50 text-sm">99.9% uptime, graceful error handling, automatic recovery</p>
                    </div>
                  </Card3D>

                  <Card3D variant="glass">
                    <div class="p-6">
                      <div class="flex items-center gap-3 mb-3">
                        <Smartphone class="w-5 h-5 text-blue-400" />
                        <span class="font-semibold text-white">Usability</span>
                      </div>
                      <p class="text-white/50 text-sm">Responsive design, intuitive navigation, accessibility compliance</p>
                    </div>
                  </Card3D>
                </div>
              </div>
            </div>

            <!-- User Roles -->
            <div class="stagger-item mt-16">
              <h3 class="text-2xl font-semibold text-white mb-8 flex items-center gap-3">
                <Users class="w-6 h-6 text-primary-400" />
                User Roles & Actors
              </h3>
              <div class="grid-3-cols">
                <Card3D variant="gradient">
                  <div class="p-8 text-center">
                    <div style="width: 5rem; height: 5rem; border-radius: 1rem; background: linear-gradient(135deg, #3b82f6, #06b6d4); margin: 0 auto 1.25rem; display: flex; align-items: center; justify-content: center; box-shadow: 0 10px 25px rgba(59, 130, 246, 0.3);">
                      <GraduationCap class="w-10 h-10 text-white" />
                    </div>
                    <h4 class="text-xl font-semibold text-white mb-3">Student</h4>
                    <p class="text-white/50 text-sm">Course registration, grades view, payments, assignments, study groups</p>
                  </div>
                </Card3D>
                <Card3D variant="gradient">
                  <div class="p-8 text-center">
                    <div style="width: 5rem; height: 5rem; border-radius: 1rem; background: linear-gradient(135deg, #22c55e, #10b981); margin: 0 auto 1.25rem; display: flex; align-items: center; justify-content: center; box-shadow: 0 10px 25px rgba(34, 197, 94, 0.3);">
                      <BookOpen class="w-10 h-10 text-white" />
                    </div>
                    <h4 class="text-xl font-semibold text-white mb-3">Faculty</h4>
                    <p class="text-white/50 text-sm">Grade entry, attendance, course management, student interactions</p>
                  </div>
                </Card3D>
                <Card3D variant="gradient">
                  <div class="p-8 text-center">
                    <div style="width: 5rem; height: 5rem; border-radius: 1rem; background: linear-gradient(135deg, #a855f7, #ec4899); margin: 0 auto 1.25rem; display: flex; align-items: center; justify-content: center; box-shadow: 0 10px 25px rgba(168, 85, 247, 0.3);">
                      <Shield class="w-10 h-10 text-white" />
                    </div>
                    <h4 class="text-xl font-semibold text-white mb-3">Administrator</h4>
                    <p class="text-white/50 text-sm">User management, system config, analytics, payment approval</p>
                  </div>
                </Card3D>
              </div>
            </div>
          </div>
        </div>

        <!-- SLIDE 3: System Architecture -->
        <div v-else-if="currentSlide === 3" :key="3" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="purple" size="lg" :animate="true">
                <Layers class="w-8 h-8 text-purple-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Technical Design</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Architecture</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">3-Tier layered architecture with modern stack</p>

            <!-- Architecture Diagram -->
            <Card3D class="stagger-item mb-12" variant="glass">
              <div class="p-10">
                <div class="flex flex-col items-center gap-8">
                  <!-- Frontend Layer -->
                  <div class="arch-layer w-full max-w-3xl">
                    <div class="flex items-center justify-center gap-4 mb-4">
                      <Monitor class="w-7 h-7 text-primary-400" />
                      <span class="text-2xl font-semibold text-white">Presentation Layer</span>
                    </div>
                    <div class="flex flex-wrap justify-center gap-3">
                      <span class="tech-chip">Vue.js 3</span>
                      <span class="tech-chip">Tailwind CSS</span>
                      <span class="tech-chip">PrimeVue</span>
                      <span class="tech-chip">Pinia</span>
                    </div>
                  </div>

                  <div class="arch-connector">
                    <ChevronDown class="w-8 h-8" />
                    <div class="connector-line"></div>
                  </div>

                  <!-- Backend Layer -->
                  <div class="arch-layer arch-layer-highlight w-full max-w-3xl">
                    <div class="flex items-center justify-center gap-4 mb-4">
                      <Server class="w-7 h-7 text-green-400" />
                      <span class="text-2xl font-semibold text-white">Business Logic Layer</span>
                    </div>
                    <div class="flex flex-wrap justify-center gap-3">
                      <span class="tech-chip">Spring Boot 3.2</span>
                      <span class="tech-chip">Spring Security</span>
                      <span class="tech-chip">JWT Auth</span>
                      <span class="tech-chip">WebSocket</span>
                    </div>
                  </div>

                  <div class="arch-connector">
                    <ChevronDown class="w-8 h-8" />
                    <div class="connector-line"></div>
                  </div>

                  <!-- Database Layer -->
                  <div class="arch-layer w-full max-w-3xl">
                    <div class="flex items-center justify-center gap-4 mb-4">
                      <Database class="w-7 h-7 text-blue-400" />
                      <span class="text-2xl font-semibold text-white">Data Access Layer</span>
                    </div>
                    <div class="flex flex-wrap justify-center gap-3">
                      <span class="tech-chip">PostgreSQL</span>
                      <span class="tech-chip">Spring Data JPA</span>
                      <span class="tech-chip">Hibernate</span>
                      <span class="tech-chip">HikariCP</span>
                    </div>
                  </div>
                </div>
              </div>
            </Card3D>

            <!-- Key Architecture Decisions -->
            <div class="grid-3-cols">
              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Lock class="w-10 h-10 text-red-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Security First</h4>
                  <p class="text-white/50 text-sm">JWT tokens, BCrypt hashing, method-level security with @PreAuthorize annotations</p>
                </div>
              </Card3D>
              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Zap class="w-10 h-10 text-yellow-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Real-time Updates</h4>
                  <p class="text-white/50 text-sm">WebSocket integration for live notifications and messaging using STOMP protocol</p>
                </div>
              </Card3D>
              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <GitBranch class="w-10 h-10 text-green-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Clean Architecture</h4>
                  <p class="text-white/50 text-sm">Controller → Service → Repository pattern with DTO-based data transfer</p>
                </div>
              </Card3D>
            </div>
          </div>
        </div>

        <!-- SLIDE 4: API Design -->
        <div v-else-if="currentSlide === 4" :key="4" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="cyan" size="lg" :animate="true">
                <Terminal class="w-8 h-8 text-cyan-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Backend API</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">REST API Design</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">37+ endpoints organized by domain</p>

            <!-- Stats -->
            <div class="stagger-item grid grid-cols-2 md:grid-cols-4 gap-6 mb-14">
              <div class="text-center">
                <AnimatedCounter :value="37" suffix="+" class="!text-5xl" />
                <div class="text-white/40 uppercase tracking-wider text-xs mt-2">Endpoints</div>
              </div>
              <div class="text-center">
                <AnimatedCounter :value="11" :delay="0.2" class="!text-5xl" />
                <div class="text-white/40 uppercase tracking-wider text-xs mt-2">Controllers</div>
              </div>
              <div class="text-center">
                <AnimatedCounter :value="27" :delay="0.4" class="!text-5xl" />
                <div class="text-white/40 uppercase tracking-wider text-xs mt-2">Entities</div>
              </div>
              <div class="text-center">
                <div class="font-display text-5xl font-bold text-gradient-primary">REST</div>
                <div class="text-white/40 uppercase tracking-wider text-xs mt-2">Architecture</div>
              </div>
            </div>

            <!-- API Endpoints -->
            <div class="grid-2-cols">
              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <h4 class="text-lg font-semibold text-white mb-5 flex items-center gap-3">
                    <Lock class="w-5 h-5 text-red-400" />
                    Authentication
                  </h4>
                  <div class="code-block-modern">
                    <div class="code-line"><span class="method post">POST</span> <span class="endpoint">/api/auth/login</span></div>
                    <div class="code-line"><span class="method post">POST</span> <span class="endpoint">/api/auth/register</span></div>
                    <div class="code-line"><span class="method post">POST</span> <span class="endpoint">/api/auth/refresh</span></div>
                    <div class="code-line"><span class="method post">POST</span> <span class="endpoint">/api/auth/logout</span></div>
                  </div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <h4 class="text-lg font-semibold text-white mb-5 flex items-center gap-3">
                    <BookOpen class="w-5 h-5 text-blue-400" />
                    Courses
                  </h4>
                  <div class="code-block-modern">
                    <div class="code-line"><span class="method get">GET</span> <span class="endpoint">/api/courses</span></div>
                    <div class="code-line"><span class="method post">POST</span> <span class="endpoint">/api/courses</span></div>
                    <div class="code-line"><span class="method put">PUT</span> <span class="endpoint">/api/courses/{'{id}'}</span></div>
                    <div class="code-line"><span class="method delete">DEL</span> <span class="endpoint">/api/courses/{'{id}'}</span></div>
                  </div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <h4 class="text-lg font-semibold text-white mb-5 flex items-center gap-3">
                    <Award class="w-5 h-5 text-yellow-400" />
                    Grades
                  </h4>
                  <div class="code-block-modern">
                    <div class="code-line"><span class="method get">GET</span> <span class="endpoint">/api/grades/student/{'{id}'}</span></div>
                    <div class="code-line"><span class="method post">POST</span> <span class="endpoint">/api/grades</span></div>
                    <div class="code-line"><span class="method get">GET</span> <span class="endpoint">/api/grades/gpa/{'{studentId}'}</span></div>
                    <div class="code-line"><span class="method get">GET</span> <span class="endpoint">/api/grades/transcript</span></div>
                  </div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <h4 class="text-lg font-semibold text-white mb-5 flex items-center gap-3">
                    <CreditCard class="w-5 h-5 text-green-400" />
                    Payments
                  </h4>
                  <div class="code-block-modern">
                    <div class="code-line"><span class="method get">GET</span> <span class="endpoint">/api/payments</span></div>
                    <div class="code-line"><span class="method post">POST</span> <span class="endpoint">/api/payments</span></div>
                    <div class="code-line"><span class="method put">PUT</span> <span class="endpoint">/api/payments/{'{id}'}/approve</span></div>
                    <div class="code-line"><span class="method get">GET</span> <span class="endpoint">/api/fees/breakdown</span></div>
                  </div>
                </div>
              </Card3D>
            </div>

            <!-- JWT Flow -->
            <Card3D class="stagger-item mt-10" variant="glass">
              <div class="p-7">
                <h4 class="text-lg font-semibold text-white mb-6">JWT Authentication Flow</h4>
                <div class="flex flex-wrap items-center justify-center gap-4">
                  <div class="flow-step">Login Request</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Validate Credentials</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Generate JWT</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Return Token</div>
                </div>
              </div>
            </Card3D>
          </div>
        </div>

        <!-- SLIDE 5: Project Structure -->
        <div v-else-if="currentSlide === 5" :key="5" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="orange" size="lg" :animate="true">
                <Layout class="w-8 h-8 text-orange-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Code Organization</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Project Structure</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">Clean separation of concerns</p>

            <div class="grid-2-cols">
              <!-- Backend Structure -->
              <Card3D class="stagger-item" variant="glass">
                <div class="p-8">
                  <h3 class="text-xl font-semibold text-white mb-6 flex items-center gap-3">
                    <Server class="w-6 h-6 text-green-400" />
                    Backend (Java/Spring)
                  </h3>
                  <div class="code-tree">
                    <div class="tree-root">src/main/java/com/sams/</div>
                    <div class="tree-item"><span class="folder blue">controller/</span> <span class="comment">// REST endpoints</span></div>
                    <div class="tree-item"><span class="folder green">service/</span> <span class="comment">// Business logic</span></div>
                    <div class="tree-item"><span class="folder yellow">entity/</span> <span class="comment">// JPA models</span></div>
                    <div class="tree-item"><span class="folder purple">dto/</span> <span class="comment">// Data transfer</span></div>
                    <div class="tree-item"><span class="folder cyan">repository/</span> <span class="comment">// Data access</span></div>
                    <div class="tree-item"><span class="folder red">config/</span> <span class="comment">// Security, CORS</span></div>
                    <div class="tree-item tree-last"><span class="folder orange">utils/</span> <span class="comment">// Helpers</span></div>
                  </div>
                  <div class="mt-6 flex flex-wrap gap-2">
                    <span class="stat-chip">23 Controllers</span>
                    <span class="stat-chip">19 Services</span>
                    <span class="stat-chip">27 Entities</span>
                  </div>
                </div>
              </Card3D>

              <!-- Frontend Structure -->
              <Card3D class="stagger-item" variant="glass">
                <div class="p-8">
                  <h3 class="text-xl font-semibold text-white mb-6 flex items-center gap-3">
                    <Monitor class="w-6 h-6 text-primary-400" />
                    Frontend (Vue.js)
                  </h3>
                  <div class="code-tree">
                    <div class="tree-root">sams-frontend/src/</div>
                    <div class="tree-item"><span class="folder blue">views/</span> <span class="comment">// Page components</span></div>
                    <div class="tree-item sub">├── student/</div>
                    <div class="tree-item sub">├── faculty/</div>
                    <div class="tree-item sub">└── admin/</div>
                    <div class="tree-item"><span class="folder green">components/</span> <span class="comment">// Reusable UI</span></div>
                    <div class="tree-item"><span class="folder yellow">services/</span> <span class="comment">// API calls</span></div>
                    <div class="tree-item"><span class="folder purple">stores/</span> <span class="comment">// Pinia state</span></div>
                    <div class="tree-item tree-last"><span class="folder cyan">router/</span> <span class="comment">// Navigation</span></div>
                  </div>
                  <div class="mt-6 flex flex-wrap gap-2">
                    <span class="stat-chip">50+ Components</span>
                    <span class="stat-chip">Role-based Views</span>
                  </div>
                </div>
              </Card3D>
            </div>

            <!-- Database Schema -->
            <Card3D class="stagger-item mt-10" variant="glass">
              <div class="p-8">
                <h3 class="text-xl font-semibold text-white mb-6 flex items-center gap-3">
                  <Database class="w-6 h-6 text-blue-400" />
                  Key Database Entities
                </h3>
                <div class="flex flex-wrap gap-3">
                  <span class="entity-chip">User</span>
                  <span class="entity-chip">Course</span>
                  <span class="entity-chip">Enrollment</span>
                  <span class="entity-chip">Grade</span>
                  <span class="entity-chip">Payment</span>
                  <span class="entity-chip">Assignment</span>
                  <span class="entity-chip">Submission</span>
                  <span class="entity-chip">Attendance</span>
                  <span class="entity-chip">Notification</span>
                  <span class="entity-chip">StudyGroup</span>
                  <span class="entity-chip">Connection</span>
                  <span class="entity-chip">PrivateMessage</span>
                </div>
              </div>
            </Card3D>
          </div>
        </div>

        <!-- SLIDE 6: Student Features -->
        <div v-else-if="currentSlide === 6" :key="6" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="blue" size="lg" :animate="true" :pulse="true">
                <GraduationCap class="w-8 h-8 text-blue-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Portal Features</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Student Portal</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">Complete academic management for students</p>

            <div class="grid-3-cols">
              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <BookOpen class="w-10 h-10 text-blue-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Course Registration</h4>
                  <p class="text-white/50 text-sm mb-4">Browse available courses, check prerequisites, register with conflict detection</p>
                  <div class="text-xs text-primary-400 font-semibold">12+ API endpoints</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <BarChart3 class="w-10 h-10 text-green-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">GPA Tracking</h4>
                  <p class="text-white/50 text-sm mb-4">Real-time GPA calculation, semester breakdown, transcript generation</p>
                  <div class="text-xs text-primary-400 font-semibold">Automated calculation</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <CreditCard class="w-10 h-10 text-yellow-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Fee Management</h4>
                  <p class="text-white/50 text-sm mb-4">View fee breakdown, submit payments, track approval status</p>
                  <div class="text-xs text-primary-400 font-semibold">Payment workflows</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <FileText class="w-10 h-10 text-purple-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Assignments</h4>
                  <p class="text-white/50 text-sm mb-4">View assignments, submit work, track submission history</p>
                  <div class="text-xs text-primary-400 font-semibold">File upload support</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Users class="w-10 h-10 text-pink-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Study Groups</h4>
                  <p class="text-white/50 text-sm mb-4">Create/join groups, group messaging, collaborative learning</p>
                  <div class="text-xs text-primary-400 font-semibold">Real-time chat</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Calendar class="w-10 h-10 text-orange-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Schedule View</h4>
                  <p class="text-white/50 text-sm mb-4">Weekly schedule, class timings, exam calendar</p>
                  <div class="text-xs text-primary-400 font-semibold">Visual timetable</div>
                </div>
              </Card3D>
            </div>

            <!-- Student Workflow -->
            <Card3D class="stagger-item mt-12" variant="glass">
              <div class="p-7">
                <h4 class="text-lg font-semibold text-white mb-8">Student Workflow</h4>
                <div class="flex flex-wrap items-center justify-center gap-4">
                  <div class="flow-step">Login</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Dashboard</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Browse Courses</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Register</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Pay Fees</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Attend Classes</div>
                </div>
              </div>
            </Card3D>
          </div>
        </div>

        <!-- SLIDE 7: Faculty Features -->
        <div v-else-if="currentSlide === 7" :key="7" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="green" size="lg" :animate="true" :pulse="true">
                <BookOpen class="w-8 h-8 text-green-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Portal Features</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Faculty Portal</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">Tools for instructors and professors</p>

            <div class="grid-3-cols">
              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Award class="w-10 h-10 text-yellow-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Grade Management</h4>
                  <p class="text-white/50 text-sm mb-4">Enter grades, calculate averages, view grade distributions</p>
                  <div class="text-xs text-primary-400 font-semibold">Batch entry support</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <ClipboardList class="w-10 h-10 text-blue-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Attendance Tracking</h4>
                  <p class="text-white/50 text-sm mb-4">Mark attendance, view statistics, generate reports</p>
                  <div class="text-xs text-primary-400 font-semibold">QR code support</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <FileText class="w-10 h-10 text-purple-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Assignment Creation</h4>
                  <p class="text-white/50 text-sm mb-4">Create assignments, set deadlines, review submissions</p>
                  <div class="text-xs text-primary-400 font-semibold">File attachments</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Users class="w-10 h-10 text-green-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Course Roster</h4>
                  <p class="text-white/50 text-sm mb-4">View enrolled students, contact information, performance</p>
                  <div class="text-xs text-primary-400 font-semibold">Export capabilities</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <MessageSquare class="w-10 h-10 text-cyan-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Student Interaction</h4>
                  <p class="text-white/50 text-sm mb-4">Direct messaging, announcements, office hours</p>
                  <div class="text-xs text-primary-400 font-semibold">Real-time messaging</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <BarChart3 class="w-10 h-10 text-orange-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Analytics</h4>
                  <p class="text-white/50 text-sm mb-4">Course statistics, performance trends, attendance rates</p>
                  <div class="text-xs text-primary-400 font-semibold">Visual charts</div>
                </div>
              </Card3D>
            </div>

            <!-- Faculty Workflow -->
            <Card3D class="stagger-item mt-12" variant="glass">
              <div class="p-7">
                <h4 class="text-lg font-semibold text-white mb-8">Faculty Workflow</h4>
                <div class="flex flex-wrap items-center justify-center gap-4">
                  <div class="flow-step">Login</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">View Courses</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Mark Attendance</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Create Assignments</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Grade Students</div>
                </div>
              </div>
            </Card3D>
          </div>
        </div>

        <!-- SLIDE 8: Admin Features -->
        <div v-else-if="currentSlide === 8" :key="8" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="purple" size="lg" :animate="true" :pulse="true">
                <Shield class="w-8 h-8 text-purple-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Portal Features</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Admin Portal</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">System administration and oversight</p>

            <div class="grid-3-cols">
              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Users class="w-10 h-10 text-blue-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">User Management</h4>
                  <p class="text-white/50 text-sm mb-4">Create accounts, assign roles, manage permissions</p>
                  <div class="text-xs text-primary-400 font-semibold">RBAC system</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <BookOpen class="w-10 h-10 text-green-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Course Administration</h4>
                  <p class="text-white/50 text-sm mb-4">Create courses, assign faculty, set schedules</p>
                  <div class="text-xs text-primary-400 font-semibold">Full CRUD</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <CreditCard class="w-10 h-10 text-yellow-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Payment Approval</h4>
                  <p class="text-white/50 text-sm mb-4">Review payments, approve/reject, generate receipts</p>
                  <div class="text-xs text-primary-400 font-semibold">Workflow automation</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <BarChart3 class="w-10 h-10 text-purple-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Analytics Dashboard</h4>
                  <p class="text-white/50 text-sm mb-4">Enrollment trends, financial summaries, system health</p>
                  <div class="text-xs text-primary-400 font-semibold">Real-time metrics</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <Settings class="w-10 h-10 text-cyan-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Fee Structure</h4>
                  <p class="text-white/50 text-sm mb-4">Configure fees, semester charges, scholarships</p>
                  <div class="text-xs text-primary-400 font-semibold">Flexible pricing</div>
                </div>
              </Card3D>

              <Card3D class="stagger-item" variant="glass">
                <div class="p-7">
                  <FileText class="w-10 h-10 text-red-400 mb-5" />
                  <h4 class="text-lg font-semibold text-white mb-3">Report Generation</h4>
                  <p class="text-white/50 text-sm mb-4">Generate reports, export data, audit logs</p>
                  <div class="text-xs text-primary-400 font-semibold">PDF/Excel export</div>
                </div>
              </Card3D>
            </div>

            <!-- Admin Workflow -->
            <Card3D class="stagger-item mt-12" variant="glass">
              <div class="p-7">
                <h4 class="text-lg font-semibold text-white mb-8">Admin Workflow</h4>
                <div class="flex flex-wrap items-center justify-center gap-4">
                  <div class="flow-step">Login</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Dashboard</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Manage Users</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Approve Payments</div>
                  <ArrowRight class="w-5 h-5 text-primary-400" />
                  <div class="flow-step">Generate Reports</div>
                </div>
              </div>
            </Card3D>
          </div>
        </div>

        <!-- SLIDE 9: Technologies -->
        <div v-else-if="currentSlide === 9" :key="9" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="yellow" size="lg" :animate="true">
                <Cpu class="w-8 h-8 text-yellow-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Tech Stack</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Technologies</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">Modern, scalable, and production-ready</p>

            <div class="grid-2-cols">
              <!-- Frontend -->
              <Card3D class="stagger-item" variant="gradient">
                <div class="p-8">
                  <h3 class="text-2xl font-semibold text-white mb-8 flex items-center gap-3">
                    <Monitor class="w-7 h-7 text-primary-400" />
                    Frontend
                  </h3>
                  <div class="space-y-5">
                    <div class="tech-item">
                      <div class="tech-icon bg-emerald-500/20">
                        <span class="text-xl">🍃</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">Vue.js 3.5</div>
                        <div class="text-sm text-white/40">Composition API, Reactivity</div>
                      </div>
                    </div>
                    <div class="tech-item">
                      <div class="tech-icon bg-cyan-500/20">
                        <span class="text-xl">💨</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">Tailwind CSS 4</div>
                        <div class="text-sm text-white/40">Utility-first styling</div>
                      </div>
                    </div>
                    <div class="tech-item">
                      <div class="tech-icon bg-yellow-500/20">
                        <span class="text-xl">🍍</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">Pinia</div>
                        <div class="text-sm text-white/40">State management</div>
                      </div>
                    </div>
                    <div class="tech-item">
                      <div class="tech-icon bg-purple-500/20">
                        <span class="text-xl">⚡</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">Vite</div>
                        <div class="text-sm text-white/40">Lightning-fast build tool</div>
                      </div>
                    </div>
                  </div>
                </div>
              </Card3D>

              <!-- Backend -->
              <Card3D class="stagger-item" variant="gradient">
                <div class="p-8">
                  <h3 class="text-2xl font-semibold text-white mb-8 flex items-center gap-3">
                    <Server class="w-7 h-7 text-green-400" />
                    Backend
                  </h3>
                  <div class="space-y-5">
                    <div class="tech-item">
                      <div class="tech-icon bg-green-500/20">
                        <span class="text-xl">🌱</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">Spring Boot 3.2</div>
                        <div class="text-sm text-white/40">Java 17 LTS</div>
                      </div>
                    </div>
                    <div class="tech-item">
                      <div class="tech-icon bg-red-500/20">
                        <span class="text-xl">🔐</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">Spring Security 6</div>
                        <div class="text-sm text-white/40">JWT Authentication</div>
                      </div>
                    </div>
                    <div class="tech-item">
                      <div class="tech-icon bg-blue-500/20">
                        <span class="text-xl">🐘</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">PostgreSQL 15</div>
                        <div class="text-sm text-white/40">Advanced relational DB</div>
                      </div>
                    </div>
                    <div class="tech-item">
                      <div class="tech-icon bg-orange-500/20">
                        <span class="text-xl">🔄</span>
                      </div>
                      <div>
                        <div class="font-semibold text-white">WebSocket/STOMP</div>
                        <div class="text-sm text-white/40">Real-time communication</div>
                      </div>
                    </div>
                  </div>
                </div>
              </Card3D>
            </div>

            <!-- Additional Tools -->
            <Card3D class="stagger-item mt-10" variant="glass">
              <div class="p-8">
                <h4 class="text-lg font-semibold text-white mb-6">Additional Libraries & Tools</h4>
                <div class="flex flex-wrap gap-3">
                  <span class="tech-chip">Chart.js</span>
                  <span class="tech-chip">GSAP</span>
                  <span class="tech-chip">Axios</span>
                  <span class="tech-chip">PrimeVue</span>
                  <span class="tech-chip">Lombok</span>
                  <span class="tech-chip">Hibernate</span>
                  <span class="tech-chip">Maven</span>
                  <span class="tech-chip">Lucide Icons</span>
                </div>
              </div>
            </Card3D>
          </div>
        </div>

        <!-- SLIDE 10: Challenges -->
        <div v-else-if="currentSlide === 10" :key="10" class="slide py-16 overflow-y-auto">
          <div class="max-w-7xl mx-auto px-4">
            <div class="stagger-item flex items-center gap-4 mb-6">
              <IconBadge color="red" size="lg" :animate="true">
                <AlertTriangle class="w-8 h-8 text-red-400" />
              </IconBadge>
              <div>
                <span class="text-white/40 uppercase tracking-[0.2em] text-xs font-semibold">Lessons Learned</span>
                <h2 class="font-display text-5xl md:text-6xl font-bold text-white">Challenges</h2>
              </div>
            </div>

            <p class="stagger-item text-xl text-white/50 mb-14 max-w-2xl">Problems encountered and how we solved them</p>

            <div class="grid-2-cols">
              <!-- Technical Challenges -->
              <div class="stagger-item space-y-5">
                <h3 class="text-xl font-semibold text-white mb-6 flex items-center gap-2">
                  <Bug class="w-5 h-5 text-red-400" />
                  Technical Challenges
                </h3>

                <Card3D variant="glass">
                  <div class="p-6">
                    <div class="flex items-start gap-3">
                      <AlertTriangle class="w-5 h-5 text-yellow-400 mt-1 flex-shrink-0" />
                      <div>
                        <h4 class="font-semibold text-white mb-2">JWT Token Refresh</h4>
                        <p class="text-white/50 text-sm mb-3">Managing token expiration and seamless refresh</p>
                        <div class="flex items-center gap-2 text-green-400 text-sm">
                          <Lightbulb class="w-4 h-4" />
                          <span>Implemented interceptors with automatic retry</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </Card3D>

                <Card3D variant="glass">
                  <div class="p-6">
                    <div class="flex items-start gap-3">
                      <AlertTriangle class="w-5 h-5 text-yellow-400 mt-1 flex-shrink-0" />
                      <div>
                        <h4 class="font-semibold text-white mb-2">WebSocket Authentication</h4>
                        <p class="text-white/50 text-sm mb-3">Securing real-time connections with JWT</p>
                        <div class="flex items-center gap-2 text-green-400 text-sm">
                          <Lightbulb class="w-4 h-4" />
                          <span>Custom handshake interceptor for token validation</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </Card3D>

                <Card3D variant="glass">
                  <div class="p-6">
                    <div class="flex items-start gap-3">
                      <AlertTriangle class="w-5 h-5 text-yellow-400 mt-1 flex-shrink-0" />
                      <div>
                        <h4 class="font-semibold text-white mb-2">Complex JPA Relationships</h4>
                        <p class="text-white/50 text-sm mb-3">N+1 query problems and lazy loading issues</p>
                        <div class="flex items-center gap-2 text-green-400 text-sm">
                          <Lightbulb class="w-4 h-4" />
                          <span>DTOs and @EntityGraph for optimized queries</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </Card3D>
              </div>

              <!-- Design Challenges -->
              <div class="stagger-item space-y-5">
                <h3 class="text-xl font-semibold text-white mb-6 flex items-center gap-2">
                  <Palette class="w-5 h-5 text-blue-400" />
                  Design Challenges
                </h3>

                <Card3D variant="glass">
                  <div class="p-6">
                    <div class="flex items-start gap-3">
                      <AlertTriangle class="w-5 h-5 text-yellow-400 mt-1 flex-shrink-0" />
                      <div>
                        <h4 class="font-semibold text-white mb-2">Responsive Dashboard</h4>
                        <p class="text-white/50 text-sm mb-3">Complex layouts breaking on mobile devices</p>
                        <div class="flex items-center gap-2 text-green-400 text-sm">
                          <Lightbulb class="w-4 h-4" />
                          <span>Mobile-first approach with Tailwind breakpoints</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </Card3D>

                <Card3D variant="glass">
                  <div class="p-6">
                    <div class="flex items-start gap-3">
                      <AlertTriangle class="w-5 h-5 text-yellow-400 mt-1 flex-shrink-0" />
                      <div>
                        <h4 class="font-semibold text-white mb-2">Dark Mode Consistency</h4>
                        <p class="text-white/50 text-sm mb-3">Maintaining theme across all components</p>
                        <div class="flex items-center gap-2 text-green-400 text-sm">
                          <Lightbulb class="w-4 h-4" />
                          <span>CSS variables and Tailwind dark: variant</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </Card3D>

                <Card3D variant="glass">
                  <div class="p-6">
                    <div class="flex items-start gap-3">
                      <AlertTriangle class="w-5 h-5 text-yellow-400 mt-1 flex-shrink-0" />
                      <div>
                        <h4 class="font-semibold text-white mb-2">Animation Performance</h4>
                        <p class="text-white/50 text-sm mb-3">Smooth animations without janky scrolling</p>
                        <div class="flex items-center gap-2 text-green-400 text-sm">
                          <Lightbulb class="w-4 h-4" />
                          <span>GPU-accelerated transforms, GSAP optimization</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </Card3D>
              </div>
            </div>
          </div>
        </div>

        <!-- SLIDE 11: Conclusion -->
        <div v-else-if="currentSlide === 11" :key="11" class="slide flex items-center justify-center">
          <div class="text-center max-w-5xl">
            <div class="stagger-item inline-flex items-center gap-3 px-6 py-3 rounded-full glass-subtle mb-10">
              <Rocket class="w-5 h-5 text-primary-400" />
              <span class="text-white/80 text-sm font-semibold tracking-widest uppercase">Project Complete</span>
            </div>

            <div class="stagger-item mb-8">
              <GlowingText
                text="Thank You!"
                tag="h2"
                variant="gradient"
                class="font-display text-6xl md:text-8xl font-bold"
              />
            </div>

            <p class="stagger-item text-xl text-white/60 mb-16 max-w-2xl mx-auto">
              SAMS demonstrates enterprise-grade software engineering with modern technologies and clean architecture
            </p>

            <!-- Final Stats -->
            <div class="stagger-item grid grid-cols-2 md:grid-cols-4 gap-6 mb-16">
              <Card3D variant="glass">
                <div class="p-6 text-center">
                  <AnimatedCounter :value="37" suffix="+" class="!text-4xl" />
                  <div class="text-white/40 uppercase tracking-wider text-xs mt-2">Endpoints</div>
                </div>
              </Card3D>
              <Card3D variant="glass">
                <div class="p-6 text-center">
                  <AnimatedCounter :value="50" suffix="+" :delay="0.1" class="!text-4xl" />
                  <div class="text-white/40 uppercase tracking-wider text-xs mt-2">Components</div>
                </div>
              </Card3D>
              <Card3D variant="glass">
                <div class="p-6 text-center">
                  <AnimatedCounter :value="27" :delay="0.2" class="!text-4xl" />
                  <div class="text-white/40 uppercase tracking-wider text-xs mt-2">DB Entities</div>
                </div>
              </Card3D>
              <Card3D variant="glass">
                <div class="p-6 text-center">
                  <div class="font-display text-4xl font-bold text-gradient-primary">100%</div>
                  <div class="text-white/40 uppercase tracking-wider text-xs mt-2">Complete</div>
                </div>
              </Card3D>
            </div>

            <!-- Key Achievements -->
            <Card3D class="stagger-item mb-16" variant="glass">
              <div class="p-8">
                <h4 class="text-lg font-semibold text-white mb-6">Key Achievements</h4>
                <div class="grid md:grid-cols-2 gap-4 text-left">
                  <div class="flex items-center gap-3">
                    <CheckCircle class="w-5 h-5 text-green-400 flex-shrink-0" />
                    <span class="text-white/60">Full-stack implementation with modern tech</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <CheckCircle class="w-5 h-5 text-green-400 flex-shrink-0" />
                    <span class="text-white/60">Secure JWT authentication system</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <CheckCircle class="w-5 h-5 text-green-400 flex-shrink-0" />
                    <span class="text-white/60">Real-time features with WebSocket</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <CheckCircle class="w-5 h-5 text-green-400 flex-shrink-0" />
                    <span class="text-white/60">Beautiful, responsive UI design</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <CheckCircle class="w-5 h-5 text-green-400 flex-shrink-0" />
                    <span class="text-white/60">Multi-role access control</span>
                  </div>
                  <div class="flex items-center gap-3">
                    <CheckCircle class="w-5 h-5 text-green-400 flex-shrink-0" />
                    <span class="text-white/60">Comprehensive documentation</span>
                  </div>
                </div>
              </div>
            </Card3D>

            <!-- Author Info -->
            <div class="stagger-item flex items-center justify-center gap-6 mb-10">
              <div class="w-20 h-20 rounded-2xl bg-gradient-to-br from-primary-500 to-purple-600 flex items-center justify-center text-3xl font-bold text-white shadow-lg shadow-primary-500/30">
                NL
              </div>
              <div class="text-left">
                <p class="text-2xl font-display font-bold text-white">LAZREK NASSIM</p>
                <p class="text-white/40">UM6P - Computer Science</p>
              </div>
            </div>

            <div class="stagger-item flex items-center justify-center gap-2 text-white/30 text-sm">
              <Heart class="w-4 h-4 text-red-400" />
              <span>Built with passion using Vue.js & Spring Boot</span>
            </div>
          </div>
        </div>
      </Transition>
    </div>

    <!-- Keyboard Hint -->
    <div class="fixed bottom-8 right-8 z-50 text-white/20 text-xs flex items-center gap-4">
      <span class="flex items-center gap-1">
        <kbd class="px-2 py-1 rounded-lg bg-white/5 border border-white/10 font-mono text-[10px]">←</kbd>
        <kbd class="px-2 py-1 rounded-lg bg-white/5 border border-white/10 font-mono text-[10px]">→</kbd>
        Navigate
      </span>
      <span class="flex items-center gap-1">
        <kbd class="px-2 py-1 rounded-lg bg-white/5 border border-white/10 font-mono text-[10px]">Space</kbd>
        Next
      </span>
    </div>
  </div>
</template>

<style scoped>
.slide {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  overflow-y: auto;
  z-index: 10;
  padding-left: 2.5rem;
  padding-right: 5rem;
}

.slide::-webkit-scrollbar {
  width: 6px;
}

.slide::-webkit-scrollbar-track {
  background: transparent;
}

.slide::-webkit-scrollbar-thumb {
  background: rgba(99, 102, 241, 0.3);
  border-radius: 3px;
}

/* Slide transition */
.slide-morph-enter-active,
.slide-morph-leave-active {
  transition: all 0.8s cubic-bezier(0.16, 1, 0.3, 1);
}

.slide-morph-enter-from {
  opacity: 0;
  transform: translateX(80px) scale(0.96);
  filter: blur(10px);
}

.slide-morph-leave-to {
  opacity: 0;
  transform: translateX(-80px) scale(0.96);
  filter: blur(10px);
}
</style>
