package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Student
import com.example.data.model.UserRole
import com.example.data.model.ViolationRecord
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShowChart
import com.example.ui.components.AddMasterViolationDialog
import com.example.ui.components.IndustrialTrendChart
import com.example.ui.components.ParentNotificationDialog
import com.example.ui.components.RawTerminalLogViewer
import com.example.ui.components.RecordViolationDialog
import com.example.ui.components.SecondaryAuthChallengeDialog
import com.example.ui.components.StudentDetailSheet
import com.example.ui.components.StudentFormDialog
import com.example.ui.components.TerminalConsole
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.RecapScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.screens.ViolationsScreen
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.Sp3Red
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.DisciplineViewModel
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Beranda", Icons.Default.Dashboard, "tab_dashboard"),
    TELEMETRY("Statistik", Icons.Default.ShowChart, "tab_telemetry"),
    STUDENTS("Siswa", Icons.Default.People, "tab_students"),
    VIOLATIONS("Pelanggaran", Icons.Default.Warning, "tab_violations"),
    RECAP("Rekap", Icons.Default.Assessment, "tab_recap"),
    NOTIFICATIONS("Notifikasi", Icons.Default.Chat, "tab_notifications")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: DisciplineViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    // Nav State
    var selectedTab by remember { mutableStateOf(AppTab.DASHBOARD) }

    // Observed States
    val schoolName by viewModel.schoolName.collectAsStateWithLifecycle()
    val counselorName by viewModel.counselorName.collectAsStateWithLifecycle()
    val currentTeacherPin by viewModel.teacherPin.collectAsStateWithLifecycle()
    val userSession by viewModel.currentUserSession.collectAsStateWithLifecycle()
    val stats by viewModel.schoolStats.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val filteredStudents by viewModel.filteredStudents.collectAsStateWithLifecycle()
    val classNames by viewModel.allClassNames.collectAsStateWithLifecycle()
    val categories by viewModel.allCategories.collectAsStateWithLifecycle()
    val masterViolations by viewModel.allMasterViolations.collectAsStateWithLifecycle()
    val allRecords by viewModel.allViolationRecords.collectAsStateWithLifecycle()
    val filteredRecords by viewModel.filteredViolationRecords.collectAsStateWithLifecycle()
    val classRecapSummaries by viewModel.classRecapSummaries.collectAsStateWithLifecycle()
    val notificationLogs by viewModel.notificationLogs.collectAsStateWithLifecycle()
    val automatedAlerts by viewModel.automatedAlerts.collectAsStateWithLifecycle()
    val terminalLogs by viewModel.terminalLogs.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedClassFilter by viewModel.selectedClassFilter.collectAsStateWithLifecycle()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val selectedRiskFilter by viewModel.selectedRiskFilter.collectAsStateWithLifecycle()
    val selectedDateRange by viewModel.selectedDateRange.collectAsStateWithLifecycle()

    // If not logged in, enforce Login Screen gate
    if (!userSession.isLoggedIn) {
        LoginScreen(
            schoolName = schoolName,
            counselorName = counselorName,
            students = allStudents,
            onLoginTeacher = { name, role, pin ->
                viewModel.loginAsTeacher(name, role, pin)
            },
            onLoginStudent = { name, nis, studentId ->
                viewModel.loginAsStudent(name, nis, studentId)
            }
        )
        return
    }

    // Role-dependent tab list
    val availableTabs = remember(userSession.role) {
        when (userSession.role) {
            UserRole.TEACHER_BK, UserRole.GURU_BK -> AppTab.values().toList()
            UserRole.TEACHER_PIKET, UserRole.GURU -> listOf(
                AppTab.DASHBOARD,
                AppTab.STUDENTS,
                AppTab.VIOLATIONS,
                AppTab.RECAP,
                AppTab.NOTIFICATIONS
            )
            UserRole.STUDENT, UserRole.SISWA -> listOf(
                AppTab.DASHBOARD,
                AppTab.STUDENTS,
                AppTab.VIOLATIONS,
                AppTab.RECAP
            )
        }
    }

    if (selectedTab !in availableTabs) {
        selectedTab = AppTab.DASHBOARD
    }

    // Secondary Authentication Challenge State
    var showSecondaryAuthDialog by remember { mutableStateOf(false) }
    var pendingAuthAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var pendingActionDescription by remember { mutableStateOf("POINT_MUTATION // INPUT_KASUS") }

    fun requestPointMutation(actionDesc: String, action: () -> Unit) {
        if (!userSession.canModifyPoints) return
        pendingActionDescription = actionDesc
        pendingAuthAction = action
        showSecondaryAuthDialog = true
    }

    // Dialog & Sheet States
    var showRecordDialog by remember { mutableStateOf(false) }
    var targetStudentForRecord by remember { mutableStateOf<Student?>(null) }

    var showNotificationDialog by remember { mutableStateOf(false) }
    var targetStudentForNotification by remember { mutableStateOf<Student?>(null) }
    var targetViolationForNotification by remember { mutableStateOf<ViolationRecord?>(null) }

    var showStudentFormDialog by remember { mutableStateOf(false) }
    var studentToEdit by remember { mutableStateOf<Student?>(null) }

    var selectedDetailStudent by remember { mutableStateOf<Student?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showAddMasterDialog by remember { mutableStateOf(false) }
    var showSchoolSettingsDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(PrimaryBlueLight.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = PrimaryBlueLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = schoolName.ifBlank { "Disiplin Siswa" },
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userSession.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(PrimaryBlueLight.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = when (userSession.role) {
                                            UserRole.TEACHER_BK, UserRole.GURU_BK -> "Guru BK"
                                            UserRole.TEACHER_PIKET, UserRole.GURU -> "Guru Piket"
                                            UserRole.STUDENT, UserRole.SISWA -> "Siswa"
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryBlueLight
                                    )
                                }
                            }
                        }
                    }
                },
                actions = {
                    if (userSession.canEditSchoolProfile) {
                        IconButton(
                            onClick = { showSchoolSettingsDialog = true },
                            modifier = Modifier.testTag("btn_school_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan Sekolah",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("btn_logout")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Keluar / Ganti Akun",
                            tint = Sp3Red
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ) {
                availableTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlueLight,
                            selectedTextColor = PrimaryBlueLight,
                            indicatorColor = PrimaryBlueLight.copy(alpha = 0.15f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                AppTab.DASHBOARD -> {
                    DashboardScreen(
                        schoolName = schoolName,
                        counselorName = counselorName,
                        stats = stats,
                        students = allStudents,
                        onRecordViolationClick = {
                            requestPointMutation("Pencatatan Poin Pelanggaran") {
                                targetStudentForRecord = null
                                showRecordDialog = true
                            }
                        },
                        onAddStudentClick = {
                            if (userSession.canManageStudents) {
                                studentToEdit = null
                                showStudentFormDialog = true
                            }
                        },
                        onSelectStudent = { student ->
                            selectedDetailStudent = student
                        },
                        onNotifyStudentParent = { student ->
                            if (userSession.canSendNotifications) {
                                targetStudentForNotification = student
                                targetViolationForNotification = null
                                showNotificationDialog = true
                            }
                        },
                        onNavigateToRecap = {
                            selectedTab = AppTab.RECAP
                        },
                        onNavigateToViolations = {
                            selectedTab = AppTab.VIOLATIONS
                        },
                        canModifyPoints = userSession.canModifyPoints
                    )
                }

                AppTab.TELEMETRY -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IndustrialTrendChart(
                            allRecords = allRecords,
                            allStudents = allStudents
                        )

                        val roleLabel = when (userSession.role) {
                            UserRole.TEACHER_BK, UserRole.GURU_BK -> "GURU_BK"
                            UserRole.TEACHER_PIKET, UserRole.GURU -> "GURU_PIKET"
                            else -> "TEACHER"
                        }
                        TerminalConsole(
                            logs = terminalLogs,
                            onExecuteCommand = { cmd -> viewModel.executeTerminalCommand(cmd) },
                            onClearLogs = { viewModel.clearTerminalLogs() },
                            userRoleLabel = roleLabel,
                            initiallyExpanded = true
                        )

                        RawTerminalLogViewer(
                            records = filteredRecords,
                            automatedAlerts = automatedAlerts,
                            canDeleteRecords = userSession.canDeleteRecords,
                            onDeleteRecord = { record ->
                                requestPointMutation("Hapus Catatan Pelanggaran") {
                                    viewModel.deleteViolationRecord(record)
                                }
                            }
                        )
                    }
                }

                AppTab.STUDENTS -> {
                    StudentsScreen(
                        students = filteredStudents,
                        classNames = classNames,
                        searchQuery = searchQuery,
                        selectedClass = selectedClassFilter,
                        selectedRisk = selectedRiskFilter,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onClassFilterChange = { viewModel.setSelectedClassFilter(it) },
                        onRiskFilterChange = { viewModel.setSelectedRiskFilter(it) },
                        onSelectStudent = { student ->
                            selectedDetailStudent = student
                        },
                        onRecordViolation = { student ->
                            requestPointMutation("Tambah Catatan: ${student.name}") {
                                targetStudentForRecord = student
                                showRecordDialog = true
                            }
                        },
                        onNotifyParent = { student ->
                            if (userSession.canSendNotifications) {
                                targetStudentForNotification = student
                                targetViolationForNotification = null
                                showNotificationDialog = true
                            }
                        },
                        onEditStudent = { student ->
                            if (userSession.canManageStudents) {
                                studentToEdit = student
                                showStudentFormDialog = true
                            }
                        },
                        onAddStudentClick = {
                            if (userSession.canManageStudents) {
                                studentToEdit = null
                                showStudentFormDialog = true
                            }
                        },
                        canModifyPoints = userSession.canModifyPoints,
                        canManageStudents = userSession.canManageStudents
                    )
                }

                AppTab.VIOLATIONS -> {
                    ViolationsScreen(
                        records = filteredRecords,
                        categories = categories,
                        masters = masterViolations,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategoryFilter,
                        selectedDateRange = selectedDateRange,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onCategoryFilterChange = { viewModel.setSelectedCategoryFilter(it) },
                        onDateRangeChange = { viewModel.setSelectedDateRange(it) },
                        onRecordViolationClick = {
                            requestPointMutation("Pencatatan Poin Pelanggaran") {
                                targetStudentForRecord = null
                                showRecordDialog = true
                            }
                        },
                        onNotifyParentForRecord = { record ->
                            if (userSession.canSendNotifications) {
                                val s = allStudents.find { it.id == record.studentId }
                                if (s != null) {
                                    targetStudentForNotification = s
                                    targetViolationForNotification = record
                                    showNotificationDialog = true
                                }
                            }
                        },
                        onDeleteRecord = { record ->
                            requestPointMutation("Hapus Catatan Pelanggaran") {
                                viewModel.deleteViolationRecord(record)
                            }
                        },
                        onAddCustomMasterClick = {
                            if (userSession.canModifyPoints) {
                                showAddMasterDialog = true
                            }
                        },
                        canModifyPoints = userSession.canModifyPoints
                    )
                }

                AppTab.RECAP -> {
                    RecapScreen(
                        schoolName = schoolName,
                        stats = stats,
                        classSummaries = classRecapSummaries,
                        allRecords = allRecords
                    )
                }

                AppTab.NOTIFICATIONS -> {
                    NotificationsScreen(
                        students = allStudents,
                        logs = notificationLogs,
                        onSelectStudentForNotification = { student ->
                            targetStudentForNotification = student
                            targetViolationForNotification = null
                            showNotificationDialog = true
                        }
                    )
                }
            }
        }

        // Interactive Command Input System at the bottom of the screen for teachers with scrollable terminal output area
        if (userSession.canModifyPoints) {
            val roleLabel = when (userSession.role) {
                UserRole.TEACHER_BK, UserRole.GURU_BK -> "GURU_BK"
                UserRole.TEACHER_PIKET, UserRole.GURU -> "GURU_PIKET"
                else -> "TEACHER"
            }
            TerminalConsole(
                logs = terminalLogs,
                onExecuteCommand = { cmd -> viewModel.executeTerminalCommand(cmd) },
                onClearLogs = { viewModel.clearTerminalLogs() },
                userRoleLabel = roleLabel,
                initiallyExpanded = false
            )
        }
    }
}

    // --- DIALOG: Record Violation ---
    if (showRecordDialog) {
        RecordViolationDialog(
            initialStudent = targetStudentForRecord,
            studentsList = allStudents,
            categories = categories,
            masterViolations = masterViolations,
            onDismiss = { showRecordDialog = false },
            onConfirm = { student, violation, customPoints, notes, reporter, notifyParent ->
                viewModel.recordViolation(
                    student = student,
                    master = violation,
                    customPoints = customPoints,
                    notes = notes,
                    reporterTeacher = reporter,
                    notifyParentNow = notifyParent,
                    notificationType = if (notifyParent) "WhatsApp" else ""
                )
                showRecordDialog = false
                if (notifyParent) {
                    targetStudentForNotification = student
                    targetViolationForNotification = null
                    showNotificationDialog = true
                }
            }
        )
    }

    // --- DIALOG: Parent Notification ---
    if (showNotificationDialog && targetStudentForNotification != null) {
        ParentNotificationDialog(
            student = targetStudentForNotification!!,
            violationRecord = targetViolationForNotification,
            schoolName = schoolName,
            onDismiss = {
                showNotificationDialog = false
                targetStudentForNotification = null
                targetViolationForNotification = null
            },
            onSendNotification = { channel, letterType, message ->
                viewModel.logParentNotification(
                    student = targetStudentForNotification!!,
                    channel = channel,
                    letterType = letterType,
                    messageContent = message
                )
                targetViolationForNotification?.let {
                    viewModel.markParentNotified(it.id, channel)
                }
                showNotificationDialog = false
                targetStudentForNotification = null
                targetViolationForNotification = null
            }
        )
    }

    // --- DIALOG: Student Form (Add / Edit) ---
    if (showStudentFormDialog) {
        StudentFormDialog(
            studentToEdit = studentToEdit,
            onDismiss = { showStudentFormDialog = false },
            onSave = { nis, nisn, name, className, gender, parentName, parentPhone, parentAddress, notes ->
                if (studentToEdit != null) {
                    viewModel.updateStudent(
                        studentToEdit!!.copy(
                            nis = nis,
                            nisn = nisn,
                            name = name,
                            className = className,
                            gender = gender,
                            parentName = parentName,
                            parentPhone = parentPhone,
                            parentAddress = parentAddress,
                            notes = notes
                        )
                    )
                } else {
                    viewModel.addStudent(
                        nis = nis,
                        nisn = nisn,
                        name = name,
                        className = className,
                        gender = gender,
                        parentName = parentName,
                        parentPhone = parentPhone,
                        parentAddress = parentAddress,
                        notes = notes
                    )
                }
                showStudentFormDialog = false
            }
        )
    }

    // --- BOTTOM SHEET: Student Detail Dossier ---
    if (selectedDetailStudent != null) {
        val currentDetailStudent = allStudents.find { it.id == selectedDetailStudent?.id } ?: selectedDetailStudent!!
        StudentDetailSheet(
            student = currentDetailStudent,
            records = allRecords,
            schoolName = schoolName,
            sheetState = detailSheetState,
            onDismiss = { selectedDetailStudent = null },
            onRecordViolation = {
                requestPointMutation("Tambah Catatan: ${currentDetailStudent.name}") {
                    targetStudentForRecord = currentDetailStudent
                    showRecordDialog = true
                }
            },
            onNotifyParent = {
                if (userSession.canSendNotifications) {
                    targetStudentForNotification = currentDetailStudent
                    targetViolationForNotification = null
                    showNotificationDialog = true
                }
            },
            onEditStudent = {
                if (userSession.canManageStudents) {
                    studentToEdit = currentDetailStudent
                    showStudentFormDialog = true
                }
            },
            onDeleteRecord = { rec ->
                requestPointMutation("Hapus Catatan Pelanggaran") {
                    viewModel.deleteViolationRecord(rec)
                }
            },
            canModifyPoints = userSession.canModifyPoints
        )
    }

    // --- DIALOG: Add Custom Master Violation ---
    if (showAddMasterDialog) {
        AddMasterViolationDialog(
            categories = categories,
            onDismiss = { showAddMasterDialog = false },
            onSave = { catId, catName, code, title, points, sanction ->
                viewModel.addCustomViolationMaster(catId, catName, code, title, points, sanction)
                showAddMasterDialog = false
            }
        )
    }

    // --- DIALOG: School Profile Settings ---
    if (showSchoolSettingsDialog) {
        var tempSchoolName by remember { mutableStateOf(schoolName) }
        var tempCounselorName by remember { mutableStateOf(counselorName) }
        var newPinInput by remember { mutableStateOf("") }
        var pinErrorMessage by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showSchoolSettingsDialog = false },
            title = { Text("Pengaturan Profil & Keamanan PIN", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempSchoolName,
                        onValueChange = { tempSchoolName = it },
                        label = { Text("Nama Sekolah") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempCounselorName,
                        onValueChange = { tempCounselorName = it },
                        label = { Text("Nama Guru BK / Kesiswaan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Keamanan Otorisasi Guru/BK",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = {
                            newPinInput = it
                            pinErrorMessage = null
                        },
                        label = { Text("Ganti PIN Baru (Opsional)") },
                        placeholder = { Text("Kosongkan jika tidak diubah") },
                        isError = pinErrorMessage != null,
                        supportingText = {
                            if (pinErrorMessage != null) {
                                Text(text = pinErrorMessage!!, color = Color(0xFFEF4444))
                            } else {
                                Text(text = "Minimal 4 karakter angka/huruf rahasia", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Pemeliharaan & Reset Data",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                    Text(
                        text = "Bersihkan seluruh data siswa & riwayat pelanggaran agar aplikasi kembali bersih seperti baru.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_reset_all_data")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bersihkan Semua Data", fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPinInput.isNotBlank()) {
                            if (newPinInput.trim().length < 4) {
                                pinErrorMessage = "PIN baru harus minimal 4 karakter"
                                return@Button
                            }
                            viewModel.updateTeacherPin(newPinInput.trim())
                        }
                        viewModel.updateSchoolProfile(tempSchoolName, tempCounselorName)
                        showSchoolSettingsDialog = false
                    }
                ) {
                    Text("Simpan Perubahan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSchoolSettingsDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // --- DIALOG: Reset Data Confirmation ---
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text("Bersihkan Semua Data?", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
            },
            text = {
                Text("Seluruh data siswa, riwayat kasus pelanggaran, dan catatan notifikasi akan dihapus permanen agar aplikasi kembali bersih seperti baru (0 siswa, 0 kasus).\n\nSistem otorisasi Guru BK dan aturan 37 pelanggaran standar tetap aman dan dapat langsung digunakan.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllSchoolData()
                        showResetConfirmDialog = false
                        showSchoolSettingsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
                ) {
                    Text("Ya, Bersihkan Sekarang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // --- DIALOG: Secondary Authentication Challenge (PIN or Biometric) ---
    if (showSecondaryAuthDialog) {
        SecondaryAuthChallengeDialog(
            targetActionName = pendingActionDescription,
            configuredTeacherPin = currentTeacherPin,
            onVerified = {
                showSecondaryAuthDialog = false
                val action = pendingAuthAction
                pendingAuthAction = null
                action?.invoke()
            },
            onDismiss = {
                showSecondaryAuthDialog = false
                pendingAuthAction = null
            }
        )
    }
}
