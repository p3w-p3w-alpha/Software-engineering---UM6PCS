<template>
  <Modal v-model="isOpen" title="Create Study Group" size="large">
    <form @submit.prevent="createGroup" class="space-y-6">
      <!-- Group Name -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Group Name <span class="text-red-500">*</span>
        </label>
        <input
          v-model="formData.name"
          type="text"
          required
          placeholder="e.g., Calculus Study Group"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          maxlength="100"
        />
        <p class="mt-1 text-xs text-gray-500">
          {{ formData.name.length }}/100 characters
        </p>
      </div>

      <!-- Course Selection -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Course <span class="text-red-500">*</span>
        </label>
        <select
          v-model="formData.courseId"
          required
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">Select a course</option>
          <option v-for="course in courses" :key="course.id" :value="course.id">
            {{ course.code }} - {{ course.name }}
          </option>
        </select>
      </div>

      <!-- Description -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Description
        </label>
        <textarea
          v-model="formData.description"
          rows="4"
          placeholder="Describe the purpose of this study group..."
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          maxlength="500"
        ></textarea>
        <p class="mt-1 text-xs text-gray-500">
          {{ formData.description.length }}/500 characters
        </p>
      </div>

      <!-- Privacy Setting -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-3">
          Privacy <span class="text-red-500">*</span>
        </label>
        <div class="space-y-3">
          <label class="flex items-start p-4 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
            <input
              v-model="formData.isPrivate"
              :value="false"
              type="radio"
              class="mt-1 h-4 w-4 text-blue-600 focus:ring-blue-500"
            />
            <div class="ml-3">
              <div class="flex items-center">
                <span class="text-sm font-medium text-gray-900">Public</span>
                <span class="ml-2 inline-block px-2 py-0.5 text-xs bg-green-100 text-green-800 rounded-full">
                  Recommended
                </span>
              </div>
              <p class="mt-1 text-sm text-gray-600">
                Anyone can find and join this group without approval
              </p>
            </div>
          </label>

          <label class="flex items-start p-4 border border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50 transition-colors">
            <input
              v-model="formData.isPrivate"
              :value="true"
              type="radio"
              class="mt-1 h-4 w-4 text-blue-600 focus:ring-blue-500"
            />
            <div class="ml-3">
              <span class="text-sm font-medium text-gray-900">Private</span>
              <p class="mt-1 text-sm text-gray-600">
                Only people with an invite link can join this group
              </p>
            </div>
          </label>
        </div>
      </div>

      <!-- Maximum Members -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Maximum Members <span class="text-red-500">*</span>
        </label>
        <div class="flex items-center space-x-4">
          <input
            v-model.number="formData.maxMembers"
            type="range"
            min="2"
            max="50"
            step="1"
            class="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer"
          />
          <div class="flex items-center justify-center w-16 px-3 py-2 bg-blue-50 border border-blue-200 rounded-lg">
            <span class="text-sm font-semibold text-blue-700">{{ formData.maxMembers }}</span>
          </div>
        </div>
        <p class="mt-2 text-xs text-gray-500">
          Recommended: 5-10 members for effective collaboration
        </p>
      </div>

      <!-- Meeting Schedule (Optional) -->
      <div>
        <label class="block text-sm font-medium text-gray-700 mb-2">
          Meeting Schedule (Optional)
        </label>
        <input
          v-model="formData.meetingSchedule"
          type="text"
          placeholder="e.g., Every Tuesday at 3 PM in Library Room 201"
          class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          maxlength="200"
        />
        <p class="mt-1 text-xs text-gray-500">
          Let members know when and where you'll meet
        </p>
      </div>

      <!-- Error Message -->
      <div v-if="errorMessage" class="p-4 bg-red-50 border border-red-200 rounded-lg">
        <div class="flex">
          <svg class="h-5 w-5 text-red-600 mr-3" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd" />
          </svg>
          <p class="text-sm text-red-800">{{ errorMessage }}</p>
        </div>
      </div>

      <!-- Info Box -->
      <div class="p-4 bg-blue-50 border border-blue-200 rounded-lg">
        <div class="flex">
          <svg class="h-5 w-5 text-blue-600 mr-3 mt-0.5" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd" />
          </svg>
          <div class="flex-1">
            <h4 class="text-sm font-medium text-blue-900 mb-1">Study Group Guidelines</h4>
            <ul class="text-sm text-blue-800 space-y-1">
              <li>• Create groups for specific courses or topics</li>
              <li>• Be respectful and supportive of all members</li>
              <li>• Share resources and help each other succeed</li>
              <li>• Regular participation helps everyone learn better</li>
            </ul>
          </div>
        </div>
      </div>
    </form>

    <template #footer>
      <div class="flex items-center justify-end space-x-3">
        <button
          @click="closeModal"
          type="button"
          class="px-6 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition-colors"
        >
          Cancel
        </button>
        <button
          @click="createGroup"
          :disabled="!isFormValid || creating"
          class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
        >
          <LoadingSpinner v-if="creating" size="small" color="white" class="mr-2" />
          <span>{{ creating ? 'Creating...' : 'Create Study Group' }}</span>
        </button>
      </div>
    </template>
  </Modal>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import Modal from '../../components/Modal.vue'
