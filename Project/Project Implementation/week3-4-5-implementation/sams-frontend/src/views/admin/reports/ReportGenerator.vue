<template>
  <div class="report-generator p-6">
    <div class="mb-6">
      <h1 class="text-3xl font-bold text-gray-900 mb-2">Advanced Report Generator</h1>
      <p class="text-gray-600">Generate comprehensive CSV reports with customizable filters and options</p>
    </div>

    <!-- Report Type Selection -->
    <Card class="mb-6">
      <template #content>
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-2">Report Type</label>
            <Dropdown
              v-model="selectedReportType"
              :options="reportTypes"
              optionLabel="label"
              optionValue="value"
              placeholder="Select report type"
              class="w-full"
              @change="onReportTypeChange"
            >
              <template #option="slotProps">
                <div class="flex items-center gap-3">
                  <i :class="slotProps.option.icon" class="text-xl"></i>
                  <div>
                    <div class="font-semibold">{{ slotProps.option.label }}</div>
                    <div class="text-sm text-gray-600">{{ slotProps.option.description }}</div>
                  </div>
                </div>
              </template>
            </Dropdown>
          </div>

          <!-- Filters Section -->
          <div v-if="selectedReportType" class="border-t pt-4">
            <h3 class="text-lg font-semibold mb-4">Filters</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Date Range Filters -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Start Date</label>
                <Calendar
                  v-model="filters.startDate"
                  dateFormat="yy-mm-dd"
                  placeholder="Select start date"
                  class="w-full"
                  showIcon
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">End Date</label>
                <Calendar
                  v-model="filters.endDate"
                  dateFormat="yy-mm-dd"
                  placeholder="Select end date"
                  class="w-full"
                  showIcon
                />
              </div>

              <!-- Status Filter (for applicable reports) -->
              <div v-if="showStatusFilter">
                <label class="block text-sm font-medium text-gray-700 mb-2">Status</label>
                <MultiSelect
                  v-model="filters.statuses"
                  :options="getStatusOptions()"
                  placeholder="Select statuses"
                  class="w-full"
                  display="chip"
                />
              </div>

              <!-- Role Filter (for user reports) -->
              <div v-if="selectedReportType === 'all-users'">
                <label class="block text-sm font-medium text-gray-700 mb-2">Roles</label>
                <MultiSelect
                  v-model="filters.roles"
                  :options="['STUDENT', 'FACULTY', 'ADMIN', 'SUPER_ADMIN']"
                  placeholder="Select roles"
                  class="w-full"
                  display="chip"
                />
              </div>

              <!-- Course Filter (for enrollment/grade reports) -->
              <div v-if="showCourseFilter">
                <label class="block text-sm font-medium text-gray-700 mb-2">Course</label>
                <Dropdown
                  v-model="filters.courseId"
                  :options="courses"
                  optionLabel="courseName"
                  optionValue="id"
                  placeholder="All Courses"
                  class="w-full"
                  filter
                  showClear
                />
              </div>

              <!-- Semester Filter (for enrollment/payment reports) -->
              <div v-if="showSemesterFilter">
                <label class="block text-sm font-medium text-gray-700 mb-2">Semester</label>
                <Dropdown
                  v-model="filters.semesterId"
                  :options="semesters"
                  optionLabel="name"
                  optionValue="id"
                  placeholder="All Semesters"
                  class="w-full"
                  showClear
                />
              </div>
            </div>
          </div>

          <!-- Export Options -->
          <div v-if="selectedReportType" class="border-t pt-4">
            <h3 class="text-lg font-semibold mb-4">Export Options</h3>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <!-- Column Selection -->
              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-2">Select Columns to Export</label>
                <MultiSelect
                  v-model="exportOptions.selectedColumns"
                  :options="availableColumns"
                  optionLabel="label"
                  optionValue="value"
                  placeholder="Select columns"
                  class="w-full"
                  display="chip"
                  selectAll
                />
              </div>

              <!-- Date Format -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">Date Format</label>
                <Dropdown
                  v-model="exportOptions.dateFormat"
                  :options="dateFormatOptions"
                  optionLabel="label"
                  optionValue="value"
                  placeholder="Select date format"
                  class="w-full"
                />
              </div>

              <!-- CSV Delimiter -->
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-2">CSV Delimiter</label>
                <Dropdown
                  v-model="exportOptions.delimiter"
                  :options="delimiterOptions"
                  optionLabel="label"
                  optionValue="value"
                  placeholder="Select delimiter"
                  class="w-full"
                />
              </div>

              <!-- Additional Options -->
              <div class="md:col-span-2 space-y-2">
                <div class="flex items-center gap-2">
                  <Checkbox v-model="exportOptions.includeHeader" inputId="header" binary />
                  <label for="header" class="text-sm text-gray-700">Include header row</label>
                </div>
                <div class="flex items-center gap-2">
                  <Checkbox v-model="exportOptions.includeTimestamp" inputId="timestamp" binary />
                  <label for="timestamp" class="text-sm text-gray-700">Include generation timestamp</label>
                </div>
                <div class="flex items-center gap-2">
                  <Checkbox v-model="exportOptions.quoteStrings" inputId="quote" binary />
                  <label for="quote" class="text-sm text-gray-700">Quote string values</label>
                </div>
              </div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div v-if="selectedReportType" class="flex justify-between items-center pt-4 border-t">
            <div class="flex gap-2">
              <Button
                label="Preview Data"
                icon="pi pi-eye"
                severity="secondary"
                @click="previewReport"
                :loading="loadingPreview"
              />
              <Button
                label="Clear Filters"
                icon="pi pi-refresh"
                severity="secondary"
                outlined
                @click="clearFilters"
              />
            </div>
            <Button
              label="Generate & Download CSV"
              icon="pi pi-download"
              @click="generateReport"
              :loading="loadingReport"
            />
          </div>
        </div>
      </template>
    </Card>

    <!-- Data Preview -->
    <Card v-if="previewData" class="mb-6">
      <template #content>
        <div class="space-y-4">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-semibold">Data Preview (First 10 Rows)</h3>
            <div class="text-sm text-gray-600">
              Total Records: <span class="font-bold">{{ totalRecords }}</span>
            </div>
          </div>

          <DataTable
            :value="previewData"
            :paginator="false"
            class="p-datatable-sm"
            responsiveLayout="scroll"
            stripedRows
          >
            <Column
              v-for="col in previewColumns"
              :key="col.field"
              :field="col.field"
              :header="col.header"
              :sortable="true"
            >
              <template #body="slotProps">
                <span v-if="col.field.includes('Date') || col.field.includes('date')">
                  {{ formatDate(slotProps.data[col.field]) }}
                </span>
                <Tag
                  v-else-if="col.field === 'status' || col.field === 'active'"
                  :value="formatStatus(slotProps.data[col.field])"
                  :severity="getStatusSeverity(slotProps.data[col.field])"
                />
                <span v-else>{{ slotProps.data[col.field] }}</span>
              </template>
            </Column>
          </DataTable>
        </div>
      </template>
    </Card>

    <!-- Statistics Panel -->
    <div v-if="statistics" class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <Card>
        <template #content>
          <div class="text-center">
            <i class="pi pi-file text-4xl text-blue-500 mb-2"></i>
            <div class="text-2xl font-bold">{{ statistics.totalRecords }}</div>
            <div class="text-sm text-gray-600">Total Records</div>
          </div>
        </template>
      </Card>
      <Card>
        <template #content>
          <div class="text-center">
            <i class="pi pi-table text-4xl text-green-500 mb-2"></i>
            <div class="text-2xl font-bold">{{ statistics.selectedColumns }}</div>
            <div class="text-sm text-gray-600">Selected Columns</div>
          </div>
        </template>
      </Card>
      <Card>
        <template #content>
          <div class="text-center">
            <i class="pi pi-calendar text-4xl text-purple-500 mb-2"></i>
            <div class="text-2xl font-bold">{{ statistics.dateRange }}</div>
            <div class="text-sm text-gray-600">Date Range (Days)</div>
          </div>
        </template>
      </Card>
      <Card>
        <template #content>
          <div class="text-center">
            <i class="pi pi-filter text-4xl text-orange-500 mb-2"></i>
            <div class="text-2xl font-bold">{{ statistics.activeFilters }}</div>
            <div class="text-sm text-gray-600">Active Filters</div>
          </div>
        </template>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useToast } from 'primevue/usetoast'
