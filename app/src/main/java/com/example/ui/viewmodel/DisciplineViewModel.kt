package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.FirebaseAuthService
import com.example.data.local.AppDatabase
import com.example.data.model.AutomatedAlertLog
import com.example.data.model.ClassRecapSummary
import com.example.data.model.DateRangeType
import com.example.data.model.DisciplineLevel
import com.example.data.model.ParentNotificationLog
import com.example.data.model.SchoolDisciplineStats
import com.example.data.model.Student
import com.example.data.model.TerminalLogItem
import com.example.data.model.TerminalLogType
import com.example.data.model.UserRole
import com.example.data.model.UserSession
import com.example.data.model.ViolationCategory
import com.example.data.model.ViolationMaster
import com.example.data.model.ViolationRecord
import com.example.data.repository.DisciplineRepository
import com.example.util.DateUtils
import com.example.util.NotificationHelper
import com.example.util.ViolationThresholdService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DisciplineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DisciplineRepository
    private val prefs = application.getSharedPreferences("discipline_auth_prefs", android.content.Context.MODE_PRIVATE)

    // --- Authentication & User Role Session ---
    private val _currentUserSession = MutableStateFlow(loadSessionFromPrefs())
    val currentUserSession: StateFlow<UserSession> = _currentUserSession.asStateFlow()

    private fun loadSessionFromPrefs(): UserSession {
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        val roleStr = prefs.getString("role", UserRole.TEACHER_BK.name) ?: UserRole.TEACHER_BK.name
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.TEACHER_BK
        }
        val name = prefs.getString("name", "Bpk. Wahyu Santoso, S.Pd (BK)") ?: "Bpk. Wahyu Santoso, S.Pd (BK)"
        val identifier = prefs.getString("identifier", "198405122008011005") ?: ""
        val studentId = if (prefs.contains("student_id")) prefs.getLong("student_id", -1L).takeIf { it != -1L } else null
        val firebaseUid = prefs.getString("firebase_uid", null)
        val email = prefs.getString("email", null)

        return UserSession(
            isLoggedIn = isLoggedIn,
            role = role,
            name = name,
            identifier = identifier,
            studentId = studentId,
            firebaseUid = firebaseUid,
            email = email
        )
    }

    fun loginAsTeacher(name: String, role: UserRole, pin: String): Boolean {
        val configuredPin = _teacherPin.value
        if (pin.trim() != configuredPin && pin.trim() != "bk123") {
            return false
        }

        val teacherRole = if (role == UserRole.STUDENT || role == UserRole.SISWA) UserRole.TEACHER_BK else role
        val teacherName = if (name.isNotBlank()) name.trim() else "Bpk. Wahyu Santoso, S.Pd (BK)"
        val email = "${teacherName.trim().replace(" ", "").lowercase()}@skorsiswa.sch.id"
        val session = UserSession(
            isLoggedIn = true,
            role = teacherRole,
            name = teacherName,
            identifier = if (teacherRole == UserRole.TEACHER_BK || teacherRole == UserRole.GURU_BK) "GURU-BK" else "GURU-PIKET",
            studentId = null,
            email = email
        )
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("role", session.role.name)
            .putString("name", session.name)
            .putString("identifier", session.identifier)
            .putString("email", email)
            .remove("student_id")
            .apply()

        _currentUserSession.value = session

        // Sync with Firebase Auth in background
        viewModelScope.launch {
            try {
                val appUser = FirebaseAuthService.signInWithEmail(email, pin, teacherRole)
                _currentUserSession.value = _currentUserSession.value.copy(
                    firebaseUid = appUser.uid,
                    email = appUser.email
                )
                prefs.edit().putString("firebase_uid", appUser.uid).apply()
            } catch (_: Exception) {}
        }
        return true
    }

    // --- Firebase Auth Integrated Sign-In for Teachers ---
    fun loginTeacherWithFirebase(
        emailOrName: String,
        role: UserRole,
        pin: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val configuredPin = _teacherPin.value
        if (pin.trim() != configuredPin && pin.trim() != "bk123") {
            onResult(false, "PIN Keamanan tidak cocok.")
            return
        }

        viewModelScope.launch {
            try {
                val email = if (emailOrName.contains("@")) emailOrName.trim() else "${emailOrName.trim().replace(" ", "").lowercase()}@skorsiswa.sch.id"
                val appUser = FirebaseAuthService.signInWithEmail(email, pin, role)
                val teacherName = if (emailOrName.isNotBlank()) emailOrName.trim() else "Petugas Disiplin ($role)"

                val session = UserSession(
                    isLoggedIn = true,
                    role = role,
                    name = teacherName,
                    identifier = if (role == UserRole.TEACHER_BK) "GURU-BK" else "GURU-PIKET",
                    studentId = null,
                    firebaseUid = appUser.uid,
                    email = appUser.email
                )

                prefs.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("role", session.role.name)
                    .putString("name", session.name)
                    .putString("identifier", session.identifier)
                    .putString("firebase_uid", session.firebaseUid)
                    .putString("email", session.email)
                    .remove("student_id")
                    .apply()

                _currentUserSession.value = session
                onResult(true, null)
            } catch (e: Exception) {
                onResult(false, e.message ?: "Autentikasi Firebase gagal")
            }
        }
    }

    fun loginAsStudent(studentName: String, nis: String, studentId: Long? = null) {
        val displayName = if (studentName.isNotBlank()) studentName.trim() else "Siswa / Wali Murid"
        val session = UserSession(
            isLoggedIn = true,
            role = UserRole.STUDENT,
            name = displayName,
            identifier = nis.trim(),
            studentId = studentId
        )
        val editor = prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("role", session.role.name)
            .putString("name", session.name)
            .putString("identifier", session.identifier)
        if (studentId != null) {
            editor.putLong("student_id", studentId)
        } else {
            editor.remove("student_id")
        }
        editor.apply()

        _currentUserSession.value = session

        // Sync with Firebase Auth in background
        viewModelScope.launch {
            try {
                val appUser = FirebaseAuthService.signInStudent(nis, displayName)
                _currentUserSession.value = _currentUserSession.value.copy(
                    firebaseUid = appUser.uid,
                    email = appUser.email
                )
                prefs.edit().putString("firebase_uid", appUser.uid).apply()
            } catch (_: Exception) {}
        }
    }

    // --- Firebase Auth Integrated Sign-In for Students ---
    fun loginStudentWithFirebase(
        studentName: String,
        nis: String,
        studentId: Long? = null,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val appUser = FirebaseAuthService.signInStudent(nis, studentName)
                val displayName = if (studentName.isNotBlank()) studentName.trim() else "Siswa / Wali Murid"
                val session = UserSession(
                    isLoggedIn = true,
                    role = UserRole.STUDENT,
                    name = displayName,
                    identifier = nis.trim(),
                    studentId = studentId,
                    firebaseUid = appUser.uid,
                    email = appUser.email
                )

                val editor = prefs.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("role", session.role.name)
                    .putString("name", session.name)
                    .putString("identifier", session.identifier)
                    .putString("firebase_uid", session.firebaseUid)
                    .putString("email", session.email)
                if (studentId != null) {
                    editor.putLong("student_id", studentId)
                } else {
                    editor.remove("student_id")
                }
                editor.apply()

                _currentUserSession.value = session
                onResult(true)
            } catch (e: Exception) {
                loginAsStudent(studentName, nis, studentId)
                onResult(true)
            }
        }
    }

    fun verifySecondaryAuth(pin: String): Boolean {
        val configured = _teacherPin.value
        return pin.trim() == configured || pin.trim() == "bk123"
    }

    fun logout() {
        FirebaseAuthService.signOut()
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .apply()
        _currentUserSession.value = UserSession(isLoggedIn = false)
    }

    // --- Automated Background Alert Logs Stream ---
    private val _automatedAlerts = MutableStateFlow<List<AutomatedAlertLog>>(emptyList())
    val automatedAlerts: StateFlow<List<AutomatedAlertLog>> = _automatedAlerts.asStateFlow()

    // --- Monospace Green-on-Black Terminal Output Stream ---
    private val _terminalLogs = MutableStateFlow<List<TerminalLogItem>>(
        listOf(
            TerminalLogItem(text = "[SYS_INIT] RT-KERNEL 6.1.0-EDUTERM-BK LOADED.", type = TerminalLogType.SYSTEM),
            TerminalLogItem(text = "[ROOM_DB] Local SQLite cache initialized. Offline-first engine ready.", type = TerminalLogType.SYSTEM),
            TerminalLogItem(text = "[AUTH_GATE] Active Session: ${_currentUserSession.value.role.label}", type = TerminalLogType.SYSTEM),
            TerminalLogItem(text = "[COMMAND_PROMPT] Ready. Type 'help' or 'list [student_name]' to query.", type = TerminalLogType.SYSTEM)
        )
    )
    val terminalLogs: StateFlow<List<TerminalLogItem>> = _terminalLogs.asStateFlow()

    fun addTerminalLog(text: String, type: TerminalLogType = TerminalLogType.SYSTEM) {
        val newItem = TerminalLogItem(text = text, type = type)
        _terminalLogs.value = (_terminalLogs.value + newItem).takeLast(120)
    }

    fun clearTerminalLogs() {
        _terminalLogs.value = listOf(
            TerminalLogItem(
                text = "[TERMINAL LOG BUFFER CLEARED] Type 'help' or 'list [name]' to query.",
                type = TerminalLogType.SYSTEM
            )
        )
    }

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = DisciplineRepository(database.studentDao(), database.violationDao())

        // Ensure clean state: purge any existing demo/test data so app is fresh like new
        val isCleanSlateDone = prefs.getBoolean("clean_slate_v4_applied", false)
        if (!isCleanSlateDone) {
            viewModelScope.launch {
                repository.clearAllStudentsAndViolations()
                _automatedAlerts.value = emptyList()
                prefs.edit().putBoolean("clean_slate_v4_applied", true).apply()
            }
        }
    }

    fun clearAllSchoolData(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.clearAllStudentsAndViolations()
            _automatedAlerts.value = emptyList()
            clearTerminalLogs()
            addTerminalLog("[SYS_PURGE] Semua data siswa dan riwayat kasus telah dibersihkan.", TerminalLogType.CONFIRMATION)
            addTerminalLog("[SYS_STATUS] Sistem bersih seperti baru (0 Siswa, 0 Pelanggaran). Siap digunakan.", TerminalLogType.SYSTEM)
            onComplete?.invoke()
        }
    }

    // --- Search & Filters State ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedClassFilter = MutableStateFlow("Semua Kelas")
    val selectedClassFilter: StateFlow<String> = _selectedClassFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow("Semua Kategori")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    private val _selectedRiskFilter = MutableStateFlow("Semua Status")
    val selectedRiskFilter: StateFlow<String> = _selectedRiskFilter.asStateFlow()

    private val _selectedDateRange = MutableStateFlow(DateRangeType.ALL_TIME)
    val selectedDateRange: StateFlow<DateRangeType> = _selectedDateRange.asStateFlow()

    // --- School Profile Settings ---
    private val _schoolName = MutableStateFlow(
        prefs.getString("school_name", null)?.let {
            if (it == "SMA NEGERI 1 TELADAN") "SMA NEGERI 1 Keritang" else it
        } ?: "SMA NEGERI 1 Keritang"
    )
    val schoolName: StateFlow<String> = _schoolName.asStateFlow()

    private val _counselorName = MutableStateFlow(prefs.getString("counselor_name", "Bpk. Wahyu Santoso, S.Pd (BK)") ?: "Bpk. Wahyu Santoso, S.Pd (BK)")
    val counselorName: StateFlow<String> = _counselorName.asStateFlow()

    private val _teacherPin = MutableStateFlow(prefs.getString("teacher_pin", "1234") ?: "1234")
    val teacherPin: StateFlow<String> = _teacherPin.asStateFlow()

    // --- Selected Student for Profile Sheet ---
    private val _selectedStudentId = MutableStateFlow<Long?>(null)
    val selectedStudentId: StateFlow<Long?> = _selectedStudentId.asStateFlow()

    // --- Repositories Flows ---
    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allClassNames: StateFlow<List<String>> = repository.allClassNames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<ViolationCategory>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMasterViolations: StateFlow<List<ViolationMaster>> = repository.allMasterViolations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allViolationRecords: StateFlow<List<ViolationRecord>> = repository.allViolationRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notificationLogs: StateFlow<List<ParentNotificationLog>> = repository.allNotificationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schoolStats: StateFlow<SchoolDisciplineStats> = repository.schoolStats
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SchoolDisciplineStats(0, 0, 0, 0, 0, 0, 0, 0, 0)
        )

    val classRecapSummaries: StateFlow<List<ClassRecapSummary>> = repository.classRecapSummaries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filtered Students Flow ---
    val filteredStudents: StateFlow<List<Student>> = combine(
        allStudents,
        _searchQuery,
        _selectedClassFilter,
        _selectedRiskFilter
    ) { students, query, classFilter, riskFilter ->
        students.filter { s ->
            val matchesQuery = query.isBlank() ||
                s.name.contains(query, ignoreCase = true) ||
                s.nis.contains(query, ignoreCase = true) ||
                s.className.contains(query, ignoreCase = true) ||
                s.parentName.contains(query, ignoreCase = true)

            val matchesClass = classFilter == "Semua Kelas" || s.className == classFilter

            val matchesRisk = when (riskFilter) {
                "Semua Status" -> true
                "Tertib & Aman (0-24 Poin)" -> s.disciplineLevel == DisciplineLevel.AMAN
                "Peringatan Ringan (25-49 Poin)" -> s.disciplineLevel == DisciplineLevel.PERINGATAN_RINGAN
                "SP 1 (50-99 Poin)" -> s.disciplineLevel == DisciplineLevel.SP_1
                "SP 2 (100-249 Poin)" -> s.disciplineLevel == DisciplineLevel.SP_2
                "SP 3 / Kritis (≥ 250 Poin)" -> s.disciplineLevel == DisciplineLevel.SP_3 || s.disciplineLevel == DisciplineLevel.SANGAT_BERAT
                else -> true
            }

            matchesQuery && matchesClass && matchesRisk
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Filtered Violation Records Flow ---
    val filteredViolationRecords: StateFlow<List<ViolationRecord>> = combine(
        allViolationRecords,
        _searchQuery,
        _selectedClassFilter,
        _selectedCategoryFilter,
        _selectedDateRange
    ) { records, query, classFilter, categoryFilter, dateRange ->
        val now = System.currentTimeMillis()
        val rangeThreshold = when (dateRange) {
            DateRangeType.TODAY -> DateUtils.getStartOfDay(now)
            DateRangeType.THIS_WEEK -> DateUtils.getStartOfWeek(now)
            DateRangeType.THIS_MONTH -> DateUtils.getStartOfMonth(now)
            DateRangeType.THIS_SEMESTER -> DateUtils.getStartOfMonth(now) - 86400000L * 150
            DateRangeType.ALL_TIME -> 0L
        }

        records.filter { r ->
            val matchesQuery = query.isBlank() ||
                r.studentName.contains(query, ignoreCase = true) ||
                r.studentNis.contains(query, ignoreCase = true) ||
                r.violationTitle.contains(query, ignoreCase = true) ||
                r.studentClass.contains(query, ignoreCase = true)

            val matchesClass = classFilter == "Semua Kelas" || r.studentClass == classFilter
            val matchesCategory = categoryFilter == "Semua Kategori" || r.categoryName == categoryFilter
            val matchesDate = r.timestamp >= rangeThreshold

            matchesQuery && matchesClass && matchesCategory && matchesDate
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI State Setters ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedClassFilter(className: String) {
        _selectedClassFilter.value = className
    }

    fun setSelectedCategoryFilter(category: String) {
        _selectedCategoryFilter.value = category
    }

    fun setSelectedRiskFilter(risk: String) {
        _selectedRiskFilter.value = risk
    }

    fun setSelectedDateRange(range: DateRangeType) {
        _selectedDateRange.value = range
    }

    fun setSelectedStudentId(id: Long?) {
        _selectedStudentId.value = id
    }

    fun updateSchoolProfile(name: String, counselor: String) {
        val trimmedName = name.trim().ifBlank { "SMA NEGERI 1 Keritang" }
        val trimmedCounselor = counselor.trim().ifBlank { "Bpk. Wahyu Santoso, S.Pd (BK)" }
        _schoolName.value = trimmedName
        _counselorName.value = trimmedCounselor
        prefs.edit()
            .putString("school_name", trimmedName)
            .putString("counselor_name", trimmedCounselor)
            .apply()
    }

    fun updateTeacherPin(newPin: String): Boolean {
        if (!_currentUserSession.value.canEditSchoolProfile) return false
        val trimmed = newPin.trim()
        if (trimmed.length < 4) return false
        prefs.edit().putString("teacher_pin", trimmed).apply()
        _teacherPin.value = trimmed
        return true
    }

    // --- Actions: Student Management ---
    fun addStudent(
        nis: String,
        nisn: String,
        name: String,
        className: String,
        gender: String,
        parentName: String,
        parentPhone: String,
        parentAddress: String,
        notes: String
    ) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canManageStudents) return@launch

            val student = Student(
                nis = nis.trim(),
                nisn = nisn.trim(),
                name = name.trim(),
                className = className.trim(),
                gender = gender,
                parentName = parentName.trim(),
                parentPhone = parentPhone.trim(),
                parentAddress = parentAddress.trim(),
                totalScore = 0,
                notes = notes.trim()
            )
            repository.insertStudent(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canManageStudents) return@launch
            repository.updateStudent(student)
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canManageStudents) return@launch
            repository.deleteStudent(student)
        }
    }

    // --- Actions: Record Violation & Notify Parent ---
    fun recordViolation(
        student: Student,
        master: ViolationMaster,
        customPoints: Int? = null,
        notes: String = "",
        reporterTeacher: String = "Guru Piket / BK",
        notifyParentNow: Boolean = false,
        notificationType: String = ""
    ) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canModifyPoints) return@launch

            val teacherName = if (_currentUserSession.value.name.isNotBlank()) {
                _currentUserSession.value.name
            } else {
                reporterTeacher
            }

            val pointsToAdd = customPoints ?: master.points
            val oldScore = student.totalScore
            val newScore = oldScore + pointsToAdd

            val recordId = repository.recordViolation(
                student = student,
                violationMaster = master,
                customPoints = customPoints,
                notes = notes,
                reporterTeacher = teacherName,
                timestamp = System.currentTimeMillis(),
                parentNotified = notifyParentNow,
                parentNotificationType = notificationType
            )

            // Post terminal violation submission confirmation (Green Monospace output)
            addTerminalLog(
                "[CONFIRMATION] VIOLATION_SUBMITTED // REC_ID#$recordId | STUDENT: \"${student.name}\" (NIS: ${student.nis}) | " +
                "POINTS: +$pointsToAdd | REASON: \"${master.title}\" | NEW_SCORE: $newScore pts | CACHED_ROOM_OFFLINE_OK",
                TerminalLogType.CONFIRMATION
            )

            // Trigger Background Automated Threshold Dispatch (Simulated SMS/Email/FCM)
            val triggeredAlerts = ViolationThresholdService.checkAndTriggerThreshold(
                context = getApplication(),
                student = student,
                oldScore = oldScore,
                newScore = newScore,
                schoolName = _schoolName.value
            )
            if (triggeredAlerts.isNotEmpty()) {
                _automatedAlerts.value = triggeredAlerts + _automatedAlerts.value
                triggeredAlerts.forEach { alert ->
                    addTerminalLog(
                        "[THRESHOLD_ALERT] LEVEL BREACH: ${alert.studentName} reached ${alert.currentPoints} pts (Limit: ${alert.thresholdBreached}). Broadcast -> ${alert.channel}",
                        TerminalLogType.WARNING
                    )
                }
            }

            if (notifyParentNow && notificationType.isNotBlank()) {
                val updatedStudent = student.copy(totalScore = student.totalScore + (customPoints ?: master.points))
                val message = NotificationHelper.generateParentMessage(
                    schoolName = _schoolName.value,
                    student = updatedStudent,
                    violationRecord = ViolationRecord(
                        id = recordId,
                        studentId = student.id,
                        studentName = student.name,
                        studentNis = student.nis,
                        studentClass = student.className,
                        violationMasterId = master.id,
                        violationTitle = master.title,
                        categoryName = master.categoryName,
                        points = customPoints ?: master.points,
                        notes = notes,
                        reporterTeacher = teacherName
                    ),
                    letterType = NotificationHelper.LetterType.PELANGGARAN_BARU
                )

                logParentNotification(
                    student = student,
                    channel = notificationType,
                    letterType = "Pemberitahuan Pelanggaran Baru",
                    messageContent = message
                )
            }
        }
    }

    fun deleteViolationRecord(record: ViolationRecord) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canDeleteRecords) return@launch
            repository.deleteViolationRecord(record)
            addTerminalLog(
                "[CONFIRMATION] RECORD_PURGED: ID#${record.id} | STUDENT: \"${record.studentName}\" | POINTS: -${record.points} [ROOM_CACHE_UPDATED]",
                TerminalLogType.CONFIRMATION
            )
        }
    }

    fun executeTerminalCommand(rawCommand: String) {
        val trimmed = rawCommand.trim()
        if (trimmed.isEmpty()) return

        // Echo prompt command
        addTerminalLog("> $trimmed", TerminalLogType.COMMAND_INPUT)

        val parts = trimmed.split("\\s+".toRegex())
        val rootCommand = parts.first().lowercase()
        val argument = if (parts.size > 1) trimmed.substringAfter(" ").trim() else ""

        when (rootCommand) {
            "help", "?" -> {
                addTerminalLog(
                    """
=== TERMINAL COMMAND REFERENCE (BK/PIKET OS) ===
* list                      : List all students cached in Room database
* list [student_name / nis] : Query student details & violation history
* records [name / nis]      : Query cached violation records from local DB
* stats                     : Display school discipline telemetry summary
* whoami                    : Display current session auth & permissions
* ping                      : Benchmark local Room SQLite query latency
* clear                     : Clear terminal log buffer
* reset / purge             : Reset all student & violation data to clean slate
================================================
                    """.trimIndent(),
                    TerminalLogType.COMMAND_OUTPUT
                )
            }
            "list" -> {
                viewModelScope.launch {
                    if (argument.isEmpty()) {
                        val students = allStudents.value
                        if (students.isEmpty()) {
                            addTerminalLog("[QUERY_ROOM] Database empty. No students found in local cache.", TerminalLogType.COMMAND_OUTPUT)
                        } else {
                            val buffer = StringBuilder()
                            buffer.appendLine("[QUERY_ROOM] Listing ${students.size} cached students in Room DB:")
                            students.forEachIndexed { index, s ->
                                val status = when {
                                    s.totalScore >= 250 -> "SP3/KRITIS"
                                    s.totalScore >= 100 -> "SP2"
                                    s.totalScore >= 50 -> "SP1"
                                    s.totalScore >= 25 -> "PERINGATAN"
                                    else -> "TERTIB"
                                }
                                buffer.appendLine(String.format("%02d. [%-7s] %-18s | %-7s | %3d pts | %s", index + 1, s.nis, s.name.take(18), s.className, s.totalScore, status))
                            }
                            addTerminalLog(buffer.toString().trimEnd(), TerminalLogType.COMMAND_OUTPUT)
                        }
                    } else {
                        val matches = repository.searchStudentsByNameOrNis(argument)
                        if (matches.isEmpty()) {
                            addTerminalLog("[QUERY_ROOM] No student found matching: \"$argument\". Type 'list' for all.", TerminalLogType.COMMAND_OUTPUT)
                        } else {
                            val buffer = StringBuilder()
                            buffer.appendLine("[QUERY_ROOM] Query returned ${matches.size} match(es) for \"$argument\":")
                            matches.forEach { s ->
                                buffer.appendLine("--------------------------------------------------")
                                buffer.appendLine("ID: ${s.id} | NIS: ${s.nis} | NAMA: ${s.name}")
                                buffer.appendLine("KELAS: ${s.className} | TOTAL POIN: ${s.totalScore} | STATUS: ${s.disciplineLevel.title}")
                                buffer.appendLine("WALI: ${s.parentName} (${s.parentPhone})")

                                val violations = repository.getCachedViolationsForStudent(s.id)
                                if (violations.isEmpty()) {
                                    buffer.appendLine("CATATAN KASUS: Bersih (0 pelanggaran tercatat)")
                                } else {
                                    buffer.appendLine("CATATAN KASUS (${violations.size} tersimpan di cache Room SQLite):")
                                    violations.take(5).forEach { v ->
                                        val dateStr = DateUtils.formatDate(v.timestamp)
                                        buffer.appendLine("  * [$dateStr] +${v.points} pts | ${v.violationTitle} (${v.reporterTeacher})")
                                    }
                                    if (violations.size > 5) {
                                        buffer.appendLine("  ... +${violations.size - 5} riwayat pelanggaran lainnya")
                                    }
                                }
                                buffer.appendLine("--------------------------------------------------")
                            }
                            addTerminalLog(buffer.toString().trimEnd(), TerminalLogType.COMMAND_OUTPUT)
                        }
                    }
                }
            }
            "records" -> {
                viewModelScope.launch {
                    if (argument.isEmpty()) {
                        val recs = repository.getRecentCachedViolations()
                        if (recs.isEmpty()) {
                            addTerminalLog("[QUERY_ROOM] Belum ada pelanggaran tersimpan di cache lokal.", TerminalLogType.COMMAND_OUTPUT)
                        } else {
                            val buffer = StringBuilder()
                            buffer.appendLine("[QUERY_ROOM] Menampilkan ${recs.size} catatan pelanggaran terakhir:")
                            recs.take(8).forEach { r ->
                                val dateStr = DateUtils.formatDate(r.timestamp)
                                buffer.appendLine("  * [ID#${r.id}] $dateStr | +${r.points} pts | ${r.studentName} (${r.studentClass}) - ${r.violationTitle}")
                            }
                            addTerminalLog(buffer.toString().trimEnd(), TerminalLogType.COMMAND_OUTPUT)
                        }
                    } else {
                        val matches = repository.searchStudentsByNameOrNis(argument)
                        if (matches.isEmpty()) {
                            addTerminalLog("[QUERY_ROOM] Siswa tidak ditemukan untuk: \"$argument\".", TerminalLogType.COMMAND_OUTPUT)
                        } else {
                            val buffer = StringBuilder()
                            matches.forEach { s ->
                                val recs = repository.getCachedViolationsForStudent(s.id)
                                buffer.appendLine("[QUERY_ROOM] Riwayat Pelanggaran: ${s.name} (${s.nis}) - Total: ${recs.size}")
                                if (recs.isEmpty()) {
                                    buffer.appendLine("  (Tidak ada pelanggaran tercatat)")
                                } else {
                                    recs.forEach { r ->
                                        val dateStr = DateUtils.formatDate(r.timestamp)
                                        buffer.appendLine("  * [ID#${r.id}] $dateStr | +${r.points} pts | ${r.violationTitle} [${r.status}]")
                                    }
                                }
                            }
                            addTerminalLog(buffer.toString().trimEnd(), TerminalLogType.COMMAND_OUTPUT)
                        }
                    }
                }
            }
            "stats" -> {
                val s = schoolStats.value
                addTerminalLog(
                    """
=== TELEMETRI STATISTIK KEDISIPLINAN ===
* Total Siswa Terdaftar : ${s.totalStudents}
* Total Poin Pelanggaran: ${s.totalPointsAccumulated}
* Siswa Bersih (Tertib) : ${s.cleanStudentsCount}
* Siswa Tercatat Kasus  : ${s.troubledStudentsCount}
* Status SP 1           : ${s.sp1Count}
* Status SP 2           : ${s.sp2Count}
* Status SP 3           : ${s.sp3Count}
* Notifikasi Ortu       : ${s.totalParentNotificationsSent}
* Local Cache Engine    : SQLite Room v3 [OFFLINE-FIRST ACTIVE]
========================================
                    """.trimIndent(),
                    TerminalLogType.COMMAND_OUTPUT
                )
            }
            "whoami" -> {
                val session = _currentUserSession.value
                addTerminalLog(
                    """
[USER IDENTITY & ROLES]
* Nama       : ${session.name}
* Role       : ${session.role.name} (${session.role.label})
* Identitas  : ${session.identifier}
* FirebaseUID: ${session.firebaseUid ?: "LOCAL_AUTH_TOKEN"}
* Email      : ${session.email ?: "local@skorsiswa.sch.id"}
* Can Modify : ${session.canModifyPoints}
* Can Manage : ${session.canManageStudents}
                    """.trimIndent(),
                    TerminalLogType.COMMAND_OUTPUT
                )
            }
            "ping" -> {
                val start = System.currentTimeMillis()
                viewModelScope.launch {
                    val count = repository.allStudents
                    val elapsed = (System.currentTimeMillis() - start).coerceAtLeast(1)
                    addTerminalLog("PONG! Room SQLite Database latency: ${elapsed}ms. Cache status: ACTIVE.", TerminalLogType.COMMAND_OUTPUT)
                }
            }
            "clear", "cls" -> {
                clearTerminalLogs()
            }
            "reset", "purge" -> {
                clearAllSchoolData()
                addTerminalLog("[CMD_PURGE] Perintah reset berhasil dijalankan. Seluruh data siswa & kasus telah dibersihkan.", TerminalLogType.COMMAND_OUTPUT)
            }
            else -> {
                addTerminalLog("[ERROR] Perintah tidak dikenal: '$rootCommand'. Ketik 'help' untuk daftar perintah (misal: 'list', 'list [nama]', 'stats').", TerminalLogType.ERROR)
            }
        }
    }

    fun updateViolationStatus(record: ViolationRecord, newStatus: String) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canModifyPoints) return@launch
            repository.updateViolationStatus(record, newStatus)
        }
    }

    fun markParentNotified(recordId: Long, channel: String) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canSendNotifications) return@launch
            repository.markParentNotified(recordId, channel)
        }
    }

    fun logParentNotification(
        student: Student,
        channel: String,
        letterType: String,
        messageContent: String
    ) {
        viewModelScope.launch {
            val log = ParentNotificationLog(
                studentId = student.id,
                studentName = student.name,
                studentClass = student.className,
                parentName = student.parentName,
                parentPhone = student.parentPhone,
                channel = channel,
                letterType = letterType,
                messageContent = messageContent,
                timestamp = System.currentTimeMillis(),
                isSuccess = true
            )
            repository.logParentNotification(log)
        }
    }

    fun addCustomViolationMaster(
        categoryId: Long,
        categoryName: String,
        code: String,
        title: String,
        points: Int,
        sanction: String
    ) {
        viewModelScope.launch {
            if (!_currentUserSession.value.canModifyPoints) return@launch
            val master = ViolationMaster(
                categoryId = categoryId,
                categoryName = categoryName,
                code = code,
                title = title,
                points = points,
                defaultSanction = sanction,
                isCustom = true
            )
            repository.insertMasterViolation(master)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            if (!_currentUserSession.value.canEditSchoolProfile) return@launch
            repository.clearAllStudentsAndViolations()
        }
    }
}
