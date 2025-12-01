import { gsap } from 'gsap'
import { ref, onMounted, onUnmounted } from 'vue'

/**
 * Composable for GSAP-powered animations in the Aurora theme
 */
export function useAnimations() {
  // Animation registry for cleanup
  const animations = ref([])

  /**
   * Page entrance animation with 3D perspective
   */
  const pageEnter = (element, options = {}) => {
    const defaults = {
      duration: 0.6,
      ease: 'power3.out',
      delay: 0
    }
    const config = { ...defaults, ...options }

    const anim = gsap.fromTo(element,
      {
        opacity: 0,
        y: 30,
        rotateX: -10,
        filter: 'blur(10px)',
        transformPerspective: 1000
      },
      {
        opacity: 1,
        y: 0,
        rotateX: 0,
        filter: 'blur(0px)',
        duration: config.duration,
        ease: config.ease,
        delay: config.delay
      }
    )
    animations.value.push(anim)
    return anim
  }

  /**
   * Page exit animation
   */
  const pageExit = (element, options = {}) => {
    const defaults = {
      duration: 0.4,
      ease: 'power2.in'
    }
    const config = { ...defaults, ...options }

    return gsap.to(element, {
      opacity: 0,
      y: -20,
      scale: 0.95,
      duration: config.duration,
      ease: config.ease
    })
  }

  /**
   * Staggered list animation
   */
  const staggerIn = (elements, options = {}) => {
    const defaults = {
      duration: 0.4,
      stagger: 0.08,
      ease: 'power2.out',
      delay: 0
    }
    const config = { ...defaults, ...options }

    const anim = gsap.fromTo(elements,
      {
        opacity: 0,
        y: 20,
        scale: 0.95
      },
      {
        opacity: 1,
        y: 0,
        scale: 1,
        duration: config.duration,
        stagger: config.stagger,
        ease: config.ease,
        delay: config.delay
      }
    )
    animations.value.push(anim)
    return anim
  }

  /**
   * Fade in animation
   */
  const fadeIn = (element, options = {}) => {
    const defaults = {
      duration: 0.3,
      ease: 'power2.out',
      delay: 0
    }
    const config = { ...defaults, ...options }

    const anim = gsap.fromTo(element,
      { opacity: 0 },
      {
        opacity: 1,
        duration: config.duration,
        ease: config.ease,
        delay: config.delay
      }
    )
    animations.value.push(anim)
    return anim
  }

  /**
   * Slide in from direction
   */
  const slideIn = (element, direction = 'up', options = {}) => {
    const defaults = {
      duration: 0.5,
      ease: 'power3.out',
      distance: 30,
      delay: 0
    }
    const config = { ...defaults, ...options }

    const directions = {
      up: { y: config.distance, x: 0 },
      down: { y: -config.distance, x: 0 },
      left: { x: config.distance, y: 0 },
      right: { x: -config.distance, y: 0 }
    }

    const from = directions[direction] || directions.up

    const anim = gsap.fromTo(element,
      { opacity: 0, ...from },
      {
        opacity: 1,
        x: 0,
        y: 0,
        duration: config.duration,
        ease: config.ease,
        delay: config.delay
      }
    )
    animations.value.push(anim)
    return anim
  }

  /**
   * Scale in animation
   */
  const scaleIn = (element, options = {}) => {
    const defaults = {
      duration: 0.4,
      ease: 'back.out(1.7)',
      delay: 0
    }
    const config = { ...defaults, ...options }

    const anim = gsap.fromTo(element,
      { opacity: 0, scale: 0.8 },
      {
        opacity: 1,
        scale: 1,
        duration: config.duration,
        ease: config.ease,
        delay: config.delay
      }
    )
    animations.value.push(anim)
    return anim
  }

  /**
   * 3D card hover effect
   */
  const card3DHover = (element, event) => {
    const rect = element.getBoundingClientRect()
    const x = event.clientX - rect.left
    const y = event.clientY - rect.top
    const centerX = rect.width / 2
    const centerY = rect.height / 2

    const rotateX = (y - centerY) / 15
    const rotateY = (centerX - x) / 15

    gsap.to(element, {
      rotateX,
      rotateY,
      transformPerspective: 1000,
      duration: 0.3,
      ease: 'power2.out'
    })
  }

  /**
   * Reset 3D card hover
   */
  const card3DReset = (element) => {
    gsap.to(element, {
      rotateX: 0,
      rotateY: 0,
      duration: 0.5,
      ease: 'power3.out'
    })
  }

  /**
   * Glow pulse animation
   */
  const glowPulse = (element, color = 'rgba(0, 212, 255, 0.5)', options = {}) => {
    const defaults = {
      duration: 2,
      repeat: -1,
      yoyo: true
    }
    const config = { ...defaults, ...options }

    const anim = gsap.to(element, {
      boxShadow: `0 0 30px ${color}, 0 0 60px ${color}`,
      duration: config.duration,
      repeat: config.repeat,
      yoyo: config.yoyo,
      ease: 'sine.inOut'
    })
    animations.value.push(anim)
    return anim
  }

  /**
   * Counter animation for numbers
   */
  const animateCounter = (element, endValue, options = {}) => {
    const defaults = {
      duration: 2,
      ease: 'power2.out',
      startValue: 0,
      decimals: 0,
      prefix: '',
      suffix: ''
    }
    const config = { ...defaults, ...options }

    const counter = { value: config.startValue }

    const anim = gsap.to(counter, {
      value: endValue,
      duration: config.duration,
      ease: config.ease,
      onUpdate: () => {
        const formatted = counter.value.toFixed(config.decimals)
        element.textContent = `${config.prefix}${formatted}${config.suffix}`
      }
    })
    animations.value.push(anim)
    return anim
  }

  /**
   * Ripple effect on click
   */
  const createRipple = (event, element) => {
    const rect = element.getBoundingClientRect()
    const ripple = document.createElement('span')
    const diameter = Math.max(rect.width, rect.height)
    const radius = diameter / 2

    ripple.style.cssText = `
      position: absolute;
      width: ${diameter}px;
      height: ${diameter}px;
      left: ${event.clientX - rect.left - radius}px;
      top: ${event.clientY - rect.top - radius}px;
      background: rgba(255, 255, 255, 0.3);
      border-radius: 50%;
      transform: scale(0);
      pointer-events: none;
    `

    element.style.position = 'relative'
    element.style.overflow = 'hidden'
    element.appendChild(ripple)

    gsap.to(ripple, {
      scale: 4,
      opacity: 0,
      duration: 0.6,
      ease: 'power2.out',
      onComplete: () => ripple.remove()
    })
  }

  /**
   * Shake animation (for errors)
   */
  const shake = (element, options = {}) => {
    const defaults = {
      intensity: 10,
      duration: 0.4
    }
    const config = { ...defaults, ...options }

    return gsap.to(element, {
      x: [0, -config.intensity, config.intensity, -config.intensity, config.intensity, 0],
      duration: config.duration,
      ease: 'power2.out'
    })
  }

  /**
   * Bounce animation
   */
  const bounce = (element, options = {}) => {
    const defaults = {
      height: 20,
      duration: 0.6
    }
    const config = { ...defaults, ...options }

    return gsap.to(element, {
      y: [-config.height, 0],
      duration: config.duration,
      ease: 'bounce.out'
    })
  }

  /**
   * Morph/Blob animation for backgrounds
   */
  const morphBlob = (element, options = {}) => {
    const defaults = {
      duration: 10,
      repeat: -1
    }
    const config = { ...defaults, ...options }

    const anim = gsap.to(element, {
      borderRadius: '40% 60% 70% 30% / 40% 50% 60% 50%',
      duration: config.duration / 4,
      repeat: config.repeat,
      yoyo: true,
      ease: 'sine.inOut',
      keyframes: [
        { borderRadius: '40% 60% 70% 30% / 40% 50% 60% 50%' },
        { borderRadius: '70% 30% 50% 50% / 30% 30% 70% 70%' },
        { borderRadius: '30% 60% 70% 40% / 50% 60% 30% 60%' },
        { borderRadius: '50% 50% 30% 70% / 70% 40% 60% 40%' }
      ]
    })
    animations.value.push(anim)
    return anim
  }

  /**
   * Text reveal animation (typewriter style)
   */
  const revealText = (element, options = {}) => {
    const defaults = {
      duration: 0.8,
      stagger: 0.02
    }
    const config = { ...defaults, ...options }

    // Split text into spans
    const text = element.textContent
    element.innerHTML = text
      .split('')
      .map(char => `<span style="opacity: 0">${char === ' ' ? '&nbsp;' : char}</span>`)
      .join('')

    const chars = element.querySelectorAll('span')

    const anim = gsap.to(chars, {
      opacity: 1,
      duration: config.duration,
      stagger: config.stagger,
      ease: 'power2.out'
    })
    animations.value.push(anim)
    return anim
  }

  /**
   * Floating animation
   */
  const float = (element, options = {}) => {
    const defaults = {
      y: 10,
      duration: 3,
      repeat: -1
    }
    const config = { ...defaults, ...options }

    const anim = gsap.to(element, {
      y: -config.y,
      duration: config.duration,
      repeat: config.repeat,
      yoyo: true,
      ease: 'sine.inOut'
    })
    animations.value.push(anim)
    return anim
  }

  /**
   * Kill all registered animations
   */
  const killAll = () => {
    animations.value.forEach(anim => {
      if (anim && anim.kill) {
        anim.kill()
      }
    })
    animations.value = []
  }

  // Cleanup on unmount
  onUnmounted(() => {
    killAll()
  })

  return {
    // Page animations
    pageEnter,
    pageExit,

    // Element animations
    staggerIn,
    fadeIn,
    slideIn,
    scaleIn,

    // Interactive animations
    card3DHover,
    card3DReset,
    createRipple,

    // Effect animations
    glowPulse,
    animateCounter,
    shake,
    bounce,
    morphBlob,
    float,

    // Text animations
    revealText,

    // Utility
    killAll
  }
}

/**
 * Simple hook for on-mount animation
 */
export function useOnMountAnimation(animationFn, elementRef, options = {}) {
  onMounted(() => {
    if (elementRef.value) {
      animationFn(elementRef.value, options)
    }
  })
}
