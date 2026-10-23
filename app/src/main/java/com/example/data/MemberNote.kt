package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_notes")
data class MemberNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val authorName: String, // "Pastor David", "Pastor Sarah", "Elder Johnson"
    val date: String, // YYYY-MM-DD
    val category: String, // "Pastoral Visit", "Counseling", "Prayer Request", "Follow-up", "Milestone"
    val content: String
)
