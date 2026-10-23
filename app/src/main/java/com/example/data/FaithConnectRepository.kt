package com.example.data

import kotlinx.coroutines.flow.Flow

class FaithConnectRepository(private val dao: FaithConnectDao) {

    // Members
    val allMembers: Flow<List<Member>> = dao.getAllMembers()
    val memberCount: Flow<Int> = dao.getMemberCount()

    suspend fun getMemberById(id: Long): Member? = dao.getMemberById(id)
    fun getMemberByIdFlow(id: Long): Flow<Member?> = dao.getMemberByIdFlow(id)
    suspend fun saveMember(member: Member): Long = dao.insertMember(member)
    suspend fun updateMember(member: Member) = dao.updateMember(member)
    suspend fun deleteMember(member: Member) = dao.deleteMember(member)

    // Departments
    val allDepartments: Flow<List<Department>> = dao.getAllDepartments()
    suspend fun saveDepartment(department: Department) = dao.insertDepartment(department)

    // Attendance
    val allAttendanceRecords: Flow<List<AttendanceRecord>> = dao.getAllAttendanceRecords()
    val recentAttendanceRecords: Flow<List<AttendanceRecord>> = dao.getRecentAttendanceRecords()
    suspend fun recordAttendance(record: AttendanceRecord): Long = dao.insertAttendanceRecord(record)

    // Events
    val allEvents: Flow<List<ChurchEvent>> = dao.getAllEvents()
    suspend fun saveEvent(event: ChurchEvent): Long = dao.insertEvent(event)
    suspend fun deleteEvent(event: ChurchEvent) = dao.deleteEvent(event)

    // Notes
    fun getNotesForMember(memberId: Long): Flow<List<MemberNote>> = dao.getNotesForMember(memberId)
    val recentNotes: Flow<List<MemberNote>> = dao.getAllRecentNotes()
    suspend fun addNote(note: MemberNote): Long = dao.insertNote(note)
}