import Card from 'primevue/card'
import Button from 'primevue/button'
import Dropdown from 'primevue/dropdown'
import MultiSelect from 'primevue/multiselect'
import Calendar from 'primevue/calendar'
import Checkbox from 'primevue/checkbox'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'
import api from '../../../services/api'

const toast = useToast()

// Report Types
const reportTypes = ref([
  {
    value: 'all-users',
    label: 'All Users',
    icon: 'pi pi-users',
    description: 'Complete list of all system users',
    endpoint: '/admin/users'
  },
  {
    value: 'students',
    label: 'Students Only',
    icon: 'pi pi-id-card',
    description: 'All student users',
    endpoint: '/users/role/STUDENT'
  },
  {
    value: 'faculty',
    label: 'Faculty Only',
    icon: 'pi pi-briefcase',
    description: 'All faculty members',
    endpoint: '/users/role/FACULTY'
  },
  {
    value: 'courses',
    label: 'All Courses',
    icon: 'pi pi-book',
    description: 'Complete course catalog',
    endpoint: '/courses'
  },
  {
    value: 'enrollments',
    label: 'Enrollments',
    icon: 'pi pi-user-plus',
    description: 'Student course enrollments',
    endpoint: '/reports/enrollments'
  },
  {
    value: 'payments',
    label: 'Payments',
    icon: 'pi pi-dollar',
    description: 'Payment records and transactions',
    endpoint: '/payments'
  },
  {
    value: 'attendance',
    label: 'Attendance Records',
    icon: 'pi pi-calendar-check',
    description: 'Attendance tracking data',
    endpoint: '/reports/attendance'
  },
  {
    value: 'grades',
    label: 'Grades',
    icon: 'pi pi-chart-bar',
    description: 'Student grades and assessments',
    endpoint: '/reports/grades'
  }
])

