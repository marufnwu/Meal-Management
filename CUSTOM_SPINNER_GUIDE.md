# User Spinner Implementation Guide

This guide explains how to use the custom user spinner with disabled items and custom design that was implemented for your app.

## Features

✅ **Custom Design**: Material Design 3 styled spinner items with icons and proper spacing  
✅ **Disabled Items**: Ability to disable specific items based on business logic  
✅ **Header Support**: Support for header items (like "Select User")  
✅ **Visual Feedback**: Different styling for enabled/disabled items  
✅ **Flexible Logic**: Easy to customize which items should be disabled  

## Files Created/Modified

### New Files:
- `UserSpinnerAdapter.kt` - The main custom user spinner adapter
- `UserUserSpinnerHelper.kt` - Helper class with business logic
- `user_spinner_selected_item.xml` - Layout for selected item
- `user_spinner_dropdown_item.xml` - Layout for dropdown items
- `ic_person_24.xml`, `ic_person_off_24.xml`, `ic_baseline_arrow_drop_down_24.xml` - Icons

### Modified Files:
- `AddMealActivity.kt` - Updated to use custom adapter
- `colors.xml` - Added spinner-specific colors
- `strings.xml` - Added user_icon string

## How to Use

### 1. Basic Usage

Replace your existing spinner setup:

```kotlin
// OLD CODE:
ArrayAdapter(this, android.R.layout.simple_spinner_item, usersArray)
    .also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMember.adapter = adapter
    }

// NEW CODE:
val userUserSpinnerItems = UserUserSpinnerHelper.createUserUserSpinnerItems(userList, "Select User")
val userSpinnerAdapter = UserSpinnerAdapter(this, userUserSpinnerItems)
binding.spinnerMember.adapter = userSpinnerAdapter
```

### 2. Customizing Disabled Logic

Edit `UserUserSpinnerHelper.kt` to customize which items should be disabled:

```kotlin
fun isUserEnabled(messUser: MessUser?): Boolean {
    return when {
        messUser == null -> false
        messUser.user == null -> false
        
        // Add your custom conditions here:
        messUser.status == "INACTIVE" -> false
        messUser.user.name?.contains("suspended", ignoreCase = true) == true -> false
        messUser.hasAddedMealToday == true -> false  // Example condition
        
        else -> true
    }
}
```

### 3. Handling Selection

Update your `onItemSelected` method:

```kotlin
override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    val adapter = parent?.adapter as? UserSpinnerAdapter
    adapter?.let { userSpinnerAdapter ->
        val selectedMessUser = userSpinnerAdapter.getSelectedMessUser(position)
        
        if (selectedMessUser != null) {
            // Handle enabled item selection
            selectedUser = selectedMessUser
            // Your logic here
        } else {
            // Handle header or disabled item selection
            selectedUser = null
        }
    }
}
```

### 4. Manual Creation of Spinner Items

For more control, create spinner items manually:

```kotlin
val userSpinnerItems = mutableListOf<UserSpinnerItem>()

// Add header
userSpinnerItems.add(UserSpinnerItem(null, "Select User", true, true))

// Add users with custom logic
userList.forEach { user ->
    val isEnabled = checkCustomCondition(user)  // Your logic
    val displayText = user.user?.name ?: "Unknown"
    userSpinnerItems.add(UserSpinnerItem(user, displayText, isEnabled, false))
}

val adapter = UserSpinnerAdapter(this, userSpinnerItems)
binding.spinner.adapter = adapter
```

## Customization Options

### Colors
Edit `colors.xml` to change spinner colors:
```xml
<color name="enabled_text_color">@color/md_theme_onSurface</color>
<color name="disabled_text_color">@color/md_theme_onSurfaceVariant</color>
<color name="colorPrimary">@color/md_theme_primary</color>
```

### Icons
Replace the drawable files to change icons:
- `ic_person_24.xml` - Icon for enabled users
- `ic_person_off_24.xml` - Icon for disabled users
- `ic_baseline_arrow_drop_down_24.xml` - Dropdown arrow

### Layouts
Customize the appearance by editing:
- `spinner_selected_item.xml` - How the selected item looks
- `spinner_dropdown_item.xml` - How dropdown items look

## Common Use Cases

### 1. Disable Users Who Already Added Meals Today
```kotlin
fun isUserEnabled(messUser: MessUser?): Boolean {
    return when {
        messUser?.hasAddedMealToday == true -> false
        else -> true
    }
}
```

### 2. Disable Inactive Users
```kotlin
fun isUserEnabled(messUser: MessUser?): Boolean {
    return when {
        messUser?.status != "ACTIVE" -> false
        else -> true
    }
}
```

### 3. Disable Users Based on Role
```kotlin
fun isUserEnabled(messUser: MessUser?): Boolean {
    return when {
        messUser?.user?.role == "ADMIN" -> false  // Admins can't add meals
        else -> true
    }
}
```

## Troubleshooting

### Issue: Items not showing as disabled
- Check that `isEnabled` returns `false` for the items you want to disable
- Verify that `areAllItemsEnabled()` returns `false` in the adapter

### Issue: Selection not working properly
- Make sure you're using `getSelectedMessUser()` method instead of direct array access
- Check that your `onItemSelected` method is properly casting the adapter

### Issue: Custom styling not applied
- Verify that the color resources exist in `colors.xml`
- Check that drawable resources are in the correct directory
- Ensure layouts are using the correct resource references

## Example Implementation

Here's a complete example of how it's implemented in `AddMealActivity`:

```kotlin
private fun setUsersToSpinner(userList: List<MessUser>) {
    val userSpinnerItems = UserSpinnerHelper.createUserSpinnerItems(userList, "Select User")
    val userSpinnerAdapter = UserSpinnerAdapter(this, userSpinnerItems)
    binding.spinnerMember.adapter = userSpinnerAdapter
}

override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    val adapter = parent?.adapter as? UserSpinnerAdapter
    adapter?.let { userSpinnerAdapter ->
        val selectedMessUser = userSpinnerAdapter.getSelectedMessUser(position)
        
        if (selectedMessUser != null) {
            selectedUser = selectedMessUser
            getUserMealByDate()
        } else {
            selectedUser = null
        }
    }
}
```

This implementation gives you full control over which items are selectable and provides a modern, Material Design 3 styled spinner interface.