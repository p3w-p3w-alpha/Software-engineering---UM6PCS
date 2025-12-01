<!--
  LineChart - wrapper for vue-chartjs line charts
  used for attendance trends and performance over time
  definately needed PointElement for the hover dots to work
-->

<template>
  <!-- Line chart for trend visualization -->
  <Line :data="chartData" :options="chartOptions" />
</template>

<script setup>
import { Line } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, LineElement, CategoryScale, LinearScale, PointElement } from 'chart.js'

// register chart components - dont forget PointElement!
ChartJS.register(Title, Tooltip, Legend, LineElement, CategoryScale, LinearScale, PointElement)

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

// setup chart configuration
const chartData = props.data
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,  // works for now
  ...props.options
}
</script>