const selectedReportType = ref(null)
const loadingPreview = ref(false)
const loadingReport = ref(false)
const previewData = ref(null)
const totalRecords = ref(0)
const courses = ref([])
const semesters = ref([])

// Filters
const filters = ref({
  startDate: new Date(new Date().setMonth(new Date().getMonth() - 3)),
  endDate: new Date(),
  statuses: [],
  roles: [],
  courseId: null,
  semesterId: null
})

// Export Options
const exportOptions = ref({
  selectedColumns: [],
  dateFormat: 'YYYY-MM-DD',
  delimiter: ',',
  includeHeader: true,
  includeTimestamp: true,
  quoteStrings: true
})

const dateFormatOptions = ref([
  { label: 'YYYY-MM-DD (2024-11-28)', value: 'YYYY-MM-DD' },
  { label: 'MM/DD/YYYY (11/28/2024)', value: 'MM/DD/YYYY' },
  { label: 'DD/MM/YYYY (28/11/2024)', value: 'DD/MM/YYYY' },
  { label: 'DD-MMM-YYYY (28-Nov-2024)', value: 'DD-MMM-YYYY' },
  { label: 'Full Date Time', value: 'FULL' }
])

const delimiterOptions = ref([
  { label: 'Comma (,)', value: ',' },
  { label: 'Semicolon (;)', value: ';' },
  { label: 'Tab', value: '\t' },
  { label: 'Pipe (|)', value: '|' }
])

