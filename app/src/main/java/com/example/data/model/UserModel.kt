package com.example.data.model

enum class UserRole(val label: String, val badgeColorHex: Long) {
    TEACHER_BK("TEACHER_BK // KESISWAAN", 0xFFE61919),
    TEACHER_PIKET("TEACHER_PIKET // STAFF", 0xFF0D9488),
    STUDENT("STUDENT // WALI_MURID", 0xFF4AF626);

    companion object {
        // Backwards-compatible aliases
        val GURU_BK = TEACHER_BK
        val GURU = TEACHER_PIKET
        val SISWA = STUDENT
    }
}

data class AppUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val role: UserRole,
    val identifier: String = ""
)

data class UserSession(
    val isLoggedIn: Boolean = false,
    val role: UserRole = UserRole.TEACHER_BK,
    val name: String = "",
    val identifier: String = "", // NIP / NIS
    val studentId: Long? = null,
    val firebaseUid: String? = null,
    val email: String? = null,
    val isSecondaryAuthVerified: Boolean = false
) {
    val canModifyPoints: Boolean
        get() = role == UserRole.TEACHER_BK || role == UserRole.TEACHER_PIKET

    val canManageStudents: Boolean
        get() = role == UserRole.TEACHER_BK || role == UserRole.TEACHER_PIKET

    val canDeleteRecords: Boolean
        get() = role == UserRole.TEACHER_BK || role == UserRole.TEACHER_PIKET

    val canSendNotifications: Boolean
        get() = role == UserRole.TEACHER_BK || role == UserRole.TEACHER_PIKET

    val canEditSchoolProfile: Boolean
        get() = role == UserRole.TEACHER_BK
}

data class AutomatedAlertLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val studentName: String,
    val studentNis: String,
    val studentClass: String,
    val thresholdBreached: Int,
    val currentPoints: Int,
    val channel: String, // "SIMULATED_SMS" / "SIMULATED_EMAIL" / "FIREBASE_FCM"
    val recipient: String,
    val status: String = "TRANSMITTED_OK"
)
