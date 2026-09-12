package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.DisciplineLevel
import com.example.data.model.Student
import com.example.data.model.ViolationRecord
import java.net.URLEncoder

object NotificationHelper {

    /**
     * Standard notification types available to school staff
     */
    enum class LetterType(val label: String, val templateTitle: String) {
        PELANGGARAN_BARU("Pemberitahuan Pelanggaran Baru", "SURAT PEMBERITAHUAN PELANGGARAN SISWA"),
        SP_1("Surat Peringatan 1 (SP 1)", "SURAT PERINGATAN 1 (SP-1)"),
        SP_2("Surat Peringatan 2 & Undangan BK", "SURAT PERINGATAN 2 (SP-2) & UNDANGAN BK"),
        SP_3("Surat Peringatan 3 & Panggilan Mendesak", "SURAT PERINGATAN 3 (SP-3) & PANGGILAN MENDESAK WALI MURID"),
        REKAP_DISIPLIN("Laporan Rekapitulasi Disiplin Siswa", "LAPORAN REKAPITULASI KEDISIPLINAN SISWA")
    }

    /**
     * Formats official WhatsApp / SMS notification text
     */
    fun generateParentMessage(
        schoolName: String = "SMA NEGERI 1 Keritang",
        student: Student,
        violationRecord: ViolationRecord? = null,
        letterType: LetterType = LetterType.PELANGGARAN_BARU,
        customNotes: String = ""
    ): String {
        val totalPoints = student.totalScore
        val level = student.disciplineLevel
        val dateStr = DateUtils.formatDate(System.currentTimeMillis())

        return buildString {
            appendLine("📢 *${letterType.templateTitle}*")
            appendLine("*BAGIAN KESISWAAN & BIMBINGAN KONSELING (BK)*")
            appendLine("*$schoolName*")
            appendLine("━━━━━━━━━━━━━━━━━━━━━")
            appendLine("Tanggal: $dateStr")
            appendLine()
            appendLine("Yth. *${student.parentName}*,")
            appendLine("Wali Murid dari:")
            appendLine("• Nama Siswa : *${student.name}*")
            appendLine("• NIS / Kelas : *${student.nis} / ${student.className}*")
            appendLine()

            when (letterType) {
                LetterType.PELANGGARAN_BARU -> {
                    appendLine("Dengan ini kami memberitahukan bahwa ananda telah tercatat melakukan pelanggaran tata tertib sekolah:")
                    if (violationRecord != null) {
                        appendLine("• Jenis Pelanggaran : *${violationRecord.violationTitle}*")
                        appendLine("• Kategori : ${violationRecord.categoryName}")
                        appendLine("• Bobot Poin : *+${violationRecord.points} Poin*")
                        appendLine("• Waktu Kejadian : ${DateUtils.formatDateTime(violationRecord.timestamp)}")
                        if (violationRecord.notes.isNotBlank()) {
                            appendLine("• Catatan Khusus : ${violationRecord.notes}")
                        }
                    }
                    appendLine()
                    appendLine("📊 *Status Akumulasi Poin Siswa Saat Ini:*")
                    appendLine("• Total Skor Pelanggaran : *${totalPoints} Poin*")
                    appendLine("• Kategori Status : *${level.title}*")
                    appendLine("• Rekomendasi Sanksi : ${level.sanctionTitle}")
                }

                LetterType.SP_1 -> {
                    appendLine("Berdasarkan evaluasi tata tertib, akumulasi poin pelanggaran ananda telah mencapai ambang batas *SURAT PERINGATAN 1 (SP-1)*.")
                    appendLine()
                    appendLine("📊 *Rincian Akumulasi:*")
                    appendLine("• Total Skor Pelanggaran : *${totalPoints} Poin*")
                    appendLine("• Status : *SP-1 (Surat Peringatan 1)*")
                    appendLine()
                    appendLine("Mohon kerja sama Bapak/Ibu untuk memberikan perhatian dan pembinaan di rumah agar ananda senantiasa mematuhi aturan dan tata tertib sekolah.")
                }

                LetterType.SP_2 -> {
                    appendLine("Diberitahukan bahwa akumulasi poin pelanggaran ananda telah mencapai ambang *SURAT PERINGATAN 2 (SP-2)*.")
                    appendLine()
                    appendLine("📊 *Rincian Akumulasi:*")
                    appendLine("• Total Skor Pelanggaran : *${totalPoints} Poin*")
                    appendLine("• Status : *SP-2 (Peringatan Keras)*")
                    appendLine()
                    appendLine("🗓️ *UNDANGAN MENGHADAP GURU BK / WALI KELAS:*")
                    appendLine("Kami mengharap kehadiran Bapak/Ibu di Ruang BK Sekolah pada:")
                    appendLine("• Hari/Tgl : Senin - Jumat (Jam Kerja Sekolah)")
                    appendLine("• Waktu    : Pukul 08.00 - 13.00 WIB")
                    appendLine("• Keperluan: Konseling & Pembinaan Siswa Bersama")
                }

                LetterType.SP_3 -> {
                    appendLine("⚠️ *PERHATIAN MENDESAK (SURAT PERINGATAN 3 / TERAKHIR)*")
                    appendLine("Akumulasi poin pelanggaran ananda telah mencapai *${totalPoints} Poin* (Ambang Batas Kritis).")
                    appendLine()
                    appendLine("Bapak/Ibu Wali Murid *DIHARAPKAN SEGERA HADIR KE SEKOLAH* besok pagi untuk penandatanganan surat perjanjian khusus bermaterai serta pembahasan status masa depan belajar ananda.")
                }

                LetterType.REKAP_DISIPLIN -> {
                    appendLine("Berikut kami sampaikan rekapitulasi poin kedisiplinan ananda hingga saat ini:")
                    appendLine("• Total Skor Pelanggaran : *${totalPoints} Poin*")
                    appendLine("• Tingkat Disiplin : *${level.title}*")
                    appendLine("• Keterangan : ${level.description}")
                }
            }

            if (customNotes.isNotBlank()) {
                appendLine()
                appendLine("📝 *Pesan Tambahan dari Guru:*")
                appendLine(customNotes)
            }

            appendLine()
            appendLine("Demikian pemberitahuan ini kami sampaikan. Atas perhatian dan kerja sama Bapak/Ibu, kami ucapkan terima kasih.")
            appendLine("━━━━━━━━━━━━━━━━━━━━━")
            appendLine("_Pesan Otomatis Sistem Bimbingan Konseling & Kesiswaan ${schoolName}_")
        }
    }

