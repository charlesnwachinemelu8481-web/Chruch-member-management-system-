package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "church_events")
data class ChurchEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String, // YYYY-MM-DD
    val time: String, // e.g. "9:00 AM - 11:30 AM"
    val department: String, // "General Church", "Choir", "Youth", "Men of Faith", "Women of Grace"
    val location: String, // "Main Sanctuary", "Fellowship Hall", "Room 201"
    val description: String,
    val isRecurringWeekly: Boolean = false
)
