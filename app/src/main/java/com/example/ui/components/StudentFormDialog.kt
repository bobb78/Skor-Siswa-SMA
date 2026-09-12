package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Student
import com.example.ui.theme.TextSecondary

@Composable
fun StudentFormDialog(
    studentToEdit: Student? = null,
    onDismiss: () -> Unit,
    onSave: (
        nis: String,
        nisn: String,
        name: String,
        className: String,
        gender: String,
        parentName: String,
        parentPhone: String,
        parentAddress: String,
        notes: String
    ) -> Unit
) {
    var nis by remember { mutableStateOf(studentToEdit?.nis ?: "") }
    var nisn by remember { mutableStateOf(studentToEdit?.nisn ?: "") }
    var name by remember { mutableStateOf(studentToEdit?.name ?: "") }
    var className by remember { mutableStateOf(studentToEdit?.className ?: "X-MIPA 1") }
    var gender by remember { mutableStateOf(studentToEdit?.gender ?: "L") }
    var parentName by remember { mutableStateOf(studentToEdit?.parentName ?: "") }
    var parentPhone by remember { mutableStateOf(studentToEdit?.parentPhone ?: "") }
    var parentAddress by remember { mutableStateOf(studentToEdit?.parentAddress ?: "") }
    var notes by remember { mutableStateOf(studentToEdit?.notes ?: "") }

    val isEditing = studentToEdit != null
    val isValid = name.isNotBlank() && nis.isNotBlank() && className.isNotBlank() && parentPhone.isNotBlank()

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
                            text = if (isEditing) "Edit Data Siswa" else "Tambah Siswa Baru",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Profil Siswa & Kontak Wali Murid",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Lengkap Siswa *") },
                        placeholder = { Text("Contoh: Muhammad Rizki Pratama") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_student_name"),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = nis,
                            onValueChange = { nis = it },
                            label = { Text("NIS *") },
                            placeholder = { Text("20261001") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_student_nis"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = nisn,
                            onValueChange = { nisn = it },
                            label = { Text("NISN (Opsional)") },
                            placeholder = { Text("0071234567") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = className,
                            onValueChange = { className = it },
                            label = { Text("Kelas / Rombel *") },
                            placeholder = { Text("X-MIPA 1 / XI-IPS 2") },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("input_student_class"),
                            singleLine = true
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Jenis Kelamin", fontSize = 12.sp, color = TextSecondary)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = gender == "L",
                                    onClick = { gender = "L" },
                                    label = { Text("L (Putra)") }
                                )
                                FilterChip(
                                    selected = gender == "P",
                                    onClick = { gender = "P" },
                                    label = { Text("P (Putri)") }
                                )
                            }
                        }
                    }

                    Text(
                        text = "Kontak & Data Orang Tua / Wali Murid:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 6.dp)
                    )

                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Nama Orang Tua / Wali *") },
                        placeholder = { Text("Bpk. Bambang Sutrisno / Ibu Siti") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_parent_name"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("No. WhatsApp / HP Orang Tua *") },
                        placeholder = { Text("081234567890 (Untuk Notifikasi WA)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_parent_phone"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = parentAddress,
                        onValueChange = { parentAddress = it },
                        label = { Text("Alamat Rumah (Opsional)") },
                        placeholder = { Text("Jl. Melati No. 12, Kel. Sukamaju") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan Tambahan / BK (Opsional)") },
                        placeholder = { Text("Catatan bimbingan khusus atau riwayat...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                            if (isValid) {
                                onSave(nis, nisn, name, className, gender, parentName, parentPhone, parentAddress, notes)
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("save_student_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.padding(start = 4.dp))
                        Text(if (isEditing) "Simpan Perubahan" else "Tambah Siswa", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
