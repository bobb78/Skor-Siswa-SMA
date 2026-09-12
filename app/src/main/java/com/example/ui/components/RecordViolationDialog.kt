package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DisciplineLevel
import com.example.data.model.Student
import com.example.data.model.ViolationCategory
import com.example.data.model.ViolationMaster
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenBg
import com.example.ui.theme.Sp1Orange
import com.example.ui.theme.Sp1OrangeBg
import com.example.ui.theme.Sp2Rose
import com.example.ui.theme.Sp2RoseBg
import com.example.ui.theme.Sp3Red
import com.example.ui.theme.Sp3RedBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberBg

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RecordViolationDialog(
    initialStudent: Student? = null,
    studentsList: List<Student>,
    categories: List<ViolationCategory>,
    masterViolations: List<ViolationMaster>,
    onDismiss: () -> Unit,
    onConfirm: (
        student: Student,
        violation: ViolationMaster,
        customPoints: Int?,
        notes: String,
        reporter: String,
        notifyParent: Boolean
    ) -> Unit
) {
    var selectedStudent by remember { mutableStateOf(initialStudent ?: studentsList.firstOrNull()) }
    var studentSearchQuery by remember { mutableStateOf("") }
    var isStudentDropdownExpanded by remember { mutableStateOf(false) }

    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedMasterViolation by remember { mutableStateOf<ViolationMaster?>(null) }
    var violationSearchQuery by remember { mutableStateOf("") }

    var notes by remember { mutableStateOf("") }
    var reporterTeacher by remember { mutableStateOf("Guru Piket / BK") }
    var notifyParentDirectly by remember { mutableStateOf(true) }

    val filteredStudents = remember(studentsList, studentSearchQuery) {
        if (studentSearchQuery.isBlank()) studentsList
        else studentsList.filter {
            it.name.contains(studentSearchQuery, ignoreCase = true) ||
                it.className.contains(studentSearchQuery, ignoreCase = true) ||
                it.nis.contains(studentSearchQuery, ignoreCase = true)
        }
    }

    val filteredMasters = remember(masterViolations, selectedCategoryId, violationSearchQuery) {
        masterViolations.filter { m ->
            val matchesCategory = selectedCategoryId == null || m.categoryId == selectedCategoryId
            val matchesSearch = violationSearchQuery.isBlank() ||
                m.title.contains(violationSearchQuery, ignoreCase = true) ||
                m.categoryName.contains(violationSearchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    // Projected Score Calculation
    val currentStudentScore = selectedStudent?.totalScore ?: 0
    val newPoints = selectedMasterViolation?.points ?: 0
    val projectedScore = currentStudentScore + newPoints
    val projectedLevel = DisciplineLevel.fromScore(projectedScore)

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
                            text = "Catat Pelanggaran Siswa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Kalkulator Skor Otomatis & Notifikasi",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Pilih Siswa
                    Text(
                        text = "1. Pilih Siswa Target",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    ExposedDropdownMenuBox(
                        expanded = isStudentDropdownExpanded,
                        onExpandedChange = { isStudentDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedStudent?.let { "${it.name} (${it.className}) - NIS: ${it.nis}" } ?: "Pilih Siswa",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Nama Siswa") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStudentDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_student_field")
                        )

                        ExposedDropdownMenu(
                            expanded = isStudentDropdownExpanded,
                            onDismissRequest = { isStudentDropdownExpanded = false }
                        ) {
                            OutlinedTextField(
                                value = studentSearchQuery,
                                onValueChange = { studentSearchQuery = it },
                                placeholder = { Text("Cari nama/kelas/NIS...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                            filteredStudents.forEach { student ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(text = student.name, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "${student.className} • NIS: ${student.nis} • Skor: ${student.totalScore} Poin",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                        }
                                    },
                                    onClick = {
                                        selectedStudent = student
                                        isStudentDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // 2. Pilih Kategori & Jenis Pelanggaran
                    Text(
                        text = "2. Pilih Jenis Pelanggaran",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Category Filter Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null },
                            label = { Text("Semua Kategori", fontSize = 12.sp) }
                        )
                        categories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategoryId == cat.id,
                                onClick = { selectedCategoryId = cat.id },
                                label = { Text(cat.name, fontSize = 12.sp) }
                            )
                        }
                    }

                    // Search Master Violations
                    OutlinedTextField(
                        value = violationSearchQuery,
                        onValueChange = { violationSearchQuery = it },
                        placeholder = { Text("Cari jenis pelanggaran (e.g. bolos, merokok, seragam)...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Violation Catalog List Cards (Pickable)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        filteredMasters.forEach { master ->
                            val isSelected = selectedMasterViolation?.id == master.id
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedMasterViolation = master }
                                    .testTag("violation_item_${master.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PrimaryBlueLight)) else null
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = master.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${master.categoryName} • Sanksi: ${master.defaultSanction}",
                                            fontSize = 11.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Sp2RoseBg)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "+${master.points} Poin",
                                            color = Sp2Rose,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. LIVE SCORE CALCULATION & RECAP PREVIEW
                    if (selectedMasterViolation != null && selectedStudent != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = when (projectedLevel) {
                                    DisciplineLevel.AMAN -> SafeGreenBg
                                    DisciplineLevel.PERINGATAN_RINGAN -> WarningAmberBg
                                    DisciplineLevel.SP_1 -> Sp1OrangeBg
                                    DisciplineLevel.SP_2 -> Sp2RoseBg
                                    DisciplineLevel.SP_3, DisciplineLevel.SANGAT_BERAT -> Sp3RedBg
                                }
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⚡ HASIL KALKULASI SKOR OTOMATIS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    ScoreBadge(score = projectedScore, level = projectedLevel, compact = true)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Skor Lama", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "$currentStudentScore Poin", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Text(text = "+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Poin Pelanggaran", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "$newPoints Poin", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Sp2Rose)
                                    }
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Total Skor Baru", fontSize = 11.sp, color = TextSecondary)
                                        Text(text = "$projectedScore Poin", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "📌 Rekomendasi Tindak Lanjut: ${projectedLevel.sanctionTitle}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // 4. Catatan & Pelapor
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan / Kronologi Singkat") },
                        placeholder = { Text("Contoh: Ditemukan di belakang kantin saat jam ke-4...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = reporterTeacher,
                        onValueChange = { reporterTeacher = it },
                        label = { Text("Guru / Petugas Pelapor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // 5. WhatsApp Direct Notification Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Kirim Notifikasi ke Orang Tua",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Kirim surat format resmi langsung ke WhatsApp Wali Murid (${selectedStudent?.parentPhone ?: "-"})",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = notifyParentDirectly,
                            onCheckedChange = { notifyParentDirectly = it },
                            modifier = Modifier.testTag("notify_parent_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal")
                    }

                    Button(
                        onClick = {
                            val student = selectedStudent
                            val violation = selectedMasterViolation
                            if (student != null && violation != null) {
                                onConfirm(
                                    student,
                                    violation,
                                    null,
                                    notes,
                                    reporterTeacher,
                                    notifyParentDirectly
                                )
                            }
                        },
                        enabled = selectedStudent != null && selectedMasterViolation != null,
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("confirm_record_violation_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (notifyParentDirectly) "Simpan & Kirim WA" else "Simpan Rekor",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
