package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.components.StudentCard
import com.example.ui.theme.BlinkingUnderscoreCursor
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun StudentsScreen(
    students: List<Student>,
    classNames: List<String>,
    searchQuery: String,
    selectedClass: String,
    selectedRisk: String,
    onSearchQueryChange: (String) -> Unit,
    onClassFilterChange: (String) -> Unit,
    onRiskFilterChange: (String) -> Unit,
    onSelectStudent: (Student) -> Unit,
    onRecordViolation: (Student) -> Unit,
    onNotifyParent: (Student) -> Unit,
    onEditStudent: (Student) -> Unit,
    onAddStudentClick: () -> Unit,
    canModifyPoints: Boolean = true,
    canManageStudents: Boolean = true,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            if (canManageStudents) {
                FloatingActionButton(
                    onClick = onAddStudentClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("fab_add_student")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = "Tambah Siswa")
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = "Tambah Siswa", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Cari nama siswa, NIS, kelas...")
                        BlinkingUnderscoreCursor(
                            color = PrimaryBlueLight,
                            fontSize = 14.sp
                        )
                    }
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Hapus")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("students_search_field"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Class Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedClass == "Semua Kelas",
                        onClick = { onClassFilterChange("Semua Kelas") },
                        label = { Text("Semua Kelas", fontSize = 12.sp) }
                    )
                }
                items(classNames) { cName ->
                    FilterChip(
                        selected = selectedClass == cName,
                        onClick = { onClassFilterChange(cName) },
                        label = { Text(cName, fontSize = 12.sp) }
                    )
                }
            }

            // Risk Level Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val riskOptions = listOf(
                    "Semua Status",
                    "Tertib & Aman (0-24 Poin)",
                    "Peringatan Ringan (25-49 Poin)",
                    "SP 1 (50-99 Poin)",
                    "SP 2 (100-249 Poin)",
                    "SP 3 / Kritis (≥ 250 Poin)"
                )
                items(riskOptions) { risk ->
                    FilterChip(
                        selected = selectedRisk == risk,
                        onClick = { onRiskFilterChange(risk) },
                        label = { Text(risk, fontSize = 11.sp) }
                    )
                }
            }

            // Results count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ditemukan ${students.size} Siswa",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Students List
            if (students.isEmpty()) {
                val isFilterActive = searchQuery.isNotBlank() || selectedClass != "Semua Kelas" || selectedRisk != "Semua Status"
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isFilterActive) Icons.Default.SentimentDissatisfied else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isFilterActive) "Tidak ada data siswa yang cocok dengan filter" else "Belum Ada Siswa Terdaftar",
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isFilterActive) {
                                "Coba ubah kata kunci pencarian atau filter kelas."
                            } else {
                                "Aplikasi siap digunakan dalam kondisi baru. Klik tombol '+ Tambah Siswa' untuk mendaftarkan siswa."
                            },
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(students, key = { it.id }) { student ->
                        StudentCard(
                            student = student,
                            onCardClick = { onSelectStudent(student) },
                            onRecordViolation = { onRecordViolation(student) },
                            onNotifyParent = { onNotifyParent(student) },
                            onEditStudent = { onEditStudent(student) },
                            canModifyPoints = canModifyPoints
                        )
                    }
                }
            }
        }
    }
}
