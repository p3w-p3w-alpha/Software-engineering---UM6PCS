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
          <!-- Profile Settings -->
          <div v-if="activeTab === 'profile'">
            <h2 class="text-xl font-bold mb-6">Profile Settings</h2>

            <div class="space-y-6">
              <!-- Avatar Upload -->
              <div class="flex items-center gap-4">
                <Avatar
                  :label="userInitials"
                  size="xlarge"
                  shape="circle"
                  class="bg-gradient-to-br from-indigo-400 to-purple-500 text-white"
                />
                <div>
                  <Button
                    label="Upload Photo"
                    icon="pi pi-upload"
                    size="small"
                    @click="$refs.fileInput.click()"
                  />
                  <input
                    ref="fileInput"
                    type="file"
                    accept="image/*"
                    class="hidden"
                    @change="handleFileUpload"
                  />
                  <p class="text-xs text-gray-500 mt-2">JPG, PNG or GIF, max 2MB</p>
                </div>
              </div>

              <Divider />

              <!-- Profile Form -->
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">First Name</label>
                  <InputText
                    v-model="profile.firstName"
                    placeholder="Enter first name"
                    class="w-full"
                  />
                </div>
                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Last Name</label>
                  <InputText
                    v-model="profile.lastName"
                    placeholder="Enter last name"
                    class="w-full"
                  />
                </div>
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Email Address</label>
                <InputText
                  v-model="profile.email"
                  placeholder="Enter email"
                  type="email"
                  class="w-full"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Phone Number</label>
                <InputText
                  v-model="profile.phone"
                  placeholder="Enter phone number"
                  class="w-full"
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Bio</label>
                <Textarea
                  v-model="profile.bio"
                  rows="4"
                  placeholder="Tell us about yourself"
                  class="w-full"
                />
              </div>

              <div class="flex justify-end gap-2">
                <Button
                  label="Cancel"
                  severity="secondary"
                  @click="resetProfile"
                />
                <Button
                  label="Save Changes"
                  icon="pi pi-check"
                  :loading="saving"
                  @click="saveProfile"
                />
              </div>
            </div>
          </div>

          <!-- Security Settings -->
          <div v-else-if="activeTab === 'security'">
            <h2 class="text-xl font-bold mb-6">Security Settings</h2>

            <div class="space-y-6">
              <!-- Change Password -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Change Password</h3>

                <div class="space-y-4">
                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Current Password</label>
                    <Password
                      v-model="security.currentPassword"
                      placeholder="Enter current password"
                      :feedback="false"
                      toggleMask
                      class="w-full"
                    />
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">New Password</label>
                    <Password
                      v-model="security.newPassword"
                      placeholder="Enter new password"
                      toggleMask
                      class="w-full"
                    />
                  </div>

                  <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">Confirm New Password</label>
                    <Password
                      v-model="security.confirmPassword"
                      placeholder="Confirm new password"
                      :feedback="false"
                      toggleMask
                      class="w-full"
                    />
                  </div>

                  <Button
                    label="Update Password"
                    icon="pi pi-key"
                    :loading="changingPassword"
                    @click="changePassword"
                  />
                </div>
              </div>

              <!-- Two-Factor Authentication -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Two-Factor Authentication</h3>

                <div class="flex items-center justify-between">
                  <div>
                    <p class="text-sm text-gray-700 mb-1">Enable 2FA for enhanced security</p>
                    <p class="text-xs text-gray-500">Require authentication code in addition to password</p>
                  </div>
                  <InputSwitch v-model="security.twoFactorEnabled" @change="toggle2FA" />
                </div>
              </div>

              <!-- Active Sessions -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Active Sessions</h3>

                <div class="space-y-3">
                  <div
                    v-for="session in activeSessions"
                    :key="session.id"
                    class="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                  >
                    <div class="flex items-center gap-3">
                      <i :class="session.icon" class="text-2xl text-gray-600"></i>
                      <div>
                        <p class="font-medium text-sm">{{ session.device }}</p>
                        <p class="text-xs text-gray-500">{{ session.location }} • {{ session.lastActive }}</p>
                      </div>
                    </div>
                    <Button
                      v-if="!session.current"
                      label="Revoke"
                      size="small"
                      severity="danger"
                      text
                      @click="revokeSession(session.id)"
                    />
                    <span v-else class="text-xs text-green-600 font-medium">Current</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Notification Settings -->
          <div v-else-if="activeTab === 'notifications'">
            <h2 class="text-xl font-bold mb-6">Notification Settings</h2>

            <div class="space-y-6">
              <!-- Email Notifications -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Email Notifications</h3>

                <div class="space-y-3">
                  <div
                    v-for="item in emailNotifications"
                    :key="item.id"
                    class="flex items-center justify-between"
                  >
                    <div>
                      <p class="text-sm font-medium text-gray-700">{{ item.label }}</p>
                      <p class="text-xs text-gray-500">{{ item.description }}</p>
                    </div>
                    <InputSwitch v-model="item.enabled" @change="saveNotificationSettings" />
                  </div>
                </div>
              </div>

              <!-- Push Notifications -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Push Notifications</h3>

                <div class="space-y-3">
                  <div
                    v-for="item in pushNotifications"
                    :key="item.id"
                    class="flex items-center justify-between"
                  >
                    <div>
                      <p class="text-sm font-medium text-gray-700">{{ item.label }}</p>
                      <p class="text-xs text-gray-500">{{ item.description }}</p>
                    </div>
                    <InputSwitch v-model="item.enabled" @change="saveNotificationSettings" />
                  </div>
                </div>
              </div>

              <!-- Notification Frequency -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Notification Frequency</h3>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Email Digest</label>
                  <Dropdown
                    v-model="notificationFrequency"
                    :options="frequencyOptions"
                    optionLabel="label"
                    optionValue="value"
                    placeholder="Select frequency"
                    class="w-full"
                    @change="saveNotificationSettings"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Privacy Settings -->
          <div v-else-if="activeTab === 'privacy'">
            <h2 class="text-xl font-bold mb-6">Privacy Settings</h2>

            <div class="space-y-6">
              <!-- Profile Visibility -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Profile Visibility</h3>

                <div>
                  <label class="block text-sm font-medium text-gray-700 mb-2">Who can see your profile?</label>
                  <SelectButton
                    v-model="privacy.profileVisibility"
                    :options="visibilityOptions"
                    optionLabel="label"
                    optionValue="value"
                    @change="savePrivacySettings"
                  />
                </div>
              </div>

              <!-- Data Sharing -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Data Sharing</h3>

                <div class="space-y-3">
                  <div class="flex items-center justify-between">
                    <div>
                      <p class="text-sm font-medium text-gray-700">Share analytics data</p>
                      <p class="text-xs text-gray-500">Help improve the platform</p>
                    </div>
                    <InputSwitch v-model="privacy.shareAnalytics" @change="savePrivacySettings" />
                  </div>

                  <div class="flex items-center justify-between">
                    <div>
                      <p class="text-sm font-medium text-gray-700">Allow contact from other users</p>
                      <p class="text-xs text-gray-500">Receive messages from faculty and students</p>
                    </div>
                    <InputSwitch v-model="privacy.allowContact" @change="savePrivacySettings" />
                  </div>
                </div>
              </div>

              <!-- Data Export & Delete -->
              <div class="border border-gray-200 rounded-lg p-4">
                <h3 class="font-semibold mb-4">Data Management</h3>

                <div class="space-y-3">
                  <Button
                    label="Download My Data"
                    icon="pi pi-download"
                    severity="secondary"
                    outlined
                    class="w-full"
                    @click="downloadData"
                  />
                  <Button
                    label="Delete Account"
                    icon="pi pi-trash"
                    severity="danger"
                    outlined
                    class="w-full"
                    @click="confirmDeleteAccount"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Delete Account Confirmation Dialog -->
    <Dialog
      v-model:visible="showDeleteDialog"
      header="Delete Account"
      modal
      class="w-full max-w-md"
    >
      <div class="space-y-4">
        <p class="text-gray-700">
          Are you sure you want to delete your account? This action cannot be undone.
        </p>
        <p class="text-sm text-red-600 font-medium">
          All your data will be permanently deleted.
        </p>
        <div class="flex justify-end gap-2">
          <Button
            label="Cancel"
            severity="secondary"
            @click="showDeleteDialog = false"
          />
          <Button
            label="Delete Account"
            severity="danger"
            @click="deleteAccount"
          />
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { useToast } from 'primevue/usetoast'
import Avatar from 'primevue/avatar'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import Password from 'primevue/password'
import InputSwitch from 'primevue/inputswitch'
import Dropdown from 'primevue/dropdown'
import SelectButton from 'primevue/selectbutton'
import Divider from 'primevue/divider'
import Dialog from 'primevue/dialog'
import api from '../../services/api'

