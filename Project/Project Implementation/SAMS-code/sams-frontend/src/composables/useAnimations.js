/*
 * useAnimations.js - GSAP Animation Composable
 *
 * provides reusable animation functions powered by GSAP
 * includes page transitions, 3D effects, counters, and more
 * took forever to get all these animations smooth - definately worth it tho
 */

import { gsap } from 'gsap'
import { ref, onMounted, onUnmounted } from 'vue'

/*
 * composable for GSAP-powered animations
 * returns all animation functions and handles cleanup automatically
 */
export function useAnimations() {
  // animation registry for cleanup - prevents memory leaks
  const animations = ref([])

  /*
   * page entrance animation with 3D perspective
   * creates nice fade + slide + blur effect when page loads
   */
  const pageEnter = (element, options = {}) => {
    // default config - can be overridden with options param
    const defaults = {
      duration: 0.6,
      ease: 'power3.out',
      delay: 0
    }
    const config = { ...defaults, ...options }

    // animate from blurred and offset to normal state
    const anim = gsap.fromTo(element,
      {
        opacity: 0,
        y: 30,
        rotateX: -10, // slight 3D tilt
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
    // save animation for cleanup later
    animations.value.push(anim)
    return anim
  }

  /*
   * page exit animation - reverse of page enter
   * animates out when navigating away
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

  /*
   * staggered list animation - animates list items one after another
   * creates nice cascading effect - works great for lists and grids
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

  /*
   * simple fade in animation - just opacity change
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

  /*
   * slide in from direction - can come from up, down, left, or right
   * useful for side panels, modals, etc
   */
  const slideIn = (element, direction = 'up', options = {}) => {
    const defaults = {
      duration: 0.5,
      ease: 'power3.out',
      distance: 30,
      delay: 0
    }
    const config = { ...defaults, ...options }

    // direction offsets - how far to move from
    const directions = {
      up: { y: config.distance, x: 0 },
      down: { y: -config.distance, x: 0 },
      left: { x: config.distance, y: 0 },
      right: { x: -config.distance, y: 0 }
    }

    // get starting position based on direction
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

  /*
   * scale in animation - grows from small to normal size
   * uses back.out easing for bounce effect - looks nice
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

  /*
   * 3D card hover effect - card tilts based on mouse position
   * this was tricky to figure out but looks really cool
   */
  const card3DHover = (element, event) => {
    // get mouse position relative to element
    const rect = element.getBoundingClientRect()
    const x = event.clientX - rect.left
    const y = event.clientY - rect.top
    const centerX = rect.width / 2
    const centerY = rect.height / 2

    // calculate rotation angles based on distance from center
    // divide by 15 to reduce intensity - looks better this way
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

  /*
   * reset 3D card hover - returns card to flat position
   */
  const card3DReset = (element) => {
    gsap.to(element, {
      rotateX: 0,
      rotateY: 0,
      duration: 0.5,
      ease: 'power3.out'
    })
  }

  /*
   * glow pulse animation - makes element glow in and out
   * useful for highlighting important elements - definately eye-catching
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

  /*
   * counter animation for numbers - counts up from start to end value
   * great for stats, dashboards, analytics - makes numbers feel dynamic
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

  /*
   * ripple effect on click - material design style
   * creates expanding circle from click point - looks satisfying
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

  /*
   * shake animation (for errors) - wiggles element back and forth
   * good for showing validation errors or attention-grabbing
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

  /*
   * bounce animation - element bounces up and down
   * playful effect for buttons or success states
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

  /*
   * morph/blob animation for backgrounds - creates organic shape changes
   * works for now but might need tweaking - kind of experimental
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

  /*
   * text reveal animation (typewriter style) - letters appear one by one
   * splits text into spans and animates each character - looks fancy
   */
  const revealText = (element, options = {}) => {
    const defaults = {
      duration: 0.8,
      stagger: 0.02
    }
    const config = { ...defaults, ...options }

    // split text into spans - wrap each character
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

  /*
   * floating animation - element gently floats up and down
   * nice subtle effect for hero sections or decorative elements
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

  /*
   * kill all registered animations - cleanup function
   * important to prevent memory leaks when component unmounts
   */
  const killAll = () => {
    animations.value.forEach(anim => {
      if (anim && anim.kill) {
        anim.kill()
      }
    })
    animations.value = []
  }

  // cleanup on unmount - automatically stops all animations
  onUnmounted(() => {
    killAll()
  })

  // return all animation functions - use whatever you need
  return {
    // page animations
    pageEnter,
    pageExit,

    // element animations
    staggerIn,
    fadeIn,
    slideIn,
    scaleIn,

    // interactive animations
    card3DHover,
    card3DReset,
    createRipple,

    // effect animations
    glowPulse,
    animateCounter,
    shake,
    bounce,
    morphBlob,
    float,

    // text animations
    revealText,

    // utility
    killAll
  }
}

/*
 * simple hook for on-mount animation - convenience wrapper
 * automatically runs animation when component mounts
 */
export function useOnMountAnimation(animationFn, elementRef, options = {}) {
  onMounted(() => {
    if (elementRef.value) {
      animationFn(elementRef.value, options)
    }
  })
}
