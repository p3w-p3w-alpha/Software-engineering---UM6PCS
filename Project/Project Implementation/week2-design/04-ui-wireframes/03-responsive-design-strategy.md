# SAMS Responsive Design Strategy

## Purpose
This document outlines the responsive design approach for SAMS, ensuring optimal user experience across all devices.

---

## Mobile-First Approach

**Philosophy:** Design for mobile first, then enhance for larger screens

**Rationale:**
- Forces priority of essential content
- Easier to scale up than down
- Majority of users may access on mobile
- Better performance (progressive enhancement)

---

## Breakpoints

### Standard Breakpoints

```css
/* Mobile First (default) */
/* Extra Small: 0-575px (phones portrait) */

/* Small: 576px-767px (phones landscape) */
@media (min-width: 576px) { }

/* Medium: 768px-1023px (tablets) */
@media (min-width: 768px) { }

/* Large: 1024px-1279px (desktops) */
@media (min-width: 1024px) { }

/* Extra Large: 1280px+ (large desktops) */
@media (min-width: 1280px) { }
```

### SAMS Specific Breakpoints

| Device Class | Width | Layout Strategy |
|--------------|-------|-----------------|
| **Mobile** | < 768px | Single column, hamburger menu, bottom nav |
| **Tablet** | 768px - 1023px | 2 columns, collapsible sidebar |
| **Desktop** | ≥ 1024px | Multi-column, fixed sidebar, max-width 1440px |

---

## Layout Adaptations

### Navigation

#### Mobile (< 768px)
```
┌────────────────────┐
│ SAMS        ≡ 🔔   │  ← Hamburger menu + notifications
├────────────────────┤
│                    │
│   Page Content     │
│                    │
│                    │
├────────────────────┤
│ 🏠 📚 📊 ⚙ │  ← Bottom navigation bar
└────────────────────┘
```

**Features:**
- Hamburger menu (full-screen overlay)
- Bottom tab bar (4-5 primary items)
- Fixed header with logo + menu
- No sidebar visible

---

#### Tablet (768px - 1023px)
```
┌─────────────────────────────────┐
│[≡] SAMS    Search...    🔔 User│
├─────────────────────────────────┤
│ ┌─┐  Page Content              │
│ │D│  ┌──────────┬──────────┐   │
│ │a│  │  Card    │  Card    │   │
│ │s│  └──────────┴──────────┘   │
│ │h│  ┌───────────────────┐     │
│ │b│  │  Table            │     │
│ └─┘  └───────────────────┘     │
└─────────────────────────────────┘
```

**Features:**
- Icon-only collapsible sidebar
- 2-column card layout
- Hover/click expands sidebar
- Responsive tables (horizontal scroll)

---

#### Desktop (≥ 1024px)
```
┌──────────────────────────────────────────┐
│ SAMS    Search...          🔔 User [≡]  │
├──────────────────────────────────────────┤
│ ┌────────┐  ┌───────────────────────┐   │
│ │Dashboard│  │ ┌────┬────┬────┐     │   │
│ │Courses  │  │ │Card│Card│Card│     │   │
│ │Grades   │  │ └────┴────┴────┘     │   │
│ │Schedule │  │ ┌─────────────────┐  │   │
│ │Payment  │  │ │   Table         │  │   │
│ │Transcript│  │ └─────────────────┘  │   │
│ │         │  │                       │   │
│ └────────┘  └───────────────────────┘   │
└──────────────────────────────────────────┘
```

**Features:**
- Fixed 240px sidebar (always visible)
- Multi-column layout
- Full-width tables
- Max content width: 1440px (centered)

---

## Component Adaptations

### Data Tables

#### Mobile Strategy: **Cards** (not tables)

**Desktop Table:**
```
┌──────────┬──────────────────┬────────┬────────┐
│ Code     │ Course Name      │ Time   │ Action │
├──────────┼──────────────────┼────────┼────────┤
│ CS301    │ Data Structures  │ MWF10  │[Enroll]│
│ MATH210  │ Calculus II      │ TTh14  │[Enroll]│
└──────────┴──────────────────┴────────┴────────┘
```