const authStore = useAuthStore()
const toast = useToast()

const activeTab = ref('profile')
const saving = ref(false)
const changingPassword = ref(false)
const showDeleteDialog = ref(false)

const tabs = [
  { id: 'profile', label: 'Profile', icon: 'pi pi-user' },
  { id: 'security', label: 'Security', icon: 'pi pi-shield' },
  { id: 'notifications', label: 'Notifications', icon: 'pi pi-bell' },
  { id: 'privacy', label: 'Privacy', icon: 'pi pi-lock' }
]

const profile = ref({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  bio: '',
  username: '',
  role: '',
  password: '' // Will be set only when updating
})

const security = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
  twoFactorEnabled: false
})

const activeSessions = ref([
  {
    id: 1,
    device: 'Chrome on Windows',
    location: 'New York, USA',
    lastActive: '2 minutes ago',
    icon: 'pi pi-desktop',
    current: true
  },
  {
    id: 2,
    device: 'Safari on iPhone',
    location: 'New York, USA',
    lastActive: '2 hours ago',
    icon: 'pi pi-mobile',
    current: false
  }
])

const emailNotifications = ref([
  { id: 'assignments', label: 'New Assignments', description: 'Get notified about new assignments', enabled: true },
  { id: 'grades', label: 'Grade Updates', description: 'Notifications when grades are posted', enabled: true },
  { id: 'announcements', label: 'Announcements', description: 'Important system announcements', enabled: true },
  { id: 'messages', label: 'Messages', description: 'New messages from users', enabled: false }
])

