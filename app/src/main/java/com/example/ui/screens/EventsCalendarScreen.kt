package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChurchEvent
import com.example.ui.components.ChurchTopBar
import com.example.ui.theme.BurgundyDeep
import com.example.ui.theme.BurgundyWine
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.FaithConnectViewModel
import java.util.Calendar

@Composable
fun EventsCalendarScreen(
    viewModel: FaithConnectViewModel,
    onOpenDrawer: () -> Unit
) {
    val events by viewModel.events.collectAsState()
    val selectedDate by viewModel.selectedCalendarDate.collectAsState()

    var showAddEventDialog by remember { mutableStateOf(false) }

    // Calendar month tracking (October 2026 default)
    var currentYear by remember { mutableIntStateOf(2026) }
    var currentMonth by remember { mutableIntStateOf(10) } // 10 = October

    val monthName = when (currentMonth) {
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "October"
    }

    // Days in current month
    val daysInMonth = when (currentMonth) {
        9, 11 -> 30
        2 -> if (currentYear % 4 == 0) 29 else 28
        else -> 31
    }

    // First day of month offset (e.g. for Oct 1, 2026, which was a Thursday = index 4)
    val startDayOffset = when (currentMonth) {
        10 -> 4 // Thursday
        11 -> 0 // Sunday
        9 -> 2 // Tuesday
        else -> 4
    }

    val selectedDayEvents = events.filter { it.date == selectedDate }

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = "Church Calendar",
                subtitle = "Services, rehearsals & fellowship events",
                onMenuClick = onOpenDrawer
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = BurgundyWine,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("calendar_fab_add_event")
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Event",
                    modifier = Modifier.size(28.dp)
                )
            }
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
            // Month Header & Navigation
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
                            IconButton(
                                onClick = {
                                    if (currentMonth > 9) currentMonth--
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$monthName $currentYear",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = BurgundyWine
                                )
                                Text(
                                    text = "${events.count { it.date.startsWith("$currentYear-${currentMonth.toString().padStart(2, '0')}") }} scheduled programs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted
                                )
                            }

                            IconButton(
                                onClick = {
                                    if (currentMonth < 12) currentMonth++
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Weekday labels
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                                Text(
                                    text = day,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (day == "S") BurgundyWine else TextMuted,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Calendar Grid (5-6 rows of 7 days)
                        val totalCells = startDayOffset + daysInMonth
                        val rows = (totalCells + 6) / 7

                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                for (c in 0 until 7) {
                                    val cellIndex = r * 7 + c
                                    val dayNum = cellIndex - startDayOffset + 1

                                    if (dayNum in 1..daysInMonth) {
                                        val dayStr = dayNum.toString().padStart(2, '0')
                                        val monthStr = currentMonth.toString().padStart(2, '0')
                                        val thisDate = "$currentYear-$monthStr-$dayStr"
                                        val isSelected = thisDate == selectedDate
                                        val hasEvents = events.any { it.date == thisDate }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isSelected -> BurgundyWine
                                                        hasEvents -> BurgundyWine.copy(alpha = 0.1f)
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .clickable { viewModel.selectCalendarDate(thisDate) }
                                                .testTag("cal_day_$dayNum"),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = dayNum.toString(),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = if (isSelected || hasEvents) FontWeight.Bold else FontWeight.Normal,
                                                    color = when {
                                                        isSelected -> Color.White
                                                        hasEvents -> BurgundyWine
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )

                                                if (hasEvents) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .background(
                                                                if (isSelected) GoldLight else GoldAccent,
                                                                CircleShape
                                                            )
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Events on Selected Date Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROGRAMS FOR $selectedDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "${selectedDayEvents.size} Events",
                        style = MaterialTheme.typography.labelSmall,
                        color = BurgundyWine,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // List of events for selected day
            if (selectedDayEvents.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No church events scheduled for this day.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showAddEventDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BurgundyWine),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+ Schedule Event on this Date", fontSize = 12.sp)
                            }
                        }
                    }
                }
            } else {
                items(selectedDayEvents, key = { it.id }) { event ->
                    ChurchEventCard(
                        event = event,
                        onDelete = { viewModel.deleteEvent(event) }
                    )
                }
            }

            // Upcoming All Events Section
            item {
                Text(
                    text = "ALL SCHEDULED SERVICES & EVENTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            items(events.filter { it.date != selectedDate }, key = { it.id }) { event ->
                ChurchEventCard(
                    event = event,
                    onDelete = { viewModel.deleteEvent(event) }
                )
            }

            item { Spacer(modifier = Modifier.height(60.dp)) }
        }
    }

    // Dialog for Adding a New Church Event
    if (showAddEventDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newDate by remember { mutableStateOf(selectedDate) }
        var newTime by remember { mutableStateOf("9:00 AM - 11:30 AM") }
        var newDept by remember { mutableStateOf("General Church") }
        var newLocation by remember { mutableStateOf("Main Sanctuary") }
        var newDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddEventDialog = false },
            title = {
                Text("Schedule Church Event", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Event Title *") },
                        placeholder = { Text("e.g. Youth Night of Praise") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("event_title_input")
                    )

                    OutlinedTextField(
                        value = newDate,
                        onValueChange = { newDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("Time Schedule") },
                        placeholder = { Text("e.g. 6:30 PM - 8:30 PM") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newDept,
                        onValueChange = { newDept = it },
                        label = { Text("Ministry / Department") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newLocation,
                        onValueChange = { newLocation = it },
                        label = { Text("Location / Venue") },
                        placeholder = { Text("Main Sanctuary / Fellowship Hall") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Description") },
                        placeholder = { Text("Event purpose and details...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addEvent(
                                title = newTitle.trim(),
                                date = newDate.trim(),
                                time = newTime.trim(),
                                department = newDept.trim(),
                                location = newLocation.trim(),
                                description = newDesc.trim(),
                                onSuccess = {
                                    showAddEventDialog = false
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BurgundyWine),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("save_event_submit_btn")
                ) {
                    Text("Schedule Event")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddEventDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ChurchEventCard(
    event: ChurchEvent,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = event.department,
                        style = MaterialTheme.typography.labelSmall,
                        color = BurgundyWine,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldContainer
                ) {
                    Text(
                        text = event.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF8C6400),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = event.time, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Place, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = event.location, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
            }

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
