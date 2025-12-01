<template>
  <div class="profile-page">
    <!-- Toast Notification -->
    <Transition name="toast">
      <div v-if="showToast" class="toast-notification" :class="toastType">
        <div class="toast-content">
          <CheckCircleIcon v-if="toastType === 'success'" class="toast-icon" />
          <ExclamationCircleIcon v-else class="toast-icon" />
          <span>{{ toastMessage }}</span>
        </div>
        <button @click="showToast = false" class="toast-close">
          <XMarkIcon class="w-4 h-4" />
        </button>
      </div>
    </Transition>

    <!-- Loading State -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p class="loading-text">Loading profile...</p>
    </div>

    <!-- Profile Content -->
    <div v-else class="profile-content">
      <!-- Profile Header Card -->
      <div class="profile-header-card">
        <!-- Decorative Background Pattern -->
        <div class="card-pattern">
          <div class="pattern-circle pattern-1"></div>
          <div class="pattern-circle pattern-2"></div>
          <div class="pattern-circle pattern-3"></div>
        </div>

        <!-- Cover Image -->
        <div class="cover-image" :style="coverStyle">
          <div class="cover-overlay"></div>
          <!-- Edit Cover Button (own profile) -->
          <button v-if="isOwnProfile" @click="triggerCoverUpload" class="edit-cover-btn">
            <CameraIcon class="w-5 h-5" />
            <span>Edit Cover</span>
          </button>
          <input
            ref="coverInput"
            type="file"
            accept="image/*"
            class="hidden"
            @change="handleCoverUpload"
          />
        </div>

        <!-- Profile Info Section -->
        <div class="profile-info-section">
          <!-- Avatar -->
          <div class="avatar-container">
            <div class="avatar-ring">
              <div class="avatar" :class="{ 'has-image': user.profilePicture }">
                <img
                  v-if="user.profilePicture"
                  :src="getProfilePictureUrl(user.profilePicture)"
                  alt="Profile"
                  class="avatar-image"
                />
                <span v-else class="avatar-initials">{{ getInitials(user.firstName, user.lastName) }}</span>
              </div>
            </div>
            <div v-if="isOnline" class="online-indicator"></div>
            <!-- Edit Avatar Button (own profile) -->
            <button v-if="isOwnProfile" @click="triggerAvatarUpload" class="edit-avatar-btn">
              <CameraIcon class="w-4 h-4" />
            </button>
            <input
              ref="avatarInput"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleAvatarUpload"
            />
          </div>

          <!-- Name, Role, Actions -->
          <div class="profile-header-content">
            <div class="profile-name-section">
              <div class="name-row">
                <h1 class="profile-name">{{ user.firstName }} {{ user.lastName }}</h1>
                <span class="role-badge" :class="getRoleBadgeClass(user.role)">
                  {{ formatRole(user.role) }}
                </span>
              </div>
              <p class="username">@{{ user.username }}</p>
              <p v-if="user.bio" class="bio">{{ user.bio }}</p>
            </div>

            <!-- Action Buttons -->
            <div class="action-buttons">
              <!-- Own Profile Actions -->
              <template v-if="isOwnProfile">
                <button @click="openEditModal" class="btn-primary">
                  <PencilIcon class="w-4 h-4" />
                  <span>Edit Profile</span>
                </button>
              </template>

              <!-- Other User Actions -->
              <template v-else>
                <!-- Connection Status Based Buttons -->
                <template v-if="connectionStatus === 'NOT_CONNECTED' || connectionStatus === 'REJECTED'">
                  <button @click="sendConnectionRequest" class="btn-primary" :disabled="actionLoading">
                    <UserPlusIcon class="w-4 h-4" />
                    <span>Connect</span>
                  </button>
                </template>

                <template v-else-if="connectionStatus === 'PENDING_SENT'">
                  <button @click="cancelRequest" class="btn-secondary" :disabled="actionLoading">
                    <ClockIcon class="w-4 h-4" />
                    <span>Request Pending</span>
                  </button>
                </template>

                <template v-else-if="connectionStatus === 'PENDING_RECEIVED'">
                  <button @click="acceptRequest" class="btn-success" :disabled="actionLoading">
                    <CheckIcon class="w-4 h-4" />
                    <span>Accept</span>
                  </button>
                  <button @click="rejectRequest" class="btn-danger" :disabled="actionLoading">
                    <XMarkIcon class="w-4 h-4" />
                    <span>Reject</span>
                  </button>
                </template>

                <template v-else-if="connectionStatus === 'CONNECTED'">
                  <button @click="goToConversation" class="btn-primary">
                    <ChatBubbleLeftRightIcon class="w-4 h-4" />
                    <span>Message</span>
                  </button>
                  <button @click="confirmRemoveConnection" class="btn-secondary">
                    <UserMinusIcon class="w-4 h-4" />
                    <span>Remove</span>
                  </button>
                </template>

                <template v-else-if="connectionStatus === 'BLOCKED_BY_YOU'">
                  <button @click="unblockUser" class="btn-secondary" :disabled="actionLoading">
                    <LockOpenIcon class="w-4 h-4" />
                    <span>Unblock</span>
                  </button>
                </template>
              </template>
            </div>
          </div>
        </div>

        <!-- Stats Row -->
        <div class="stats-row">
          <div class="stat-item" @click="activeTab = 'connections'">
            <span class="stat-value">{{ stats.connections }}</span>
            <span class="stat-label">Connections</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.studyGroups }}</span>
            <span class="stat-label">Study Groups</span>
          </div>
          <div v-if="user.role === 'STUDENT'" class="stat-item">
            <span class="stat-value stat-highlight">{{ stats.gpa }}</span>
            <span class="stat-label">GPA</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ stats.courses }}</span>
            <span class="stat-label">{{ user.role === 'FACULTY' ? 'Teaching' : 'Courses' }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ formatMemberSince(user.createdAt) }}</span>
            <span class="stat-label">Member Since</span>
          </div>
        </div>

        <!-- Mutual Connections (shown for other users) -->
        <div v-if="!isOwnProfile && mutualConnections.count > 0" class="mutual-connections">
          <div class="mutual-avatars">
            <div
              v-for="(mutual, index) in mutualConnections.users.slice(0, 3)"
              :key="mutual.id"
              class="mutual-avatar"
              :style="{ zIndex: 3 - index }"
            >
              <img
                v-if="mutual.profilePicture"
                :src="getProfilePictureUrl(mutual.profilePicture)"
                :alt="mutual.firstName"
              />
              <span v-else>{{ getInitials(mutual.firstName, mutual.lastName) }}</span>
            </div>
          </div>
          <span class="mutual-text">
            {{ mutualConnections.count }} mutual connection{{ mutualConnections.count > 1 ? 's' : '' }}
          </span>
        </div>
      </div>

      <!-- Tabs Section -->
      <div class="tabs-card">
        <div class="tabs-nav">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="activeTab = tab.id"
            class="tab-btn"
            :class="{ active: activeTab === tab.id }"
          >
            <component :is="tab.icon" class="tab-icon" />
            <span>{{ tab.name }}</span>
          </button>
        </div>

        <!-- Tab Content -->
        <div class="tab-content">
          <!-- About Tab -->
          <div v-if="activeTab === 'about'" class="about-tab">
            <div class="info-section">
              <h3 class="section-title">
                <InformationCircleIcon class="section-icon" />
                Contact Information
              </h3>
              <div class="info-grid">
                <div class="info-item">
                  <EnvelopeIcon class="info-icon" />
                  <div>
                    <span class="info-label">Email</span>
                    <span class="info-value">{{ user.email }}</span>
                  </div>
                </div>
                <div v-if="user.phone" class="info-item">
                  <PhoneIcon class="info-icon" />
                  <div>
                    <span class="info-label">Phone</span>
                    <span class="info-value">{{ user.phone }}</span>
                  </div>
                </div>
                <div v-if="user.department" class="info-item">
                  <BuildingOfficeIcon class="info-icon" />
                  <div>
                    <span class="info-label">Department</span>
                    <span class="info-value">{{ user.department }}</span>
                  </div>
                </div>
                <div v-if="user.officeLocation" class="info-item">
                  <MapPinIcon class="info-icon" />
                  <div>
                    <span class="info-label">Office Location</span>
                    <span class="info-value">{{ user.officeLocation }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Skills Section -->
            <div v-if="user.skills && user.skills.length > 0" class="info-section">
              <h3 class="section-title">
                <SparklesIcon class="section-icon" />
                Skills
              </h3>
              <div class="tags-container">
                <span v-for="skill in user.skills" :key="skill" class="skill-tag">
                  {{ skill }}
                </span>
              </div>
            </div>

            <!-- Interests Section -->
            <div v-if="user.interests && user.interests.length > 0" class="info-section">
              <h3 class="section-title">
                <HeartIcon class="section-icon" />
                Interests
              </h3>
              <div class="tags-container">
                <span v-for="interest in user.interests" :key="interest" class="interest-tag">
                  {{ interest }}
                </span>
              </div>
            </div>

            <!-- Social Links -->
            <div v-if="hasSocialLinks" class="info-section">
              <h3 class="section-title">
                <LinkIcon class="section-icon" />
                Social Links
              </h3>
              <div class="social-links">
                <a v-if="user.linkedinUrl" :href="user.linkedinUrl" target="_blank" class="social-link linkedin">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M20.447 20.452h-3.554v-5.569c0-1.328-.027-3.037-1.852-3.037-1.853 0-2.136 1.445-2.136 2.939v5.667H9.351V9h3.414v1.561h.046c.477-.9 1.637-1.85 3.37-1.85 3.601 0 4.267 2.37 4.267 5.455v6.286zM5.337 7.433c-1.144 0-2.063-.926-2.063-2.065 0-1.138.92-2.063 2.063-2.063 1.14 0 2.064.925 2.064 2.063 0 1.139-.925 2.065-2.064 2.065zm1.782 13.019H3.555V9h3.564v11.452zM22.225 0H1.771C.792 0 0 .774 0 1.729v20.542C0 23.227.792 24 1.771 24h20.451C23.2 24 24 23.227 24 22.271V1.729C24 .774 23.2 0 22.222 0h.003z"/>
                  </svg>
                  <span>LinkedIn</span>
                </a>
                <a v-if="user.githubUrl" :href="user.githubUrl" target="_blank" class="social-link github">
                  <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                    <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
                  </svg>
                  <span>GitHub</span>
                </a>
                <a v-if="user.websiteUrl" :href="user.websiteUrl" target="_blank" class="social-link website">
                  <GlobeAltIcon class="w-5 h-5" />
                  <span>Website</span>
                </a>
              </div>
            </div>
          </div>

          <!-- Connections Tab -->
          <div v-else-if="activeTab === 'connections'" class="connections-tab">
            <div v-if="connections.length > 0" class="connections-grid">
              <div
                v-for="connection in connections"
                :key="connection.id"
                class="connection-card"
                @click="viewProfile(connection.id)"
              >
                <div class="connection-avatar">
                  <img
                    v-if="connection.profilePicture"
                    :src="getProfilePictureUrl(connection.profilePicture)"
                    :alt="connection.firstName"
                  />
                  <span v-else>{{ getInitials(connection.firstName, connection.lastName) }}</span>
                </div>
                <div class="connection-info">
                  <p class="connection-name">{{ connection.firstName }} {{ connection.lastName }}</p>
                  <p class="connection-role">{{ formatRole(connection.role) }}</p>
                </div>
                <button class="connection-action" @click.stop="goToConversationWith(connection.id)">
                  <ChatBubbleLeftIcon class="w-4 h-4" />
                </button>
              </div>
            </div>
            <div v-else class="empty-state">
              <UsersIcon class="empty-icon" />
              <p class="empty-title">No Connections Yet</p>
              <p class="empty-text">Connect with others to grow your network</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Profile Modal -->
    <Transition name="modal">
      <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
        <div class="modal-container">
          <div class="modal-header">
            <h2 class="modal-title">Edit Profile</h2>
            <button @click="closeEditModal" class="modal-close">
              <XMarkIcon class="w-5 h-5" />
            </button>
          </div>

          <div class="modal-body">
            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">First Name</label>
                <input v-model="editForm.firstName" type="text" class="form-input" />
              </div>
              <div class="form-group">
                <label class="form-label">Last Name</label>
                <input v-model="editForm.lastName" type="text" class="form-input" />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Bio</label>
              <textarea v-model="editForm.bio" rows="3" class="form-input" placeholder="Tell us about yourself..."></textarea>
            </div>

            <div class="form-grid">
              <div class="form-group">
                <label class="form-label">Phone</label>
                <input v-model="editForm.phone" type="tel" class="form-input" />
              </div>
              <div class="form-group">
                <label class="form-label">Department</label>
                <input v-model="editForm.department" type="text" class="form-input" />
              </div>
            </div>

            <div v-if="user.role === 'FACULTY'" class="form-group">
              <label class="form-label">Office Location</label>
              <input v-model="editForm.officeLocation" type="text" class="form-input" placeholder="Building A, Room 301" />
            </div>

            <div class="form-group">
              <label class="form-label">Skills (comma separated)</label>
              <input v-model="editForm.skillsText" type="text" class="form-input" placeholder="Java, Python, SQL" />
            </div>

            <div class="form-group">
              <label class="form-label">Interests (comma separated)</label>
              <input v-model="editForm.interestsText" type="text" class="form-input" placeholder="Machine Learning, Web Development" />
            </div>

            <div class="form-section-title">Social Links</div>
            <div class="form-group">
              <label class="form-label">LinkedIn URL</label>
              <input v-model="editForm.linkedinUrl" type="url" class="form-input" placeholder="https://linkedin.com/in/username" />
            </div>
            <div class="form-group">
              <label class="form-label">GitHub URL</label>
              <input v-model="editForm.githubUrl" type="url" class="form-input" placeholder="https://github.com/username" />
            </div>
            <div class="form-group">
              <label class="form-label">Website URL</label>
              <input v-model="editForm.websiteUrl" type="url" class="form-input" placeholder="https://yourwebsite.com" />
            </div>
          </div>

          <div class="modal-footer">
            <button @click="closeEditModal" class="btn-secondary">Cancel</button>
            <button @click="saveProfile" class="btn-primary" :disabled="saving">
              <span v-if="saving">Saving...</span>
              <span v-else>Save Changes</span>
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Confirm Remove Connection Modal -->
    <Transition name="modal">
      <div v-if="showRemoveModal" class="modal-overlay" @click.self="showRemoveModal = false">
        <div class="modal-container modal-sm">
          <div class="modal-header">
            <h2 class="modal-title">Remove Connection</h2>
            <button @click="showRemoveModal = false" class="modal-close">
              <XMarkIcon class="w-5 h-5" />
            </button>
          </div>
          <div class="modal-body">
            <p class="confirm-text">
              Are you sure you want to remove <strong>{{ user.firstName }} {{ user.lastName }}</strong> from your connections?
            </p>
          </div>
          <div class="modal-footer">
            <button @click="showRemoveModal = false" class="btn-secondary">Cancel</button>
            <button @click="removeConnection" class="btn-danger" :disabled="actionLoading">
              Remove
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import {
  CheckCircleIcon,
  ExclamationCircleIcon,
  XMarkIcon,
  CameraIcon,
  PencilIcon,
  UserPlusIcon,
  UserMinusIcon,
  ClockIcon,
  CheckIcon,
  ChatBubbleLeftRightIcon,
  ChatBubbleLeftIcon,
  LockOpenIcon,
  InformationCircleIcon,
  EnvelopeIcon,
  PhoneIcon,
  BuildingOfficeIcon,
  MapPinIcon,
  SparklesIcon,
  HeartIcon,
  LinkIcon,
  GlobeAltIcon,
  UsersIcon,
  UserCircleIcon,
  AcademicCapIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// Refs
const loading = ref(true)
const actionLoading = ref(false)
const saving = ref(false)
const activeTab = ref('about')
const user = ref({})
const connections = ref([])
const connectionStatus = ref('NOT_CONNECTED')
const connectionId = ref(null)
const mutualConnections = ref({ count: 0, users: [] })
const showEditModal = ref(false)
const showRemoveModal = ref(false)

// File inputs
const avatarInput = ref(null)
const coverInput = ref(null)

// Toast
const showToast = ref(false)
const toastMessage = ref('')
const toastType = ref('success')

// Edit form
const editForm = ref({
  firstName: '',
  lastName: '',
  bio: '',
  phone: '',
  department: '',
  officeLocation: '',
  skillsText: '',
  interestsText: '',
  linkedinUrl: '',
  githubUrl: '',
  websiteUrl: ''
})

// Tabs config
const tabs = [
  { id: 'about', name: 'About', icon: InformationCircleIcon },
  { id: 'connections', name: 'Connections', icon: UsersIcon }
]

// Computed
const isOwnProfile = computed(() => {
  const profileId = route.params.id ? Number(route.params.id) : authStore.userId
  return profileId === authStore.userId
})

const isOnline = computed(() => {
  // Placeholder - would be from WebSocket presence
  return false
})

const coverStyle = computed(() => {
  if (user.value.coverPicture) {
    return { backgroundImage: `url(${getProfilePictureUrl(user.value.coverPicture)})` }
  }
  return {}
})

const stats = computed(() => ({
  connections: connections.value.length,
  studyGroups: 0,
  gpa: user.value.gpa || '0.00',
  courses: user.value.coursesCount || 0
}))

const hasSocialLinks = computed(() => {
  return user.value.linkedinUrl || user.value.githubUrl || user.value.websiteUrl
})

// Watch for route changes
watch(() => route.params.id, () => {
  loadProfile()
})

// Lifecycle
onMounted(() => {
  loadProfile()
})

// Methods
function showNotification(message, type = 'success') {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => { showToast.value = false }, 5000)
}

async function loadProfile() {
  try {
    loading.value = true
    const userId = route.params.id || authStore.userId

    // Load user profile
    const response = await api.getUserById(userId)
    user.value = response.data

    // Load connections
    try {
      const connectionsResponse = await api.getUserConnections(userId)
      // Extract user info from connections - filter for ACCEPTED only
      // API returns: requesterId, requesterName, receiverId, receiverName, status
      const acceptedConnections = (connectionsResponse.data || []).filter(conn => conn.status === 'ACCEPTED')
      connections.value = acceptedConnections.map(conn => {
        // Determine which user is the "other" user (not the profile being viewed)
        const isRequester = conn.requesterId === Number(userId)
        const otherId = isRequester ? conn.receiverId : conn.requesterId
        const otherName = isRequester ? conn.receiverName : conn.requesterName
        const otherEmail = isRequester ? conn.receiverEmail : conn.requesterEmail

        // Parse name into firstName/lastName
        const nameParts = (otherName || '').split(' ')
        const firstName = nameParts[0] || ''
        const lastName = nameParts.slice(1).join(' ') || ''

        return {
          id: otherId,
          firstName,
          lastName,
          email: otherEmail,
          username: otherEmail ? otherEmail.split('@')[0] : '',
          role: 'STUDENT', // Default role, could be fetched if needed
          profilePicture: null
        }
      }).slice(0, 20)
      console.log('Connections loaded:', connections.value)
    } catch (e) {
      console.error('Error loading connections:', e)
      connections.value = []
    }

    // If viewing other user's profile, check connection status and mutual connections
    if (!isOwnProfile.value) {
      await loadConnectionStatus()
      await loadMutualConnections()
    }

  } catch (error) {
    console.error('Error loading profile:', error)
    showNotification('Failed to load profile', 'error')
  } finally {
    loading.value = false
  }
}

async function loadConnectionStatus() {
  try {
    const userId = route.params.id
    const response = await api.getConnectionStatus(authStore.userId, userId)
    connectionStatus.value = response.data.status
    connectionId.value = response.data.connectionId
  } catch (error) {
    connectionStatus.value = 'NOT_CONNECTED'
  }
}

async function loadMutualConnections() {
  try {
    const userId = route.params.id
    const response = await api.getMutualConnections(authStore.userId, userId)
    mutualConnections.value = response.data
  } catch (error) {
    mutualConnections.value = { count: 0, users: [] }
  }
}

async function sendConnectionRequest() {
  try {
    actionLoading.value = true
    const userId = route.params.id
    await api.sendConnectionRequest(authStore.userId, userId)
    connectionStatus.value = 'PENDING_SENT'
    showNotification('Connection request sent!')
  } catch (error) {
    showNotification(error.userMessage || 'Failed to send request', 'error')
  } finally {
    actionLoading.value = false
  }
}

async function cancelRequest() {
  try {
    actionLoading.value = true
    await api.cancelConnectionRequest(connectionId.value, authStore.userId)
    connectionStatus.value = 'NOT_CONNECTED'
    connectionId.value = null
    showNotification('Request cancelled')
  } catch (error) {
    showNotification(error.userMessage || 'Failed to cancel request', 'error')
  } finally {
    actionLoading.value = false
  }
}

async function acceptRequest() {
  try {
    actionLoading.value = true
    await api.acceptConnectionRequest(connectionId.value, authStore.userId)
    connectionStatus.value = 'CONNECTED'
    showNotification('Connection accepted!')
    await loadProfile()
  } catch (error) {
    showNotification(error.userMessage || 'Failed to accept request', 'error')
  } finally {
    actionLoading.value = false
  }
}

async function rejectRequest() {
  try {
    actionLoading.value = true
    await api.rejectConnectionRequest(connectionId.value, authStore.userId)
    connectionStatus.value = 'REJECTED'
    connectionId.value = null
    showNotification('Request rejected')
  } catch (error) {
    showNotification(error.userMessage || 'Failed to reject request', 'error')
  } finally {
    actionLoading.value = false
  }
}

function confirmRemoveConnection() {
  showRemoveModal.value = true
}

async function removeConnection() {
  try {
    actionLoading.value = true
    await api.removeConnection(connectionId.value, authStore.userId)
    connectionStatus.value = 'NOT_CONNECTED'
    connectionId.value = null
    showRemoveModal.value = false
    showNotification('Connection removed')
    await loadProfile()
  } catch (error) {
    showNotification(error.userMessage || 'Failed to remove connection', 'error')
  } finally {
    actionLoading.value = false
  }
}

async function unblockUser() {
  try {
    actionLoading.value = true
    const userId = route.params.id
    await api.unblockUser(authStore.userId, userId)
    connectionStatus.value = 'NOT_CONNECTED'
    showNotification('User unblocked')
  } catch (error) {
    showNotification(error.userMessage || 'Failed to unblock user', 'error')
  } finally {
    actionLoading.value = false
  }
}

function goToConversation() {
  const userId = route.params.id
  router.push(`/messages/${userId}`)
}

function goToConversationWith(userId) {
  router.push(`/messages/${userId}`)
}

function viewProfile(userId) {
  router.push(`/profile/${userId}`)
}

// Edit Profile
function openEditModal() {
  editForm.value = {
    firstName: user.value.firstName || '',
    lastName: user.value.lastName || '',
    bio: user.value.bio || '',
    phone: user.value.phone || '',
    department: user.value.department || '',
    officeLocation: user.value.officeLocation || '',
    skillsText: (user.value.skills || []).join(', '),
    interestsText: (user.value.interests || []).join(', '),
    linkedinUrl: user.value.linkedinUrl || '',
    githubUrl: user.value.githubUrl || '',
    websiteUrl: user.value.websiteUrl || ''
  }
  showEditModal.value = true
}

function closeEditModal() {
  showEditModal.value = false
}

async function saveProfile() {
  try {
    saving.value = true
    const profileData = {
      firstName: editForm.value.firstName,
      lastName: editForm.value.lastName,
      bio: editForm.value.bio,
      phone: editForm.value.phone,
      department: editForm.value.department,
      officeLocation: editForm.value.officeLocation,
      skills: editForm.value.skillsText.split(',').map(s => s.trim()).filter(s => s),
      interests: editForm.value.interestsText.split(',').map(s => s.trim()).filter(s => s),
      linkedinUrl: editForm.value.linkedinUrl,
      githubUrl: editForm.value.githubUrl,
      websiteUrl: editForm.value.websiteUrl
    }

    await api.updateProfile(authStore.userId, profileData)
    showNotification('Profile updated successfully!')
    closeEditModal()
    await loadProfile()
  } catch (error) {
    showNotification(error.userMessage || 'Failed to update profile', 'error')
  } finally {
    saving.value = false
  }
}

// File uploads
function triggerAvatarUpload() {
  avatarInput.value?.click()
}

function triggerCoverUpload() {
  coverInput.value?.click()
}

async function handleAvatarUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  try {
    const response = await api.uploadProfilePicture(authStore.userId, file)
    const filePath = response.data.filePath
    await api.updateProfile(authStore.userId, { profilePicture: filePath })
    showNotification('Profile picture updated!')
    await loadProfile()
  } catch (error) {
    showNotification(error.userMessage || 'Failed to upload picture', 'error')
  }
}