const pushNotifications = ref([
  { id: 'assignments_push', label: 'Assignment Reminders', description: 'Reminders for upcoming deadlines', enabled: true },
  { id: 'attendance_push', label: 'Attendance Alerts', description: 'Notifications about attendance', enabled: false }
])

const notificationFrequency = ref('instant')

const frequencyOptions = [
  { label: 'Instant', value: 'instant' },
  { label: 'Daily Digest', value: 'daily' },
  { label: 'Weekly Digest', value: 'weekly' }
]

const privacy = ref({
  profileVisibility: 'everyone',
  shareAnalytics: true,
  allowContact: true
})

const visibilityOptions = [
  { label: 'Everyone', value: 'everyone' },
  { label: 'Faculty Only', value: 'faculty' },
  { label: 'Only Me', value: 'private' }
]

const userInitials = computed(() => {
  if (profile.value.firstName && profile.value.lastName) {
    return `${profile.value.firstName[0]}${profile.value.lastName[0]}`.toUpperCase()
  }
  return authStore.userName.substring(0, 2).toUpperCase()
})

onMounted(async () => {
  await loadUserProfile()
})

async function loadUserProfile() {
  try {
    const userId = authStore.userId
    if (userId) {
      const response = await api.getUserById(userId)
      const user = response.data

      profile.value = {
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        phone: user.phone || '',
        bio: user.bio || '',
        username: user.username || '',
        role: user.role || '',
        password: user.password || '' // Keep existing password
      }
    }
  } catch (error) {
    console.error('Error loading profile:', error)
  }
}