import LoadingSpinner from '../../components/LoadingSpinner.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'group-created'])

const authStore = useAuthStore()

const creating = ref(false)
const errorMessage = ref('')
const courses = ref([])

const formData = ref({
  name: '',
  courseId: '',
  description: '',
  isPrivate: false,
  maxMembers: 10,
  meetingSchedule: ''
})

const isOpen = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isFormValid = computed(() => {
  return formData.value.name.trim().length > 0 &&
    formData.value.courseId !== '' &&
    formData.value.maxMembers >= 2 &&
    formData.value.maxMembers <= 50
})

// Watch for modal open to load courses
watch(isOpen, (newValue) => {
  if (newValue) {
    loadCourses()
  } else {
    resetForm()
  }
})

onMounted(() => {
  if (isOpen.value) {
    loadCourses()
  }
})

async function loadCourses() {
  try {
    // Load user's enrolled courses
    const enrollmentsResponse = await api.getStudentEnrollments(authStore.userId)
    const enrollments = enrollmentsResponse.data || []

    // Load course details
    const coursePromises = enrollments.map(e => api.getCourseById(e.courseId))
    const courseResponses = await Promise.all(coursePromises)
    courses.value = courseResponses.map(r => r.data)
  } catch (error) {
    console.error('Error loading courses:', error)
    errorMessage.value = 'Failed to load courses. Please try again.'
  }
}

async function createGroup() {
  if (!isFormValid.value || creating.value) return

  try {
    creating.value = true
    errorMessage.value = ''

    const groupData = {
      name: formData.value.name.trim(),
      courseId: formData.value.courseId,
      description: formData.value.description.trim(),
      isPrivate: formData.value.isPrivate,
      maxMembers: formData.value.maxMembers,
      meetingSchedule: formData.value.meetingSchedule.trim(),
      creatorId: authStore.userId,
      createdAt: new Date().toISOString()
    }

    const response = await api.createStudyGroup(groupData)
    const newGroup = response.data

    // Emit success
    emit('group-created', newGroup)

    // Close modal
    closeModal()

    alert('Study group created successfully!')
  } catch (error) {
    console.error('Error creating study group:', error)
    errorMessage.value = error.response?.data?.message || 'Failed to create study group. Please try again.'
  } finally {
    creating.value = false
  }
}

function closeModal() {
  isOpen.value = false
}

function resetForm() {
  formData.value = {
    name: '',
    courseId: '',
    description: '',
    isPrivate: false,
    maxMembers: 10,
    meetingSchedule: ''
  }
  errorMessage.value = ''
}
</script>

<style scoped>
/* Custom styling for range slider */
input[type="range"]::-webkit-slider-thumb {
  appearance: none;
  width: 20px;
  height: 20px;
  background: #3b82f6;
  cursor: pointer;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

input[type="range"]::-moz-range-thumb {
  width: 20px;
  height: 20px;
  background: #3b82f6;
  cursor: pointer;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

input[type="range"]:focus::-webkit-slider-thumb {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

input[type="range"]:focus::-moz-range-thumb {
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}
</style>
