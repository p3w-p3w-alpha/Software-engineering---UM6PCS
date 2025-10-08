import React, { useState } from 'react';

const UseCaseDiagrams = () => {
  const [selectedPortal, setSelectedPortal] = useState('student');

  return (
    <div className="p-8 bg-gray-50 min-h-screen">
      <h1 className="text-3xl font-bold text-center mb-2 text-gray-800">
        SAMS Use Case Diagrams
      </h1>
      <p className="text-center text-gray-600 mb-8">Section 3.3 - Use Case Diagrams and Narratives</p>

      {/* Portal Selection */}
      <div className="flex justify-center gap-4 mb-8">
        <button
          onClick={() => setSelectedPortal('student')}
          className={`px-6 py-3 rounded-lg font-semibold transition ${
            selectedPortal === 'student'
              ? 'bg-blue-600 text-white shadow-lg'
              : 'bg-white text-gray-700 hover:bg-gray-100'
          }`}
        >
          Student Portal
        </button>
        <button
          onClick={() => setSelectedPortal('faculty')}
          className={`px-6 py-3 rounded-lg font-semibold transition ${
            selectedPortal === 'faculty'
              ? 'bg-green-600 text-white shadow-lg'
              : 'bg-white text-gray-700 hover:bg-gray-100'
          }`}
        >
          Faculty Portal
        </button>
        <button
          onClick={() => setSelectedPortal('admin')}
          className={`px-6 py-3 rounded-lg font-semibold transition ${
            selectedPortal === 'admin'
              ? 'bg-purple-600 text-white shadow-lg'
              : 'bg-white text-gray-700 hover:bg-gray-100'
          }`}
        >
          Administrator Portal
        </button>
      </div>

      {/* Student Portal */}
      {selectedPortal === 'student' && (
        <div className="bg-white p-6 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-blue-700 border-b-2 border-blue-700 pb-2">
            Student Portal Use Cases
          </h2>
          
          <svg viewBox="0 0 900 650" className="w-full border-2 border-gray-300 rounded mb-6">
            {/* Student Actor */}
            <g>
              <circle cx="80" cy="325" r="22" fill="none" stroke="#2563eb" strokeWidth="2.5"/>
              <line x1="80" y1="347" x2="80" y2="390" stroke="#2563eb" strokeWidth="2.5"/>
              <line x1="55" y1="367" x2="105" y2="367" stroke="#2563eb" strokeWidth="2.5"/>
              <line x1="80" y1="390" x2="55" y2="425" stroke="#2563eb" strokeWidth="2.5"/>
              <line x1="80" y1="390" x2="105" y2="425" stroke="#2563eb" strokeWidth="2.5"/>
              <text x="50" y="445" fontSize="15" fontWeight="bold" fill="#2563eb">Student</text>
            </g>

            {/* System Boundary */}
            <rect x="170" y="40" width="680" height="570" fill="none" stroke="#374151" strokeWidth="2.5" rx="12"/>
            <text x="510" y="28" fontSize="18" fontWeight="bold" textAnchor="middle" fill="#374151">
              Student Portal System
            </text>

            {/* Row 1 Use Cases */}
            <ellipse cx="310" cy="100" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="310" y="105" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">View Dashboard</text>

            <ellipse cx="510" cy="100" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="510" y="100" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Register for</text>
            <text x="510" y="115" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Courses</text>

            <ellipse cx="710" cy="100" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="710" y="100" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Manage Class</text>
            <text x="710" y="115" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Schedule</text>

            {/* Row 2 Use Cases */}
            <ellipse cx="310" cy="210" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="310" y="210" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Track Degree</text>
            <text x="310" y="225" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Progress</text>

            <ellipse cx="510" cy="210" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="510" y="215" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">View Grades</text>

            <ellipse cx="710" cy="210" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="710" y="210" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Generate</text>
            <text x="710" y="225" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Transcript</text>

            {/* Row 3 Use Cases */}
            <ellipse cx="310" cy="320" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="310" y="320" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">View Financial</text>
            <text x="310" y="335" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Information</text>

            <ellipse cx="510" cy="320" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="510" y="325" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Make Payment</text>

            <ellipse cx="710" cy="320" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="710" y="320" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Join/Create</text>
            <text x="710" y="335" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Study Groups</text>

            {/* Row 4 Use Cases */}
            <ellipse cx="410" cy="430" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="410" y="430" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">View</text>
            <text x="410" y="445" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Notifications</text>

            <ellipse cx="610" cy="430" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="610" y="435" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Communicate</text>

            {/* Row 5 Use Case */}
            <ellipse cx="510" cy="540" rx="95" ry="32" fill="#dbeafe" stroke="#2563eb" strokeWidth="2"/>
            <text x="510" y="545" fontSize="13" textAnchor="middle" fontWeight="500" fill="#1e40af">Login/Logout</text>

            {/* Lines connecting actor to use cases */}
            <line x1="105" y1="315" x2="215" y2="100" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="318" x2="415" y2="100" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="320" x2="615" y2="100" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="323" x2="215" y2="210" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="325" x2="415" y2="210" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="327" x2="615" y2="210" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="329" x2="215" y2="320" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="330" x2="415" y2="320" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="332" x2="615" y2="320" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="335" x2="315" y2="430" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="337" x2="515" y2="430" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="340" x2="415" y2="540" stroke="#6b7280" strokeWidth="1.5"/>
          </svg>

          <div className="space-y-6">
            <div>
              <h3 className="text-xl font-semibold text-blue-700 mb-3">Use Case Narratives</h3>
              
              <div className="bg-blue-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-blue-900 mb-2">UC-S1: Register for Courses</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Student</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Student searches for available courses, checks prerequisites, and registers for desired courses.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Student is logged in and within registration period.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Student navigates to course registration section</li>
                  <li>Student searches for courses using filters (department, time, instructor)</li>
                  <li>System displays matching courses with details (schedule, capacity, prerequisites)</li>
                  <li>Student selects a course to view detailed information</li>
                  <li>System verifies prerequisite completion</li>
                  <li>System checks for time conflicts with existing schedule</li>
                  <li>Student clicks "Register" button</li>
                  <li>System enrolls student and updates available seats</li>
                  <li>System adds course fee to student's billing</li>
                  <li>System displays confirmation message</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If prerequisite not met: System displays error and prevents registration</li>
                  <li>If time conflict exists: System alerts student and prevents registration</li>
                  <li>If course is full: System adds student to waitlist</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Student is enrolled in course; seat count updated; fee added to billing.</p>
              </div>

              <div className="bg-blue-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-blue-900 mb-2">UC-S2: Make Payment</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Student</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Student submits payment for enrolled courses before semester begins.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Student has enrolled in at least one course.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Student navigates to financial information section</li>
                  <li>System displays total amount due based on enrolled courses</li>
                  <li>Student selects payment method (credit card or bank transfer)</li>
                  <li>Student enters payment information</li>
                  <li>Student confirms payment submission</li>
                  <li>System validates payment information format</li>
                  <li>System submits payment to administrator queue for validation</li>
                  <li>System displays payment pending confirmation message</li>
                  <li>Student receives notification when payment is validated</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If payment information is invalid: System displays error and requests correction</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Payment submitted for validation; student awaits course access approval.</p>
              </div>

              <div className="bg-blue-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-blue-900 mb-2">UC-S3: Track Degree Progress</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Student</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Student views progress toward graduation requirements.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Student is logged in.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Student navigates to degree progress section</li>
                  <li>System calculates completed credits vs. required credits</li>
                  <li>System displays completed courses by requirement category</li>
                  <li>System identifies remaining requirements</li>
                  <li>System calculates projected graduation date</li>
                  <li>System displays alerts for missing prerequisites or unfulfilled requirements</li>
                  <li>Student reviews progress information</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Student understands current academic standing and remaining requirements.</p>
              </div>

              <div className="bg-blue-50 p-4 rounded-lg">
                <h4 className="font-semibold text-blue-900 mb-2">UC-S4: Join/Create Study Groups</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Student</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Student creates or joins study groups for collaboration.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Student is logged in and enrolled in at least one course.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow (Create):</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Student navigates to study groups section</li>
                  <li>Student clicks "Create Study Group"</li>
                  <li>Student selects course for the study group</li>
                  <li>Student enters group name and description</li>
                  <li>System creates study group with student as creator</li>
                  <li>Student invites other students from the course roster</li>
                  <li>System sends invitations to selected students</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Main Flow (Join):</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Student views available study groups for enrolled courses</li>
                  <li>Student selects a study group to view details</li>
                  <li>Student clicks "Join Group"</li>
                  <li>System adds student to group member list</li>
                  <li>Student can view member contact information and send messages</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Student is member of study group; can communicate with other members.</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Faculty Portal */}
      {selectedPortal === 'faculty' && (
        <div className="bg-white p-6 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-green-700 border-b-2 border-green-700 pb-2">
            Faculty Portal Use Cases
          </h2>
          
          <svg viewBox="0 0 900 550" className="w-full border-2 border-gray-300 rounded mb-6">
            {/* Faculty Actor */}
            <g>
              <circle cx="80" cy="275" r="22" fill="none" stroke="#16a34a" strokeWidth="2.5"/>
              <line x1="80" y1="297" x2="80" y2="340" stroke="#16a34a" strokeWidth="2.5"/>
              <line x1="55" y1="317" x2="105" y2="317" stroke="#16a34a" strokeWidth="2.5"/>
              <line x1="80" y1="340" x2="55" y2="375" stroke="#16a34a" strokeWidth="2.5"/>
              <line x1="80" y1="340" x2="105" y2="375" stroke="#16a34a" strokeWidth="2.5"/>
              <text x="50" y="395" fontSize="15" fontWeight="bold" fill="#16a34a">Faculty</text>
            </g>

            {/* System Boundary */}
            <rect x="170" y="40" width="680" height="470" fill="none" stroke="#374151" strokeWidth="2.5" rx="12"/>
            <text x="510" y="28" fontSize="18" fontWeight="bold" textAnchor="middle" fill="#374151">
              Faculty Portal System
            </text>

            {/* Row 1 Use Cases */}
            <ellipse cx="310" cy="110" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="310" y="110" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Create Course</text>
            <text x="310" y="125" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Offerings</text>

            <ellipse cx="510" cy="110" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="510" y="110" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Manage Course</text>
            <text x="510" y="125" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Rosters</text>

            <ellipse cx="710" cy="110" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="710" y="110" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Post Course</text>
            <text x="710" y="125" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Materials</text>

            {/* Row 2 Use Cases */}
            <ellipse cx="310" cy="220" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="310" y="225" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Enter Grades</text>

            <ellipse cx="510" cy="220" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="510" y="220" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Submit Final</text>
            <text x="510" y="235" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Grades</text>

            <ellipse cx="710" cy="220" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="710" y="220" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">View Student</text>
            <text x="710" y="235" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Profiles</text>

            {/* Row 3 Use Cases */}
            <ellipse cx="410" cy="330" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="410" y="330" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Communicate</text>
            <text x="410" y="345" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">with Students</text>

            <ellipse cx="610" cy="330" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="610" y="335" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Track Attendance</text>

            {/* Row 4 Use Case */}
            <ellipse cx="510" cy="440" rx="95" ry="32" fill="#dcfce7" stroke="#16a34a" strokeWidth="2"/>
            <text x="510" y="445" fontSize="13" textAnchor="middle" fontWeight="500" fill="#166534">Login/Logout</text>

            {/* Lines connecting actor to use cases */}
            <line x1="105" y1="268" x2="215" y2="110" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="270" x2="415" y2="110" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="272" x2="615" y2="110" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="274" x2="215" y2="220" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="276" x2="415" y2="220" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="278" x2="615" y2="220" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="280" x2="315" y2="330" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="282" x2="515" y2="330" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="285" x2="415" y2="440" stroke="#6b7280" strokeWidth="1.5"/>
          </svg>

          <div className="space-y-6">
            <div>
              <h3 className="text-xl font-semibold text-green-700 mb-3">Use Case Narratives</h3>
              
              <div className="bg-green-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-green-900 mb-2">UC-F1: Create Course Offerings</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Faculty</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Faculty member creates a new course offering for an upcoming semester.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Faculty is logged in and authorized to create course offerings.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Faculty navigates to course management section</li>
                  <li>Faculty clicks "Create Course Offering"</li>
                  <li>Faculty selects semester and course from catalog</li>
                  <li>Faculty specifies course schedule (days of week and time)</li>
                  <li>Faculty sets maximum enrollment capacity</li>
                  <li>Faculty defines prerequisite courses (if any)</li>
                  <li>Faculty enters course location/room information</li>
                  <li>Faculty reviews and confirms course offering details</li>
                  <li>System validates schedule for conflicts</li>
                  <li>System creates course offering and makes it available for registration</li>
                  <li>System displays confirmation message</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If schedule conflict exists: System alerts faculty and prevents creation until resolved</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Course offering is created and available for student registration.</p>
              </div>

              <div className="bg-green-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-green-900 mb-2">UC-F2: Enter Grades</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Faculty</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Faculty enters or updates grades for students in their courses.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Faculty is logged in and has enrolled students in their course.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Faculty navigates to grade management section</li>
                  <li>Faculty selects the course for grade entry</li>
                  <li>System displays enrolled student roster</li>
                  <li>Faculty selects assignment or exam type</li>
                  <li>Faculty enters grades for each student</li>
                  <li>System validates grade entries (within acceptable range)</li>
                  <li>Faculty clicks "Submit Grades"</li>
                  <li>System saves grades to database</li>
                  <li>System triggers automatic GPA recalculation for affected students</li>
                  <li>System sends notifications to students about new grades</li>
                  <li>System displays confirmation to faculty</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If grade is out of range: System displays error and prevents submission</li>
                  <li>If faculty wants to modify existing grade: System allows update and logs modification with timestamp</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Grades are saved; student GPAs updated; notifications sent.</p>
              </div>

              <div className="bg-green-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-green-900 mb-2">UC-F3: Submit Final Grades</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Faculty</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Faculty submits final course grades at semester end.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Semester is ending and final grade submission period is active.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Faculty navigates to final grade submission section</li>
                  <li>Faculty selects the course for final grade submission</li>
                  <li>System displays student roster with calculated course grades</li>
                  <li>Faculty reviews all final grades for accuracy</li>
                  <li>Faculty enters or adjusts final letter grades if needed</li>
                  <li>Faculty clicks "Submit Final Grades"</li>
                  <li>System validates that all students have grades assigned</li>
                  <li>System locks final grades and updates transcripts</li>
                  <li>System updates degree progress tracking for all students</li>
                  <li>System sends confirmation to faculty</li>
                  <li>System notifies students that final grades are posted</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If some students missing grades: System alerts faculty and prevents submission</li>
                  <li>If submission deadline passed: System requires administrator approval for late submission</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Final grades submitted and locked; transcripts updated; students notified.</p>
              </div>

              <div className="bg-green-50 p-4 rounded-lg">
                <h4 className="font-semibold text-green-900 mb-2">UC-F4: Post Course Materials</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Faculty</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Faculty posts course materials for enrolled students.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Faculty is logged in and course has enrolled students.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Faculty navigates to course materials section</li>
                  <li>Faculty selects the course</li>
                  <li>Faculty clicks "Post New Material"</li>
                  <li>Faculty enters material title and description</li>
                  <li>Faculty uploads text content (syllabus, announcements, notes)</li>
                  <li>Faculty optionally sets visibility date</li>
                  <li>Faculty clicks "Publish"</li>
                  <li>System saves material and makes it visible to enrolled students</li>
                  <li>System sends notification to students about new material</li>
                  <li>System displays confirmation to faculty</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>Faculty can edit or remove previously posted materials</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Course material is published and accessible to enrolled students.</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Administrator Portal */}
      {selectedPortal === 'admin' && (
        <div className="bg-white p-6 rounded-lg shadow-lg">
          <h2 className="text-2xl font-semibold mb-6 text-purple-700 border-b-2 border-purple-700 pb-2">
            Administrator Portal Use Cases
          </h2>
          
          <svg viewBox="0 0 900 550" className="w-full border-2 border-gray-300 rounded mb-6">
            {/* Admin Actor */}
            <g>
              <circle cx="80" cy="275" r="22" fill="none" stroke="#9333ea" strokeWidth="2.5"/>
              <line x1="80" y1="297" x2="80" y2="340" stroke="#9333ea" strokeWidth="2.5"/>
              <line x1="55" y1="317" x2="105" y2="317" stroke="#9333ea" strokeWidth="2.5"/>
              <line x1="80" y1="340" x2="55" y2="375" stroke="#9333ea" strokeWidth="2.5"/>
              <line x1="80" y1="340" x2="105" y2="375" stroke="#9333ea" strokeWidth="2.5"/>
              <text x="35" y="395" fontSize="15" fontWeight="bold" fill="#9333ea">Administrator</text>
            </g>

            {/* System Boundary */}
            <rect x="170" y="40" width="680" height="470" fill="none" stroke="#374151" strokeWidth="2.5" rx="12"/>
            <text x="510" y="28" fontSize="18" fontWeight="bold" textAnchor="middle" fill="#374151">
              Administrator Portal System
            </text>

            {/* Row 1 Use Cases */}
            <ellipse cx="310" cy="110" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="310" y="110" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Create/Manage</text>
            <text x="310" y="125" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">User Accounts</text>

            <ellipse cx="510" cy="110" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="510" y="110" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Assign Roles &</text>
            <text x="510" y="125" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Permissions</text>

            <ellipse cx="710" cy="110" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="710" y="110" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Manage Security</text>
            <text x="710" y="125" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Settings</text>

            {/* Row 2 Use Cases */}
            <ellipse cx="310" cy="220" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="310" y="220" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Validate</text>
            <text x="310" y="235" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Payments</text>

            <ellipse cx="510" cy="220" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="510" y="220" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Grant Course</text>
            <text x="510" y="235" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Access</text>

            <ellipse cx="710" cy="220" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="710" y="220" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">View Student</text>
            <text x="710" y="235" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Information</text>

            {/* Row 3 Use Cases */}
            <ellipse cx="410" cy="330" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="410" y="330" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">View Faculty</text>
            <text x="410" y="345" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Information</text>

            <ellipse cx="610" cy="330" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="610" y="330" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Configure System</text>
            <text x="610" y="345" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Settings</text>

            {/* Row 4 Use Case */}
            <ellipse cx="510" cy="440" rx="95" ry="32" fill="#f3e8ff" stroke="#9333ea" strokeWidth="2"/>
            <text x="510" y="445" fontSize="13" textAnchor="middle" fontWeight="500" fill="#6b21a8">Login/Logout</text>

            {/* Lines connecting actor to use cases */}
            <line x1="105" y1="268" x2="215" y2="110" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="270" x2="415" y2="110" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="272" x2="615" y2="110" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="274" x2="215" y2="220" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="276" x2="415" y2="220" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="278" x2="615" y2="220" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="280" x2="315" y2="330" stroke="#6b7280" strokeWidth="1.5"/>
            <line x1="105" y1="282" x2="515" y2="330" stroke="#6b7280" strokeWidth="1.5"/>
            
            <line x1="105" y1="285" x2="415" y2="440" stroke="#6b7280" strokeWidth="1.5"/>
          </svg>

          <div className="space-y-6">
            <div>
              <h3 className="text-xl font-semibold text-purple-700 mb-3">Use Case Narratives</h3>
              
              <div className="bg-purple-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-purple-900 mb-2">UC-A1: Create/Manage User Accounts</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Administrator</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Administrator creates and manages user accounts for students, faculty, and staff.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Administrator is logged in with user management permissions.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow (Create):</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Administrator navigates to user management section</li>
                  <li>Administrator clicks "Create New Account"</li>
                  <li>Administrator selects account type (Student, Faculty, or Staff)</li>
                  <li>Administrator enters user information (name, email, ID number)</li>
                  <li>Administrator creates unique username</li>
                  <li>Administrator generates or sets initial password</li>
                  <li>Administrator assigns appropriate role and permissions</li>
                  <li>Administrator reviews account details</li>
                  <li>System validates username uniqueness</li>
                  <li>System creates user account in database</li>
                  <li>System sends welcome email with login credentials to user</li>
                  <li>System displays confirmation to administrator</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Main Flow (Edit):</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Administrator searches for existing user account</li>
                  <li>Administrator selects account to edit</li>
                  <li>Administrator modifies account information</li>
                  <li>System saves changes and logs modification</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If username already exists: System displays error and requests different username</li>
                  <li>Administrator can deactivate or delete accounts when needed</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> User account created/modified; user can log into system.</p>
              </div>

              <div className="bg-purple-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-purple-900 mb-2">UC-A2: Validate Payments</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Administrator</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Administrator validates student payment submissions to grant course access.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Administrator is logged in; students have submitted payments.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Administrator navigates to payment validation section</li>
                  <li>System displays queue of pending payment submissions</li>
                  <li>Administrator selects a payment to review</li>
                  <li>System displays payment details (student name, amount, method, courses enrolled)</li>
                  <li>Administrator verifies payment against financial records:
                    <ul className="ml-4 list-disc">
                      <li>For credit card: Verifies transaction approval</li>
                      <li>For bank transfer: Verifies transfer confirmation number</li>
                    </ul>
                  </li>
                  <li>Administrator clicks "Validate Payment"</li>
                  <li>System records validation with timestamp and administrator ID</li>
                  <li>System automatically grants course access for enrolled courses</li>
                  <li>System updates student's financial account balance</li>
                  <li>System sends notification to student confirming payment and course access</li>
                  <li>System removes payment from pending queue</li>
                  <li>System displays confirmation to administrator</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If payment cannot be verified: Administrator can reject payment with reason; student is notified to resubmit</li>
                  <li>If payment already validated: System prevents duplicate validation</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Payment validated; course access granted; student notified; financial records updated.</p>
              </div>

              <div className="bg-purple-50 p-4 rounded-lg mb-4">
                <h4 className="font-semibold text-purple-900 mb-2">UC-A3: Grant Course Access</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Administrator</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Administrator manually grants or revokes course access for students.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Administrator is logged in with course access management permissions.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow (Grant):</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Administrator navigates to course access management section</li>
                  <li>Administrator searches for student by name or ID</li>
                  <li>System displays student's enrolled courses and access status</li>
                  <li>Administrator selects course(s) to grant access</li>
                  <li>Administrator enters reason for manual access grant (optional)</li>
                  <li>Administrator clicks "Grant Access"</li>
                  <li>System updates course access permissions</li>
                  <li>System logs action with administrator ID and timestamp</li>
                  <li>System sends notification to student</li>
                  <li>Student can now access course materials</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Main Flow (Revoke):</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Administrator selects course(s) to revoke access</li>
                  <li>Administrator enters reason for revocation (required)</li>
                  <li>System removes course access permissions</li>
                  <li>System logs action and notifies student</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> Course access granted or revoked; action logged; student notified.</p>
              </div>

              <div className="bg-purple-50 p-4 rounded-lg">
                <h4 className="font-semibold text-purple-900 mb-2">UC-A4: Configure System Settings</h4>
                <p className="text-sm text-gray-700 mb-2"><strong>Actor:</strong> Administrator</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Description:</strong> Administrator configures system-wide settings and academic calendar.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Preconditions:</strong> Administrator is logged in with system configuration permissions.</p>
                <p className="text-sm text-gray-700 mb-2"><strong>Main Flow:</strong></p>
                <ol className="text-sm text-gray-700 ml-6 list-decimal space-y-1">
                  <li>Administrator navigates to system configuration section</li>
                  <li>Administrator selects configuration category:
                    <ul className="ml-4 list-disc">
                      <li>Academic calendar dates (semester start/end, holidays)</li>
                      <li>Registration periods (open/close dates)</li>
                      <li>Payment deadlines</li>
                      <li>Notification settings (timing, frequency)</li>
                      <li>Security policies (password requirements, session timeout)</li>
                    </ul>
                  </li>
                  <li>Administrator enters or modifies configuration values</li>
                  <li>System validates configuration values for consistency</li>
                  <li>Administrator reviews changes</li>
                  <li>Administrator clicks "Save Configuration"</li>
                  <li>System applies new settings across the platform</li>
                  <li>System logs configuration change with administrator ID and timestamp</li>
                  <li>System displays confirmation message</li>
                  <li>Changes take effect immediately or at specified time</li>
                </ol>
                <p className="text-sm text-gray-700 mt-2"><strong>Alternative Flow:</strong></p>
                <ul className="text-sm text-gray-700 ml-6 list-disc">
                  <li>If configuration values are invalid: System displays error and prevents saving</li>
                  <li>Administrator can revert to previous configuration if needed</li>
                </ul>
                <p className="text-sm text-gray-700 mt-2"><strong>Postconditions:</strong> System settings updated; changes applied; action logged.</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Summary Section */}
      <div className="mt-8 bg-gray-100 p-6 rounded-lg">
        <h3 className="text-xl font-semibold text-gray-800 mb-4">Use Case Diagram Summary</h3>
        <p className="text-gray-700 mb-4">
          The use case diagrams above illustrate the primary interactions between system actors (Students, Faculty, and Administrators) 
          and the SAMS platform. Each diagram represents a distinct portal with role-specific functionalities:
        </p>
        
        <div className="grid md:grid-cols-3 gap-4">
          <div className="bg-blue-50 p-4 rounded-lg border-2 border-blue-200">
            <h4 className="font-semibold text-blue-800 mb-2">Student Portal</h4>
            <p className="text-sm text-gray-700">
              Focuses on academic self-service: course registration, grade viewing, degree progress tracking, 
              payment processing, and collaboration through study groups.
            </p>
            <p className="text-sm text-blue-700 mt-2 font-medium">11 Use Cases</p>
          </div>
          
          <div className="bg-green-50 p-4 rounded-lg border-2 border-green-200">
            <h4 className="font-semibold text-green-800 mb-2">Faculty Portal</h4>
            <p className="text-sm text-gray-700">
              Emphasizes course management: creating offerings, managing rosters, posting materials, 
              entering grades, and tracking student performance.
            </p>
            <p className="text-sm text-green-700 mt-2 font-medium">9 Use Cases</p>
          </div>
          
          <div className="bg-purple-50 p-4 rounded-lg border-2 border-purple-200">
            <h4 className="font-semibold text-purple-800 mb-2">Administrator Portal</h4>
            <p className="text-sm text-gray-700">
              Provides system oversight: user management, payment validation, course access control, 
              and system-wide configuration.
            </p>
            <p className="text-sm text-purple-700 mt-2 font-medium">9 Use Cases</p>
          </div>
        </div>

        <div className="mt-6 bg-white p-4 rounded-lg border border-gray-300">
          <h4 className="font-semibold text-gray-800 mb-2">Key Integration Points</h4>
          <ul className="text-sm text-gray-700 space-y-1 ml-4 list-disc">
            <li><strong>Course Registration Workflow:</strong> Faculty creates offerings → Students register → System manages enrollment</li>
            <li><strong>Payment & Access Workflow:</strong> Students submit payment → Administrators validate → System grants course access</li>
            <li><strong>Grade Submission Workflow:</strong> Faculty enters grades → System updates GPAs → Students receive notifications</li>
            <li><strong>Authentication:</strong> All actors use secure login/logout functionality with role-based access control</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default UseCaseDiagrams;