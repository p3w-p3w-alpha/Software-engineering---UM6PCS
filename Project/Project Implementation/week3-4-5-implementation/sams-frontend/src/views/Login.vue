<template>
  <div class="login-container min-h-screen relative overflow-hidden">
    <!-- Animated Background -->
    <div class="absolute inset-0 gradient-aurora opacity-80"></div>
    <div class="morphing-bg">
      <div class="floating-shapes">
        <div v-for="n in 6" :key="n" :class="`shape shape-${n}`"></div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="relative z-10 min-h-screen flex items-center justify-center px-4 py-8">
      <div class="w-full max-w-md animate-fade-in-scale">
        <!-- Logo Section with Animation -->
        <div class="text-center mb-8 animate-slide-in-up" style="animation-delay: 0.1s">
          <div class="logo-container inline-block relative">
            <div class="absolute inset-0 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-full blur-2xl opacity-50 animate-pulse-slow"></div>
            <div class="relative inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-indigo-600 to-purple-700 rounded-2xl shadow-2xl transform hover:rotate-12 transition-all duration-500">
              <i class="pi pi-graduation-cap text-3xl text-white"></i>
            </div>
          </div>
          <h1 class="text-5xl font-bold text-white mt-6 mb-2 text-gradient">SAMS</h1>
          <p class="text-white/80 text-lg font-medium">Student Academic Management System</p>
          <div class="flex justify-center gap-1 mt-3">
            <span v-for="n in 3" :key="n" class="w-2 h-2 bg-white/60 rounded-full animate-bounce-subtle" :style="`animation-delay: ${n * 0.2}s`"></span>
          </div>
        </div>

        <!-- Login Card with Glass Morphism -->
        <Card class="glass-card border-0 shadow-2xl animate-slide-in-up" style="animation-delay: 0.2s">
          <template #content>
            <form @submit.prevent="handleLogin" class="space-y-6 p-4">
              <!-- Welcome Message -->
              <div class="text-center mb-6">
                <h2 class="text-2xl font-bold bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">Welcome Back!</h2>
                <p class="text-gray-600 mt-1">Please sign in to continue</p>
              </div>

              <!-- Error Message with Animation -->
              <Transition name="slide-up">
                <Message v-if="errorMessage" severity="error" :closable="true" @close="errorMessage = ''" class="mb-4">
                  <template #icon>
                    <i class="pi pi-exclamation-triangle"></i>
                  </template>
                  {{ errorMessage }}
                </Message>
              </Transition>

              <!-- Username Field -->
              <div class="space-y-2">
                <label for="username" class="block text-sm font-semibold text-gray-700">
                  <i class="pi pi-user mr-2"></i>Username
                </label>
                <div class="relative group">
                  <InputText
                    id="username"
                    v-model="credentials.username"
                    type="text"
                    required
                    placeholder="Enter your username"
                    class="w-full modern-input pl-12 pr-4 py-3 text-lg"
                    :class="{'border-red-500': errors.username}"
                    @input="errors.username = ''"
                    @focus="fieldFocus.username = true"
                    @blur="fieldFocus.username = false"
                  />
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 transition-all duration-300"
                        :class="{'text-indigo-600': fieldFocus.username}">
                    <i class="pi pi-user text-xl"></i>
                  </span>
                  <div v-if="fieldFocus.username" class="absolute bottom-0 left-0 w-full h-0.5 bg-gradient-to-r from-indigo-600 to-purple-600 transform origin-left animate-slide-in-right"></div>
                </div>
                <small v-if="errors.username" class="text-red-600 flex items-center gap-1 animate-fade-in">
                  <i class="pi pi-exclamation-circle"></i>{{ errors.username }}
                </small>
              </div>

              <!-- Password Field -->
              <div class="space-y-2">
                <label for="password" class="block text-sm font-semibold text-gray-700">
                  <i class="pi pi-lock mr-2"></i>Password
                </label>
                <div class="relative group">
                  <Password
                    id="password"
                    v-model="credentials.password"
                    :feedback="false"
                    required
                    placeholder="Enter your password"
                    :toggleMask="true"
                    class="w-full"
                    inputClass="w-full modern-input pl-12 pr-12 py-3 text-lg"
                    :inputProps="{ autocomplete: 'current-password' }"
                    :class="{'border-red-500': errors.password}"
                    @input="errors.password = ''"
                    @focus="fieldFocus.password = true"
                    @blur="fieldFocus.password = false"
                  />
                  <span class="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 transition-all duration-300"
                        :class="{'text-indigo-600': fieldFocus.password}">
                    <i class="pi pi-lock text-xl"></i>
                  </span>
                  <div v-if="fieldFocus.password" class="absolute bottom-0 left-0 w-full h-0.5 bg-gradient-to-r from-indigo-600 to-purple-600 transform origin-left animate-slide-in-right"></div>
                </div>
                <small v-if="errors.password" class="text-red-600 flex items-center gap-1 animate-fade-in">
                  <i class="pi pi-exclamation-circle"></i>{{ errors.password }}
                </small>
              </div>

              <!-- Remember Me & Forgot Password -->
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <Checkbox v-model="rememberMe" inputId="remember" :binary="true" class="mr-2" />
                  <label for="remember" class="text-sm text-gray-600 cursor-pointer hover:text-gray-800">Remember me</label>
                </div>
                <a href="#" class="text-sm text-indigo-600 hover:text-indigo-800 font-medium transition-colors">
                  Forgot password?
                </a>
              </div>

              <!-- Submit Button with Gradient and Animation -->
              <Button
                type="submit"
                :loading="loading"
                :disabled="loading"
                label="Sign In"
                icon="pi pi-sign-in"
                class="w-full btn-gradient py-3 text-lg font-semibold transform hover:scale-[1.02] active:scale-[0.98] transition-all duration-300"
                :pt="{
                  root: { class: 'bg-gradient-to-r from-indigo-600 to-purple-700 border-0 rounded-xl shadow-lg hover:shadow-xl' },
                  label: { class: 'flex-1' }
                }"
              />

              <!-- OR Divider -->
              <div class="relative my-6">
                <div class="absolute inset-0 flex items-center">
                  <div class="w-full border-t border-gray-300"></div>
                </div>
                <div class="relative flex justify-center text-sm">
                  <span class="px-4 bg-white text-gray-500">Or continue with</span>
                </div>
              </div>

              <!-- Social Login Buttons -->
              <div class="grid grid-cols-3 gap-3">
                <Button
                  v-for="provider in socialProviders"
                  :key="provider.name"
                  :icon="`pi pi-${provider.icon}`"
                  :title="`Sign in with ${provider.name}`"
                  @click="socialLogin(provider.name)"
                  class="p-3 bg-white hover:bg-gray-50 border border-gray-300 rounded-xl transition-all duration-300 hover:shadow-lg transform hover:-translate-y-1"
                  :style="{ color: provider.color }"
                />
              </div>
            </form>

            <!-- Demo Accounts Section -->
            <Divider />
            <div class="px-4 pb-4">
              <Button
                @click="showTestAccounts = !showTestAccounts"
                icon="pi pi-info-circle"
                label="Demo Credentials"
                class="w-full p-button-text p-button-sm"
                :iconPos="showTestAccounts ? 'right' : 'left'"
              />

              <Transition name="slide-up">
                <div v-if="showTestAccounts" class="mt-4 space-y-2 animate-fade-in">
                  <div
                    v-for="account in testAccounts"
                    :key="account.username"
                    @click="fillCredentials(account)"
                    class="p-4 bg-gradient-to-r from-gray-50 to-gray-100 hover:from-indigo-50 hover:to-purple-50 rounded-xl cursor-pointer transition-all duration-300 transform hover:scale-[1.02] hover:shadow-lg group"
                  >
                    <div class="flex items-center justify-between">
                      <div>
                        <span class="inline-flex items-center gap-2">
                          <i :class="account.icon" class="text-indigo-600"></i>
                          <p class="font-semibold text-gray-900 group-hover:text-indigo-700">{{ account.role }}</p>
                        </span>
                        <p class="text-sm text-gray-600 mt-1">
                          <i class="pi pi-user text-xs mr-1"></i>{{ account.username }}
                          <span class="text-gray-400 ml-2">
                            <i class="pi pi-lock text-xs mr-1"></i>{{ account.password }}
                          </span>
                        </p>
                      </div>
                      <i class="pi pi-arrow-right text-gray-400 group-hover:text-indigo-600 transition-all duration-300 transform group-hover:translate-x-1"></i>
                    </div>
                  </div>
                </div>
              </Transition>
            </div>
          </template>
        </Card>

        <!-- Footer -->
        <div class="text-center mt-8 animate-fade-in" style="animation-delay: 0.4s">
          <p class="text-white/80 text-sm">
            © 2024 SAMS. All rights reserved.
          </p>
          <div class="flex justify-center gap-4 mt-3">
            <a href="#" class="text-white/60 hover:text-white transition-colors">
              <i class="pi pi-question-circle"></i> Help
            </a>
            <a href="#" class="text-white/60 hover:text-white transition-colors">
              <i class="pi pi-shield"></i> Privacy
            </a>
            <a href="#" class="text-white/60 hover:text-white transition-colors">
              <i class="pi pi-file-edit"></i> Terms
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import Button from 'primevue/button'
import Card from 'primevue/card'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Checkbox from 'primevue/checkbox'
import Message from 'primevue/message'
import Divider from 'primevue/divider'
import { useToast } from 'primevue/usetoast'

