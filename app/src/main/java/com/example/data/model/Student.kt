package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model for Student (Siswa)
 */
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nis: String,
    val nisn: String = "",
    val name: String,
    val className: String, // e.g. "X-MIPA 1", "XI-IPS 2", "XII-RPL 1"
    val gender: String = "L", // "L" (Laki-laki) or "P" (Perempuan)
    val parentName: String,
    val parentPhone: String, // WhatsApp / Phone number e.g. "08123456789"
    val parentAddress: String = "",
    val totalScore: Int = 0,
    val lastViolationDate: Long? = null,
    val notes: String = ""
) {
    val disciplineLevel: DisciplineLevel
        get() = DisciplineLevel.fromScore(totalScore)
}

/**
 * Disciplinary Level based on accumulated score points
 */
enum class DisciplineLevel(
    val title: String,
    val shortName: String,
    val colorHex: Long,
    val sanctionTitle: String,
    val description: String,
    val requiresParentCall: Boolean
) {
    AMAN(
        title = "Tertib & Aman",
        shortName = "Aman",
        colorHex = 0xFF2E7D32, // Green
        sanctionTitle = "Pembinaan Rutin",
        description = "Siswa berstatus tertib dan belum memiliki akumulasi poin mengkhawatirkan.",
        requiresParentCall = false
    ),
    PERINGATAN_RINGAN(
        title = "Perhatian / Teguran Lisan",
        shortName = "Teguran",
        colorHex = 0xFFF57F17, // Amber
        sanctionTitle = "Teguran Lisan & Pembinaan Wali Kelas",
        description = "Akumulasi skor mencapai batas teguran. Perlu pembinaan dari Wali Kelas.",
        requiresParentCall = false
    ),
    SP_1(
        title = "Surat Peringatan 1 (SP 1)",
        shortName = "SP 1",
        colorHex = 0xFFE65100, // Deep Orange
        sanctionTitle = "Surat Peringatan 1 & Notifikasi Wali Murid",
        description = "Poin mencapai ambang batas SP 1. Wajib pemberitahuan tertulis / WA ke orang tua.",
        requiresParentCall = true
    ),
    SP_2(
        title = "Surat Peringatan 2 (SP 2)",
        shortName = "SP 2",
        colorHex = 0xFFC2185B, // Rose Red
        sanctionTitle = "Panggilan Orang Tua Ke-1 & Bimbingan Konseling (BK)",
        description = "Poin mencapai ambang SP 2. Orang tua wajib diundang ke sekolah untuk konseling.",
        requiresParentCall = true
    ),
    SP_3(
        title = "Panggilan Darurat / SP 3",
        shortName = "SP 3",
        colorHex = 0xFFB71C1C, // Deep Red
        sanctionTitle = "Panggilan Orang Tua + Perjanjian Terakhir + Skorsing",
        description = "Poin kritis mendekati batas maksimal. Skorsing 3-7 hari dan surat perjanjian bermaterai.",
        requiresParentCall = true
    ),
    SANGAT_BERAT(
        title = "Tindakan Khusus / Konferensi Kasus",
        shortName = "DO / Konferensi",
        colorHex = 0xFF4A148C, // Deep Purple
        sanctionTitle = "Konferensi Kasus / Pengembalian ke Orang Tua",
        description = "Pelanggaran kumulatif ≥ 100 poin. Rapat dewan guru untuk sanksi pengembalian siswa ke orang tua.",
        requiresParentCall = true
    );

    companion object {
        fun fromScore(score: Int): DisciplineLevel {
            return when {
                score >= 100 -> SANGAT_BERAT
                score >= 75 -> SP_3
                score >= 50 -> SP_2
                score >= 25 -> SP_1
                score >= 10 -> PERINGATAN_RINGAN
                else -> AMAN
            }
        }
    }
}
