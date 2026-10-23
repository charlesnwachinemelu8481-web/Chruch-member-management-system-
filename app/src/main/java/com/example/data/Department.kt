package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "departments")
data class Department(
    @PrimaryKey
    val id: String, // e.g. "choir", "ushering", "youth", "children", "men", "women", "media", "evangelism"
    val name: String,
    val leaderName: String,
    val description: String,
    val meetingTime: String,
    val iconCategory: String, // "music", "hospitality", "group", "child", "men", "women", "tech", "outreach"
    val roomOrVenue: String = "Main Auditorium"
)