async function handleCoverUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  try {
    // Reuse profile picture upload for cover (or create separate endpoint)
    const response = await api.uploadProfilePicture(authStore.userId, file)
    const filePath = response.data.filePath
    await api.updateProfile(authStore.userId, { coverPicture: filePath })
    showNotification('Cover image updated!')
    await loadProfile()
  } catch (error) {
    showNotification(error.userMessage || 'Failed to upload cover', 'error')
  }
}

// Utilities
function getProfilePictureUrl(path) {
  if (!path) return null
  if (path.startsWith('http')) return path
  return `http://localhost:8080/api/files/download?filePath=${encodeURIComponent(path)}`
}

function getInitials(firstName, lastName) {
  const first = firstName?.[0] || ''
  const last = lastName?.[0] || ''
  return (first + last).toUpperCase() || '?'
}

function formatRole(role) {
  const roles = {
    'STUDENT': 'Student',
    'FACULTY': 'Faculty',
    'ADMIN': 'Admin',
    'SUPER_ADMIN': 'Super Admin'
  }
  return roles[role] || role
}

function getRoleBadgeClass(role) {
  const classes = {
    'STUDENT': 'badge-student',
    'FACULTY': 'badge-faculty',
    'ADMIN': 'badge-admin',
    'SUPER_ADMIN': 'badge-superadmin'
  }
  return classes[role] || ''
}

