<!--
  Reports view - admin dashboard for generating and downloading various system reports
  definately useful for exporting data in different formats
-->
<template>
  <div class="p-6">
    <div class="mb-6 flex justify-between items-center">
      <div>
        <!-- page header -->
        <h1 class="text-3xl font-bold text-gray-900">Reports</h1>
        <p class="text-gray-600 mt-2">Generate and download system reports</p>
      </div>
      <!-- button to navigate to advanced report generator -->
      <Button
        label="Advanced Report Generator"
        icon="pi pi-file-export"
        severity="success"
        @click="$router.push('/admin/reports/generator')"
      />
    </div>

    <!-- grid of available report types - recieve different formats --><div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="report in reports"
        :key="report.id"
        class="glass-card p-6 hover:shadow-xl transition-all cursor-pointer"
        @click="openReportDialog(report)"
      >
        <div class="flex items-center justify-between mb-4">
          <i :class="[report.icon, 'text-3xl']" :style="{ color: report.color }"></i>
          <span class="text-xs text-gray-500">{{ report.format }}</span>
        </div>
        <h3 class="text-lg font-semibold mb-2">{{ report.title }}</h3>
        <p class="text-sm text-gray-600 mb-4">{{ report.description }}</p>
        <Button
          :label="generating[report.id] ? 'Generating...' : 'Generate Report'"
          icon="pi pi-download"
          class="w-full"
          :loading="generating[report.id]"
          @click.stop="generateReport(report)"
        />
      </div>
    </div>

    <!-- Report Configuration Dialog -->
    <Dialog
      v-model:visible="showDialog"
      :header="selectedReport?.title"
      modal
      class="w-full max-w-2xl"
    >
      <div v-if="selectedReport" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Report Format</label>
          <Dropdown
            v-model="reportConfig.format"
            :options="formats"
            optionLabel="label"
            optionValue="value"
            placeholder="Select format"
            class="w-full"
          />
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Start Date</label>
            <Calendar
              v-model="reportConfig.startDate"
              dateFormat="yy-mm-dd"
              placeholder="Select start date"
              class="w-full"
            />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">End Date</label>
            <Calendar
              v-model="reportConfig.endDate"
              dateFormat="yy-mm-dd"
              placeholder="Select end date"
              class="w-full"
            />
          </div>
        </div>

        <div v-if="selectedReport.id === 'enrollment'">
          <label class="block text-sm font-medium text-gray-700 mb-2">Semester</label>
          <Dropdown
            v-model="reportConfig.semesterId"
            :options="semesters"
            optionLabel="name"
            optionValue="id"
            placeholder="Select semester"
            class="w-full"
          />
        </div>

        <div v-if="selectedReport.id === 'grades'">
          <label class="block text-sm font-medium text-gray-700 mb-2">Course</label>
          <Dropdown
            v-model="reportConfig.courseId"
            :options="courses"
            optionLabel="courseName"
            optionValue="id"
            placeholder="Select course"
            class="w-full"
            filter
          />
        </div>

        <div class="flex items-center gap-2">
          <Checkbox v-model="reportConfig.includeCharts" inputId="charts" binary />
          <label for="charts" class="text-sm text-gray-700">Include Charts and Visualizations</label>
        </div>

        <div class="flex justify-end gap-2 pt-4">
          <Button
            label="Cancel"
            severity="secondary"
            @click="showDialog = false"
          />
          <Button
            label="Generate & Download"
            icon="pi pi-download"
            :loading="generating[selectedReport.id]"
            @click="generateReportWithConfig"
          />
        </div>
      </div>
    </Dialog>

    <!-- Report Preview Dialog -->
    <Dialog
      v-model:visible="showPreview"
      header="Report Preview"
      modal
      maximizable
      class="w-full max-w-6xl"
    >
      <div v-if="previewData" class="space-y-4">
        <div class="bg-gray-50 p-4 rounded-lg">
          <h3 class="font-semibold mb-2">Report Summary</h3>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div>
              <p class="text-sm text-gray-600">Total Records</p>
              <p class="text-2xl font-bold">{{ previewData.totalRecords }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Date Range</p>
              <p class="text-sm font-semibold">
                {{ formatDate(previewData.startDate) }} - {{ formatDate(previewData.endDate) }}
              </p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Generated</p>
              <p class="text-sm font-semibold">{{ formatDate(new Date()) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">Format</p>
              <p class="text-sm font-semibold">{{ previewData.format }}</p>
            </div>
          </div>
        </div>

        <DataTable :value="previewData.data" paginator :rows="10" class="p-datatable-sm">
          <Column
            v-for="col in previewData.columns"
            :key="col.field"
            :field="col.field"
            :header="col.header"
          />
        </DataTable>

        <div class="flex justify-end gap-2">
          <Button
            label="Close"
            severity="secondary"
            @click="showPreview = false"
          />
          <Button
            label="Download Report"
            icon="pi pi-download"
            @click="downloadReport"
          />
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import Dialog from 'primevue/dialog'
import Dropdown from 'primevue/dropdown'
import Calendar from 'primevue/calendar'
import Checkbox from 'primevue/checkbox'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import api from '../../services/api'

const toast = useToast()

const reports = ref([
  {
    id: 'enrollment',
    title: 'Student Enrollment Report',
    description: 'Detailed enrollment statistics and trends',
    icon: 'pi pi-users',
    color: '#6366f1',
    format: 'PDF, Excel, CSV'
  },
  {
    id: 'financial',
    title: 'Financial Report',
    description: 'Revenue, expenses, and payment status',
    icon: 'pi pi-dollar',
    color: '#10b981',
    format: 'PDF, Excel'
  },
  {
    id: 'attendance',
    title: 'Attendance Report',
    description: 'Student attendance data and analytics',
    icon: 'pi pi-calendar-check',
    color: '#f59e0b',
    format: 'PDF, Excel, CSV'
  },
  {
    id: 'grades',
    title: 'Grade Distribution Report',
    description: 'Grade statistics by course and semester',
    icon: 'pi pi-chart-bar',
    color: '#8b5cf6',
    format: 'PDF, Excel'
  },
  {
    id: 'faculty',
    title: 'Faculty Performance Report',
    description: 'Faculty metrics, courses, and KPIs',
    icon: 'pi pi-chart-line',
    color: '#ec4899',
    format: 'PDF, Excel'
  },
  {
    id: 'course',
    title: 'Course Analytics Report',
    description: 'Course completion rates and statistics',
    icon: 'pi pi-book',
    color: '#3b82f6',
    format: 'PDF, Excel, CSV'
  }
])

const showDialog = ref(false)
const showPreview = ref(false)
const selectedReport = ref(null)
const generating = ref({})
const previewData = ref(null)

const reportConfig = ref({
  format: 'pdf',
  startDate: new Date(new Date().setMonth(new Date().getMonth() - 1)),
  endDate: new Date(),
  semesterId: null,
  courseId: null,
  includeCharts: true
})

const formats = ref([
  { label: 'PDF Document', value: 'pdf' },
  { label: 'Excel Spreadsheet', value: 'excel' },
  { label: 'CSV File', value: 'csv' }
])

const semesters = ref([])
const courses = ref([])

onMounted(async () => {
  await loadSemesters()
  await loadCourses()
})

async function loadSemesters() {
  try {
    const response = await api.getAllSemesters()
    semesters.value = response.data
  } catch (error) {
    console.error('Error loading semesters:', error)
  }
}

async function loadCourses() {
  try {
    const response = await api.getAllCourses()
    courses.value = response.data
  } catch (error) {
    console.error('Error loading courses:', error)
  }
}

function openReportDialog(report) {
  selectedReport.value = report
  showDialog.value = true

  // Reset config
  reportConfig.value = {
    format: 'pdf',
    startDate: new Date(new Date().setMonth(new Date().getMonth() - 1)),
    endDate: new Date(),
    semesterId: null,
    courseId: null,
    includeCharts: true
  }
}

async function generateReport(report) {
  generating.value[report.id] = true

  try {
    let data

    switch(report.id) {
      case 'enrollment':
        data = await generateEnrollmentReport()
        break
      case 'financial':
        data = await generateFinancialReport()
        break
      case 'attendance':
        data = await generateAttendanceReport()
        break
      case 'grades':
        data = await generateGradesReport()
        break
      case 'faculty':
        data = await generateFacultyReport()
        break
      case 'course':
        data = await generateCourseReport()
        break
    }

    // Show preview
    previewData.value = data
    showPreview.value = true

    toast.add({
      severity: 'success',
      summary: 'Report Generated',
      detail: `${report.title} generated successfully`,
      life: 3000
    })
  } catch (error) {
    console.error('Error generating report:', error)
    toast.add({
      severity: 'error',
      summary: 'Generation Failed',
      detail: 'Failed to generate report. Please try again.',
      life: 3000
    })
  } finally {
    generating.value[report.id] = false
  }
}

async function generateReportWithConfig() {
  if (selectedReport.value) {
    await generateReport(selectedReport.value)
    showDialog.value = false
  }
}

async function generateEnrollmentReport() {
  const response = await api.get('/reports/enrollments')
  const enrollments = response.data

  return {
    totalRecords: enrollments.length,
    startDate: reportConfig.value.startDate,
    endDate: reportConfig.value.endDate,
    format: reportConfig.value.format,
    columns: [
      { field: 'studentName', header: 'Student' },
      { field: 'courseName', header: 'Course' },
      { field: 'status', header: 'Status' },
      { field: 'enrollmentDate', header: 'Enrolled Date' }
    ],
    data: enrollments
  }
}

async function generateFinancialReport() {
  const response = await api.get('/reports/fees')
  const fees = response.data

  return {
    totalRecords: fees.length,
    startDate: reportConfig.value.startDate,
    endDate: reportConfig.value.endDate,
    format: reportConfig.value.format,
    columns: [
      { field: 'studentName', header: 'Student' },
      { field: 'amount', header: 'Amount' },
      { field: 'status', header: 'Status' },
      { field: 'dueDate', header: 'Due Date' }
    ],
    data: fees
  }
}

async function generateAttendanceReport() {
  const response = await api.get('/reports/attendance')
  const attendance = response.data

  return {
    totalRecords: attendance.length,
    startDate: reportConfig.value.startDate,
    endDate: reportConfig.value.endDate,
    format: reportConfig.value.format,
    columns: [
      { field: 'studentName', header: 'Student' },
      { field: 'courseName', header: 'Course' },
      { field: 'date', header: 'Date' },
      { field: 'status', header: 'Status' }
    ],
    data: attendance
  }
}

async function generateGradesReport() {
  const response = await api.get('/reports/grades')
  const grades = response.data

  return {
    totalRecords: grades.length,
    startDate: reportConfig.value.startDate,
    endDate: reportConfig.value.endDate,
    format: reportConfig.value.format,
    columns: [
      { field: 'studentName', header: 'Student' },
      { field: 'courseName', header: 'Course' },
      { field: 'gradeValue', header: 'Grade' },
      { field: 'gradePoints', header: 'Points' }
    ],
    data: grades
  }
}

async function generateFacultyReport() {
  const response = await api.get('/reports/users?role=FACULTY')
  const faculty = response.data

  return {
    totalRecords: faculty.length,
    startDate: reportConfig.value.startDate,
    endDate: reportConfig.value.endDate,
    format: reportConfig.value.format,
    columns: [
      { field: 'firstName', header: 'First Name' },
      { field: 'lastName', header: 'Last Name' },
      { field: 'email', header: 'Email' },
      { field: 'active', header: 'Status' }
    ],
    data: faculty
  }
}

async function generateCourseReport() {
  const response = await api.get('/reports/courses')
  const courses = response.data

  return {
    totalRecords: courses.length,
    startDate: reportConfig.value.startDate,
    endDate: reportConfig.value.endDate,
    format: reportConfig.value.format,
    columns: [
      { field: 'courseCode', header: 'Code' },
      { field: 'courseName', header: 'Course Name' },
      { field: 'credits', header: 'Credits' },
      { field: 'capacity', header: 'Capacity' }
    ],
    data: courses
  }
}

function downloadReport() {
  const format = reportConfig.value.format
  const reportName = `${selectedReport.value.id}_report_${new Date().getTime()}`

  if (format === 'csv') {
    downloadCSV(reportName)
  } else if (format === 'excel') {
    downloadExcel(reportName)
  } else {
    downloadPDF(reportName)
  }

  toast.add({
    severity: 'success',
    summary: 'Download Started',
    detail: `Downloading ${reportName}.${format}`,
    life: 3000
  })
}

function downloadCSV(filename) {
  if (!previewData.value || !previewData.value.data) {
    toast.add({
      severity: 'error',
      summary: 'Export Failed',
      detail: 'No data available to export',
      life: 3000
    })
    return
  }

  try {
    const csv = convertToCSV(previewData.value.data, previewData.value.columns)
    const BOM = '\uFEFF' // UTF-8 BOM for Excel compatibility
    const blob = new Blob([BOM + csv], { type: 'text/csv;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${filename}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    toast.add({
      severity: 'success',
      summary: 'Export Complete',
      detail: `Report saved as ${filename}.csv`,
      life: 3000
    })
  } catch (error) {
    console.error('CSV export error:', error)
    toast.add({
      severity: 'error',
      summary: 'Export Failed',
      detail: 'Failed to generate CSV file',
      life: 3000
    })
  }
}

function downloadExcel(filename) {
  // Generate CSV with Excel-friendly formatting
  if (!previewData.value || !previewData.value.data) {
    toast.add({
      severity: 'error',
      summary: 'Export Failed',
      detail: 'No data available to export',
      life: 3000
    })
    return
  }

  try {
    const csv = convertToCSV(previewData.value.data, previewData.value.columns)
    const BOM = '\uFEFF'
    const blob = new Blob([BOM + csv], { type: 'application/vnd.ms-excel;charset=utf-8' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${filename}.xls`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

    toast.add({
      severity: 'success',
      summary: 'Export Complete',
      detail: `Report saved as ${filename}.xls`,
      life: 3000
    })
  } catch (error) {
    console.error('Excel export error:', error)
    toast.add({
      severity: 'error',
      summary: 'Export Failed',
      detail: 'Failed to generate Excel file',
      life: 3000
    })
  }
}

function downloadPDF(filename) {
  // For PDF, create a print-friendly view
  if (!previewData.value || !previewData.value.data) {
    toast.add({
      severity: 'error',
      summary: 'Export Failed',
      detail: 'No data available to export',
      life: 3000
    })
    return
  }

  try {
    // Create a new window with printable content
    const printWindow = window.open('', '_blank')
    const html = generatePrintableHTML(previewData.value, selectedReport.value.title)
    printWindow.document.write(html)
    printWindow.document.close()
    printWindow.focus()

    // Trigger print dialog after content loads
    printWindow.onload = () => {
      printWindow.print()
    }

    toast.add({
      severity: 'info',
      summary: 'PDF Export',
      detail: 'Print dialog opened. Save as PDF from print options.',
      life: 5000
    })
  } catch (error) {
    console.error('PDF export error:', error)
    toast.add({
      severity: 'error',
      summary: 'Export Failed',
      detail: 'Failed to generate PDF',
      life: 3000
    })
  }
}

function generatePrintableHTML(data, title) {
  const rows = data.data.map(row =>
    `<tr>${data.columns.map(col => `<td style="padding: 8px; border: 1px solid #ddd;">${escapeHtml(row[col.field])}</td>`).join('')}</tr>`
  ).join('')

  const headers = data.columns.map(col =>
    `<th style="padding: 10px; border: 1px solid #ddd; background: #f5f5f5; font-weight: bold;">${col.header}</th>`
  ).join('')

  return `
    <!DOCTYPE html>
    <html>
    <head>
      <title>${title} - Report</title>
      <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; margin-bottom: 10px; }
        .meta { color: #666; font-size: 14px; margin-bottom: 20px; }
        table { border-collapse: collapse; width: 100%; }
        @media print {
          body { margin: 0; }
          table { font-size: 12px; }
        }
      </style>
    </head>
    <body>
      <h1>${title}</h1>
      <div class="meta">
        <p>Generated: ${new Date().toLocaleString()}</p>
        <p>Total Records: ${data.totalRecords}</p>
        <p>Date Range: ${formatDate(data.startDate)} - ${formatDate(data.endDate)}</p>
      </div>
      <table>
        <thead><tr>${headers}</tr></thead>
        <tbody>${rows}</tbody>
      </table>
    </body>
    </html>
  `
}

function escapeHtml(value) {
  if (value === null || value === undefined) return 'N/A'
  const str = String(value)
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function convertToCSV(data, columns) {
  if (!data || !columns) return ''

  const headers = columns.map(col => `"${col.header}"`).join(',')
  const rows = data.map(row =>
    columns.map(col => {
      let value = row[col.field]
      if (value === null || value === undefined) {
        return '""'
      }
      // Convert to string and escape quotes
      value = String(value).replace(/"/g, '""')
      // Wrap in quotes if contains comma, newline, or quotes
      if (value.includes(',') || value.includes('\n') || value.includes('"')) {
        return `"${value}"`
      }
      return `"${value}"`
    }).join(',')
  )
  return [headers, ...rows].join('\r\n')
}

function formatDate(date) {
  if (!date) return 'N/A'
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
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
