package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DisciplineLevel
import com.example.data.model.Student
import com.example.data.model.ViolationRecord
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.Sp1Orange
import com.example.ui.theme.Sp1OrangeBg
import com.example.ui.theme.Sp2Rose
import com.example.ui.theme.Sp2RoseBg
import com.example.ui.theme.Sp3Red
import com.example.ui.theme.Sp3RedBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg
import com.example.util.DateUtils
import com.example.util.ExportHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailSheet(
    student: Student,
    records: List<ViolationRecord>,
    schoolName: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onRecordViolation: () -> Unit,
    onNotifyParent: () -> Unit,
    onEditStudent: () -> Unit,
    onDeleteRecord: (ViolationRecord) -> Unit,
    canModifyPoints: Boolean = true
) {
    val context = LocalContext.current
    val studentRecords = records.filter { it.studentId == student.id }.sortedByDescending { it.timestamp }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        modifier = Modifier.fillMaxHeight(0.9f)
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
                        text = "Profil & Rekam Jejak Disiplin",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bimbingan Konseling & Kesiswaan",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Student Summary Hero Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when (student.disciplineLevel) {
                                DisciplineLevel.AMAN -> SafeGreenBg
                                DisciplineLevel.PERINGATAN_RINGAN -> WarningAmberBg
                                DisciplineLevel.SP_1 -> Sp1OrangeBg
                                DisciplineLevel.SP_2 -> Sp2RoseBg
                                DisciplineLevel.SP_3, DisciplineLevel.SANGAT_BERAT -> Sp3RedBg
                            }
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "${student.className} • NIS: ${student.nis} ${if (student.nisn.isNotBlank()) "• NISN: ${student.nisn}" else ""}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                ScoreBadge(score = student.totalScore, level = student.disciplineLevel)
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "⚖️ Tindak Lanjut: ${student.disciplineLevel.sanctionTitle}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = student.disciplineLevel.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Parent Contact Info
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Informasi Wali Murid:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Nama: ${student.parentName}", fontSize = 13.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "No. WhatsApp: ${student.parentPhone}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (student.parentAddress.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextSecondary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Alamat: ${student.parentAddress}", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Timeline & History of Violations
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Riwayat Pelanggaran (${studentRecords.size} Kasus)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        IconButton(
                            onClick = {
                                val text = ExportHelper.generateStudentHistoryText(schoolName, student, studentRecords)
                                ExportHelper.shareText(context, "Rekam Jejak Disiplin - ${student.name}", text)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Bagikan Rekap", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                if (studentRecords.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SafeGreenBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = "Rekor Bersih (0 Poin)", fontWeight = FontWeight.Bold, color = SafeGreen)
                                    Text(text = "Siswa ini belum pernah tercatat melakukan pelanggaran tata tertib.", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                } else {
                    items(studentRecords, key = { it.id }) { rec ->
                        ViolationCard(
                            record = rec,
                            onNotifyParent = onNotifyParent,
                            onDeleteRecord = { onDeleteRecord(rec) },
                            canModifyPoints = canModifyPoints
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Bar (Only teachers can edit/add violations/send WA notifications)
            if (canModifyPoints) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEditStudent,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Data")
                    }

                    FilledTonalButton(
                        onClick = onRecordViolation,
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.AddAlert, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Pelanggaran")
                    }

                    Button(
                        onClick = onNotifyParent,
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("detail_notify_parent_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Kirim WA", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                FilledTonalButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Tutup Lembar Profil", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
