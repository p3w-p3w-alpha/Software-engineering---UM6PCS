# ReportGenerator Component Structure

## Visual Layout

```
┌─────────────────────────────────────────────────────────────────┐
│                    Advanced Report Generator                     │
│  Generate comprehensive CSV reports with customizable filters    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ REPORT TYPE SELECTION CARD                                      │
├─────────────────────────────────────────────────────────────────┤
│ Report Type: [Dropdown ▼]                                       │
│              ┌──────────────────────────────────────┐           │
│              │ 📊 All Users                          │           │
│              │    Complete list of all system users  │           │
│              │ 🎓 Students Only                      │           │
│              │    All student users                  │           │
│              │ 💼 Faculty Only                       │           │
│              │    All faculty members                │           │
│              │ 📚 All Courses                        │           │
│              │    Complete course catalog            │           │
│              │ ➕ Enrollments                        │           │
│              │    Student course enrollments         │           │
│              │ 💵 Payments                           │           │
│              │    Payment records and transactions   │           │
│              │ ✓ Attendance Records                  │           │
│              │    Attendance tracking data           │           │
│              │ 📈 Grades                             │           │
│              │    Student grades and assessments     │           │
│              └──────────────────────────────────────┘           │
├─────────────────────────────────────────────────────────────────┤
│ FILTERS                                                          │
├─────────────────────────────────────────────────────────────────┤
│ Start Date: [📅 Calendar]     End Date: [📅 Calendar]          │
│ Status: [🔽 Multi-Select]     Course: [🔽 Dropdown]            │
│                                Semester: [🔽 Dropdown]          │
├─────────────────────────────────────────────────────────────────┤
│ EXPORT OPTIONS                                                   │
├─────────────────────────────────────────────────────────────────┤
│ Select Columns to Export:                                       │
│ [🔽 Multi-Select with chips]                                    │
│                                                                  │
│ Date Format: [YYYY-MM-DD ▼]   CSV Delimiter: [Comma (,) ▼]    │
│                                                                  │
│ ☑ Include header row                                           │
│ ☑ Include generation timestamp                                 │
│ ☑ Quote string values                                          │
├─────────────────────────────────────────────────────────────────┤
│ [👁 Preview Data] [🔄 Clear Filters]   [⬇ Generate & Download] │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│ DATA PREVIEW (First 10 Rows)            Total Records: 150      │
├─────────────────────────────────────────────────────────────────┤
│ ┌─────────┬───────────┬──────────┬─────────────────┬─────────┐ │
│ │ ID ↕    │ Name ↕    │ Email ↕  │ Status ↕        │ Date ↕  │ │
│ ├─────────┼───────────┼──────────┼─────────────────┼─────────┤ │
│ │ 1       │ John Doe  │ john@... │ ✓ Active        │ Nov 15  │ │
│ │ 2       │ Jane Smith│ jane@... │ ✓ Active        │ Nov 16  │ │
│ │ 3       │ Bob Wilson│ bob@...  │ ⚠ Pending       │ Nov 17  │ │
│ │ ...     │ ...       │ ...      │ ...             │ ...     │ │
│ └─────────┴───────────┴──────────┴─────────────────┴─────────┘ │
└─────────────────────────────────────────────────────────────────┘

┌──────────┬──────────┬──────────┬──────────┐
│ 📄 150   │ 📊 8     │ 📅 90    │ 🔍 3     │
│ Total    │ Selected │ Date     │ Active   │
│ Records  │ Columns  │ Range    │ Filters  │
└──────────┴──────────┴──────────┴──────────┘
```

## Component Tree Structure

