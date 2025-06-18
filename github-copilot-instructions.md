# GitHub Copilot Design Instructions for My Dining App

## Project Overview
**My Dining** is an Android meal management application built with Material Design 3 principles, featuring a green-themed banking-style interface for managing mess operations, meals, deposits, and member management.

## Design System & Principles

### 🎨 Color Palette
The app follows a **green-themed Material Design 3** color system with banking/financial app aesthetics:

#### Primary Colors
- **Primary**: `#25A56A` (Main green)
- **Primary Container**: `#1D9159` (Darker green)
- **On Primary**: `#FFFFFF` (White text on green)
- **Secondary**: `#4CAF86` (Light green accent)

#### Banking-Specific Colors
- **Bank Card**: `#25A56A` (Card backgrounds)
- **Chart Line**: `#25A56A` (Data visualization)
- **Remove Action**: `#FF5B5B` (Coral red for destructive actions)
- **Success**: `#0B6E4F` (Dark green for positive feedback)
- **Warning**: `#E6841C` (Orange for alerts)
- **Error**: `#B3261E` (Red for errors)

#### Status Colors
- **Active**: `#E3F6ED` background, `#0B6E4F` text
- **Inactive**: `#F9DEDC` background, `#B3261E` text
- **Pending**: `#FFF2E4` background, `#E6841C` text

### 🏗️ Layout Architecture

#### Container Patterns
1. **Card-Based Design**: Use `MaterialCardView` with consistent corner radius (`@dimen/_12sdp`)
2. **SwipeRefreshLayout**: Wrap main content for pull-to-refresh functionality
3. **Coordinator Layouts**: For complex scrolling behaviors with AppBar
4. **Linear/Constraint Layouts**: Primary layout containers

#### Spacing System
- Uses scalable dp (sdp) dimensions for consistent sizing across devices
- Standard margins: `@dimen/_8sdp`, `@dimen/_12sdp`, `@dimen/_16sdp`
- Card corner radius: `@dimen/_12sdp`
- Elevation: `@dimen/_4sdp` for cards, `@dimen/_8sdp` for elevated elements

### 🎭 Theme Structure

#### Base Themes
```xml
<!-- Default theme with ActionBar -->
AppTheme (extends AppTheme.Base)

<!-- No ActionBar with primary status bar -->
AppTheme.NoToolbar

<!-- Edge-to-edge with transparent status bar -->
AppTheme.EdgeToEdge

<!-- Content-matching with surface-colored status bar -->
AppTheme.ContentMatching
```

#### Component Themes
- **ActionBar**: Primary green background with white text
- **Bottom Sheets**: Rounded top corners (`16dp`)
- **Dialogs**: Material 3 styles with custom button styling
- **Cards**: Outlined style with subtle elevation

### 📱 UI Components Standards

#### Cards
```xml
<style name="SummaryCardStyle" parent="Widget.Material3.CardView.Elevated">
    <item name="cardCornerRadius">12dp</item>
    <item name="cardElevation">2dp</item>
    <item name="cardBackgroundColor">?attr/colorSurface</item>
</style>
```

#### User Item Cards
```xml
<style name="SummaryCardStyle.UserItem" parent="Widget.Material3.CardView.Outlined">
    <item name="strokeWidth">1dp</item>
    <item name="strokeColor">?attr/colorOutlineVariant</item>
</style>
```

#### Text Hierarchy
- **Title**: `TextAppearance.Material3.TitleMedium` - Bold, high contrast
- **Body**: `TextAppearance.Material3.BodyMedium` - Regular weight
- **Labels**: `TextAppearance.Material3.LabelLarge` - Secondary color
- **Captions**: `TextAppearance.Material3.BodySmall` - Tertiary color

#### Buttons
- **Primary**: Green background (`#25A56A`) with white text
- **Secondary**: Outlined style with green border
- **Destructive**: Coral red (`#FF5B5B`) for delete/remove actions
- **Corner Radius**: `@dimen/_10sdp` for buttons

### 🖼️ Visual Patterns

#### Profile Images
- **Shape**: Circular using `ShapeAppearance.Material3.Corner.Full`
- **Size**: `@dimen/_35sdp` to `@dimen/_40sdp` for list items
- **Border**: 1dp outline color stroke

