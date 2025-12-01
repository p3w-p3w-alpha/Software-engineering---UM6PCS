<!--
  DoughnutChart - wrapper for doughnut/pie charts
  shows enrollment distribution, grade breakdowns, etc
  ArcElement is what makes teh circular segments work
-->

<template>
  <!-- Doughnut chart component -->
  <Doughnut :data="chartData" :options="chartOptions" />
</template>

<script setup>
import { Doughnut } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from 'chart.js'

// register required chart components for doughnut
ChartJS.register(Title, Tooltip, Legend, ArcElement)

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

// setup data and options
const chartData = props.data
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  ...props.options
}
</script>
