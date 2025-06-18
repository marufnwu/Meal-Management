# GitHub Copilot Design Instructions for My Dining App

## Project Overview
**My Dining** is an Android meal management application built with Material Design 3 principles, featuring a green-themed banking-style interface for managing mess operations, meals, deposits, and member management. The app follows **Clean Architecture** with **MVVM pattern**, **Hilt dependency injection**, and **Repository pattern**.

## Architecture Overview

### 📁 Project Structure
```
app/src/main/java/com/logicline/mydining/
├── data/                    # Data layer
│   ├── models/             # Data models and domain entities
│   ├── repository/         # Repository implementations
│   ├── local/             # Local database (Room)
│   └── enums/             # Application enums
├── network/               # Network layer (Retrofit)
├── ui/                   # Presentation layer
│   ├── activities/       # Activity classes
│   ├── fragments/        # Fragment classes
│   ├── adapters/         # RecyclerView adapters
│   ├── viewmodels/       # ViewModels (Hilt)
│   └── custom/           # Custom views and components
├── utils/                # Utility classes
├── extensions/           # Kotlin extensions
└── MyApplication.kt      # Application class with Hilt
```

### 🏗️ Architecture Patterns
- **MVVM**: Activities/Fragments observe ViewModels via StateFlow/LiveData
- **Repository Pattern**: Single source of truth for data
- **Dependency Injection**: Hilt for DI throughout the app
- **Clean Architecture**: Separation of concerns across layers
- **Observer Pattern**: StateFlow for reactive UI updates

## Design System & Principles

### 🎨 Color System & Theme Attributes
The app follows a **green-themed Material Design 3** color system with banking/financial app aesthetics. **IMPORTANT**: Always use theme attributes instead of hardcoded colors for consistency and dynamic theming support.

#### Theme Attributes Usage
**ALWAYS use these theme attributes instead of direct colors:**

##### Primary Theme Attributes
- `?attr/colorPrimary` - Main green (`#25A56A`)
- `?attr/colorPrimaryContainer` - Darker green (`#1D9159`)
- `?attr/colorOnPrimary` - White text on green
- `?attr/colorOnPrimaryContainer` - Text on primary container

##### Surface & Background Attributes
- `?attr/colorSurface` - Card and surface backgrounds
- `?attr/colorOnSurface` - Primary text color
- `?attr/colorOnSurfaceVariant` - Secondary text color
- `?attr/colorSurfaceContainerLow` - Subtle background containers
- `?attr/colorSurfaceContainer` - Standard container backgrounds

##### Outline & Border Attributes
- `?attr/colorOutline` - Strong borders (profile images, form fields)
- `?attr/colorOutlineVariant` - Subtle borders (card strokes, dividers)

##### Semantic Color Attributes
- `?attr/colorError` - Error states and destructive actions
- `?attr/colorOnError` - Text on error backgrounds
- `?attr/colorTertiary` - Accent color for special elements
- `?attr/colorOnTertiary` - Text on tertiary backgrounds

##### Control Attributes
- `?attr/colorControlNormal` - Default icon tints
- `?attr/selectableItemBackground` - Touch feedback backgrounds

#### Status Colors (Use theme attributes when possible)
- **Active**: `#E3F6ED` background, `#0B6E4F` text
- **Inactive**: `#F9DEDC` background, `#B3261E` text  
- **Pending**: `#FFF2E4` background, `#E6841C` text

**Example Usage:**
```xml
<!-- Correct - using theme attributes -->
<MaterialCardView
    app:strokeColor="?attr/colorOutlineVariant"
    app:cardBackgroundColor="?attr/colorSurface" />

<!-- Avoid - hardcoded colors -->
<MaterialCardView
    app:strokeColor="#C0C9C4"
    app:cardBackgroundColor="#FFFFFF" />
```

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