#### Status Indicators
- **Chips**: Rounded corners with semantic colors
- **Icons**: 20dp size with 0.8 alpha for subtlety
- **Dividers**: `1dp` height with `colorOutlineVariant`

#### Navigation
- **Tab Layout**: Custom tab selector with rounded selected state
- **Bottom Navigation**: Material 3 style
- **Back Navigation**: Consistent with Material guidelines

### 📋 Layout Naming Conventions

#### Activity Layouts
- Pattern: `activity_[feature_name].xml`
- Examples: `activity_mess_info.xml`, `activity_available_messes.xml`

#### Item Layouts
- Pattern: `item_[component_name].xml` or `layout_[component_name].xml`
- Examples: `item_available_mess.xml`, `layout_user_item.xml`

#### Custom Views
- Pattern: `view_[component_name].xml`
- Examples: `view_month_selector.xml`, `view_material_icon.xml`

### 🎯 Interaction Patterns

#### Feedback States
- **Loading**: SwipeRefreshLayout with branded colors
- **Empty States**: Centered text with illustration
- **Error States**: Error color with action buttons
- **Success**: Green confirmation with subtle animation

#### Touch Targets
- **Minimum**: 48dp for all interactive elements
- **Cards**: Full card area clickable with ripple effect
- **Buttons**: Material 3 elevation and state changes

### 🔤 Typography

#### Font Family
- **Primary**: `@font/hubballi` for headings and names
- **System**: Material 3 default for body text

#### Text Colors
- **Primary**: `#1B211D` (High contrast)
- **Secondary**: `#4A524D` (Medium contrast)
- **Tertiary**: `#6F7772` (Low contrast)

### 🌐 Internationalization

#### String Resources
- All user-facing text in `strings.xml`
- Translatable flag management for app-specific strings
- Support for Bengali (`values-bn`)

#### RTL Support
- Use `start`/`end` instead of `left`/`right`
- Proper margin and padding declarations

### 📐 Animation & Transitions

#### Standard Animations
- **Dialog**: Fade in/out
- **Page Transitions**: Slide left/right
- **Cards**: Subtle elevation changes on interaction
- **Collapsing Toolbar**: Smooth scroll animations

### 🛡️ Accessibility

#### Content Descriptions
- All interactive elements have proper content descriptions
- Icons paired with text labels
- Semantic markup for screen readers

#### Touch Accessibility
- 48dp minimum touch targets
- High contrast ratios maintained
- Focus indicators visible

### 💡 Implementation Guidelines

#### When Creating New Screens:
1. **Start with appropriate base theme** (NoToolbar for custom headers, EdgeToEdge for immersive)
2. **Use SwipeRefreshLayout** for data-driven screens
3. **Implement consistent card patterns** for list items
4. **Follow color semantics** (green for positive, coral for destructive)
5. **Apply proper spacing** using sdp dimensions
6. **Add empty and loading states**

#### When Creating Custom Components:
1. **Extend Material 3 components** when possible
2. **Use theme attributes** instead of hardcoded colors
3. **Implement proper state management** (enabled, disabled, selected)
4. **Follow established naming patterns**
5. **Add proper accessibility support**

#### Code Quality Standards:
- **Consistent indentation** (4 spaces)
- **Meaningful IDs** using camelCase or snake_case
- **Group related attributes** (layout, appearance, behavior)
- **Use style inheritance** to reduce duplication
- **Comment complex layout logic**

### 🎨 Visual Hierarchy Principles

1. **Information Architecture**: Cards → Sections → Items
2. **Visual Weight**: Primary actions prominent, secondary actions subtle
3. **Grouping**: Related information clustered with consistent spacing
4. **Scanning**: Important information aligned and easily scannable
5. **Affordance**: Interactive elements clearly distinguished

### 🔄 State Management

#### Loading States
- SwipeRefreshLayout for pull-to-refresh
- Subtle progress indicators for in-place updates
- Skeleton screens for complex content

#### Error Handling
- Inline error messages with action buttons
- Network error states with retry functionality
- Form validation with immediate feedback

This design system ensures consistency, accessibility, and maintainability across the My Dining application while following Material Design 3 principles and banking app UX patterns.
