package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Student
import com.example.data.model.ViolationRecord
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.util.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentNotificationDialog(
    student: Student,
    violationRecord: ViolationRecord? = null,
    schoolName: String = "SMA NEGERI 1 Keritang",
    onDismiss: () -> Unit,
    onSendNotification: (channel: String, letterType: String, message: String) -> Unit
) {
    val context = LocalContext.current

    // Determine initial letter type based on student's current disciplinary level
    val defaultLetterType = when {
        violationRecord != null -> NotificationHelper.LetterType.PELANGGARAN_BARU
        student.totalScore >= 75 -> NotificationHelper.LetterType.SP_3
        student.totalScore >= 50 -> NotificationHelper.LetterType.SP_2
        student.totalScore >= 25 -> NotificationHelper.LetterType.SP_1
        else -> NotificationHelper.LetterType.REKAP_DISIPLIN
    }

    var selectedLetterType by remember { mutableStateOf(defaultLetterType) }
    var isTypeDropdownExpanded by remember { mutableStateOf(false) }
    var customTeacherNotes by remember { mutableStateOf("") }

    var editableMessage by remember(selectedLetterType, customTeacherNotes) {
        mutableStateOf(
            NotificationHelper.generateParentMessage(
                schoolName = schoolName,
                student = student,
                violationRecord = violationRecord,
                letterType = selectedLetterType,
                customNotes = customTeacherNotes
            )
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pengiriman Notifikasi Orang Tua",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Format Resmi BK / Kesiswaan via WhatsApp & SMS",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Scrollable Body
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Recipient Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Penerima: ${student.parentName} (Wali Murid)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Siswa: ${student.name} (${student.className}) • Skor: ${student.totalScore} Poin",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "No. WhatsApp/HP: ${student.parentPhone}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            ScoreBadge(score = student.totalScore, level = student.disciplineLevel, compact = true)
                        }
                    }

                    // Pilih Format / Jenis Surat Peringatan
                    Text(
                        text = "Pilih Template Format Pesan:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ExposedDropdownMenuBox(
                        expanded = isTypeDropdownExpanded,
                        onExpandedChange = { isTypeDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedLetterType.label,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTypeDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_letter_type_field")
                        )

                        ExposedDropdownMenu(
                            expanded = isTypeDropdownExpanded,
                            onDismissRequest = { isTypeDropdownExpanded = false }
                        ) {
                            NotificationHelper.LetterType.values().forEach { type ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = type.label,
                                            fontWeight = if (selectedLetterType == type) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedLetterType = type
                                        isTypeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Custom Note Addon
                    OutlinedTextField(
                        value = customTeacherNotes,
                        onValueChange = { customTeacherNotes = it },
                        label = { Text("Pesan Tambahan dari Guru / Petugas (Opsional)") },
                        placeholder = { Text("Contoh: Mohon membawa buku penghubung siswa...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Message Preview & Direct Edit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pratinjau Pesan yang Akan Dikirim:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        IconButton(
                            onClick = {
                                NotificationHelper.copyToClipboard(context, "Surat Pemberitahuan", editableMessage)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin Teks",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editableMessage,
                        onValueChange = { editableMessage = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .testTag("message_preview_field"),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Send Action Buttons (WhatsApp, SMS, Cancel)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal")
                    }

                    FilledTonalButton(
                        onClick = {
                            NotificationHelper.sendSms(context, student.parentPhone, editableMessage)
                            onSendNotification("SMS", selectedLetterType.label, editableMessage)
                        },
                        modifier = Modifier.weight(1.1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SMS", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            NotificationHelper.sendWhatsApp(context, student.parentPhone, editableMessage)
                            onSendNotification("WhatsApp", selectedLetterType.label, editableMessage)
                        },
                        modifier = Modifier
                            .weight(1.6f)
                            .testTag("send_whatsapp_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366) // Official WhatsApp Green
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
