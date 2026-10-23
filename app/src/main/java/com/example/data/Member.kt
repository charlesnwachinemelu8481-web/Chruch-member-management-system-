package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val phone: String,
    val email: String,
    val address: String,
    val dateJoined: String,
    val departments: String, // Comma-separated departments, e.g. "Choir, Ushering"
    val householdRole: String = "Head of Household", // Head of Household, Spouse, Child, Individual
    val householdName: String = "", // e.g. "The Adewale Family"
    val photoUri: String = "",
    val status: String = "Active", // Active, New Member, Honorary, Inactive
    val gender: String = "Female", // Female, Male
    val ageGroup: String = "Adult", // Child, Youth, Adult, Senior
    val baptismStatus: String = "Baptized",
    val emergencyContact: String = ""
)
