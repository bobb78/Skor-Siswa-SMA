package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.data.model.AutomatedAlertLog
import com.example.data.model.Student

object ViolationThresholdService {
    private const val TAG = "ThresholdService"
    private const val CHANNEL_ID = "DISCIPLINE_TELEMETRY_ALERTS"
    private const val CHANNEL_NAME = "Tactical Telemetry Discipline Alerts"

    // Predefined Industrial Discipline Thresholds
    val THRESHOLDS = listOf(25, 50, 100, 250)

    fun checkAndTriggerThreshold(
        context: Context,
        student: Student,
        oldScore: Int,
        newScore: Int,
        schoolName: String
    ): List<AutomatedAlertLog> {
        val triggeredLogs = mutableListOf<AutomatedAlertLog>()

        for (thresh in THRESHOLDS) {
            if (oldScore < thresh && newScore >= thresh) {
                // Threshold has been crossed!
                val severityLabel = when (thresh) {
                    25 -> "PERINGATAN RINGAN"
                    50 -> "SP-1 (SURAT PERINGATAN 1)"
                    100 -> "SP-2 (SURAT PERINGATAN 2 & PANGGILAN ORTU)"
                    250 -> "SP-3 / KRITIS (SIDANG KESISWAAN)"
                    else -> "THRESHOLD BREACH ($thresh PTS)"
                }

                // 1. Simulated SMS Dispatch
                val smsLog = AutomatedAlertLog(
                    studentName = student.name,
                    studentNis = student.nis,
                    studentClass = student.className,
                    thresholdBreached = thresh,
                    currentPoints = newScore,
                    channel = "SIMULATED_SMS",
                    recipient = student.parentPhone.ifBlank { "0812-XXXX-XXXX" },
                    status = "SMS_DISPATCHED // CARRIER_OK"
                )
                triggeredLogs.add(smsLog)

                // 2. Simulated Email / Telemetry Dispatch
                val emailLog = AutomatedAlertLog(
                    studentName = student.name,
                    studentNis = student.nis,
                    studentClass = student.className,
                    thresholdBreached = thresh,
                    currentPoints = newScore,
                    channel = "SIMULATED_EMAIL",
                    recipient = "${student.nis.lowercase()}@wali.skorsiswa.sch.id",
                    status = "EMAIL_QUEUED // SMTP_250_OK"
                )
                triggeredLogs.add(emailLog)

                // 3. Post System Android Notification
                showSystemAlertNotification(
                    context = context,
                    studentName = student.name,
                    className = student.className,
                    score = newScore,
                    severityLabel = severityLabel
                )

                Log.i(TAG, "AUTOMATED DISPATCH TRIGGERED for ${student.name}: Crossed $thresh pts (Current: $newScore)")
            }
        }

        return triggeredLogs
    }

    private fun showSystemAlertNotification(
        context: Context,
        studentName: String,
        className: String,
        score: Int,
        severityLabel: String
    ) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Automated tactical telemetry alerts when students cross discipline thresholds"
                }
                notificationManager.createNotificationChannel(channel)
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle(">>> [TELEMETRY ALERT] $studentName ($className)")
                .setContentText("Skor mencapai $score Poin // Status: $severityLabel")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Pemberitahuan Otomatis Sistem Disiplin:\nSiswa $studentName ($className) telah melewati ambang batas disiplin dengan total skor $score Poin.\nStatus Otomatis: $severityLabel.\nNotifikasi SMS/Email otomatis telah dialirkan ke wali murid.")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (e: Exception) {
            Log.w(TAG, "Notification post skipped: ${e.message}")
        }
    }
}
