<!--
  HelloWorld - basic demo component from Vue scaffolding
  handles a simple counter and displays welcome messages
  this was just teh default template, kept for testing purposes
-->

<script setup>
import { ref } from 'vue'

// recieve message prop from parent
defineProps({
  msg: String,
})

// simple counter state - works for now
const count = ref(0)
</script>

<template>
  <!-- Main heading with passed prop -->
  <h1>{{ msg }}</h1>

  <!-- Counter demo card -->
  <div class="card">
    <button type="button" @click="count++">count is {{ count }}</button>
    <p>
      Edit
      <code>components/HelloWorld.vue</code> to test HMR
    </p>
  </div>

  <!-- Documentation links - TODO: fix later or remove -->
  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank"
      >create-vue</a
    >, the official Vue + Vite starter
  </p>
  <p>
    Learn more about IDE Support for Vue in the
    <a
      href="https://vuejs.org/guide/scaling-up/tooling.html#ide-support"
      target="_blank"
      >Vue Docs Scaling up Guide</a
    >.
  </p>
  <p class="read-the-docs">Click on the Vite and Vue logos to learn more</p>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
