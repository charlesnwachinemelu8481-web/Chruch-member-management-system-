package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.AttendanceMiniChart
import com.example.ui.components.ChurchTopBar
import com.example.ui.components.MemberAvatar
import com.example.ui.components.StatCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyWine
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.FaithConnectViewModel

@Composable
fun DashboardScreen(
    viewModel: FaithConnectViewModel,
    onOpenDrawer: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val userSession by viewModel.userSession.collectAsState()
    val members by viewModel.members.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val events by viewModel.events.collectAsState()

    val totalMembers = members.size
    val activeDepartmentsCount = departments.size
    val latestAttendance = attendanceRecords.firstOrNull()?.totalCount ?: 362
    val upcomingEventsCount = events.size

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = "FaithConnect",
                subtitle = "Grace Cathedral",
                onMenuClick = onOpenDrawer,
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = GoldAccent.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldAccent),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userSession.avatarInitials,
                                color = GoldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Sanctuary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.church_sanctuary_banner_1792761518497),
                            contentDescription = "Sanctuary",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            BurgundyWine.copy(alpha = 0.9f),
                                            BurgundyDeep.copy(alpha = 0.75f)
                                        )
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Welcome back, ${userSession.name.split(" ").last()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Ministry overview & congregation management",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldLight
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Today: Sunday Divine Service",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }

            // Stat Cards Grid (2x2)
            item {
                Text(
                    text = "MINISTRY METRICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Members",
                        value = "$totalMembers",
                        subtitle = "Active congregation",
                        icon = Icons.Filled.People,
                        trendText = "+12% this mo",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_total_members"),
                        onClick = { onNavigate(Screen.MembersList.route) }
                    )

                    StatCard(
                        title = "Active Ministries",
                        value = "$activeDepartmentsCount",
                        subtitle = "Serving teams",
                        icon = Icons.Filled.Diversity3,
                        iconTint = NavyPrimary,
                        iconBg = NavyContainer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_active_depts"),
                        onClick = { onNavigate(Screen.Departments.route) }
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Last Attendance",
                        value = "$latestAttendance",
                        subtitle = "Worship celebration",
                        icon = Icons.Filled.CheckCircle,
                        trendText = "+8% vs avg",
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_attendance"),
                        onClick = { onNavigate(Screen.Attendance.route) }
                    )

                    StatCard(
                        title = "Upcoming Events",
                        value = "$upcomingEventsCount",
                        subtitle = "Next 30 days",
                        icon = Icons.Filled.CalendarMonth,
                        iconTint = GoldAccent,
                        iconBg = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("stat_events"),
                        onClick = { onNavigate(Screen.EventsCalendar.route) }
                    )
                }
            }

            // Attendance Trend Mini-Chart Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.Attendance.route) }
                        .testTag("dashboard_attendance_chart_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Weekly Attendance Trend",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Consecutive Sunday services attendance count",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View Details",
                                tint = BurgundyWine,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        AttendanceMiniChart(
                            records = attendanceRecords,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Quick Actions Bar
            item {
                Text(
                    text = "QUICK ACTIONS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onNavigate(Screen.AddEditMember.createRoute()) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("dash_quick_add_member_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BurgundyWine)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Member", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(Screen.Attendance.route) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("dash_quick_attendance_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BurgundyWine)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Attendance", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Upcoming Church Schedule Preview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UPCOMING SERVICES & PROGRAMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "View Calendar",
                        style = MaterialTheme.typography.labelMedium,
                        color = BurgundyWine,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigate(Screen.EventsCalendar.route) }
                    )
                }
            }

            items(events.take(3)) { event ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(Screen.EventsCalendar.route) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BurgundyWine.copy(alpha = 0.1f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                val dateParts = event.date.split("-")
                                val day = dateParts.getOrNull(2) ?: "25"
                                val month = when (dateParts.getOrNull(1)) {
                                    "10" -> "OCT"
                                    "11" -> "NOV"
                                    "12" -> "DEC"
                                    else -> "OCT"
                                }
                                Text(
                                    text = month,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = BurgundyWine,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyWine,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${event.time} • ${event.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
