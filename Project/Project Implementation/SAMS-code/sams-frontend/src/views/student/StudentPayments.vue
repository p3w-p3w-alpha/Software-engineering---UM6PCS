<template>
  <div class="max-w-7xl mx-auto">
    <!-- Toast Notification -->
    <transition
      enter-active-class="transition ease-out duration-300"
      enter-from-class="opacity-0 translate-y-2"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-200"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-2"
    >
      <div
        v-if="showToast"
        :class="[
          'fixed top-4 right-4 z-50 px-4 py-3 rounded-lg shadow-lg flex items-center gap-3 max-w-md',
          toastType === 'success' ? 'bg-green-50 text-green-800 border border-green-200' : 'bg-red-50 text-red-800 border border-red-200'
        ]"
      >
        <svg v-if="toastType === 'success'" class="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <svg v-else class="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <span class="text-sm font-medium">{{ toastMessage }}</span>
        <button @click="showToast = false" class="ml-auto text-gray-400 hover:text-gray-600">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
    </transition>

    <!-- Header -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-900">Fees & Payments</h1>
      <p class="mt-2 text-gray-600">Manage your tuition, view payment history, and download transcripts</p>
    </div>

    <!-- Financial Overview Cards -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Total Due</p>
            <p class="text-2xl font-bold text-gray-900 mt-1">${{ formatCurrency(financialSummary.totalDue) }}</p>
          </div>
          <div class="p-3 bg-red-100 rounded-lg">
            <svg class="w-6 h-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
        <div v-if="financialSummary.overdue > 0" class="mt-3">
          <span class="text-xs text-red-600 font-medium">${{ formatCurrency(financialSummary.overdue) }} overdue</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Total Paid</p>
            <p class="text-2xl font-bold text-green-600 mt-1">${{ formatCurrency(financialSummary.totalPaid) }}</p>
          </div>
          <div class="p-3 bg-green-100 rounded-lg">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
        <div class="mt-3">
          <span class="text-xs text-gray-500">This academic year</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Pending Approval</p>
            <p class="text-2xl font-bold text-yellow-600 mt-1">${{ formatCurrency(financialSummary.pendingApproval) }}</p>
          </div>
          <div class="p-3 bg-yellow-100 rounded-lg">
            <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
        </div>
        <div class="mt-3">
          <span class="text-xs text-gray-500">{{ financialSummary.pendingCount }} payment(s)</span>
        </div>
      </div>

      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-500">Next Due Date</p>
            <p class="text-2xl font-bold text-blue-600 mt-1">{{ financialSummary.nextDueDate || 'N/A' }}</p>
          </div>
          <div class="p-3 bg-blue-100 rounded-lg">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
        </div>
        <div v-if="financialSummary.daysUntilDue !== null" class="mt-3">
          <span :class="financialSummary.daysUntilDue <= 7 ? 'text-red-600' : 'text-gray-500'" class="text-xs font-medium">
            {{ financialSummary.daysUntilDue }} days remaining
          </span>
        </div>
      </div>
    </div>

    <!-- Payment Progress & Fee Breakdown -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
      <!-- Payment Progress Chart -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Payment Progress</h2>
        <div class="flex items-center justify-center mb-4">
          <!-- Circular Progress -->
          <div class="relative w-48 h-48">
            <svg class="w-full h-full transform -rotate-90">
              <circle cx="96" cy="96" r="88" stroke="#e5e7eb" stroke-width="12" fill="none" />
              <circle
                cx="96"
                cy="96"
                r="88"
                stroke="#10b981"
                stroke-width="12"
                fill="none"
                stroke-linecap="round"
                :stroke-dasharray="circumference"
                :stroke-dashoffset="progressOffset"
                class="transition-all duration-1000"
              />
            </svg>
            <div class="absolute inset-0 flex flex-col items-center justify-center">
              <span class="text-3xl font-bold text-gray-900">{{ paymentProgress }}%</span>
              <span class="text-sm text-gray-500">Paid</span>
            </div>
          </div>
        </div>
        <div class="grid grid-cols-3 gap-4 text-center">
          <div>
            <p class="text-sm text-gray-500">Total Fees</p>
            <p class="font-semibold text-gray-900">${{ formatCurrency(financialSummary.totalFees) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Paid</p>
            <p class="font-semibold text-green-600">${{ formatCurrency(financialSummary.totalPaid) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Remaining</p>
            <p class="font-semibold text-red-600">${{ formatCurrency(financialSummary.totalDue) }}</p>
          </div>
        </div>
      </div>

      <!-- Fee Breakdown by Category -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Fee Breakdown</h2>
        <canvas ref="feeBreakdownChart" height="200"></canvas>
        <div class="mt-4 grid grid-cols-2 gap-2">
          <div v-for="(item, index) in feeCategories" :key="item.name" class="flex items-center">
            <div class="w-3 h-3 rounded-full mr-2" :style="{ backgroundColor: chartColors[index] }"></div>
            <span class="text-sm text-gray-600">{{ item.name }}: ${{ formatCurrency(item.amount) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Current Semester Payment Status -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 mb-8">
      <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
        <h2 class="text-lg font-semibold text-gray-900">Current Semester Payment</h2>
        <span v-if="currentPayment" :class="getPaymentStatusClass(currentPayment.status)" class="px-3 py-1 text-sm font-semibold rounded-full">
          {{ currentPayment.status }}
        </span>
      </div>
      <div class="p-6">
        <div v-if="loadingPayment" class="flex justify-center py-8">
          <LoadingSpinner />
        </div>

        <div v-else-if="!currentPayment" class="text-center py-8 text-gray-500">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p class="mt-2 font-medium">All payments are up to date!</p>
          <p class="text-sm">No outstanding payments at this time.</p>
        </div>

        <div v-else>
          <!-- Payment Summary -->
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
            <div class="bg-gray-50 rounded-lg p-4">
              <p class="text-sm text-gray-600">Semester</p>
              <p class="font-semibold text-gray-900">{{ currentPayment.semester?.name || 'Current' }}</p>
            </div>
            <div class="bg-blue-50 rounded-lg p-4">
              <p class="text-sm text-blue-600">Total Amount</p>
              <p class="text-xl font-bold text-blue-900">${{ formatCurrency(currentPayment.amount) }}</p>
            </div>
            <div class="bg-green-50 rounded-lg p-4">
              <p class="text-sm text-green-600">Paid Amount</p>
              <p class="text-xl font-bold text-green-900">${{ formatCurrency(currentPayment.paidAmount) }}</p>
            </div>
            <div class="bg-red-50 rounded-lg p-4">
              <p class="text-sm text-red-600">Remaining</p>
              <p class="text-xl font-bold text-red-900">${{ formatCurrency(currentPayment.remainingAmount) }}</p>
            </div>
          </div>

          <!-- Payment Progress Bar -->
          <div class="mb-6">
            <div class="flex justify-between text-sm mb-1">
              <span class="text-gray-600">Payment Progress</span>
              <span class="font-medium">{{ Math.round((currentPayment.paidAmount / currentPayment.amount) * 100) }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-3">
              <div
                class="bg-gradient-to-r from-green-400 to-green-600 h-3 rounded-full transition-all duration-500"
                :style="{ width: `${Math.min((currentPayment.paidAmount / currentPayment.amount) * 100, 100)}%` }"
              ></div>
            </div>
          </div>

          <!-- Enrolled Courses in Payment -->
          <div class="mb-6">
            <h3 class="text-md font-semibold text-gray-900 mb-3">Enrolled Courses</h3>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Course</th>
                    <th class="px-4 py-3 text-left text-xs font-medium text-gray-500 uppercase">Credits</th>
                    <th class="px-4 py-3 text-right text-xs font-medium text-gray-500 uppercase">Fee</th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="enrollment in currentPayment.enrollments" :key="enrollment.id">
                    <td class="px-4 py-3">
                      <p class="font-medium text-gray-900">{{ enrollment.course?.courseCode }} - {{ enrollment.course?.courseName }}</p>
                      <p class="text-sm text-gray-500">{{ enrollment.course?.instructor?.fullName || 'TBA' }}</p>
                    </td>
                    <td class="px-4 py-3 text-gray-600">{{ enrollment.course?.creditHours }} credits</td>
                    <td class="px-4 py-3 text-right font-semibold text-gray-900">${{ formatCurrency(enrollment.course?.courseFee || 0) }}</td>
                  </tr>
                  <tr class="bg-gray-50">
                    <td class="px-4 py-3 font-semibold text-gray-900" colspan="2">Total</td>
                    <td class="px-4 py-3 text-right font-bold text-gray-900">${{ formatCurrency(currentPayment.amount) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Due Date Warning -->
          <div v-if="currentPayment.dueDate && getDaysUntilDue(currentPayment.dueDate) <= 7" class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-6">
            <div class="flex">
              <svg class="h-5 w-5 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
              </svg>
              <div class="ml-3">
                <p class="text-sm text-yellow-700 font-medium">
                  Payment due {{ formatDate(currentPayment.dueDate) }} ({{ getDaysUntilDue(currentPayment.dueDate) }} days remaining)
                </p>
                <p class="text-xs text-yellow-600 mt-1">Late payments may incur additional fees.</p>
              </div>
            </div>
          </div>

          <!-- Status Messages -->
          <div v-if="currentPayment.status === 'PAID'" class="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-6">
            <div class="flex">
              <svg class="h-5 w-5 text-yellow-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div class="ml-3">
                <p class="text-sm text-yellow-700">Your payment has been submitted and is awaiting admin approval.</p>
                <p v-if="currentPayment.transactionReference" class="text-xs text-yellow-600 mt-1">
                  Transaction Reference: <span class="font-mono font-medium">{{ currentPayment.transactionReference }}</span>
                </p>
              </div>
            </div>
          </div>

          <div v-if="currentPayment.status === 'APPROVED'" class="bg-green-50 border-l-4 border-green-400 p-4 mb-6">
            <div class="flex">
              <svg class="h-5 w-5 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <div class="ml-3">
                <p class="text-sm text-green-700 font-medium">Payment approved! Your enrollments are now active.</p>
                <p v-if="currentPayment.approvedBy" class="text-xs text-green-600 mt-1">
                  Approved by {{ currentPayment.approvedBy.username }} on {{ formatDate(currentPayment.updatedAt) }}
                </p>
              </div>
            </div>
          </div>

          <!-- Submit Payment Button -->
          <div v-if="currentPayment.status === 'PENDING' || currentPayment.status === 'DUE'" class="flex gap-4">
            <button
              @click="showPaymentModal = true"
              class="flex-1 md:flex-none px-6 py-3 bg-blue-600 text-white font-medium rounded-lg hover:bg-blue-700 transition-colors flex items-center justify-center gap-2"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
              Submit Payment
            </button>
            <button
              @click="showInstallmentModal = true"
              class="px-6 py-3 border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50 transition-colors flex items-center justify-center gap-2"
            >
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
              </svg>
              Payment Plan
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Payment History Timeline -->
    <div class="bg-white rounded-xl shadow-sm border border-gray-200 mb-8">
      <div class="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
        <h2 class="text-lg font-semibold text-gray-900">Payment History</h2>
        <button
          @click="downloadTranscript"
          class="px-4 py-2 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors flex items-center gap-2"
        >
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          Download Transcript
        </button>
      </div>
      <div class="p-6">
        <!-- Tabs -->
        <div class="flex gap-4 mb-6 border-b">
          <button
            v-for="tab in historyTabs"
            :key="tab.id"
            @click="activeHistoryTab = tab.id"
            :class="[
              'px-4 py-2 text-sm font-medium border-b-2 transition-colors -mb-px',
              activeHistoryTab === tab.id
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700'
            ]"
          >
            {{ tab.label }} ({{ tab.count }})
          </button>
        </div>

        <div v-if="loadingHistory" class="flex justify-center py-8">
          <LoadingSpinner />
        </div>

        <div v-else-if="filteredPaymentHistory.length === 0" class="text-center py-8 text-gray-500">
          <svg class="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
          </svg>
          <p class="mt-2">No payment records found</p>
        </div>

        <!-- Timeline View -->
        <div v-else class="space-y-4">
          <div
            v-for="payment in filteredPaymentHistory"
            :key="payment.id"
            class="relative pl-8 pb-6 border-l-2 border-gray-200 last:border-transparent"
          >
            <!-- Timeline Dot -->
            <div
              :class="[
                'absolute left-0 w-4 h-4 rounded-full -translate-x-1/2 border-2 border-white',
                getTimelineDotColor(payment.status)
              ]"
            ></div>

            <!-- Payment Card -->
            <div class="bg-gray-50 rounded-lg p-4 hover:bg-gray-100 transition-colors cursor-pointer" @click="viewPaymentDetails(payment)">
              <div class="flex items-start justify-between">
                <div>
                  <div class="flex items-center gap-2">
                    <h4 class="font-semibold text-gray-900">{{ payment.semester }}</h4>
                    <span :class="getPaymentStatusClass(payment.status)" class="px-2 py-0.5 text-xs font-semibold rounded-full">
                      {{ payment.status }}
                    </span>
                  </div>
                  <p class="text-sm text-gray-500 mt-1">{{ formatDate(payment.createdAt) }}</p>
                  <p v-if="payment.transactionReference" class="text-xs text-gray-400 mt-1 font-mono">
                    Ref: {{ payment.transactionReference }}
                  </p>
                </div>
                <div class="text-right">
                  <p class="text-lg font-bold text-gray-900">${{ formatCurrency(payment.amount) }}</p>
                  <p class="text-sm text-green-600">Paid: ${{ formatCurrency(payment.paidAmount) }}</p>
                </div>
              </div>

              <!-- Enrolled Courses Preview -->
              <div v-if="payment.enrollments?.length" class="mt-3 pt-3 border-t border-gray-200">
                <p class="text-xs text-gray-500 mb-2">{{ payment.enrollments.length }} course(s)</p>
                <div class="flex flex-wrap gap-2">
                  <span
                    v-for="enrollment in payment.enrollments.slice(0, 3)"
                    :key="enrollment.id"
                    class="px-2 py-1 bg-white text-xs text-gray-600 rounded border"
                  >
                    {{ enrollment.course?.courseCode }}
                  </span>
                  <span v-if="payment.enrollments.length > 3" class="px-2 py-1 bg-white text-xs text-gray-400 rounded border">
                    +{{ payment.enrollments.length - 3 }} more
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Payment Methods & Support -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
      <!-- Accepted Payment Methods -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Accepted Payment Methods</h2>
        <div class="grid grid-cols-2 gap-4">
          <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
            <div class="p-2 bg-blue-100 rounded-lg">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
              </svg>
            </div>
            <div>
              <p class="font-medium text-gray-900">Credit/Debit Card</p>
              <p class="text-xs text-gray-500">Visa, Mastercard, Amex</p>
            </div>
          </div>
          <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
            <div class="p-2 bg-green-100 rounded-lg">
              <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 14v3m4-3v3m4-3v3M3 21h18M3 10h18M3 7l9-4 9 4M4 10h16v11H4V10z" />
              </svg>
            </div>
            <div>
              <p class="font-medium text-gray-900">Bank Transfer</p>
              <p class="text-xs text-gray-500">Direct deposit</p>
            </div>
          </div>
          <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
            <div class="p-2 bg-purple-100 rounded-lg">
              <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <div>
              <p class="font-medium text-gray-900">Check</p>
              <p class="text-xs text-gray-500">Payable to University</p>
            </div>
          </div>
          <div class="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
            <div class="p-2 bg-yellow-100 rounded-lg">
              <svg class="w-5 h-5 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <div>
              <p class="font-medium text-gray-900">Cash</p>
              <p class="text-xs text-gray-500">At cashier's office</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Support Info -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <h2 class="text-lg font-semibold text-gray-900 mb-4">Need Help?</h2>
        <div class="space-y-4">
          <div class="flex items-start gap-3">
            <div class="p-2 bg-blue-100 rounded-lg">
              <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
            </div>
            <div>
              <p class="font-medium text-gray-900">Email Support</p>
              <a href="mailto:billing@university.edu" class="text-sm text-blue-600 hover:underline">billing@university.edu</a>
            </div>
          </div>
          <div class="flex items-start gap-3">
            <div class="p-2 bg-green-100 rounded-lg">
              <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" />
              </svg>
            </div>
            <div>
              <p class="font-medium text-gray-900">Phone Support</p>
              <p class="text-sm text-gray-600">+1 (555) 123-4567</p>
              <p class="text-xs text-gray-500">Mon-Fri 9AM-5PM</p>
            </div>
          </div>
          <div class="flex items-start gap-3">
            <div class="p-2 bg-purple-100 rounded-lg">
              <svg class="w-5 h-5 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </div>
            <div>
              <p class="font-medium text-gray-900">Visit Us</p>
              <p class="text-sm text-gray-600">Student Services Building, Room 101</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Payment Submission Modal -->
    <Modal
      v-model="showPaymentModal"
      title="Submit Payment"
      confirm-text="Submit Payment"
      :confirm-disabled="!isPaymentFormValid || submitting"
      @confirm="submitPayment"
    >
      <div class="space-y-4">
        <div class="bg-blue-50 rounded-lg p-4 mb-4">
          <p class="text-sm text-blue-700">
            <strong>Amount Due:</strong> ${{ formatCurrency(currentPayment?.remainingAmount || 0) }}
          </p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Payment Amount *</label>
          <input
            v-model.number="paymentForm.amount"
            type="number"
            step="0.01"
            min="0.01"
            :max="currentPayment?.remainingAmount"
            :class="[
              'w-full px-4 py-2 border rounded-lg focus:ring-blue-500 focus:border-blue-500',
              paymentFormErrors.amount ? 'border-red-500' : 'border-gray-300'
            ]"
            placeholder="Enter amount"
            required
            aria-label="Payment amount"
          />
          <p v-if="paymentFormErrors.amount" class="mt-1 text-sm text-red-600">{{ paymentFormErrors.amount }}</p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Payment Method *</label>
          <select
            v-model="paymentForm.paymentMethod"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="BANK_TRANSFER">Bank Transfer</option>
            <option value="CREDIT_CARD">Credit Card</option>
            <option value="DEBIT_CARD">Debit Card</option>
            <option value="CASH">Cash</option>
            <option value="CHECK">Check</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Transaction Reference *</label>
          <input
            v-model="paymentForm.transactionReference"
            type="text"
            :class="[
              'w-full px-4 py-2 border rounded-lg focus:ring-blue-500 focus:border-blue-500',
              paymentFormErrors.transactionReference ? 'border-red-500' : 'border-gray-300'
            ]"
            placeholder="e.g., TXN123456789"
            required
            aria-label="Transaction reference"
          />
          <p v-if="paymentFormErrors.transactionReference" class="mt-1 text-sm text-red-600">{{ paymentFormErrors.transactionReference }}</p>
          <p v-else class="mt-1 text-xs text-gray-500">Enter your payment confirmation number</p>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Payment Date</label>
          <input
            v-model="paymentForm.paymentDate"
            type="date"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Notes (Optional)</label>
          <textarea
            v-model="paymentForm.notes"
            rows="2"
            class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            placeholder="Any additional information..."
          ></textarea>
        </div>
      </div>
    </Modal>

    <!-- Installment Plan Modal -->
    <Modal
      v-model="showInstallmentModal"
      title="Payment Plan Options"
      confirm-text="Select Plan"
      :confirm-disabled="!selectedPlan"
      @confirm="selectInstallmentPlan"
    >
      <div class="space-y-4">
        <p class="text-sm text-gray-600 mb-4">
          Choose a payment plan that works for you. Total amount: ${{ formatCurrency(currentPayment?.remainingAmount || 0) }}
        </p>

        <div
          v-for="plan in installmentPlans"
          :key="plan.id"
          @click="selectedPlan = plan.id"
          :class="[
            'p-4 border rounded-lg cursor-pointer transition-colors',
            selectedPlan === plan.id
              ? 'border-blue-500 bg-blue-50'
              : 'border-gray-200 hover:border-gray-300'
          ]"
        >
          <div class="flex items-center justify-between mb-2">
            <h4 class="font-semibold text-gray-900">{{ plan.name }}</h4>
            <div class="flex items-center gap-2">
              <span class="text-sm text-gray-500">{{ plan.installments }} payments</span>
              <div :class="['w-4 h-4 rounded-full border-2', selectedPlan === plan.id ? 'border-blue-500 bg-blue-500' : 'border-gray-300']">
                <svg v-if="selectedPlan === plan.id" class="w-3 h-3 text-white m-0.5" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                </svg>
              </div>
            </div>
          </div>
          <p class="text-sm text-gray-600">{{ plan.description }}</p>
          <div class="mt-2 flex items-baseline gap-1">
            <span class="text-xl font-bold text-gray-900">${{ formatCurrency(plan.monthlyAmount) }}</span>
            <span class="text-sm text-gray-500">/month</span>
          </div>
          <p v-if="plan.fee > 0" class="text-xs text-gray-400 mt-1">+ ${{ plan.fee }} processing fee</p>
        </div>
      </div>
    </Modal>

    <!-- Payment Details Modal -->
    <Modal
      v-model="showDetailsModal"
      title="Payment Details"
      :showFooter="false"
    >
      <div v-if="selectedPayment" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <p class="text-sm text-gray-500">Semester</p>
            <p class="font-medium">{{ selectedPayment.semester }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Status</p>
            <span :class="getPaymentStatusClass(selectedPayment.status)" class="px-2 py-0.5 text-xs font-semibold rounded-full">
              {{ selectedPayment.status }}
            </span>
          </div>
          <div>
            <p class="text-sm text-gray-500">Total Amount</p>
            <p class="font-medium">${{ formatCurrency(selectedPayment.amount) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Paid Amount</p>
            <p class="font-medium text-green-600">${{ formatCurrency(selectedPayment.paidAmount) }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Payment Method</p>
            <p class="font-medium">{{ selectedPayment.paymentMethod || 'N/A' }}</p>
          </div>
          <div>
            <p class="text-sm text-gray-500">Transaction Ref</p>
            <p class="font-medium font-mono text-sm">{{ selectedPayment.transactionReference || 'N/A' }}</p>
          </div>
        </div>

        <!-- Payment Timeline -->
        <div v-if="selectedPayment.history?.length" class="mt-6">
          <h4 class="font-semibold text-gray-900 mb-3">Activity Timeline</h4>
          <div class="space-y-3">
            <div v-for="event in selectedPayment.history" :key="event.id" class="flex items-start gap-3">
              <div :class="['w-2 h-2 rounded-full mt-2', getTimelineDotColor(event.newStatus)]"></div>
              <div>
                <p class="text-sm text-gray-900">{{ event.actionType }}</p>
                <p class="text-xs text-gray-500">
                  {{ event.performedBy?.username || 'System' }} - {{ formatDate(event.createdAt) }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Enrolled Courses -->
        <div v-if="selectedPayment.enrollments?.length" class="mt-6">
          <h4 class="font-semibold text-gray-900 mb-3">Courses</h4>
          <div class="space-y-2">
            <div v-for="enrollment in selectedPayment.enrollments" :key="enrollment.id" class="flex items-center justify-between p-2 bg-gray-50 rounded">
              <span class="text-sm">{{ enrollment.course?.courseCode }} - {{ enrollment.course?.courseName }}</span>
              <span class="text-sm font-medium">${{ formatCurrency(enrollment.course?.courseFee || 0) }}</span>
            </div>
          </div>
        </div>
      </div>
    </Modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useAuthStore } from '../../stores/auth'
import api from '../../services/api'
import LoadingSpinner from '../../components/LoadingSpinner.vue'
import Modal from '../../components/Modal.vue'
import Chart from 'chart.js/auto'

const authStore = useAuthStore()

// State
const currentPayment = ref(null)
const paymentHistory = ref([])
const loadingPayment = ref(false)
const loadingHistory = ref(false)
const showPaymentModal = ref(false)
const showInstallmentModal = ref(false)
const showDetailsModal = ref(false)
const submitting = ref(false)
const selectedPayment = ref(null)
const selectedPlan = ref(null)
const activeHistoryTab = ref('all')

// Chart refs
const feeBreakdownChart = ref(null)
let feeChartInstance = null

// Chart colors
const chartColors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899']

// Computed
const circumference = 2 * Math.PI * 88

const paymentProgress = computed(() => {
  if (!financialSummary.value.totalFees) return 0
  return Math.round((financialSummary.value.totalPaid / financialSummary.value.totalFees) * 100)
})

const progressOffset = computed(() => {
  return circumference - (paymentProgress.value / 100) * circumference
})

const financialSummary = computed(() => {
  const approved = paymentHistory.value.filter(p => p.status === 'APPROVED')
  const pending = paymentHistory.value.filter(p => p.status === 'PAID') // Submitted, awaiting approval
  const due = paymentHistory.value.filter(p => p.status === 'DUE' || p.status === 'PENDING') // Not yet paid

  const totalPaid = approved.reduce((sum, p) => sum + (p.paidAmount || 0), 0) +
                    pending.reduce((sum, p) => sum + (p.paidAmount || 0), 0)
  // Calculate total due from DUE payments (amount - paidAmount)
  const totalDue = due.reduce((sum, p) => {
    const remaining = (p.amount || 0) - (p.paidAmount || 0)
    return sum + (remaining > 0 ? remaining : 0)
  }, 0)
  const totalFees = paymentHistory.value.reduce((sum, p) => sum + (p.amount || 0), 0)
  const pendingApproval = pending.reduce((sum, p) => sum + (p.paidAmount || 0), 0)

  // Find next due date from DUE payments
  const upcomingPayments = due.filter(p => p.dueDate && new Date(p.dueDate) > new Date())
  upcomingPayments.sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
  const nextDue = upcomingPayments[0]

  // Calculate overdue amount from DUE payments past their due date
  const overdue = due.filter(p => p.dueDate && new Date(p.dueDate) < new Date())
    .reduce((sum, p) => {
      const remaining = (p.amount || 0) - (p.paidAmount || 0)
      return sum + (remaining > 0 ? remaining : 0)
    }, 0)

  return {
    totalFees: totalFees,
    totalPaid: totalPaid,
    totalDue: totalDue,
    pendingApproval: pendingApproval,
    pendingCount: pending.length,
    overdue: overdue,
    nextDueDate: nextDue ? formatDate(nextDue.dueDate) : null,
    daysUntilDue: nextDue ? getDaysUntilDue(nextDue.dueDate) : null
  }
})

const feeCategories = computed(() => {
  // Calculate fee breakdown from actual payment and enrollment data
  const categories = []

  // Calculate tuition from enrolled courses
  const enrollments = currentPayment.value?.enrollments || []
  if (enrollments.length > 0) {
    const tuition = enrollments.reduce((sum, e) => sum + (e.course?.courseFee || 0), 0)
    categories.push({ name: 'Tuition', amount: tuition })
  }

  // If we have payment history, use that to extract fee breakdown
  if (paymentHistory.value.length > 0) {
    const allEnrollments = paymentHistory.value.flatMap(p => p.enrollments || [])
    if (allEnrollments.length > 0 && categories.length === 0) {
      const totalTuition = allEnrollments.reduce((sum, e) => sum + (e.course?.courseFee || 0), 0)
      categories.push({ name: 'Tuition', amount: totalTuition })
    }
  }

  // Add any additional fees from the payment record if available
  if (currentPayment.value?.additionalFees) {
    Object.entries(currentPayment.value.additionalFees).forEach(([name, amount]) => {
      categories.push({ name, amount })
    })
  }

  // If no data available, return empty array (chart will show nothing)
  if (categories.length === 0 && financialSummary.value.totalFees > 0) {
    categories.push({ name: 'Total Fees', amount: financialSummary.value.totalFees })
  }

  return categories
})

const historyTabs = computed(() => [
  { id: 'all', label: 'All', count: paymentHistory.value.length },
  { id: 'approved', label: 'Approved', count: paymentHistory.value.filter(p => p.status === 'APPROVED').length },
  { id: 'pending', label: 'Pending', count: paymentHistory.value.filter(p => p.status === 'PENDING' || p.status === 'PAID' || p.status === 'DUE').length },
  { id: 'rejected', label: 'Rejected', count: paymentHistory.value.filter(p => p.status === 'REJECTED').length }
])

const filteredPaymentHistory = computed(() => {
  if (activeHistoryTab.value === 'all') return paymentHistory.value
  if (activeHistoryTab.value === 'approved') return paymentHistory.value.filter(p => p.status === 'APPROVED')
  if (activeHistoryTab.value === 'pending') return paymentHistory.value.filter(p => p.status === 'PENDING' || p.status === 'PAID' || p.status === 'DUE')
  if (activeHistoryTab.value === 'rejected') return paymentHistory.value.filter(p => p.status === 'REJECTED')
  return paymentHistory.value
})

const installmentPlans = computed(() => {
  const total = currentPayment.value?.remainingAmount || 0
  return [
    {
      id: 'full',
      name: 'Pay in Full',
      installments: 1,
      monthlyAmount: total,
      fee: 0,
      description: 'One-time payment with no additional fees'
    },
    {
      id: '2-month',
      name: '2-Month Plan',
      installments: 2,
      monthlyAmount: total / 2,
      fee: 25,
      description: 'Split your payment into 2 equal installments'
    },
    {
      id: '4-month',
      name: '4-Month Plan',
      installments: 4,
      monthlyAmount: total / 4,
      fee: 50,
      description: 'Spread payments over 4 months'
    },
    {
      id: '6-month',
      name: '6-Month Plan',
      installments: 6,
      monthlyAmount: total / 6,
      fee: 75,
      description: 'Extended plan with smaller monthly payments'
    }
  ]
})

// Payment form
const paymentForm = ref({
  amount: 0,
  paymentMethod: 'BANK_TRANSFER',
  transactionReference: '',
  paymentDate: new Date().toISOString().split('T')[0],
  notes: ''
})

// Form validation errors
const paymentFormErrors = ref({
  amount: '',
  transactionReference: ''
})

// Form validation computed
const isPaymentFormValid = computed(() => {
  paymentFormErrors.value = { amount: '', transactionReference: '' }

  // Validate amount
  if (!paymentForm.value.amount || paymentForm.value.amount <= 0) {
    paymentFormErrors.value.amount = 'Payment amount is required and must be greater than 0'
    return false
  }

  const maxAmount = currentPayment.value?.remainingAmount || 0
  if (paymentForm.value.amount > maxAmount) {
    paymentFormErrors.value.amount = `Amount cannot exceed $${formatCurrency(maxAmount)}`
    return false
  }

  // Validate transaction reference
  if (!paymentForm.value.transactionReference || paymentForm.value.transactionReference.trim().length < 3) {
    paymentFormErrors.value.transactionReference = 'Transaction reference is required (min 3 characters)'
    return false
  }

  return true
})

// Methods
const loadCurrentPayment = async () => {
  try {
    loadingPayment.value = true
    const response = await api.getStudentPayments(authStore.userId)
    const payments = response.data || []

    // Find a pending, due, or awaiting approval payment
    currentPayment.value = payments.find(p =>
      p.status === 'PENDING' || p.status === 'PAID' || p.status === 'DUE'
    ) || null

    if (currentPayment.value) {
      paymentForm.value.amount = currentPayment.value.remainingAmount || currentPayment.value.amount || 0
    }
  } catch (error) {
    console.error('Error loading payment:', error)
    currentPayment.value = null
  } finally {
    loadingPayment.value = false
  }
}

const loadPaymentHistory = async () => {
  try {
    loadingHistory.value = true
    const response = await api.getStudentPayments(authStore.userId)
    paymentHistory.value = response.data.map(p => ({
      ...p,
      // Use semester name, description, or generate from date
      semester: p.semester?.name || p.description || p.title || generateSemesterName(p.createdAt)
    }))
  } catch (error) {
    console.error('Error loading payment history:', error)
    paymentHistory.value = []
  } finally {
    loadingHistory.value = false
  }
}

// Generate semester name from date if not available
const generateSemesterName = (dateString) => {
  if (!dateString) return 'Payment'
  const date = new Date(dateString)
  const month = date.getMonth()
  const year = date.getFullYear()

  // Determine semester based on month
  if (month >= 0 && month <= 4) {
    return `Spring ${year}`
  } else if (month >= 5 && month <= 7) {
    return `Summer ${year}`
  } else {
    return `Fall ${year}`
  }
}

// Toast notification state
const toastMessage = ref('')
const toastType = ref('success')
const showToast = ref(false)

const showNotification = (message, type = 'success') => {
  toastMessage.value = message
  toastType.value = type
  showToast.value = true
  setTimeout(() => {
    showToast.value = false
  }, 5000)
}

const submitPayment = async () => {
  try {
    submitting.value = true
    await api.submitPayment(currentPayment.value.id, paymentForm.value)

    showNotification('Payment submitted successfully! Awaiting admin approval.', 'success')
    showPaymentModal.value = false

    await loadCurrentPayment()
    await loadPaymentHistory()

    paymentForm.value = {
      amount: 0,
      paymentMethod: 'BANK_TRANSFER',
      transactionReference: '',
      paymentDate: new Date().toISOString().split('T')[0],
      notes: ''
    }
  } catch (error) {
    const errorMessage = error.response?.data?.message || 'Payment submission failed'
    showNotification(errorMessage, 'error')
  } finally {
    submitting.value = false
  }
}

const viewPaymentDetails = async (payment) => {
  selectedPayment.value = { ...payment }

  try {
    const response = await api.getPaymentHistory(payment.id)
    selectedPayment.value.history = response.data || []
  } catch (error) {
    console.error('Error loading payment details:', error)
    selectedPayment.value.history = []
  }

  showDetailsModal.value = true
}

const selectInstallmentPlan = () => {
  const plan = installmentPlans.value.find(p => p.id === selectedPlan.value)
  if (plan) {
    showNotification(`Payment plan selected: ${plan.name} - Monthly payment: $${formatCurrency(plan.monthlyAmount)} + $${plan.fee} processing fee. Please visit the cashier's office to finalize.`, 'success')
    showInstallmentModal.value = false
    selectedPlan.value = null
  }
}

const downloadTranscript = () => {
  // Generate payment transcript
  const student = authStore.user
  const transcript = generateTranscriptContent()

  // Create download
  const blob = new Blob([transcript], { type: 'text/plain' })
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `payment_transcript_${student?.username || 'student'}_${new Date().toISOString().split('T')[0]}.txt`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  window.URL.revokeObjectURL(url)
}

const generateTranscriptContent = () => {
  const student = authStore.user
  const lines = [
    '=' .repeat(60),
    '                    PAYMENT TRANSCRIPT',
    '                  University of Excellence',
    '=' .repeat(60),
    '',
    `Student: ${student?.fullName || student?.username || 'N/A'}`,
    `Student ID: ${student?.id || 'N/A'}`,
    `Generated: ${new Date().toLocaleString()}`,
    '',
    '-'.repeat(60),
    'PAYMENT HISTORY',
    '-'.repeat(60),
    ''
  ]

  paymentHistory.value.forEach(payment => {
    lines.push(`Semester: ${payment.semester}`)
    lines.push(`Amount: $${formatCurrency(payment.amount)}`)
    lines.push(`Paid: $${formatCurrency(payment.paidAmount)}`)
    lines.push(`Status: ${payment.status}`)
    lines.push(`Reference: ${payment.transactionReference || 'N/A'}`)
    lines.push(`Date: ${formatDate(payment.createdAt)}`)

    if (payment.enrollments?.length) {
      lines.push('Courses:')
      payment.enrollments.forEach(e => {
        lines.push(`  - ${e.course?.courseCode}: ${e.course?.courseName} ($${formatCurrency(e.course?.courseFee || 0)})`)
      })
    }
    lines.push('')
    lines.push('-'.repeat(40))
    lines.push('')
  })

  lines.push('')
  lines.push('=' .repeat(60))
  lines.push('SUMMARY')
  lines.push('=' .repeat(60))
  lines.push(`Total Fees: $${formatCurrency(financialSummary.value.totalFees)}`)
  lines.push(`Total Paid: $${formatCurrency(financialSummary.value.totalPaid)}`)
  lines.push(`Outstanding: $${formatCurrency(financialSummary.value.totalDue)}`)
  lines.push('')
  lines.push('This transcript is generated electronically.')
  lines.push('For official documents, please visit the Registrar\'s Office.')

  return lines.join('\n')
}

const initFeeBreakdownChart = () => {
  if (!feeBreakdownChart.value) return

  if (feeChartInstance) {
    feeChartInstance.destroy()
  }

  feeChartInstance = new Chart(feeBreakdownChart.value, {
    type: 'doughnut',
    data: {
      labels: feeCategories.value.map(c => c.name),
      datasets: [{
        data: feeCategories.value.map(c => c.amount),
        backgroundColor: chartColors,
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      cutout: '60%'
    }
  })
}

// Utility functions
const getPaymentStatusClass = (status) => {
  const classes = {
    'PENDING': 'bg-gray-100 text-gray-800',
    'DUE': 'bg-orange-100 text-orange-800',
    'PAID': 'bg-yellow-100 text-yellow-800',
    'APPROVED': 'bg-green-100 text-green-800',
    'REJECTED': 'bg-red-100 text-red-800',
    'PARTIAL': 'bg-blue-100 text-blue-800'
  }
  return classes[status] || 'bg-gray-100 text-gray-800'
}

const getTimelineDotColor = (status) => {
  const colors = {
    'PENDING': 'bg-gray-400',
    'DUE': 'bg-orange-400',
    'PAID': 'bg-yellow-400',
    'APPROVED': 'bg-green-500',
    'REJECTED': 'bg-red-500',
    'PARTIAL': 'bg-blue-500'
  }
  return colors[status] || 'bg-gray-400'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

const formatCurrency = (value) => {
  if (value === null || value === undefined) return '0.00'
  return Number(value).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getDaysUntilDue = (dueDate) => {
  if (!dueDate) return null
  const due = new Date(dueDate)
  const now = new Date()
  const diff = Math.ceil((due - now) / (1000 * 60 * 60 * 24))
  return diff
}

// Watch for data changes to update chart
watch(feeCategories, () => {
  initFeeBreakdownChart()
}, { deep: true })

// Lifecycle
onMounted(async () => {
  await Promise.all([loadCurrentPayment(), loadPaymentHistory()])
  setTimeout(initFeeBreakdownChart, 100)
})

onUnmounted(() => {
  if (feeChartInstance) {
    try {
      feeChartInstance.destroy()
    } catch (e) {
      // Ignore errors during chart destruction
    }
    feeChartInstance = null
  }
})
</script>