```
ReportGenerator.vue
│
├── <template>
│   ├── Header Section
│   │   ├── Title: "Advanced Report Generator"
│   │   └── Description
│   │
│   ├── Main Card: Report Configuration
│   │   │
│   │   ├── Section 1: Report Type Selection
│   │   │   └── Dropdown with custom option template
│   │   │       ├── Icon
│   │   │       ├── Label
│   │   │       └── Description
│   │   │
│   │   ├── Section 2: Filters (v-if selectedReportType)
│   │   │   ├── Date Range Filters
│   │   │   │   ├── Start Date Calendar
│   │   │   │   └── End Date Calendar
│   │   │   │
│   │   │   ├── Conditional Filters
│   │   │   │   ├── Status MultiSelect (v-if showStatusFilter)
│   │   │   │   ├── Role MultiSelect (v-if all-users)
│   │   │   │   ├── Course Dropdown (v-if showCourseFilter)
│   │   │   │   └── Semester Dropdown (v-if showSemesterFilter)
│   │   │
│   │   ├── Section 3: Export Options
│   │   │   ├── Column Selection MultiSelect
│   │   │   ├── Date Format Dropdown
│   │   │   ├── CSV Delimiter Dropdown
│   │   │   └── Checkboxes
│   │   │       ├── Include Header
│   │   │       ├── Include Timestamp
│   │   │       └── Quote Strings
│   │   │
│   │   └── Section 4: Action Buttons
│   │       ├── Preview Data Button
│   │       ├── Clear Filters Button
│   │       └── Generate & Download Button
│   │
│   ├── Preview Card (v-if previewData)
│   │   ├── Header with Total Records
│   │   └── DataTable
│   │       └── Dynamic Columns
│   │           ├── Date formatting
│   │           ├── Status badges
│   │           └── Default display
│   │
│   └── Statistics Panel (v-if statistics)
│       ├── Total Records Card
│       ├── Selected Columns Card
│       ├── Date Range Card
│       └── Active Filters Card
│
├── <script setup>
│   │
│   ├── Imports
│   │   ├── Vue: ref, computed, onMounted
│   │   ├── PrimeVue: Components + useToast
│   │   └── Services: api
│   │
│   ├── State Variables
│   │   ├── reportTypes (8 types with metadata)
│   │   ├── selectedReportType
│   │   ├── loadingPreview
│   │   ├── loadingReport
│   │   ├── previewData
│   │   ├── totalRecords
│   │   ├── courses
│   │   ├── semesters
│   │   ├── filters
│   │   │   ├── startDate
│   │   │   ├── endDate
│   │   │   ├── statuses
│   │   │   ├── roles
│   │   │   ├── courseId
│   │   │   └── semesterId
│   │   ├── exportOptions
│   │   │   ├── selectedColumns
│   │   │   ├── dateFormat
│   │   │   ├── delimiter
│   │   │   ├── includeHeader
│   │   │   ├── includeTimestamp
│   │   │   └── quoteStrings
│   │   └── columnDefinitions (8 report types)
│   │
│   ├── Computed Properties
│   │   ├── availableColumns
│   │   ├── previewColumns
│   │   ├── showStatusFilter
│   │   ├── showCourseFilter
│   │   ├── showSemesterFilter
│   │   └── statistics
│   │
│   ├── Lifecycle Hooks
│   │   └── onMounted
│   │       ├── loadCourses()
│   │       └── loadSemesters()
│   │
│   ├── Data Loading Functions
│   │   ├── loadCourses()
│   │   └── loadSemesters()
│   │
│   ├── Event Handlers
│   │   ├── onReportTypeChange()
│   │   └── clearFilters()
│   │
│   ├── Utility Functions
│   │   ├── getStatusOptions()
│   │   └── applyFilters(data)
│   │
│   ├── Preview Functions
│   │   └── previewReport()
│   │       ├── Fetch data from API
│   │       ├── Apply filters
│   │       ├── Get first 10 rows
│   │       └── Update preview state
│   │
│   ├── Report Generation Functions
│   │   └── generateReport()
│   │       ├── Fetch data from API
│   │       ├── Apply filters
│   │       ├── Convert to CSV
│   │       └── Download file
│   │
│   ├── CSV Functions
│   │   ├── convertToCSV(data)
│   │   │   ├── Add timestamp
│   │   │   ├── Add headers
│   │   │   ├── Format rows
│   │   │   └── Return CSV string
│   │   └── downloadCSV(content, filename)
│   │       ├── Create Blob
│   │       ├── Create download link
│   │       └── Trigger download
│   │
│   ├── Formatting Functions
│   │   ├── formatDateForExport(date)
│   │   ├── formatDate(date)
│   │   ├── formatStatus(status)
│   │   ├── getStatusSeverity(status)
│   │   └── generateFilename()
│   │
│   └── Toast Notifications
│       ├── Success messages
│       ├── Warning messages
│       └── Error messages
│
└── <style scoped>
    ├── Container styles
    ├── PrimeVue customization (:deep)
    │   ├── Cards
    │   ├── DataTable
    │   ├── Dropdowns
    │   └── Buttons
    └── Responsive adjustments
```

