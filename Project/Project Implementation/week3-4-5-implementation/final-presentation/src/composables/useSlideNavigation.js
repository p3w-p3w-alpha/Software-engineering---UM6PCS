import { ref, computed, onMounted, onUnmounted } from 'vue'

export function useSlideNavigation(totalSlides) {
  const currentSlide = ref(0)
  const isAnimating = ref(false)
  const direction = ref('next')

  const progress = computed(() => ((currentSlide.value + 1) / totalSlides) * 100)

  const canGoNext = computed(() => currentSlide.value < totalSlides - 1)
  const canGoPrev = computed(() => currentSlide.value > 0)

  const goToSlide = (index) => {
    if (isAnimating.value || index === currentSlide.value) return
    if (index < 0 || index >= totalSlides) return

    isAnimating.value = true
    direction.value = index > currentSlide.value ? 'next' : 'prev'
    currentSlide.value = index

    setTimeout(() => {
      isAnimating.value = false
    }, 800)
  }

  const nextSlide = () => {
    if (canGoNext.value) {
      goToSlide(currentSlide.value + 1)
    }
  }

  const prevSlide = () => {
    if (canGoPrev.value) {
      goToSlide(currentSlide.value - 1)
    }
  }

  const handleKeydown = (e) => {
    if (e.key === 'ArrowRight' || e.key === ' ' || e.key === 'Enter') {
      e.preventDefault()
      nextSlide()
    } else if (e.key === 'ArrowLeft' || e.key === 'Backspace') {
      e.preventDefault()
      prevSlide()
    } else if (e.key === 'Home') {
      e.preventDefault()
      goToSlide(0)
    } else if (e.key === 'End') {
      e.preventDefault()
      goToSlide(totalSlides - 1)
    } else if (e.key >= '1' && e.key <= '9') {
      const slideIndex = parseInt(e.key) - 1
      if (slideIndex < totalSlides) {
        goToSlide(slideIndex)
      }
    }
  }

  let touchStartX = 0
  let touchEndX = 0

  const handleTouchStart = (e) => {
    touchStartX = e.changedTouches[0].screenX
  }

  const handleTouchEnd = (e) => {
    touchEndX = e.changedTouches[0].screenX
    handleSwipe()
  }

  const handleSwipe = () => {
    const swipeThreshold = 50
    const diff = touchStartX - touchEndX

    if (Math.abs(diff) > swipeThreshold) {
      if (diff > 0) {
        nextSlide()
      } else {
        prevSlide()
      }
    }
  }

  onMounted(() => {
    window.addEventListener('keydown', handleKeydown)
    window.addEventListener('touchstart', handleTouchStart)
    window.addEventListener('touchend', handleTouchEnd)
  })

  onUnmounted(() => {
    window.removeEventListener('keydown', handleKeydown)
    window.removeEventListener('touchstart', handleTouchStart)
    window.removeEventListener('touchend', handleTouchEnd)
  })

  return {
    currentSlide,
    progress,
    direction,
    isAnimating,
    canGoNext,
    canGoPrev,
    goToSlide,
    nextSlide,
    prevSlide
  }
}