function formatMemberSince(date) {
  if (!date) return 'N/A'
  const d = new Date(date)
  const month = d.toLocaleString('default', { month: 'short' })
  return `${month} ${d.getFullYear()}`
}
</script>

<style scoped>
/* =====================================================
   PROFILE PAGE - REFINED LIGHT THEME
   Aesthetic: Clean, Editorial, Sophisticated
   Typography: Plus Jakarta Sans (system font fallback)
   Color Palette: Soft whites, indigo accents, purple gradients
   ===================================================== */

/* Base Styles */
.profile-page {
  min-height: 100vh;
  padding: 24px;
  background: linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
}

/* Loading */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 16px;
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 3px solid #e2e8f0;
  border-top-color: #6366f1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

/* Profile Content */
.profile-content {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Profile Header Card */
.profile-header-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 10px 15px -3px rgba(0, 0, 0, 0.05);
  position: relative;
}

/* Decorative Pattern */
.card-pattern {
  position: absolute;
  inset: 0;
  overflow: hidden;
  pointer-events: none;
  z-index: 0;
}

.pattern-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.03;
}

.pattern-1 {
  width: 400px;
  height: 400px;
  background: #6366f1;
  top: -200px;
  right: -100px;
}

.pattern-2 {
  width: 300px;
  height: 300px;
  background: #a855f7;
  bottom: -150px;
  left: -100px;
}