#### When Creating New Layouts:
1. **ALWAYS use theme attributes** instead of hardcoded colors
   ```xml
   <!-- Correct -->
   app:strokeColor="?attr/colorOutlineVariant"
   android:textColor="?attr/colorOnSurface"
   
   <!-- Wrong -->
   app:strokeColor="#C0C9C4"
   android:textColor="#1B211D"
   ```

2. **Follow established card patterns**:
   ```xml
   <MaterialCardView
       style="@style/SummaryCardStyle.UserItem"
       android:layout_marginHorizontal="@dimen/_8sdp"
       android:layout_marginVertical="@dimen/_4sdp"
       app:cardCornerRadius="@dimen/_12sdp"
       app:strokeColor="?attr/colorOutlineVariant" />
   ```

3. **Use consistent spacing with SDP dimensions**:
   - Margins: `@dimen/_8sdp`, `@dimen/_12sdp`
   - Padding: `@dimen/_8sdp` to `@dimen/_12sdp`
   - Corner radius: `@dimen/_12sdp` for cards

4. **Implement SwipeRefreshLayout** for data-driven screens
5. **Use ConstraintLayout** or LinearLayout as primary containers
6. **Apply Material 3 text appearances** consistently

#### When Creating Custom Components:
1. **Extend existing custom components** when possible (StatusView, MaterialIconEditText)
2. **Use MaterialCardView** for containers with theme-aware backgrounds
3. **Apply theme attributes** for all color properties
4. **Follow established naming patterns**: `layout_`, `item_`, `view_` prefixes
5. **Implement proper state management** with view state handling
6. **Add custom attributes** for XML configuration when needed

#### Custom Component Usage Examples:

**Form Field with Icon:**
```xml
<com.logicline.mydining.ui.custom.MaterialIconEditTextField
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldIcon="@drawable/person_24px"
    app:fieldIconTint="?attr/colorOnPrimary"
    android:hint="Full Name"
    android:inputType="textPersonName" />
```

**Display Field with Icon:**
```xml
<com.logicline.mydining.ui.custom.MaterialIconField
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldIcon="@drawable/calendar_24px">
    
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Selected Date: June 18, 2025"
        android:padding="@dimen/_12sdp"
        android:textColor="?attr/colorOnSurface" />
</com.logicline.mydining.ui.custom.MaterialIconField>
```

**Status/Empty State:**
```xml
<com.logicline.mydining.ui.custom.StatusView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:statusMessage="No data available"
    app:statusImage="@drawable/empty"
    app:positiveButtonText="Retry"
    app:negativeButtonText="Cancel" />
```

#### Custom Component Design Principles:
- **Consistent theming**: All components use the same color scheme
- **Icon containers**: 32dp cards with 8dp corner radius and primary container background  
- **Icon sizing**: 20dp icons with primary-on tint
- **Main containers**: Surface container low background with outline variant stroke
- **Spacing**: 12dp margins and padding for consistency
- **Elevation**: 0dp for flat Material 3 design

#### Profile Image Implementation:
```xml
<ShapeableImageView
    android:layout_width="@dimen/_40sdp"
    android:layout_height="@dimen/_40sdp"
    app:shapeAppearanceOverlay="@style/ShapeAppearance.Material3.Corner.Full"
    app:strokeColor="?attr/colorOutline"
    app:strokeWidth="1dp" />
```

#### Status Indicator Implementation:
```xml
<MaterialCardView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="?attr/colorSurfaceContainerLow"
    app:cardCornerRadius="@dimen/_16sdp"
    app:cardElevation="0dp">
    
    <ImageView
        android:layout_width="@dimen/_24sdp"
        android:layout_height="@dimen/_24sdp"
        android:padding="@dimen/_4sdp"
        app:tint="?attr/colorError" />
</MaterialCardView>
```

#### Interactive Element Guidelines:
- **Touch feedback**: `android:foreground="?attr/selectableItemBackground"`
- **Button styling**: Use Material 3 button styles with theme attributes
- **Icon buttons**: `style="@style/Widget.Material3.Button.IconButton"`
- **Text buttons**: Use outlined or text button styles from Material 3

