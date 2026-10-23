package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Diversity3
import androidx.compose.material.icons.outlined.People
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object MembersList : Screen("members_list")
    data object AddEditMember : Screen("add_edit_member?memberId={memberId}") {
        fun createRoute(memberId: Long? = null) = if (memberId != null) "add_edit_member?memberId=$memberId" else "add_edit_member"
    }
    data object MemberProfile : Screen("member_profile/{memberId}") {
        fun createRoute(memberId: Long) = "member_profile/$memberId"
    }
    data object Departments : Screen("departments")
    data object Attendance : Screen("attendance")
    data object EventsCalendar : Screen("events_calendar")
    data object Reports : Screen("reports")
}

data class DrawerMenuItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int? = null
)

val drawerMenuItems = listOf(
    DrawerMenuItem(
        title = "Dashboard",
        route = Screen.Dashboard.route,
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    ),
    DrawerMenuItem(
        title = "Member Directory",
        route = Screen.MembersList.route,
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    ),
    DrawerMenuItem(
        title = "Ministries & Depts",
        route = Screen.Departments.route,
        selectedIcon = Icons.Filled.Diversity3,
        unselectedIcon = Icons.Outlined.Diversity3
    ),
    DrawerMenuItem(
        title = "Attendance Tracking",
        route = Screen.Attendance.route,
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle
    ),
    DrawerMenuItem(
        title = "Church Calendar",
        route = Screen.EventsCalendar.route,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    ),
    DrawerMenuItem(
        title = "Reports & Analytics",
        route = Screen.Reports.route,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )
)