.pattern-3 {
  width: 200px;
  height: 200px;
  background: #ec4899;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

/* Cover Image */
.cover-image {
  height: 200px;
  background: linear-gradient(135deg, #6366f1 0%, #a855f7 50%, #ec4899 100%);
  background-size: cover;
  background-position: center;
  position: relative;
  z-index: 1;
}

.cover-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 40%, rgba(255, 255, 255, 0.9) 100%);
}

.edit-cover-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 12px;
  color: #374151;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.edit-cover-btn:hover {
  background: #ffffff;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

/* Profile Info Section */
.profile-info-section {
  padding: 0 32px 28px;
  display: flex;
  gap: 28px;
  margin-top: -70px;
  position: relative;
  z-index: 2;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
}

.avatar-ring {
  padding: 5px;
  background: linear-gradient(135deg, #6366f1, #a855f7, #ec4899);
  border-radius: 24px;
}

.avatar {
  width: 130px;
  height: 130px;
  border-radius: 20px;
  background: linear-gradient(135deg, #f1f5f9, #e2e8f0);
  border: 5px solid #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.avatar.has-image {
  background: transparent;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-initials {
  font-size: 44px;
  font-weight: 700;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.online-indicator {
  position: absolute;
  bottom: 12px;
  right: 12px;
  width: 22px;
  height: 22px;
  background: #22c55e;
  border: 4px solid #ffffff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.4);
}

.edit-avatar-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  border: 3px solid #ffffff;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.edit-avatar-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.4);
}

/* Profile Header Content */
.profile-header-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding-top: 80px;
}

.profile-name-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.profile-name {
  font-size: 30px;
  font-weight: 800;
  color: #1e293b;
  margin: 0;
  letter-spacing: -0.02em;
}

.role-badge {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.badge-student {
  background: linear-gradient(135deg, #dbeafe, #e0e7ff);
  color: #3b82f6;
}

.badge-faculty {
  background: linear-gradient(135deg, #f3e8ff, #fae8ff);
  color: #a855f7;
}

.badge-admin {
  background: linear-gradient(135deg, #ffedd5, #fed7aa);
  color: #f97316;
}

.badge-superadmin {
  background: linear-gradient(135deg, #fce7f3, #fbcfe8);
  color: #ec4899;
}

.username {
  color: #64748b;
  font-size: 15px;
  margin: 0;
  font-weight: 500;
}

.bio {
  color: #475569;
  font-size: 14px;
  margin: 10px 0 0;
  max-width: 420px;
  line-height: 1.6;
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-primary,
.btn-secondary,
.btn-success,
.btn-danger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.btn-primary {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white;
  box-shadow: 0 4px 14px rgba(99, 102, 241, 0.35);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.45);
}

.btn-secondary {
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.btn-secondary:hover:not(:disabled) {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.btn-success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  color: white;
  box-shadow: 0 4px 14px rgba(34, 197, 94, 0.35);
}

.btn-success:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(34, 197, 94, 0.45);
}

.btn-danger {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 4px 14px rgba(239, 68, 68, 0.35);
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(239, 68, 68, 0.45);
}

.btn-primary:disabled,
.btn-secondary:disabled,
.btn-success:disabled,
.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* Stats Row */
.stats-row {
  display: flex;
  padding: 24px 32px;
  border-top: 1px solid #f1f5f9;
  gap: 12px;
  position: relative;
  z-index: 2;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 18px 12px;
  background: linear-gradient(135deg, #fafafa, #f5f5f5);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.25s ease;
  border: 1px solid transparent;
}

.stat-item:hover {
  background: #ffffff;
  border-color: #e2e8f0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.stat-value {
  font-size: 26px;
  font-weight: 800;
  color: #1e293b;
  letter-spacing: -0.02em;
}

.stat-highlight {
  background: linear-gradient(135deg, #6366f1, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  margin-top: 4px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Mutual Connections */
.mutual-connections {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 32px;
  border-top: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #fefce8, #fef3c7);
}

.mutual-avatars {
  display: flex;
}

.mutual-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  border: 3px solid #fefce8;
  margin-left: -10px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-size: 12px;
  font-weight: 700;
  color: white;
}

.mutual-avatar:first-child {
  margin-left: 0;
}

.mutual-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.mutual-text {
  color: #92400e;
  font-size: 14px;
  font-weight: 600;
}

/* Tabs Card */
.tabs-card {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 10px 15px -3px rgba(0, 0, 0, 0.05);
}

.tabs-nav {
  display: flex;
  padding: 0 28px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(180deg, #fafafa, #ffffff);
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 18px 24px;
  background: none;
  border: none;
  color: #94a3b8;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border-bottom: 3px solid transparent;
  margin-bottom: -1px;
}

.tab-btn:hover {
  color: #64748b;
}

.tab-btn.active {
  color: #6366f1;
  border-bottom-color: #6366f1;
}

.tab-icon {
  width: 20px;
  height: 20px;
}

.tab-content {
  padding: 28px;
}

/* About Tab */
.about-tab {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.section-icon {
  width: 22px;
  height: 22px;
  color: #6366f1;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  padding: 18px;
  background: linear-gradient(135deg, #fafafa, #f8fafc);
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  transition: all 0.2s ease;
}

.info-item:hover {
  border-color: #e2e8f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.info-icon {
  width: 22px;
  height: 22px;
  color: #94a3b8;
  flex-shrink: 0;
  margin-top: 2px;
}

.info-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 4px;
}

.info-value {
  display: block;
  font-size: 15px;
  font-weight: 500;
  color: #1e293b;
}

/* Tags */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  padding: 8px 16px;
  background: linear-gradient(135deg, #eef2ff, #e0e7ff);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #4f46e5;
  border: 1px solid #c7d2fe;
  transition: all 0.2s ease;
}

.skill-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(99, 102, 241, 0.15);
}

.interest-tag {
  padding: 8px 16px;
  background: linear-gradient(135deg, #fdf4ff, #fae8ff);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: #a855f7;
  border: 1px solid #e9d5ff;
  transition: all 0.2s ease;
}

.interest-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(168, 85, 247, 0.15);
}

/* Social Links */
.social-links {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.social-link {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border-radius: 14px;
  font-size: 14px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.25s ease;
}

.social-link.linkedin {
  background: linear-gradient(135deg, #eff6ff, #dbeafe);
  color: #0077b5;
  border: 1px solid #bfdbfe;
}

.social-link.linkedin:hover {
  background: #dbeafe;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 119, 181, 0.2);
}

.social-link.github {
  background: linear-gradient(135deg, #f3f4f6, #e5e7eb);
  color: #24292f;
  border: 1px solid #d1d5db;
}

.social-link.github:hover {
  background: #e5e7eb;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(36, 41, 47, 0.15);
}

.social-link.website {
  background: linear-gradient(135deg, #ecfdf5, #d1fae5);
  color: #059669;
  border: 1px solid #a7f3d0;
}

.social-link.website:hover {
  background: #d1fae5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.2);
}

/* Connections Tab */
.connections-tab {
  min-height: 200px;
}

.connections-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 18px;
}

.connection-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 18px;
  background: linear-gradient(135deg, #fafafa, #f8fafc);
  border: 1px solid #f1f5f9;
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.connection-card:hover {
  background: #ffffff;
  border-color: #e2e8f0;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.connection-avatar {
  width: 54px;
  height: 54px;
  border-radius: 14px;
  background: linear-gradient(135deg, #6366f1, #a855f7);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.connection-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.connection-avatar span {
  font-size: 18px;
  font-weight: 700;
  color: white;
}

.connection-info {
  flex: 1;
  min-width: 0;
}

.connection-name {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.connection-role {
  font-size: 13px;
  color: #64748b;
  margin: 4px 0 0;
  font-weight: 500;
}

.connection-action {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #eef2ff, #e0e7ff);
  border: none;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6366f1;
  cursor: pointer;
  transition: all 0.2s ease;
}

.connection-action:hover {
  background: linear-gradient(135deg, #6366f1, #4f46e5);
  color: white;
  transform: scale(1.05);
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
  text-align: center;
}

.empty-icon {
  width: 56px;
  height: 56px;
  color: #cbd5e1;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 18px;
  font-weight: 700;
  color: #475569;
  margin: 0 0 8px;
}

.empty-text {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 24px;
}

.modal-container {
  width: 100%;
  max-width: 580px;
  max-height: 90vh;
  background: #ffffff;
  border-radius: 24px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
}

.modal-sm {
  max-width: 420px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 28px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(180deg, #fafafa, #ffffff);
}

.modal-title {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  margin: 0;
}

.modal-close {
  width: 40px;
  height: 40px;
  background: #f1f5f9;
  border: none;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #e2e8f0;
  color: #1e293b;
}

.modal-body {
  padding: 28px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 14px;
  padding: 24px 28px;
  border-top: 1px solid #f1f5f9;
  background: linear-gradient(180deg, #ffffff, #fafafa);
}

/* Form Styles */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

.form-group {
  margin-bottom: 18px;
}

.form-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #475569;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 14px 18px;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 14px;
  color: #1e293b;
  font-size: 15px;
  transition: all 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #6366f1;
  background: #ffffff;
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
}

.form-input::placeholder {
  color: #94a3b8;
}

textarea.form-input {
  resize: vertical;
  min-height: 100px;
}

.form-section-title {
  font-size: 14px;
  font-weight: 700;
  color: #64748b;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 28px 0 18px;
  padding-top: 20px;
  border-top: 1px solid #f1f5f9;
}

.confirm-text {
  color: #475569;
  font-size: 15px;
  line-height: 1.7;
}

.confirm-text strong {
  color: #1e293b;
}

/* Toast */
.toast-notification {
  position: fixed;
  top: 24px;
  right: 24px;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 22px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  z-index: 200;
  border: 1px solid #e2e8f0;
}

.toast-notification.success {
  border-left: 4px solid #22c55e;
}

.toast-notification.error {
  border-left: 4px solid #ef4444;
}

.toast-content {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #1e293b;
  font-size: 14px;
  font-weight: 600;
}

.toast-icon {
  width: 22px;
  height: 22px;
}

.toast-notification.success .toast-icon {
  color: #22c55e;
}

.toast-notification.error .toast-icon {
  color: #ef4444;
}

.toast-close {
  background: none;
  border: none;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
  transition: color 0.2s ease;
}

.toast-close:hover {
  color: #64748b;
}

/* Transitions */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(100px);
}

.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.95) translateY(20px);
}

.hidden {
  display: none;
}

/* Responsive */
@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
  }

  .profile-info-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
    padding: 0 20px 24px;
  }

  .profile-header-content {
    flex-direction: column;
    align-items: center;
    padding-top: 24px;
    gap: 24px;
  }

  .profile-name-section {
    align-items: center;
  }

  .name-row {
    flex-direction: column;
    gap: 10px;
  }

  .profile-name {
    font-size: 24px;
  }

  .bio {
    text-align: center;
  }

  .action-buttons {
    flex-wrap: wrap;
    justify-content: center;
  }

  .stats-row {
    flex-wrap: wrap;
    padding: 18px;
  }

  .stat-item {
    min-width: calc(50% - 6px);
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .tabs-nav {
    padding: 0 16px;
    overflow-x: auto;
  }

  .tab-btn {
    padding: 14px 18px;
    white-space: nowrap;
  }

  .tab-content {
    padding: 20px;
  }
}
</style>