const router = useRouter()
const authStore = useAuthStore()
const toast = useToast()

const credentials = ref({ username: '', password: '' })
const loading = ref(false)
const errorMessage = ref('')
const showTestAccounts = ref(false)
const rememberMe = ref(false)
const errors = ref({ username: '', password: '' })
const fieldFocus = ref({ username: false, password: false })

const testAccounts = [
  {
    role: 'Super Admin',
    username: 'superadmin',
    password: 'Admin@123',
    icon: 'pi pi-shield'
  },
  {
    role: 'Student',
    username: 'student1',
    password: 'password123',
    icon: 'pi pi-graduation-cap'
  },
  {
    role: 'Faculty',
    username: 'faculty1',
    password: 'password123',
    icon: 'pi pi-book'
  }
]

const socialProviders = [
  { name: 'Google', icon: 'google', color: '#4285F4' },
  { name: 'GitHub', icon: 'github', color: '#333' },
  { name: 'Microsoft', icon: 'microsoft', color: '#0078D4' }
]

const validateForm = () => {
  errors.value = { username: '', password: '' }
  let isValid = true

  if (!credentials.value.username.trim()) {
    errors.value.username = 'Username is required'
    isValid = false
  }

  if (!credentials.value.password) {
    errors.value.password = 'Password is required'
    isValid = false
  }

  return isValid
}

