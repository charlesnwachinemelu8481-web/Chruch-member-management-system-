package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AttendanceRecord
import com.example.data.ChurchEvent
import com.example.data.Department
import com.example.data.FaithConnectDatabase
import com.example.data.FaithConnectRepository
import com.example.data.Member
import com.example.data.MemberNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UserSession(
    val name: String = "Pastor David Vance",
    val title: String = "Senior Pastor",
    val email: String = "pastor.david@faithconnect.church",
    val role: String = "Pastor", // "Pastor" or "Church Admin"
    val avatarInitials: String = "DV"
)

class FaithConnectViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FaithConnectRepository

    init {
        val db = FaithConnectDatabase.getDatabase(application, viewModelScope)
        repository = FaithConnectRepository(db.dao())
    }

    // User authentication session
    private val _userSession = MutableStateFlow(UserSession())
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun login(role: String, passcode: String = "") {
        if (role == "Pastor") {
            _userSession.value = UserSession(
                name = "Pastor David Vance",
                title = "Senior Pastor",
                email = "pastor.david@faithconnect.church",
                role = "Pastor",
                avatarInitials = "DV"
            )
        } else {
            _userSession.value = UserSession(
                name = "Sarah Jenkins",
                title = "Church Administrator",
                email = "admin.sarah@faithconnect.church",
                role = "Church Admin",
                avatarInitials = "SJ"
            )
        }
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
    }

    // All Members
    val members: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filters for members directory
    val searchQuery = MutableStateFlow("")
    val selectedDepartmentFilter = MutableStateFlow("All")
    val selectedStatusFilter = MutableStateFlow("All")

    val filteredMembers: StateFlow<List<Member>> = combine(
        members,
        searchQuery,
        selectedDepartmentFilter,
        selectedStatusFilter
    ) { list, query, dept, status ->
        list.filter { member ->
            val matchesQuery = query.isBlank() ||
                member.fullName.contains(query, ignoreCase = true) ||
                member.phone.contains(query, ignoreCase = true) ||
                member.email.contains(query, ignoreCase = true) ||
                member.departments.contains(query, ignoreCase = true) ||
                member.householdName.contains(query, ignoreCase = true)

            val matchesDept = dept == "All" || member.departments.contains(dept, ignoreCase = true)
            val matchesStatus = status == "All" || member.status.equals(status, ignoreCase = true)

            matchesQuery && matchesDept && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Departments
    val departments: StateFlow<List<Department>> = repository.allDepartments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedDepartmentForRoster = MutableStateFlow<Department?>(null)

    fun selectDepartmentForRoster(dept: Department?) {
        selectedDepartmentForRoster.value = dept
    }

    // Attendance
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendanceRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentAttendance: StateFlow<List<AttendanceRecord>> = repository.recentAttendanceRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // New Attendance entry form state
    val entryServiceName = MutableStateFlow("Sunday Morning Celebration")
    val entryServiceDate = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )
    val entryMode = MutableStateFlow("Checklist") // "Checklist" or "Headcount"
    val checklistPresentIds = MutableStateFlow<Set<Long>>(setOf(1L, 2L, 4L, 5L, 6L, 7L, 8L))
    val headcountAdults = MutableStateFlow("245")
    val headcountChildren = MutableStateFlow("58")
    val headcountGuests = MutableStateFlow("24")
    val attendanceNote = MutableStateFlow("")

    fun toggleMemberPresent(memberId: Long) {
        val current = checklistPresentIds.value.toMutableSet()
        if (current.contains(memberId)) {
            current.remove(memberId)
        } else {
            current.add(memberId)
        }
        checklistPresentIds.value = current
    }

    fun selectAllMembersPresent(present: Boolean) {
        if (present) {
            checklistPresentIds.value = members.value.map { it.id }.toSet()
        } else {
            checklistPresentIds.value = emptySet()
        }
    }

    fun saveAttendance(onSaved: () -> Unit = {}) {
        viewModelScope.launch {
            val record = if (entryMode.value == "Checklist") {
                val presentList = checklistPresentIds.value.toList()
                val total = presentList.size + (headcountGuests.value.toIntOrNull() ?: 0)
                AttendanceRecord(
                    serviceName = entryServiceName.value,
                    date = entryServiceDate.value,
                    mode = "Checklist",
                    adultCount = (presentList.size * 0.75).toInt().coerceAtLeast(presentList.size - 2),
                    childCount = (presentList.size * 0.25).toInt(),
                    guestCount = headcountGuests.value.toIntOrNull() ?: 0,
                    totalCount = total,
                    presentMemberIds = presentList.joinToString(","),
                    notes = attendanceNote.value
                )
            } else {
                val adults = headcountAdults.value.toIntOrNull() ?: 0
                val children = headcountChildren.value.toIntOrNull() ?: 0
                val guests = headcountGuests.value.toIntOrNull() ?: 0
                AttendanceRecord(
                    serviceName = entryServiceName.value,
                    date = entryServiceDate.value,
                    mode = "Headcount",
                    adultCount = adults,
                    childCount = children,
                    guestCount = guests,
                    totalCount = adults + children + guests,
                    notes = attendanceNote.value
                )
            }
            repository.recordAttendance(record)
            attendanceNote.value = ""
            onSaved()
        }
    }

    // Events & Calendar
    val events: StateFlow<List<ChurchEvent>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedCalendarDate = MutableStateFlow(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    )

    fun selectCalendarDate(date: String) {
        selectedCalendarDate.value = date
    }

    fun addEvent(
        title: String,
        date: String,
        time: String,
        department: String,
        location: String,
        description: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val event = ChurchEvent(
                title = title,
                date = date,
                time = time,
                department = department,
                location = location,
                description = description
            )
            repository.saveEvent(event)
            onSuccess()
        }
    }

    fun deleteEvent(event: ChurchEvent) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    // Member Profile & Edit
    suspend fun getMemberById(id: Long): Member? = repository.getMemberById(id)
    fun getMember(id: Long): Flow<Member?> = repository.getMemberByIdFlow(id)

    fun getMemberNotes(memberId: Long): Flow<List<MemberNote>> = repository.getNotesForMember(memberId)

    fun saveMember(
        id: Long = 0,
        fullName: String,
        phone: String,
        email: String,
        address: String,
        dateJoined: String,
        departments: String,
        householdRole: String,
        householdName: String,
        status: String,
        gender: String,
        ageGroup: String,
        onComplete: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val member = Member(
                id = id,
                fullName = fullName,
                phone = phone,
                email = email,
                address = address,
                dateJoined = dateJoined,
                departments = departments,
                householdRole = householdRole,
                householdName = householdName,
                status = status,
                gender = gender,
                ageGroup = ageGroup
            )
            val newId = repository.saveMember(member)
            onComplete(if (id != 0L) id else newId)
        }
    }

    fun deleteMember(member: Member, onDeleted: () -> Unit) {
        viewModelScope.launch {
            repository.deleteMember(member)
            onDeleted()
        }
    }

    fun addMemberNote(
        memberId: Long,
        category: String,
        content: String,
        onAdded: () -> Unit
    ) {
        viewModelScope.launch {
            val note = MemberNote(
                memberId = memberId,
                authorName = userSession.value.name,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                category = category,
                content = content
            )
            repository.addNote(note)
            onAdded()
        }
    }
}
