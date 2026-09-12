package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.CategoryRecapSummary
import com.example.data.model.ClassRecapSummary
import com.example.data.model.SchoolDisciplineStats
import com.example.data.model.Student
import com.example.data.model.ViolationRecord

object ExportHelper {

    fun generateSchoolRecapReportText(
        schoolName: String = "SMA NEGERI 1 Keritang",
        stats: SchoolDisciplineStats,
        classSummaries: List<ClassRecapSummary>,
        allRecords: List<ViolationRecord>,
        filterLabel: String = "Semua Waktu"
    ): String {
        return buildString {
            appendLine("📑 *LAPORAN REKAPITULASI KEDISIPLINAN SISWA*")
            appendLine("*BAGIAN KESISWAAN & BIMBINGAN KONSELING (BK)*")
            appendLine("*$schoolName*")
            appendLine("Periode: $filterLabel | Dicetak: ${DateUtils.formatDateTime(System.currentTimeMillis())}")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine()
            appendLine("📊 *RINGKASAN UMUM:*")
            appendLine("• Total Siswa Terdata     : ${stats.totalStudents} siswa")
            appendLine("• Siswa Disiplin (0 Poin) : ${stats.cleanStudentsCount} siswa (${if (stats.totalStudents > 0) (stats.cleanStudentsCount * 100 / stats.totalStudents) else 0}%)")
            appendLine("• Siswa Tercatat Melanggar: ${stats.troubledStudentsCount} siswa")
            appendLine("• Total Pelanggaran Masuk : ${stats.totalViolationsRecorded} kasus")
            appendLine("• Total Akumulasi Poin   : ${stats.totalPointsAccumulated} poin")
            appendLine("• Siswa Status SP 1       : ${stats.sp1Count} siswa")
            appendLine("• Siswa Status SP 2       : ${stats.sp2Count} siswa")
            appendLine("• Siswa SP 3 / Kritis     : ${stats.sp3Count} siswa")
            appendLine("• Notifikasi Terkirim Ortu: ${stats.totalParentNotificationsSent} kali")
            appendLine()
            appendLine("🏫 *REKAPITULASI POIN PER KELAS:*")
            classSummaries.forEachIndexed { index, cs ->
                appendLine("${index + 1}. Kelas *${cs.className}*")
                appendLine("   - Total Siswa: ${cs.totalStudents} | Kasus: ${cs.totalViolations} | Akumulasi: *${cs.totalScorePoints} Poin*")
                appendLine("   - Tertib: ${cs.safeCount} | SP1: ${cs.sp1Count} | SP2: ${cs.sp2Count} | SP3: ${cs.sp3Count}")
            }
            appendLine()
            appendLine("🔍 *DISTRIBUSI KATEGORI PELANGGARAN:*")
            stats.topViolations.forEach { cat ->
                appendLine("• ${cat.categoryName}: ${cat.violationCount} kasus (${cat.totalPoints} poin)")
            }
            appendLine()
            if (stats.topRiskStudents.isNotEmpty()) {
                appendLine("⚠️ *DAFTAR SISWA PERLU PERHATIAN KHUSUS (SKOR TERTINGGI):*")
                stats.topRiskStudents.forEachIndexed { i, s ->
                    appendLine("${i + 1}. ${s.name} (${s.className}) - *${s.totalScore} Poin* [${s.disciplineLevel.shortName}]")
                    appendLine("   Wali: ${s.parentName} (${s.parentPhone})")
                }
            }
            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("_Laporan Dihasilkan Otomatis oleh Sistem Penghitungan Skor Pelanggaran Siswa_")
        }
    }

    fun generateStudentHistoryText(
        schoolName: String = "SMA NEGERI 1 Keritang",
        student: Student,
        records: List<ViolationRecord>
    ): String {
        return buildString {
            appendLine("📋 *REKAM JEJAK KEDISIPLINAN SISWA*")
            appendLine("*$schoolName*")
            appendLine("Dicetak: ${DateUtils.formatDateTime(System.currentTimeMillis())}")
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("• Nama Siswa    : *${student.name}*")
            appendLine("• NIS / NISN    : ${student.nis} / ${student.nisn.ifBlank { "-" }}")
            appendLine("• Kelas         : *${student.className}*")
            appendLine("• Jenis Kelamin : ${if (student.gender == "L") "Laki-laki" else "Perempuan"}")
            appendLine("• Orang Tua/Wali: ${student.parentName} (${student.parentPhone})")
            appendLine("• Alamat        : ${student.parentAddress.ifBlank { "-" }}")
            appendLine("• Total Skor    : *${student.totalScore} Poin*")
            appendLine("• Status        : *${student.disciplineLevel.title}*")
            appendLine("• Sanksi/Tindakan: ${student.disciplineLevel.sanctionTitle}")
            appendLine()
            appendLine("📝 *RIWAYAT PELANGGARAN TERCATAT (${records.size} Kasus):*")
            if (records.isEmpty()) {
                appendLine("Tidak ada riwayat pelanggaran. Siswa berstatus tertib dan aman (0 Poin).")
            } else {
                records.forEachIndexed { i, r ->
                    appendLine("${i + 1}. *${r.violationTitle}*")
                    appendLine("   • Poin: +${r.points} Poin | Kategori: ${r.categoryName}")
                    appendLine("   • Tanggal: ${DateUtils.formatDateTime(r.timestamp)}")
                    appendLine("   • Pelapor: ${r.reporterTeacher}")
                    if (r.notes.isNotBlank()) appendLine("   • Catatan: ${r.notes}")
                    appendLine("   • Notifikasi Ortu: ${if (r.parentNotified) "Sudah (${r.parentNotificationType})" else "Belum"}")
                    appendLine("   • Status Tindak Lanjut: ${r.status}")
                }
            }
            appendLine()
            appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            appendLine("_Dokumen Resmi Bimbingan Konseling (BK) & Kesiswaan ${schoolName}_")
        }
    }

    fun shareText(context: Context, title: String, content: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan Rekapitulasi"))
    }
}
