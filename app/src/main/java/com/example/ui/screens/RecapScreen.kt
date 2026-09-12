package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.ClassRecapSummary
import com.example.data.model.SchoolDisciplineStats
import com.example.data.model.ViolationRecord
import com.example.ui.components.IndustrialTrendChart
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.NavyPrimary
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
import com.example.util.ExportHelper
import com.example.util.NotificationHelper

@Composable
fun RecapScreen(
    schoolName: String,
    stats: SchoolDisciplineStats,
    classSummaries: List<ClassRecapSummary>,
    allRecords: List<ViolationRecord>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val fullReportText = ExportHelper.generateSchoolRecapReportText(
        schoolName = schoolName,
        stats = stats,
        classSummaries = classSummaries,
        allRecords = allRecords
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp)
    ) {
        // Header & Quick Export Actions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
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
                        Column {
                            Text(
                                text = "Rekapitulasi Data Otomatis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Laporan Kedisiplinan & Bimbingan Konseling",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilledTonalButton(
                            onClick = {
                                NotificationHelper.copyToClipboard(context, "Laporan Rekapitulasi", fullReportText)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("copy_recap_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin Rekap", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                ExportHelper.shareText(context, "Laporan Rekapitulasi Kedisiplinan Siswa", fullReportText)
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("share_recap_btn"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bagikan Laporan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: Industrial Telemetry Trend Visualizer
        item {
            IndustrialTrendChart(
                allRecords = allRecords,
                allStudents = emptyList()
            )
        }

        // Section: Overall School Analytics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorderLight)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ringkasan Sekolah",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Siswa Terdata", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "${stats.totalStudents} Siswa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Siswa Bersih (0 Poin)", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "${stats.cleanStudentsCount} Siswa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SafeGreen)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Siswa Pernah Melanggar", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "${stats.troubledStudentsCount} Siswa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Sp1Orange)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Pelanggaran Masuk", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "${stats.totalViolationsRecorded} Kasus", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Akumulasi Poin Pelanggaran", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "${stats.totalPointsAccumulated} Poin", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Sp2Rose)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Notifikasi Terkirim ke Wali Murid", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "${stats.totalParentNotificationsSent} Notifikasi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Section: Rekapitulasi Otomatis per Rombel / Kelas
        item {
            Text(
                text = "Rekapitulasi Otomatis per Kelas (${classSummaries.size} Kelas)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(classSummaries, key = { it.className }) { cs ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorderLight)
                )
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
                        Column {
                            Text(
                                text = "Kelas ${cs.className}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${cs.totalStudents} Siswa • ${cs.totalViolations} Kasus Pelanggaran",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (cs.totalScorePoints == 0) SafeGreenBg else Sp2RoseBg
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "${cs.totalScorePoints} Poin",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = if (cs.totalScorePoints == 0) SafeGreen else Sp2Rose
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Breakdown Badges for this class
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ClassBreakdownPill(label = "Tertib", count = cs.safeCount, color = SafeGreen, bg = SafeGreenBg, modifier = Modifier.weight(1f))
                        ClassBreakdownPill(label = "SP 1", count = cs.sp1Count, color = Sp1Orange, bg = Sp1OrangeBg, modifier = Modifier.weight(1f))
                        ClassBreakdownPill(label = "SP 2", count = cs.sp2Count, color = Sp2Rose, bg = Sp2RoseBg, modifier = Modifier.weight(1f))
                        ClassBreakdownPill(label = "SP 3", count = cs.sp3Count, color = Sp3Red, bg = Sp3RedBg, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Section: Breakdown Kategori
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(CardBorderLight)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Proporsi Jenis Pelanggaran Terbanyak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    stats.topViolations.forEach { cat ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = cat.categoryName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${cat.violationCount} Kasus (${cat.totalPoints} Poin)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { if (stats.totalViolationsRecorded > 0) cat.violationCount.toFloat() / stats.totalViolationsRecorded.toFloat() else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassBreakdownPill(
    label: String,
    count: Int,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 9.sp, color = color, fontWeight = FontWeight.Medium)
            Text(text = "$count", fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