**Mobile Cards:**
```
┌──────────────────────────────┐
│ CS301 - Data Structures      │
│ Dr. Smith                    │
│ MWF 10:00-11:15              │
│ Building A, Room 205         │
│           [Enroll] [Details] │
└──────────────────────────────┘

┌──────────────────────────────┐
│ MATH210 - Calculus II        │
│ Dr. Lee                      │
│ TTh 14:00-15:50              │
│ Building B, Room 101         │
│           [Enroll] [Details] │
└──────────────────────────────┘
```

**Benefits:**
- More readable on small screens
- Avoids horizontal scroll
- Thumb-friendly tap targets
- Shows all relevant info

---

### Forms

#### Mobile: **Stack inputs**, single column

```
Username *
<______________________>

Password *
<______________________>

Email Address *
<______________________>

[        Submit        ]
```

#### Desktop: **Multi-column** for related fields

```
First Name *           Last Name *
<_______________>      <_______________>

Email Address *                Phone Number
<______________________________>  <_______________>

[Cancel]                           [Submit]
```

---

### Modals

#### Mobile: **Full-screen** takeover

```
┌────────────────────┐
│ [✕] Modal Title    │
├────────────────────┤
│                    │
│   Form Content     │
│                    │
│                    │
│                    │
├────────────────────┤
│ [Cancel] [Submit]  │
└────────────────────┘
```

**Rationale:** Maximizes usable space, no clumsy small popups

---

#### Desktop: **Centered dialog**

```
        ┌──────────────────────┐
        │ Modal Title      [✕] │
        ├──────────────────────┤
        │                      │
        │   Form Content       │
        │                      │
        ├──────────────────────┤
        │ [Cancel]   [Submit]  │
        └──────────────────────┘
```

**Rationale:** Focuses attention, maintains context of page behind

---

## Typography Scaling

### Font Size Adjustments

```css
/* Mobile (< 768px) */
h1 { font-size: 24px; }  /* Smaller than desktop */
h2 { font-size: 20px; }
body { font-size: 16px; }  /* Larger for readability */

/* Desktop (≥ 1024px) */
h1 { font-size: 32px; }
h2 { font-size: 24px; }
body { font-size: 14px; }
```

**Rationale:**
- Larger body text on mobile (easier reading)
- Smaller headings conserve space
- Proportional scaling maintains hierarchy

---

## Touch Targets

### Minimum Sizes (WCAG 2.1)

| Element | Mobile | Desktop |
|---------|--------|---------|
| Buttons | 56px height | 40px height |
| Links | 44px tap area | 24px (clickable) |
| Table rows | 56px height | 48px height |
| Form inputs | 56px height | 40px height |

**Implementation:**
```css
/* Mobile */
.btn { padding: 16px 24px; }

/* Desktop */
@media (min-width: 1024px) {
  .btn { padding: 10px 20px; }
}
```

---

## Images and Media

### Responsive Images

```html
<picture>
  <source media="(min-width: 1024px)" srcset="image-desktop.jpg">
  <source media="(min-width: 768px)" srcset="image-tablet.jpg">
  <img src="image-mobile.jpg" alt="Description">
</picture>
```

**Or with CSS:**
```css
.hero-image {
  background-image: url('hero-mobile.jpg');
}

@media (min-width: 768px) {
  .hero-image {
    background-image: url('hero-tablet.jpg');
  }
}

@media (min-width: 1024px) {
  .hero-image {
    background-image: url('hero-desktop.jpg');
  }
}
```

---

## Grid System

### Flexbox-Based Grid

```css
.container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

/* Mobile: 1 column */
.col {
  flex: 0 0 100%;
}

/* Tablet: 2 columns */
@media (min-width: 768px) {
  .col {
    flex: 0 0 calc(50% - 8px);
  }
}

/* Desktop: 3 columns */
@media (min-width: 1024px) {
  .col {
    flex: 0 0 calc(33.333% - 11px);
  }
}
```

**Usage Example:**
```html
<div class="container">
  <div class="col">Card 1</div>
  <div class="col">Card 2</div>
  <div class="col">Card 3</div>
</div>
```

**Result:**
- Mobile: Stacked (1 per row)
- Tablet: 2 per row
- Desktop: 3 per row

---

## Content Priority

### Progressive Disclosure

**Mobile:** Show essential content first, hide advanced features

