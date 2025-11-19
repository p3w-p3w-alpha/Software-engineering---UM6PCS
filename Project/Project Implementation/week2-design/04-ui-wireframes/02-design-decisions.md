# SAMS UI/UX Design Decisions

## Purpose
This document explains the rationale behind key UI/UX design decisions for SAMS, including layout choices, interaction patterns, and user experience considerations.

---

## Design Philosophy

### Core Principles
1. **Simplicity**: Clean, uncluttered interfaces
2. **Consistency**: Uniform patterns across all screens
3. **Efficiency**: Minimize clicks to complete tasks
4. **Accessibility**: WCAG 2.1 Level AA compliance
5. **Responsiveness**: Mobile-first approach

---

## Layout Decisions

### 1. Sidebar Navigation vs Top Navigation

**Decision:** **Sidebar Navigation** (left-aligned)

**Rationale:**
- ✅ **Vertical space**: More menu items without dropdown clutter
- ✅ **Clarity**: Always visible, no hidden menus
- ✅ **Scalability**: Easy to add new menu items
- ✅ **Modern**: Common in SaaS applications (Gmail, Slack, GitHub)
- ✅ **Mobile adaptation**: Collapses to hamburger menu

**Alternative Considered:** Top horizontal navigation
- ❌ Limited horizontal space for many items
- ❌ Requires dropdowns for sub-menus
- ❌ Less scannable

**Implementation:**
```
Desktop (>1024px): Fixed 240px sidebar, always visible
Tablet (768-1023px): Collapsible sidebar, icon + text
Mobile (<768px): Hamburger menu, full-screen overlay
```

---

### 2. Dashboard Layout: Cards vs Lists

**Decision:** **Card-based Layout** for summary metrics

**Rationale:**
- ✅ **Visual hierarchy**: Important metrics stand out
- ✅ **Scannability**: Quick overview at a glance
- ✅ **Responsive**: Cards stack naturally on mobile
- ✅ **Flexibility**: Can show icons, numbers, charts
- ✅ **Modern aesthetic**: Clean, professional appearance

**Where Used:**
- Dashboard summary cards (enrolled courses, GPA, credits)
- Admin statistics (pending payments, active users)

**Alternative:** Traditional table for all data
- ❌ Less visually engaging
- ❌ Harder to scan quickly
- ❌ Not responsive-friendly

---

### 3. Table Design: Data Density vs Readability

**Decision:** **Balanced approach** - moderate density with expandable rows

**Key Choices:**
- Row height: 48px (desktop), 56px (mobile) - comfortable tap targets
- Font size: 14px body text, 16px on mobile
- Alternating row colors for readability
- Expandable rows for detailed information (grades breakdown)
- Pagination vs infinite scroll: **Pagination** (predictable, better performance)

**Rationale:**
- Meets WCAG tap target size (44x44px minimum)
- Readability without excessive scrolling
- Performance: Pagination limits DOM elements

---

## Interaction Patterns

### 1. Multi-Step Course Registration

**Decision:** **Shopping cart** model vs immediate enrollment

**Flow:**
```
1. Browse courses
2. Add to cart (multiple courses)
3. Review cart
4. Check for conflicts
5. Submit all enrollments at once
```

**Rationale:**
- ✅ **User control**: Review before committing
- ✅ **Efficiency**: Enroll in multiple courses at once
- ✅ **Error prevention**: Conflict detection before submission
- ✅ **Familiar pattern**: E-commerce metaphor

**Alternative:** Immediate enrollment per course
- ❌ Each click enrolls immediately (risky)
- ❌ Can't review overall schedule first
- ❌ Harder to undo mistakes

---

### 2. Grade Entry: Inline Editing vs Modal Forms

**Decision:** **Inline spreadsheet-style** editing

**Rationale:**
- ✅ **Faculty familiarity**: Excel-like interface
- ✅ **Efficiency**: Enter all grades without popup interruptions
- ✅ **Bulk operations**: Copy-paste, keyboard navigation
- ✅ **Visual context**: See all students at once

**Implementation:**
- Click cell to edit
- Tab key moves to next cell
- Enter key saves and moves down
- CSV import for bulk entry

**Alternative:** Modal form per student
- ❌ Slow for large classes (28+ students)
- ❌ Requires many clicks

---

### 3. Search and Filter Pattern

