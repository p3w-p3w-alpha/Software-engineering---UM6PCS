<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-indigo-50 to-purple-50">
    <div class="text-center">
      <div class="mb-8">
        <h1 class="text-9xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600">
          404
        </h1>
        <div class="mt-4">
          <i class="pi pi-exclamation-triangle text-6xl text-indigo-600 animate-bounce-subtle"></i>
        </div>
      </div>
      <h2 class="text-3xl font-bold text-gray-800 mb-4">Page Not Found</h2>
      <p class="text-gray-600 mb-8">The page you're looking for doesn't exist or has been moved.</p>
      <router-link to="/" class="btn-gradient px-8 py-3 inline-block rounded-xl">
        <i class="pi pi-home mr-2"></i>
        Go to Home
      </router-link>
    </div>
  </div>
</template>

<script setup>
// 404 Page
</script>

<style scoped>
.btn-gradient {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-gradient:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.4);
}
</style>