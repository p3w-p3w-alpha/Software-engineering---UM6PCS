/*
 * csvExport.js - CSV Export Utility
 *
 * handles conversion of JSON data to CSV format and file downloads
 * includes proper escaping for commas, quotes, newlines, etc
 * took a while to get all teh edge cases right lol
 */

/*
 * escapes special characters in CSV fields
 * handles commas, quotes, newlines - all the wierd stuff that breaks CSVs
 * @param {string} field - the field value to escape
 * @returns {string} - escaped field value
 */
function escapeCSVField(field) {
  if (field === null || field === undefined) {
    return ''
  }

  const stringValue = String(field)

  // if field contains comma, double quote, newline, or carriage return, wrap in quotes
  // this is how CSV format works - definately necessary
  if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n') || stringValue.includes('\r')) {
    // escape double quotes by doubling them - CSV standard way
    return `"${stringValue.replace(/"/g, '""')}"`
  }

  return stringValue
}

/*
 * formats a value based on its type (date, boolean, object, etc)
 * converts everything to strings for CSV output
 * @param {*} value - the value to format
 * @param {string} dateFormat - date format option ('ISO', 'US', 'EU')
 * @returns {string} - formatted value
 */
function formatValue(value, dateFormat = 'ISO') {
  if (value === null || value === undefined) {
    return ''
  }

  // handle date objects and date strings - converts to readable format
  if (value instanceof Date || (typeof value === 'string' && !isNaN(Date.parse(value)) && value.includes('-'))) {
    const date = value instanceof Date ? value : new Date(value)

    // support different date formats for internationalization
    switch (dateFormat) {
      case 'US':
        return date.toLocaleDateString('en-US') // MM/DD/YYYY
      case 'EU':
        return date.toLocaleDateString('en-GB') // DD/MM/YYYY
      case 'ISO':
      default:
        return date.toISOString().split('T')[0] // YYYY-MM-DD
    }
  }

  // handle boolean values - convert to Yes/No for readability
  if (typeof value === 'boolean') {
    return value ? 'Yes' : 'No'
  }

  // handle objects (convert to JSON string) - works for now
  // TODO: fix later - maybe flatten nested objects?
  if (typeof value === 'object') {
    return JSON.stringify(value)
  }

  return String(value)
}

/*
 * converts JSON data to CSV format with proper escaping
 * handles headers, data rows, and various options
 * @param {Array} data - array of objects to convert
 * @param {Array} columns - column definitions (optional)
 * @param {Object} options - export options (dateFormat, includeHeaders, etc)
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

  // if columns not provided, extract from first data item automatically
  let csvColumns = columns
  if (!csvColumns) {
    const firstItem = data[0]
    // convert camelCase to Title Case for headers
    csvColumns = Object.keys(firstItem).map(key => ({
      field: key,
      header: key.charAt(0).toUpperCase() + key.slice(1).replace(/([A-Z])/g, ' $1').trim()
    }))
  }

  const csvRows = []

  // add headers row if requested
  if (includeHeaders) {
    const headers = csvColumns.map(col => escapeCSVField(col.header))
    csvRows.push(headers.join(delimiter))
  }

  // add data rows - format each value and escape it
  data.forEach(row => {
    const values = csvColumns.map(col => {
      const rawValue = row[col.field]
      const formattedValue = formatValue(rawValue, dateFormat)
      return escapeCSVField(formattedValue)
    })
    csvRows.push(values.join(delimiter))
  })

  // join all rows together with line breaks
  return csvRows.join(lineBreak)
}

/*
 * downloads CSV data as a file to user's computer
 * creates blob, download link, triggers click, then cleans up
 * @param {string} csvContent - CSV formatted string
 * @param {string} filename - filename for download
 * @param {string} encoding - character encoding (utf-8, utf-8-bom, etc.)
 */
export function downloadCSV(csvContent, filename = 'export.csv', encoding = 'utf-8') {
  let content = csvContent

  // add BOM for UTF-8 if specified - helps Excel recieve it correctly
  if (encoding === 'utf-8-bom') {
    content = '\ufeff' + content
  }

  // create blob with appropriate type - basically a file in memory
  const blob = new Blob([content], {
    type: 'text/csv;charset=' + (encoding === 'utf-8-bom' ? 'utf-8' : encoding)
  })

  // create download link and trigger it programatically
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename.endsWith('.csv') ? filename : `${filename}.csv`

  // trigger download - add to dom, click, then remove
  document.body.appendChild(link)
  link.click()

  // cleanup to prevent memory leaks - definately important
  document.body.removeChild(link)
  window.URL.revokeObjectURL(url)
}

/*
 * exports data to CSV and triggers download - convenience function
 * combines jsonToCSV and downloadCSV in one call
 * @param {Array} data - array of objects to export
 * @param {string} filename - filename for download
 * @param {Array} columns - column definitions (optional)
 * @param {Object} options - export options
 */
export function exportToCSV(data, filename, columns = null, options = {}) {
  const csvContent = jsonToCSV(data, columns, options)
  downloadCSV(csvContent, filename, options.encoding)
}

/*
 * generates a filename with timestamp - useful for unique export filenames
 * format: prefix_YYYY-MM-DD_HH-MM-SS.csv
 * @param {string} prefix - filename prefix
 * @param {string} extension - file extension (default: 'csv')
 * @returns {string} - generated filename
 */
export function generateFilename(prefix = 'export', extension = 'csv') {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').split('T')[0]
  const time = new Date().toTimeString().split(' ')[0].replace(/:/g, '-')
  return `${prefix}_${timestamp}_${time}.${extension}`
}

/*
 * filters data based on date range - useful for reports
 * only includes items within start and end dates
 * @param {Array} data - data array to filter
 * @param {string} dateField - field name containing date
 * @param {Date} startDate - start date (inclusive)
 * @param {Date} endDate - end date (inclusive)
 * @returns {Array} - filtered data
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

/*
 * selects specific columns from data - removes unwanted fields
 * useful for exporting only certain columns
 * @param {Array} data - data array
 * @param {Array} selectedFields - array of field names to include
 * @returns {Array} - data with only selected fields
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