#### Code Quality Standards:
- **Consistent indentation** (4 spaces)
- **Meaningful IDs** using camelCase (Views) or snake_case (resources)
- **Group related attributes** (layout, appearance, behavior)
- **Use style inheritance** to reduce duplication in themes.xml
- **Comment complex layout logic** especially for custom views

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

## Architecture & Existing Design Analysis

### 🏗️ Project Architecture Overview
The app follows **Clean Architecture** with **MVVM pattern**, **Hilt dependency injection**, and **Repository pattern**.

#### 📁 Project Structure
```
app/src/main/java/com/logicline/mydining/
├── data/                    # Data layer
│   ├── models/             # Data models and domain entities
│   ├── repository/         # Repository implementations
│   ├── local/             # Local database (Room)
│   └── enums/             # Application enums
├── network/               # Network layer (Retrofit)
├── ui/                   # Presentation layer
│   ├── activities/       # Activity classes
│   ├── fragments/        # Fragment classes
│   ├── adapters/         # RecyclerView adapters
│   ├── viewmodels/       # ViewModels (Hilt)
│   └── custom/           # Custom views and components
├── utils/                # Utility classes
├── extensions/           # Kotlin extensions
└── MyApplication.kt      # Application class with Hilt
```

### 🎯 Custom Components Analysis

#### StatusView Component
**Location**: `ui/custom/StatusView.kt`
**Purpose**: Displays different states (empty, error, info) with customizable content
**Key Features**:
- Supports custom images, messages, and action buttons
- Uses theme attributes for consistent styling
- Implements listener interfaces for button interactions
- Provides XML attributes for declarative configuration

**Custom Attributes**:
```xml
<declare-styleable name="StatusView">
    <attr name="statusMessage" format="string" />
    <attr name="statusImage" format="reference" />
    <attr name="positiveButtonText" format="string" />
    <attr name="negativeButtonText" format="string" />
</declare-styleable>
```

**Usage Pattern**:
```xml
<com.logicline.mydining.ui.custom.StatusView
    android:id="@+id/status_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:statusMessage="No internet connection"
    app:statusImage="@drawable/empty"
    app:positiveButtonText="Create New Mess"
    app:negativeButtonText="Join Existing Mess" />
```

#### MaterialIconEditTextField Component
**Location**: `ui/custom/MaterialIconEditText.kt`
**Purpose**: Custom EditText with Material 3 styling and icon container
**Key Features**:
- Icon in dedicated MaterialCardView container with theme-aware colors
- Uses `?attr/colorPrimaryContainer` for icon background, `?attr/colorOnPrimary` for icon tint
- Supports all standard EditText attributes plus custom icon configuration
- Main container uses `?attr/colorSurfaceContainerLow` background with `?attr/colorOutlineVariant` stroke

**Custom Attributes**:
```xml
<declare-styleable name="MaterialIconEditTextField">
    <!-- Standard EditText attributes -->
    <attr name="android:hint" />
    <attr name="android:inputType" />
    <attr name="android:text" />
    <attr name="android:maxLines" />
    <attr name="android:maxLength" />
    <attr name="android:digits" />
    <attr name="android:imeOptions" />
    
    <!-- Custom attributes -->
    <attr name="fieldIcon" format="reference" />
    <attr name="fieldIconTint" format="color" />
</declare-styleable>
```

**Usage Pattern**:
```xml
<com.logicline.mydining.ui.custom.MaterialIconEditTextField
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldIcon="@drawable/person_24px"
    app:fieldIconTint="?attr/colorOnPrimary"
    android:hint="Enter your name"
    android:inputType="textPersonName" />
```

**Public API**:
```kotlin
// Properties
var text: String
var hint: CharSequence?
var inputType: Int

// Methods
fun addTextChangedListener(watcher: TextWatcher)
fun setSelection(index: Int)
fun clear()
fun setIcon(@DrawableRes iconResId: Int)
fun getEditText(): EditText // Direct access if needed
```

