<!--
  BarChart - wrapper for vue-chartjs bar charts
  handles data visualization for student stats and grades
  the chartjs integration was wierd at first but works now
-->

<template>
  <!-- Bar chart component from vue-chartjs -->
  <Bar :data="chartData" :options="chartOptions" />
</template>

<script setup>
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

// register all teh chart components we need
ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)

// recieve data and options from parent component
const props = defineProps({
  data: {
    type: Object,
    required: true
  },
  options: {
    type: Object,
    default: () => ({})
  }
})

// setup chart data and options - merge with defaults
const chartData = props.data
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,  // this was important for responsive layout
  ...props.options
}
</script>