    /**
     * Normalizes phone number to international WhatsApp format (e.g. "0812..." -> "62812...")
     */
    fun normalizePhoneNumber(phone: String): String {
        var clean = phone.replace(Regex("[^0-9]"), "")
        if (clean.startsWith("0")) {
            clean = "62" + clean.substring(1)
        } else if (clean.startsWith("8")) {
            clean = "62$clean"
        }
        return clean
    }

    /**
     * Opens WhatsApp with pre-filled message directly to parent's phone number
     */
    fun sendWhatsApp(context: Context, rawPhone: String, messageText: String): Boolean {
        val cleanPhone = normalizePhoneNumber(rawPhone)
        return try {
            val encodedMessage = URLEncoder.encode(messageText, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            try {
                // Fallback direct whatsapp uri
                val uri = Uri.parse("whatsapp://send?phone=$cleanPhone&text=${URLEncoder.encode(messageText, "UTF-8")}")
                val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                true
            } catch (ex: Exception) {
                Toast.makeText(context, "Aplikasi WhatsApp tidak ditemukan. Teks telah disalin ke clipboard.", Toast.LENGTH_LONG).show()
                copyToClipboard(context, "Pesan Notifikasi", messageText)
                false
            }
        }
    }

    /**
     * Opens SMS app with pre-filled recipient and message body
     */
    fun sendSms(context: Context, rawPhone: String, messageText: String): Boolean {
        val cleanPhone = rawPhone.replace(Regex("[^0-9+]"), "")
        return try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$cleanPhone")
                putExtra("sms_body", messageText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka aplikasi SMS. Teks telah disalin.", Toast.LENGTH_SHORT).show()
            copyToClipboard(context, "Pesan Notifikasi SMS", messageText)
            false
        }
    }

    /**
     * Copies message text to system clipboard
     */
    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Teks notifikasi berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
    }
}
