<template>
  <div class="empty-state" :class="{ 'compact': compact }">
    <div class="illustration-wrapper">
      <div class="illustration" v-if="!customIcon">
        <!-- Default SVG illustration based on type -->
        <svg v-if="illustrationType === 'no-data'" viewBox="0 0 200 200" class="empty-icon">
          <circle cx="100" cy="100" r="80" fill="none" stroke="currentColor" stroke-width="2" stroke-dasharray="5,5" opacity="0.3"/>
          <path d="M70 100 L130 100 M100 70 L100 130" stroke="currentColor" stroke-width="3" stroke-linecap="round" opacity="0.5"/>
        </svg>
        
        <svg v-else-if="illustrationType === 'search'" viewBox="0 0 200 200" class="empty-icon">
          <circle cx="80" cy="80" r="40" fill="none" stroke="currentColor" stroke-width="3"/>
          <line x1="110" y1="110" x2="140" y2="140" stroke="currentColor" stroke-width="3" stroke-linecap="round"/>
        </svg>
        
        <svg v-else-if="illustrationType === 'inbox'" viewBox="0 0 200 200" class="empty-icon">
          <rect x="40" y="60" width="120" height="100" rx="8" fill="none" stroke="currentColor" stroke-width="3"/>
          <path d="M40 90 L100 130 L160 90" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>

        <svg v-else viewBox="0 0 200 200" class="empty-icon">
          <circle cx="100" cy="100" r="60" fill="none" stroke="currentColor" stroke-width="2" opacity="0.3"/>
          <path d="M80 90 Q90 80 100 90 Q110 100 120 90" fill="none" stroke="currentColor" stroke-width="2"/>
          <circle cx="85" cy="95" r="3" fill="currentColor"/>
          <circle cx="115" cy="95" r="3" fill="currentColor"/>
        </svg>
      </div>
      
      <!-- Custom icon slot -->
      <div v-else class="custom-icon">
        <slot name="icon"></slot>
      </div>
    </div>

    <div class="empty-content">
      <h3 class="empty-title">{{ title }}</h3>
      <p class="empty-description">{{ description }}</p>
      
      <div v-if="$slots.action || actionText" class="empty-action">
        <slot name="action">
          <button 
            v-if="actionText" 
            @click="$emit('action')"
            class="btn-primary"
          >
            {{ actionText }}
          </button>
        </slot>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: 'No data available'
  },
  description: {
    type: String,
    default: 'There is nothing to display here yet.'
  },
  actionText: {
    type: String,
    default: ''
  },
  illustrationType: {
    type: String,
    default: 'no-data', // no-data, search, inbox, default
    validator: (value) => ['no-data', 'search', 'inbox', 'default'].includes(value)
  },
  customIcon: {
    type: Boolean,
    default: false
  },
  compact: {
    type: Boolean,
    default: false
  }
})

defineEmits(['action'])
</script>

<style scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 32px;
  text-align: center;
  animation: fadeIn 0.5s ease-out;
}

.empty-state.compact {
  padding: 32px 16px;
}

.illustration-wrapper {
  margin-bottom: 32px;
  animation: float 3s ease-in-out infinite;
}

.illustration {
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-state.compact .illustration {
  width: 120px;
  height: 120px;
}

.empty-icon {
  width: 100%;
  height: 100%;
  color: #94a3b8;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
}

.custom-icon {
  font-size: 80px;
  color: #3B82F6;
  filter: drop-shadow(0 4px 6px rgba(59, 130, 246, 0.2));
}

.empty-content {
  max-width: 400px;
}

.empty-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.empty-state.compact .empty-title {
  font-size: 18px;
  margin-bottom: 8px;
}

.empty-description {
  font-size: 16px;
  color: #64748b;
  margin-bottom: 24px;
  line-height: 1.6;
}

.empty-state.compact .empty-description {
  font-size: 14px;
  margin-bottom: 16px;
}

.empty-action {
  margin-top: 24px;
  animation: slideInUp 0.5s ease-out 0.2s both;
}

.btn-primary {
  padding: 12px 32px;
  background: linear-gradient(135deg, #3B82F6 0%, #8B5CF6 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 6px rgba(59, 130, 246, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 12px rgba(59, 130, 246, 0.4);
}

.btn-primary:active {
  transform: translateY(0);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

@keyframes slideInUp {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