// Column Definitions
const columnDefinitions = {
  'all-users': [
    { value: 'id', label: 'User ID' },
    { value: 'firstName', label: 'First Name' },
    { value: 'lastName', label: 'Last Name' },
    { value: 'email', label: 'Email' },
    { value: 'role', label: 'Role' },
    { value: 'active', label: 'Active Status' },
    { value: 'createdAt', label: 'Created Date' }
  ],
  'students': [
    { value: 'id', label: 'Student ID' },
    { value: 'firstName', label: 'First Name' },
    { value: 'lastName', label: 'Last Name' },
    { value: 'email', label: 'Email' },
    { value: 'active', label: 'Active Status' },
    { value: 'createdAt', label: 'Enrollment Date' }
  ],
  'faculty': [
    { value: 'id', label: 'Faculty ID' },
    { value: 'firstName', label: 'First Name' },
    { value: 'lastName', label: 'Last Name' },
    { value: 'email', label: 'Email' },
    { value: 'active', label: 'Active Status' },
    { value: 'createdAt', label: 'Hire Date' }
  ],
  'courses': [
    { value: 'id', label: 'Course ID' },
    { value: 'courseCode', label: 'Course Code' },
    { value: 'courseName', label: 'Course Name' },
    { value: 'credits', label: 'Credits' },
    { value: 'capacity', label: 'Capacity' },
    { value: 'description', label: 'Description' }
  ],
  'enrollments': [
    { value: 'id', label: 'Enrollment ID' },
    { value: 'studentId', label: 'Student ID' },
    { value: 'studentName', label: 'Student Name' },
    { value: 'courseId', label: 'Course ID' },
    { value: 'courseName', label: 'Course Name' },
    { value: 'status', label: 'Status' },
    { value: 'enrollmentDate', label: 'Enrollment Date' },
    { value: 'finalGrade', label: 'Final Grade' }
  ],
  'payments': [
    { value: 'id', label: 'Payment ID' },
    { value: 'studentId', label: 'Student ID' },
    { value: 'studentName', label: 'Student Name' },
    { value: 'amount', label: 'Amount' },
    { value: 'status', label: 'Status' },
    { value: 'paymentDate', label: 'Payment Date' },
    { value: 'dueDate', label: 'Due Date' },
    { value: 'paymentMethod', label: 'Payment Method' }
  ],
  'attendance': [
    { value: 'id', label: 'Attendance ID' },
    { value: 'userId', label: 'User ID' },
    { value: 'userName', label: 'User Name' },
    { value: 'date', label: 'Date' },
    { value: 'status', label: 'Status' },
    { value: 'checkInTime', label: 'Check In Time' },
    { value: 'checkOutTime', label: 'Check Out Time' },
    { value: 'notes', label: 'Notes' }
  ],
  'grades': [
    { value: 'id', label: 'Grade ID' },
    { value: 'studentId', label: 'Student ID' },
    { value: 'studentName', label: 'Student Name' },
    { value: 'courseId', label: 'Course ID' },
    { value: 'courseName', label: 'Course Name' },
    { value: 'gradeValue', label: 'Grade' },
    { value: 'gradePoints', label: 'Grade Points' },
    { value: 'semester', label: 'Semester' }
  ]
}

const availableColumns = computed(() => {
  return columnDefinitions[selectedReportType.value] || []
})

const previewColumns = computed(() => {
  if (!exportOptions.value.selectedColumns.length) return []
  return availableColumns.value.filter(col =>
    exportOptions.value.selectedColumns.includes(col.value)
  ).map(col => ({
    field: col.value,
    header: col.label
  }))
})

const showStatusFilter = computed(() => {
  return ['enrollments', 'payments', 'attendance'].includes(selectedReportType.value)
})

const showCourseFilter = computed(() => {
  return ['enrollments', 'grades'].includes(selectedReportType.value)
})

const showSemesterFilter = computed(() => {
  return ['enrollments', 'payments'].includes(selectedReportType.value)
})

const statistics = computed(() => {
  if (!previewData.value) return null

  const daysDiff = filters.value.endDate && filters.value.startDate
    ? Math.ceil((filters.value.endDate - filters.value.startDate) / (1000 * 60 * 60 * 24))
    : 0

  const activeFiltersCount = [
    filters.value.statuses.length > 0,
    filters.value.roles.length > 0,
    filters.value.courseId !== null,
    filters.value.semesterId !== null,
    filters.value.startDate !== null,
    filters.value.endDate !== null
  ].filter(Boolean).length

  return {
    totalRecords: totalRecords.value,
    selectedColumns: exportOptions.value.selectedColumns.length,
    dateRange: daysDiff,
    activeFilters: activeFiltersCount
  }
})

onMounted(async () => {
  await loadCourses()
  await loadSemesters()
})

async function loadCourses() {
  try {
    const response = await api.getAllCourses()
    courses.value = response.data
  } catch (error) {
    console.error('Error loading courses:', error)
  }
}

async function loadSemesters() {
  try {
    const response = await api.getAllSemesters()
    semesters.value = response.data
  } catch (error) {
    console.error('Error loading semesters:', error)
  }
}

function onReportTypeChange() {
  // Reset filters and options
  clearFilters()

  // Auto-select all columns
  exportOptions.value.selectedColumns = availableColumns.value.map(col => col.value)
}

function clearFilters() {
  filters.value = {
    startDate: new Date(new Date().setMonth(new Date().getMonth() - 3)),
    endDate: new Date(),
    statuses: [],
    roles: [],
    courseId: null,
    semesterId: null
  }
  previewData.value = null
}

