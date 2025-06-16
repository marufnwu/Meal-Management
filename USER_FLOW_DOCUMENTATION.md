# **Meal Management App - User Flow Documentation**

## **Table of Contents**
1. [Overview](#overview)
2. [App Architecture & Entry Points](#app-architecture--entry-points)
3. [Authentication Flow](#authentication-flow)
4. [User Roles & Permissions](#user-roles--permissions)
5. [Main Dashboard Navigation](#main-dashboard-navigation)
6. [Core Feature Flows](#core-feature-flows)
7. [Settings & Administration](#settings--administration)
8. [Error Handling & Edge Cases](#error-handling--edge-cases)
9. [Data Flow & State Management](#data-flow--state-management)

---

## **Overview**

The Meal Management App is a comprehensive mess (dining) management system designed for shared living environments like hostels, student accommodations, or group housing. The app manages meals, expenses, deposits, and financial tracking with role-based permissions.

### **Key Features**
- Multi-role user management (Super User, Manager, Regular User)
- Real-time meal tracking with fractional support (0.0-1.0)
- Financial management (purchases, deposits, expenses)
- Monthly reporting and analytics
- Purchase request system
- Member initiation and management

---

## **App Architecture & Entry Points**

### **Application Flow Architecture**
```
FirstActivity (Entry Point)
    ↓
App Update Check → Ad Settings → Authentication Check
    ↓
LoginActivity ←→ MainActivity (Main Dashboard)
    ↓
Feature Activities & Fragments
```

### **Main Entry Point: FirstActivity**
```kotlin
FirstActivity → checkUpdate() → getAdSettings() → checkLogin()
    ├── If Logged In: checkAccessToken() → MainActivity
    └── If Not Logged In: LoginActivity
```

**Key Responsibilities:**
- App update verification via Google Play
- Ad settings initialization
- Authentication token validation
- Navigation to appropriate entry point

---

## **Authentication Flow**

### **1. Login Process**
```
LoginActivity
    ├── Username/Password Input
    ├── Login Button → API Call → Token Validation
    ├── "Forgot Password" → ResetPasswordActivity
    ├── "Create New Mess" → CreateMessActivity
    └── Customer Support Integration
```

**Login Implementation:**
```kotlin
// LoginActivity.login()
private fun login() {
    val userName = binding.edtUerName.text.toString()
    val password = binding.edtPassword.text.toString()
    
    (application as MyApplication)
        .myApi
        .login(userName, password)
        .enqueue(callback)
}
```

### **2. Password Reset Flow**
```
ResetPasswordActivity
    ├── Step 1: Username Input → OTP Request
    ├── Step 2: OTP Verification → Email Validation
    └── Step 3: New Password → Password Reset
```

### **3. New Mess Creation**
```
CreateMessActivity
    ├── Personal Details (Name, Phone, Email, Gender, City)
    ├── Mess Details (Mess Name)
    ├── Account Credentials (Username, Password)
    └── Account Creation → Auto Login → MainActivity
```

---

## **User Roles & Permissions**

### **Role Hierarchy**
1. **Super User (Admin)** - Full system control
2. **Manager** - Most management functions
3. **Regular User** - Limited personal actions

### **Permission Matrix**
| Feature | Super User | Manager | Regular User |
|---------|------------|---------|--------------|
| Add/Edit Members | ✅ | ✅ | ❌ |
| Add Meals (Any Date) | ✅ | ✅ | Today/Tomorrow Only |
| Manage Purchases | ✅ | ✅ | Request Only |
| View All Reports | ✅ | ✅ | ❌ |
| Reset Mess Data | ✅ | ❌ | ❌ |
| Change Super User | ✅ | ❌ | ❌ |
| Settings Control | ✅ | ✅ | Limited |
| Member Initiation | ✅ | ✅ | ❌ |

### **Permission Implementation**
```kotlin
// Permission checking utility
object Constant {
    fun isManagerOrSuperUser(): Boolean
    fun isSuperUser(): Boolean
    fun isRegularUser(): Boolean
}
```

---

## **Main Dashboard Navigation**

### **MainActivity - Central Hub**
```
MainActivity (Single Activity)
    ├── Header Section
    │   ├── User Profile & Photo
    │   ├── Current Month Display
    │   └── Settings Icon
    ├── Quick Stats Cards
    │   ├── Total Meals This Month
    │   ├── Meal Charge Rate
    │   └── User Balance Status
    ├── Action Grid (Role-Based)
    │   ├── [Add Meal] [Purchases] [Deposit]
    │   ├── [Members] [Meal Chart] [Summary]
    │   ├── [Add Fund] [Add Purchase] [Initiate Member]
    │   └── [Other Cost] [Purchase Request] [Profile]
    └── User Guide Section (Expandable)
```

### **Navigation Implementation**
```kotlin
// MainActivity navigation setup
private fun initListener() {
    binding.addMeal.setOnClickListener {
        startActivity(Intent(this, AddMealActivity::class.java))
    }
    
    binding.members.setOnClickListener {
        startActivity(Intent(this, MembersActivity::class.java))
    }
    
    // Role-based visibility
    if(!Constant.isManagerOrSuperUser()) {
        binding.initiateMember.visibility = View.GONE
        binding.addPurchase.visibility = View.GONE
    }
}
```

---

## **Core Feature Flows**

### **1. Meal Management Flow**

#### **A. Adding Meals**
```
AddMealActivity
    ├── User Selection (Role-dependent)
    │   ├── Managers: All Users Dropdown
    │   └── Regular Users: Auto-select Self
    ├── Date Selection
    │   ├── Managers: Any Date
    │   └── Regular Users: Today/Tomorrow Only
    ├── Meal Entry (0.0-1.0 scale)
    │   ├── Breakfast Count
    │   ├── Lunch Count
    │   └── Dinner Count
    └── Submit → API Call → Success/Failure
```

**Implementation Details:**
```kotlin
// Different user flows based on role
private fun getUsersList() {
    if(Constant.isManagerOrSuperUser()) {
        getUserListForManager()
    } else {
        // Auto-select current user for regular users
        LocalDB.getUser()?.let { currentUser ->
            // Set current user as selected
        }
    }
}
```

#### **B. Meal Chart Viewing**
```
MealActivity
    ├── Month Selection → Calendar View
    ├── Daily Meal Breakdown
    │   ├── Member-wise meal counts
    │   ├── Total meals per day
    │   └── Edit/Delete Options (Managers)
    └── Monthly Summary Statistics
```

### **2. Financial Management Flow**

#### **A. Purchase Management**
```
PurchasesActivity
    ├── Purchase Type Selection
    │   ├── Meal Purchases (Type 1)
    │   └── Other Costs (Type 2)
    ├── Monthly View with Filters
    ├── Manager Actions
    │   ├── Add New Purchase
    │   ├── Edit Existing
    │   └── Delete Purchase
    └── Regular User: View Only
```

#### **B. Purchase Addition Flow**
```
AddPurchaseActivity
    ├── Member Selection → Dropdown
    ├── Date Selection → Calendar
    ├── Purchase Details
    │   ├── Product Description
    │   ├── Amount Entry
    │   ├── Purchase Type (Meal/Other)
    │   └── Deposit Option Checkbox
    └── Submit → Update Member Deposit (Optional)
```

#### **C. Deposit Management**
```
DepositActivity
    ├── Monthly Deposit Overview
    ├── Member-wise Balance Display
    ├── Deposit History Access
    │   └── DepositHistoryActivity
    │       ├── Single User History
    │       ├── Edit/Delete Deposits
    │       └── Transaction Records
    └── Add New Deposit (Managers Only)
        └── AddDepositActivity
```

### **3. Member Management Flow**

#### **A. Member Overview**
```
MembersActivity
    ├── Member List Display
    │   ├── Profile Information
    │   ├── Role Indicators
    │   └── Contact Details
    ├── Manager Actions
    │   ├── Add New Member → Dialog
    │   ├── Delete Member → Confirmation
    │   └── Edit Member Details
    └── Member Profile Access
        └── ProfileActivity
```

#### **B. Member Addition Process**
```
Add Member Dialog
    ├── Personal Information
    │   ├── Name, Username, Password
    │   ├── Phone (with country picker)
    │   ├── Email, City, Gender
    │   └── Random Password Generation
    ├── Validation Checks
    │   ├── Username uniqueness
    │   ├── Phone number format
    │   └── Email validation
    └── Account Creation → Member List Refresh
```

### **4. Member Initiation Flow**
```
InitiateMemberActivity
    ├── Current Month Context
    ├── Two Categories Display
    │   ├── Initiated Members (This Month)
    │   └── Non-Initiated Members
    └── Initiation Actions
        ├── Add Member to Current Month
        └── Update Member Status
```

**Key Implementation:**
```kotlin
// Member initiation check
private fun checkIsMemberInitiate() {
    val res = myApi.isUserInitiate()
    if (!res.body()!!.error) {
        // User already initiated
    } else {
        // Show initiation dialog
        showInitiationDialog()
    }
}
```

### **5. Purchase Request Flow (Regular Users)**

#### **A. Request Submission**
```
SubmitPurchaseActivity
    ├── Product Selection/Entry
    ├── Purchase Details
    │   ├── Description
    │   ├── Amount
    │   ├── Type (Meal/Other)
    │   └── Deposit Option
    └── Submit Request → Pending Approval
```

#### **B. Request Management**
```
PurchaseRequestActivity
    ├── Tab Layout Navigation
    │   ├── Pending Requests
    │   ├── Accepted Requests
    │   └── Rejected Requests
    └── Manager Actions (Per Fragment)
        ├── Accept → Add to Purchases
        ├── Reject → Update Status
        └── View Details
```

### **6. Reporting & Analytics**

#### **A. Monthly Summary**
```
SummaryActivity
    ├── Month/Year Selection
    ├── Financial Overview
    │   ├── Total Meals & Costs
    │   ├── Purchase Breakdown
    │   ├── Fund Summary
    │   └── Member Balances
    └── Member-wise Statistics
```

#### **B. Report Generation (Managers)**
```
ReportActivity
    ├── Generated Reports List
    ├── Report Creation Dialog
    │   ├── Month/Year Selection
    │   └── Generate PDF
    └── Report Download/Share
```

---

## **Settings & Administration**

### **Settings Dashboard**
```
SettingsActivity
    ├── User Guide Access
    ├── Administrative Controls
    │   ├── Change Super User (Super User Only)
    │   ├── Reset Mess Data (Super User Only)
    │   ├── Generate Reports (Managers)
    │   └── Configuration Toggles
    │       ├── Allow All Users Add Meal
    │       └── Enable Fund Management
    └── System Settings
```

### **Profile Management**
```
ProfileActivity
    ├── Personal Information Display
    ├── Profile Photo Management
    ├── Contact Details Editing
    ├── Password Change Dialog
    ├── Manager Toggle (Super User Only)
    └── Logout Option
```

### **Super User Functions**
```
ChangeSuperuserActivity (Super User Only)
    ├── Current Super User Display
    ├── New Super User Selection
    ├── Transfer Confirmation
    └── Auto Logout → Re-login Required
```

---

## **Error Handling & Edge Cases**

### **Common Error Scenarios**
1. **Network Connectivity Issues**
   - Retry mechanisms in API calls
   - Offline state handling
   - User feedback via toasts/dialogs

2. **Authentication Failures**
   - Token expiration handling
   - Automatic re-login prompts
   - Session management

3. **Permission Violations**
   - Role-based access control
   - UI element visibility based on permissions
   - Server-side validation

4. **Data Validation**
   - Input field validation
   - Business rule enforcement
   - User feedback for invalid data

### **Implementation Examples**
```kotlin
// Error handling in API calls
override fun onFailure(call: Call<Response>, t: Throwable) {
    if(t is UnknownHostException) {
        shortToast("Internet connection not working")
    } else if (t is NetworkErrorException) {
        shortToast("Something went wrong. Contact support")
    }
    loadingDialog.hide()
}
```

---

## **Data Flow & State Management**

### **Local Data Storage**
```kotlin
// LocalDB utility for persistent storage
object LocalDB {
    fun saveUser(user: User)
    fun getUser(): User?
    fun saveAccessToken(token: String)
    fun getAccessToken(): String?
    fun logout() // Clear all stored data
}
```

### **API Integration**
```kotlin
// Centralized API management
class MyApplication : Application() {
    val myApi: ApiInterface by lazy {
        // Retrofit configuration
    }
    
    companion object {
        fun isLogged(): Boolean
        fun logOut(context: Activity)
    }
}
```

### **State Synchronization**
- Real-time data updates across activities
- Automatic refresh on data changes
- Background sync for critical data
- Offline capability with local caching

---

## **Navigation Patterns**

### **Activity Lifecycle Management**
```kotlin
// Base activity for common functionality
open class BaseActivity(checkUserInitiate: Boolean = true) {
    override fun onStart() {
        super.onStart()
        if(Constant.isManagerOrSuperUser() && checkUserInitiate) {
            checkIsMemberInitiate()
        }
    }
}
```

### **Intent-based Navigation**
- Explicit intents for activity transitions
- Data passing via Intent extras
- Result handling for data updates
- Back stack management

### **Fragment Usage**
- Limited fragment usage (mainly for tabs)
- Activity-centric navigation
- Simplified state management
- Clear activity boundaries

---

This documentation provides a comprehensive overview of the Meal Management App's user flow, covering all major features, navigation patterns, and implementation details. The app follows a straightforward activity-based architecture with clear role-based permissions and comprehensive financial management capabilities.
