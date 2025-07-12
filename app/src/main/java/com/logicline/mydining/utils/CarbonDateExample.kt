package com.logicline.mydining.utils

import com.logicline.mydining.data.models.Mess

/**
 * Example usage of CarbonDate
 * 
 * This file demonstrates how to use CarbonDate in your Android app
 * when working with Laravel API responses.
 */

object CarbonDateExample {
    
    fun demonstrateUsage() {
        // Example 1: Basic usage with API response
        val apiDateString = "2025-06-29T12:26:54.000000Z"
        val carbonDate = CarbonDate(apiDateString)
        
        // Convert to Date object
        val date = carbonDate.toDate()
        
        // Format for display
        val displayDate = carbonDate.toDisplayDate() // "Jun 29, 2025"
        val displayDateTime = carbonDate.toDisplayDateTime() // "Jun 29, 2025 at 12:26"
        
        // Get relative time
        val relativeTime = carbonDate.toRelativeTime() // "2 days ago"
        
        // Check if it's today/yesterday
        val isToday = carbonDate.isToday()
        val isYesterday = carbonDate.isYesterday()
        
        // Get specific date components
        val year = carbonDate.getYear() // 2025
        val month = carbonDate.getMonth() // 6
        val day = carbonDate.getDay() // 29
        val dayOfWeek = carbonDate.getDayOfWeek() // "Sunday"
        val monthName = carbonDate.getMonthName() // "June"
        
        // Example 2: In your data models
        val mess = Mess(
            id = 1,
            name = "My Mess",
            status = "active",
            adFree = true,
            allUserAddMeal = false,
            fundAddEnabled = true,
            createdAt = CarbonDate("2025-06-29T12:26:54.000000Z"),
            updatedAt = CarbonDate("2025-06-29T12:26:54.000000Z"),
            isAcceptingMembers = true
        )
        
        // Now you can easily format dates in your UI
        val createdDate = mess.createdAt?.toDisplayDate() // "Jun 29, 2025"
        val updatedDate = mess.updatedAt?.toDisplayDateTime() // "Jun 29, 2025 at 12:26"
        
        // Example 3: In your adapters
        // Instead of complex date parsing, just use:
        // txtDate.text = mess.createdAt?.toDisplayDate() ?: "Recently created"
        
        // Example 4: Custom format
        val customFormat = carbonDate.toDisplayDate("EEEE, MMMM dd, yyyy") // "Sunday, June 29, 2025"
    }
    
    fun formatPatterns() {
        val carbonDate = CarbonDate("2025-06-29T12:26:54.000000Z")
        
        // Common date formats
        carbonDate.toDisplayDate("MM/dd/yyyy") // "06/29/2025"
        carbonDate.toDisplayDate("dd-MM-yyyy") // "29-06-2025"
        carbonDate.toDisplayDate("yyyy-MM-dd") // "2025-06-29"
        carbonDate.toDisplayDate("MMM dd") // "Jun 29"
        carbonDate.toDisplayDate("MMMM yyyy") // "June 2025"
        
        // DateTime formats
        carbonDate.toDisplayDateTime("MMM dd, yyyy HH:mm") // "Jun 29, 2025 12:26"
        carbonDate.toDisplayDateTime("EEEE, MMMM dd, yyyy 'at' HH:mm") // "Sunday, June 29, 2025 at 12:26"
    }
} 