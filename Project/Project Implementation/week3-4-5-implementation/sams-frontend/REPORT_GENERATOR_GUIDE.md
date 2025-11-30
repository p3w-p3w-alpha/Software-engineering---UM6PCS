# Advanced Report Generator Component

## Overview

The **ReportGenerator.vue** component is a comprehensive CSV report generation tool for administrators. It provides advanced filtering, customizable export options, and real-time data preview capabilities.

## Location

```
/src/views/admin/reports/ReportGenerator.vue
```

## Features

### 1. Multiple Report Types

The component supports 8 different report types:

- **All Users**: Complete list of all system users
- **Students Only**: All student users
- **Faculty Only**: All faculty members
- **All Courses**: Complete course catalog
- **Enrollments**: Student course enrollments
- **Payments**: Payment records and transactions
- **Attendance Records**: Attendance tracking data
- **Grades**: Student grades and assessments

### 2. Advanced Filtering

#### Date Range Filters
- Start Date: Filter records from a specific date
- End Date: Filter records up to a specific date
- Default: Last 3 months

#### Status Filters (Context-Dependent)
- **Enrollments**: ENROLLED, COMPLETED, DROPPED, WITHDRAWN
- **Payments**: PENDING, SUBMITTED, APPROVED, REJECTED, OVERDUE
- **Attendance**: PRESENT, ABSENT, LATE, EXCUSED

#### Additional Filters
- **Role Filter** (All Users report): Filter by STUDENT, FACULTY, ADMIN, SUPER_ADMIN
- **Course Filter** (Enrollments/Grades): Filter by specific course
- **Semester Filter** (Enrollments/Payments): Filter by semester

### 3. Export Options

#### Column Selection
- Multi-select dropdown to choose which columns to export
- Auto-selects all columns by default
- Dynamic based on report type

#### Date Format Options
- `YYYY-MM-DD` (2024-11-28)
- `MM/DD/YYYY` (11/28/2024)
- `DD/MM/YYYY` (28/11/2024)
- `DD-MMM-YYYY` (28-Nov-2024)
- `Full Date Time` (ISO 8601)

#### CSV Delimiter Options
- Comma (,)
- Semicolon (;)
- Tab
- Pipe (|)

#### Additional Options
- **Include Header Row**: Add column headers to CSV
- **Include Timestamp**: Add generation metadata as comments
- **Quote Strings**: Wrap string values in quotes

### 4. Data Preview

- Shows first 10 rows of filtered data
- Interactive data table with sorting
- Status badges with color coding
- Formatted dates
- Total record count display

### 5. Statistics Panel

Displays key metrics:
- **Total Records**: Number of records after filtering
- **Selected Columns**: Number of columns to export
- **Date Range**: Number of days in date range
- **Active Filters**: Count of active filters

## API Endpoints Used

The component fetches data from these backend endpoints:

```javascript
// Report Data
GET /admin/users                  // All users
GET /users/role/{ROLE}           // Users by role
GET /courses                     // All courses
GET /reports/enrollments         // Enrollments
GET /payments                    // Payments
GET /reports/attendance          // Attendance
GET /reports/grades              // Grades

// Supporting Data
GET /courses                     // Course list for filters
GET /semesters                   // Semester list for filters
```

## Usage Guide

### Accessing the Component

1. **From Admin Menu**: Navigate to Reports → Advanced Report Generator
2. **From Reports Page**: Click the "Advanced Report Generator" button
3. **Direct URL**: `/admin/reports/generator`

### Generating a Report

1. **Select Report Type**
   - Choose from the dropdown menu
   - View description and icon for each type

2. **Apply Filters** (Optional)
   - Set date range
   - Select statuses, roles, courses, or semesters as applicable
   - Clear filters button to reset

3. **Configure Export Options**
   - Select columns to include
   - Choose date format
   - Select CSV delimiter
   - Toggle additional options

4. **Preview Data**
   - Click "Preview Data" to see first 10 rows
   - Review statistics panel
   - Verify data is correct

5. **Generate & Download**
   - Click "Generate & Download CSV"
   - File downloads automatically
   - Filename format: `{report-type}_report_{date}.csv`

## CSV Output Format

### With Timestamp Enabled

```csv
# Generated on 2024-11-28T10:30:00.000Z
# Report Type: Students Only
# Total Records: 150
"Student ID","First Name","Last Name","Email","Active Status","Enrollment Date"
1,"John","Doe","john.doe@example.com","Active","2024-01-15"
2,"Jane","Smith","jane.smith@example.com","Active","2024-01-16"
...
```

### Without Timestamp

```csv
"Student ID","First Name","Last Name","Email","Active Status","Enrollment Date"
1,"John","Doe","john.doe@example.com","Active","2024-01-15"
2,"Jane","Smith","jane.smith@example.com","Active","2024-01-16"
...
```

## Column Definitions by Report Type

### All Users
- User ID, First Name, Last Name, Email, Role, Active Status, Created Date

