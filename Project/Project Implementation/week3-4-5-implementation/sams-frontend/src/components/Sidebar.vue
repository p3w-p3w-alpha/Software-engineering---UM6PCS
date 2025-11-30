<template>
  <aside class="bg-white w-64 min-h-screen shadow-lg border-r border-gray-200" role="navigation" aria-label="Sidebar navigation">
    <div class="p-6">
      <h2 class="text-2xl font-bold text-gray-800">{{ title }}</h2>
      <p class="text-sm text-gray-500 mt-1">{{ subtitle }}</p>
    </div>

    <nav class="mt-6" aria-label="Main menu">
      <div
        v-for="item in menuItems"
        :key="item.name"
        class="px-4 mb-1"
      >
        <router-link
          v-if="!item.action"
          :to="item.href"
          class="flex items-center px-4 py-3 rounded-lg transition-colors"
          :class="[
            isActive(item.href)
              ? 'bg-blue-50 text-blue-600'
              : 'text-gray-700 hover:bg-gray-50'
          ]"
          :aria-current="isActive(item.href) ? 'page' : undefined"
        >
          <component :is="item.icon" class="h-5 w-5 mr-3" aria-hidden="true" />
          <span class="font-medium">{{ item.name }}</span>
          <span
            v-if="item.badge"
            class="ml-auto inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800"
            :aria-label="`${item.badge} notifications`"
          >
            {{ item.badge }}
          </span>
        </router-link>

        <button
          v-else
          @click="item.action"
          class="w-full flex items-center px-4 py-3 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
          :aria-label="item.name"
        >
          <component :is="item.icon" class="h-5 w-5 mr-3" aria-hidden="true" />
          <span class="font-medium">{{ item.name }}</span>
        </button>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  menuItems: {
    type: Array,
    required: true
  }
})

const route = useRoute()

const isActive = (href) => {
  return route.path === href || route.path.startsWith(href + '/')
}
</script>