function getStatusOptions() {
  switch (selectedReportType.value) {
    case 'enrollments':
      return ['ENROLLED', 'COMPLETED', 'DROPPED', 'WITHDRAWN']
    case 'payments':
      return ['PENDING', 'SUBMITTED', 'APPROVED', 'REJECTED', 'OVERDUE']
    case 'attendance':
      return ['PRESENT', 'ABSENT', 'LATE', 'EXCUSED']
    default:
      return []
  }
}

async function previewReport() {
  if (!selectedReportType.value) {
    toast.add({
      severity: 'warn',
      summary: 'No Report Type',
      detail: 'Please select a report type first',
      life: 3000
    })
    return
  }

  if (exportOptions.value.selectedColumns.length === 0) {
    toast.add({
      severity: 'warn',
      summary: 'No Columns Selected',
      detail: 'Please select at least one column to preview',
      life: 3000
    })
    return
  }

  loadingPreview.value = true

  try {
    const reportConfig = reportTypes.value.find(r => r.value === selectedReportType.value)
    const response = await api.get(reportConfig.endpoint)
    let data = response.data

    // Apply filters
    data = applyFilters(data)

    totalRecords.value = data.length

    // Get preview (first 10 rows)
    previewData.value = data.slice(0, 10).map(item => {
      const filteredItem = {}
      exportOptions.value.selectedColumns.forEach(col => {
        filteredItem[col] = item[col]
      })
      return filteredItem
    })

    toast.add({
      severity: 'success',
      summary: 'Preview Generated',
      detail: `Showing first 10 of ${totalRecords.value} records`,
      life: 3000
    })
  } catch (error) {
    console.error('Error generating preview:', error)
    toast.add({
      severity: 'error',
      summary: 'Preview Failed',
      detail: error.response?.data?.message || 'Failed to generate preview',
      life: 3000
    })
  } finally {
    loadingPreview.value = false
  }
}

async function generateReport() {
  if (!selectedReportType.value) {
    toast.add({
      severity: 'warn',
      summary: 'No Report Type',
      detail: 'Please select a report type first',
      life: 3000
    })
    return
  }

  if (exportOptions.value.selectedColumns.length === 0) {
    toast.add({
      severity: 'warn',
      summary: 'No Columns Selected',
      detail: 'Please select at least one column to export',
      life: 3000
    })
    return
  }

  loadingReport.value = true

  try {
    const reportConfig = reportTypes.value.find(r => r.value === selectedReportType.value)
    const response = await api.get(reportConfig.endpoint)
    let data = response.data

    // Apply filters
    data = applyFilters(data)

    // Generate CSV
    const csv = convertToCSV(data)

    // Download file
    downloadCSV(csv, generateFilename())

    toast.add({
      severity: 'success',
      summary: 'Report Generated',
      detail: `Successfully exported ${data.length} records`,
      life: 3000
    })
  } catch (error) {
    console.error('Error generating report:', error)
    toast.add({
      severity: 'error',
      summary: 'Generation Failed',
      detail: error.response?.data?.message || 'Failed to generate report',
      life: 3000
    })
  } finally {
    loadingReport.value = false
  }
}

function applyFilters(data) {
  let filtered = [...data]

  // Date range filter
  if (filters.value.startDate || filters.value.endDate) {
    filtered = filtered.filter(item => {
      const dateField = item.createdAt || item.date || item.enrollmentDate || item.paymentDate
      if (!dateField) return true

      const itemDate = new Date(dateField)
      const start = filters.value.startDate ? new Date(filters.value.startDate) : null
      const end = filters.value.endDate ? new Date(filters.value.endDate) : null

      if (start && itemDate < start) return false
      if (end && itemDate > end) return false
      return true
    })
  }

  // Status filter
  if (filters.value.statuses.length > 0) {
    filtered = filtered.filter(item =>
      filters.value.statuses.includes(item.status)
    )
  }

  // Role filter
  if (filters.value.roles.length > 0) {
    filtered = filtered.filter(item =>
      filters.value.roles.includes(item.role)
    )
  }

  // Course filter
  if (filters.value.courseId) {
    filtered = filtered.filter(item =>
      item.courseId === filters.value.courseId
    )
  }

  // Semester filter
  if (filters.value.semesterId) {
    filtered = filtered.filter(item =>
      item.semesterId === filters.value.semesterId
    )
  }

  return filtered
}

