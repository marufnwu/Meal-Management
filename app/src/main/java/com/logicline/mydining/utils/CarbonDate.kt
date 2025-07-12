package com.logicline.mydining.utils

import java.text.SimpleDateFormat
import java.util.*

@JvmInline
value class CarbonDate(val raw: String) {



    fun toDate(): Date? {
        return try {
            // Try parsing with microseconds (Laravel format)
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(raw)
        } catch (e: Exception) {
            try {
                // Fallback for seconds only (no microseconds)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                sdf.parse(raw)
            } catch (e2: Exception) {
                try {
                    // Fallback for date only
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf.parse(raw)
                } catch (e3: Exception) {
                    null
                }
            }
        }
    }

    fun toDisplayDate(format: String = "MM-dd-yyyy"): String {
        val date = toDate()
        return if (date != null) {
            SimpleDateFormat(format, Locale.getDefault()).format(date)
        } else {
            raw
        }
    }

    fun toDisplayDateTime(format: String = "MM dd, yyyy 'at' HH:mm"): String {
        val date = toDate()
        return if (date != null) {
            SimpleDateFormat(format, Locale.getDefault()).format(date)
        } else {
            raw
        }
    }

    fun toRelativeTime(): String {
        val date = toDate() ?: return raw
        val now = Date()
        val diffInMillis = now.time - date.time
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24

        return when {
            diffInDays > 0 -> "$diffInDays day${if (diffInDays > 1) "s" else ""} ago"
            diffInHours > 0 -> "$diffInHours hour${if (diffInHours > 1) "s" else ""} ago"
            diffInMinutes > 0 -> "$diffInMinutes minute${if (diffInMinutes > 1) "s" else ""} ago"
            else -> "Just now"
        }
    }

    fun isToday(): Boolean {
        val date = toDate() ?: return false
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance().apply { time = date }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(): Boolean {
        val date = toDate() ?: return false
        val cal1 = Calendar.getInstance()
        cal1.add(Calendar.DAY_OF_YEAR, -1)
        val cal2 = Calendar.getInstance().apply { time = date }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun getDayOfWeek(): String {
        val date = toDate() ?: return ""
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(date)
    }

    fun getMonthName(): String {
        val date = toDate() ?: return ""
        val sdf = SimpleDateFormat("MMMM", Locale.getDefault())
        return sdf.format(date)
    }

    fun getYear(): Int {
        val date = toDate() ?: return 0
        val cal = Calendar.getInstance().apply { time = date }
        return cal.get(Calendar.YEAR)
    }

    /**
     * Returns a string representation of the date using the specified format.
     * If the date cannot be parsed, returns the raw string value.
     *
     * @param format The date format pattern to use (defaults to ISO format)
     * @return Formatted string representation of the date
     */
    override fun toString(): String {
        val format: String = "yyyy-MM-dd"
        val date = toDate()
        return if (date != null) {
            SimpleDateFormat(format, Locale.getDefault()).format(date)
        } else {
            raw
        }
    }

    fun getMonth(): Int {
        val date = toDate() ?: return 0
        val cal = Calendar.getInstance().apply { time = date }
        return cal.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
    }

    fun getDay(): Int {
        val date = toDate() ?: return 0
        val cal = Calendar.getInstance().apply { time = date }
        return cal.get(Calendar.DAY_OF_MONTH)
    }
}