## Data Flow Diagram

```
┌─────────────┐
│   User      │
└──────┬──────┘
       │ 1. Selects report type
       ▼
┌─────────────────────────────┐
│ onReportTypeChange()        │
│ - Clear filters             │
│ - Auto-select all columns   │
└──────────┬──────────────────┘
           │ 2. Component initializes
           ▼
    ┌──────────────┐
    │ onMounted()  │
    │ - Load data  │
    └──────┬───────┘
           │
     ┌─────┴──────┐
     │            │
     ▼            ▼
┌──────────┐  ┌──────────┐
│loadCourses│  │loadSemesters│
└─────┬────┘  └─────┬────┘
      │             │
      └──────┬──────┘
             │ 3. User applies filters
             ▼
    ┌────────────────┐
    │ Filters State  │
    │ (Reactive)     │
    └────────┬───────┘
             │ 4. User clicks Preview
             ▼
    ┌─────────────────┐
    │ previewReport() │
    └────────┬────────┘
             │
             ├─→ API Call
             │   └─→ api.get(endpoint)
             │
             ├─→ Apply Filters
             │   └─→ applyFilters(data)
             │
             ├─→ Slice Data
             │   └─→ data.slice(0, 10)
             │
             └─→ Update State
                 ├─→ previewData
                 └─→ totalRecords

             5. User clicks Generate
             ▼
    ┌──────────────────┐
    │ generateReport() │
    └────────┬─────────┘
             │
             ├─→ API Call
             │   └─→ api.get(endpoint)
             │
             ├─→ Apply Filters
             │   └─→ applyFilters(data)
             │
             ├─→ Convert to CSV
             │   └─→ convertToCSV(data)
             │       ├─→ Add timestamp
             │       ├─→ Add headers
             │       ├─→ Format rows
             │       └─→ Return CSV string
             │
             └─→ Download File
                 └─→ downloadCSV(csv, filename)
                     ├─→ Create Blob
                     ├─→ Create link
                     └─→ Trigger download
```

## State Management Flow

```
┌────────────────────────────────────────────────┐
│              Component State                    │
├────────────────────────────────────────────────┤
│                                                 │
│  selectedReportType ─┐                         │
│                      │                         │
│                      ├─→ Computed Properties   │
│  filters ────────────┤   - availableColumns    │
│                      │   - showStatusFilter    │
│  exportOptions ──────┤   - showCourseFilter    │
│                      │   - previewColumns      │
│  previewData ────────┤   - statistics          │
│                      │                         │
│  courses ────────────┘                         │
│  semesters                                      │
│                                                 │
└────────────────────────────────────────────────┘
         │
         │ Reactive Updates
         ▼
┌────────────────────────────────────────────────┐
│              Template (UI)                      │
├────────────────────────────────────────────────┤
│                                                 │
│  - Dropdowns render with options               │
│  - Filters show/hide based on report type      │
│  - Buttons enable/disable based on state       │
│  - Preview updates when data changes           │
│  - Statistics recalculate automatically        │
│                                                 │
└────────────────────────────────────────────────┘
```

## API Integration Points

