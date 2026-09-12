package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Category of School Violations
 */
@Entity(tableName = "violation_categories")
data class ViolationCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // e.g. "KRP" (Kerapian), "DIS" (Kedisiplinan), "TTB" (Ketertiban), "PRL" (Perilaku), "BRT" (Pelanggaran Berat)
    val name: String,
    val iconName: String = "category",
    val colorHex: Long = 0xFF1976D2
)

/**
 * Master catalog of violation types with their designated point scores
 */
@Entity(tableName = "violation_masters")
data class ViolationMaster(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val categoryName: String,
    val code: String,
    val title: String,
    val description: String = "",
    val points: Int,
    val defaultSanction: String = "Teguran & Pembinaan",
    val isCustom: Boolean = false
)

/**
 * Historical record of a violation committed by a student
 */
@Entity(tableName = "violation_records")
data class ViolationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val studentNis: String,
    val studentClass: String,
    val violationMasterId: Long,
    val violationTitle: String,
    val categoryName: String,
    val points: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val reporterTeacher: String = "Guru Piket / BK",
    val parentNotified: Boolean = false,
    val parentNotificationType: String = "", // "WhatsApp", "SMS", "Surat Fisik"
    val parentNotificationDate: Long? = null,
    val status: String = "Tercatat" // "Tercatat", "Dalam Pembinaan", "Selesai"
)

/**
 * Log of parent notifications sent via WhatsApp/SMS
 */
@Entity(tableName = "parent_notification_logs")
data class ParentNotificationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val studentClass: String,
    val parentName: String,
    val parentPhone: String,
    val channel: String, // "WhatsApp" or "SMS"
    val letterType: String, // "Pemberitahuan Pelanggaran", "SP 1", "SP 2 / Undangan BK", "SP 3 / Panggilan Mendesak", "Rekap Nilai Disiplin"
    val messageContent: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true
)
