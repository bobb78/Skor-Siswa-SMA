package com.example.data.model

/**
 * Summary data class for automated class-level recapitulation
 */
data class ClassRecapSummary(
    val className: String,
    val totalStudents: Int,
    val totalViolations: Int,
    val totalScorePoints: Int,
    val safeCount: Int,
    val warningCount: Int,
    val sp1Count: Int,
    val sp2Count: Int,
    val sp3Count: Int
)

/**
 * Summary data class for automated category-level breakdown
 */
data class CategoryRecapSummary(
    val categoryName: String,
    val violationCount: Int,
    val totalPoints: Int,
    val percentage: Float
)

/**
 * Overall school disciplinary statistics
 */
data class SchoolDisciplineStats(
    val totalStudents: Int,
    val cleanStudentsCount: Int,
    val troubledStudentsCount: Int,
    val totalViolationsRecorded: Int,
    val totalPointsAccumulated: Int,
    val sp1Count: Int,
    val sp2Count: Int,
    val sp3Count: Int,
    val totalParentNotificationsSent: Int,
    val topViolations: List<CategoryRecapSummary> = emptyList(),
    val topRiskStudents: List<Student> = emptyList()
)

/**
 * Filter criteria for filtering violation records and recaps
 */
data class ViolationFilter(
    val searchQuery: String = "",
    val selectedClass: String = "Semua Kelas",
    val selectedCategory: String = "Semua Kategori",
    val selectedRiskLevel: String = "Semua Tingkat",
    val dateRangeType: DateRangeType = DateRangeType.ALL_TIME
)

enum class DateRangeType(val label: String) {
    TODAY("Hari Ini"),
    THIS_WEEK("Minggu Ini"),
    THIS_MONTH("Bulan Ini"),
    THIS_SEMESTER("Semester Ini"),
    ALL_TIME("Semua Waktu")
}
