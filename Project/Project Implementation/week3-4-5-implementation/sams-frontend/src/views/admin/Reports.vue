<template>
  <div class="p-6">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-900">Reports</h1>
      <p class="text-gray-600 mt-2">Generate and view system reports</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="report in reports"
        :key="report.id"
        class="glass-card p-6 hover:shadow-xl transition-all cursor-pointer"
      >
        <div class="flex items-center justify-between mb-4">
          <i :class="[report.icon, 'text-3xl']" :style="{ color: report.color }"></i>
          <button class="p-2 hover:bg-gray-100 rounded-lg">
            <i class="pi pi-ellipsis-v"></i>
          </button>
        </div>
        <h3 class="text-lg font-semibold mb-2">{{ report.title }}</h3>
        <p class="text-sm text-gray-600">{{ report.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const reports = ref([
  {
    id: 1,
    title: 'Student Enrollment',
    description: 'View enrollment statistics',
    icon: 'pi pi-users',
    color: '#6366f1'
  },
  {
    id: 2,
    title: 'Financial Report',
    description: 'Revenue and expenses',
    icon: 'pi pi-dollar',
    color: '#10b981'
  },
  {
    id: 3,
    title: 'Attendance Report',
    description: 'Student attendance data',
    icon: 'pi pi-calendar-check',
    color: '#f59e0b'
  },
  {
    id: 4,
    title: 'Grade Distribution',
    description: 'Grade statistics by course',
    icon: 'pi pi-chart-bar',
    color: '#8b5cf6'
  },
  {
    id: 5,
    title: 'Faculty Performance',
    description: 'Faculty metrics and KPIs',
    icon: 'pi pi-chart-line',
    color: '#ec4899'
  },
  {
    id: 6,
    title: 'Course Analytics',
    description: 'Course completion rates',
    icon: 'pi pi-book',
    color: '#3b82f6'
  }
])
</script>