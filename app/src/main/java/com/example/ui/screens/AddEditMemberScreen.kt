package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ChurchTopBar
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.BurgundyWine
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.FaithConnectViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditMemberScreen(
    memberId: Long?,
    viewModel: FaithConnectViewModel,
    onNavigateBack: () -> Unit,
    onSaved: (Long) -> Unit
) {
    BackHandler { onNavigateBack() }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dateJoined by remember {
        mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    }
    var householdRole by remember { mutableStateOf("Head of Household") }
    var householdName by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Active") }
    var gender by remember { mutableStateOf("Female") }
    var ageGroup by remember { mutableStateOf("Adult") }

    val allMinistries = listOf(
        "Choir & Music Ministry",
        "Ushering & Hospitality",
        "Youth & Young Adults",
        "Children's Church (Kingdom Kids)",
        "Men of Faith Fellowship",
        "Women of Grace",
        "Media & Audio-Visual",
        "Evangelism & Community Care"
    )

    var selectedMinistries by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(memberId) {
        if (memberId != null && memberId != 0L) {
            isEditing = true
            val member = viewModel.getMemberById(memberId)
            member?.let {
                fullName = it.fullName
                phone = it.phone
                email = it.email
                address = it.address
                dateJoined = it.dateJoined
                householdRole = it.householdRole
                householdName = it.householdName
                status = it.status
                gender = it.gender
                ageGroup = it.ageGroup
                selectedMinistries = it.departments.split(",")
                    .map { dept -> dept.trim() }
                    .filter { dept -> dept.isNotBlank() }
                    .toSet()
            }
        }
    }

    Scaffold(
        topBar = {
            ChurchTopBar(
                title = if (isEditing) "Edit Member" else "Add New Member",
                subtitle = if (isEditing) fullName.ifBlank { "Member Record" } else "Register into FaithConnect",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo & Avatar Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MemberAvatar(
                        name = fullName.ifBlank { "New Member" },
                        size = 64.dp,
                        showGoldBorder = true
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Member Avatar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Generated automatically with liturgical gold trim based on name",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }

            // Personal Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "PERSONAL INFORMATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name *") },
                        placeholder = { Text("e.g. Marcus Sterling") },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = BurgundyWine)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_input_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number *") },
                        placeholder = { Text("+1 (555) 000-0000") },
                        leadingIcon = {
                            Icon(Icons.Filled.Phone, contentDescription = null, tint = BurgundyWine)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_input_phone"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("member@gmail.com") },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null, tint = BurgundyWine)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_input_email"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Residential Address") },
                        placeholder = { Text("Street, City, State") },
                        leadingIcon = {
                            Icon(Icons.Filled.Home, contentDescription = null, tint = BurgundyWine)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_input_address"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = dateJoined,
                        onValueChange = { dateJoined = it },
                        label = { Text("Date Joined (YYYY-MM-DD)") },
                        placeholder = { Text("2024-01-15") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_input_date_joined"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Gender & Age Group Selectors
                    Text(
                        text = "Demographics & Category",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Female", "Male").forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BurgundyWine,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Youth", "Adult", "Senior", "Child").forEach { cat ->
                            FilterChip(
                                selected = ageGroup == cat,
                                onClick = { ageGroup = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BurgundyWine,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Family & Household Link
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "FAMILY & HOUSEHOLD LINK",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    OutlinedTextField(
                        value = householdName,
                        onValueChange = { householdName = it },
                        label = { Text("Household / Family Name") },
                        placeholder = { Text("e.g. The Sterling Family") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("member_input_household"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Text(
                        text = "Role in Household",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Head of Household", "Spouse", "Child", "Individual").forEach { role ->
                            FilterChip(
                                selected = householdRole == role,
                                onClick = { householdRole = role },
                                label = { Text(role, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BurgundyWine,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Ministries & Departments
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Diversity3,
                            contentDescription = null,
                            tint = BurgundyWine,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "DEPARTMENT MEMBERSHIPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "Tap ministries to assign this member:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allMinistries.forEach { ministry ->
                            val isSelected = selectedMinistries.contains(ministry)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    val current = selectedMinistries.toMutableSet()
                                    if (isSelected) current.remove(ministry) else current.add(ministry)
                                    selectedMinistries = current
                                },
                                label = { Text(ministry, fontSize = 12.sp) },
                                leadingIcon = if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BurgundyWine,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Membership Status
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "CHURCH STATUS",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Active", "New Member", "Honorary").forEach { st ->
                            FilterChip(
                                selected = status == st,
                                onClick = { status = st },
                                label = { Text(st) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BurgundyWine,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Save Action Button
            Button(
                onClick = {
                    if (fullName.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please enter member full name")
                        }
                        return@Button
                    }
                    if (phone.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please enter contact phone number")
                        }
                        return@Button
                    }

                    val deptsString = selectedMinistries.joinToString(", ")
                    viewModel.saveMember(
                        id = memberId ?: 0L,
                        fullName = fullName.trim(),
                        phone = phone.trim(),
                        email = email.trim(),
                        address = address.trim(),
                        dateJoined = dateJoined.trim(),
                        departments = deptsString,
                        householdRole = householdRole,
                        householdName = householdName.trim(),
                        status = status,
                        gender = gender,
                        ageGroup = ageGroup,
                        onComplete = { savedId ->
                            onSaved(savedId)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_member_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgundyWine,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Update Member Details" else "Register Member",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
