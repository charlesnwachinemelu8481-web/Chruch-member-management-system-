package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FaithConnectDao {

    // Members
    @Query("SELECT * FROM members ORDER BY fullName ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: Long): Member?

    @Query("SELECT * FROM members WHERE id = :id LIMIT 1")
    fun getMemberByIdFlow(id: Long): Flow<Member?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<Member>)

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("SELECT COUNT(*) FROM members")
    fun getMemberCount(): Flow<Int>

    // Departments
    @Query("SELECT * FROM departments ORDER BY name ASC")
    fun getAllDepartments(): Flow<List<Department>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartments(departments: List<Department>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDepartment(department: Department)

    // Attendance
    @Query("SELECT * FROM attendance_records ORDER BY date DESC, id DESC")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records ORDER BY date DESC, id DESC LIMIT 8")
    fun getRecentAttendanceRecords(): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecord(record: AttendanceRecord): Long

    // Events
    @Query("SELECT * FROM church_events ORDER BY date ASC, time ASC")
    fun getAllEvents(): Flow<List<ChurchEvent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: ChurchEvent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<ChurchEvent>)

    @Delete
    suspend fun deleteEvent(event: ChurchEvent)

    // Member Notes
    @Query("SELECT * FROM member_notes WHERE memberId = :memberId ORDER BY id DESC")
    fun getNotesForMember(memberId: Long): Flow<List<MemberNote>>

    @Query("SELECT * FROM member_notes ORDER BY id DESC LIMIT 20")
    fun getAllRecentNotes(): Flow<List<MemberNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: MemberNote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<MemberNote>)
}