**Decision:** **Persistent filters** with instant results

**Components:**
- Search input (text)
- Dropdown filters (department, semester, etc.)
- Checkboxes (available seats only)
- Clear all button
- Filter button (optional - filters apply on change)

**Behavior:**
- Search debounced (300ms delay)
- Filters applied on selection change
- Results update without page refresh
- Filter state persisted in URL (bookmarkable)

---

## Color and Visual Hierarchy

### 1. Status Colors (Semantic)

**Decision:** Consistent color coding for statuses

| Status | Color | Use Case |
|--------|-------|----------|
| Success/Enrolled | Green (#52c41a) | Enrolled status, validation success |
| Warning/Pending | Orange (#faad14) | Pending payment, waitlisted |
| Error/Rejected | Red (#f5222d) | Rejected payment, failed validation |
| Info/Neutral | Blue (#1890ff) | General information, unread notifications |
| Disabled | Gray (#d9d9d9) | Unavailable actions, full courses |

**Rationale:**
- Universal color associations (green=go, red=stop)
- WCAG AA contrast ratios met
- Color-blind friendly (labels + icons, not color alone)

---

### 2. Typography Hierarchy

**Decision:** Clear heading hierarchy with adequate spacing

**Scale:**
```
H1: 32px, Bold, Page titles
H2: 24px, Bold, Section headers
H3: 18px, Semibold, Subsection headers
Body: 14px, Regular, Main content
Small: 12px, Regular, Metadata, captions
```

**Rationale:**
- Clear visual hierarchy
- Readable at all sizes
- Adequate line-height (1.5) for readability
- Scalable for responsive design

---

## Form Design

### 1. Validation: Real-Time vs On-Submit

**Decision:** **Hybrid approach**

**Implementation:**
- **On focus out**: Validate individual field
- **On submit**: Validate entire form
- **Immediate feedback**: Show errors inline, below field

**Example:**
```
Password field:
<Input type="password" />
❌ Password must be at least 8 characters  ← Shows on blur if invalid
```

**Rationale:**
- Not disruptive (no errors while typing)
- Immediate feedback after leaving field
- Clear error association

---

### 2. Required Field Indicators

**Decision:** **Asterisk (*) + Label** + "Required" text

**Example:**
```
Email Address *
<_________________>
* Required field
```

**Rationale:**
- Visible indicator (asterisk)
- Not reliant on color alone (accessibility)
- Clear expectation

---

## Loading States

### 1. Data Loading Pattern

**Decision:** **Skeleton screens** for initial load, **spinners** for actions

**Initial Page Load:**
```
┌────────────────────┐
│ ░░░░░░░░░░░░░░░░  │  ← Skeleton placeholder (gray boxes)
│ ░░░░░░  ░░░░░░░   │
│ ░░░░░░░░░░░░░░░░  │
└────────────────────┘
```

**Action Loading (button click):**
```
[Submitting... ⊙]  ← Spinner + text
```

**Rationale:**
- Skeletons reduce perceived wait time
- Clear indication of structure being loaded
- Spinners for explicit actions (submit, save)

---

## Error Handling

### 1. Error Message Placement

**Decision:** **Inline errors** below form fields, **Toast notifications** for global errors

**Form Errors:**
```
Email Address *
<invalid@email>
❌ Please enter a valid email address  ← Inline, red text
```

**Global Errors (API failure):**
```
┌────────────────────────────────────┐
│ ❌ Error: Unable to save data      │  ← Toast notification (top-right)
│    Please try again later          │
│                              [✕]   │
└────────────────────────────────────┘
```

**Rationale:**
- Inline errors: Clear field-to-error association
- Toasts: Non-blocking, auto-dismiss
- Different severity handled differently

---

### 2. Empty States

**Decision:** **Helpful empty states** with action prompts

**Example (No Courses Enrolled):**
```
┌─────────────────────────────────────┐
│                                      │
│        📚  No courses enrolled       │
│                                      │
│   You haven't enrolled in any        │
│   courses for this semester yet.     │
│                                      │
│      [Browse Available Courses]      │
│                                      │
└─────────────────────────────────────┘
```

**Rationale:**
- Informative (explains why empty)
- Actionable (provides next step)
- Friendly tone

---

## Notification Design

### 1. Notification System

**Decision:** **Icon badge** + **Dropdown panel** + **Toast for new items**

**Header Notification Icon:**
```
🔔 (3)  ← Badge shows unread count
```

**Dropdown Panel (on click):**
```
┌─────────────────────────────────┐
│ Notifications        [Mark All] │
├─────────────────────────────────┤
│ 🔵 New grade posted - CS301     │  ← Unread (blue dot)
│    2 minutes ago                │
├─────────────────────────────────┤
│ ⚪ Payment validated             │  ← Read (gray dot)
│    1 hour ago                   │
└─────────────────────────────────┘
```

**Toast (new notification arrives):**
```
┌─────────────────────────────────┐
│ 🔔 New Grade Posted             │  ← Auto-dismiss after 5s
│    CS301 - Homework 3           │
│                          [View] │
└─────────────────────────────────┘
```

**Rationale:**
- Badge: Always visible count
- Dropdown: Full list without leaving page
- Toast: Real-time alerts for new items

---

## Accessibility Decisions

### 1. Keyboard Navigation

**Decision:** **Full keyboard support** for all interactive elements

**Implementation:**
- Tab order: Logical flow (top-left to bottom-right)
- Focus indicators: 2px blue outline
- Skip links: "Skip to main content" for screen readers
- Escape key: Close modals/dropdowns
- Enter/Space: Activate buttons

---

### 2. Screen Reader Support

**Decision:** **ARIA labels** for all non-obvious elements

**Example:**
```html
<button aria-label="Close notification panel">
  ✕
</button>

<input type="search" aria-label="Search courses by name or code" />

<div role="status" aria-live="polite">
  12 search results found
</div>
```

---

## Mobile-Specific Decisions

### 1. Bottom Navigation Bar

**Decision:** **Bottom nav** for primary actions on mobile

**Layout:**
```
┌────────────────────┐
│   Page Content     │
│                    │
│                    │
├────────────────────┤
│ Home Courses Grades│  ← Bottom nav (always visible)
└────────────────────┘
```

**Rationale:**
- Thumb-friendly (easier to reach)
- Always accessible
- Common mobile pattern (Instagram, Twitter)

---

### 2. Touch Targets

**Decision:** **Minimum 44x44px** tap targets (WCAG requirement)

**Implementation:**
- Buttons: 48px height (desktop), 56px (mobile)
- Table rows: 56px height on mobile
- Padding around links for larger hit areas

---

## Performance Optimizations

### 1. Lazy Loading

**Decision:** **Lazy load** routes and images

**Implementation:**
```javascript
// Vue Router lazy loading
const CourseRegistration = () => import('./views/CourseRegistration.vue');

// Image lazy loading
<img src="..." loading="lazy" />
```

**Rationale:**
- Faster initial page load
- Load only what's needed
- Better mobile experience (data savings)

---

### 2. Pagination vs Infinite Scroll

**Decision:** **Pagination** for data tables

**Rationale:**
- ✅ Predictable (users know how much data)
- ✅ Better performance (limited DOM elements)
- ✅ Bookmarkable (page 3 in URL)
- ✅ Easier to find specific item

**Alternative:** Infinite scroll
- ❌ Harder to reach footer
- ❌ Can't bookmark position
- ❌ Performance issues with many items

---

## Consistency Patterns

### 1. Action Button Placement

**Decision:** **Primary action on right**, **cancel on left**

**Example:**
```
[Cancel]                [Submit]
  ↑                        ↑
Secondary (left)      Primary (right)
```

**Rationale:**
- Matches reading direction (left to right)
- Primary action in natural flow
- Consistent across all modals/forms

---

### 2. Icon Usage

**Decision:** **Icons + text labels** (not icons alone)

**Example:**
```
[📥 Download]   NOT   [📥]
```

**Rationale:**
- Clearer meaning (not ambiguous)
- Accessible (text for screen readers)
- Faster comprehension

---

## Design System Benefits

**Achieved Through Decisions:**
1. **Consistency**: Users learn once, apply everywhere
2. **Efficiency**: Reusable components speed development
3. **Accessibility**: Built-in compliance
4. **Maintainability**: Changes propagate across app
5. **Professional**: Cohesive, polished appearance

---

**Document Status:** Complete
**Design Decisions:** 20+ key UI/UX choices justified
**Compliance:** WCAG 2.1 Level AA, mobile-friendly
**Next Step:** Responsive design strategy document