```
[Show Filters] ← Button to expand filters
```

**Desktop:** Show all options upfront

```
Filters: [Dept▼] [Semester▼] [Credits▼] [Available seats☑]
```

### Content Hiding

**What to Hide on Mobile:**
- Sidebar navigation (hamburger menu)
- Extra columns in tables (show in detail view)
- Verbose descriptions (truncate with "Read more")
- Decorative images

**What to ALWAYS Show:**
- Primary actions (buttons)
- Critical information (price, status)
- Navigation (bottom bar)
- Error messages

---

## Performance Considerations

### Mobile Optimizations

1. **Lazy Loading:**
   ```javascript
   <img loading="lazy" src="..." />
   ```

2. **Smaller Images:**
   - Mobile: 750px width max
   - Tablet: 1024px width max
   - Desktop: 1920px width max

3. **Conditional Loading:**
   ```javascript
   if (window.innerWidth >= 1024) {
     import('./DesktopComponent.vue');
   } else {
     import('./MobileComponent.vue');
   }
   ```

4. **Touch Optimization:**
   - Remove :hover effects on mobile (save CSS)
   - Use -webkit-tap-highlight-color for iOS

---

## Testing Strategy

### Devices to Test

**Physical Devices:**
- iPhone SE (smallest modern phone)
- iPhone 12/13 (standard size)
- iPad (tablet)
- Desktop (1920x1080)

**Browser DevTools:**
- Chrome DevTools (device emulation)
- Firefox Responsive Design Mode
- Safari Web Inspector

### Breakpoint Testing Checklist

- [ ] Navigation works at all breakpoints
- [ ] No horizontal scroll on mobile
- [ ] Touch targets ≥ 44px on mobile
- [ ] Text readable without zoom
- [ ] Forms usable on mobile
- [ ] Tables adapt to cards on mobile
- [ ] Modals full-screen on mobile
- [ ] Images load appropriate sizes
- [ ] Performance acceptable on 3G

---

## CSS Framework Approach

### Element Plus Responsive Grid

```vue
<el-row :gutter="16">
  <el-col :xs="24" :sm="12" :md="8" :lg="6">
    <!-- Responsive column -->
    <!-- Mobile: 24/24 (full width) -->
    <!-- Tablet: 12/24 (half width) -->
    <!-- Desktop: 8/24 (1/3 width) -->
    <!-- Large: 6/24 (1/4 width) -->
  </el-col>
</el-row>
```

---

## Accessibility on Mobile

### Key Considerations

1. **Font Size:** Minimum 16px body text (avoid browser zoom)
2. **Contrast:** Same WCAG AA requirements as desktop
3. **Touch Targets:** 44x44px minimum
4. **Screen Readers:** VoiceOver (iOS), TalkBack (Android)
5. **Orientation:** Support both portrait and landscape
6. **Gestures:** Avoid relying solely on swipe gestures

---

## Responsive Design Checklist

### Pre-Launch Verification

#### Mobile (< 768px)
- [ ] Hamburger menu opens/closes
- [ ] Bottom navigation functional
- [ ] Forms fit on screen (no horizontal scroll)
- [ ] Tables converted to cards
- [ ] Images scale properly
- [ ] Touch targets adequate size
- [ ] No hover-dependent interactions

#### Tablet (768px - 1023px)
- [ ] Sidebar collapsible
- [ ] 2-column layout works
- [ ] Hybrid touch/mouse works
- [ ] Tables responsive

#### Desktop (≥ 1024px)
- [ ] Sidebar fixed and visible
- [ ] Multi-column layouts render
- [ ] Hover states functional
- [ ] Tables full-featured

#### All Breakpoints
- [ ] No content cutoff
- [ ] Readable fonts
- [ ] Adequate spacing
- [ ] Consistent branding
- [ ] Fast performance

---

## Future Enhancements

### Progressive Web App (PWA)
- Offline support
- Add to home screen
- Push notifications
- App-like experience

### Dark Mode
- Reduced eye strain
- Battery savings (OLED screens)
- User preference detection

---

**Document Status:** Complete
**Responsive Strategy:** Mobile-first, 3 main breakpoints
**Compliance:** WCAG 2.1 touch targets, readability
**Next Step:** Design system documentation
