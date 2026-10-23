package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AttendanceTrendFullChart
import com.example.ui.components.ChurchTopBar
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyWine
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.SuccessContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.FaithConnectViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: FaithConnectViewModel,
    onOpenDrawer: () -> Unit
) {
    val members by viewModel.members.collectAsState()
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()

    val serviceName by viewModel.entryServiceName.collectAsState()
    val serviceDate by viewModel.entryServiceDate.collectAsState()
    val mode by viewModel.entryMode.collectAsState()
    val presentIds by viewModel.checklistPresentIds.collectAsState()
    val adults by viewModel.headcountAdults.collectAsState()
    val children by viewModel.headcountChildren.collectAsState()
    val guests by viewModel.headcountGuests.collectAsState()
    val notes by viewModel.attendanceNote.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var serviceDropdownExpanded by remember { mutableStateOf(false) }

    val availableServices = listOf(
        "Sunday Morning Celebration",
        "Midweek Bible Study",
        "Youth Fellowship",
        "Prayer & Worship Night"
    )

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = "Attendance Tracking",
                subtitle = "Service record & congregation counts",
                onMenuClick = onOpenDrawer
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Service & Date Configuration Card
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
                        Text(
                            text = "SERVICE & SESSION DETAILS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        // Service Name Dropdown
                        ExposedDropdownMenuBox(
                            expanded = serviceDropdownExpanded,
                            onExpandedChange = { serviceDropdownExpanded = !serviceDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = serviceName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Service Program") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceDropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("attendance_service_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = serviceDropdownExpanded,
                                onDismissRequest = { serviceDropdownExpanded = false }
                            ) {
                                availableServices.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s) },
                                        onClick = {
                                            viewModel.entryServiceName.value = s
                                            serviceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Date Field & Quick Pickers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = serviceDate,
                                onValueChange = { viewModel.entryServiceDate.value = it },
                                label = { Text("Date (YYYY-MM-DD)") },
                                leadingIcon = {
                                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = BurgundyWine)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("attendance_date_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.entryServiceDate.value = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Today", color = BurgundyWine, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Entry Mode Switcher (Checklist vs Headcount)
                        Text(
                            text = "Entry Method",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .padding(4.dp)
                        ) {
                            ModeOption(
                                title = "Checklist Mode",
                                subtitle = "Roster by Name",
                                selected = mode == "Checklist",
                                onClick = { viewModel.entryMode.value = "Checklist" },
                                modifier = Modifier.weight(1f)
                            )
                            ModeOption(
                                title = "Headcount Mode",
                                subtitle = "Quick Counters",
                                selected = mode == "Headcount",
                                onClick = { viewModel.entryMode.value = "Headcount" },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Entry Mode Body: Checklist or Headcount
            if (mode == "Checklist") {
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
                                Column {
                                    Text(
                                        text = "Member Roll Call",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${presentIds.size} of ${members.size} members marked present",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SuccessContainer
                                ) {
                                    Text(
                                        text = "${presentIds.size} Present",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.selectAllMembersPresent(true) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Mark All Present", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.selectAllMembersPresent(false) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Clear Selection", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Scrollable interactive checklist
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                members.forEach { member ->
                                    val isPresent = presentIds.contains(member.id)
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isPresent) SuccessContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isPresent) SuccessGreen.copy(alpha = 0.5f) else Color.Transparent
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.toggleMemberPresent(member.id) }
                                            .testTag("checklist_item_${member.id}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            MemberAvatar(name = member.fullName, size = 36.dp, showGoldBorder = false)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = member.fullName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = member.departments.ifBlank { "Member" },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontSize = 11.sp,
                                                    color = TextMuted,
                                                    maxLines = 1
                                                )
                                            }

                                            Checkbox(
                                                checked = isPresent,
                                                onCheckedChange = { viewModel.toggleMemberPresent(member.id) },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = BurgundyWine
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = guests,
                                onValueChange = { viewModel.headcountGuests.value = it },
                                label = { Text("First-Time Guests / Visitors") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            } else {
                // Headcount Mode Form
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
                            Text(
                                text = "RAPID AUDITORIUM HEADCOUNT",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            CounterField(
                                label = "Adult Congregation",
                                value = adults,
                                onValueChange = { viewModel.headcountAdults.value = it }
                            )

                            CounterField(
                                label = "Children's Church",
                                value = children,
                                onValueChange = { viewModel.headcountChildren.value = it }
                            )

                            CounterField(
                                label = "First-Time Guests",
                                value = guests,
                                onValueChange = { viewModel.headcountGuests.value = it }
                            )

                            // Total Preview
                            val aNum = adults.toIntOrNull() ?: 0
                            val cNum = children.toIntOrNull() ?: 0
                            val gNum = guests.toIntOrNull() ?: 0
                            val totalTally = aNum + cNum + gNum

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total Sanctuary Headcount",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BurgundyDeep
                                    )
                                    Text(
                                        text = "$totalTally",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BurgundyWine
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Service Notes Field & Save Button
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { viewModel.attendanceNote.value = it },
                            label = { Text("Service Highlights & Observation Notes") },
                            placeholder = { Text("e.g. Communion Sunday, altar call response, special testimony...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("attendance_notes_input"),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )

                        Button(
                            onClick = {
                                viewModel.saveAttendance {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Attendance record saved successfully!")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_attendance_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BurgundyWine,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save Attendance Record",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Historical Trend Chart
            item {
                AttendanceTrendFullChart(
                    records = attendanceRecords,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ModeOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) BurgundyWine else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) GoldLight else TextMuted
            )
        }
    }
}

@Composable
private fun CounterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Button(
                onClick = {
                    val num = (value.toIntOrNull() ?: 0) - 5
                    onValueChange(num.coerceAtLeast(0).toString())
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Text("-5", color = BurgundyWine, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(90.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    val num = (value.toIntOrNull() ?: 0) + 5
                    onValueChange(num.toString())
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Text("+5", color = BurgundyWine, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
