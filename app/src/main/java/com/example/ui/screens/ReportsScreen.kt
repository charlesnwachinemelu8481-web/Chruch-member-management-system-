package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AttendanceTrendFullChart
import com.example.ui.components.ChurchTopBar
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyWine
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.FaithConnectViewModel

@Composable
fun ReportsScreen(
    viewModel: FaithConnectViewModel,
    onOpenDrawer: () -> Unit
) {
    val members by viewModel.members.collectAsState()
    val departments by viewModel.departments.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()

    val totalMembers = members.size.coerceAtLeast(1)
    val avgAttendance = if (attendanceRecords.isNotEmpty()) {
        attendanceRecords.map { it.totalCount }.average().toInt()
    } else 320

    val maxAttendance = attendanceRecords.maxOfOrNull { it.totalCount } ?: 362

    // Demographic counts
    val youthCount = members.count { it.ageGroup == "Youth" }
    val adultCount = members.count { it.ageGroup == "Adult" }
    val seniorCount = members.count { it.ageGroup == "Senior" }
    val childCount = members.count { it.ageGroup == "Child" }

    val femaleCount = members.count { it.gender == "Female" }
    val maleCount = members.count { it.gender == "Male" }

    val distinctHouseholds = members.mapNotNull { it.householdName.ifBlank { null } }.distinct().size

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = "Reports & Analytics",
                subtitle = "Growth metrics, demographics & ministry reports",
                onMenuClick = onOpenDrawer
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
            // High-Level Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "EXECUTIVE SUMMARY",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SuccessContainer
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+14% Net Growth",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SuccessGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ReportMetricItem(value = "$avgAttendance", label = "Avg Weekly Attendance")
                            ReportMetricItem(value = "$maxAttendance", label = "Peak Congregation")
                            ReportMetricItem(value = "$distinctHouseholds", label = "Active Households")
                        }
                    }
                }
            }

            // Section 1: Attendance Over Time
            item {
                Text(
                    text = "ATTENDANCE OVER TIME",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                AttendanceTrendFullChart(
                    records = attendanceRecords,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 2: Department Growth & Distribution
            item {
                Text(
                    text = "MINISTRY & DEPARTMENT DISTRIBUTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        departments.forEach { dept ->
                            val deptCount = members.count {
                                it.departments.contains(dept.name, ignoreCase = true) ||
                                it.departments.contains(dept.id, ignoreCase = true)
                            }
                            val progress = (deptCount.toFloat() / totalMembers.toFloat()).coerceIn(0.05f, 1f)

                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dept.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "$deptCount members (${(progress * 100).toInt()}%)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = BurgundyWine,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = BurgundyWine,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Section 3: Member Demographics
            item {
                Text(
                    text = "MEMBER DEMOGRAPHICS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Congregation Age Brackets",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        DemographicBar(label = "Adults (26 - 55 yrs)", count = adultCount, total = totalMembers, color = BurgundyWine)
                        DemographicBar(label = "Youth & Young Adults (13 - 25 yrs)", count = youthCount, total = totalMembers, color = GoldAccent)
                        DemographicBar(label = "Seniors (56+ yrs)", count = seniorCount, total = totalMembers, color = NavyPrimary)
                        DemographicBar(label = "Children (0 - 12 yrs)", count = childCount, total = totalMembers, color = Color(0xFFC77800))

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Gender Ratio",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        val fPercent = ((femaleCount.toFloat() / totalMembers.toFloat()) * 100).toInt()
                        val mPercent = 100 - fPercent

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Female: $femaleCount ($fPercent%)", style = MaterialTheme.typography.bodySmall, color = BurgundyWine, fontWeight = FontWeight.Bold)
                            Text(text = "Male: $maleCount ($mPercent%)", style = MaterialTheme.typography.bodySmall, color = NavyPrimary, fontWeight = FontWeight.Bold)
                        }

                        // Split Progress Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(fPercent.coerceAtLeast(1).toFloat())
                                    .fillMaxSize()
                                    .background(BurgundyWine)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(mPercent.coerceAtLeast(1).toFloat())
                                    .fillMaxSize()
                                    .background(NavyPrimary)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
private fun ReportMetricItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = BurgundyWine
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun DemographicBar(
    label: String,
    count: Int,
    total: Int,
    color: Color
) {
    val progress = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "$count (${(progress * 100).toInt()}%)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
