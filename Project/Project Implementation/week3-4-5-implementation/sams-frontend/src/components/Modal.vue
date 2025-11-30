<template>
  <Teleport to="body">
    <transition
      enter-active-class="transition ease-out duration-200"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition ease-in duration-150"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div
        v-if="modelValue"
        class="fixed inset-0 z-50 overflow-y-auto"
        @click.self="closeModal"
      >
        <!-- Backdrop -->
        <div class="fixed inset-0 bg-black bg-opacity-50" @click="closeModal"></div>

        <!-- Modal Container -->
        <div class="flex min-h-full items-center justify-center p-4">
          <transition
            enter-active-class="transition ease-out duration-200"
            enter-from-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
            enter-to-class="opacity-100 translate-y-0 sm:scale-100"
            leave-active-class="transition ease-in duration-150"
            leave-from-class="opacity-100 translate-y-0 sm:scale-100"
            leave-to-class="opacity-0 translate-y-4 sm:translate-y-0 sm:scale-95"
          >
            <div
              v-if="modelValue"
              class="relative transform overflow-hidden rounded-lg bg-white shadow-xl transition-all"
              :class="sizeClasses"
              role="dialog"
              aria-modal="true"
              :aria-labelledby="'modal-title-' + title.replace(/\s+/g, '-').toLowerCase()"
            >
              <!-- Header -->
              <div class="bg-gray-50 px-6 py-4 border-b border-gray-200">
                <div class="flex items-center justify-between">
                  <h3 :id="'modal-title-' + title.replace(/\s+/g, '-').toLowerCase()" class="text-lg font-semibold text-gray-900">
                    {{ title }}
                  </h3>
                  <button
                    v-if="closeable"
                    @click="closeModal"
                    class="text-gray-400 hover:text-gray-600 transition-colors"
                    aria-label="Close modal"
                  >
                    <svg
                      aria-hidden="true"
                      class="h-6 w-6"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M6 18L18 6M6 6l12 12"
                      />
                    </svg>
                  </button>
                </div>
              </div>

              <!-- Body -->
              <div class="px-6 py-4">
                <slot></slot>
              </div>

              <!-- Footer -->
              <div
                v-if="showFooter"
                class="bg-gray-50 px-6 py-4 border-t border-gray-200 flex justify-end space-x-3"
              >
                <slot name="footer">
                  <button
                    v-if="showCancel"
                    @click="closeModal"
                    class="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                  >
                    {{ cancelText }}
                  </button>
                  <button
                    v-if="showConfirm"
                    @click="$emit('confirm')"
                    :disabled="confirmDisabled"
                    class="px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    {{ confirmText }}
                  </button>
                </slot>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  title: {
    type: String,
    required: true
  },
  size: {
    type: String,
    default: 'md',
    validator: (value) => ['sm', 'md', 'lg', 'xl', 'full', 'small', 'medium', 'large'].includes(value)
  },
  closeable: {
    type: Boolean,
    default: true
  },
  showFooter: {
    type: Boolean,
    default: true
  },
  showCancel: {
    type: Boolean,
    default: true
  },
  showConfirm: {
    type: Boolean,
    default: true
  },
  cancelText: {
    type: String,
    default: 'Cancel'
  },
  confirmText: {
    type: String,
    default: 'Confirm'
  },
  confirmDisabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'confirm', 'close'])

const sizeClasses = computed(() => {
  const sizes = {
    sm: 'max-w-sm w-full',
    small: 'max-w-sm w-full',
    md: 'max-w-md w-full',
    medium: 'max-w-lg w-full',
    lg: 'max-w-2xl w-full',
    large: 'max-w-2xl w-full',
    xl: 'max-w-4xl w-full',
    full: 'max-w-7xl w-full mx-4'
  }
  return sizes[props.size]
})

const closeModal = () => {
  if (props.closeable) {
    emit('update:modelValue', false)
    emit('close')
  }
}
</script>
