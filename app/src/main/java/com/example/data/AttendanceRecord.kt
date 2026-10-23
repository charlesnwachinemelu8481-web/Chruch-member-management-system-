package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serviceName: String, // "Sunday Morning Celebration", "Midweek Bible Study", "Youth Fellowship", "Prayer & Worship Night"
    val date: String, // YYYY-MM-DD
    val mode: String, // "Checklist" or "Headcount"
    val adultCount: Int,
    val childCount: Int,
    val guestCount: Int,
    val totalCount: Int,
    val presentMemberIds: String = "", // Comma-separated member ids marked present
    val notes: String = ""
)