function convertToCSV(data) {
  const columns = availableColumns.value.filter(col =>
    exportOptions.value.selectedColumns.includes(col.value)
  )

  let csv = ''

  // Add timestamp if requested
  if (exportOptions.value.includeTimestamp) {
    csv += `# Generated on ${new Date().toISOString()}\n`
    csv += `# Report Type: ${reportTypes.value.find(r => r.value === selectedReportType.value)?.label}\n`
    csv += `# Total Records: ${data.length}\n`
  }

  // Add header row
  if (exportOptions.value.includeHeader) {
    const headers = columns.map(col =>
      exportOptions.value.quoteStrings ? `"${col.label}"` : col.label
    )
    csv += headers.join(exportOptions.value.delimiter) + '\n'
  }

  // Add data rows
  data.forEach(row => {
    const values = columns.map(col => {
      let value = row[col.value]

      // Format dates
      if (col.value.includes('Date') || col.value.includes('date')) {
        value = formatDateForExport(value)
      }

      // Handle null/undefined
      if (value === null || value === undefined) {
        value = ''
      }

      // Quote strings if requested
      if (exportOptions.value.quoteStrings && typeof value === 'string') {
        value = `"${value.replace(/"/g, '""')}"` // Escape quotes
      }

      return value
    })
    csv += values.join(exportOptions.value.delimiter) + '\n'
  })

  return csv
}

function downloadCSV(csvContent, filename) {
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')

  if (navigator.msSaveBlob) {
    // IE 10+
    navigator.msSaveBlob(blob, filename)
  } else {
    link.href = URL.createObjectURL(blob)
    link.download = filename
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }
}

function generateFilename() {
  const reportType = selectedReportType.value
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').split('T')[0]
  return `${reportType}_report_${timestamp}.csv`
}

function formatDateForExport(date) {
  if (!date) return ''

  const d = new Date(date)
  if (isNaN(d.getTime())) return date

  switch (exportOptions.value.dateFormat) {
    case 'YYYY-MM-DD':
      return d.toISOString().split('T')[0]
    case 'MM/DD/YYYY':
      return `${d.getMonth() + 1}/${d.getDate()}/${d.getFullYear()}`
    case 'DD/MM/YYYY':
      return `${d.getDate()}/${d.getMonth() + 1}/${d.getFullYear()}`
    case 'DD-MMM-YYYY':
      const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
      return `${d.getDate()}-${months[d.getMonth()]}-${d.getFullYear()}`
    case 'FULL':
      return d.toISOString()
    default:
      return d.toISOString().split('T')[0]
  }
}

function formatDate(date) {
  if (!date) return 'N/A'
  const d = new Date(date)
  if (isNaN(d.getTime())) return date
  return d.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function formatStatus(status) {
  if (typeof status === 'boolean') {
    return status ? 'Active' : 'Inactive'
  }
  return status || 'N/A'
}

function getStatusSeverity(status) {
  if (typeof status === 'boolean') {
    return status ? 'success' : 'danger'
  }

  const statusUpper = String(status).toUpperCase()

  if (['ENROLLED', 'COMPLETED', 'APPROVED', 'PRESENT', 'ACTIVE'].includes(statusUpper)) {
    return 'success'
  }
  if (['PENDING', 'SUBMITTED', 'LATE'].includes(statusUpper)) {
    return 'warning'
  }
  if (['DROPPED', 'WITHDRAWN', 'REJECTED', 'ABSENT', 'OVERDUE', 'INACTIVE'].includes(statusUpper)) {
    return 'danger'
  }
  if (['EXCUSED'].includes(statusUpper)) {
    return 'info'
  }

  return 'secondary'
}
</script>

<style scoped>
.report-generator {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background: #f8f9fa;
}

:deep(.p-card) {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 12px;
}

:deep(.p-datatable) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.p-datatable .p-datatable-thead > tr > th) {
  background: #f8f9fa;
  font-weight: 600;
  color: #374151;
  border-bottom: 2px solid #e5e7eb;
}

:deep(.p-datatable .p-datatable-tbody > tr:hover) {
  background: #f3f4f6;
}

:deep(.p-dropdown),
:deep(.p-multiselect),
:deep(.p-calendar) {
  border-radius: 8px;
}

:deep(.p-button) {
  border-radius: 8px;
  font-weight: 500;
}
</style>
