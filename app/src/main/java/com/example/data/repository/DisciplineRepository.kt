package com.example.data.repository

import com.example.data.local.StudentDao
import com.example.data.local.ViolationDao
import com.example.data.model.CategoryRecapSummary
import com.example.data.model.ClassRecapSummary
import com.example.data.model.DisciplineLevel
import com.example.data.model.ParentNotificationLog
import com.example.data.model.SchoolDisciplineStats
import com.example.data.model.Student
import com.example.data.model.ViolationCategory
import com.example.data.model.ViolationMaster
import com.example.data.model.ViolationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DisciplineRepository(
    private val studentDao: StudentDao,
    private val violationDao: ViolationDao
) {

    // --- Students ---
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    val allClassNames: Flow<List<String>> = studentDao.getAllClassNames()
    val totalStudentCount: Flow<Int> = studentDao.getTotalStudentCount()
    val atRiskStudents: Flow<List<Student>> = studentDao.getAtRiskStudents()

    fun getStudentById(id: Long): Flow<Student?> = studentDao.getStudentFlowById(id)

    suspend fun insertStudent(student: Student): Long = studentDao.insertStudent(student)

    suspend fun updateStudent(student: Student) = studentDao.updateStudent(student)

    suspend fun deleteStudent(student: Student) {
        studentDao.deleteStudent(student)
    }

    suspend fun clearAllStudentsAndViolations() {
        studentDao.deleteAllStudents()
        violationDao.deleteAllRecords()
        violationDao.deleteAllNotificationLogs()
    }

    // --- Violation Categories & Masters ---
    val allCategories: Flow<List<ViolationCategory>> = violationDao.getAllCategories()
    val allMasterViolations: Flow<List<ViolationMaster>> = violationDao.getAllMasterViolations()

    suspend fun insertMasterViolation(master: ViolationMaster): Long =
        violationDao.insertMasterViolation(master)

    suspend fun updateMasterViolation(master: ViolationMaster) =
        violationDao.updateMasterViolation(master)

    suspend fun deleteMasterViolation(master: ViolationMaster) =
        violationDao.deleteMasterViolation(master)

    // --- Violation Records ---
    val allViolationRecords: Flow<List<ViolationRecord>> = violationDao.getAllViolationRecords()

    fun getViolationsByStudentId(studentId: Long): Flow<List<ViolationRecord>> =
        violationDao.getViolationsByStudentId(studentId)

    suspend fun searchStudentsByNameOrNis(query: String): List<Student> =
        studentDao.searchStudentsByNameOrNis(query)

    suspend fun getCachedViolationsForStudent(studentId: Long): List<ViolationRecord> =
        violationDao.getCachedViolationsForStudent(studentId)

    suspend fun getRecentCachedViolations(): List<ViolationRecord> =
        violationDao.getRecentCachedViolations()

    /**
     * Record a new violation for a student, recalculates total score and updates student status
     */
    suspend fun recordViolation(
        student: Student,
        violationMaster: ViolationMaster,
        customPoints: Int? = null,
        notes: String = "",
        reporterTeacher: String = "Guru Piket / BK",
        timestamp: Long = System.currentTimeMillis(),
        parentNotified: Boolean = false,
        parentNotificationType: String = ""
    ): Long {
        val pointsToApply = customPoints ?: violationMaster.points
        val record = ViolationRecord(
            studentId = student.id,
            studentName = student.name,
            studentNis = student.nis,
            studentClass = student.className,
            violationMasterId = violationMaster.id,
            violationTitle = violationMaster.title,
            categoryName = violationMaster.categoryName,
            points = pointsToApply,
            timestamp = timestamp,
            notes = notes,
            reporterTeacher = reporterTeacher,
            parentNotified = parentNotified,
            parentNotificationType = parentNotificationType,
            parentNotificationDate = if (parentNotified) timestamp else null,
            status = "Tercatat"
        )

        val recordId = violationDao.insertViolationRecord(record)

        // Recalculate total score for student
        val totalPoints = violationDao.calculateStudentTotalPoints(student.id) ?: 0
        studentDao.updateStudentScore(
            studentId = student.id,
            newScore = totalPoints,
            lastDate = timestamp
        )

        return recordId
    }

    /**
     * Delete a violation record and update student's score
     */
    suspend fun deleteViolationRecord(record: ViolationRecord) {
        violationDao.deleteViolationRecord(record)
        val totalPoints = violationDao.calculateStudentTotalPoints(record.studentId) ?: 0
        studentDao.updateStudentScore(
            studentId = record.studentId,
            newScore = totalPoints,
            lastDate = System.currentTimeMillis()
        )
    }

    /**
     * Update status of violation record (e.g. "Dalam Pembinaan", "Selesai")
     */
    suspend fun updateViolationStatus(record: ViolationRecord, newStatus: String) {
        val updated = record.copy(status = newStatus)
        violationDao.updateViolationRecord(updated)
    }

    /**
     * Mark parent notified
     */
    suspend fun markParentNotified(recordId: Long, channel: String) {
        violationDao.markParentNotified(recordId, channel, System.currentTimeMillis())
    }

    // --- Parent Notifications ---
    val allNotificationLogs: Flow<List<ParentNotificationLog>> = violationDao.getAllNotificationLogs()
    val totalNotificationCount: Flow<Int> = violationDao.getTotalNotificationCount()

    suspend fun logParentNotification(log: ParentNotificationLog): Long =
        violationDao.insertNotificationLog(log)

    // --- Automatic Recapitulation Stream ---
    val schoolStats: Flow<SchoolDisciplineStats> = combine(
        allStudents,
        allViolationRecords,
        allCategories,
        totalNotificationCount
    ) { students, records, categories, notifCount ->
        val totalStudents = students.size
        val cleanStudents = students.count { it.totalScore == 0 }
        val troubledStudents = students.count { it.totalScore > 0 }
        val totalPoints = students.sumOf { it.totalScore }

        var sp1 = 0
        var sp2 = 0
        var sp3 = 0

        students.forEach { s ->
            when (s.disciplineLevel) {
                DisciplineLevel.SP_1 -> sp1++
                DisciplineLevel.SP_2 -> sp2++
                DisciplineLevel.SP_3, DisciplineLevel.SANGAT_BERAT -> sp3++
                else -> {}
            }
        }

        // Category breakdown
        val totalRecordsCount = records.size
        val categoryBreakdown = categories.map { cat ->
            val catRecords = records.filter { it.categoryName == cat.name }
            val count = catRecords.size
            val points = catRecords.sumOf { it.points }
            val pct = if (totalRecordsCount > 0) (count.toFloat() / totalRecordsCount.toFloat()) * 100f else 0f
            CategoryRecapSummary(
                categoryName = cat.name,
                violationCount = count,
                totalPoints = points,
                percentage = pct
            )
        }.sortedByDescending { it.violationCount }

        val topRisk = students.filter { it.totalScore > 0 }.take(5)

        SchoolDisciplineStats(
            totalStudents = totalStudents,
            cleanStudentsCount = cleanStudents,
            troubledStudentsCount = troubledStudents,
            totalViolationsRecorded = totalRecordsCount,
            totalPointsAccumulated = totalPoints,
            sp1Count = sp1,
            sp2Count = sp2,
            sp3Count = sp3,
            totalParentNotificationsSent = notifCount,
            topViolations = categoryBreakdown,
            topRiskStudents = topRisk
        )
    }

    /**
     * Automatic Class-Level Recapitulation
     */
    val classRecapSummaries: Flow<List<ClassRecapSummary>> = combine(
        allStudents,
        allViolationRecords
    ) { students, records ->
        val groupedByClass = students.groupBy { it.className }

        groupedByClass.map { (className, classStudents) ->
            val classRecords = records.filter { it.studentClass == className }
            val totalScore = classStudents.sumOf { it.totalScore }
            val safe = classStudents.count { it.disciplineLevel == DisciplineLevel.AMAN }
            val warning = classStudents.count { it.disciplineLevel == DisciplineLevel.PERINGATAN_RINGAN }
            val sp1 = classStudents.count { it.disciplineLevel == DisciplineLevel.SP_1 }
            val sp2 = classStudents.count { it.disciplineLevel == DisciplineLevel.SP_2 }
            val sp3 = classStudents.count {
                it.disciplineLevel == DisciplineLevel.SP_3 || it.disciplineLevel == DisciplineLevel.SANGAT_BERAT
            }

            ClassRecapSummary(
                className = className,
                totalStudents = classStudents.size,
                totalViolations = classRecords.size,
                totalScorePoints = totalScore,
                safeCount = safe,
                warningCount = warning,
                sp1Count = sp1,
                sp2Count = sp2,
                sp3Count = sp3
            )
        }.sortedByDescending { it.totalScorePoints }
    }
}
