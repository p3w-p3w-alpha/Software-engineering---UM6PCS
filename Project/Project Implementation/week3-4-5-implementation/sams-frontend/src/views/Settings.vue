<template>
  <div class="p-6">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-900">Settings</h1>
      <p class="text-gray-600 mt-2">Manage your account preferences and settings</p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- Settings Menu -->
      <div class="lg:col-span-1">
        <div class="glass-card p-4">
          <nav class="space-y-2">
            <button
              v-for="tab in tabs"
              :key="tab.id"
              @click="activeTab = tab.id"
              :class="[
                'w-full flex items-center gap-3 px-4 py-3 rounded-lg text-left transition-all',
                activeTab === tab.id
                  ? 'bg-gradient-to-r from-indigo-600 to-purple-600 text-white'
                  : 'text-gray-700 hover:bg-gray-100'
              ]"
            >
              <i :class="tab.icon"></i>
              <span>{{ tab.label }}</span>
            </button>
          </nav>
        </div>
      </div>

      <!-- Settings Content -->
      <div class="lg:col-span-2">
        <div class="glass-card p-6">
          <div v-if="activeTab === 'profile'">
            <h2 class="text-xl font-bold mb-4">Profile Settings</h2>
            <p class="text-gray-600">Manage your profile information</p>
          </div>

          <div v-if="activeTab === 'security'">
            <h2 class="text-xl font-bold mb-4">Security Settings</h2>
            <p class="text-gray-600">Update your security preferences</p>
          </div>

          <div v-if="activeTab === 'notifications'">
            <h2 class="text-xl font-bold mb-4">Notification Settings</h2>
            <p class="text-gray-600">Configure your notification preferences</p>
          </div>

          <div v-if="activeTab === 'privacy'">
            <h2 class="text-xl font-bold mb-4">Privacy Settings</h2>
            <p class="text-gray-600">Manage your privacy settings</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('profile')

const tabs = [
  { id: 'profile', label: 'Profile', icon: 'pi pi-user' },
  { id: 'security', label: 'Security', icon: 'pi pi-shield' },
  { id: 'notifications', label: 'Notifications', icon: 'pi pi-bell' },
  { id: 'privacy', label: 'Privacy', icon: 'pi pi-lock' }
]
</script>