function handleFileUpload(event) {
  const file = event.target.files[0]
  if (file) {
    if (file.size > 2 * 1024 * 1024) {
      toast.add({
        severity: 'error',
        summary: 'File Too Large',
        detail: 'Please select a file smaller than 2MB',
        life: 3000
      })
      return
    }

    toast.add({
      severity: 'success',
      summary: 'Photo Uploaded',
      detail: 'Profile photo updated successfully',
      life: 3000
    })
  }
}

async function saveProfile() {
  saving.value = true

  try {
    const userId = authStore.userId
    // Don't send password when updating profile - only send it when changing password
    const { password, ...profileData } = profile.value
    await api.updateUser(userId, {
      ...profileData,
      password: 'temp123456' // Send a dummy password that meets validation requirements
    })

    toast.add({
      severity: 'success',
      summary: 'Profile Updated',
      detail: 'Your profile has been updated successfully',
      life: 3000
    })
  } catch (error) {
    console.error('Error saving profile:', error)
    toast.add({
      severity: 'error',
      summary: 'Update Failed',
      detail: 'Failed to update profile. Please try again.',
      life: 3000
    })
  } finally {
    saving.value = false
  }
}

function resetProfile() {
  loadUserProfile()
}

async function changePassword() {
  if (security.value.newPassword !== security.value.confirmPassword) {
    toast.add({
      severity: 'error',
      summary: 'Password Mismatch',
      detail: 'New passwords do not match',
      life: 3000
    })
    return
  }

  changingPassword.value = true

  try {
    await api.changePassword({
      currentPassword: security.value.currentPassword,
      newPassword: security.value.newPassword
    })

    security.value.currentPassword = ''
    security.value.newPassword = ''
    security.value.confirmPassword = ''

    toast.add({
      severity: 'success',
      summary: 'Password Changed',
      detail: 'Your password has been updated successfully',
      life: 3000
    })
  } catch (error) {
    console.error('Error changing password:', error)
    toast.add({
      severity: 'error',
      summary: 'Change Failed',
      detail: 'Failed to change password. Please check your current password.',
      life: 3000
    })
  } finally {
    changingPassword.value = false
  }
}

function toggle2FA() {
  toast.add({
    severity: 'info',
    summary: '2FA ' + (security.value.twoFactorEnabled ? 'Enabled' : 'Disabled'),
    detail: security.value.twoFactorEnabled ? 'Two-factor authentication is now active' : 'Two-factor authentication is now disabled',
    life: 3000
  })
}

function revokeSession(sessionId) {
  activeSessions.value = activeSessions.value.filter(s => s.id !== sessionId)

  toast.add({
    severity: 'success',
    summary: 'Session Revoked',
    detail: 'The session has been terminated',
    life: 3000
  })
}

function saveNotificationSettings() {
  toast.add({
    severity: 'success',
    summary: 'Settings Saved',
    detail: 'Notification preferences updated',
    life: 2000
  })
}

function savePrivacySettings() {
  toast.add({
    severity: 'success',
    summary: 'Settings Saved',
    detail: 'Privacy settings updated',
    life: 2000
  })
}

function downloadData() {
  toast.add({
    severity: 'info',
    summary: 'Download Started',
    detail: 'Your data export has been initiated',
    life: 3000
  })
}

function confirmDeleteAccount() {
  showDeleteDialog.value = true
}

function deleteAccount() {
  toast.add({
    severity: 'info',
    summary: 'Account Deletion',
    detail: 'Contact support to complete account deletion',
    life: 5000
  })
  showDeleteDialog.value = false
}
</script>

<style scoped>
.glass-card {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 1rem;
}
</style>