```
Backend API Endpoints
│
├── Data Fetching
│   ├── GET /admin/users
│   ├── GET /users/role/STUDENT
│   ├── GET /users/role/FACULTY
│   ├── GET /courses
│   ├── GET /reports/enrollments
│   ├── GET /payments
│   ├── GET /reports/attendance
│   └── GET /reports/grades
│
└── Supporting Data
    ├── GET /courses (for filter)
    └── GET /semesters (for filter)

API Client (api.js)
│
├── Axios Instance
│   ├── Base URL: http://localhost:8080/api
│   ├── Request Interceptor (adds JWT token)
│   └── Response Interceptor (handles 401)
│
└── Generic Methods
    ├── get(url, config)
    ├── post(url, data, config)
    ├── put(url, data, config)
    └── delete(url, config)

ReportGenerator Component
│
└── Uses api.get() for all data fetching
```

## File Structure

```
sams-frontend/
│
├── src/
│   ├── views/
│   │   └── admin/
│   │       ├── Reports.vue (updated with nav button)
│   │       └── reports/
│   │           └── ReportGenerator.vue ← NEW COMPONENT
│   │
│   ├── router/
│   │   └── index.js (updated with route)
│   │
│   └── services/
│       └── api.js (existing API client)
│
└── Documentation/
    ├── REPORT_GENERATOR_GUIDE.md
    ├── REPORT_GENERATOR_IMPLEMENTATION_SUMMARY.md
    └── REPORT_GENERATOR_COMPONENT_STRUCTURE.md ← THIS FILE
```

## PrimeVue Components Used

```
Import Hierarchy:
│
├── Layout Components
│   └── Card (for sections)
│
├── Form Components
│   ├── Dropdown (single select)
│   ├── MultiSelect (multiple select)
│   ├── Calendar (date picker)
│   └── Checkbox (toggle options)
│
├── Action Components
│   └── Button (all actions)
│
├── Display Components
│   ├── DataTable (preview table)
│   ├── Column (table columns)
│   └── Tag (status badges)
│
└── Feedback Components
    └── Toast (notifications via useToast())
```

## CSS Architecture

```
Styling Layers:
│
├── Global Styles
│   └── Tailwind CSS utilities
│       ├── Spacing (p-6, mb-4, etc.)
│       ├── Layout (grid, flex, etc.)
│       ├── Typography (text-xl, font-bold, etc.)
│       └── Colors (text-gray-600, etc.)
│
├── Component Scoped Styles
│   └── <style scoped>
│       └── Container-specific styles
│
└── PrimeVue Deep Overrides
    └── :deep() selector
        ├── Card customization
        ├── DataTable styling
        ├── Form component styling
        └── Button styling
```

## Responsive Breakpoints

```
Mobile (< 768px)
├── grid-cols-1 (single column)
├── Full-width components
└── Stack everything vertically

Tablet (768px - 1024px)
├── md:grid-cols-2 (2 columns for filters)
├── Optimized spacing
└── Readable text sizes

Desktop (> 1024px)
├── lg:grid-cols-3 (3+ columns where appropriate)
├── Full table width
└── Optimal information density
```

## Event Flow

```
User Interaction Events:
│
├── @change="onReportTypeChange"
│   └── Dropdown: Report Type selected
│
├── @click="previewReport"
│   └── Button: Preview Data clicked
│
├── @click="generateReport"
│   └── Button: Generate & Download clicked
│
├── @click="clearFilters"
│   └── Button: Clear Filters clicked
│
└── v-model bindings (auto-update)
    ├── filters.startDate
    ├── filters.endDate
    ├── filters.statuses
    ├── exportOptions.selectedColumns
    └── etc.
```

## Key Features Summary

```
✅ 8 Report Types
✅ Real API Integration
✅ Advanced Filters (6 types)
✅ Export Options (10+ settings)
✅ Live Preview (10 rows)
✅ Statistics Panel (4 metrics)
✅ CSV Generation & Download
✅ Loading States
✅ Toast Notifications
✅ Responsive Design
✅ Error Handling
✅ PrimeVue UI
✅ Comprehensive Documentation
```

---

This structure document provides a complete visual and technical overview of the ReportGenerator component architecture, making it easy to understand and maintain.