### Students Only / Faculty Only
- ID, First Name, Last Name, Email, Active Status, Enrollment/Hire Date

### All Courses
- Course ID, Course Code, Course Name, Credits, Capacity, Description

### Enrollments
- Enrollment ID, Student ID, Student Name, Course ID, Course Name, Status, Enrollment Date, Final Grade

### Payments
- Payment ID, Student ID, Student Name, Amount, Status, Payment Date, Due Date, Payment Method

### Attendance Records
- Attendance ID, User ID, User Name, Date, Status, Check In Time, Check Out Time, Notes

### Grades
- Grade ID, Student ID, Student Name, Course ID, Course Name, Grade, Grade Points, Semester

## Component Architecture

### State Management
- Vue 3 Composition API with `ref()` and `computed()`
- Reactive filters and export options
- Loading states for preview and generation

### Data Flow
1. User selects report type
2. Component fetches data from API
3. Filters applied to data
4. Preview generated (first 10 rows)
5. Full CSV generated on download

### CSV Generation
- Custom `convertToCSV()` function
- Handles date formatting
- Escapes special characters
- Supports custom delimiters

### Download Mechanism
- Creates Blob from CSV string
- Uses browser download API
- Supports IE 10+ with fallback

## Error Handling

The component includes comprehensive error handling:

- **No Report Type**: Warning toast if user tries to preview/generate without selecting type
- **No Columns Selected**: Warning toast if no columns are selected
- **API Errors**: Error toast with specific error message
- **Date Validation**: Automatically handles invalid dates

## Toast Notifications

### Success Messages
- "Preview Generated" - When preview loads successfully
- "Report Generated" - When CSV is generated
- "Download Started" - When download begins

### Warning Messages
- "No Report Type" - No report type selected
- "No Columns Selected" - No columns selected for export

### Error Messages
- "Preview Failed" - Preview generation error
- "Generation Failed" - CSV generation error

## Responsive Design

- Mobile-friendly layout
- Responsive grid for filters (1 column on mobile, 2 on desktop)
- Scrollable data tables on small screens
- Touch-friendly controls

## Styling

### Design Features
- Clean, modern UI with PrimeVue components
- Card-based layout with shadows
- Color-coded status badges
- Smooth hover effects
- Professional data table styling

### Color Scheme
- Primary: Blues and purples
- Success: Green
- Warning: Orange
- Danger: Red
- Info: Light blue

## Performance Considerations

- **Lazy Loading**: Component loaded only when needed
- **Preview Limit**: Only 10 rows shown in preview
- **Efficient Filtering**: In-memory filtering for fast results
- **Blob Downloads**: Efficient file generation without server load

## Browser Compatibility

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+
- IE 10+ (with polyfills)

## Future Enhancements

Potential features for future versions:

1. **Excel Export**: Native Excel file generation
2. **PDF Export**: PDF report generation with charts
3. **Scheduled Reports**: Auto-generate reports on schedule
4. **Email Delivery**: Email reports to recipients
5. **Report Templates**: Save filter configurations
6. **Custom Queries**: Advanced SQL-like queries
7. **Data Visualization**: Charts and graphs in preview
8. **Bulk Operations**: Generate multiple reports at once

## Troubleshooting

### Issue: Preview shows no data
**Solution**: Check date range filters - they may be too restrictive

### Issue: Download doesn't work
**Solution**: Check browser pop-up blocker settings

### Issue: CSV has incorrect encoding
**Solution**: Open CSV in spreadsheet app using UTF-8 encoding

### Issue: Dates appear as numbers
**Solution**: Format cells as date in spreadsheet application

## Security Considerations

- **Authorization**: Only accessible to ADMIN and SUPER_ADMIN roles
- **Authentication**: Requires valid JWT token
- **Data Filtering**: Server-side data already filtered by permissions
- **No SQL Injection**: Uses parameterized API calls

## Testing

### Manual Testing Steps

1. **Report Type Selection**
   - Select each report type
   - Verify columns update correctly
   - Verify filters show/hide appropriately

2. **Filtering**
   - Test date range filtering
   - Test status filtering
   - Test course/semester filtering
   - Test filter combinations

3. **Export Options**
   - Test column selection
   - Test different date formats
   - Test different delimiters
   - Test toggle options

4. **Preview**
   - Generate preview for each report type
   - Verify data accuracy
   - Verify statistics panel

5. **Download**
   - Generate CSV for each report type
   - Verify file downloads
   - Verify CSV format in text editor
   - Open in spreadsheet application

## Support

For issues or questions:
- Check browser console for errors
- Review API endpoint responses
- Verify user has admin permissions
- Check backend logs for API errors

## Version History

- **v1.0.0** (2024-11-28): Initial release
  - 8 report types
  - Advanced filtering
  - CSV export with customization
  - Data preview
  - Statistics panel

---

**Component Created**: November 28, 2024
**Location**: `/src/views/admin/reports/ReportGenerator.vue`
**Route**: `/admin/reports/generator`
**Access Level**: ADMIN, SUPER_ADMIN
