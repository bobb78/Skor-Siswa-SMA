package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Student
import com.example.data.model.UserRole
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.Sp1Orange
import com.example.ui.theme.Sp1OrangeBg
import com.example.ui.theme.Sp2Rose
import com.example.ui.theme.Sp2RoseBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    schoolName: String,
    counselorName: String,
    students: List<Student>,
    onLoginTeacher: (name: String, role: UserRole, pin: String) -> Boolean,
    onLoginStudent: (studentName: String, nis: String, studentId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRoleTab by remember { mutableIntStateOf(0) } // 0: Guru/BK, 1: Siswa

    // Teacher Form State
    var selectedTeacherRole by remember { mutableStateOf(UserRole.GURU_BK) }
    var teacherName by remember(counselorName) { mutableStateOf(counselorName) }
    var teacherPin by remember { mutableStateOf("") }
    var showPin by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf<String?>(null) }
    var failedAttempts by remember { mutableIntStateOf(0) }

    // Student Form State
    var studentNisInput by remember { mutableStateOf("") }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                // Background cover
                Image(
                    painter = painterResource(id = R.drawable.img_cover_banner_1788147441694),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    NavyDark.copy(alpha = 0.82f),
                                    NavyPrimary.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sistem Skor & Kedisiplinan",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = schoolName,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.18f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Portal Akses Guru BK & Siswa",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Role Tabs (Guru vs Siswa)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(CardBorderLight)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    TabRow(
                        selectedTabIndex = selectedRoleTab,
                        containerColor = Color.Transparent,
                        contentColor = NavyPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedRoleTab]),
                                color = NavyPrimary,
                                height = 3.dp
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedRoleTab == 0,
                            onClick = {
                                selectedRoleTab = 0
                                pinError = null
                            },
                            modifier = Modifier.testTag("tab_login_teacher"),
                            text = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Guru / Guru BK",
                                        fontWeight = if (selectedRoleTab == 0) FontWeight.Black else FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Akses Input Poin",
                                        fontSize = 10.sp,
                                        color = if (selectedRoleTab == 0) NavyPrimary else TextMuted
                                    )
                                }
                            }
                        )

                        Tab(
                            selected = selectedRoleTab == 1,
                            onClick = {
                                selectedRoleTab = 1
                                pinError = null
                            },
                            modifier = Modifier.testTag("tab_login_student"),
                            text = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Siswa / Wali Murid",
                                        fontWeight = if (selectedRoleTab == 1) FontWeight.Black else FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Mode Pantau Skor",
                                        fontSize = 10.sp,
                                        color = if (selectedRoleTab == 1) Sp1Orange else TextMuted
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Tab 0: GURU / GURU BK
                if (selectedRoleTab == 0) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(CardBorderLight)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(NavyPrimary.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Login Guru & Guru BK",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Memiliki izin menambah & mengelola poin siswa",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Role Selection Radio/Chips
                            Text(
                                text = "Peran / Tugas:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RoleChip(
                                    title = "Guru BK / Kesiswaan",
                                    subtitle = "Full Access & Profil",
                                    selected = selectedTeacherRole == UserRole.GURU_BK,
                                    onClick = { selectedTeacherRole = UserRole.GURU_BK },
                                    modifier = Modifier.weight(1f)
                                )
                                RoleChip(
                                    title = "Guru Piket / Pengajar",
                                    subtitle = "Catat Pelanggaran",
                                    selected = selectedTeacherRole == UserRole.GURU,
                                    onClick = { selectedTeacherRole = UserRole.GURU },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Name Input
                            OutlinedTextField(
                                value = teacherName,
                                onValueChange = { teacherName = it },
                                label = { Text("Nama Guru / Petugas") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_teacher_name"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NavyPrimary,
                                    focusedLabelColor = NavyPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // PIN Input
                            OutlinedTextField(
                                value = teacherPin,
                                onValueChange = {
                                    teacherPin = it
                                    pinError = null
                                },
                                label = { Text("PIN Keamanan Guru") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = NavyPrimary)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showPin = !showPin }) {
                                        Icon(
                                            imageVector = if (showPin) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle PIN",
                                            tint = TextMuted
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.NumberPassword,
                                    imeAction = ImeAction.Done
                                ),
                                isError = pinError != null,
                                supportingText = {
                                    if (pinError != null) {
                                        Text(text = pinError!!, color = Sp2Rose, fontWeight = FontWeight.Medium)
                                    } else {
                                        Text(text = "PIN default: 1234 (Master: bk123). Login Guru/BK tidak bergantung pada data siswa.", color = TextSecondary, fontSize = 11.sp)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_teacher_pin"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NavyPrimary,
                                    focusedLabelColor = NavyPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Login Button
                            Button(
                                onClick = {
                                    if (teacherPin.isBlank()) {
                                        pinError = "Silakan masukkan PIN keamanan Guru/BK"
                                    } else {
                                        val success = onLoginTeacher(teacherName, selectedTeacherRole, teacherPin)
                                        if (!success) {
                                            failedAttempts++
                                            pinError = if (failedAttempts >= 3) {
                                                "PIN salah ($failedAttempts kali). Akses Guru/BK dilindungi pihak berwenang."
                                            } else {
                                                "PIN keamanan tidak valid. Akses ditolak."
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_submit_teacher_login"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavyPrimary,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Masuk sebagai Guru / BK",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Security note (No bypass button!)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Otorisasi Guru/BK menggunakan PIN keamanan (bukan tabel data siswa). Tetap dapat login meskipun database siswa kosong.",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // Tab 1: SISWA / WALI MURID
                if (selectedRoleTab == 1) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(CardBorderLight)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Sp1OrangeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Sp1Orange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Akses Siswa & Wali Murid",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Transparansi Poin & Tata Tertib Sekolah",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Security Notice Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Sp1OrangeBg)
                                    .border(1.dp, Sp1Orange.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Sp1Orange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Hak Akses Terbatas (Read-Only)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Sp1Orange
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Sebagai Siswa, Anda hanya dapat memantau perolehan poin dan riwayat kasus. Anda TIDAK DAPAT menambahkan atau mengurangi poin pelanggaran.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                            lineHeight = 15.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // NIS Input (Optional for specific student view)
                            OutlinedTextField(
                                value = studentNisInput,
                                onValueChange = { input ->
                                    studentNisInput = input
                                    selectedStudent = students.find { it.nis.equals(input.trim(), ignoreCase = true) }
                                },
                                label = { Text("NIS Siswa (Opsional)") },
                                placeholder = { Text("Contoh: 20241001") },
                                leadingIcon = {
                                    Icon(Icons.Default.School, contentDescription = null, tint = PrimaryBlueLight)
                                },
                                singleLine = true,
                                supportingText = {
                                    if (selectedStudent != null) {
                                        Text(
                                            text = "Siswa ditemukan: ${selectedStudent?.name} (${selectedStudent?.className})",
                                            color = PrimaryBlueLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    } else {
                                        Text(
                                            text = "Kosongkan untuk melihat seluruh data kedisiplinan umum",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_student_nis")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Login as Student Button
                            Button(
                                onClick = {
                                    val name = selectedStudent?.name ?: if (studentNisInput.isNotBlank()) "Siswa ($studentNisInput)" else "Siswa / Tamu"
                                    onLoginStudent(name, studentNisInput, selectedStudent?.id)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btn_submit_student_login"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Sp1Orange,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Masuk sebagai Siswa (Mode Pantau)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick demo student button
                            FilledTonalButton(
                                onClick = {
                                    val firstStudent = students.firstOrNull()
                                    if (firstStudent != null) {
                                        onLoginStudent(firstStudent.name, firstStudent.nis, firstStudent.id)
                                    } else {
                                        onLoginStudent("Siswa Umum", "20241001", null)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("btn_quick_student_login"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "⚡ Masuk Cepat: Siswa (Hanya Lihat Skor)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleChip(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) NavyPrimary else CardBorderLight
    val bgColor = if (selected) NavyPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) NavyPrimary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = if (selected) NavyPrimary.copy(alpha = 0.8f) else TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