const fillCredentials = (account) => {
  credentials.value.username = account.username
  credentials.value.password = account.password
  showTestAccounts.value = false
  errors.value = { username: '', password: '' }

  toast.add({
    severity: 'info',
    summary: 'Demo Account Selected',
    detail: `${account.role} credentials filled`,
    life: 3000
  })
}

const socialLogin = (provider) => {
  toast.add({
    severity: 'warn',
    summary: 'Coming Soon',
    detail: `${provider} authentication will be available soon`,
    life: 3000
  })
}

const handleLogin = async () => {
  if (!validateForm()) return

  loading.value = true
  errorMessage.value = ''

  try {
    const result = await authStore.login(credentials.value)

    if (result.success) {
      toast.add({
        severity: 'success',
        summary: 'Welcome!',
        detail: 'Login successful. Redirecting...',
        life: 2000
      })

      setTimeout(() => {
        if (authStore.isAdmin) router.push('/admin')
        else if (authStore.isStudent) router.push('/student')
        else if (authStore.isFaculty) router.push('/faculty')
      }, 500)
    } else {
      errorMessage.value = result.message || 'Invalid credentials'
      toast.add({
        severity: 'error',
        summary: 'Login Failed',
        detail: errorMessage.value,
        life: 5000
      })
    }
  } catch (error) {
    errorMessage.value = 'An error occurred. Please try again.'
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: errorMessage.value,
      life: 5000
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // Add entrance animation to body
  document.body.classList.add('overflow-hidden')
  setTimeout(() => {
    document.body.classList.remove('overflow-hidden')
  }, 1000)
})
</script>

<style scoped>
.login-container {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

/* Floating Shapes Animation */
.floating-shapes {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.shape {
  position: absolute;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  border-radius: 50%;
  animation: float-random 20s infinite ease-in-out;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  bottom: 10%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 30%;
  right: 30%;
  animation-delay: 6s;
}

.shape-5 {
  width: 70px;
  height: 70px;
  bottom: 30%;
  right: 20%;
  animation-delay: 8s;
}

.shape-6 {
  width: 90px;
  height: 90px;
  top: 70%;
  left: 40%;
  animation-delay: 10s;
}

@keyframes float-random {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(100px, -100px) rotate(90deg);
  }
  50% {
    transform: translate(-100px, 100px) rotate(180deg);
  }
  75% {
    transform: translate(50px, 50px) rotate(270deg);
  }
}

/* Custom Input Focus Effects */
:deep(.p-inputtext:focus),
:deep(.p-password-input:focus) {
  box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
  border-color: #6366f1;
}

/* Custom Button Styles */
:deep(.p-button) {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

:deep(.p-button:not(:disabled):hover) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

/* Glass Card Enhancement */
.glass-card {
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* Logo Container Animation */
.logo-container {
  animation: rotate-slow 20s linear infinite;
}

@keyframes rotate-slow {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* Additional Animations */
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

.animate-slide-in-up {
  animation: slideInUp 0.5s ease-out;
  animation-fill-mode: both;
}

.animate-fade-in-scale {
  animation: fadeInScale 0.5s ease-out;
}
</style>