#### MaterialIconField Component
**Location**: `ui/custom/MaterialIconField.kt`
**Purpose**: Container for any content with consistent icon styling (non-editable)
**Key Features**:
- Flexible container that accepts any child views via XML
- Uses same styling as MaterialIconEditTextField for consistency
- Overrides `addView()` to redirect child views to content container
- Supports custom icon background color override

**Custom Attributes**:
```xml
<declare-styleable name="MaterialIconField">
    <attr name="fieldIcon" format="reference" />
    <attr name="fieldIconTint" format="color" />
    <attr name="iconBackgroundColor" format="color" />
</declare-styleable>
```

**Usage Pattern**:
```xml
<com.logicline.mydining.ui.custom.MaterialIconField
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:fieldIcon="@drawable/calendar_24px"
    app:fieldIconTint="?attr/colorOnPrimary">
    
    <!-- Any content can go here -->
    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Selected Date"
        android:padding="@dimen/_12sdp" />
</com.logicline.mydining.ui.custom.MaterialIconField>
```

**Public API**:
```kotlin
fun setIcon(@DrawableRes iconResId: Int)
fun setIconTint(colorStateList: ColorStateList?)
fun setIconBackgroundColor(colorStateList: ColorStateList?)
```

#### MonthPickerView Component
**Location**: `ui/custom/MonthPickerView.kt`
**Purpose**: Custom month/date selection component
**Key Features**:
- Multiple selection modes (checkmark, radio, highlight)
- Search functionality with configurable debounce time
- Grid or list display modes
- Theme-aware styling

### 🎨 Theme Attribute Usage Patterns

#### Consistent Color Application
The app **NEVER uses hardcoded colors** in layouts. All color references use theme attributes:

```xml
<!-- Profile images with themed borders -->
<ShapeableImageView
    app:strokeColor="?attr/colorOutline"
    app:strokeWidth="1dp" />

<!-- Cards with themed backgrounds and strokes -->
<MaterialCardView
    app:cardBackgroundColor="?attr/colorSurface"
    app:strokeColor="?attr/colorOutlineVariant" />

<!-- Icons with themed tints -->
<ImageView
    app:tint="?attr/colorControlNormal" />

<!-- Text with themed colors -->
<TextView
    android:textColor="?attr/colorOnSurfaceVariant" />
```

#### Interactive Elements
- Touch feedback: `android:foreground="?attr/selectableItemBackground"`
- Button styling: Uses Material 3 button styles with theme attributes
- Status indicators: Custom colored backgrounds using theme attributes

### 📐 Layout Patterns

#### Card-Based Information Architecture
Most content is organized in MaterialCardView containers with consistent styling:

```xml
<!-- Standard card pattern used in custom components -->
<MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="@dimen/_12sdp"
    app:cardBackgroundColor="?attr/colorSurfaceContainerLow"
    app:cardCornerRadius="@dimen/_12sdp"
    app:cardElevation="0dp"
    app:strokeWidth="1dp"
    app:strokeColor="?attr/colorOutlineVariant">
    
    <!-- Content with horizontal orientation -->
    <LinearLayout
        android:orientation="horizontal"
        android:gravity="center_vertical">
        
        <!-- Icon container pattern -->
        <MaterialCardView
            android:id="@+id/iconContainer"
            android:layout_width="@dimen/_32sdp"
            android:layout_height="@dimen/_32sdp"
            android:layout_margin="@dimen/_8sdp"
            app:cardBackgroundColor="?attr/colorPrimaryContainer"
            app:cardCornerRadius="@dimen/_8sdp"
            app:cardElevation="0dp">
            
            <ImageView
                android:layout_width="@dimen/_20sdp"
                android:layout_height="@dimen/_20sdp"
                android:layout_gravity="center"
                app:tint="?attr/colorOnPrimary" />
        </MaterialCardView>
        
        <!-- Content area -->
        <!-- This can be EditText, FrameLayout, or any content -->
    </LinearLayout>
</MaterialCardView>
```

