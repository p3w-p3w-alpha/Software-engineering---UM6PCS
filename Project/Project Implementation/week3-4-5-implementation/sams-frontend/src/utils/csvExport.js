/**
 * CSV Export Utility
 * Handles conversion of JSON data to CSV format and file downloads
 */

/**
 * Escapes special characters in CSV fields
 * @param {string} field - The field value to escape
 * @returns {string} - Escaped field value
 */
function escapeCSVField(field) {
  if (field === null || field === undefined) {
    return ''
  }

  const stringValue = String(field)

  // If field contains comma, double quote, newline, or carriage return, wrap in quotes
  if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n') || stringValue.includes('\r')) {
    // Escape double quotes by doubling them
    return `"${stringValue.replace(/"/g, '""')}"`
  }

  return stringValue
}

/**
 * Formats a value based on its type
 * @param {*} value - The value to format
 * @param {string} dateFormat - Date format option ('ISO', 'US', 'EU')
 * @returns {string} - Formatted value
 */
function formatValue(value, dateFormat = 'ISO') {
  if (value === null || value === undefined) {
    return ''
  }

  // Handle Date objects and date strings
  if (value instanceof Date || (typeof value === 'string' && !isNaN(Date.parse(value)) && value.includes('-'))) {
    const date = value instanceof Date ? value : new Date(value)

    switch (dateFormat) {
      case 'US':
        return date.toLocaleDateString('en-US')
      case 'EU':
        return date.toLocaleDateString('en-GB')
      case 'ISO':
      default:
        return date.toISOString().split('T')[0]
    }
  }

  // Handle boolean values
  if (typeof value === 'boolean') {
    return value ? 'Yes' : 'No'
  }

  // Handle objects (convert to JSON string)
  if (typeof value === 'object') {
    return JSON.stringify(value)
  }

  return String(value)
}

/**
 * Converts JSON data to CSV format
 * @param {Array} data - Array of objects to convert
 * @param {Array} columns - Column definitions (optional)
 * @param {Object} options - Export options
 * @returns {string} - CSV formatted string
 */
export function jsonToCSV(data, columns = null, options = {}) {
  const {
    dateFormat = 'ISO',
    includeHeaders = true,
    encoding = 'utf-8',
    delimiter = ',',
    lineBreak = '\n'
  } = options

  if (!data || data.length === 0) {
    return ''
  }

  // If columns not provided, extract from first data item
  let csvColumns = columns
  if (!csvColumns) {
    const firstItem = data[0]
    csvColumns = Object.keys(firstItem).map(key => ({
      field: key,
      header: key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1').trim()
    }))
  }

  const csvRows = []

  // Add headers
  if (includeHeaders) {
    const headers = csvColumns.map(col => escapeCSVField(col.header))
    csvRows.push(headers.join(delimiter))
  }

  // Add data rows
  data.forEach(row => {
    const values = csvColumns.map(col => {
      const rawValue = row[col.field]
      const formattedValue = formatValue(rawValue, dateFormat)
      return escapeCSVField(formattedValue)
    })
    csvRows.push(values.join(delimiter))
  })

  return csvRows.join(lineBreak)
}

/**
 * Downloads CSV data as a file
 * @param {string} csvContent - CSV formatted string
 * @param {string} filename - Filename for download
 * @param {string} encoding - Character encoding (utf-8, utf-8-bom, etc.)
 */
export function downloadCSV(csvContent, filename = 'export.csv', encoding = 'utf-8') {
  let content = csvContent

  // Add BOM for UTF-8 if specified
  if (encoding === 'utf-8-bom') {
    content = '\ufeff' + content
  }

  // Create blob with appropriate type
  const blob = new Blob([content], {
    type: 'text/csv;charset=' + (encoding === 'utf-8-bom' ? 'utf-8' : encoding)
  })

  // Create download link
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename.endsWith('.csv') ? filename : `${filename}.csv`

  // Trigger download
  document.body.appendChild(link)
  link.click()

  // Cleanup
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/**
 * Exports data to CSV and triggers download
 * @param {Array} data - Array of objects to export
 * @param {string} filename - Filename for download
 * @param {Array} columns - Column definitions (optional)
 * @param {Object} options - Export options
 */
export function exportToCSV(data, filename, columns = null, options = {}) {
  const csvContent = jsonToCSV(data, columns, options)
  downloadCSV(csvContent, filename, options.encoding)
}

/**
 * Generates a filename with timestamp
 * @param {string} prefix - Filename prefix
 * @param {string} extension - File extension (default: 'csv')
 * @returns {string} - Generated filename
 */
export function generateFilename(prefix = 'export', extension = 'csv') {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').split('T')[0]
  const time = new Date().toTimeString().split(' ')[0].replace(/:/g, '-')
  return `${prefix}_${timestamp}_${time}.${extension}`
}

/**
 * Filters data based on date range
 * @param {Array} data - Data array to filter
 * @param {string} dateField - Field name containing date
 * @param {Date} startDate - Start date
 * @param {Date} endDate - End date
 * @returns {Array} - Filtered data
 */
export function filterByDateRange(data, dateField, startDate, endDate) {
  if (!startDate && !endDate) {
    return data
  }

  return data.filter(item => {
    const itemDate = new Date(item[dateField])

    if (startDate && itemDate < startDate) {
      return false
    }

    if (endDate && itemDate > endDate) {
      return false
    }

    return true
  })
}

/**
 * Selects specific columns from data
 * @param {Array} data - Data array
 * @param {Array} selectedFields - Array of field names to include
 * @returns {Array} - Data with only selected fields
 */
export function selectColumns(data, selectedFields) {
  return data.map(item => {
    const filtered = {}
    selectedFields.forEach(field => {
      if (item.hasOwnProperty(field)) {
        filtered[field] = item[field]
      }
    })
    return filtered
  })
}

export default {
  jsonToCSV,
  downloadCSV,
  exportToCSV,
  generateFilename,
  filterByDateRange,
  selectColumns
}