#### Profile Image Pattern
Consistent across the app:
```xml
<ShapeableImageView
    android:layout_width="@dimen/_40sdp"
    android:layout_height="@dimen/_40sdp"
    app:shapeAppearanceOverlay="@style/ShapeAppearance.Material3.Corner.Full"
    app:strokeColor="?attr/colorOutline"
    app:strokeWidth="1dp" />
```

#### Status Indicator Pattern
Small cards with semantic meaning:
```xml
<MaterialCardView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="?attr/colorSurfaceContainerLow"
    app:cardCornerRadius="@dimen/_16sdp">
    
    <ImageView
        android:layout_width="@dimen/_24sdp"
        android:layout_height="@dimen/_24sdp"
        app:tint="?attr/colorError" />
</MaterialCardView>
```

#### Custom Component Layout Structure
All custom form components follow this pattern:
1. **Main Container**: MaterialCardView with `colorSurfaceContainerLow` background
2. **Icon Container**: Smaller MaterialCardView with `colorPrimaryContainer` background
3. **Icon**: 20dp ImageView with `colorOnPrimary` tint
4. **Content Area**: EditText, FrameLayout, or other content with 12dp padding

### 🎭 Drawable Resource Patterns

#### Theme-Aware Drawables
All vector drawables use theme attributes for tinting:

```xml
<!-- Icon with theme-aware tint -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:tint="?attr/colorControlNormal">
    <!-- path data -->
</vector>

<!-- Shape with theme-aware colors -->
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="?attr/colorPrimary" />
    <stroke android:color="?attr/colorOutlineVariant" />
</shape>
```

#### Tab Selection Pattern
Custom tab indicator using theme attributes:
```xml
<!-- tab_selected.xml -->
<shape android:shape="rectangle">
    <corners android:radius="@dimen/_24sdp" />
    <solid android:color="?attr/colorPrimary" />
</shape>
```

### 📱 Activity/Fragment Patterns

#### SwipeRefreshLayout Usage
Most data-driven screens use SwipeRefreshLayout:
```xml
<SwipeRefreshLayout
    android:id="@+id/layoutRefresh"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- Content here -->
</SwipeRefreshLayout>
```

#### Collapsing Toolbar Pattern
Home fragment uses sophisticated collapsing behavior:
```xml
<CoordinatorLayout>
    <AppBarLayout>
        <CollapsingToolbarLayout
            app:layout_scrollFlags="scroll|exitUntilCollapsed|snap">
            <!-- Header content -->
        </CollapsingToolbarLayout>
    </AppBarLayout>
    <!-- Scrollable content -->
</CoordinatorLayout>
```

### 🔧 Spacing & Dimensions

#### Scalable DP (SDP) System
The app uses a custom SDP (scalable dp) dimension system:
- `@dimen/_4sdp`, `@dimen/_8sdp`, `@dimen/_12sdp`, `@dimen/_16sdp` for margins
- `@dimen/_20sdp`, `@dimen/_24sdp` for larger elements
- `@dimen/_35sdp`, `@dimen/_40sdp` for profile images
- `@dimen/_12sdp` standard card corner radius

#### Consistent Spacing Rules
- Card margins: `@dimen/_8sdp` horizontal, `@dimen/_4sdp` vertical
- Content padding: `@dimen/_8sdp` to `@dimen/_12sdp`
- Element spacing: `@dimen/_4sdp` to `@dimen/_8sdp`

### 📝 Text Styling Patterns

#### Material 3 Text Appearances
Consistent usage across the app:
- Titles: `TextAppearance.Material3.TitleMedium`
- Body text: `TextAppearance.Material3.BodyMedium`
- Labels: `TextAppearance.Material3.LabelLarge`
- Captions: `TextAppearance.Material3.BodySmall`

#### Font Usage
- Primary font: `@font/hubballi` for names and headings
- System font: Material 3 default for body text

#### Text Color Hierarchy
- Primary text: `?attr/colorOnSurface`
- Secondary text: `?attr/colorOnSurfaceVariant`
- Labels and captions: Lighter variants of theme colors
